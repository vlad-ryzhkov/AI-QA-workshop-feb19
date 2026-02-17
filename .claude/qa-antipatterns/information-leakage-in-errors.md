# Information Leakage in Error Responses

**Applies to:** `/api-tests` (автотесты), `/spec-audit` (аудит требований)

## Why this is bad

Утечка внутренней информации через error responses:
- Stack traces раскрывают технологический стек, версии библиотек и структуру кода
- Internal paths раскрывают файловую систему сервера (`/opt/app/src/...`)
- Debug info раскрывает SQL-запросы, имена таблиц, connection strings
- Атакующий использует эту информацию для targeted-атак

## Bad Example

```kotlin
// ❌ BAD: Тест не проверяет отсутствие утечки информации в ошибке
@Test
@DisplayName("[Error] Сервер возвращает 500 при невалидных данных")
fun serverError() {
    val response = ApiHelper.apiClient.execute { CreateRawRequest(TestData.corruptedJson()) }
    assertEquals(500, response.code, "Server should return 500")
    // Тест прошёл, но ответ содержит:
    // {"error": "NullPointerException at com.company.service.UserService.create(UserService.kt:42)"}
}

// ❌ BAD: Тест проверяет наличие stack trace как ожидаемое поведение
@Test
fun serverErrorContainsDetails() {
    val response = ApiHelper.apiClient.execute { CreateRawRequest("""{"invalid": true}""") }
    assertTrue(response.rawBody.contains("Exception"), "Фиксирует утечку как фичу")
}
```

## Good Example

```kotlin
// ✅ GOOD: Тест проверяет что ошибка НЕ содержит внутренних деталей
@Test
@Severity(CRITICAL)
@DisplayName("[Security] 500-ошибка не раскрывает stack trace")
fun serverErrorDoesNotLeakStackTrace() {
    val response = ApiHelper.apiClient.execute { CreateRawRequest("""{"trigger": "server_error"}""") }
    assertEquals(500, response.code, "Server should return 500")

    val body = response.rawBody.orEmpty()
    assertFalse(body.contains("Exception"), "Error response should not contain Exception")
    assertFalse(body.contains("at com."), "Error response should not contain stack trace")
    assertFalse(body.contains(".kt:"), "Error response should not contain source references")
}

// ✅ GOOD: Тест проверяет что ошибка не содержит internal paths
@Test
@Severity(CRITICAL)
@DisplayName("[Security] Error response не содержит внутренних путей")
fun errorDoesNotLeakInternalPaths() {
    val response = ApiHelper.apiClient.execute { CreateRawRequest("""{"trigger": "bad_request"}""") }

    val body = response.rawBody.orEmpty()
    assertFalse(body.contains("/opt/"), "Error should not contain server paths")
    assertFalse(body.contains("/home/"), "Error should not contain home directory paths")
    assertFalse(body.contains("src/main/"), "Error should not contain source paths")
}

// ✅ GOOD: Тест проверяет формат generic error response
@Test
@Severity(NORMAL)
@DisplayName("[Error] 500-ошибка возвращает generic сообщение")
fun serverErrorReturnsGenericMessage() {
    val response = ApiHelper.apiClient.execute { CreateRawRequest("""{"trigger": "server_error"}""") }
    assertEquals(500, response.code, "Server should return 500")
    assertEquals("Internal Server Error", response.body.message, "Error should have generic message")
    assertNull(response.body.details, "Error should not expose details to client")
}
```

## What to look for in review

- Тесты на 4xx/5xx ошибки не проверяют содержимое error body на утечки
- Error response содержит слова: `Exception`, `Error at`, `stack`, `trace`
- Error response содержит пути файловой системы (`/opt/`, `/home/`, `/var/`, `src/`)
- Error response содержит имена классов (`com.company.`, `io.ktor.`)
- Error response содержит SQL-фрагменты (`SELECT`, `INSERT`, `table`)
- Error response содержит версии технологий

## Grep-сигнатуры для автоматического обнаружения

```
# В error response assertions — ищем тесты которые НЕ проверяют утечки
pattern: "assertEquals(500"  # → проверь что рядом есть assertion на body
pattern: "response.error"    # → проверь что нет фиксации утечек как ожидаемого поведения
```
