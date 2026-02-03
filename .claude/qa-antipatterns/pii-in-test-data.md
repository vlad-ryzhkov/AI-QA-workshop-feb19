# PII in Test Data

## Why this is bad

Реальные персональные данные в тестах:
- Нарушение GDPR/152-ФЗ при утечке репозитория
- Случайная отправка на prod (copy-paste)
- Юридические риски при аудите
- Этические проблемы (данные реальных людей)

## Bad Example

```kotlin
// ❌ BAD: Реальные данные (даже если "свои")
object TestData {
    val REAL_EMAIL = "john.smith@company.com"
    val REAL_PHONE = "+79161234567"  // Чей-то реальный номер
    val REAL_NAME = "Иван Петров"
}

// ❌ BAD: Данные похожие на реальные
val payload = RegisterRequest(
    email = "ivan.petrov.1985@gmail.com",  // Выглядит как реальный
    phone = "+79031112233",
    fullName = "Петров Иван Сергеевич"
)
```

## Good Example

```kotlin
// ✅ GOOD: Очевидно тестовые данные
object TestData {
    fun email() = "test_${System.currentTimeMillis()}@example.com"
    fun phone() = "+70000000000"  // Явно невалидный
    fun name() = "Test User ${UUID.randomUUID().toString().take(4)}"
}

// ✅ GOOD: Домены для тестов (RFC 2606)
val safeEmails = listOf(
    "user@example.com",      // RFC 2606 reserved
    "test@example.org",
    "auto@test.example"
)

// ✅ GOOD: Faker для генерации
val faker = Faker()
val payload = RegisterRequest(
    email = faker.internet.safeEmail(),  // Гарантированно фейковый
    phone = faker.phoneNumber.phoneNumber(),
    fullName = faker.name.fullName()
)
```

## What to look for in code review

- Email с реальными доменами (@gmail.com, @yandex.ru, @company.com)
- Телефоны в реальных форматах (+7916..., +7903...)
- ФИО похожие на реальные (не "Test User")
- Номера карт, паспортов, ИНН
- Комментарии "это мой email" или "тестовый аккаунт Васи"
