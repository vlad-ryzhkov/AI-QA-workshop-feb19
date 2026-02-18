# Map Instead of DTO

## Why this is bad

Использование `Map<String, Any>` вместо типизированных моделей:
- Компилятор не ловит опечатки в названиях полей
- Нет автодополнения в IDE
- При рефакторинге API нужно искать строки по всему проекту
- Невозможно понять структуру данных без документации

## Bad Example

```kotlin
// ❌ BAD: Map — компилятор не поможет
@Test
fun `user can register`() {
    val payload = mapOf(
        "email" to "test@example.com",
        "phone" to "+79991234567",
        "pasword" to "Test123!",  // Опечатка! Компилятор молчит
        "full_name" to "Test User"
    )

    val response = client.post("/register") {
        setBody(payload)
    }
}
```

## Good Example

```kotlin
// ✅ GOOD: Data class с аннотациями
@Serializable
data class RegisterRequest(
    val email: String,
    val phone: String,
    val password: String,  // Опечатка = ошибка компиляции
    @SerialName("full_name")
    val fullName: String
)

@Test
fun `user can register`() {
    val payload = RegisterRequest(
        email = "test@example.com",
        phone = "+79991234567",
        password = "Test123!",  // IDE подсказывает
        fullName = "Test User"
    )

    val response = apiClient.register(payload)
}
```

## What to look for in code review

- `mapOf()`, `mutableMapOf()`, `hashMapOf()` для request/response
- `Map<String, Any>`, `Map<String, String>` в сигнатурах
- JSON строки собранные через string interpolation
- Отсутствие моделей в папке `models/` или `dto/`
