# Flaky Sleep Tests

## Why this is bad

`Thread.sleep()` / `delay()` создаёт нестабильные тесты:
- На медленных машинах тест падает (timeout слишком короткий)
- На быстрых машинах тест тратит время впустую
- Невозможно предсказать, сколько времени нужно async-операции

## Bad Example

```kotlin
// ❌ BAD: Magic number, flaky on slow CI
@Test
fun `user status becomes ACTIVE after registration`() {
    val userId = RegistrationHelper.registerUser(FeatureTestData.validRequest())

    Thread.sleep(2000) // Ждём "достаточно"

    val response = ApiHelper.apiClient.execute { GetUserRequest(userId) }
    assertEquals("ACTIVE", response.body.status, "User should become ACTIVE")
}
```

## Good Example

```kotlin
// ✅ GOOD: Awaitility polling with timeout
@Test
fun `user status becomes ACTIVE after registration`() {
    val userId = RegistrationHelper.registerUser(FeatureTestData.validRequest())

    await()
        .atMost(10, TimeUnit.SECONDS)
        .pollInterval(500, TimeUnit.MILLISECONDS)
        .until {
            val response = ApiHelper.apiClient.execute { GetUserRequest(userId) }
            response.body.status == "ACTIVE"
        }
}

// ✅ GOOD: Awaitility с assertion message
await()
    .atMost(10, TimeUnit.SECONDS)
    .pollInterval(1, TimeUnit.SECONDS)
    .untilAsserted {
        val response = ApiHelper.apiClient.execute { GetUserRequest(userId) }
        assertEquals("ACTIVE", response.body.status, "User should become ACTIVE within 10s")
    }
```

## What to look for in code review

- `Thread.sleep()`, `delay()`, `TimeUnit.SECONDS.sleep()`
- Магические числа в таймаутах без объяснения
- Комментарии типа "wait for async operation"
- Тесты, которые "иногда падают"
