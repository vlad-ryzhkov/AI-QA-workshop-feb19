package prompted.registration2.data

import prompted.registration2.models.RegistrationRequest
import java.util.UUID

/**
 * Test Data Factory (Object Mother pattern)
 * All variations through copy(). No hardcoded PII.
 */
object RegistrationTestData {

    private fun uniqueId() = UUID.randomUUID().toString().take(8)
    private fun timestamp() = System.currentTimeMillis().toString().takeLast(7)

    // ==================== BASE VALID REQUEST ====================

    /**
     * Returns unique valid request on each invocation
     * Password: Secure#1x (8 chars, uppercase, digit, special)
     */
    fun validRequest() = RegistrationRequest(
        email = "auto_${uniqueId()}@example.com",
        phone = "+7999${timestamp()}",
        password = "Secure#1x",
        fullName = "Test User"
    )

    // ==================== EMAIL VARIATIONS ====================

    object InvalidEmail {
        fun empty() = validRequest().copy(email = "")
        fun withoutAt() = validRequest().copy(email = "userexample.com")
        fun withoutDomain() = validRequest().copy(email = "user@")
        fun sqlInjection() = validRequest().copy(email = "test'OR'1'='1@example.com")
    }

    object DuplicateEmail {
        /**
         * Use this email for conflict tests.
         * Precondition: register this user first, then use same email.
         */
        fun existingEmail() = "existing_${uniqueId()}@example.com"
    }

    // ==================== PHONE VARIATIONS ====================

    object InvalidPhone {
        fun empty() = validRequest().copy(phone = "")
        fun withoutPlus() = validRequest().copy(phone = "79990000000")
        fun withLetters() = validRequest().copy(phone = "+7999ABC0000")
    }

    object DuplicatePhone {
        /**
         * Use this phone for conflict tests.
         * Precondition: register this user first, then use same phone.
         */
        fun existingPhone() = "+7888${timestamp()}"
    }

    // ==================== PASSWORD VARIATIONS ====================

    object InvalidPassword {
        fun empty() = validRequest().copy(password = "")
        fun tooShort() = validRequest().copy(password = "Aa1!xxx")  // 7 chars
        fun noDigits() = validRequest().copy(password = "Abcdefgh!")
        fun noSpecialChars() = validRequest().copy(password = "Abcdefg1")
        fun noUppercase() = validRequest().copy(password = "abcdefg1!")
    }

    object PasswordBoundary {
        /** BVA Min: exactly 8 characters - should pass */
        fun exactlyMin() = validRequest().copy(password = "Aa1!xxxx")  // 8 chars
    }

    object PasswordPII {
        /** Password contains 4+ chars from full_name - should fail */
        fun containsFullNamePart() = validRequest().copy(
            fullName = "TestUser",
            password = "Test1pass!"  // "Test" (4 chars) from "TestUser"
        )

        /** Password contains 3 chars from full_name - should pass (>3 rule) */
        fun containsThreeCharPart() = validRequest().copy(
            fullName = "TestUser",
            password = "Tes1pass!x"  // "Tes" (3 chars) - allowed
        )

        /** Password contains part of email local-part - should fail */
        fun containsEmailPart(): RegistrationRequest {
            val emailLocal = "testuser_${uniqueId()}"
            return validRequest().copy(
                email = "$emailLocal@example.com",
                password = "Testuser1!"  // "testuser" from email
            )
        }
    }

    // ==================== FULL_NAME VARIATIONS ====================

    object InvalidFullName {
        fun empty() = validRequest().copy(fullName = "")
        fun tooShort() = validRequest().copy(fullName = "A")  // 1 char
        fun tooLong() = validRequest().copy(fullName = "A".repeat(101))  // 101 chars
    }

    object FullNameBoundary {
        /** BVA Min: exactly 2 characters - should pass */
        fun exactlyMin() = validRequest().copy(fullName = "Ab")

        /** BVA Max: exactly 100 characters - should pass */
        fun exactlyMax() = validRequest().copy(fullName = "A".repeat(100))
    }

    // ==================== SECURITY PAYLOADS ====================

    object Security {
        fun xssInFullName() = validRequest().copy(fullName = "<script>alert(1)</script>")
    }

    // ==================== RAW JSON FOR STRUCTURAL TESTS ====================

    object RawJson {
        private fun phone() = "+7999${timestamp()}"
        private fun email() = "auto_${uniqueId()}@example.com"

        fun missingEmail() = """
            {
                "phone": "${phone()}",
                "password": "Secure#1x",
                "full_name": "Test User"
            }
        """.trimIndent()

        fun missingPhone() = """
            {
                "email": "${email()}",
                "password": "Secure#1x",
                "full_name": "Test User"
            }
        """.trimIndent()

        fun missingPassword() = """
            {
                "email": "${email()}",
                "phone": "${phone()}",
                "full_name": "Test User"
            }
        """.trimIndent()

        fun missingFullName() = """
            {
                "email": "${email()}",
                "phone": "${phone()}",
                "password": "Secure#1x"
            }
        """.trimIndent()
    }
}
