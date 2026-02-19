package registration.helpers

import registration.requests.RegisterRequest
import java.util.UUID

object RegistrationTestData {

    fun uniqueEmail(): String = "user.${UUID.randomUUID().toString().take(8)}@example.com"

    fun uniquePhone(): String {
        val digits = (100000000L..999999999L).random()
        return "+7$digits"
    }

    fun validPassword(): String = "Safe_Pass1!"

    fun validName(): String = "Alex Kideer"

    fun validRequest(): RegisterRequest = RegisterRequest(
        email = uniqueEmail(),
        phone = uniquePhone(),
        password = validPassword(),
        fullName = validName()
    )

    fun emailOf254Chars(): String {
        val localPart = "a".repeat(244)
        return "$localPart@example.com"
    }

    fun emailOf255Chars(): String {
        val localPart = "a".repeat(245)
        return "$localPart@example.com"
    }

    fun phoneOf8Chars(): String = "+7999123"

    fun phoneOf7Chars(): String = "+799912"

    fun phoneOf16Chars(): String = "+123456789012345"

    fun phoneOf17Chars(): String = "+1234567890123456"

    fun validPasswordOf8Chars(): String = "Abc1!xyz"

    fun validPasswordOf64Chars(): String = "Abc1!" + "x".repeat(59)

    fun passwordOf7Chars(): String = "Abc1!xy"

    fun passwordOf65Chars(): String = "Abc1!" + "x".repeat(60)

    fun passwordNoUppercase(): String = "abc1!xyzw"

    fun passwordNoDigit(): String = "Abcd!xyzw"

    fun passwordNoSpecial(): String = "Abcd1xyzw"

    fun nameOf2Chars(): String = "Аб"

    fun nameOf1Char(): String = "А"

    fun nameOf100Chars(): String = "A" + "b".repeat(49) + " " + "C" + "d".repeat(48)

    fun passwordContainingNameToken(): String = "JohnSafe1!"

    fun nameMatchingPassword(): String = "John Smith"

    fun cyrillicName(): String = "Иван Петров"

    fun arabicName(): String = "محمد علي"

    fun cjkName(): String = "张伟"

    fun hyphenatedName(): String = "Mary-Jane"
}
