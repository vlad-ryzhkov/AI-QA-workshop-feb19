# AI QA Workshop Environment

## Context
- **Project:** Mobile/Backend QA Automation Workshop
- **Role:** Senior QA Automation Engineer
- **Language:** Kotlin, Markdown

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

## QA Agent

**Перед выполнением любого skill читай:** `.claude/qa_agent.md`

Определяет: Core Mindset, Anti-Patterns, Cross-Skill Protocol.

## Skills

| Skill | Назначение |
|-------|------------|
| `/analyze` | QA-аудит требований |
| `/testcases` | Мануальные тест-кейсы (Kotlin DSL) |
| `/api-tests` | API автотесты (Ktor + Kotest) |
| `/screenshot-analyze` | Анализ скриншотов на L10N и UI дефекты |

**Workflow:** `/analyze` → `/testcases` → `/api-tests`

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
