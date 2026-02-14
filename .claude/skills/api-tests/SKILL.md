---
name: api-tests
description: Генерирует Production-Ready API автотесты (Kotlin, Ktor/Kotest) с 4-слойной архитектурой. Используй для создания тестов REST API, покрытия спецификации автотестами или генерации тестов с нуля. Не используй для UI/E2E тестов или мануальных тест-кейсов — для этого /testcases.
allowed-tools: "Read Write Edit Glob Grep Bash(./gradlew*)"
---

# Senior SDET: API Test Automation

Генерация надежных, поддерживаемых автотестов для Kotlin/Ktor стека.

---

## Технологический стек (HARD CONSTRAINTS)

**СТРОГО СОБЛЮДАТЬ. Не использовать альтернативы.**

| Компонент | Технология | Альтернативы ЗАПРЕЩЕНЫ |
|-----------|------------|------------------------|
| HTTP Client | **Ktor Client** (CIO engine) | ❌ Retrofit, OkHttp, Fuel |
| Serialization | **Jackson** (SNAKE_CASE) | ❌ Gson, Moshi, kotlinx.serialization |
| Assertions | **Kotest matchers** | ❌ JUnit assertEquals, AssertJ |
| Async | **Kotlin Coroutines** (runBlocking) | ❌ RxJava, CompletableFuture |
| Test Framework | **JUnit 5** | — |
| Reporting | **Allure** | — |

---

## 4-Слойная Архитектура (ОБЯЗАТЕЛЬНО)

```
feature/
├── models/           # 1. DTO (Request/Response)
│   └── FeatureModels.kt
├── client/           # 2. API Client wrapper
│   └── FeatureApiClient.kt
├── data/             # 3. Test Data Factory (Object Mother)
│   └── FeatureTestData.kt
└── FeatureApiTests.kt  # 4. Tests
```

**ЗАПРЕЩЕНО:**
- Писать всё в одном файле
- HTTP вызовы напрямую в тестах
- Hardcoded JSON в тестах (кроме structural tests)

---

## Слой 1: Models (DTO)

```kotlin
package feature.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

// Request: контролируем контракт (SNAKE_CASE через @JsonNaming на классе)
@JsonNaming(SnakeCaseStrategy::class)
data class FeatureRequest(
    val email: String,
    val phone: String,
    val fullName: String
)

// Response: устойчивость к изменениям backend
@JsonNaming(SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class FeatureResponse(
    val userId: String,
    val status: String
)

// Error: унифицированная структура ошибок
@JsonNaming(SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class ErrorResponse(
    val error: String? = null,
    val message: String? = null,
    val details: Map<String, String>? = null
)
```

---

## Слой 2: API Client (ОБЯЗАТЕЛЬНАЯ ОБЕРТКА)

**Никогда не используй raw HttpClient в тестах.**

```kotlin
package feature.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import feature.models.*

// Обертка ответа (ОБЯЗАТЕЛЬНО)
data class ApiResponse<T>(
    val status: Int,
    val body: T?,
    val error: ErrorResponse?,
    val rawBody: String  // Для отладки
)

class FeatureApiClient(
    private val baseUrl: String = System.getenv("API_BASE_URL") ?: "http://localhost:8080",
    engine: HttpClientEngine? = null  // Для инъекции MockEngine
) {
    private val client = HttpClient(engine ?: CIO.create()) {
        install(ContentNegotiation) {
            jackson {
                propertyNamingStrategy = PropertyNamingStrategies.SNAKE_CASE
            }
        }
    }

    // Типизированный запрос
    suspend fun create(request: FeatureRequest): ApiResponse<FeatureResponse> {
        val response = client.post("$baseUrl/api/v1/feature") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return ApiResponse(
            status = response.status.value,
            body = if (response.status.isSuccess()) response.body() else null,
            error = if (!response.status.isSuccess()) tryParseError(response) else null,
            rawBody = response.bodyAsText()
        )
    }

    // Raw запрос для structural tests (missing fields)
    suspend fun createRaw(json: String): ApiResponse<FeatureResponse> {
        val response = client.post("$baseUrl/api/v1/feature") {
            contentType(ContentType.Application.Json)
            setBody(json)
        }
        return ApiResponse(
            status = response.status.value,
            body = if (response.status.isSuccess()) response.body() else null,
            error = if (!response.status.isSuccess()) tryParseError(response) else null,
            rawBody = response.bodyAsText()
        )
    }

    private suspend fun tryParseError(response: HttpResponse): ErrorResponse? {
        return try {
            response.body<ErrorResponse>()
        } catch (_: Exception) {
            null
        }
    }

    fun close() = client.close()
}
```

