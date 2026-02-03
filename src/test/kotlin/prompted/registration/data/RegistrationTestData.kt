package prompted.registration.data

import prompted.registration.models.RegisterRequest
import java.util.UUID

object RegistrationTestData {

    private fun uniqueId() = UUID.randomUUID().toString().take(8)

    fun validRequest() = RegisterRequest(
        email = "auto_${uniqueId()}@example.com",
        phone = "+7999${System.currentTimeMillis().toString().takeLast(7)}",
        password = "Strong#Pass99",
        fullName = "Test User"
    )

    fun withEmail(email: String) = validRequest().copy(email = email)

    fun withPhone(phone: String) = validRequest().copy(phone = phone)

    fun withPassword(password: String) = validRequest().copy(password = password)

    fun withFullName(fullName: String) = validRequest().copy(fullName = fullName)

    fun withEmailAndPassword(email: String, password: String) =
        validRequest().copy(email = email, password = password)

    fun withFullNameAndPassword(fullName: String, password: String) =
        validRequest().copy(fullName = fullName, password = password)

    object InvalidEmail {
        fun withoutAtSymbol() = withEmail("invalid-email.com")
        fun empty() = withEmail("")
        fun withDoubleAt() = withEmail("test@@example.com")
        fun withSpaces() = withEmail("test @example.com")
        fun sqlInjection() = withEmail("test@x.com'; DROP TABLE users;--")
    }

    object InvalidPhone {
        fun withoutPlus() = withPhone("79991234567")
        fun withLetters() = withPhone("+7999ABC4567")
        fun tooShort() = withPhone("+7999")
        fun tooLong() = withPhone("+7999123456789012345")
    }

    object InvalidPassword {
        fun tooShort() = withPassword("Short1!")
        fun exactlyMin() = withPassword("Eight1#A")
        fun noDigits() = withPassword("NoDigits!@#A")
        fun noSpecialChars() = withPassword("NoSpecial123A")
        fun noUppercase() = withPassword("nouppercase123!")
        fun noLowercase() = withPassword("NOLOWERCASE123!")
    }

    object PiiInPassword {
        fun containsEmailPart() = withEmailAndPassword(
            email = "vladimir@work.com",
            password = "Vlad123#Secure"
        )
        fun containsName() = withFullNameAndPassword(
            fullName = "Alex Kid",
            password = "Alex_2026!"
        )
        fun exactly3CharMatch() = withFullNameAndPassword(
            fullName = "Max Test",
            password = "Max_Strong99!"
        )
    }

    object InvalidFullName {
        fun tooShort() = withFullName("A")
        fun exactlyMin() = withFullName("Li")
        fun exactlyMax() = withFullName("A".repeat(100))
        fun tooLong() = withFullName("A".repeat(101))
        fun empty() = withFullName("")
    }

    object SpecialCharacters {
        fun cyrillic() = withFullName("Иван Петров")
        fun emoji() = withFullName("Test User 🚀")
        fun xssScript() = withFullName("<script>alert(1)</script>")
    }
}
