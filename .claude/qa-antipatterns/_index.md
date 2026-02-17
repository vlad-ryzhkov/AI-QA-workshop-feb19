# QA Anti-Patterns Index

> **Lazy Load Protocol:** Читай файл ТОЛЬКО при обнаружении нарушения.
> Превентивная загрузка всех файлов ЗАПРЕЩЕНА (Token Economy).

## Naming Convention

`{problem-name}.md` → описание проблемы и Good Example.

## Available Patterns

| Файл | Проблема |
|------|----------|
| `assertion-without-message.md` | Assertions без message |
| `configure-http-client.md` | HTTP client не настроен |
| `controlled-retries.md` | Нет retry strategy |
| `coroutine-test-return-type.md` | `runBlocking` без явного Unit type |
| `flaky-sleep-tests.md` | `Thread.sleep()` вместо polling |
| `hardcoded-test-data.md` | Hardcoded данные |
| `information-leakage-in-errors.md` | Утечка данных в логах ошибок |
| `junit-test-initialization.md` | `@TestInstance(PER_CLASS)` + field init failures |
| `map-instead-of-dto.md` | `Map<String, Any>` вместо DTO |
| `missing-content-type-validation.md` | Content-Type не валидируется |
| `no-abstraction-layer.md` | Прямые HTTP-вызовы в тестах |
| `no-cleanup-pattern.md` | Нет cleanup после тестов |
| `no-hardcoded-timeouts.md` | Магические числа в таймаутах |
| `no-order-dependent-tests.md` | Тесты зависят друг от друга |
| `no-sensitive-data-logging.md` | PII в логах |
| `no-shared-mutable-state.md` | Shared state между тестами |
| `pii-in-test-data.md` | PII в тестовых данных |
| `pii-literals-in-code.md` | PII литералы в коде |
| `static-object-mother.md` | Static Object Mother |
| `wrap-infrastructure-errors.md` | Unwrapped infrastructure errors |

## Usage (для Auditor)

```bash
# Сканируй файлы через ls
ls .claude/qa-antipatterns/

# Grep в артефакте
grep -r "Thread.sleep" src/test/kotlin/

# Прочитай соответствующий файл ТОЛЬКО при match
cat .claude/qa-antipatterns/flaky-sleep-tests.md
```

## Usage (для SDET)

При обнаружении проблемы в коде:
1. `ls .claude/qa-antipatterns/` — найди файл по имени проблемы
2. Прочитай файл → примени Good Example → процитируй `(ref: filename.md)`
3. Если reference не найден → BLOCKER, не угадывай fix
