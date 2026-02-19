# AI-конфигурация проекта

> Реестр AI-паттернов, файлов и решений этого проекта.
> Обновляется через `/update-ai-setup`.

## Архитектура AI-контекста

Три слоя по принципу Progressive Disclosure:

```text
┌─────────────────────────────────────────┐
│  Уровень 1: CLAUDE.md (122 строки)      │  ← Всегда в контексте
│  Tech Stack, Safety, Skills, Conventions│
├─────────────────────────────────────────┤
│  Уровень 2: qa_agent.md (155 строк)     │  ← При вызове любого skill
│  Mindset, Anti-Patterns, Protocols      │
├─────────────────────────────────────────┤
│  Уровень 3: SKILL.md + references/      │  ← При активации конкретного skill
│  Алгоритм, примеры, чек-листы           │
└─────────────────────────────────────────┘
```

AI загружает только нужный слой — экономия токенов, фокус на задаче.

## Инвентаризация файлов

### Структура проекта

```text
.
├── CLAUDE.md                        # Project Passport — главный контекст для AI
├── README.md                        # Документация проекта
├── .mcp.json                        # MCP серверы: context7, sequential-thinking
├── .markdownlint.yaml               # Правила линтинга markdown
│
├── .claude/                         # Конфигурация Claude Code
│   ├── qa_agent.md                  # Core Mindset + Anti-Patterns
│   │
│   │   Routing:
│   │   qa_agent.md (Оркестратор)
│   │     ├── agents/sdet.md      →  /test-cases, /api-tests, /init-skill
│   │     └── agents/auditor.md   →  /skill-audit, /doc-lint, /screenshot-analyze
│   │
│   ├── protocols/                   # Протоколы поведения агентов
│   │   └── gardener.md              # 🌱 Интерфейс проактивных улучшений
│   ├── settings.json                # Плагины, permissions, hooks
│   ├── agents/                      # Role-specific агенты
│   │   ├── sdet.md
│   │   └── auditor.md
│   ├── hooks/                       # PostToolUse hooks
│   │   └── skill-lint.sh
│   ├── qa-antipatterns/             # Shared анти-паттерны (1 файл)
│   ├── references/                  # Shared шаблоны
│   │   ├── claude-md-template.md
│   │   ├── qa-agent-template.md
│   │   └── skill-template.md
│   └── skills/                      # Навыки (11 skills)
│       ├── spec-audit/              # /spec-audit — QA-аудит требований
│       ├── test-cases/              # /test-cases — генерация тест-кейсов
│       ├── api-tests/               # /api-tests — API автотесты
│       ├── screenshot-analyze/      # /screenshot-analyze — L10n UI аудит
│       ├── repo-scout/              # /repo-scout — разведка бэкенд-репо
│       ├── init-project/            # /init-project — генерация CLAUDE.md
│       ├── init-agent/              # /init-agent — генерация qa_agent.md
│       ├── init-skill/              # /init-skill — генерация нового skill
│       ├── doc-lint/                # /doc-lint — аудит документации
│       ├── update-ai-setup/         # /update-ai-setup — обновление реестра
│       └── skill-audit/             # /skill-audit — аудит SKILL.md
│
├── docs/                            # Документация
│   └── ai-setup.md                  # Реестр AI-конфигурации
│
├── specifications/                  # Спецификации API для анализа
│
├── src/test/
│   ├── testCases/                   # Мануальные тесты (Kotlin DSL)
│   ├── kotlin/                      # API автотесты
│   └── resources/screenshots/       # Скриншоты для L10n анализа
│
├── audit/                           # Результаты аудита требований
└── rtl-example/                     # Пример RTL верстки
```

### Корневые конфигурационные файлы

| Файл              | Путь                            | Строк | Назначение                                             |
|-------------------|---------------------------------|------:|--------------------------------------------------------|
| CLAUDE.md         | `CLAUDE.md`                     |   122 | Главный онбординг: стек, безопасность, конвенции       |
| QA Agent          | `.claude/qa_agent.md`           |   155 | Mindset, анти-паттерны, Cross-Skill Protocol           |
| Gardener Protocol | `.claude/protocols/gardener.md` |    46 | Интерфейс проактивных улучшений (Dependency Injection) |
| Settings          | `.claude/settings.json`         |    46 | Плагины, permissions, hooks                            |
| MCP Servers       | `.mcp.json`                     |    12 | context7 + sequential-thinking                         |
| Markdownlint      | `.markdownlint.yaml`            |    38 | Правила линтинга markdown                              |

