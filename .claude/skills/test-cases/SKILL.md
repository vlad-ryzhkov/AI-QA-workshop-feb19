---
name: test-cases
description: Генерирует исчерпывающую матрицу тестовых сценариев (Markdown) напрямую из спецификаций API. Используй, когда нужно создать полное покрытие (regression coverage), найти Edge-кейсы или подготовить строгое ТЗ для автотестов. Не используй для генерации кода автотестов — для этого /api-tests.
allowed-tools: "Read Write Edit Glob Grep"
agent: agents/sdet.md
context: fork
---

## Когда использовать

- Перед написанием автотестов — создать baseline тест-кейсов
- После `/spec-audit` — покрыть все найденные endpoints
- Для regression coverage нового API или изменённых endpoints
- Как ТЗ для ручного тестирования

## 🔒 SYSTEM REQUIREMENTS

Перед выполнением агент ОБЯЗАН загрузить: `.claude/protocols/gardener.md`

---

# Test Scenario Designer (Full Coverage)

## Input Strategy (Auto-Discovery)

1. **Source:** Ищи спецификации API в репозитории:
   - `*.yaml`, `*.json` (Swagger/OpenAPI)
   - `*.proto` (gRPC)
   - `audit/spec-audit-report.md` (если есть)
   - **Игнорируй** `audit/test-plan.md` (работаем напрямую со спецификацией).

2. **Scope:** **ALL ENDPOINTS (100% Coverage).**
   - Не фильтруй по важности. Тестируй всё: Auth, Business Logic, Dictionaries, Settings.
   - Каждый найденный метод (GET/POST/PUT/DELETE) должен иметь набор сценариев.

## Protocol

1. **Format:** Markdown Table (Strict Structure). **Никакого кода (Kotlin/Java).**
2. **Data Strategy:** Абстрактные плейсхолдеры (`{UNIQUE_EMAIL}`, `{UUID}`, `{MAX_INT+1}`, `{PAST_DATE}`).
3. **Spec Exclusions (PRIORITY — читать ДО генерации):**
   Перед генерацией явно ищи в спецификации директивы исключений на двух уровнях:

   **Уровень 1 — тип целиком (`EXCLUDED_TYPES`):**
   Маркеры на весь тип: `no security testing`, `BVA not required`, `skip NEG`, `SEC: out of scope`.
   - `EXCLUDED_TYPES = [SEC, BVA, ...]` — весь тип пропускается для всех эндпоинтов.

   **Уровень 2 — конкретные сценарии (`EXCLUDED_SCENARIOS`):**
   Маркеры на конкретные проверки: `handled by ORM`, `delegated to Middleware`, `covered by library (Zod/Pydantic)`, `not tested`, `N/A`, явные примеры кейсов которые спека выводит за скоуп.
   - `EXCLUDED_SCENARIOS` — список паттернов сценариев, которые не генерировать. Примеры:
     - `SEC:injection` — SQLi, XSS, SSTI (ORM защищает, инфраструктурное покрытие)
     - `NEG:missing_field` — дубли "отсутствующее поле" если валидатор (Zod/Pydantic) обрабатывает их однотипно → оставить **один** NEG на каждый тип ошибки, не на каждое поле
     - `BVA:{field}` — BVA для конкретного поля (например `BVA:password`), если длина делегирована Middleware
     - `POS:encoding_variants` — лишние Happy Path с Unicode/дефисами поверх базового POS если стандартная библиотека валидации покрывает их

   - Если исключения не найдены — `EXCLUDED_TYPES = []`, `EXCLUDED_SCENARIOS = []`, применяй полный Coverage Matrix.

