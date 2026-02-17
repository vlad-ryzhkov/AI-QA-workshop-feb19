# No Order-Dependent Tests

**Applies to:** `/api-tests`

## Why this is bad

Тесты, зависящие от порядка выполнения:
- JUnit 5 не гарантирует порядок по умолчанию
- Параллельный запуск невозможен
- Один упавший тест каскадно ломает все следующие

## Bad Example

```kotlin
// ❌ BAD: Тесты зависят от порядка — delete не работает без create
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class UserTest : TestBase() {
    companion object {
        lateinit var userId: String
    }

    @Test @Order(1)
    fun `create user`() {
        val response = apiClient.execute { CreateUserRequest(TestData.validCreateBody()) }
        userId = response.body.id
    }

    @Test @Order(2)
    fun `get user`() {
        val response = apiClient.execute { GetUserRequest(userId) }
        assertEquals(200, response.code, "Get user should return 200")
    }

    @Test @Order(3)
    fun `delete user`() {
        val response = apiClient.execute { DeleteUserRequest(userId) }
        assertEquals(204, response.code, "Delete should return 204")
    }
}
```

## Good Example

```kotlin
// ✅ GOOD: Каждый тест полностью автономен
class UserTest : TestBase() {
    @Test
    fun `get user by id`() {
        val userId = UserHelper.createUser(TestData.validCreateBody())

        val response = apiClient.execute { GetUserRequest(userId) }
        assertEquals(200, response.code, "Get user should return 200")
    }

    @Test
    fun `delete user`() {
        val userId = UserHelper.createUser(TestData.validCreateBody())

        val response = apiClient.execute { DeleteUserRequest(userId) }
        assertEquals(204, response.code, "Delete should return 204")
    }
}
```

## What to look for in code review

- `@TestMethodOrder` + `@Order` annotations
- `lateinit var` в companion object, заполняемый в одном тесте
- Тесты, которые падают при запуске поодиночке
- Комментарии типа "run after test X"
