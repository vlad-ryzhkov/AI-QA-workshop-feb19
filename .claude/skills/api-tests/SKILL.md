---
name: api-tests
description: Генерирует Production-Ready API автотесты на Kotlin (JUnit5, Allure). Используй когда нужно покрыть REST endpoint тестами из test-scenarios.md или спецификации. Не используй для генерации тест-кейсов — для этого /test-cases.
allowed-tools: "Read Write Edit Glob Grep Bash(./gradlew*)"
agent: agents/sdet.md
context: fork
---

## 🔒 SYSTEM REQUIREMENTS

Перед выполнением агент ОБЯЗАН загрузить: `.claude/protocols/gardener.md`

---

# SDET: API Automation (Kotlin)

<purpose>
Генерирует полный набор Kotlin-автотестов для REST API: модели, HTTP-клиент, хелперы, тесты.
Источник сценариев — `audit/test-scenarios.md` (результат /test-cases) или спецификация напрямую.
</purpose>

## Когда использовать

- Есть `audit/test-scenarios.md` с матрицей сценариев — покрыть их автотестами
- Нужно написать тесты для нового endpoint с нуля
- Ревью существующих тестов на соответствие стандартам (`review` arg)

## Protocol
1. **Stack:** HTTP-клиент = Ktor `HttpClient(CIO)` инициализируется в `requests/` слое (не в тестах) через `by lazy(LazyThreadSafetyMode.SYNCHRONIZED)`. JUnit5 + `@ParameterizedTest` (`junit-jupiter-params`), Awaitility, Ktor Logging (`LogLevel.ALL`), JSON Schema Validator, Faker (генерация данных в TestData).
2. **BANNED:** `Thread.sleep`, `delay`, `runBlocking` (используй `runTest`), `HttpClient(` в `*Tests.kt` (inline HTTP в тестах), manual `@AllureId`, `shouldBe` (use `assertEquals`), comments.
3. **Security Headers Rule:** Каждый POS-тест (POST/PUT/DELETE с 2xx) обязан проверять `Content-Type`, `X-Content-Type-Options`, `Strict-Transport-Security` через `assertEquals` на `response.headers`.
4. **Structure:**
   - `requests/`: DTOs (`@JsonNaming`) + Request-объекты.
   - `helpers/`: `@Step` annotated flows.
   - `tests/`: `@Epic` (из названия фичи/пакета), `@Feature` (из названия эндпоинта), `@Severity`, `@DisplayName`. `@AllureId` — **НЕ генерируется**: проставляется вручную или через утилиту после привязки к TMS.
4. **Gates:** `compileTestKotlin`, `ktlintCheck`.

## Input Source Strategy

**Primary Source:** `audit/test-scenarios.md` — результат /test-cases. Каждая строка таблицы → автотест.
**Secondary Source:** Спецификация напрямую — если test-scenarios.md отсутствует.

## Input Validation (Mandatory Check)

**КРИТИЧНО:** Перед началом генерации выполни 2-фазную проверку.

### Фаза 1: Проверка наличия test-scenarios (Primary Source)

```bash
[ -f audit/test-scenarios.md ] || echo "WARNING"
```

**Если файл отсутствует:**
```
⚠️ WARNING: audit/test-scenarios.md не найден. Продолжаю без pre-built сценариев.
```

### Фаза 2: Проверка наличия строк таблицы (защита от пустого файла)

```bash
grep -q "^|" audit/test-scenarios.md || echo "WARNING"
```

**Если строки таблицы не найдены:**
```
⚠️ WARNING: test-scenarios.md существует, но не содержит строк таблицы. Продолжаю с пустой базой.
```

### Если все проверки пройдены:

- Read `audit/test-scenarios.md` — извлеки все строки таблицы (каждая строка = один автотест)
- Прочитай `audit/test-plan.md` (если существует) для приоритизации порядка генерации (P0 → P1 → P2)

### Parsing test-scenarios.md

1. Читай `audit/test-scenarios.md`
2. Для каждой строки таблицы извлекай: ID, Type, Scenario, Input, Expected
3. BVA-значения из колонки Input → переноси в автотест ТОЧНО
4. Порядок генерации: по приоритету из `audit/test-plan.md` (если есть) или строка за строкой

**Если User запрашивает endpoint без сценариев в таблице:**
```
⚠️ WARNING: No scenarios for {endpoint} in audit/test-scenarios.md. Продолжаю без сценариев для этого endpoint.
```

## Verbosity Protocol

**Code first, talk later:** Генерация → Compilation → Post-Check → SKILL COMPLETE. Нет промежуточных explanation.

**Запрещено:**
- "I will now create..." — просто Create
- "The test covers..." — покрытие идёт в SKILL COMPLETE метрику
- "Let me fix..." — просто Fix и Compile
- Explanation после каждого файла — группируй все файлы → один compilation attempt

**Разрешено:**
- Compilation errors — показывай stderr, не описание
- SKILL COMPLETE — метрики (Coverage, Compilation status)

**Post-Check:** Inline (5 строк), проверка против BANNED list и Quality Gates.

**Mandatory Checks:**
```bash
grep -r "Thread.sleep\|delay(\|runBlocking\|shouldBe\|//\|body<\|@AllureId(" src/test/kotlin/
grep -rl "HttpClient(" src/test/kotlin/ | grep "Tests\.kt$"
grep -r "Map<String, Any>" src/test/kotlin/
```
⛔ Любой match → FAIL, применить anti-pattern fix:

