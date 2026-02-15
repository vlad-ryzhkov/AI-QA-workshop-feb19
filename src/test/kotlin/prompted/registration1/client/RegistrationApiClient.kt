package prompted.registration1.client

import prompted.registration1.models.ErrorResponse
import prompted.registration1.models.RegisterRequest
import prompted.registration1.models.RegisterResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import kotlinx.coroutines.delay

class RegistrationApiClient(
    private val baseUrl: String = System.getenv("API_BASE_URL") ?: "http://localhost:8080",
    engine: HttpClientEngine? = null
) {
    private val client = HttpClient(engine ?: CIO.create()) {
        install(ContentNegotiation) {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
        }
    }

    suspend fun register(request: RegisterRequest): ApiResponse<RegisterResponse> {
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

    suspend fun registerRaw(body: String): ApiResponse<RegisterResponse> {
        val response = client.post("$baseUrl/api/v1/users/register") {
            contentType(ContentType.Application.Json)
            setBody(body)
        }
        return ApiResponse(
            status = response.status.value,
            body = if (response.status.isSuccess()) response.body() else null,
            error = if (!response.status.isSuccess()) tryParseError(response) else null,
            rawBody = response.bodyAsText()
        )
    }

    suspend fun deleteUser(userId: String): Boolean {
        return runCatching {
            val response = client.delete("$baseUrl/api/v1/users/$userId")
            response.status.isSuccess()
        }.getOrDefault(false)
    }

    suspend fun deleteUserByEmail(email: String): Boolean {
        return runCatching {
            val response = client.delete("$baseUrl/api/v1/users") {
                parameter("email", email)
            }
            response.status.isSuccess()
        }.getOrDefault(false)
    }

    suspend fun <T> executeUntil(
        request: suspend () -> T,
        condition: (T) -> Boolean,
        timeoutMs: Long = 5000,
        intervalMs: Long = 500
    ): T {
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            val result = request()
            if (condition(result)) return result
            delay(intervalMs)
        }
        return request()
    }

    private suspend fun tryParseError(response: HttpResponse): ErrorResponse? {
        return runCatching { response.body<ErrorResponse>() }.getOrNull()
    }

    fun close() {
        client.close()
    }
}

data class ApiResponse<T>(
    val status: Int,
    val body: T?,
    val error: ErrorResponse?,
    val rawBody: String
)
