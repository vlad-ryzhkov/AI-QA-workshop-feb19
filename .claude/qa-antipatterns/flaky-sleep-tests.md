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
    val userId = apiClient.register(validPayload)

    Thread.sleep(2000) // Ждём "достаточно"

    val status = apiClient.getUserStatus(userId)
    status shouldBe "ACTIVE"
}
```

## Good Example

```kotlin
// ✅ GOOD: Polling with timeout
@Test
fun `user status becomes ACTIVE after registration`() {
    val userId = apiClient.register(validPayload)

    eventually(duration = 10.seconds, interval = 500.milliseconds) {
        val status = apiClient.getUserStatus(userId)
        status shouldBe "ACTIVE"
    }
}

// ✅ GOOD: Awaitility (Java/Kotlin)
await()
    .atMost(10, TimeUnit.SECONDS)
    .pollInterval(500, TimeUnit.MILLISECONDS)
    .until { apiClient.getUserStatus(userId) == "ACTIVE" }
```

## What to look for in code review

- `Thread.sleep()`, `delay()`, `TimeUnit.SECONDS.sleep()`
- Магические числа в таймаутах без объяснения
- Комментарии типа "wait for async operation"
- Тесты, которые "иногда падают"