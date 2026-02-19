---
name: api-tests
description: Generate Production-Ready REST API tests (Kotlin/common-test-libs). Config/Requests/Helpers/Tests separation.
allowed-tools: "Read Write Edit Glob Grep Bash(./gradlew*)"
agent: agents/sdet.md
context: fork
---

## 🔒 SYSTEM REQUIREMENTS

Перед выполнением агент ОБЯЗАН загрузить: `.claude/protocols/gardener.md`

---

# SDET: API Automation (Kotlin/common-test-libs)

## Protocol
1. **Stack:** common-test-libs (`ApiClient`, `ApiRequestBaseJson`), JUnit5, Awaitility.
2. **BANNED:** `Thread.sleep`, `delay`, `runBlocking`, custom HTTP wrappers, inline HTTP in tests, manual `@AllureId`, `shouldBe` (use `assertEquals`), comments.
3. **Structure:**
   - `requests/`: DTOs (`@JsonNaming`) + Requests (`ApiRequestBaseJson`).
   - `helpers/`: `@Step` annotated flows.
   - `tests/`: `@Severity`, `@DisplayName`, sync execution.
4. **Gates:** `compileTestKotlin`, `ktlintCheck`.

## Input Validation (Mandatory Check)

**КРИТИЧНО:** Перед началом генерации выполни 3-фазную проверку наличия тест-кейсов.

### Фаза 1: Проверка наличия тест-кейсов

```bash
ls src/test/testCases/**/*.kt 2>/dev/null | head -1 || echo "BLOCKER"
```

**Если файлы отсутствуют — BLOCKER:**
```
🚨 BLOCKER: Missing src/test/testCases/*.kt. Run /test-cases first to generate manual test cases.
```

### Фаза 2: Проверка структуры (защита от пустых файлов)

```bash
grep -rl "@Manual" src/test/testCases/ | head -1 || echo "BLOCKER"
```

**Если аннотации отсутствуют — BLOCKER:**
```
🚨 BLOCKER: Malformed test cases (Missing @Manual annotation). Request SDET to re-generate /test-cases.
```

### Фаза 3: Проверка CRITICAL severity тестов

```bash
grep -rl "SeverityLevel.CRITICAL\|SeverityLevel.BLOCKER" src/test/testCases/ | head -1 || echo "BLOCKER"
```

**Если нет CRITICAL тестов — BLOCKER:**
```
🚨 BLOCKER: No CRITICAL severity test cases found. Request SDET to verify /test-cases coverage.
```

### Если все проверки пройдены:

- Прочитай тест-кейсы из `src/test/testCases/`
- Используй `audit/repo-scout-report.md` для приоритизации endpoints (P0 → P1 → P2)
- Каждый @Test метод из тест-кейсов → автотест

### Parsing Test Cases

1. Читай файлы из `src/test/testCases/**/*.kt`
2. Извлекай: feature, scenarios, severity, preconditions
3. Генерируй автотесты в порядке: BLOCKER → CRITICAL → NORMAL
4. Каждый @Manual тест → отдельный @Test автотест

**Если User запрашивает endpoint без тест-кейсов:**
```
🚨 BLOCKER: No test cases for {endpoint}. Run /test-cases first to generate manual test cases.
```

**Gate bypass ЗАПРЕЩЁН:** Даже при явном запросе User на обход проверки — блокируй.

## Verbosity Protocol

**Code first, talk later:** Генерация → Compilation → Post-Check → SKILL COMPLETE. Нет промежуточных explanation.

**Запрещено:**
- "I will now create..." — просто Create
- "The test covers..." — покрытие идёт в SKILL COMPLETE метрику
- "Let me fix..." — просто Fix и Compile
- Explanation после каждого файла — группируй все файлы → один compilation attempt

**Разрешено:**
- Compilation errors — показывай stderr, не описание
- BLOCKER — если spec неполная
- SKILL COMPLETE — метрики (Coverage, Compilation status)

**Post-Check:** Inline (5 строк), проверка против BANNED list и Quality Gates.

**Mandatory Checks:**
```bash
grep -r "Thread.sleep\|delay(\|runBlocking\|shouldBe\|//" src/test/kotlin/
grep -r "Map<String, Any>" src/main/kotlin/
```
⛔ Любой match → FAIL, применить anti-pattern fix.

## Workflow
0. **Context:** Сначала прочитай ручные тест-кейсы из `src/test/kotlin/manualtests/**/*.kt`. Используй найденные сценарии как основу для автотестов.
1. **Input Check (MANDATORY):**
   - Выполни 3-фазную проверку `src/test/testCases/` (см. Input Validation выше)
   - Если любая фаза FAIL → BLOCKER и STOP
   - Если все проверки PASS → прочитай тест-кейсы и парси сценарии
