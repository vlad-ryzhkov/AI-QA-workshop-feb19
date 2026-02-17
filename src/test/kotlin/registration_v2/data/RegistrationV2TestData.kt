package registration_v2.data

import registration_v2.models.RegistrationV2Request
import java.util.UUID

object RegistrationV2TestData {

    private fun uniqueId() = UUID.randomUUID().toString().take(8)

    // Base valid request (уникальный при каждом вызове)
    fun validRequest() = RegistrationV2Request(
        email = "auto_${uniqueId()}@example.com",
        phone = "+7999${System.currentTimeMillis().toString().takeLast(7)}",
        password = "Strong#Pass99",
        fullName = "Test User"
    )

    // Модификаторы через copy()
    fun withEmail(email: String) = validRequest().copy(email = email)
    fun withPhone(phone: String) = validRequest().copy(phone = phone)
    fun withPassword(password: String) = validRequest().copy(password = password)
    fun withFullName(name: String) = validRequest().copy(fullName = name)

    // ==================== EMAIL ====================

    object InvalidEmail {
        fun empty() = withEmail("")
        fun withoutAt() = withEmail("userexample.com")
        fun withoutDomain() = withEmail("user@")
        fun sqlInjection() = withEmail("test'OR'1'='1@example.com")
    }

    // ==================== PHONE ====================

    object InvalidPhone {
        fun empty() = withPhone("")
        fun withoutPlusPrefix() = withPhone("79990000000")
        fun withLetters() = withPhone("+7999ABC0000")
    }

    // ==================== PASSWORD ====================

    object InvalidPassword {
        fun empty() = withPassword("")
        fun sevenChars() = withPassword("Short1!") // BVA Min-1
        fun noDigits() = withPassword("NoDigits!@#A")
        fun noSpecialChars() = withPassword("NoSpecial123A")
        fun noUppercase() = withPassword("nouppercase1#")
    }

    object ValidPassword {
        fun exactlyEightChars() = withPassword("Eight1#A") // BVA Min (ровно 8 символов)
    }

    object PasswordWithPII {
        // Пароль содержит часть email (>3 символов)
        fun withEmailPart(): RegistrationV2Request {
            val email = "testuser_${uniqueId()}@example.com"
            return RegistrationV2Request(
                email = email,
                phone = "+7999${System.currentTimeMillis().toString().takeLast(7)}",
                password = "TestUser123#", // Содержит "testuser" из email
                fullName = "Test User"
            )
        }

        // Пароль содержит часть full_name (>3 символов)
        fun withFullNamePart() = RegistrationV2Request(
            email = "auto_${uniqueId()}@example.com",
            phone = "+7999${System.currentTimeMillis().toString().takeLast(7)}",
            password = "Test1234#", // Содержит "Test" из "Test User" (4 символа, >3)
            fullName = "Test User"
        )

        // Пароль содержит часть имени в другом регистре (case-insensitive)
        fun withNamePartCaseInsensitive() = RegistrationV2Request(
            email = "auto_${uniqueId()}@example.com",
            phone = "+7999${System.currentTimeMillis().toString().takeLast(7)}",
            password = "TEST1234#", // "TEST" = "Test" case-insensitive (4 символа, >3)
            fullName = "Test User"
        )

        // BVA: Ровно 3 символа из имени (допускается)
        fun withThreeCharsFromName() = RegistrationV2Request(
            email = "auto_${uniqueId()}@example.com",
            phone = "+7999${System.currentTimeMillis().toString().takeLast(7)}",
            password = "Tes1234#", // "Tes" = ровно 3 символа из "TestUser"
            fullName = "TestUser"
        )

        // BVA: Ровно 4 символа из имени (запрещено)
        fun withFourCharsFromName() = RegistrationV2Request(
            email = "auto_${uniqueId()}@example.com",
            phone = "+7999${System.currentTimeMillis().toString().takeLast(7)}",
            password = "Test1234#", // "Test" = ровно 4 символа из "TestUser" (>3)
            fullName = "TestUser"
        )
    }

    // ==================== FULL_NAME ====================

    object BoundaryFullName {
        fun twoChars() = withFullName("Li") // BVA Min
        fun oneChar() = withFullName("A") // BVA Min-1
        fun hundredChars() = withFullName("A".repeat(100)) // BVA Max
        fun hundredOneChars() = withFullName("A".repeat(101)) // BVA Max+1
        fun empty() = withFullName("")
    }

    // ==================== SECURITY ====================

    object SecurityPayloads {
        fun xssInFullName() = withFullName("<script>alert(1)</script>")
    }

    // ==================== RAW JSON для structural tests ====================

    object RawJson {
        fun missingEmail() = """{"phone": "+79991234567", "password": "Strong#Pass99", "full_name": "Test User"}"""
        fun missingPhone() = """{"email": "test@example.com", "password": "Strong#Pass99", "full_name": "Test User"}"""
        fun missingFullName() = """{"email": "test@example.com", "phone": "+79991234567", "password": "Strong#Pass99"}"""
    }
}
