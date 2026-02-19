package registration.requests

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson

object Endpoints {
    const val REGISTER = "/api/v1/users/register"
}

object Config {
    val baseUrl: String = System.getenv("BASE_URL") ?: "http://localhost:8080"
}

object RegistrationClient {
    val httpClient: HttpClient by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson {
                    propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
                    registerKotlinModule()
                }
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            expectSuccess = false
        }
    }

    suspend fun register(body: RegisterRequest): HttpResponse =
        httpClient.post(Config.baseUrl + Endpoints.REGISTER) {
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    suspend fun registerRaw(rawBody: String): HttpResponse =
        httpClient.post(Config.baseUrl + Endpoints.REGISTER) {
            contentType(ContentType.Application.Json)
            setBody(rawBody)
        }
}
