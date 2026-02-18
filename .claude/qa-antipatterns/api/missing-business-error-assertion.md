# Anti-Pattern: Проверка только HTTP-кода без бизнес-кода ошибки

## Problem

NEG-тест проверяет только HTTP-статус (`400`, `422`), не проверяя `body.code`.
Тест зелёный, но реальная ошибка бизнес-логики (неверный `code`) не обнаружена.

## Bad Example

```kotlin
// ❌ BAD: проверяем только HTTP статус
@Test
fun `returns 400 for invalid email`() {
    val resp = apiClient.execute { RegisterRequest(TestData.invalidEmail()) }
    assertEquals(400, resp.code)
    // Бизнес-код не проверен — любой 400 пройдёт
}
```

## Good Example

```kotlin
// ✅ GOOD: проверяем HTTP статус + бизнес-код ошибки
@Test
fun `returns 400 for invalid email`() {
    val resp = apiClient.execute { RegisterRequest(TestData.invalidEmail()) }
    assertEquals(400, resp.code, "HTTP status mismatch")
    assertEquals("VALIDATION_ERROR", resp.body.code, "error code mismatch")
    assertEquals("email", resp.body.field, "error field mismatch")
}
```

## Why

- HTTP `400` может приходить по многим причинам (auth, schema, rate limit)
- Без `body.code` тест не отличает `VALIDATION_ERROR` от `MISSING_FIELD` или `INVALID_FORMAT`
- Регрессия в бизнес-логике ошибок остаётся незамеченной

## Detection

```bash
grep -n "assertEquals(400\|assertEquals(422\|assertEquals(401" src/test/kotlin/ -r \
  | grep -v "body.code\|body\.error"
```

Результат содержит строки → проверь каждый тест на наличие `body.code` assertion.

## References

- (ref: missing-business-error-assertion.md)
