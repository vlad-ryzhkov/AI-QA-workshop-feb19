# No Sensitive Data Logging

**Applies to:** `/api-tests`, `/spec-audit`

## Why this is bad

Логирование чувствительных данных в тестах:
- Токены и пароли попадают в CI-логи (доступны всей команде)
- Allure-отчёты с секретами доступны по ссылке
- Нарушение compliance (GDPR, PCI DSS)

## Bad Example

```kotlin
// ❌ BAD: Токен в Allure step
@Step("Auth with token: {token}")
fun authenticate(token: String): AuthResponse {
    return apiClient.execute { AuthRequest(token) }
}

// ❌ BAD: Пароль в assertion message
assertEquals(200, response.code, "Auth failed for password=$password")

// ❌ BAD: Полный response body с токенами в println
println("Response: ${response.body}")
```

## Good Example

```kotlin
// ✅ GOOD: Маскированный токен в step
@Step("Auth with token: {maskedToken}")
fun authenticate(token: String): AuthResponse {
    val maskedToken = "${token.take(4)}****"
    return apiClient.execute { AuthRequest(token) }
}

// ✅ GOOD: Assertion без секретов
assertEquals(200, response.code, "Auth should succeed for test user")

// ✅ GOOD: Логируем только структуру, не значения
@Step("Verify response has required fields")
fun verifyResponseStructure(response: ApiResponse<UserResponse>) {
    assertNotNull(response.body.id, "Response should contain user ID")
}
```

## What to look for in code review

- `@Step` с `{token}`, `{password}`, `{secret}` в шаблоне
- `println` / `logger` с response body (может содержать токены)
- Assertion messages с interpolated секретами
- Hardcoded реальные токены/API keys в тестовом коде
