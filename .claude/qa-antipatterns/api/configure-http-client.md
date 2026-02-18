# Configure HTTP Client

**Applies to:** `/api-tests`

## Why this is bad

Дефолтная конфигурация HTTP-клиента в тестах:
- Дефолтный timeout (бесконечный или слишком большой) вешает CI
- Redirect following скрывает реальные проблемы (301/302)
- Отсутствие connection pool лимитов приводит к resource exhaustion

## Bad Example

```kotlin
// ❌ BAD: Дефолтный клиент без таймаутов
object ApiHelper {
    val apiClient = ApiClient(Config.BASE_URL)
}

// ❌ BAD: Таймаут задан в каждом тесте по-разному
@Test
fun `slow endpoint`() {
    apiClient.setReadTimeout(30000)
    val response = apiClient.execute { SlowRequest() }
    apiClient.setReadTimeout(5000)
}
```

## Good Example

```kotlin
// ✅ GOOD: Централизованная конфигурация в Config
object ApiHelper {
    val apiClient = ApiClient(Config.BASE_URL).apply {
        setConnectTimeout(Config.CONNECT_TIMEOUT_MS)
        setReadTimeout(Config.READ_TIMEOUT_MS)
        setFollowRedirects(false)
    }
}

object Config {
    val BASE_URL: String = System.getenv("BASE_URL") ?: "http://localhost:8080"
    const val CONNECT_TIMEOUT_MS = 5_000
    const val READ_TIMEOUT_MS = 10_000
}
```

## What to look for in code review

- `ApiClient()` без явных таймаутов
- `setReadTimeout` / `setConnectTimeout` в теле тестов (а не в config)
- Разные таймауты в разных тестах для одного сервиса
- `followRedirects = true` (скрывает redirect-баги)