4. **Coverage Matrix (The Grid):**
   Для **КАЖДОГО** эндпоинта примени слои проверок **КРОМЕ типов из `EXCLUDED_TYPES`** и **КРОМЕ паттернов из `EXCLUDED_SCENARIOS`**:
   - **POS (Positive):** Happy Path (Min data & Max data).
   - **NEG (Negative):** Validation (Null, Empty, Wrong Type, Malformed JSON), State Conflicts (Action on PENDING/BLOCKED entity).
   - **BVA (Boundaries):** Числа (Min-1/Min, Max/Max+1), Строки (Len-1, Len+1), Logic Boundaries (e.g. >3 chars → 3 (Pass), 4 (Fail)), Массивы (Empty, Max items).
   - **SEC (Security):** No Token, Invalid Token, Injection payloads (`' OR 1=1`), IDOR (чужой ID).
   - **L10N (Localization) [Условный]:** Применяй если endpoint принимает текстовые поля пользователя (name, address, description, comment). Проверить POS с: кириллицей (`{CYR_NAME}`), арабским/RTL (`{AR_NAME}`), CJK (`{ZH_NAME}`), emoji (`{EMOJI_STRING}`), спецсимволами (`{SPECIAL_CHARS}`: `& < > " '`). Ожидаемый результат: данные сохранены и возвращены без искажений (UTF-8 round-trip). **Маркер исключения в спеце:** `L10N: out of scope`.
   - **IDEM (Idempotency) [ОБЯЗАТЕЛЬНЫЙ для POST/PUT]:** Применяй для **каждого** POST/PUT endpoint без исключений. Два сценария: (1) повторный запрос с идентичными данными → должен вернуть **тот же результат** без создания дубля (`200 OK` или `201 Created` по контракту); (2) повторный запрос после успешного создания уникальной сущности → `409 Conflict` (если бизнес-логика запрещает дубли). **Маркер исключения в спеце:** `IDEM: not required`.

## Expected Result Engineering

Правила для колонки `Expected Result (HTTP + Logic)` — обязательны для всех сценариев:

### 1. Contract-First (Schema Validation)
Для **POS**-сценариев мутирующих и read-операций `Expected Result` ОБЯЗАН содержать ссылку на JSON-схему:
- Формат: `Contract Match: {field}({type}), {field}({type})`
- Типы: `string`, `UUID`, `ISO8601`, `boolean`, `integer`, `array`
- Пример: `201 Created. Contract Match: verification_token(string/UUID), expires_at(ISO8601), status(string)`
- Профит: один тест автоматически ловит переименование поля, смену типа или удаление обязательного ключа.

### 2. State Verification (Side Effects)
Для **любого** мутирующего запроса (POST/PUT/PATCH/DELETE) `Expected Result` ОБЯЗАН содержать проверку состояния системы после ответа сервера:
- DB: `DB: users.status = 'PENDING' WHERE email = {EMAIL}`
- Queue: `Event published: user.registered (topic: registrations)`
- Cache: `Cache invalidated: user:{UUID}`
- Если внешнее состояние не проверяется — явно указать `State: N/A (read-only)`.
- Пример: `201 Created. DB: users.status='PENDING', verification_token NOT NULL`

### 3. Headers & Security
Для **POS Happy Path** каждого endpoint добавить строку проверки заголовков (`Type: HEADERS`):
- `Content-Type: application/json; charset=utf-8` — обязателен
- Заголовки безопасности: `X-Content-Type-Options: nosniff`
- Пример строки таблицы: `| REG-01h | HEADERS | Response headers | — | Content-Type: application/json; charset=utf-8. X-Content-Type-Options: nosniff |`

### 4. Audit-Ready (NEG Specificity)
Для **NEG**-сценариев `Expected Result` ОБЯЗАН содержать `code` из тела ответа, а не только HTTP-статус:
- Формат: `{HTTP_CODE} + body.code: '{ERROR_CODE}'`
- Пример: `400 Bad Request + body.code: 'VALIDATION_ERROR'` (❌ просто `400 Bad Request`)