---

## Слой 3: Test Data (Object Mother + copy())

**Все вариации через `copy()`. Вложенные объекты для группировки.**

```kotlin
package feature.data

import feature.models.FeatureRequest
import java.util.UUID

object FeatureTestData {

    private fun uniqueId() = UUID.randomUUID().toString().take(8)

    // Base valid request (уникальный при каждом вызове)
    fun validRequest() = FeatureRequest(
        email = "auto_${uniqueId()}@example.com",
        phone = "+7999${System.currentTimeMillis().toString().takeLast(7)}",
        fullName = "Test User"
    )

    // Модификаторы через copy()
    fun withEmail(email: String) = validRequest().copy(email = email)
    fun withPhone(phone: String) = validRequest().copy(phone = phone)
    fun withFullName(name: String) = validRequest().copy(fullName = name)

    // Группировка негативных кейсов
    object InvalidEmail {
        fun withoutAt() = withEmail("invalid-email.com")
        fun empty() = withEmail("")
        fun withDoubleAt() = withEmail("test@@example.com")
        fun sqlInjection() = withEmail("test@x.com'; DROP TABLE users;--")
    }

    object InvalidPassword {
        fun tooShort() = validRequest().copy(password = "Short1!")
        fun exactlyMin() = validRequest().copy(password = "Eight1#A")  // 8 символов
        fun noDigits() = validRequest().copy(password = "NoDigits!@#A")
    }

    object BoundaryValues {
        fun fullNameMin() = withFullName("Li")        // 2 символа
        fun fullNameMax() = withFullName("A".repeat(100))  // 100 символов
    }
}
```

---

## Слой 4: Tests

```kotlin
package feature

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldMatch
import io.qameta.allure.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*

@Epic("Feature Name")
@Feature("Feature API")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeatureApiTests {

    private val apiClient = FeatureApiClient()

    @AfterAll
    fun tearDown() = apiClient.close()

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("201 - успешное создание")
    fun successfulCreate() = runBlocking {
        val response = apiClient.create(FeatureTestData.validRequest())

        response.status shouldBe 201
        response.body shouldNotBe null
        response.body!!.userId shouldMatch Regex("[0-9a-f-]{36}")
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("400 - отсутствует обязательное поле email")
    fun error400WhenEmailMissing() = runBlocking {
        // Structural test: используем raw JSON
        val response = apiClient.createRaw("""{"phone": "+79991234567"}""")
        response.status shouldBe 400
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("400 - невалидный формат email")
    fun error400WhenEmailInvalid() = runBlocking {
        val response = apiClient.create(FeatureTestData.InvalidEmail.withoutAt())
        response.status shouldBe 400
    }
}
```

---

## ⛔ BANNED (Anti-Patterns)

### 1. HTTP напрямую в тестах
```kotlin
// ❌ BANNED
@Test
fun test() {
    val response = client.post("http://...") { ... }
}

// ✅ CORRECT
@Test
fun test() {
    val response = apiClient.create(payload)
}
```

### 2. Map вместо DTO
```kotlin
// ❌ BANNED
val payload = mapOf("email" to "test@test.com")

// ✅ CORRECT
val payload = FeatureTestData.validRequest()
```

### 3. assertEquals вместо Kotest
```kotlin
// ❌ BANNED
assertEquals(201, response.status)

// ✅ CORRECT
response.status shouldBe 201
```

### 4. Thread.sleep()
```kotlin
// ❌ BANNED
Thread.sleep(2000)

// ✅ CORRECT: Polling
apiClient.executeUntil(
    request = { apiClient.getStatus(id) },
    condition = { it.status == "ACTIVE" },
    timeoutMs = 5000
)
```

### 5. Неиспользуемые методы в TestData
```kotlin
// ❌ BANNED: Методы в TestData, которые не вызываются ни в одном тесте

// ✅ CORRECT: Каждый метод в TestData должен использоваться минимум в 1 тесте
```

### 6. Отсутствие Allure аннотаций
```kotlin
// ❌ BANNED
@Test
@DisplayName("Test")
fun test() { }

// ✅ CORRECT
@Test
@Severity(SeverityLevel.CRITICAL)
@DisplayName("400 - ошибка валидации email")
fun test() { }
```

---

## Cross-Skill: Traceability с Manual Tests

**ОБЯЗАТЕЛЬНО:** При наличии мануальных тестов — автоматизируй ВСЕ сценарии.

