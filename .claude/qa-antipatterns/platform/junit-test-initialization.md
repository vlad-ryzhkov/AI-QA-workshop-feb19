# Anti-Pattern: @TestInstance(PER_CLASS) + Field Initialization Failures

## Problem

JUnit не может создать экземпляр тестового класса если field initialization падает.

## Bad Example

```kotlin
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MyTests {
    private val client = ApiClient() // Может упасть при init

    @Test
    fun test() { /* ... */ }
}
```

**Symptom:** "No tests found" — JUnit пропускает весь класс.

## Good Example

```kotlin
class MyTests {
    private lateinit var client: ApiClient

    @BeforeEach
    fun setUp() {
        client = ApiClient() // Контролируемая инициализация
    }

    @AfterEach
    fun tearDown() {
        client.close()
    }

    @Test
    fun test() { /* ... */ }
}
```

## Why

- `@BeforeEach` запускается после успешной инициализации класса
- Ошибки setup изолированы от класса
- Cleanup гарантирован через `@AfterEach`

## Detection

```bash
./gradlew test --debug 2>&1 | grep "No tests found"
```

## References

- JUnit 5 User Guide: Test Instance Lifecycle
- (ref: junit-test-initialization.md)
