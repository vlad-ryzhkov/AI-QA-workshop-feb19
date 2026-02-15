package prompted.registration1

import prompted.registration1.client.RegistrationApiClient
import prompted.registration1.data.RegistrationTestData
import prompted.registration1.mock.MockRegistrationServer
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationApiTests {

    private val apiClient = RegistrationApiClient(engine = MockRegistrationServer.engine)

    @BeforeEach
    fun setUp() {
        MockRegistrationServer.reset()
    }

    @AfterAll
    fun tearDown() {
        apiClient.close()
    }

    @Test
    @DisplayName("Успешная регистрация")
    fun successfulRegistration() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.validRequest())
            response.status shouldBe 201
            response.body shouldNotBe null
            response.body!!.userId shouldMatch Regex("[0-9a-f-]{36}")
            response.body!!.status shouldBe "PENDING"
        }
    }

    @Test
    @DisplayName("400 - нет email")
    fun error400WhenEmailMissing() {
        runBlocking {
            val response = apiClient.registerRaw("""{"phone": "+79991234567", "password": "Strong#Pass99", "full_name": "Test"}""")
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - нет phone")
    fun error400WhenPhoneMissing() {
        runBlocking {
            val response = apiClient.registerRaw("""{"email": "t@t.com", "password": "Strong#Pass99", "full_name": "Test"}""")
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - нет password")
    fun error400WhenPasswordMissing() {
        runBlocking {
            val response = apiClient.registerRaw("""{"email": "t@t.com", "phone": "+79991234567", "full_name": "Test"}""")
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - нет full_name")
    fun error400WhenFullNameMissing() {
        runBlocking {
            val response = apiClient.registerRaw("""{"email": "t@t.com", "phone": "+79991234567", "password": "Strong#Pass99"}""")
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - невалидный email")
    fun error400WhenEmailInvalid() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidEmail.withoutAtSymbol())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - невалидный телефон")
    fun error400WhenPhoneInvalid() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidPhone.withoutPlus())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - короткий пароль")
    fun error400WhenPasswordTooShort() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidPassword.tooShort())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - пароль без цифр")
    fun error400WhenPasswordHasNoDigits() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidPassword.noDigits())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - пароль без спецсимволов")
    fun error400WhenPasswordHasNoSpecialChars() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidPassword.noSpecialChars())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - пароль без заглавных")
    fun error400WhenPasswordHasNoUppercase() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidPassword.noUppercase())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - пароль содержит имя")
    fun error400WhenPasswordContainsName() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.PiiInPassword.containsName())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - короткое имя")
    fun error400WhenFullNameTooShort() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidFullName.tooShort())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("400 - длинное имя")
    fun error400WhenFullNameTooLong() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidFullName.tooLong())
            response.status shouldBe 400
        }
    }

    @Test
    @DisplayName("409 - дубликат email")
    fun error409WhenEmailAlreadyRegistered() {
        runBlocking {
            val first = RegistrationTestData.validRequest()
            apiClient.register(first)
            val second = RegistrationTestData.validRequest().copy(email = first.email)
            val response = apiClient.register(second)
            response.status shouldBe 409
        }
    }

    @Test
    @DisplayName("409 - дубликат телефона")
    fun error409WhenPhoneAlreadyRegistered() {
        runBlocking {
            val first = RegistrationTestData.validRequest()
            apiClient.register(first)
            val second = RegistrationTestData.validRequest().copy(phone = first.phone)
            val response = apiClient.register(second)
            response.status shouldBe 409
        }
    }

    @Test
    @DisplayName("429 - rate limit")
    fun error429WhenRateLimitExceeded() {
        runBlocking {
            repeat(6) { apiClient.register(RegistrationTestData.validRequest()) }
            val response = apiClient.register(RegistrationTestData.validRequest())
            response.status shouldBe 429
        }
    }

    @Test
    @DisplayName("UTF-8 в имени")
    fun successWithCyrillicFullName() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.SpecialCharacters.cyrillic())
            response.status shouldBe 201
        }
    }

    @Test
    @DisplayName("SQL Injection отклонен")
    fun sqlInjectionRejected() {
        runBlocking {
            val response = apiClient.register(RegistrationTestData.InvalidEmail.sqlInjection())
            response.status shouldBe 400
        }
    }
}
