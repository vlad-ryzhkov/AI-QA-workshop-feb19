# QA Anti-Patterns Index

> **Lazy Load Protocol:** Читай файл ТОЛЬКО при обнаружении нарушения.
> Превентивная загрузка всех файлов ЗАПРЕЩЕНА (Token Economy).

## Naming Convention

`{category}/{problem-name}.md` → описание проблемы и Good Example.

## Available Patterns

### common/ — Базовая гигиена кода

| Файл | Проблема |
|------|----------|
| `common/assertion-without-message.md` | Assertions без message |
| `common/hardcoded-test-data.md` | Hardcoded данные |
| `common/no-abstraction-layer.md` | Прямые HTTP-вызовы в тестах |
| `common/static-object-mother.md` | Static Object Mother |
| `common/no-order-dependent-tests.md` | Тесты зависят друг от друга |
| `common/no-cleanup-pattern.md` | Нет cleanup после тестов |

### api/ — Специфика HTTP и протоколов

| Файл | Проблема |
|------|----------|
| `api/map-instead-of-dto.md` | `Map<String, Any>` вместо DTO |
| `api/missing-content-type-validation.md` | Content-Type не валидируется |
| `api/configure-http-client.md` | HTTP client не настроен |
| `api/wrap-infrastructure-errors.md` | Unwrapped infrastructure errors |
| `api/inline-http-calls.md` | `HttpClient(` создаётся inline в тесте |
| `api/missing-security-headers.md` | POS-тест без проверки security headers |
| `api/missing-business-error-assertion.md` | NEG-тест без проверки `body.code` |

### platform/ — Kotlin + JUnit5

| Файл | Проблема |
|------|----------|
| `platform/coroutine-test-return-type.md` | `runBlocking` без явного Unit type |
| `platform/junit-test-initialization.md` | `@TestInstance(PER_CLASS)` + field init failures |
| `platform/flaky-sleep-tests.md` | `Thread.sleep()` / `delay()` вместо polling |
| `platform/no-hardcoded-timeouts.md` | Магические числа в таймаутах |
| `platform/no-shared-mutable-state.md` | Shared state между тестами |
| `platform/controlled-retries.md` | Неконтролируемая retry-логика |

### security/ — Данные и безопасность

| Файл | Проблема |
|------|----------|
| `security/no-sensitive-data-logging.md` | PII в логах |
| `security/information-leakage-in-errors.md` | Утечка данных в логах ошибок |
| `security/pii-combined.md` | PII в тест-данных и коде (api-tests + testcases) |

## Usage (для SDET)

При обнаружении проблемы в коде:
1. Определи категорию: common / api / platform / security
2. Прочитай `.claude/qa-antipatterns/{category}/{name}.md` → примени Good Example → процитируй `(ref: {category}/{name}.md)`
3. Если reference не найден → BLOCKER, не угадывай fix

## Usage (для Auditor)

```bash
# Сканируй по категории
ls .claude/qa-antipatterns/api/

# Grep в артефакте
grep -r "HttpClient(\|Map<String, Any>\|body<" src/test/kotlin/

# Прочитай файл при match
cat .claude/qa-antipatterns/api/inline-http-calls.md
```
