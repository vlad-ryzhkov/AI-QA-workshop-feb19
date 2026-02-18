---
name: api-tests
description: Generate Production-Ready REST API tests (Kotlin). Config/Requests/Helpers/Tests separation.
allowed-tools: "Read Write Edit Glob Grep Bash(./gradlew*)"
agent: agents/sdet.md
context: fork
---

## 🔒 SYSTEM REQUIREMENTS

Перед выполнением агент ОБЯЗАН загрузить: `.claude/protocols/gardener.md`

---

# SDET: API Automation (Kotlin)

## Protocol
1. **Stack:** HTTP-клиент, JUnit5, Awaitility.
2. **BANNED:** `Thread.sleep`, `delay`, `runBlocking`, custom HTTP wrappers, inline HTTP in tests, manual `@AllureId`, `shouldBe` (use `assertEquals`), comments.
3. **Structure:**
   - `requests/`: DTOs (`@JsonNaming`) + Request-объекты.
   - `helpers/`: `@Step` annotated flows.
   - `tests/`: `@Severity`, `@DisplayName`, sync execution.
4. **Gates:** `compileTestKotlin`, `ktlintCheck`.

## Input Validation (Mandatory Check)

**КРИТИЧНО:** Перед началом генерации выполни 3-фазную проверку `audit/test-plan.md`.

### Фаза 1: Проверка наличия файла

```bash
[ -f audit/test-plan.md ] || echo "BLOCKER"
```

**Если файл отсутствует — BLOCKER:**
```
🚨 BLOCKER: Missing audit/test-plan.md. Request Auditor to run /test-plan first.
```

### Фаза 2: Проверка структуры (защита от пустых файлов)

```bash
grep -q "## 3. Execution List for SDET" audit/test-plan.md || echo "BLOCKER"
```

**Если секция отсутствует — BLOCKER:**
```
🚨 BLOCKER: Malformed test-plan.md (Missing section "3. Execution List for SDET"). Request Auditor to re-generate.
```

### Фаза 3: Проверка наличия таблицы P0

```bash
grep -A 5 "### P0 (Critical)" audit/test-plan.md | grep -q "|" || echo "BLOCKER"
```

**Если нет P0 endpoints — BLOCKER:**
```
🚨 BLOCKER: No P0 endpoints in test-plan.md. Request Auditor to re-run /test-plan or escalate to User.
```

### Если все проверки пройдены:

- Прочитай `audit/test-plan.md`
- Парси таблицу "3. Execution List for SDET" (P0 → P1 → P2)
- Используй Priority Matrix для определения порядка генерации

### Parsing Execution List

1. Читай таблицу "3. Execution List for SDET" из `audit/test-plan.md`
2. Извлекай: Endpoint, HTTP Method, Spec Location, Test Scenarios, Context
3. Генерируй в порядке: P0 → P1 → P2
4. Каждый Test Scenario → отдельный @Test метод

**Если User запрашивает endpoint не из плана:**
```
🚨 BLOCKER: Endpoint {endpoint} missing in test-plan.md. Request Auditor to update plan.
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
0. **Input Check (MANDATORY):**
   - Выполни 3-фазную проверку `audit/test-plan.md` (см. Input Validation выше)
   - Если любая фаза FAIL → BLOCKER и STOP
   - Если все проверки PASS → прочитай test-plan.md и парси Execution List
1. **Discovery:**
   - Read `CLAUDE.md`, `build.gradle.kts`.
   - Если передан аргумент (путь к спецификации) — прочитай его как дополнительный контекст к Execution List.
   - Glob `src/**/*Test*.kt`, `src/**/requests/**/*.kt`.
   - **Manual Tests Linkage:** Glob `src/test/kotlin/manualtests/**/*.kt`. Если найдены — при генерации автотеста для совпадающей фичи добавь KDoc: `/** Manual: {filename} */`. Если папка не найдена — INFO и продолжай.
   - Print Summary: Config/Patterns/Deps status.
2. **Plan & Gen:**
   - USE `audit/test-plan.md` Priority Matrix для порядка endpoint-ов (P0 → P1 → P2)
   - Check `references/api-patterns.md` for specific logic (Auth/CRUD/Page).
   - Order: Validation (400) -> Auth (401) -> Business (200/409) -> Cleanup.
   - **Phase 1:** Stateless (Validation, Auth fail).
   - **Phase 2:** 1-step setup (CRUD, simple flows).
   - **Phase 3:** Multi-step (Helpers, State transitions).
3. **Compile:** `./gradlew compileTestKotlin && ./gradlew ktlintCheck`. Если > 1 неудачных компиляций → ESCALATION (см. ниже)
4. **Verify:** Grep BANNED patterns (см. Post-Check выше). Fix violations → re-compile.

### Escalation (3-Strike Rule)

**Если > 1 неудачных компиляций на одном endpoint:** Активируй **Escalation Protocol** (определён в Agent Prompt). EXIT с `⚠️ SKILL PARTIAL`.

## Architecture
- **Models:** `data class` + `@JsonNaming(SnakeCaseStrategy)`.
- **Requests:** `init { body = ...; headers[...] = ... }`.
- **Helpers:** `object FeatureHelper` with `@Step` methods returning data.
- **Tests:** `extends TestBase`. HTTP-клиент выполняет Request, результат проверяется assertions.

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
├─ Артефакты: src/main/kotlin/**/ (requests, helpers, config) + src/test/kotlin/**/ (tests)
├─ Compilation: PASS
├─ Upstream: audit/test-plan.md (P0: X endpoints, P1: Y endpoints)
├─ Coverage: N/M endpoints из плана (NN%)
├─ Traceability: @Link присутствует в N/M тестах
└─ BANNED check: PASS
```

### Partial (With Blockers)

```
⚠️ SKILL PARTIAL: /api-tests
├─ Артефакты: [{file1}.kt (✅), {file2}.kt (❌)]
├─ Compilation: PARTIAL (X/Y files)
├─ Upstream: audit/test-plan.md (P0: Z endpoints)
├─ Coverage: X/Z endpoints (NN%)
├─ Blockers: 1 UNIMPLEMENTABLE (см. ESCALATION выше)
├─ Traceability: @Link присутствует в X/Y успешных тестах
└─ Status: BLOCKED, требуется решение Orchestrator
```

**Когда использовать SKILL PARTIAL:**
- После 3 неудачных компиляций на одном endpoint (Escalation)
- Техническая блокировка (библиотека не поддерживает feature)
- Неполная спецификация для одного endpoint (остальные покрыты)
