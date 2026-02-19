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
import registration.helpers.RegistrationHelper
import registration.helpers.RegistrationTestData
import registration.requests.ErrorResponse
import registration.requests.RegisterRequest
import registration.requests.RegistrationClient
import kotlin.test.assertEquals

@Epic("Registration")
@Feature("POST /api/v1/users/register")
class RegistrationIdempotencyTests {

    private val createdEmails = mutableListOf<String>()

    @AfterEach
    fun cleanup() {
        createdEmails.forEach { email -> RegistrationHelper.deleteUserFromDb(email) }
        createdEmails.clear()
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("REG-49: Повторный запрос с теми же email + phone → конфликт")
    @Link(name = "Scenario REG-49", url = "file://audit/test-scenarios.md")
    fun `REG-49 duplicate request same email and phone returns 409`() = runTest {
        val email = RegistrationTestData.uniqueEmail()
        val phone = RegistrationTestData.uniquePhone()
        val request = RegisterRequest(
            email = email,
            phone = phone,
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )

        val firstResponse = RegistrationClient.register(request)
        assertEquals(201, firstResponse.status.value, "REG-49: первый запрос должен вернуть 201")
        createdEmails.add(email)

        val secondResponse = RegistrationClient.register(request)
        assertEquals(409, secondResponse.status.value, "REG-49: повторный запрос должен вернуть 409 Conflict")
        val errorBody = secondResponse.body<ErrorResponse>()
        assertEquals("CONFLICT", errorBody.code, "REG-49: error code mismatch")
        RegistrationHelper.verifyNoDuplicateInDb(email)
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("REG-50: Повторный запрос с тем же email + новый phone → конфликт по email")
    @Link(name = "Scenario REG-50", url = "file://audit/test-scenarios.md")
    fun `REG-50 duplicate email with new phone returns 409 on email`() = runTest {
        val email = RegistrationTestData.uniqueEmail()
        val phone1 = RegistrationTestData.uniquePhone()
        val phone2 = RegistrationTestData.uniquePhone()

        val firstRequest = RegisterRequest(
            email = email,
            phone = phone1,
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )
        val firstResponse = RegistrationClient.register(firstRequest)
        assertEquals(201, firstResponse.status.value, "REG-50: первый запрос должен вернуть 201")
        createdEmails.add(email)

        val secondRequest = RegisterRequest(
            email = email,
            phone = phone2,
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )
        val secondResponse = RegistrationClient.register(secondRequest)
        assertEquals(409, secondResponse.status.value, "REG-50: повторный запрос должен вернуть 409 Conflict")
        val errorBody = secondResponse.body<ErrorResponse>()
        assertEquals("CONFLICT", errorBody.code, "REG-50: error code mismatch")
        assertEquals("email", errorBody.field, "REG-50: error field must be 'email'")
        RegistrationHelper.verifyNoDuplicateInDb(email)
    }
}
