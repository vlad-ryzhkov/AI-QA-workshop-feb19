# Anti-Pattern: runBlocking без явного Unit Type

## Problem

`fun test() = runBlocking {}` возвращает Unit вместо void → JUnit 5 пропускает тест.

## Bad Example

```kotlin
@Test
fun `should return 200`() = runBlocking {
    val response = client.get("/api/users")
    response.status shouldBe 200
}
```

**Symptom:** "No tests found for given includes" — JUnit не распознаёт метод как тест.

## Good Example

### Option 1: Explicit Unit Type (Quick Fix)

```kotlin
@Test
fun `should return 200`(): Unit = runBlocking {
    val response = client.get("/api/users")
    assertEquals(200, response.status, "Expected 200 OK")
}
```

### Option 2: Block Body (Preferred)

```kotlin
@Test
fun `should return 200`() {
    runBlocking {
        val response = client.get("/api/users")
        assertEquals(200, response.status, "Expected 200 OK")
    }
}
```

### Option 3: runTest (Best)

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

@Test
fun `should return 200`() = runTest {
    val response = client.get("/api/users")
    assertEquals(200, response.status, "Expected 200 OK")
}
```

## Why

- JUnit 5 требует `void` return type для `@Test` методов
- `= runBlocking {}` без `: Unit` имеет inferred type `Unit`, который не соответствует `void`
- Явный `: Unit` или block body решают проблему
- `runTest` из kotlinx-coroutines-test возвращает `TestResult` который совместим с JUnit

## Detection

```bash
# JUnit debug logs показывают "Executing test class" но не методы
./gradlew test --tests 'package.*' --debug 2>&1 | grep "Executing test class"

# Или проверка через compilation:
./gradlew compileTestKotlin 2>&1 | grep "should be of type 'void'"
```

## References

- JUnit 5 User Guide: Writing Tests
- kotlinx-coroutines-test documentation
- (ref: coroutine-test-return-type.md)
