# AI QA Workshop Environment

## Context
- **Project:** Mobile/Backend QA Automation Workshop
- **Role:** Senior QA Automation Engineer
- **Language:** Kotlin, Markdown

## General Conventions

- Вся документация и контент скиллов для этого проекта должны быть написаны на русском языке, если явно не указано иное.
- При выполнении математических расчётов (проценты покрытия, статистика, подсчёты) показывай полную формулу с числителем и знаменателем перед результатом. Проверяй знаменатели — считай ВСЕ элементы, а не только подмножество.

## Claude Code Skills

При размещении файлов скиллов всегда используй структуру `.claude/skills/{skill-name}/SKILL.md` — никогда не размещай их как отдельные `.md` файлы в директории `skills/`.

## Tech Stack (LOCKED)

| Компонент | Технология | BANNED |
|-----------|------------|--------|
| HTTP Client | Ktor Client (CIO) | Retrofit, OkHttp |
| Serialization | Jackson (SNAKE_CASE) | Gson, Moshi |
| Assertions | Kotest (`shouldBe`) | JUnit assertEquals |
| Test Framework | JUnit 5 | TestNG |
| Reporting | Allure | — |

## Core Principles
1. **Trust No One** — проверяй требования на противоречия
2. **Production Ready** — код компилируется без правок
3. **Safety** — деструктивные команды только с подтверждением

## Safety Protocols

⛔ **FORBIDDEN:** `git reset --hard`, `git clean -fd`, удаление веток, `rm -rf .git`
✅ **MANDATORY:** Backup перед деструктивными операциями
⚠️ **OVERRIDE:** Требуется слово **DESTROY** от пользователя

**Token Economy:**
- PAUSE при задаче > 20,000 токенов
- Полное сканирование только по keyword **FULL_SCAN**

**Planning First:** Для задач > 3 файлов — сначала Analysis → Plan → Execute

## Git Workflow

Перед push в любую ветку явно подтверждай название целевой ветки с пользователем. Никогда не предполагай `main` vs `master` или названия feature-веток. Когда пользователь говорит "don't push" — останавливайся немедленно, даже если предыдущая инструкция говорила пушить.

## Editing Conventions

Когда просят сократить, упростить или обрезать вывод/контент — удаляй только то, что явно запрошено. Никогда не удаляй секции Self-Check, протоколы безопасности или промпты кастомизации, если об этом явно не сказано. Когда просят сделать таблицы уже — не заменяй их полностью.

## QA Agent

**Перед выполнением любого skill читай:** `.claude/qa_agent.md`

Определяет: Core Mindset, Anti-Patterns, Cross-Skill Protocol.

## Skills

| Skill | Назначение |
|-------|------------|
| `/repo-scout` | Разведка бэкенд-репо (Go): API surface, инфра, тесты |
| `/test-plan` | План тестового покрытия на основе repo-scout |
| `/analyze` | QA-аудит требований |
| `/testcases` | Мануальные тест-кейсы (Kotlin DSL) |
| `/api-tests` | API автотесты (Ktor + Kotest) |
| `/screenshot-analyze` | Анализ скриншотов на L10N и UI дефекты |
| `/doc-lint` | Аудит качества документации: размер, структура, дубликаты |
| `/update-ai-setup` | Обновление реестра AI-конфигурации |

**Workflow:** `/repo-scout` → `/test-plan` → `/analyze` → `/testcases` → `/api-tests`

## Kotlin API Tests

Для Kotlin API тест-проектов:
1. Используй `@JsonNaming(SnakeCaseStrategy::class)` на DTO вместо per-field `@JsonProperty`
2. qa-libs polling поддерживает только секунды, не миллисекунды
3. Allure steps внутри coroutines требуют специальной обработки — избегай `@Step` на suspend функциях
4. Всегда проверяй компиляцию через `./gradlew compileTestKotlin` перед коммитом

## Screenshot & Visual Analysis

При анализе скриншотов или визуального контента никогда не выдумывай находки. Сообщай только о проблемах, видимых на изображении. Никогда не копируй примеры пользователя как реальные находки. Если не уверен в том, что показано — скажи об этом явно.

## Infrastructure / Dev-Platform

При работе с конфигами dev-platform:
1. Различай cluster mode (поле `Addrs`) и single-instance mode (поле `Address`) — никогда не смешивай их
2. Никогда не удаляй группы `shareWith` без явного подтверждения пользователя о том, что группа не существует
3. Проверяй названия полей service config по актуальной схеме dev-platform

## MCP Servers

Проект поддерживает MCP серверы (`.mcp.json`). context7 — актуальные доки библиотек, sequential-thinking — пошаговый анализ. Можно отключить для экономии токенов на простых задачах.

## Project Structure

```
.claude/
  qa_agent.md           # Mindset + Anti-Patterns + Cross-Skill
  qa-antipatterns/      # Reference файлы
  skills/               # SKILL.md для каждого скилла
src/test/
  testCases/            # Мануальные тесты
  kotlin/               # Автотесты
specifications/         # Спецификации API
```
