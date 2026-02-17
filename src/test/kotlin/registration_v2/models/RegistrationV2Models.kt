package registration_v2.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

// Request: контролируем контракт (SNAKE_CASE через @JsonNaming на классе)
@JsonNaming(SnakeCaseStrategy::class)
data class RegistrationV2Request(
    val email: String,
    val phone: String,
    val password: String,
    val fullName: String
)

// Response: устойчивость к изменениям backend
@JsonNaming(SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class RegistrationV2Response(
    val userId: String,
    val status: String,
    val message: String? = null
)

// Error: унифицированная структура ошибок
@JsonNaming(SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val details: Map<String, String>? = null
)