### 5. Cleanup / Teardown
Каждый **POS**-сценарий, создающий данные, ОБЯЗАН завершаться шагом очистки:
- Добавляй в `Expected Result`: `Cleanup: DELETE /users/{UUID}` или `Cleanup: DB rollback`
- Цель: повторный прогон тестов не должен давать конфликт уникальности (duplicate email, duplicate phone).
- Если cleanup не нужен (read-only) — явно указать `Cleanup: N/A`.

## Constraints (Нарушение = REJECT)

| Категория | Правило | Нарушение → Правильно |
|-----------|---------|----------------------|
| **Format** | NO CODE | `@Test fun...` (❌) → `| ID | Scenario |` (✅) |
| **Data** | Плейсхолдеры | `test@test.com` (❌) → `{UNIQUE_EMAIL}` (✅) |
| **Privacy** | NO PII | `ivan.petrov` (❌) → `user_{uuid}` (✅) |
| **Privacy** | RFC 2606 Only | `@gmail.com` (❌) → `@example.com` (✅) |
| **Expectations** | Specificity | "Error" (❌) → "400 Bad Request + Code 'INVALID_ID'" (✅) |
| **Expectations** | NO Vague | "HTTP 201 или 400" (❌) → "HTTP 201 Created" (✅) |
| **Atomicity** | 1 Row = 1 Check | "Success then Fail" (❌) → Две разные строки (✅) |
| **BVA** | Full Coverage | Только Min-1 (fail) (❌) → Min-1 (fail) + Min (success) (✅) |
| **Completeness** | Full Grid | Только Happy Path (❌) → POS + NEG + BVA + SEC + HEADERS (✅) |
| **Duplication** | NO Duplicates | Same Action + Same Expected = Удалить дубликат |
| **Contract** | Schema-First | `"Token returned"` (❌) → `"Contract Match: token(UUID), expires_at(ISO8601)"` (✅) |
| **State** | Side Effects | `"201 Created"` (❌) → `"201 Created. DB: status='PENDING'"` (✅) для POST/PATCH |
| **Audit** | NEG Specificity | `"400 Bad Request"` (❌) → `"400 + body.code: 'VALIDATION_ERROR'"` (✅) |
| **Headers** | Content-Type | Не указан Content-Type (❌) → отдельная строка `HEADERS` для каждого endpoint (✅) |
| **Cleanup** | Teardown | POS без cleanup (❌) → `"Cleanup: DELETE /resource/{UUID}"` (✅) |
| **L10N** | UTF-8 Round-Trip | Только ASCII в name/address (❌) → `{CYR_NAME}`, `{AR_NAME}`, `{EMOJI_STRING}` (✅) если поле текстовое |
| **IDEM** | Idempotency | Нет повторного запроса (❌) → IDEM-сценарии обязательны для всех POST/PUT: повторный запрос без дубля + 409 при конфликте (✅) |

## Output Template

Создай файл `audit/test-scenarios.md`.
**Важно:** Если сценариев > 100 или файл становится слишком большим, создай папку `audit/scenarios/` и разбей вывод: `01_Auth.md`, `02_Users.md`, `03_Orders.md`.

```markdown
# Test Scenarios Specification

## Feature: [User Auth] (POST /login)

| ID | Type | Scenario | Input Data | Expected Result (HTTP + Logic) |
|----|------|----------|------------|--------------------------------|
| AUTH-01 | POS | Valid Login | email: `{USER}`, pass: `{PASS}` | 200 OK, Token returned |
| AUTH-02 | NEG | Invalid Pass | email: `{USER}`, pass: `Wrong` | 401 Unauthorized |
| AUTH-03 | SEC | SQL Injection | email: `' OR 1=1--` | 400 Bad Request (Sanitized) |

## Feature: [Create Order] (POST /orders)

