# AI-конфигурация проекта

> Реестр AI-паттернов, файлов и решений этого проекта.
> Обновляется через `/update-ai-setup`.

## Архитектура AI-контекста

Три слоя по принципу Progressive Disclosure:

```
┌─────────────────────────────────────────┐
│  Уровень 1: CLAUDE.md (99 строк)        │  ← Всегда в контексте
│  Tech Stack, Safety, Skills, Conventions│
├─────────────────────────────────────────┤
│  Уровень 2: qa_agent.md (194 строки)    │  ← При вызове любого skill
│  Mindset, Anti-Patterns, Protocols      │
├─────────────────────────────────────────┤
│  Уровень 3: SKILL.md + references/      │  ← При активации конкретного skill
│  Алгоритм, примеры, чек-листы           │
└─────────────────────────────────────────┘
```

AI загружает только нужный слой — экономия токенов, фокус на задаче.

## Инвентаризация файлов

### Корневые конфигурационные файлы

| Файл | Путь | Строк | Назначение |
|------|------|------:|------------|
| CLAUDE.md | `CLAUDE.md` | 99 | Главный онбординг: стек, безопасность, конвенции |
| QA Agent | `.claude/qa_agent.md` | 194 | Mindset, анти-паттерны, Cross-Skill Protocol |
| Settings | `.claude/settings.json` | 5 | Плагин claude-md-management |
| MCP Servers | `.mcp.json` | 11 | context7 + sequential-thinking |
| Markdownlint | `.markdownlint.yaml` | 38 | Правила линтинга markdown |

### Скиллы

| Скилл | Путь | Строк | Категория | Триггер |
|-------|------|------:|-----------|---------|
| `/analyze` | `.claude/skills/requirements-analysis/SKILL.md` | 154 | Analysis | QA-аудит требований |
| `/testcases` | `.claude/skills/testcases/SKILL.md` | 242 | Generation | Мануальные тест-кейсы |
| `/api-tests` | `.claude/skills/api-tests/SKILL.md` | 478 | Generation | API автотесты (Ktor + Kotest) |
| `/screenshot-analyze` | `.claude/skills/screenshot-analyze/SKILL.md` | 337 | Analysis | L10N и UI дефекты |
| `/init-project` | `.claude/skills/init-project-claudemd/SKILL.md` | 160 | Meta | Генерация CLAUDE.md |
| `/init-agent` | `.claude/skills/init-agent/SKILL.md` | 208 | Meta | Генерация qa_agent.md |
| `/init-skill` | `.claude/skills/init-skill/SKILL.md` | 435 | Meta | Генерация нового skill |
| `/doc-lint` | `.claude/skills/human-doc-lint/SKILL.md` | 362 | Analysis | Аудит качества документации |
| `/update-ai-setup` | `.claude/skills/update-ai-setup/SKILL.md` | — | Meta | Обновление этого реестра |

### Анти-паттерны (shared)

| Файл | Путь | Строк | Для скиллов |
|------|------|------:|-------------|
| flaky-sleep-tests | `.claude/qa-antipatterns/flaky-sleep-tests.md` | 50 | /api-tests |
| no-cleanup-pattern | `.claude/qa-antipatterns/no-cleanup-pattern.md` | 66 | /api-tests |
| map-instead-of-dto | `.claude/qa-antipatterns/map-instead-of-dto.md` | 61 | /api-tests |
| no-abstraction-layer | `.claude/qa-antipatterns/no-abstraction-layer.md` | 71 | /api-tests |
| assertion-without-message | `.claude/qa-antipatterns/assertion-without-message.md` | 72 | /api-tests |
| static-object-mother | `.claude/qa-antipatterns/static-object-mother.md` | 90 | /api-tests |
| pii-literals-in-code | `.claude/qa-antipatterns/pii-literals-in-code.md` | 102 | /api-tests |
| hardcoded-test-data | `.claude/qa-antipatterns/hardcoded-test-data.md` | 64 | /testcases |
| pii-in-test-data | `.claude/qa-antipatterns/pii-in-test-data.md` | 63 | /testcases |

### Reference-файлы

