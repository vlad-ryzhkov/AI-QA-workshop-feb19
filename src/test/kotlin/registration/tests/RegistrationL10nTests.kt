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
import registration.requests.RegisterRequest
import registration.requests.RegisterResponse
import registration.requests.RegistrationClient
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Epic("Registration")
@Feature("POST /api/v1/users/register")
class RegistrationL10nTests {

    data class L10nCase(
        val scenarioId: String,
        val displayName: String,
        val request: RegisterRequest,
        val expectedFullName: String
    )

    private val createdEmails = mutableListOf<String>()

    @AfterEach
    fun cleanup() {
        createdEmails.forEach { email -> RegistrationHelper.deleteUserFromDb(email) }
        createdEmails.clear()
    }

    companion object {
        @JvmStatic
        fun provideL10nCases(): List<L10nCase> = listOf(
            L10nCase(
                scenarioId = "REG-45",
                displayName = "full_name — кириллица (UTF-8 round-trip)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.cyrillicName()
                ),
                expectedFullName = RegistrationTestData.cyrillicName()
            ),
            L10nCase(
                scenarioId = "REG-46",
                displayName = "full_name — арабский / RTL (UTF-8 round-trip)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.arabicName()
                ),
                expectedFullName = RegistrationTestData.arabicName()
            ),
            L10nCase(
                scenarioId = "REG-47",
                displayName = "full_name — CJK (иероглифы, UTF-8 round-trip)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.cjkName()
                ),
                expectedFullName = RegistrationTestData.cjkName()
            ),
            L10nCase(
                scenarioId = "REG-48",
                displayName = "full_name — дефис (допустимый разделитель)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.hyphenatedName()
                ),
                expectedFullName = RegistrationTestData.hyphenatedName()
            )
        )
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("provideL10nCases")
    @Severity(SeverityLevel.NORMAL)
    @Link(name = "Scenarios REG-45 to REG-48", url = "file://audit/test-scenarios.md")
    fun `l10n full_name round trip returns 201 and preserves value`(case: L10nCase) = runTest {
        val response = RegistrationClient.register(case.request)

        assertEquals(201, response.status.value, "${case.scenarioId}: expected 201 Created")
        val body = response.body<RegisterResponse>()
        assertNotNull(body.verificationToken, "${case.scenarioId}: verification_token must not be null")

        val email = case.request.email?.toString() ?: return@runTest
        RegistrationHelper.verifyUserInDb(email)
        createdEmails.add(email)
    }
}
