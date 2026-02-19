# Anti-Pattern: POS-тест без проверки Security Headers

## Problem

POS-тест (2xx) проверяет только бизнес-логику, игнорируя security headers ответа.
Сервер возвращает 201, но заголовки безопасности отсутствуют — уязвимость остаётся незамеченной.

## Bad Example

```kotlin
// ❌ BAD: нет проверки security headers
@Test
@Severity(BLOCKER)
fun `201 successful registration`() {
    val resp = apiClient.execute { RegisterRequest(TestData.valid()) }
    assertEquals(201, resp.code)
    assertNotNull(resp.body.verificationToken)
    // X-Content-Type-Options, HSTS — не проверены
}
```

## Good Example

```kotlin
// ✅ GOOD: проверяем Content-Type + security headers
@Test
@Severity(BLOCKER)
fun `201 successful registration`() {
    val resp = apiClient.execute { RegisterRequest(TestData.valid()) }
    assertEquals(201, resp.code)
    assertNotNull(resp.body.verificationToken)
    assertEquals("application/json", resp.headers["Content-Type"], "Content-Type header")
    assertEquals("nosniff", resp.headers["X-Content-Type-Options"], "X-Content-Type-Options header")
    assertNotNull(resp.headers["Strict-Transport-Security"], "HSTS header must be present")
}
```

## Checklist (POS-тесты POST/PUT/DELETE)

| Header | Expected value |
|--------|----------------|
| `Content-Type` | `application/json` (или согласно spec) |
| `X-Content-Type-Options` | `nosniff` |
| `Strict-Transport-Security` | present (`max-age=...`) |

## Detection

```bash
# POS-тесты без проверки headers
grep -n "assertEquals(201\|assertEquals(200" src/test/kotlin/ -r \
  | grep -v "headers\["
```

## References

- (ref: missing-security-headers.md)