### Скиллы

| Скилл                 | Путь                                         | Строк | Категория  | Триггер                                    |
|-----------------------|----------------------------------------------|------:|------------|--------------------------------------------|
| `/api-tests`          | `.claude/skills/api-tests/SKILL.md`          |   197 | Generation | API автотесты (common-test-libs + JUnit 5) |
| `/doc-lint`           | `.claude/skills/doc-lint/SKILL.md`           |   248 | Analysis   | Аудит качества документации                |
| `/init-agent`         | `.claude/skills/init-agent/SKILL.md`         |   200 | Meta       | Генерация qa_agent.md                      |
| `/init-project`       | `.claude/skills/init-project/SKILL.md`       |   146 | Meta       | Генерация CLAUDE.md                        |
| `/init-skill`         | `.claude/skills/init-skill/SKILL.md`         |   283 | Meta       | Генерация нового skill                     |
| `/repo-scout`         | `.claude/skills/repo-scout/SKILL.md`         |   272 | Analysis   | Разведка бэкенд-репо                       |
| `/screenshot-analyze` | `.claude/skills/screenshot-analyze/SKILL.md` |   279 | Analysis   | L10N и UI дефекты                          |
| `/skill-audit`        | `.claude/skills/skill-audit/SKILL.md`        |   212 | Analysis   | Аудит SKILL.md                             |
| `/spec-audit`         | `.claude/skills/spec-audit/SKILL.md`         |   204 | Analysis   | QA-аудит требований                        |
| `/test-cases`         | `.claude/skills/test-cases/SKILL.md`         |   192 | Generation | Мануальные тест-кейсы                      |
| `/update-ai-setup`    | `.claude/skills/update-ai-setup/SKILL.md`    |   169 | Meta       | Обновление этого реестра                   |

### Анти-паттерны (shared)

| Файл   | Путь                                | Строк | Для скиллов |
|--------|-------------------------------------|------:|-------------|
| _index | `.claude/qa-antipatterns/_index.md` |    72 | Все         |

### Reference-файлы

| Файл                 | Путь                                                                | Строк | Назначение                                      |
|----------------------|---------------------------------------------------------------------|------:|-------------------------------------------------|
| claude-md-template   | `.claude/references/claude-md-template.md`                          |   106 | Шаблон CLAUDE.md (shared)                       |
| qa-agent-template    | `.claude/references/qa-agent-template.md`                           |   103 | Шаблон qa_agent.md (shared)                     |
| skill-template       | `.claude/references/skill-template.md`                              |   143 | Шаблон SKILL.md (shared)                        |
| api-patterns         | `.claude/skills/api-tests/references/api-patterns.md`               |    68 | Паттерны для API-тестов                         |
| examples             | `.claude/skills/api-tests/references/examples.md`                   |    68 | Примеры кода для /api-tests                     |
| best-practices       | `.claude/skills/doc-lint/references/best-practices.md`              |    82 | Корпоративные практики документирования         |
| check-rules          | `.claude/skills/doc-lint/references/check-rules.md`                 |   110 | Пороги, сигнатуры дубликатов, SSOT-матрица      |
| phases               | `.claude/skills/doc-lint/references/phases.md`                      |   215 | Фазы и алгоритм выполнения для /doc-lint        |
| qa-agent-template    | `.claude/skills/init-agent/references/qa-agent-template.md`         |   103 | Шаблон qa_agent.md                              |
| qa-profiles          | `.claude/skills/init-agent/references/qa-profiles.md`               |   128 | Профили QA-агентов                              |
| claude-md-template   | `.claude/skills/init-project/references/claude-md-template.md`      |   116 | Шаблон CLAUDE.md                                |
| validation-checklist | `.claude/skills/init-skill/references/validation-checklist.md`      |    39 | Чек-лист валидации для /init-skill              |
| yaml-reference       | `.claude/skills/init-skill/references/yaml-reference.md`            |    91 | YAML-спецификация skill                         |
| interaction-guide    | `.claude/skills/init-skill/references/interaction-guide.md`         |    95 | Гайд по интерактивному workflow для /init-skill |
| lang-patterns        | `.claude/skills/repo-scout/references/lang-patterns.md`             |    93 | Языковые паттерны для /repo-scout               |
| report-template      | `.claude/skills/repo-scout/references/report-template.md`           |   101 | Шаблон отчёта для /repo-scout                   |
| checklists           | `.claude/skills/screenshot-analyze/references/checklists.md`        |   113 | Чек-листы L10N проверок                         |
| cldr-tables          | `.claude/skills/screenshot-analyze/references/cldr-tables.md`       |   151 | CLDR справочники                                |
| html-template        | `.claude/skills/screenshot-analyze/references/html-template.md`     |   160 | HTML шаблон отчёта                              |
| l10n-domain-rules    | `.claude/skills/screenshot-analyze/references/l10n-domain-rules.md` |    41 | Правила локализации доменов                     |
| lqa-rules            | `.claude/skills/screenshot-analyze/references/lqa-rules.md`         |    42 | Правила LQA-проверок для /screenshot-analyze    |

