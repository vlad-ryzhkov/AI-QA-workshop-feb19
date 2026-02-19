package registration.tests

import io.ktor.client.call.body
import io.qameta.allure.Epic
import io.qameta.allure.Feature
import io.qameta.allure.Link
import io.qameta.allure.Severity
import io.qameta.allure.SeverityLevel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
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
class RegistrationSecurityTests {

    data class InjectionCase(
        val scenarioId: String,
        val displayName: String,
        val request: RegisterRequest,
        val expectedField: String
    )

    private val createdEmails = mutableListOf<String>()

    @AfterEach
    fun cleanup() {
        createdEmails.forEach { email -> RegistrationHelper.deleteUserFromDb(email) }
        createdEmails.clear()
    }

    companion object {
        @JvmStatic
        fun provideInjectionCases(): List<InjectionCase> = listOf(
            InjectionCase(
                scenarioId = "REG-39",
                displayName = "SQL Injection в поле email",
                request = RegisterRequest(
                    email = "' OR 1=1--@example.com",
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "email"
            ),
            InjectionCase(
                scenarioId = "REG-40",
                displayName = "SQL Injection в поле full_name",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = "'; DROP TABLE users;--"
                ),
                expectedField = "full_name"
            ),
            InjectionCase(
                scenarioId = "REG-41",
                displayName = "XSS payload в поле full_name",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = "<script>alert(1)</script>"
                ),
                expectedField = "full_name"
            ),
            InjectionCase(
                scenarioId = "REG-42",
                displayName = "SSTI payload в поле full_name",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = "{{7*7}}"
                ),
                expectedField = "full_name"
            )
        )
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("provideInjectionCases")
    @Severity(SeverityLevel.BLOCKER)
    @Link(name = "Scenarios REG-39 to REG-42", url = "file://audit/test-scenarios.md")
    fun `injection payloads are rejected with 400`(case: InjectionCase) = runTest {
        val response = RegistrationClient.register(case.request)

        assertEquals(400, response.status.value, "${case.scenarioId}: injection payload must be rejected with 400")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("VALIDATION_ERROR", errorBody.code, "${case.scenarioId}: error code mismatch")
        assertEquals(case.expectedField, errorBody.field, "${case.scenarioId}: error field mismatch")
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("REG-43: IDOR — тело содержит посторонний user_id")
    @Link(name = "Scenario REG-43", url = "file://audit/test-scenarios.md")
    fun `REG-43 idor extra user_id field ignored or rejected`() = runTest {
        val email = RegistrationTestData.uniqueEmail()
        val existingUserId = "00000000-0000-0000-0000-000000000001"
        val request = RegisterRequest(
            email = email,
            phone = RegistrationTestData.uniquePhone(),
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName(),
            userId = existingUserId
        )

        val response = RegistrationClient.register(request)

        val status = response.status.value
        val isAccepted = status == 201
        val isRejected = status == 400
        assertTrue(
            isAccepted || isRejected,
            "REG-43: server must return 201 (ignoring user_id) or 400 (rejecting unknown field), got $status"
        )

        if (isAccepted) {
            createdEmails.add(email)
        }
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("REG-44: Поле password маскируется в логах приложения")
    @Link(name = "Scenario REG-44", url = "file://audit/test-scenarios.md")
    fun `REG-44 password is masked in application logs`() = runTest {
        val password = RegistrationTestData.validPassword()
        val request = RegisterRequest(
            email = RegistrationTestData.uniqueEmail(),
            phone = RegistrationTestData.uniquePhone(),
            password = password,
            fullName = RegistrationTestData.validName()
        )

        RegistrationClient.register(request)

        RegistrationHelper.verifyPasswordMaskedInLogs(password)
    }
}

private fun assertTrue(condition: Boolean, message: String) {
    kotlin.test.assertTrue(condition, message)
}
