package prompted.registration2.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Request DTO for user registration
 */
data class RegistrationRequest(
    val email: String,
    val phone: String,
    val password: String,
    @JsonProperty("full_name") val fullName: String
)

/**
 * Success response DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class RegistrationResponse(
    @JsonProperty("user_id") val userId: String,
    val status: String,
    val message: String? = null
)

/**
 * Error response DTO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val details: Map<String, String>? = null
)
