# No Abstraction Layer

## Why this is bad

HTTP-вызовы напрямую в тестах:
- При смене URL нужно править 100+ тестов
- Дублирование кода настройки клиента
- Сложно добавить логирование/retry/auth
- Тесты знают слишком много о реализации API

## Bad Example

```kotlin
// ❌ BAD: Raw HTTP напрямую в каждом тесте
@Test
fun `user can register`() {
    val response = httpClient.post("https://api.example.com/api/v1/users/register") {
        contentType(ContentType.Application.Json)
        header("X-Api-Key", "secret-key")
        setBody(payload)
    }

    assertEquals(201, response.code)
}

@Test
fun `registration fails with invalid email`() {
    // Тот же boilerplate снова...
    val response = httpClient.post("https://api.example.com/api/v1/users/register") {
        contentType(ContentType.Application.Json)
        header("X-Api-Key", "secret-key")
        setBody(invalidPayload)
    }
}
```

## Good Example

```kotlin
// ✅ GOOD: Request class инкапсулирует HTTP
class RegisterRequest(
    request: FeatureRequest
) : ApiRequestBaseJson<FeatureResponse>(FeatureResponse::class.java) {
    init {
        url = Config.baseUrl + Endpoints.REGISTER
        body = request
    }
}

// Тесты чистые и читаемые
@Test
fun `user can register`() {
    val response = ApiHelper.apiClient.execute { RegisterRequest(validPayload) }
    assertEquals(201, response.code, "Registration should succeed with valid payload")
}
```

## What to look for in code review

- Raw HTTP вызовы (`httpClient.post()`, `httpClient.get()`) напрямую в `@Test` методах
- Дублирование URL, headers, contentType
- Отсутствие Request classes extending `ApiRequestBaseJson<T>`
- Хардкод URL в тестах (`"https://..."`)
- Custom `ApiClient`/`ApiResponse` wrappers вместо common-test-libs
