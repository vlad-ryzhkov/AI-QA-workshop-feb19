package mock

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

object MockRegistrationServer {
    private val registeredEmails = ConcurrentHashMap.newKeySet<String>()
    private val registeredPhones = ConcurrentHashMap.newKeySet<String>()
    private val requestCount = AtomicInteger(0)

    fun reset() {
        registeredEmails.clear()
        registeredPhones.clear()
        requestCount.set(0)
    }

    val engine = MockEngine { request ->
        when {
            request.url.encodedPath == "/api/v1/users/register" && request.method == HttpMethod.Post -> {
                handleRegister(this, request.body.toByteArray().decodeToString())
            }
            request.url.encodedPath.startsWith("/api/v1/users") && request.method == HttpMethod.Delete -> {
                respond("", HttpStatusCode.NoContent)
            }
            else -> respond("Not Found", HttpStatusCode.NotFound)
        }
    }

    private fun handleRegister(scope: MockRequestHandleScope, body: String) = with(scope) {
        if (requestCount.incrementAndGet() > 5) {
            return@with json("""{"error": "Too Many Requests"}""", HttpStatusCode.TooManyRequests)
        }

        val email = body.extractField("email")
        val phone = body.extractField("phone")
        val password = body.extractField("password")
        val fullName = body.extractField("full_name")

        when {
            email == null -> json("""{"error": "email is required"}""", HttpStatusCode.BadRequest)
            phone == null -> json("""{"error": "phone is required"}""", HttpStatusCode.BadRequest)
            password == null -> json("""{"error": "password is required"}""", HttpStatusCode.BadRequest)
            fullName == null -> json("""{"error": "full_name is required"}""", HttpStatusCode.BadRequest)

            !email.contains("@") || email.contains("@@") || email.isBlank() ->
                json("""{"error": "invalid email"}""", HttpStatusCode.BadRequest)
            email.contains("'") || email.contains(";") ->
                json("""{"error": "invalid email"}""", HttpStatusCode.BadRequest)

            !phone.startsWith("+") || phone.any { it.isLetter() } || phone.length < 10 || phone.length > 15 ->
                json("""{"error": "invalid phone"}""", HttpStatusCode.BadRequest)

            password.length < 8 -> json("""{"error": "password too short"}""", HttpStatusCode.BadRequest)
            !password.any { it.isDigit() } -> json("""{"error": "password needs digit"}""", HttpStatusCode.BadRequest)
            !password.any { it.isUpperCase() } -> json("""{"error": "password needs uppercase"}""", HttpStatusCode.BadRequest)
            !password.any { it.isLowerCase() } -> json("""{"error": "password needs lowercase"}""", HttpStatusCode.BadRequest)
            !password.any { !it.isLetterOrDigit() } -> json("""{"error": "password needs special"}""", HttpStatusCode.BadRequest)
            containsPii(fullName, password) -> json("""{"error": "password contains PII"}""", HttpStatusCode.BadRequest)

            fullName.length < 2 -> json("""{"error": "name too short"}""", HttpStatusCode.BadRequest)
            fullName.length > 100 -> json("""{"error": "name too long"}""", HttpStatusCode.BadRequest)

            registeredEmails.contains(email.lowercase()) -> json("""{"error": "email exists"}""", HttpStatusCode.Conflict)
            registeredPhones.contains(phone) -> json("""{"error": "phone exists"}""", HttpStatusCode.Conflict)

            else -> {
                registeredEmails.add(email.lowercase())
                registeredPhones.add(phone)
                val userId = UUID.randomUUID().toString()
                json("""{"user_id": "$userId", "status": "PENDING", "message": "OTP sent"}""", HttpStatusCode.Created)
            }
        }
    }

    private fun containsPii(fullName: String, password: String): Boolean {
        val pwdLower = password.lowercase()
        val nameParts = fullName.lowercase().split(" ").filter { it.length > 3 }
        return nameParts.any { pwdLower.contains(it) }
    }

    private fun String.extractField(name: String): String? {
        val regex = """"$name"\s*:\s*"([^"]*)"""".toRegex()
        return regex.find(this)?.groupValues?.get(1)
    }

    private fun MockRequestHandleScope.json(body: String, status: HttpStatusCode) = respond(
        body, status, headersOf(HttpHeaders.ContentType, "application/json")
    )
}
