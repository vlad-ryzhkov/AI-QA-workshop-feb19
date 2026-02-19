package registration.requests

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(SnakeCaseStrategy::class)
data class RegisterRequest(
    val email: Any?,
    val phone: Any?,
    val password: Any?,
    val fullName: Any?,
    val userId: Any? = null
)

@JsonNaming(SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class RegisterResponse(
    val verificationToken: String,
    val expiresAt: String
)

@JsonNaming(SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorResponse(
    val code: String,
    val message: String,
    val field: String? = null
)
