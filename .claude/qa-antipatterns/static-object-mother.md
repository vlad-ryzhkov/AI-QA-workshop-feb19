# Static Object Mother (API Tests)

**Applies to:** `/api-tests` (автоматизированные API тесты)

## Why this is bad

Статичные данные в Object Mother / TestData:
- Конфликты при параллельном запуске (одинаковые email/phone)
- Невозможно запустить тест дважды без cleanup
- Flaky тесты из-за `UNIQUE constraint violation`
- Скрывают проблемы изоляции между тестами

## Bad Example

```kotlin
// ❌ BAD: Статичные константы
object RegistrationTestData {
    val VALID_EMAIL = "test@example.com"      // Конфликт при втором запуске
    val VALID_PHONE = "+79991234567"
    val VALID_PASSWORD = "Password123!"

    fun validRequest() = RegisterRequest(
        email = VALID_EMAIL,  // Всегда одинаковый
        phone = VALID_PHONE,
        password = VALID_PASSWORD
    )
}

// ❌ BAD: Хардкод в функции без генерации
fun validRequest() = RegisterRequest(
    email = "fixed_test@example.com",  // Статика!
    phone = "+70001112233",
    password = "Test123!"
)
```

## Good Example

```kotlin
// ✅ GOOD: Object Mother с генерацией уникальных данных
object RegistrationTestData {

    fun validRequest() = RegisterRequest(
        email = "auto_${System.currentTimeMillis()}@example.com",
        phone = "+7${(9000000000..9999999999).random()}",
        password = "Test#${UUID.randomUUID().toString().take(8)}",
        fullName = "Test User"
    )

    // Модификации через copy() — email всё равно уникальный
    fun withInvalidEmail() = validRequest().copy(
        email = "invalid-email-no-at-sign"
    )

    fun withWeakPassword() = validRequest().copy(
        password = "weak"
    )

    fun withEmptyName() = validRequest().copy(
        fullName = ""
    )
}
```

## Pattern: Unique Suffix Generator

```kotlin
// ✅ Переиспользуемый генератор
object TestDataUtils {
    fun uniqueSuffix() = "${System.currentTimeMillis()}_${(1000..9999).random()}"
    fun uniqueEmail(prefix: String = "auto") = "${prefix}_${uniqueSuffix()}@example.com"
    fun uniquePhone() = "+7${(9000000000..9999999999).random()}"
}

object RegistrationTestData {
    fun validRequest() = RegisterRequest(
        email = TestDataUtils.uniqueEmail(),
        phone = TestDataUtils.uniquePhone(),
        // ...
    )
}
```

## What to look for in code review

- `const val` или `val` с фиксированными email/phone/id
- Функции `valid*()` без `System.currentTimeMillis()` или `UUID`
- Отсутствие рандомизации в данных, которые должны быть уникальными
- Тесты с `@Disabled` из-за "конфликтов данных"
- Комментарии "run cleanup before test" или "change email before running"
