package registration_v2.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import registration_v2.models.*

// Обертка ответа (ОБЯЗАТЕЛЬНО)
data class ApiResponse<T>(
    val status: Int,
    val body: T?,
    val error: ErrorResponse?,
    val rawBody: String  // Для отладки
)

class RegistrationV2ApiClient(
    private val baseUrl: String = System.getenv("API_BASE_URL") ?: "http://localhost:8080",
    engine: HttpClientEngine? = null  // Для инъекции MockEngine
) {
    private val client = HttpClient(engine ?: CIO.create()) {
        install(ContentNegotiation) {
            jackson {
                propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
            }
        }
    }

    // Типизированный запрос
    suspend fun register(request: RegistrationV2Request): ApiResponse<RegistrationV2Response> {
        val response = client.post("$baseUrl/api/v1/users/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return ApiResponse(
            status = response.status.value,
            body = if (response.status.isSuccess()) response.body() else null,
            error = if (!response.status.isSuccess()) tryParseError(response) else null,
            rawBody = response.bodyAsText()
        )
    }

    // Raw запрос для structural tests (missing fields)
    suspend fun registerRaw(json: String): ApiResponse<RegistrationV2Response> {
        val response = client.post("$baseUrl/api/v1/users/register") {
            contentType(ContentType.Application.Json)
            setBody(json)
        }
        return ApiResponse(
            status = response.status.value,
            body = if (response.status.isSuccess()) response.body() else null,
            error = if (!response.status.isSuccess()) tryParseError(response) else null,
            rawBody = response.bodyAsText()
        )
    }

    private suspend fun tryParseError(response: HttpResponse): ErrorResponse? {
        return try {
            response.body<ErrorResponse>()
        } catch (_: Exception) {
            null
        }
    }

    fun close() = client.close()
}