| Файл | Путь | Строк | Назначение |
|------|------|------:|------------|
| claude-md-template | `.claude/references/claude-md-template.md` | 104 | Шаблон CLAUDE.md |
| qa-agent-template | `.claude/references/qa-agent-template.md` | 103 | Шаблон qa_agent.md |
| skill-template | `.claude/references/skill-template.md` | 143 | Шаблон SKILL.md |
| cldr-tables | `.claude/skills/screenshot-analyze/references/cldr-tables.md` | 151 | CLDR справочники |
| checklists | `.claude/skills/screenshot-analyze/references/checklists.md` | 113 | Чек-листы L10N проверок |
| html-template | `.claude/skills/screenshot-analyze/references/html-template.md` | 163 | HTML шаблон отчёта |
| check-rules | `.claude/skills/human-doc-lint/references/check-rules.md` | 108 | Пороги, сигнатуры дубликатов, SSOT-матрица |
| best-practices | `.claude/skills/human-doc-lint/references/best-practices.md` | 82 | Корпоративные практики документирования |

### Документация

| Файл | Путь | Строк | Назначение |
|------|------|------:|------------|
| AI Files Handbook | `docs/ai-files-handbook.md` | 654 | Гайд по созданию AI-файлов |
| AI Setup (этот файл) | `docs/ai-setup.md` | — | Реестр AI-конфигурации |

### Кросс-IDE конфиги

| Файл | Путь | Назначение |
|------|------|------------|
| Cursor wrapper: project | `.cursor/rules/00-project-context.mdc` | `@CLAUDE.md` — alwaysApply |
| Cursor wrapper: agent | `.cursor/rules/01-qa-agent.mdc` | `@.claude/qa_agent.md` |
| Cursor wrapper: analyze | `.cursor/rules/02-skill-analyze.mdc` | `@.claude/skills/requirements-analysis/SKILL.md` |
| Cursor wrapper: testcases | `.cursor/rules/03-skill-testcases.mdc` | `@.claude/skills/testcases/SKILL.md` |
| Cursor wrapper: api-tests | `.cursor/rules/04-skill-api-tests.mdc` | `@.claude/skills/api-tests/SKILL.md` |
| Cursor wrapper: screenshot | `.cursor/rules/05-skill-screenshot.mdc` | `@.claude/skills/screenshot-analyze/SKILL.md` |
| Copilot Instructions | `.github/copilot-instructions.md` | Краткий контекст для VS Code / IntelliJ Copilot |
| Claude PR Review | `.github/workflows/claude_review.yml` | CI/CD review через Claude на PR |
| AGENTS.md | `AGENTS.md` | Pointer для Zed, Cline, Continue.dev |

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
9 анти-паттернов в shared-директории `.claude/qa-antipatterns/`, ссылки из qa_agent.md.
→ Реализация: `.claude/qa-antipatterns/*.md`, `.claude/qa_agent.md:101-129`

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
Последовательный workflow: `/analyze` → `/testcases` → `/api-tests`. Каждый skill учитывает upstream-артефакты.
→ Реализация: `.claude/qa_agent.md:131-154`

### 10. Compilation Gate
Обязательная проверка `./gradlew compileTestKotlin` перед коммитом для `/api-tests`. Max 3 попытки.
→ Реализация: `.claude/qa_agent.md:183-193`, `CLAUDE.md:75`

### 11. Traceability
Связь мануальных тест-кейсов с автотестами через `@Link("TC-XX")`.
→ Реализация: `.claude/qa_agent.md:156-162`

### 12. Security-First Mindset
OWASP, PII-проверки, SQL Injection, XSS, IDOR — встроены в mindset агента.
→ Реализация: `.claude/qa_agent.md:10` (Security First), `.claude/qa-antipatterns/pii-*.md`

### 13. Meta-Skills Bootstrap
Три мета-скилла для создания AI-конфигурации: `/init-project`, `/init-agent`, `/init-skill`.
→ Реализация: `.claude/skills/init-project-claudemd/`, `.claude/skills/init-agent/`, `.claude/skills/init-skill/`

### 14. Plugin: claude-md-management
Плагин для аудита и улучшения CLAUDE.md. Включён в `.claude/settings.json`.
→ Реализация: `.claude/settings.json`

### 15. Skill Size Limit
SKILL.md ≤ 500 строк. Превышение → выноси в references/, scripts/, qa-antipatterns/.
→ Реализация: `.claude/qa_agent.md:37-69`, `.claude/references/skill-template.md:5-9`

