package registration.tests

import io.kotest.matchers.shouldNotBe
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
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
import registration.requests.RegisterRequest
import registration.requests.RegisterResponse
import registration.requests.RegistrationClient
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Epic("Registration")
@Feature("POST /api/v1/users/register")
class RegistrationPositiveTests {

    private val createdEmails = mutableListOf<String>()

    @AfterEach
    fun cleanup() {
        createdEmails.forEach { email -> RegistrationHelper.deleteUserFromDb(email) }
        createdEmails.clear()
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("REG-01: Happy Path — минимально допустимые данные")
    @Link(name = "Scenario REG-01", url = "file://audit/test-scenarios.md")
    fun `REG-01 happy path minimal valid data`() = runTest {
        val email = RegistrationTestData.uniqueEmail()
        val request = RegisterRequest(
            email = email,
            phone = RegistrationTestData.phoneOf8Chars(),
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.nameOf2Chars()
        )

        val response = RegistrationClient.register(request)

        assertEquals(201, response.status.value, "REG-01: expected 201 Created")
        val body = response.body<RegisterResponse>()
        assertNotNull(body.verificationToken, "REG-01: verification_token must not be null")
        assertTrue(body.verificationToken.isNotBlank(), "REG-01: verification_token must be non-blank string")
        assertNotNull(body.expiresAt, "REG-01: expires_at must not be null")
        assertTrue(body.expiresAt.isNotBlank(), "REG-01: expires_at must be ISO8601 string")

        RegistrationHelper.verifyUserInDb(email)
        createdEmails.add(email)
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("REG-01h: Response headers — Happy Path")
    @Link(name = "Scenario REG-01h", url = "file://audit/test-scenarios.md")
    fun `REG-01h response headers happy path`() = runTest {
        val email = RegistrationTestData.uniqueEmail()
        val request = RegisterRequest(
            email = email,
            phone = RegistrationTestData.phoneOf8Chars(),
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.nameOf2Chars()
        )

        val response = RegistrationClient.register(request)

        assertEquals(201, response.status.value, "REG-01h: expected 201 Created")
        assertEquals(
            "application/json; charset=utf-8",
            response.headers["Content-Type"]?.lowercase(),
            "REG-01h: Content-Type must be application/json; charset=utf-8"
        )
        assertEquals(
            "nosniff",
            response.headers["X-Content-Type-Options"],
            "REG-01h: X-Content-Type-Options must be nosniff"
        )

        createdEmails.add(email)
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("REG-02: Happy Path — максимально допустимые данные")
    @Link(name = "Scenario REG-02", url = "file://audit/test-scenarios.md")
    fun `REG-02 happy path maximum valid data`() = runTest {
        val email = RegistrationTestData.emailOf254Chars()
        val request = RegisterRequest(
            email = email,
            phone = RegistrationTestData.phoneOf16Chars(),
            password = RegistrationTestData.validPasswordOf64Chars(),
            fullName = RegistrationTestData.nameOf100Chars()
        )

        val response = RegistrationClient.register(request)

        assertEquals(201, response.status.value, "REG-02: expected 201 Created")
        val body = response.body<RegisterResponse>()
        assertNotNull(body.verificationToken, "REG-02: verification_token must not be null")
        assertTrue(body.verificationToken.isNotBlank(), "REG-02: verification_token must be non-blank string")
        assertNotNull(body.expiresAt, "REG-02: expires_at must not be null")

        RegistrationHelper.verifyUserInDb(email)
        createdEmails.add(email)
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("REG-02h: Response headers — Max data Happy Path")
    @Link(name = "Scenario REG-02h", url = "file://audit/test-scenarios.md")
    fun `REG-02h response headers max data`() = runTest {
        val email = RegistrationTestData.emailOf254Chars()
        val request = RegisterRequest(
            email = email,
            phone = RegistrationTestData.phoneOf16Chars(),
            password = RegistrationTestData.validPasswordOf64Chars(),
            fullName = RegistrationTestData.nameOf100Chars()
        )

        val response = RegistrationClient.register(request)

        assertEquals(201, response.status.value, "REG-02h: expected 201 Created")
        assertEquals(
            "application/json; charset=utf-8",
            response.headers["Content-Type"]?.lowercase(),
            "REG-02h: Content-Type must be application/json; charset=utf-8"
        )
        assertEquals(
            "nosniff",
            response.headers["X-Content-Type-Options"],
            "REG-02h: X-Content-Type-Options must be nosniff"
        )

        createdEmails.add(email)
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("REG-38: SEC — публичный endpoint без Authorization")
    @Link(name = "Scenario REG-38", url = "file://audit/test-scenarios.md")
    fun `REG-38 public endpoint no authorization header`() = runTest {
        val email = RegistrationTestData.uniqueEmail()
        val request = RegisterRequest(
            email = email,
            phone = RegistrationTestData.uniquePhone(),
            password = RegistrationTestData.validPassword(),
            fullName = RegistrationTestData.validName()
        )

        val response = RegistrationClient.register(request)

        assertEquals(201, response.status.value, "REG-38: public endpoint must return 201, not 401")
        val body = response.body<RegisterResponse>()
        assertNotNull(body.verificationToken, "REG-38: verification_token must not be null")
        assertNotNull(body.expiresAt, "REG-38: expires_at must not be null")

        createdEmails.add(email)
    }
}