| ID | Type | Scenario | Input Data | Expected Result (HTTP + Logic) |
|----|------|----------|------------|--------------------------------|
| ORD-01 | POS | Simple Order | items: `[{id:1, qty:1}]` | 201 Created, ID: `{UUID}` |
| ORD-02 | BVA | Max Qty | items: `[{id:1, qty:{MAX_INT}}]` | 201 Created |
| ORD-03 | BVA | Qty Overflow | items: `[{id:1, qty:{MAX_INT}+1}]` | 400 Bad Request |
| ORD-04 | NEG | Empty Cart | items: `[]` | 400 Bad Request |
```

## Execution Flow

1. **Analyze:** Найди спецификации. Составь полный список эндпоинтов.
   - Извлеки `EXCLUDED_TYPES` из спецификации (см. Spec Exclusions выше).
   - Если найдены исключения — зафикси их явно в начале выходного файла: `> ⚠️ Scope: SEC excluded per spec (section X.Y)`.
2. **Design Loop:** Для каждого эндпоинта, **пропуская типы из `EXCLUDED_TYPES`**:
   - Сгенерируй POS сценарии (Min/Max).
   - Сгенерируй NEG (Validation).
   - Сгенерируй BVA (Границы).
   - Сгенерируй SEC (Auth/Injection).
   - Сгенерируй IDEM (повторный запрос) — для всех POST/PUT **обязательно**.
3. **Review (Exclusion Compliance Checklist):**
   - [ ] BVA полный? (Min-1/Min, Max/Max+1) — **пропустить если BVA ∈ `EXCLUDED_TYPES`**
   - [ ] NO hardcode? (email/phone/name)
   - [ ] NO PII? (@gmail/@yandex, реальные ФИО)
   - [ ] Expectations конкретны? (нет "или", "зависит от")
   - [ ] Atomic? (1 строка = 1 сценарий)
   - [ ] Дубликаты? (Same Action + Same Expected → удалить)
   - [ ] **Нет сценариев из `EXCLUDED_TYPES`?** → Найти и удалить все строки, где `Type` входит в `EXCLUDED_TYPES`.
   - [ ] **Нет сценариев из `EXCLUDED_SCENARIOS`?** → Найти и удалить строки, соответствующие паттернам (injection, дубли NEG по полям, BVA исключённых полей, лишние encoding-варианты POS).
4. **Write:** Запиши результат в `audit/test-scenarios.md` (или разбей на файлы).

## Quality Gates

- Каждый endpoint имеет минимум 1 POS + 1 NEG + 1 BVA + 1 SEC + 1 HEADERS сценарий; каждый POST/PUT дополнительно минимум 1 IDEM
- Ни один сценарий не содержит hardcode данных (email, phone, name)
- Все Expected Results конкретны (HTTP-код + бизнес-логика)
- BVA покрывает оба граничных значения (Min-1/Min, Max/Max+1)
- Нет дублирующихся строк (Same Action + Same Expected)
- POS-сценарии мутирующих endpoints содержат `Contract Match` с типами всех ключевых полей
- Мутирующие endpoints (POST/PATCH/PUT/DELETE) содержат `State Verification` в Expected Result
- NEG-сценарии содержат `body.code: '{ERROR_CODE}'`, а не только HTTP-статус
- Каждый POS-сценарий содержит `Cleanup` — шаг очистки или явный `Cleanup: N/A`
- Endpoints с текстовыми полями (name/address/description) содержат L10N-сценарии (`{CYR_NAME}`, `{AR_NAME}`) или явное `L10N: out of scope` из спецификации
- Все POST/PUT endpoints содержат IDEM-сценарии (повторный запрос без дубля + конфликт) или явное `IDEM: not required` из спецификации

## Completion Contract

✅ SKILL COMPLETE: /test-cases
├─ Артефакт: audit/test-scenarios.md (или папка audit/scenarios/)
├─ Coverage: 100% found endpoints
├─ Scenarios: N (POS: X, NEG: Y, BVA: Z, SEC: W)
└─ Ready for: /api-tests (Implementation)