### 16. Cross-IDE Compatibility
Файлы совместимы с Claude Code, OpenCode, Cursor, VS Code, IntelliJ IDEA, Zed, Cline, Continue.dev.
→ Реализация: `.cursor/rules/*.mdc`, `.github/copilot-instructions.md`, `AGENTS.md`, `docs/ai-files-handbook.md`

### 17. Workshop Checkpoint Branches
Git-ветки для checkpoint'ов воркшопа, позволяющие начать с любого этапа.
→ Реализация: `README.md` (секция Checkpoints)

### 18. MCP Integration
MCP серверы расширяют capabilities AI: context7 — актуальные доки библиотек, sequential-thinking — пошаговый анализ сложных задач.
→ Реализация: `.mcp.json`

### 19. CI/CD PR Review
GitHub Actions workflow для автоматического code review через Claude на каждый PR. Использует skills проекта.
→ Реализация: `.github/workflows/claude_review.yml`

### 20. Markdown Lint
Автоматическая проверка качества markdown-документации. Selective rules: headings, code blocks, tables, whitespace.
→ Реализация: `.markdownlint.yaml`

## Стек и плагины

### Tech Stack (LOCKED)

| Компонент | Технология | BANNED |
|-----------|------------|--------|
| HTTP Client | Ktor Client (CIO) | Retrofit, OkHttp |
| Serialization | Jackson (SNAKE_CASE) | Gson, Moshi |
| Assertions | Kotest (`shouldBe`) | JUnit assertEquals |
| Test Framework | JUnit 5 | TestNG |
| Reporting | Allure | — |

### Build

| Компонент | Версия |
|-----------|--------|
| Kotlin | 1.9.22 |
| JVM | 17 |
| Gradle | 9.2.1 |

### Плагин

| Плагин | Статус | Назначение |
|--------|--------|------------|
| claude-md-management | Включён | Аудит и улучшение CLAUDE.md |

## Безопасность и управление

| Механизм | Описание | Где определён |
|----------|----------|---------------|
| FORBIDDEN | `git reset --hard`, `git clean -fd`, удаление веток, `rm -rf .git` | `CLAUDE.md:34` |
| DESTROY | Override для деструктивных операций — требуется слово от пользователя | `CLAUDE.md:36` |
| Token Economy | PAUSE > 20K токенов, FULL_SCAN для полного сканирования | `CLAUDE.md:38-40` |
| Planning First | Задачи > 3 файлов → Analysis → Plan → Execute | `CLAUDE.md:42` |
| Git Workflow | Подтверждение ветки перед push, "don't push" = STOP | `CLAUDE.md:44-46` |
| Compilation Gate | `./gradlew compileTestKotlin` перед коммитом, max 3 попытки | `qa_agent.md:183-193` |
| Fail Fast | BLOCKER при нетестируемых/противоречивых требованиях | `qa_agent.md:15-34` |

## Кросс-IDE совместимость

| Возможность | Claude Code | OpenCode | Cursor | VS Code | IntelliJ |
|-------------|:-----------:|:--------:|:------:|:-------:|:--------:|
| CLAUDE.md | ✅ | ✅ | ✅ | ✅ | ✅ |
| qa_agent.md | ✅ | ✅ | ✅ | ✅ | ✅ |
| Skills (SKILL.md) | ✅ | ✅ | ✅ | ✅ | ⚠️ |
| Плагины | ✅ | ❌ | ❌ | ❌ | ❌ |
| Anti-patterns (shared) | ✅ | ✅ | ✅ | ✅ | ✅ |

✅ = полная поддержка, ⚠️ = частичная (ручное подключение), ❌ = не поддерживается

### IDE-специфичные конфиги

| Файл | IDE | Тип |
|------|-----|-----|
| `.cursor/rules/*.mdc` (6 файлов) | Cursor | Thin wrappers с `@file` ссылками |
| `.github/copilot-instructions.md` | VS Code Copilot, IntelliJ Copilot | Самодостаточный краткий контекст |
| `AGENTS.md` | Zed, Cline, Continue.dev | Pointer-файл с ссылками |

## Changelog

| Дата | Изменение |
|------|-----------|
| 2026-02-14 | MCP серверы, CI/CD PR Review, Markdownlint — паттерны #18-#20 |
| 2026-02-14 | Кросс-IDE конфиги: 6 `.cursor/rules/*.mdc`, `.github/copilot-instructions.md`, `AGENTS.md` |
| 2026-02-14 | Создание реестра: 17 паттернов, 8 скиллов, 9 анти-паттернов, 6 references |
