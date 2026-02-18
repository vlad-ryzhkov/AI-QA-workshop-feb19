# Wrap Infrastructure Errors

**Applies to:** `/api-tests`

## Why this is bad

Инфраструктурные ошибки (сеть, таймаут, DNS) не отличимы от бизнес-ошибок:
- Тест падает с `ConnectException` — непонятно, это баг или infra issue
- CI report показывает 50 FAILED, но все из-за одного упавшего сервиса
- Нет разделения между "тест нашёл баг" и "окружение сломано"

## Bad Example

```kotlin
// ❌ BAD: Infrastructure exception выглядит как test failure
@Test
fun `create user`() {
    val response = apiClient.execute { CreateUserRequest(TestData.validCreateBody()) }
    assertEquals(201, response.code, "Create user should succeed")
}
// При ConnectException: AssertionError → невозможно отличить от бага
```

## Good Example

```kotlin
// ✅ GOOD: Health check в @BeforeAll — infra issues видны сразу
companion object {
    @JvmStatic
    @BeforeAll
    fun verifyServiceAvailable() {
        val healthResponse = runCatching {
            apiClient.execute { HealthCheckRequest() }
        }.getOrNull()

        assertEquals(
            200,
            healthResponse?.code,
            "Service unavailable at ${Config.BASE_URL} — check infrastructure"
        )
    }
}

// ✅ GOOD: Assertion message указывает на возможную infra причину
@Test
fun `create user`() {
    val response = runCatching {
        apiClient.execute { CreateUserRequest(TestData.validCreateBody()) }
    }.getOrElse {
        fail("Request failed with infrastructure error: ${it.javaClass.simpleName} — ${it.message}")
    }
    assertEquals(201, response.code, "Create user should succeed")
}
```

## What to look for in code review

- Отсутствие health check или connectivity check перед тест-сьютом
- `ConnectException`, `SocketTimeoutException` в CI без пояснения
- Все тесты в сьюте падают одинаково (признак infra issue, не bug)
