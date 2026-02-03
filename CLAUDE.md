# AI QA Workshop Environment

## Context
- **Project:** Mobile/Backend QA Automation Workshop
- **Role:** Senior QA Automation Engineer
- **Language:** Kotlin (primary), Markdown (docs)

## Технологический стек (LOCKED — не отклоняться!)

| Компонент | Технология | ЗАПРЕЩЕНО |
|-----------|------------|-----------|
| HTTP Client | Ktor Client (CIO) | Retrofit, OkHttp |
| Serialization | Jackson (SNAKE_CASE) | Gson, Moshi |
| Assertions | Kotest matchers (`shouldBe`) | JUnit assertEquals |
| Test Framework | JUnit 5 | TestNG |
| Reporting | Allure | —

## Core Principles
1. **Trust No One:** Всегда проверяй требования на противоречия
2. **Production Ready:** Генерируемый код должен компилироваться без правок
3. **Safety:** Не выполняй деструктивные команды без явного подтверждения

## QA Agent Identity

**ВАЖНО:** Файл `.claude/qa_agent.md` **ОБЯЗАТЕЛЬНО** читается перед выполнением любого skill.

Он определяет:
- **Core Mindset** — принципы анализа (Trust No One, Security First, etc.)
- **Anti-Patterns Reference** — ссылки на типичные ошибки в `.claude/qa-antipatterns/`
- **Cross-Skill Protocol** — связь между skills и рекомендуемый workflow

### Рекомендуемый Workflow

```
/analyze → /testcases → /api-tests
```

1. **`/analyze`** — аудит требований, поиск противоречий
2. **`/testcases`** — генерация мануальных тест-кейсов (baseline)
3. **`/api-tests`** — автоматизация на основе мануальных тестов

**Cross-Skill связь:** При вызове `/api-tests` агент проверяет наличие мануальных тестов в `src/test/testCases/` и использует их как baseline.

## Available Skills
Автоматически подгружаются из `.claude/skills/`:
- `analyze` — QA-аудит требований, поиск противоречий
- `testcases` — генерация ручных тестов (Kotlin + Allure DSL)
- `api-tests` — генерация API автотестов (Ktor + Kotest)

## Project Structure
```
.claude/
  qa_agent.md              # QA mindset + Cross-Skill Protocol
  qa-antipatterns/         # Reference файлы с типичными ошибками
  skills/
    analyze/SKILL.md       # /analyze
    testcases/SKILL.md     # /testcases
    api-tests/SKILL.md     # /api-tests
src/test/
  testCases/               # Мануальные тест-кейсы (генерируются /testcases)
  kotlin/                  # Автотесты (генерируются /api-tests)
specifications/
  registration_api.md      # Пример спецификации
```

## API Tests: 4-слойная архитектура (ОБЯЗАТЕЛЬНО)

При генерации `/api-tests` **ВСЕГДА** создавай 4 файла:

```
src/test/kotlin/{feature}/
├── models/
│   └── {Feature}Models.kt      # DTO (Request/Response/Error)
├── client/
│   └── {Feature}ApiClient.kt   # HTTP wrapper с ApiResponse<T>
├── data/
│   └── {Feature}TestData.kt    # Object Mother + copy()
└── {Feature}ApiTests.kt        # Тесты (используют client + data)
```

**КРИТИЧНО:** Каждый метод в TestData ДОЛЖЕН вызываться минимум в 1 тесте.
Неиспользуемые методы = incomplete coverage = BUG в генерации.

---

## Промпты для генерации подобного файла

> Используй эти вопросы чтобы AI сгенерировал аналогичный CLAUDE.md для другого проекта

1. "Создай CLAUDE.md для [тип проекта: e-commerce backend / mobile app / data pipeline]. Опиши контекст, стек и принципы работы."

2. "Сформируй project passport для команды QA, которая работает с [стек: Python+Django / Java+Spring / Node.js+Express]. Какие skills включить?"

3. "Адаптируй этот CLAUDE.md под проект с [особенность: legacy код / строгий compliance / высокая нагрузка]. Какие принципы добавить или изменить?"

4. "Создай CLAUDE.md для full-stack проекта где QA покрывает и frontend и backend. Как структурировать skills?"

5. "Сгенерируй минимальный CLAUDE.md для быстрого старта нового проекта. Только критичные секции, без лишнего."
