# No Cleanup Pattern

## Why this is bad

Тесты без очистки данных:
- Засоряют БД тысячами тестовых записей
- Создают flaky тесты (конфликты уникальности)
- Делают невозможным параллельный запуск
- Усложняют отладку на staging/dev окружениях

## Bad Example

```kotlin
// ❌ BAD: Данные остаются в БД навсегда
@Test
fun `user can register`() {
    val payload = RegisterRequest(
        email = "test_${System.currentTimeMillis()}@example.com",
    )

    val response = ApiHelper.apiClient.execute { RegisterRequest(payload) }
    assertEquals(201, response.code, "Registration should succeed")
    // Тест закончился, юзер остался в БД
}
```

## Good Example

```kotlin
// ✅ GOOD: try-finally гарантирует cleanup
@Test
fun `user can register`() {
    var userId: String? = null

    try {
        val response = ApiHelper.apiClient.execute { RegisterRequest(validPayload) }
        assertEquals(201, response.code, "Registration should succeed")
        userId = response.body.userId

        // Assertions...
    } finally {
        userId?.let { ApiHelper.apiClient.execute { DeleteUserRequest(it) } }
    }
}
```

## Рекомендованная стратегия: Cleanup-First

**Cleanup в `@BeforeEach` (не `@AfterEach`)** — рекомендованный подход для integration-тестов.

**Почему Cleanup-First лучше Cleanup-After:**
- При падении теста данные **сохраняются** в БД для отладки
- Следующий запуск **сам очистит** перед собой (идемпотентно)
- `@AfterEach` может не выполниться при crash/timeout JVM

```kotlin
// ✅ RECOMMENDED: Cleanup-First в @BeforeEach
@BeforeEach
fun cleanup() {
    runCatching { ApiHelper.apiClient.execute { DeleteUserByEmailRequest(testEmail) } }
}

@Test
fun `user can register`() {
    val response = ApiHelper.apiClient.execute { RegisterRequest(payload) }
    assertEquals(201, response.code, "Registration should succeed")
}
```

```kotlin
// ✅ GOOD: Cleanup-First inline (для одиночных тестов)
@Test
fun `user can register`() {
    runCatching { ApiHelper.apiClient.execute { DeleteUserByEmailRequest(testEmail) } }

    val response = ApiHelper.apiClient.execute { RegisterRequest(payload) }
    assertEquals(201, response.code, "Registration should succeed")
}
```

**Когда использовать какой подход:**

| Стратегия | Когда |
|-----------|-------|
| **Cleanup-First (`@BeforeEach`)** | Integration-тесты, shared DB, нужна отладка при падении |
| **try-finally** | Тест создаёт уникальный ресурс, который нужно удалить сразу |
| **Cleanup-After (`@AfterEach`)** | Только если Cleanup-First невозможен (нет idempotent DELETE) |

## What to look for in code review

- Отсутствие `finally` блока, `@BeforeEach` cleanup или `@AfterEach`
- "Уникальные префиксы" как единственная стратегия изоляции
- Тесты, которые падают при повторном запуске
- `@AfterEach` вместо `@BeforeEach` cleanup без обоснования
- Cleanup-операции, которые не идемпотентны (падают если ресурс не существует)
