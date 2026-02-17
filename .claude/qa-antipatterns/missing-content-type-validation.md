# Missing Content-Type Validation

**Applies to:** `/api-tests`

## Why this is bad

Тесты не проверяют Content-Type ответа:
- Сервер может вернуть HTML вместо JSON (ошибка reverse proxy)
- Десериализация молча парсит мусор в null-поля
- Баг обнаруживается только в production при интеграции

## Bad Example

```kotlin
// ❌ BAD: Проверяем только status code, Content-Type игнорируется
@Test
fun `get user returns JSON`() {
    val response = apiClient.execute { GetUserRequest(userId) }
    assertEquals(200, response.code, "Get user should return 200")
    assertNotNull(response.body.id, "User should have ID")
}
```

## Good Example

```kotlin
// ✅ GOOD: Проверяем Content-Type для критичных endpoints
@Test
@Severity(NORMAL)
@DisplayName("[Headers] GET user возвращает application/json")
fun getUserReturnsJsonContentType() {
    val response = apiClient.execute { GetUserRequest(userId) }
    assertEquals(200, response.code, "Get user should return 200")

    val contentType = response.headers["Content-Type"]?.firstOrNull().orEmpty()
    assertTrue(
        contentType.contains("application/json"),
        "Content-Type should be application/json, got: $contentType"
    )
}

// ✅ GOOD: Cross-cutting тест на Content-Type для error responses
@Test
@Severity(NORMAL)
@DisplayName("[Headers] Error response возвращает application/json")
fun errorResponseReturnsJsonContentType() {
    val response = apiClient.execute { CreateRawRequest("""{"invalid": true}""") }

    val contentType = response.headers["Content-Type"]?.firstOrNull().orEmpty()
    assertTrue(
        contentType.contains("application/json"),
        "Error Content-Type should be application/json, got: $contentType"
    )
}
```

## What to look for in code review

- Ни один тест не проверяет `Content-Type` header
- Десериализация ответа без проверки, что ответ действительно JSON
- Error responses (4xx/5xx) не проверяются на Content-Type
