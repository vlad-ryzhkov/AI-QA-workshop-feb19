# No Hardcoded Timeouts

**Applies to:** `/api-tests`

## Why this is bad

Захардкоженные таймауты в Awaitility или HTTP-клиенте:
- Ломаются на медленном CI (timeout слишком маленький)
- Тратят время на быстром окружении (timeout слишком большой)
- Невозможно переопределить для разных окружений (dev/staging/prod)

## Bad Example

```kotlin
// ❌ BAD: Magic numbers разбросаны по тестам
await()
    .atMost(5, TimeUnit.SECONDS)
    .pollInterval(500, TimeUnit.MILLISECONDS)
    .until { getStatus(id) == "ACTIVE" }

// ❌ BAD: Разные таймауты в каждом тесте
await().atMost(3, TimeUnit.SECONDS).until { ... }
await().atMost(10, TimeUnit.SECONDS).until { ... }
await().atMost(30, TimeUnit.SECONDS).until { ... }
```

## Good Example

```kotlin
// ✅ GOOD: Таймауты в Config, переиспользуются
object PollingConfig {
    val DEFAULT_TIMEOUT = Duration.ofSeconds(
        System.getenv("POLL_TIMEOUT_SEC")?.toLongOrNull() ?: 10
    )
    val DEFAULT_INTERVAL = Duration.ofSeconds(1)
}

await()
    .atMost(PollingConfig.DEFAULT_TIMEOUT)
    .pollInterval(PollingConfig.DEFAULT_INTERVAL)
    .until { getStatus(id) == "ACTIVE" }
```

## What to look for in code review

- `atMost(N, TimeUnit.SECONDS)` с literal числами в тестах
- Разные таймауты для одинаковых операций в разных тестах
- Отсутствие централизованного polling config