| Pattern | ref |
|---------|-----|
| `Thread.sleep` / `delay(` | `platform/flaky-sleep-tests.md` |
| `runBlocking` | `platform/coroutine-test-return-type.md` |
| `shouldBe` | `common/assertion-without-message.md` |
| `HttpClient(` в `*Tests.kt` | `api/inline-http-calls.md` — перенести в `requests/` |
| `body<` | `api/map-instead-of-dto.md` |
| `@AllureId(` | `sdet.md:131` — только `./gradlew assignAllureIds` |
| `Map<String, Any>` | `api/map-instead-of-dto.md` |

**Quality Gates:**
- Каждый мутирующий POS-тест (POST/PUT/DELETE) содержит проверку **Side Effects**: состояние БД (`DB:`), события в очереди или Cache — через вызов Helper-метода.
- Все NEG-тесты проверяют не только HTTP-код, но и **бизнес-код ошибки** (`assertEquals(expectedCode, response.body.code, "error code mismatch")`).
- **No duplication:** логика создания сущностей — только в Helpers; логика данных — только в TestData/FakerService. Inline-строки в тестах запрещены.

## Workflow
0. **Input Check (MANDATORY):**
   - Выполни 2-фазную проверку test-scenarios (см. Input Validation выше)
   - Если любая фаза FAIL → выведи ⚠️ WARNING и продолжи с доступными данными
   - Если все проверки PASS → Read `audit/test-scenarios.md`
1. **Discovery:**
   - Read `CLAUDE.md`, `build.gradle.kts`.
   - Read `audit/test-scenarios.md` (Primary Source) → извлеки все строки таблицы.
   - Glob `src/**/*Test*.kt`, `src/**/requests/**/*.kt` (для контекста уже существующих паттернов).
   - Прочитай `audit/test-plan.md` (если существует) — только для определения приоритетов P0/P1/P2.
   - **Style Analysis:** Glob `src/**/models/**/*.kt`. Если поля уже используют snake_case без `@JsonNaming` → НЕ добавляй `@JsonNaming` в генерируемые модели. Прочитай `src/**/TestBase.kt` — используй те же суперклассы, аннотации уровня класса и import-ы.
   - Print Summary: N сценариев найдено, M endpoint-ов в плане, стиль моделей: [SnakeCase/Native].
2. **Plan & Gen:**
   - **Источник сценариев:** строки таблицы из `audit/test-scenarios.md`.
   - Порядок: по приоритету из test-plan.md (P0 → P1 → P2). Если test-plan.md отсутствует — строка за строкой.
   - Check `references/api-patterns.md` for specific logic (Auth/CRUD/Page).
   - Для каждой строки таблицы генерируй один автотест:
     - Реализуй Input как параметры HTTP-запроса
     - Реализуй Expected как assertions (HTTP status + logic)
     - Перенеси BVA-значения из колонки Input ТОЧНО (граничные значения нельзя округлять или менять)
     - Добавь `@Link(name = "Scenario {ID}", url = "file://audit/test-scenarios.md")` — обязательно
   - **Phase 1:** Stateless (Validation, Auth fail).
   - **Phase 2:** 1-step setup (CRUD, simple flows).
   - **Phase 3:** Multi-step (Helpers, State transitions).
3. **Translation & Grouping:** Применяй маппинг из `references/api-patterns.md#translation-rules`. Группировку NEG/BVA — из `api-patterns.md#grouping-strategy`.
4. **Compile:** `./gradlew compileTestKotlin && ./gradlew ktlintCheck`. Если > 1 неудачных компиляций → ESCALATION (см. ниже)
5. **Verify:** Grep BANNED patterns (см. Post-Check выше). Fix violations → re-compile.

### Escalation (3-Strike Rule)

**Если > 1 неудачных компиляций на одном endpoint:** Активируй **Escalation Protocol** (определён в Agent Prompt). EXIT с `⚠️ SKILL PARTIAL`.

## Review Mode (`review` arg)
1. Read `src/test/**/*.kt`.
2. Check against **Protocol** + `references/api-patterns.md#architecture` + `qa-antipatterns/_index.md`.
3. Report: `⛔ Violation (ref: antipattern)` / `✅ Pass`. DO NOT EDIT.

## References
- Architecture & patterns: `references/api-patterns.md` (Architecture, Translation Rules, Coverage Matrix, Grouping)
- Code examples: `references/examples.md`
- Anti-patterns: `.claude/qa-antipatterns/_index.md` → `platform/`, `api/`, `common/`, `security/`

## Completion Contract

### Success (Full Coverage)

```
✅ SKILL COMPLETE: /api-tests
├─ Артефакты: src/main/kotlin/**/ (requests, helpers, config) + src/test/kotlin/autotests/**/ (tests)
├─ Compilation: PASS
├─ Source: audit/test-scenarios.md (N сценариев)
├─ Context: audit/test-plan.md (P0: X endpoints, P1: Y endpoints) | "нет"
├─ Coverage: N/M сценариев реализовано (NN%)
├─ Traceability: @Link(scenario ID) в N/N тестах (100% обязательно)
└─ BANNED check: PASS
```

### Partial (With Blockers)

```
⚠️ SKILL PARTIAL: /api-tests
├─ Артефакты: [{file1}.kt (✅), {file2}.kt (❌)]
├─ Compilation: PARTIAL (X/Y files)
├─ Source: audit/test-scenarios.md (N сценариев)
├─ Coverage: X/N сценариев реализовано (NN%)
├─ Blockers: 1 UNIMPLEMENTABLE (см. ESCALATION выше)
├─ Traceability: @Link присутствует в X/Y успешных автотестах
└─ Status: BLOCKED, требуется решение Orchestrator
```

**Когда использовать SKILL PARTIAL:**
- После 3 неудачных компиляций на одном endpoint (Escalation)
- Техническая блокировка (библиотека не поддерживает feature)
- Неполная спецификация для одного endpoint (остальные покрыты)