1. Проверь `src/test/testCases/**/*ManualTests.kt`
2. Для каждого `@DisplayName` в мануальных тестах должен быть соответствующий автотест
3. TestData должен содержать методы для ВСЕХ сценариев
4. **Self-Check:** Нет неиспользуемых методов в TestData

```kotlin
// Связь с мануальным тестом
@Test
@Link("TC-01")  // ID из мануального теста
@DisplayName("[Registration] Успешная регистрация")
fun successfulRegistration() { ... }
```

---

## Coverage Checklist (по порядку)

При генерации тестов покрывай в следующем порядке:

1. **400 - Missing Fields** (structural tests с raw JSON)
2. **400 - Invalid Formats** (email, phone, etc.)
3. **400 - Boundary Values** (min-1, min, max, max+1)
4. **400 - Security** (XSS, SQLi, PII in password)
5. **409 - Conflicts** (duplicates)
6. **429 - Rate Limit**
7. **500 - Server Errors** (если применимо)
8. **201/200 - Success Cases** (happy path + edge cases)

---

## Self-Check (Definition of Done)

- [ ] **4-слойная архитектура:** models/, client/, data/, tests — разделены?
- [ ] **ApiResponse<T>:** Обертка с status/body/error/rawBody?
- [ ] **Kotest:** Используется shouldBe, НЕ assertEquals?
- [ ] **TestData complete:** Для каждого мануального теста есть метод?
- [ ] **TestData used:** Нет неиспользуемых методов в TestData?
- [ ] **Allure:** @Severity и @DisplayName на каждом тесте?
- [ ] **No hardcode:** Уникальные email/phone через UUID/timestamp?

---

## Compilation Gate (ОБЯЗАТЕЛЬНО)

После генерации кода и ПЕРЕД Self-Review:

```bash
./gradlew compileTestKotlin
```

- BUILD SUCCESSFUL → продолжи к Self-Review
- BUILD FAILED → исправь, повтори (max 3 попытки)
- 3x FAIL → STOP, сообщи пользователю

Код который не компилируется НЕ подлежит self-review.

---

## 🔄 Self-Review Protocol (ОБЯЗАТЕЛЬНЫЙ ЭТАП)

**После завершения генерации** выполни повторный анализ своего результата и создай отчёт критики.

### Алгоритм Self-Review

1. **Перечитай** сгенерированный код как Code Reviewer
2. **Проверь** соответствие 4-слойной архитектуре
3. **Найди** нарушения BANNED паттернов
4. **Сверь** с мануальными тестами (если есть)
5. **Сформируй отчёт** `*_self_review.md` рядом с тестами

### Формат отчёта Self-Review

```markdown
# Self-Review: [Feature]ApiTests

## Оценка соответствия стандартам

**Источники правил:**
- `SKILL.md` (api-tests)
- `CLAUDE.md`
- `.claude/qa_agent.md`

## Scorecard

### Архитектура
- 4 слоя (models/client/data/tests): ✅/❌
- ApiResponse<T> wrapper: ✅/❌
- Файлы разделены: ✅/❌

### Стек
- Ktor Client (не OkHttp/Retrofit): ✅/❌
- Jackson SNAKE_CASE: ✅/❌
- Kotest shouldBe (не assertEquals): ✅/❌
- runBlocking (не RxJava): ✅/❌

### Качество
- Allure аннотации: X/Y тестов = NN%
- TestData методов: N
- TestData используется: X/N = NN%
- Hardcode отсутствует: ✅/❌

### Покрытие (vs Manual Tests)
- Мануальных тестов: N
- Автоматизировано: M
- **Coverage: M/N = NN%**

### Итоговый Score: NN%

## Нарушения BANNED паттернов

- [ ] HTTP напрямую в тестах: [где]
- [ ] Map вместо DTO: [где]
- [ ] assertEquals: [где]
- [ ] Thread.sleep(): [где]
- [ ] Неиспользуемые методы TestData: [какие]
- [ ] Без Allure аннотаций: [какие тесты]

## Непокрытые сценарии из Manual Tests

- [ ] [DisplayName из мануального теста без автотеста]

## Рекомендации

1. [Конкретное действие]
```

### Правила Self-Review

- **НЕ ИСПРАВЛЯТЬ** код — только анализ
- **Сверять с Manual Tests** — обязательно, если они есть в `src/test/testCases/`
- **Проверять TestData** — каждый метод должен вызываться
- **Файл рядом** — `RegistrationApiTests.kt` → `RegistrationApiTests_self_review.md`

### Завершение
После создания `*_self_review.md` — напечатай блок `SKILL COMPLETE` (формат в qa_agent.md).
