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
    val response = ApiHelper.apiClient.execute { RegisterRequest(payload) }

    assertEquals(201, response.code)           // AssertionError: expected 201 but was 400
    assertNotNull(response.body.userId)        // Какой userId? Почему null?
    assertEquals("PENDING", response.body.status)
}

// В логах CI:
// AssertionError: expected:<201> but was:<400>
// 🤷 Что пошло не так?
```

## Good Example

```kotlin
// ✅ GOOD: assertEquals с message
@Test
fun `user registration flow`() {
    val response = ApiHelper.apiClient.execute { RegisterRequest(payload) }

    assertEquals(201, response.code, "Registration should return 201 for valid payload")
    assertNotNull(response.body.userId, "User ID should be returned after successful registration")
    assertEquals("PENDING", response.body.status, "New user should have PENDING status until OTP verification")
}

// ✅ GOOD: Hamcrest checkAll для множественных проверок
@Test
fun `user registration flow`() {
    val response = ApiHelper.apiClient.execute { RegisterRequest(payload) }

    checkAll {
        assertEquals(201, response.code, "Registration should return 201")
        assertNotNull(response.body.userId, "User ID should be present")
        assertEquals("PENDING", response.body.status, "Status should be PENDING")
    }
}

// ✅ GOOD: Allure step с контекстом
@Test
fun `user registration flow`() {
    step("Register new user") {
        val response = ApiHelper.apiClient.execute { RegisterRequest(payload) }

        step("Verify HTTP 201 Created") {
            assertEquals(201, response.code, "Registration should succeed")
        }

        step("Verify user ID is returned") {
            assertNotNull(response.body.userId, "User ID should be present")
        }
    }
}
```

## What to look for in code review

- `assertEquals`, `assertNotNull` без message параметра
- Несколько assertions подряд без контекста
- Отсутствие Allure `step()` в integration тестах
- Assertions на вложенные поля без пояснения структуры
