package prompted.registration2

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.qameta.allure.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import prompted.registration2.client.RegistrationApiClient
import prompted.registration2.data.RegistrationTestData

@Epic("User Management")
@Feature("Registration API")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationApiTests {

    private val apiClient = RegistrationApiClient()

    @AfterAll
    fun tearDown() = apiClient.close()

    // ==================== HAPPY PATH ====================

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("[Registration] Успешная регистрация с валидными данными")
    fun `successful registration with valid data`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.validRequest())

        response.status shouldBe 201
        response.body shouldNotBe null
        response.body!!.userId shouldMatch Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")
        response.body!!.status shouldBe "PENDING"
        response.body!!.message shouldNotBe null
    }

    // ==================== EMAIL VALIDATION ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Email] Ошибка 400 при пустом email")
    fun `error 400 when email is empty`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidEmail.empty())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Email] Ошибка 400 при отсутствии поля email")
    fun `error 400 when email field is missing`() = runBlocking {
        val response = apiClient.registerRaw(RegistrationTestData.RawJson.missingEmail())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Email] Ошибка 400 при невалидном формате email (без @)")
    fun `error 400 when email has no at symbol`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidEmail.withoutAt())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Email] Ошибка 400 при невалидном формате email (без домена)")
    fun `error 400 when email has no domain`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidEmail.withoutDomain())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Email] Ошибка 409 при дублировании email")
    fun `error 409 when email already registered`() = runBlocking {
        // Setup: register user first
        val existingEmail = RegistrationTestData.DuplicateEmail.existingEmail()
        val firstRequest = RegistrationTestData.validRequest().copy(email = existingEmail)
        val setupResponse = apiClient.register(firstRequest)
        setupResponse.status shouldBe 201

        // Test: try to register with same email
        val duplicateRequest = RegistrationTestData.validRequest().copy(email = existingEmail)
        val response = apiClient.register(duplicateRequest)

        response.status shouldBe 409
    }

    // ==================== PHONE VALIDATION ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Phone] Ошибка 400 при пустом phone")
    fun `error 400 when phone is empty`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidPhone.empty())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Phone] Ошибка 400 при отсутствии поля phone")
    fun `error 400 when phone field is missing`() = runBlocking {
        val response = apiClient.registerRaw(RegistrationTestData.RawJson.missingPhone())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Phone] Ошибка 400 при невалидном формате phone (без +)")
    fun `error 400 when phone has no plus prefix`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidPhone.withoutPlus())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Phone] Ошибка 400 при невалидном формате phone (буквы)")
    fun `error 400 when phone contains letters`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidPhone.withLetters())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Phone] Ошибка 409 при дублировании phone")
    fun `error 409 when phone already registered`() = runBlocking {
        // Setup: register user first
        val existingPhone = RegistrationTestData.DuplicatePhone.existingPhone()
        val firstRequest = RegistrationTestData.validRequest().copy(phone = existingPhone)
        val setupResponse = apiClient.register(firstRequest)
        setupResponse.status shouldBe 201

        // Test: try to register with same phone
        val duplicateRequest = RegistrationTestData.validRequest().copy(phone = existingPhone)
        val response = apiClient.register(duplicateRequest)

        response.status shouldBe 409
    }

    // ==================== PASSWORD VALIDATION ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Успех при пароле ровно 8 символов (BVA Min)")
    fun `success when password is exactly 8 characters`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.PasswordBoundary.exactlyMin())

        response.status shouldBe 201
        response.body shouldNotBe null
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле 7 символов (BVA Min-1)")
    fun `error 400 when password is 7 characters`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidPassword.tooShort())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пустом пароле")
    fun `error 400 when password is empty`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidPassword.empty())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле без цифр")
    fun `error 400 when password has no digits`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidPassword.noDigits())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле без спецсимволов")
    fun `error 400 when password has no special characters`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidPassword.noSpecialChars())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле без заглавных букв")
    fun `error 400 when password has no uppercase letters`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidPassword.noUppercase())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле содержащем часть email")
    fun `error 400 when password contains part of email`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.PasswordPII.containsEmailPart())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле содержащем часть full_name")
    fun `error 400 when password contains part of full_name`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.PasswordPII.containsFullNamePart())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Password] Успех при пароле с 3-символьной частью имени (граница)")
    fun `success when password contains exactly 3 char part of name`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.PasswordPII.containsThreeCharPart())

        response.status shouldBe 201
    }

    // ==================== FULL_NAME VALIDATION ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Успех при full_name ровно 2 символа (BVA Min)")
    fun `success when full_name is exactly 2 characters`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.FullNameBoundary.exactlyMin())

        response.status shouldBe 201
        response.body shouldNotBe null
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Ошибка 400 при full_name 1 символ (BVA Min-1)")
    fun `error 400 when full_name is 1 character`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidFullName.tooShort())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Успех при full_name ровно 100 символов (BVA Max)")
    fun `success when full_name is exactly 100 characters`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.FullNameBoundary.exactlyMax())

        response.status shouldBe 201
        response.body shouldNotBe null
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Ошибка 400 при full_name 101 символ (BVA Max+1)")
    fun `error 400 when full_name is 101 characters`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidFullName.tooLong())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Ошибка 400 при пустом full_name")
    fun `error 400 when full_name is empty`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidFullName.empty())

        response.status shouldBe 400
    }

    // ==================== RATE LIMITING ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[RateLimit] Ошибка 429 при превышении лимита 5 запросов/мин")
    fun `error 429 when rate limit exceeded`() = runBlocking {
        // Send 5 requests to exhaust rate limit
        repeat(5) {
            apiClient.register(RegistrationTestData.validRequest())
        }

        // 6th request should be rate-limited
        val response = apiClient.register(RegistrationTestData.validRequest())

        response.status shouldBe 429
    }

    // ==================== SECURITY ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Security] Обработка XSS payload в full_name")
    fun `xss payload in full_name is rejected`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.Security.xssInFullName())

        // XSS should be rejected with 400
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Security] Обработка SQL Injection в email")
    fun `sql injection in email is rejected`() = runBlocking {
        val response = apiClient.register(RegistrationTestData.InvalidEmail.sqlInjection())

        response.status shouldBe 400
    }

    // ==================== STRUCTURAL TESTS (Missing Fields) ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при отсутствии поля password")
    fun `error 400 when password field is missing`() = runBlocking {
        val response = apiClient.registerRaw(RegistrationTestData.RawJson.missingPassword())

        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Ошибка 400 при отсутствии поля full_name")
    fun `error 400 when full_name field is missing`() = runBlocking {
        val response = apiClient.registerRaw(RegistrationTestData.RawJson.missingFullName())

        response.status shouldBe 400
    }
}