### Агенты

| Файл    | Путь                        | Строк | Роль                          |
|---------|-----------------------------|------:|-------------------------------|
| auditor | `.claude/agents/auditor.md` |   188 | Планирование + аудит качества |
| sdet    | `.claude/agents/sdet.md`    |   208 | Генерация тестового кода      |

### Hooks

| Файл          | Путь                          | Строк | Триггер                  | Назначение                            |
|---------------|-------------------------------|------:|--------------------------|---------------------------------------|
| skill-lint.sh | `.claude/hooks/skill-lint.sh` |    48 | PostToolUse (Write/Edit) | Валидация SKILL.md при редактировании |

### Документация

| Файл                 | Путь                        | Строк | Назначение                                                       |
|----------------------|-----------------------------|------:|------------------------------------------------------------------|
| AI Setup (этот файл) | `docs/ai-setup.md`          |   437 | Реестр AI-конфигурации                                           |
| Workshop Commands    | `docs/workshop-commands.md` |   247 | Команды для воркшопа                                             |
| Scope Guide          | `docs/scope-guide.md`       |    59 | Краткая справка по scope-параметрам для /skill-audit и /doc-lint |
| Audit History        | `audit/audit-history.md`    |     5 | Append-only лог аудитов (/skill-audit, /doc-lint)                |

## Каталог паттернов

### 1. Three-Layer AI Context

Трёхуровневая архитектура: CLAUDE.md → qa_agent.md → SKILL.md. Каждый слой загружается по необходимости.
→ Реализация: `CLAUDE.md` (секция QA Agent), `.claude/qa_agent.md` (секция Progressive Disclosure)

### 2. Progressive Disclosure

AI загружает только нужный уровень контекста: YAML description → тело SKILL.md → references/scripts.
→ Реализация: `.claude/qa_agent.md:61-67`, `.claude/references/skill-template.md:64-77`

### 3. Self-Review Protocol

Каждый skill завершается Self-Review с scorecard. Score < 70% → предупреждение.
→ Реализация: `.claude/qa_agent.md:164-193` (Skill Completion Protocol)

### 4. 4-Layer Test Architecture

Models → Client → Data → Tests. Разделение ответственности в автотестах.
→ Реализация: `.claude/skills/api-tests/SKILL.md` (секция Алгоритм)

### 5. Anti-Pattern Library

1 индексный файл в shared-директории `.claude/qa-antipatterns/`, ссылки из qa_agent.md.
→ Реализация: `.claude/qa-antipatterns/_index.md`, `.claude/qa_agent.md`

### 6. Locked Tech Stack + BANNED

Фиксированный стек (Ktor, Jackson, Kotest, JUnit 5, Allure) с явным списком запрещённых альтернатив.
→ Реализация: `CLAUDE.md:17-25`

### 7. Token Economy

PAUSE при задаче > 20K токенов. FULL_SCAN — keyword для полного сканирования.
→ Реализация: `CLAUDE.md:38-40`

### 8. Safety Protocols

FORBIDDEN-команды, DESTROY-override, обязательный backup перед деструктивными операциями.
→ Реализация: `CLAUDE.md:32-36`

### 9. Cross-Skill Pipeline

Последовательный workflow: `/spec-audit` → `/test-cases` → `/api-tests`. Каждый skill учитывает upstream-артефакты.
→ Реализация: `.claude/qa_agent.md:131-154`

### 10. Compilation Gate

Обязательная проверка `./gradlew compileTestKotlin` перед коммитом для `/api-tests`. Max 3 попытки.
→ Реализация: `.claude/qa_agent.md:183-193`, `CLAUDE.md:75`

### 11. Traceability

Связь мануальных тест-кейсов с автотестами через `@Link("TC-XX")`.
→ Реализация: `.claude/qa_agent.md:156-162`

### 12. Security-First Mindset

