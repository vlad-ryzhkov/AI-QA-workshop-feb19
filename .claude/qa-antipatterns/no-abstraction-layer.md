# No Abstraction Layer

## Why this is bad

HTTP-вызовы напрямую в тестах:
- При смене URL нужно править 100+ тестов
- Дублирование кода настройки клиента
- Сложно добавить логирование/retry/auth
- Тесты знают слишком много о реализации API

## Bad Example

```kotlin
// ❌ BAD: HTTP напрямую в каждом тесте
@Test
fun `user can register`() {
    val response = client.post("https://api.example.com/api/v1/users/register") {
        contentType(ContentType.Application.Json)
        header("X-Api-Key", "secret-key")
        setBody(payload)
    }

    response.status shouldBe HttpStatusCode.Created
}

@Test
fun `registration fails with invalid email`() {
    // Тот же boilerplate снова...
    val response = client.post("https://api.example.com/api/v1/users/register") {
        contentType(ContentType.Application.Json)
        header("X-Api-Key", "secret-key")
        setBody(invalidPayload)
    }
}
```

## Good Example

```kotlin
// ✅ GOOD: ApiClient инкапсулирует HTTP
class RegistrationApiClient(
    private val baseUrl: String = Config.API_URL,
    private val apiKey: String = Config.API_KEY
) {
    private val client = HttpClient { /* config */ }

    suspend fun register(request: RegisterRequest): ApiResponse<RegisterResponse> {
        return client.post("$baseUrl/api/v1/users/register") {
            contentType(ContentType.Application.Json)
            header("X-Api-Key", apiKey)
            setBody(request)
        }.toApiResponse()
    }

    suspend fun deleteUser(userId: String) { /* ... */ }
}

// Тесты чистые и читаемые
@Test
fun `user can register`() {
    val response = apiClient.register(validPayload)
    response.status shouldBe 201
}
```

## What to look for in code review

- `client.post()`, `client.get()` напрямую в `@Test` методах
- Дублирование URL, headers, contentType
- Отсутствие класса `*ApiClient` или `*Service`
- Хардкод URL в тестах (`"https://..."`)
