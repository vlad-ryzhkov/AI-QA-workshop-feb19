package prompted.registration2.client

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import prompted.registration2.models.ErrorResponse
import prompted.registration2.models.RegistrationRequest
import prompted.registration2.models.RegistrationResponse

/**
 * Unified API response wrapper
 */
data class ApiResponse<T>(
    val status: Int,
    val body: T?,
    val error: ErrorResponse?,
    val rawBody: String
)

/**
 * Registration API client wrapper
 * Encapsulates HTTP communication - tests never use HttpClient directly
 */
class RegistrationApiClient(
    private val baseUrl: String = System.getenv("API_BASE_URL") ?: "http://localhost:8080",
    engine: HttpClientEngine? = null
) {
    private val objectMapper = ObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private val client = HttpClient(engine ?: CIO.create()) {
        install(ContentNegotiation) {
            jackson {
                propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    /**
     * Register user with typed request
     */
    suspend fun register(request: RegistrationRequest): ApiResponse<RegistrationResponse> {
        val response = client.post("$baseUrl/api/v1/users/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return parseResponse(response)
    }

    /**
     * Register user with raw JSON (for structural tests - missing fields)
     */
    suspend fun registerRaw(json: String): ApiResponse<RegistrationResponse> {
        val response = client.post("$baseUrl/api/v1/users/register") {
            contentType(ContentType.Application.Json)
            setBody(json)
        }
        return parseResponse(response)
    }

    private suspend fun parseResponse(response: HttpResponse): ApiResponse<RegistrationResponse> {
        val rawBody = response.bodyAsText()
        val status = response.status.value

        return if (response.status.isSuccess()) {
            ApiResponse(
                status = status,
                body = tryParse(rawBody, RegistrationResponse::class.java),
                error = null,
                rawBody = rawBody
            )
        } else {
            ApiResponse(
                status = status,
                body = null,
                error = tryParse(rawBody, ErrorResponse::class.java),
                rawBody = rawBody
            )
        }
    }

    private fun <T> tryParse(json: String, clazz: Class<T>): T? {
        return try {
            objectMapper.readValue(json, clazz)
        } catch (e: Exception) {
            null
        }
    }

    fun close() = client.close()
}
