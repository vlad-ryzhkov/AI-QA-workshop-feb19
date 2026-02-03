# Assertion Without Message

## Why this is bad

Assertions без сообщений:
- При падении непонятно, что именно проверялось
- Сложно отлаживать в CI (только stack trace)
- Нужно открывать код чтобы понять причину
- Отчёты Allure становятся бесполезными

## Bad Example

```kotlin
// ❌ BAD: Что упало? Почему?
@Test
fun `user registration flow`() {
    val response = apiClient.register(payload)

    response.status shouldBe 201          // AssertionError: expected 201 but was 400
    response.body.userId shouldNotBe null // Какой userId? Почему null?
    response.body.status shouldBe "PENDING"
}

// В логах CI:
// AssertionError: expected:<201> but was:<400>
// 🤷 Что пошло не так?
```

## Good Example

```kotlin
// ✅ GOOD: Понятные сообщения
@Test
fun `user registration flow`() {
    val response = apiClient.register(payload)

    response.status shouldBe 201 withClue {
        "Registration failed. Response: ${response.body}"
    }

    response.body.userId.shouldNotBeNull().withClue {
        "User ID should be returned after successful registration"
    }

    response.body.status shouldBe "PENDING" withClue {
        "New user should have PENDING status until OTP verification"
    }
}

// ✅ GOOD: Allure step с контекстом
@Test
fun `user registration flow`() {
    step("Register new user") {
        val response = apiClient.register(payload)

        step("Verify HTTP 201 Created") {
            response.status shouldBe 201
        }

        step("Verify user ID is returned") {
            response.body.userId.shouldNotBeNull()
        }
    }
}
```

## What to look for in code review

- `shouldBe`, `assertEquals` без `withClue` / message параметра
- Несколько assertions подряд без контекста
- Отсутствие Allure `step()` в integration тестах
- Assertions на вложенные поля без пояснения структуры
