package prompted.registration1.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

data class RegisterRequest(
    val email: String,
    val phone: String,
    val password: String,
    @JsonProperty("full_name") val fullName: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RegisterResponse(
    @JsonProperty("user_id") val userId: String,
    val status: String,
    val message: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val details: Map<String, String>? = null
)
