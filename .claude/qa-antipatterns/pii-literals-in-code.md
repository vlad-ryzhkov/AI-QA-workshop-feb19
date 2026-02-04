# PII Literals in Code (API Tests)

**Applies to:** `/api-tests` (автоматизированные API тесты)

## Why this is bad

Персональные данные в Kotlin коде автотестов:
- Код попадает в Git → утечка PII при публикации репозитория
- Copy-paste из тестов на production (случайно)
- Нарушение GDPR/152-ФЗ при аудите кодовой базы
- "Реалистичные" данные могут совпасть с реальными людьми

## Bad Example

```kotlin
// ❌ BAD: Реальные домены в литералах
object TestData {
    fun validRequest() = RegisterRequest(
        email = "ivan.petrov@gmail.com",     // Реальный домен!
        phone = "+79161234567",               // Реальный формат
        fullName = "Петров Иван Сергеевич"   // Похоже на реального человека
    )
}

// ❌ BAD: "Тестовые" данные коллег
const val TEST_EMAIL = "vasya.dev@company.com"  // PII!
const val TEST_PHONE = "+79031112233"            // Чей-то номер

// ❌ BAD: Платёжные данные
fun validPayment() = PaymentRequest(
    cardNumber = "4111111111111111",  // Тестовая карта Visa, но паттерн плохой
    cvv = "123",
    inn = "123456789012"              // Похоже на реальный ИНН
)
```

## Good Example

```kotlin
// ✅ GOOD: RFC 2606 reserved domains + явно невалидные телефоны
object TestData {
    fun validRequest() = RegisterRequest(
        email = "auto_${System.currentTimeMillis()}@example.com",  // RFC 2606
        phone = "+70000000000",  // Явно тестовый (нули)
        fullName = "Test User ${UUID.randomUUID().toString().take(4)}"
    )
}

// ✅ GOOD: Безопасные домены для email
val SAFE_DOMAINS = listOf(
    "example.com",      // RFC 2606 reserved
    "example.org",
    "example.net",
    "test.example",     // RFC 6761
    "localhost"
)

// ✅ GOOD: Ссылка на документацию платёжной системы
object PaymentTestData {
    // Тестовые карты из документации Stripe/YooKassa
    // См. https://stripe.com/docs/testing#cards
    fun validCard() = PaymentRequest(
        cardNumber = "4242424242424242",  // Stripe test card
        cvv = "000"
    )
}
```

## Safe Patterns

### Email
```kotlin
// ✅ RFC 2606 reserved domains
"user@example.com"
"test@example.org"
"auto_123@test.example"
```

### Phone
```kotlin
// ✅ Явно невалидные номера
"+70000000000"      // Все нули
"+79999999999"      // Все девятки (слишком "круглый")
"+7000${random}"    // Префикс 000 не существует
```

### Names
```kotlin
// ✅ Очевидно тестовые
"Test User"
"Auto Test 12345"
"QA Bot"
```

## What to look for in code review

- Email с @gmail.com, @yandex.ru, @mail.ru, @company.com
- Телефоны с реальными префиксами (+7916, +7903, +7925)
- ФИО в формате "Фамилия Имя Отчество"
- ИНН, СНИЛС, номера паспортов (даже "тестовые")
- Комментарии "мой тестовый аккаунт", "номер Васи"
- Номера карт без ссылки на документацию тестовых карт
