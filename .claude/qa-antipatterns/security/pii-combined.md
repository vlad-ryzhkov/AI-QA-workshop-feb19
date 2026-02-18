# Anti-Pattern: PII в тестах и коде

**Applies to:** `/api-tests`, `/testcases`

## Problem

Персональные данные (реальные или «реалистичные») в коде тестов и тест-кейсах:
- Код попадает в Git → утечка PII при публикации репозитория
- Тестировщики копируют данные из тест-кейсов на prod
- Нарушение GDPR / 152-ФЗ при аудите кодовой базы
- «Тестовый аккаунт Васи» — это всё ещё PII

## Bad Example (API Tests)

```kotlin
// ❌ BAD: реальные домены и форматы
object TestData {
    fun validRequest() = RegisterRequest(
        email = "ivan.petrov@gmail.com",     // реальный домен
        phone = "+79161234567",               // реальный формат
        fullName = "Петров Иван Сергеевич"   // похоже на реального человека
    )
}

const val TEST_EMAIL = "vasya.dev@company.com"  // PII коллеги
const val TEST_PHONE = "+79031112233"            // чей-то номер
```

## Bad Example (Test Cases)

```kotlin
// ❌ BAD: реальные данные в шагах тест-кейса
testCase("Регистрация") {
    step("Ввести данные") {
        action = "Ввести email: ivan.petrov@gmail.com, телефон: +79161234567"
    }
}
```

## Good Example (API Tests)

```kotlin
// ✅ GOOD: RFC 2606 + явно невалидные форматы
object TestData {
    fun validRequest() = RegisterRequest(
        email = "auto_${System.currentTimeMillis()}@example.com",  // RFC 2606
        phone = "+70000000000",  // явно тестовый (нули)
        fullName = "Test User ${UUID.randomUUID().toString().take(4)}"
    )
}
```

## Good Example (Test Cases)

```kotlin
// ✅ GOOD: описание класса данных без конкретики
testCase("Регистрация") {
    step("Ввести данные") {
        action = "Ввести валидный email и телефон в формате +7XXXXXXXXXX"
    }
}
```

## Safe Patterns

| Тип | ✅ Безопасно | ❌ Запрещено |
|-----|-------------|-------------|
| Email | `@example.com`, `@example.org` (RFC 2606) | `@gmail.com`, `@yandex.ru`, `@company.com` |
| Phone | `+70000000000`, `+79999999999` | `+7916...`, `+7903...` |
| Name | `Test User`, `QA Bot`, `Auto Test 123` | ФИО в формате «Фамилия Имя Отчество» |
| Card | ссылка на тестовые карты из docs платёжной системы | любые 16-значные числа без ссылки |

## Detection

```bash
grep -rn "@gmail\.com\|@yandex\.ru\|@mail\.ru\|+7916\|+7903\|+7925" src/test/
```

## References

- (ref: pii-combined.md)
