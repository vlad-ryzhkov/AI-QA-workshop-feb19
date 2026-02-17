package registration_v2

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.qameta.allure.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import registration_v2.client.RegistrationV2ApiClient
import registration_v2.data.RegistrationV2TestData

@Epic("User Management")
@Feature("Registration API v2")
class RegistrationV2ApiTests {

    private lateinit var apiClient: RegistrationV2ApiClient

    @BeforeEach
    fun setUp() {
        apiClient = RegistrationV2ApiClient()
    }

    @AfterEach
    fun tearDown() {
        apiClient.close()
    }

    // ==================== HAPPY PATH ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Registration] Успешная регистрация с валидными данными → 201, статус PENDING, OTP отправлен")
    fun `successful registration with valid data`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.validRequest())

        response.status shouldBe 201
        response.body shouldNotBe null
        response.body?.userId shouldMatch Regex("[0-9a-f-]{36}")
        response.body?.status shouldBe "PENDING"
        response.body?.message shouldNotBe null
    }

    // ==================== EMAIL VALIDATION ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Email] Ошибка 400 при пустом email")
    fun `error 400 when email is empty`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidEmail.empty())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Email] Ошибка 400 при отсутствии поля email в запросе")
    fun `error 400 when email field is missing`(): Unit = runBlocking {
        val response = apiClient.registerRaw(RegistrationV2TestData.RawJson.missingEmail())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Email] Ошибка 400 при невалидном формате email (без символа @)")
    fun `error 400 when email has no at symbol`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidEmail.withoutAt())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Email] Ошибка 400 при невалидном формате email (без домена)")
    fun `error 400 when email has no domain`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidEmail.withoutDomain())
        response.status shouldBe 400
    }

    // Note: 409 для дублирования email требует предусловия (existing user)
    // Здесь показан только структурный тест для baseline

    // ==================== PHONE VALIDATION ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Phone] Ошибка 400 при пустом phone")
    fun `error 400 when phone is empty`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidPhone.empty())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Phone] Ошибка 400 при отсутствии поля phone в запросе")
    fun `error 400 when phone field is missing`(): Unit = runBlocking {
        val response = apiClient.registerRaw(RegistrationV2TestData.RawJson.missingPhone())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Phone] Ошибка 400 при невалидном формате phone (без префикса +)")
    fun `error 400 when phone has no plus prefix`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidPhone.withoutPlusPrefix())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Phone] Ошибка 400 при невалидном формате phone (содержит буквы)")
    fun `error 400 when phone contains letters`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidPhone.withLetters())
        response.status shouldBe 400
    }

    // Note: 409 для дублирования phone требует предусловия (existing user)

    // ==================== PASSWORD VALIDATION (BVA) ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Успех при пароле ровно 8 символов (BVA Min)")
    fun `success when password is exactly 8 characters`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.ValidPassword.exactlyEightChars())

        response.status shouldBe 201
        response.body shouldNotBe null
        response.body?.userId shouldMatch Regex("[0-9a-f-]{36}")
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле 7 символов (BVA Min-1)")
    fun `error 400 when password is 7 characters`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidPassword.sevenChars())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пустом пароле")
    fun `error 400 when password is empty`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidPassword.empty())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле без цифр")
    fun `error 400 when password has no digits`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidPassword.noDigits())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле без спецсимволов")
    fun `error 400 when password has no special characters`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidPassword.noSpecialChars())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле без заглавных букв")
    fun `error 400 when password has no uppercase letters`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidPassword.noUppercase())
        response.status shouldBe 400
    }

    // ==================== PASSWORD: PII IN PASSWORD ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле содержащем часть email (>3 символов)")
    fun `error 400 when password contains part of email`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.PasswordWithPII.withEmailPart())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле содержащем часть full_name (>3 символов)")
    fun `error 400 when password contains part of full_name`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.PasswordWithPII.withFullNamePart())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Password] Ошибка 400 при пароле содержащем часть имени в другом регистре (case-insensitive)")
    fun `error 400 when password contains name part case insensitive`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.PasswordWithPII.withNamePartCaseInsensitive())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Password] Успех при пароле с 3-символьной частью имени (граница: ≤3 допускается)")
    fun `success when password contains exactly 3 char part of name`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.PasswordWithPII.withThreeCharsFromName())
        response.status shouldBe 201
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("[Password] Ошибка 400 при пароле с 4-символьной частью имени (граница: >3 запрещено)")
    fun `error 400 when password contains 4 char part of name`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.PasswordWithPII.withFourCharsFromName())
        response.status shouldBe 400
    }

    // ==================== FULL_NAME VALIDATION (BVA) ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Успех при full_name ровно 2 символа (BVA Min)")
    fun `success when full_name is exactly 2 characters`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.BoundaryFullName.twoChars())

        response.status shouldBe 201
        response.body shouldNotBe null
        response.body?.userId shouldMatch Regex("[0-9a-f-]{36}")
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Ошибка 400 при full_name 1 символ (BVA Min-1)")
    fun `error 400 when full_name is 1 character`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.BoundaryFullName.oneChar())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Успех при full_name ровно 100 символов (BVA Max)")
    fun `success when full_name is exactly 100 characters`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.BoundaryFullName.hundredChars())

        response.status shouldBe 201
        response.body shouldNotBe null
        response.body?.userId shouldMatch Regex("[0-9a-f-]{36}")
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Ошибка 400 при full_name 101 символ (BVA Max+1)")
    fun `error 400 when full_name is 101 characters`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.BoundaryFullName.hundredOneChars())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Ошибка 400 при пустом full_name")
    fun `error 400 when full_name is empty`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.BoundaryFullName.empty())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[FullName] Ошибка 400 при отсутствии поля full_name в запросе")
    fun `error 400 when full_name field is missing`(): Unit = runBlocking {
        val response = apiClient.registerRaw(RegistrationV2TestData.RawJson.missingFullName())
        response.status shouldBe 400
    }

    // ==================== SECURITY ====================

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Security] Ошибка 400 при XSS payload в full_name")
    fun `error 400 when xss payload in full_name`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.SecurityPayloads.xssInFullName())
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Security] Ошибка 400 при SQL Injection в email")
    fun `error 400 when sql injection in email`(): Unit = runBlocking {
        val response = apiClient.register(RegistrationV2TestData.InvalidEmail.sqlInjection())
        response.status shouldBe 400
    }

    // ==================== NOTES ====================

    // Следующие тесты из мануальных требуют специальных предусловий:
    // - 409 Email already registered (требуется existing user)
    // - 409 Phone already registered (требуется existing user)
    // - 409 Registration with credentials of PENDING user (требуется PENDING user)
    // - 409 Registration with phone of PENDING user (требуется PENDING user)
    // - 429 Rate limit exceeded (требуется 5+ запросов)
    // - 429 Success after rate limit window expires (требуется timing control)
    // - 500 Server error (требуется mock/stub сервера)
    //
    // Эти тесты требуют:
    // 1. Setup инфраструктуры для создания test fixtures (existing users)
    // 2. Rate limiting simulation
    // 3. Mock server для 500 ошибок
    //
    // Baseline покрытие: 25/44 тестов (57%)
    // Автоматизированы все структурные и валидационные тесты без сложных предусловий
}
