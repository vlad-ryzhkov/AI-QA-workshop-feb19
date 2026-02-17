# Controlled Retries

**Applies to:** `/api-tests`

## Why this is bad

Неконтролируемые retry-логики в тестах:
- Бесконечные retry скрывают реальные баги
- Retry без backoff перегружают тестовый сервер
- Retry всех ошибок маскируют non-retriable failures (400, 403)

## Bad Example

```kotlin
// ❌ BAD: Retry всех ошибок, маскирует баги
fun createUserWithRetry(body: CreateUserRequest): UserResponse {
    repeat(5) {
        try {
            val response = apiClient.execute { CreateUserRequest(body) }
            if (response.code == 201) return response.body
        } catch (e: Exception) { }
    }
    throw RuntimeException("Failed after 5 retries")
}

// ❌ BAD: Awaitility на не-async операцию — скрывает нестабильность
await().atMost(10, TimeUnit.SECONDS).until {
    apiClient.execute { CreateUserRequest(body) }.code == 201
}
```

## Good Example

```kotlin
// ✅ GOOD: Awaitility только для ASYNC операций (ожидание статуса)
await()
    .atMost(10, TimeUnit.SECONDS)
    .pollInterval(1, TimeUnit.SECONDS)
    .until {
        val response = apiClient.execute { GetUserRequest(userId) }
        response.body.status == "ACTIVE"
    }

// ✅ GOOD: Sync операции без retry — если падает, это баг
@Test
fun `create user`() {
    val response = apiClient.execute { CreateUserRequest(TestData.validCreateBody()) }
    assertEquals(201, response.code, "Create user should succeed")
}
```

## What to look for in code review

- `repeat(N)` или `while` loop вокруг API-вызовов
- `catch (e: Exception)` с пустым телом (проглатывание ошибок)
- Awaitility на синхронные CRUD-операции (не async status polling)
- Retry без различия retriable (5xx, timeout) и non-retriable (4xx) ошибок
