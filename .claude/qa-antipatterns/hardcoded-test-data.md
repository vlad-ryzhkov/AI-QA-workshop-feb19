# Hardcoded Test Data

## Why this is bad

Захардкоженные данные в тестах:
- Конфликты при параллельном запуске (одинаковые email/phone)
- Невозможно запустить тест дважды без cleanup
- Скрывают edge cases (всегда один и тот же input)
- Создают ложное чувство покрытия

## Bad Example

```kotlin
// ❌ BAD: Статичные данные
object TestData {
    val USER_EMAIL = "test@example.com"  // Конфликт при втором запуске
    val USER_PHONE = "+79991234567"
    val USER_PASSWORD = "Password123!"
}

@Test
fun `user can register`() {
    val payload = RegisterRequest(
        email = TestData.USER_EMAIL,  // Всегда одинаковый
        phone = TestData.USER_PHONE,
        password = TestData.USER_PASSWORD
    )
    // ...
}
```

## Good Example

```kotlin
// ✅ GOOD: Object Mother pattern с генерацией
object RegistrationTestData {

    fun validRequest() = RegisterRequest(
        email = "auto_${System.currentTimeMillis()}@example.com",
        phone = "+7999${(1000000..9999999).random()}",
        password = "Test#${UUID.randomUUID().toString().take(8)}",
        fullName = "Test User"
    )

    fun withInvalidEmail() = validRequest().copy(
        email = "invalid-email"
    )

    fun withWeakPassword() = validRequest().copy(
        password = "weak"
    )
}

@Test
fun `user can register`() {
    val payload = RegistrationTestData.validRequest()  // Уникальный каждый раз
    // ...
}
```

## What to look for in code review

- `const val` или `val` с фиксированными email/phone/id
- Одинаковые данные в нескольких тестах
- Комментарии "change this before running"
- Тесты с `@Disabled` из-за конфликтов данных
