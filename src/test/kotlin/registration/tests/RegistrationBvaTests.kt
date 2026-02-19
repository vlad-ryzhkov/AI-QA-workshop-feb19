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
import registration.requests.RegisterResponse
import registration.requests.RegistrationClient
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Epic("Registration")
@Feature("POST /api/v1/users/register")
class RegistrationBvaTests {

    data class BvaPassCase(
        val scenarioId: String,
        val displayName: String,
        val request: RegisterRequest
    )

    data class BvaFailCase(
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
        fun provideBvaPassCases(): List<BvaPassCase> = listOf(
            BvaPassCase(
                scenarioId = "REG-25",
                displayName = "Email — ровно 254 символа (Max, PASS)",
                request = RegisterRequest(
                    email = RegistrationTestData.emailOf254Chars(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                )
            ),
            BvaPassCase(
                scenarioId = "REG-27",
                displayName = "Phone — ровно 8 символов (Min, PASS)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.phoneOf8Chars(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                )
            ),
            BvaPassCase(
                scenarioId = "REG-29",
                displayName = "Phone — ровно 16 символов (Max, PASS)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.phoneOf16Chars(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                )
            ),
            BvaPassCase(
                scenarioId = "REG-31",
                displayName = "full_name — ровно 2 символа (Min, PASS)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.nameOf2Chars()
                )
            ),
            BvaPassCase(
                scenarioId = "REG-33",
                displayName = "full_name — ровно 100 символов (Max, PASS)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.nameOf100Chars()
                )
            ),
            BvaPassCase(
                scenarioId = "REG-34",
                displayName = "Пароль — ровно 8 символов (Min, PASS)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPasswordOf8Chars(),
                    fullName = RegistrationTestData.validName()
                )
            ),
            BvaPassCase(
                scenarioId = "REG-36",
                displayName = "Пароль — ровно 64 символа (Max, PASS)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPasswordOf64Chars(),
                    fullName = RegistrationTestData.validName()
                )
            )
        )

        @JvmStatic
        fun provideBvaFailCases(): List<BvaFailCase> = listOf(
            BvaFailCase(
                scenarioId = "REG-26",
                displayName = "Email — 255 символов (Max+1, FAIL)",
                request = RegisterRequest(
                    email = RegistrationTestData.emailOf255Chars(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "email"
            ),
            BvaFailCase(
                scenarioId = "REG-28",
                displayName = "Phone — 7 символов (Min-1, FAIL)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.phoneOf7Chars(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "phone"
            ),
            BvaFailCase(
                scenarioId = "REG-30",
                displayName = "Phone — 17 символов (Max+1, FAIL)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.phoneOf17Chars(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "phone"
            ),
            BvaFailCase(
                scenarioId = "REG-32",
                displayName = "full_name — 1 символ (Min-1, FAIL)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.validPassword(),
                    fullName = RegistrationTestData.nameOf1Char()
                ),
                expectedField = "full_name"
            ),
            BvaFailCase(
                scenarioId = "REG-35",
                displayName = "Пароль — 7 символов (Min-1, FAIL)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.passwordOf7Chars(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "password"
            ),
            BvaFailCase(
                scenarioId = "REG-37",
                displayName = "Пароль — 65 символов (Max+1, FAIL)",
                request = RegisterRequest(
                    email = RegistrationTestData.uniqueEmail(),
                    phone = RegistrationTestData.uniquePhone(),
                    password = RegistrationTestData.passwordOf65Chars(),
                    fullName = RegistrationTestData.validName()
                ),
                expectedField = "password"
            )
        )
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("provideBvaPassCases")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "Scenarios REG-25,27,29,31,33,34,36", url = "file://audit/test-scenarios.md")
    fun `BVA boundary pass cases return 201`(case: BvaPassCase) = runTest {
        val response = RegistrationClient.register(case.request)

        assertEquals(201, response.status.value, "${case.scenarioId}: expected 201 Created at boundary")
        val body = response.body<RegisterResponse>()
        assertNotNull(body.verificationToken, "${case.scenarioId}: verification_token must not be null")
        assertTrue(body.verificationToken.isNotBlank(), "${case.scenarioId}: verification_token must be non-blank")
        assertNotNull(body.expiresAt, "${case.scenarioId}: expires_at must not be null")

        val email = case.request.email?.toString() ?: return@runTest
        RegistrationHelper.verifyUserInDb(email)
        createdEmails.add(email)
    }

    @ParameterizedTest(name = "{0}: {1}")
    @MethodSource("provideBvaFailCases")
    @Severity(SeverityLevel.CRITICAL)
    @Link(name = "Scenarios REG-26,28,30,32,35,37", url = "file://audit/test-scenarios.md")
    fun `BVA boundary fail cases return 400`(case: BvaFailCase) = runTest {
        val response = RegistrationClient.register(case.request)

        assertEquals(400, response.status.value, "${case.scenarioId}: expected 400 at boundary+1")
        val errorBody = response.body<ErrorResponse>()
        assertEquals("VALIDATION_ERROR", errorBody.code, "${case.scenarioId}: error code mismatch")
        assertEquals(case.expectedField, errorBody.field, "${case.scenarioId}: error field mismatch")

        val email = case.request.email?.toString() ?: "null"
        RegistrationHelper.verifyUserNotInDb(email)
    }
}
