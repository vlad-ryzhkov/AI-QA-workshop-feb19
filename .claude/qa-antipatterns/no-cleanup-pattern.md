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
        // ...
    )

    val response = apiClient.register(payload)
    response.status shouldBe 201
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
        val response = apiClient.register(validPayload)
        response.status shouldBe 201
        userId = response.body.userId

        // Assertions...
    } finally {
        userId?.let { apiClient.deleteUser(it) }
    }
}

// ✅ GOOD: Cleanup-First (для integration тестов)
@Test
fun `user can register`() {
    // 1. Cleanup FIRST (идемпотентно)
    apiClient.deleteUserByEmail(testEmail).runCatching { }

    // 2. Test
    val response = apiClient.register(payload)
    response.status shouldBe 201

    // 3. Данные остаются для отладки если тест упал
}
```

## What to look for in code review

- Отсутствие `finally` блока или `@AfterEach`
- "Уникальные префиксы" как единственная стратегия изоляции
- Тесты, которые падают при повторном запуске
- Комментарии "cleanup manually" или "run once"