OWASP, PII-проверки, SQL Injection, XSS, IDOR — встроены в mindset агента.
→ Реализация: `.claude/qa_agent.md:10` (Security First), `.claude/qa-antipatterns/_index.md`

### 13. Meta-Skills Bootstrap

Три мета-скилла для создания AI-конфигурации: `/init-project`, `/init-agent`, `/init-skill`.
→ Реализация: `.claude/skills/init-project/`, `.claude/skills/init-agent/`, `.claude/skills/init-skill/`

### 14. Plugin: kotlin-lsp

Плагин Kotlin LSP для навигации и анализа кода. Включён в `.claude/settings.json`.
→ Реализация: `.claude/settings.json`

### 15. Skill Size Limit

SKILL.md ≤ 500 строк. Превышение → выноси в references/, scripts/, qa-antipatterns/.
→ Реализация: `.claude/qa_agent.md:37-69`, `.claude/references/skill-template.md:5-9`

### 16. Cross-IDE Compatibility

Файлы совместимы с Claude Code, OpenCode, Cursor, VS Code, IntelliJ IDEA, Zed, Cline, Continue.dev.
→ Реализация: `.cursor/rules/*.mdc` (17 шт.), `docs/ai-setup.md`

### 17. Workshop Checkpoint Branches

Git-ветки для checkpoint'ов воркшопа, позволяющие начать с любого этапа.
→ Реализация: `README.md` (секция Checkpoints)

### 18. MCP Integration

MCP серверы расширяют capabilities AI: context7 — актуальные доки библиотек, sequential-thinking — пошаговый анализ сложных задач.
→ Реализация: `.mcp.json`

### 20. Markdown Lint

Автоматическая проверка качества markdown-документации. Selective rules: headings, code blocks, tables, whitespace.
→ Реализация: `.markdownlint.yaml`

### 21. Dependency Injection (Gardener)

Агенты (sdet, auditor) не просто запускают скиллы, а инъектируют в них протокол `.claude/protocols/gardener.md` во время выполнения. Механика: runtime-подключение контекста без увеличения базового промпта. Результат: AI работает в режиме Co-Pilot — выполняет задачу + предлагает улучшения (🌱) по архитектуре/безопасности, не блокируя основной поток.
→ Реализация: `.claude/protocols/gardener.md`, `.claude/agents/auditor.md` (секция Protocol Injection)

## Feedback Loop

### Цикл непрерывного улучшения

```text
┌─────────────┐     ┌───────────┐     ┌───────────┐
│  Discovery  │────▶│ Strategy  │────▶│ Execution │
│ /repo-scout │     │           │     │ /api-tests│
│ /spec-audit │     │           │     │/test-cases│
└─────────────┘     └───────────┘     └─────┬─────┘
       ▲                                     │
       │            ┌──────────────────────┐ │
       │            │  Gardener Protocol   │ │
       └────────────│  ошибка → правило →  │◀┘
                    │  предотвращение      │
                    └──────────┬───────────┘
                               │ обновляет
                               ▼
                    ┌──────────────────────┐
                    │ qa-antipatterns/     │
                    │ skills/*/SKILL.md    │
                    │ agents/*.md          │
                    │ CLAUDE.md            │
                    └──────────────────────┘
```

**Ключевой принцип:** Писатель ≠ Проверяющий. Разделение ответственности — zero rubber-stamping.

### Quality Gates

| Переход          | Что проверяется                                       | Блокер                                 |
|------------------|-------------------------------------------------------|----------------------------------------|
| Plan → Execution | Покрытие endpoints, приоритеты, gaps в спецификациях  | Пропущены Critical endpoints           |
| Execution → Done | Компиляция, `@Link` на спецификацию, coverage vs план | `compileTestKotlin` fail (max 3 retry) |

### 10 механизмов самоулучшения

