# No Shared Mutable State

**Applies to:** `/api-tests`

## Why this is bad

Разделяемое mutable состояние между тестами:
- Тесты зависят от порядка выполнения
- Параллельный запуск ломает всё
- Падение одного теста каскадно ломает следующие

## Bad Example

```kotlin
// ❌ BAD: Mutable var в companion object — тесты зависят друг от друга
companion object {
    var createdUserId: String = ""

    @JvmStatic
    @BeforeAll
    fun setup() {
        createdUserId = createUser()
    }
}

@Test
fun `update user`() {
    val response = apiClient.execute { UpdateUserRequest(createdUserId, newData) }
    assertEquals(200, response.code, "Update should succeed")
}

@Test
fun `delete user`() {
    val response = apiClient.execute { DeleteUserRequest(createdUserId) }
    assertEquals(204, response.code, "Delete should succeed")
}
```

## Good Example

```kotlin
// ✅ GOOD: Каждый тест создаёт свои данные
@Test
fun `update user`() {
    val userId = UserHelper.createUser(TestData.validCreateBody())

    val response = apiClient.execute { UpdateUserRequest(userId, TestData.validUpdateBody()) }
    assertEquals(200, response.code, "Update should succeed")
}

@Test
fun `delete user`() {
    val userId = UserHelper.createUser(TestData.validCreateBody())

    val response = apiClient.execute { DeleteUserRequest(userId) }
    assertEquals(204, response.code, "Delete should succeed")
}

// ✅ GOOD: val в companion (immutable) для shared config — допустимо
companion object {
    val token: String = ""

    @JvmStatic
    @BeforeAll
    fun setup() {
        token = AuthHelper.getToken()
    }
}
```

## What to look for in code review

- `var` в `companion object` (кроме lateinit для @BeforeAll)
- Тест A создаёт данные, тест B использует их
- Тесты падают при запуске в другом порядке
- `@TestMethodOrder(MethodOrderer.OrderAnnotation::class)` — признак зависимости
