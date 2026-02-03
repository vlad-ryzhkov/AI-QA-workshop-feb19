# Запрос к ИИ (Gemini 3 "fast"): подготовь АПИ тесты для требований registration_api_v2.md

---

# Ответ ИИ (без контекста проекта):
Для написания API тестов на Kotlin стандартным выбором является связка RestAssured + JUnit 5. Этот стек позволяет писать читаемые и выразительные тесты.

Ниже представлен пример тестового набора, покрывающего основные сценарии вашей спецификации.

## Настройка зависимостей (Gradle)
```kotlin
testImplementation("io.rest-assured:rest-assured:5.3.0")
testImplementation("io.rest-assured:kotlin-extensions:5.3.0")
testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
```

## Реализация тестов
```kotlin
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRegistrationTests {

    @BeforeAll
    fun setup() {
        RestAssured.baseURI = "https://your-api-url.com" // ⚠️ HARDCODE
        RestAssured.basePath = "/api/v1"
    }

    @Test
    @DisplayName("Успешная регистрация: Статус PENDING и маскированный телефон")
    fun successRegistrationTest() {
        // ⚠️ MAP INSTEAD OF DTO
        val payload = mapOf(
            "email" to "tester_${System.currentTimeMillis()}@example.com",
            "phone" to "+79991234567",
            "password" to "Strong#Pass99",
            "full_name" to "Alex Kid"
        )

        given()
            .contentType(ContentType.JSON)
            .body(payload)
        .`when`()
            .post("/users/register")
        .then()
            .statusCode(201)
            .body("user_id", notNullValue())
            .body("status", equalTo("PENDING"))
    }

    @Nested
    @DisplayName("Валидация пароля (Security Rules)")
    inner class PasswordValidationTests {

        @Test
        @DisplayName("Ошибка: Пароль содержит части email")
        fun errorPasswordContainsEmailPart() {
             // ⚠️ DUPLICATED PAYLOAD CREATION
            val payload = mapOf(
                "email" to "vladimir@work.com",
                "phone" to "+79001112233",
                "password" to "Vlad123#Secure",
                "full_name" to "Other Name"
            )
            postAndExpect400(payload)
        }
    }

    // ... (остальные тесты пропущены для краткости)

    private fun postAndExpect400(body: Map<String, Any>) {
        given()
            .contentType(ContentType.JSON)
            .body(body)
        .`when`()
            .post("/users/register")
        .then()
            .statusCode(400)
    }
}
```
---------------------------------------------------------
## Пример ошибок исходя контекста проекта

> **Покрытие:** Реализовано **2 теста** из 14 тест-кейсов (14%).
> Для сравнения: в [TestCases.md](../../testCases/rawResults/RawTestCases.md) описано 14 сценариев (TC-01 — TC-14).
> ИИ написал только `successRegistrationTest` и `errorPasswordContainsEmailPart`, остальные "пропущены для краткости".

### 1. Map вместо DTO
`Map<String, Any>` — компилятор не поймает переименование `full_name` → `fullName`.
**Решение:** Использовать `data class RegisterRequest`.

### 2. Нет абстракции
`given().post("/users/register")` напрямую в тесте — при смене на `/v2/` править все тесты.
**Решение:** Паттерн `ApiClient` с методом `registerUser()`.

### 3. Нет cleanup
"Уникальные префиксы" = тысячи мусорных юзеров в базе.
**Решение:** `finally { apiClient.deleteUser(id) }`.
