package registration.tests

import io.ktor.client.call.body
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Link
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import registration.helpers.RegistrationHelper
import registration.helpers.RegistrationTestData
import registration.requests.ErrorResponse
import registration.requests.RegisterRequest
import registration.requests.RegistrationClient
import kotlin.test.assertEquals

@Epic("Registration")
@Feature("POST /api/v1/users/register")
class RegistrationNegativeTests {

    data class ValidationCase(
        val scenarioId: String,
        val displayName: String,
        val request: RegisterRequest,
        val expectedField: String?
    )

    companion object {
        @JvmStatic
        fun provideMissingFieldCases(): List<ValidationCase> = listOf(
            ValidationCase(
                scenarioId = "REG-03",
                displayName = "Отсутствует поле email",
                request = RegisterRequest(
                    email = null,
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "email"
            ),
            ValidationCase(
                scenarioId = "REG-04",
                displayName = "Отсутствует поле phone",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = null,
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "phone"
            ),
            ValidationCase(
                scenarioId = "REG-05",
                displayName = "Отсутствует поле password",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = null,
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "password"
            ),
            ValidationCase(
                scenarioId = "REG-06",
                displayName = "Отсутствует поле full_name",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = null
                ),
                expectedField = "full_name"
            ),
            ValidationCase(
                scenarioId = "REG-10",
                displayName = "Поле email — null",
                request = RegisterRequest(
                    email = null,
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "email"
            ),
            ValidationCase(
                scenarioId = "REG-11",
                displayName = "Поле email — пустая строка",
                request = RegisterRequest(
                    email = "",
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "email"
            ),
            ValidationCase(
                scenarioId = "REG-12",
                displayName = "Поле phone — null",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = null,
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "phone"
            ),
            ValidationCase(
                scenarioId = "REG-13",
                displayName = "Поле phone — пустая строка",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = "",
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "phone"
            ),
            ValidationCase(
                scenarioId = "REG-14",
                displayName = "Поле password — null",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = null,
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "password"
            ),
            ValidationCase(
                scenarioId = "REG-15",
                displayName = "Поле password — пустая строка",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = "",
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "password"
            ),
            ValidationCase(
                scenarioId = "REG-16",
                displayName = "Поле full_name — null",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = null
                ),
                expectedField = "full_name"
            ),
            ValidationCase(
                scenarioId = "REG-17",
                displayName = "Поле full_name — пустая строка",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = ""
                ),
                expectedField = "full_name"
            )
        )

        @JvmStatic
        fun providePasswordRuleCases(): List<ValidationCase> = listOf(
            ValidationCase(
                scenarioId = "REG-20",
                displayName = "Пароль без заглавной буквы",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.passwordNoUppercase(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "password"
            ),
            ValidationCase(
                scenarioId = "REG-21",
                displayName = "Пароль без цифры",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.passwordNoDigit(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "password"
            ),
            ValidationCase(
                scenarioId = "REG-22",
                displayName = "Пароль без спецсимвола",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.passwordNoSpecial(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "password"
            )
        )
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("provideMissingFieldCases")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "Scenarios REG-03 to REG-17", url = "file://audit/test-scenarios.md")
    fun `missing or null or empty field returns 400`(case: ValidationCase) = runTest {
        val response = RegistrationClient.register(case.request)

        assertEquals(400, response.status.value, "${case.scenarioId}: expected 400 Bad Request")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("VALIDATION_ERROR", errorBody.code, "${case.scenarioId}: error code mismatch")
        if (case.expectedField != null) {
            assertEquals(case.expectedField, errorBody.field, "${case.scenarioId}: error field mismatch")
        }
        RegistrationHelper.verifyUserNotInDb(case.request.email?.toString() ?: "null")
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("providePasswordRuleCases")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "Scenarios REG-20 to REG-22", url = "file://audit/test-scenarios.md")
    fun `invalid password complexity returns 400`(case: ValidationCase) = runTest {
        val response = RegistrationClient.register(case.request)

        assertEquals(400, response.status.value, "${case.scenarioId}: expected 400 Bad Request")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("VALIDATION_ERROR", errorBody.code, "${case.scenarioId}: error code mismatch")
        assertEquals("password", errorBody.field, "${case.scenarioId}: error field must be 'password'")
        RegistrationHelper.verifyUserNotInDb(case.request.email?.toString() ?: "null")
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("REG-07: Пустое тело запроса {}")
    @Link(name = "Scenario REG-07", url = "file://audit/test-scenarios.md")
    fun `REG-07 empty body returns 400`() = runTest {
        val response = RegistrationClient.registerRaw("{}")

        assertEquals(400, response.status.value, "REG-07: expected 400 Bad Request")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("VALIDATION_ERROR", errorBody.code, "REG-07: error code mismatch")
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("REG-08: Malformed JSON")
    @Link(name = "Scenario REG-08", url = "file://audit/test-scenarios.md")
    fun `REG-08 malformed json returns 400`() = runTest {
        val response = RegistrationClient.registerRaw("{email: missing_quote, phone:}")

        assertEquals(400, response.status.value, "REG-08: expected 400 Bad Request")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("VALIDATION_ERROR", errorBody.code, "REG-08: error code mismatch")
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("REG-09: Поле email — неверный тип (число)")
    @Link(name = "Scenario REG-09", url = "file://audit/test-scenarios.md")
    fun `REG-09 email wrong type integer returns 400`() = runTest {
        val request = RegisterRequest(
            email = 42,
            phone = RegistrationTestData.uniquePhone(),
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )

        val response = RegistrationClient.register(request)

        assertEquals(400, response.status.value, "REG-09: expected 400 Bad Request")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("VALIDATION_ERROR", errorBody.code, "REG-09: error code mismatch")
        assertEquals("email", errorBody.field, "REG-09: error field must be 'email'")
        RegistrationHelper.verifyUserNotInDb("42")
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("REG-18: Конфликт — email уже существует")
    @Link(name = "Scenario REG-18", url = "file://audit/test-scenarios.md")
    fun `REG-18 conflict email already exists returns 409`() = runTest {
        val existingEmail = RegistrationTestData.uniqueEmail()
        val setupRequest = RegisterRequest(
            email = existingEmail,
            phone = RegistrationTestData.uniquePhone(),
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )
        RegistrationClient.register(setupRequest)

        val conflictRequest = RegisterRequest(
            email = existingEmail,
            phone = RegistrationTestData.uniquePhone(),
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )
        val response = RegistrationClient.register(conflictRequest)

        assertEquals(409, response.status.value, "REG-18: expected 409 Conflict")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("CONFLICT", errorBody.code, "REG-18: error code mismatch")
        assertEquals("email", errorBody.field, "REG-18: error field must be 'email'")
        RegistrationHelper.verifyNoDuplicateInDb(existingEmail)
        RegistrationHelper.deleteUserFromDb(existingEmail)
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("REG-19: Конфликт — phone уже существует")
    @Link(name = "Scenario REG-19", url = "file://audit/test-scenarios.md")
    fun `REG-19 conflict phone already exists returns 409`() = runTest {
        val existingPhone = RegistrationTestData.uniquePhone()
        val setupRequest = RegisterRequest(
            email = RegistrationTestData.uniqueEmail(),
            phone = existingPhone,
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )
        RegistrationClient.register(setupRequest)
        val setupEmail = setupRequest.email.toString()

        val conflictRequest = RegisterRequest(
            email = RegistrationTestData.uniqueEmail(),
            phone = existingPhone,
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )
        val response = RegistrationClient.register(conflictRequest)

        assertEquals(409, response.status.value, "REG-19: expected 409 Conflict")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("CONFLICT", errorBody.code, "REG-19: error code mismatch")
        assertEquals("phone", errorBody.field, "REG-19: error field must be 'phone'")
        RegistrationHelper.deleteUserFromDb(setupEmail)
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("REG-23: Пароль содержит токен из full_name (PII — базовый sad path)")
    @Link(name = "Scenario REG-23", url = "file://audit/test-scenarios.md")
    fun `REG-23 password contains name token returns 400`() = runTest {
        val request = RegisterRequest(
            email = RegistrationTestData.uniqueEmail(),
            phone = RegistrationTestData.uniquePhone(),
            password = RegistrationTestData.passwordContainingNameToken(),
            fullName = RegistrationTestData.nameMatchingPassword()
        )

        val response = RegistrationClient.register(request)

        assertEquals(400, response.status.value, "REG-23: expected 400 Bad Request")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("VALIDATION_ERROR", errorBody.code, "REG-23: error code mismatch")
        assertEquals("password", errorBody.field, "REG-23: error field must be 'password'")
        RegistrationHelper.verifyUserNotInDb(request.email.toString())
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("REG-24: SMS-шлюз недоступен — 503, транзакция откатана")
    @Link(name = "Scenario REG-24", url = "file://audit/test-scenarios.md")
    fun `REG-24 sms gateway unavailable returns 503 and rolls back`() = runTest {
        val email = RegistrationTestData.uniqueEmail()
        val request = RegisterRequest(
            email = email,
            phone = RegistrationTestData.uniquePhone(),
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )

        val response = RegistrationClient.register(request)

        assertEquals(503, response.status.value, "REG-24: expected 503 Service Unavailable when SMS gateway is down")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("SERVICE_UNAVAILABLE", errorBody.code, "REG-24: error code mismatch")
        RegistrationHelper.verifyUserNotInDb(email)
    }
}