| #  | Механизм                      | Что делает                                                                                                                                                                                          |
|----|-------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1  | **Multi-Agent Orchestration** | 3 агента (SDET, Auditor) + оркестратор `qa_agent.md`                                                                                                                                                |
| 2  | **Doc-Lint**                  | Cross-file дубликаты, нарушения SSOT, health score: `100 - (CRIT×15) - (WARN×5) - (INFO×1)`                                                                                                         |
| 4  | **Skill-Audit**               | 9 проверок: раздутость, waste-секции, дублирование, вредные паттерны                                                                                                                                |
| 5  | **AI Registry Sync**          | Delta-обновление `docs/ai-setup.md` — реестр всех AI-файлов проекта                                                                                                                                 |
| 6  | **Real-Time Hook**            | `skill-lint.sh` на PostToolUse — валидация SKILL.md при каждом редактировании                                                                                                                       |
| 7  | **Gardener Protocol**         | In-process Consult: агент замечает "запахи" кода/архитектуры во время работы → выдаёт 🌱 Suggestion, не блокируя основной поток; найденная ошибка → новое правило в antipatterns/SKILL.md/CLAUDE.md |
| 8  | **Anti-Pattern Library**      | 1 индексный файл в `.claude/qa-antipatterns/` — reference-driven проверки                                                                                                                           |
| 9  | **CLAUDE.md Plugin**          | `kotlin-lsp` — навигация и анализ Kotlin кода                                                                                                                                                       |
| 10 | **Segregation of Duties**     | Architect планирует, SDET кодит, Auditor проверяет — никто не проверяет сам себя - предотвращает ситуацию когда ИИ одобряет собственный плохой код                                                  |

## Стек и плагины

### Tech Stack (LOCKED)

| Компонент      | Технология                                               | BANNED                 |
|----------------|----------------------------------------------------------|------------------------|
| HTTP Client    | common-test-libs ApiClient + `ApiRequestBaseJson<T>`     | Custom HTTP wrappers   |
| Serialization  | Jackson (SNAKE_CASE)                                     | Gson, Moshi            |
| Assertions     | JUnit 5 (`assertEquals` с message) + Hamcrest `checkAll` | Assertions без message |
| Test Framework | JUnit 5                                                  | TestNG                 |
| Reporting      | Allure                                                   | —                      |

### Build

| Компонент | Версия |
|-----------|--------|
| Kotlin    | 1.9.22 |
| JVM       | 17     |
| Gradle    | 9.2.1  |

### Плагины

| Плагин     | Пакет                   | Статус  | Назначение                              |
|------------|-------------------------|---------|-----------------------------------------|
| kotlin-lsp | claude-plugins-official | Включён | Kotlin LSP для навигации и анализа кода |

### MCP серверы

| Сервер              | Пакет                                            | Назначение                        |
|---------------------|--------------------------------------------------|-----------------------------------|
| context7            | @upstash/context7-mcp@latest                     | Актуальная документация библиотек |
| sequential-thinking | @modelcontextprotocol/server-sequential-thinking | Пошаговый анализ сложных задач    |

## Безопасность и управление

| Механизм         | Описание                                                              | Где определён         |
|------------------|-----------------------------------------------------------------------|-----------------------|
| FORBIDDEN        | `git reset --hard`, `git clean -fd`, удаление веток, `rm -rf .git`    | `CLAUDE.md:34`        |
| DESTROY          | Override для деструктивных операций — требуется слово от пользователя | `CLAUDE.md:36`        |
| Token Economy    | PAUSE > 20K токенов, FULL_SCAN для полного сканирования               | `CLAUDE.md:38-40`     |
| Planning First   | Задачи > 3 файлов → Analysis → Plan → Execute                         | `CLAUDE.md:42`        |
| Git Workflow     | Подтверждение ветки перед push, "don't push" = STOP                   | `CLAUDE.md:44-46`     |
| Compilation Gate | `./gradlew compileTestKotlin` перед коммитом, max 3 попытки           | `qa_agent.md:183-193` |
| Fail Fast        | BLOCKER при нетестируемых/противоречивых требованиях                  | `qa_agent.md:15-34`   |

## Кросс-IDE совместимость

| Возможность            | Claude Code | OpenCode | Cursor | VS Code | IntelliJ |
|------------------------|:-----------:|:--------:|:------:|:-------:|:--------:|
| CLAUDE.md              |      ✅      |    ✅     |   ✅    |    ✅    |    ✅     |
| qa_agent.md            |      ✅      |    ✅     |   ✅    |    ✅    |    ✅     |
| Skills (SKILL.md)      |      ✅      |    ✅     |   ✅    |    ✅    |    ⚠️    |
| Плагины                |      ✅      |    ❌     |   ❌    |    ❌    |    ❌     |
| Anti-patterns (shared) |      ✅      |    ✅     |   ✅    |    ✅    |    ✅     |

✅ = полная поддержка, ⚠️ = частичная (ручное подключение), ❌ = не поддерживается

### IDE-специфичные конфиги

| Файл                     | Путь                  | Назначение                       |
|--------------------------|-----------------------|----------------------------------|
| Cursor wrappers (17 шт.) | `.cursor/rules/*.mdc` | Thin wrappers с `@file` ссылками |