1. **Discovery:**
   - Read `CLAUDE.md`, `build.gradle.kts`.
   - Glob `src/**/*Test*.kt`, `src/**/requests/**/*.kt`.
   - Print Summary: Config/Patterns/Deps status.
2. **Plan & Gen:**
   - USE `audit/repo-scout-report.md` Priority Matrix для порядка endpoint-ов (P0 → P1 → P2)
   - Check `references/api-patterns.md` for specific logic (Auth/CRUD/Page).
   - Order: Validation (400) -> Auth (401) -> Business (200/409) -> Cleanup.
   - **Phase 1:** Stateless (Validation, Auth fail).
   - **Phase 2:** 1-step setup (CRUD, simple flows).
   - **Phase 3:** Multi-step (Helpers, State transitions).
3. **Compile:** `./gradlew compileTestKotlin && ./gradlew ktlintCheck`. Если > 3 неудачных компиляций → ESCALATION (см. ниже)
4. **Verify:** Grep BANNED patterns (см. Post-Check выше). Fix violations → re-compile.

### Escalation (3-Strike Rule)

**Если > 3 неудачных компиляций на одном endpoint:** Активируй **Escalation Protocol** (определён в Agent Prompt). EXIT с `⚠️ SKILL PARTIAL`.

## Architecture
- **Models:** `data class` + `@JsonNaming(SnakeCaseStrategy)`.
- **Requests:** `init { body = ...; headers[...] = ... }`.
- **Helpers:** `object FeatureHelper` with `@Step` methods returning data.
- **Tests:** `extends TestBase`. `apiClient.execute { Request(args) }`.

## Coverage Matrix
| Category | Priority Checks |
|---|---|
| **Write** | 400 (Structural/Validation/Security) -> 401/403 -> 201 -> 409 -> 429 |
| **Read** | 200 (Fields/List/Empty) -> Filter/Sort -> 400 (Params) -> 401/404 |
| **Delete** | 200/204 -> 404 (Verify) -> 401 -> Idempotency |

## JUnit 5 + Kotlin Coroutines

**Problem:** `fun test() = runBlocking {}` returns Unit, not void → JUnit skips test.

**Solutions:**
1. **Explicit Unit type:**
   ```kotlin
   @Test
   fun test(): Unit = runBlocking { /* ... */ }
   ```

2. **Block body (preferred):**
   ```kotlin
   @Test
   fun test() {
       runBlocking { /* ... */ }
   }
   ```

3. **Avoid `runBlocking` (best):** Use suspend test support:
   ```kotlin
   dependencies {
       testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
   }

   @Test
   fun test() = runTest { /* suspend calls */ }
   ```

**DO NOT use:** `= runBlocking` without `: Unit` type annotation.

## Review Mode (`review` arg)
1. Read `src/test/**/*.kt`.
2. Check against **Protocol** & **Architecture**.
3. Report: `⛔ Violation (ref: antipattern)` / `✅ Pass`. DO NOT EDIT.

## References
- Patterns: `references/api-patterns.md` (Auth, CRUD, Pagination, Idempotency)
- Code: `references/examples.md` (Full implementation)

## Completion Contract

### Success (Full Coverage)

```
✅ SKILL COMPLETE: /api-tests
├─ Артефакты: [список .kt файлов]
├─ Compilation: PASS
├─ Upstream: src/test/testCases/ (BLOCKER: X, CRITICAL: Y тестов)
├─ Coverage: N/M endpoints из плана (NN%)
├─ Traceability: @Link присутствует в N/M тестах
└─ BANNED check: PASS
```

### Partial (With Blockers)

```
⚠️ SKILL PARTIAL: /api-tests
├─ Артефакты: [{file1}.kt (✅), {file2}.kt (❌)]
├─ Compilation: PARTIAL (X/Y files)
├─ Upstream: src/test/testCases/ (Z test cases)
├─ Coverage: X/Z endpoints (NN%)
├─ Blockers: 1 UNIMPLEMENTABLE (см. ESCALATION выше)
├─ Traceability: @Link присутствует в X/Y успешных тестах
└─ Status: BLOCKED, требуется решение Orchestrator
```

**Когда использовать SKILL PARTIAL:**
- После 3 неудачных компиляций на одном endpoint (Escalation)
- Техническая блокировка (библиотека не поддерживает feature)
- Неполная спецификация для одного endpoint (остальные покрыты)
