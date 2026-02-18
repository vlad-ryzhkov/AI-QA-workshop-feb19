# Anti-Pattern: HttpClient создаётся inline в теле теста

## Problem

`HttpClient(...)` создаётся прямо внутри `@Test` метода.
Каждый тест управляет своим клиентом — нет единой точки конфигурации.

## Bad Example

```kotlin
// ❌ BAD: inline HttpClient в тесте
@Test
fun `should create user`() {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) { jackson() }
    }
    val response = client.post("$BASE_URL/api/v1/users") {
        setBody(payload)
    }
    assertEquals(201, response.status.value)
}
```

## Good Example

```kotlin
// ✅ GOOD: Request class через apiClient из TestBase
@Test
fun `should create user`() {
    val response = apiClient.execute { CreateUserRequest(TestData.valid()) }
    assertEquals(201, response.code, "User creation should return 201")
}
```

## Why

- Inline client не переиспользует connection pool → медленные тесты
- Нет единой точки для Logging, Auth, Retry конфигурации
- При смене baseUrl нужно обновлять N тестов, не один Config

## Detection

```bash
grep -rn "HttpClient(" src/test/kotlin/
```

## References

- (ref: inline-http-calls.md)
- Общий принцип: `common/no-abstraction-layer.md`
