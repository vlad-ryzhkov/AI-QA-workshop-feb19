# CLAUDE.md — Шаблон онбординга проекта

> **Назначение:** Wiki проекта для AI. Первый день нового сотрудника — какой язык, где конфиги, как форматируем код.

---

## Шаблон

```markdown
# [Project Name]

## Context
- **Project:** [Что тестируем/разрабатываем]
- **Role:** Senior QA Automation Engineer
- **Language:** [Kotlin/Python/TypeScript]

## Tech Stack (LOCKED)

| Компонент | Технология | BANNED |
|-----------|------------|--------|
| HTTP Client | [Ktor/requests/axios] | [alternatives] |
| Serialization | [Jackson/Pydantic/zod] | [alternatives] |
| Assertions | [Kotest/pytest/Jest] | [alternatives] |
| Test Framework | [JUnit 5/pytest/Jest] | [alternatives] |
| Reporting | [Allure] | — |

## Project Structure

```text
[Реальная структура проекта]
```

## Commands

| Действие | Команда |
|----------|---------|
| Build | `[command]` |
| Test | `[command]` |
| Single test | `[command]` |

## Safety Protocols

⛔ **FORBIDDEN:** `git reset --hard`, `git clean -fd`, удаление веток
✅ **MANDATORY:** Backup перед деструктивными операциями
⚠️ **OVERRIDE:** Требуется слово **DESTROY**

## Token Economy

- PAUSE при задаче > 20,000 токенов
- Полное сканирование только по **FULL_SCAN**

## Workflow

Для задач > 3 файлов: Analysis → Plan → Execute → Verify
```

---

## Tech Stack по языкам

### Kotlin
```
| Компонент | Технология | BANNED |
|-----------|------------|--------|
| HTTP | Ktor Client (CIO) | Retrofit, OkHttp |
| JSON | Jackson (SNAKE_CASE) | Gson |
| Assertions | Kotest shouldBe | assertEquals |
| Framework | JUnit 5 | TestNG |
```

### Python
```
| Компонент | Технология | BANNED |
|-----------|------------|--------|
| HTTP | httpx / requests | urllib |
| JSON | Pydantic | manual parsing |
| Assertions | pytest assert | unittest |
| Framework | pytest | nose |
```

### TypeScript
```
| Компонент | Технология | BANNED |
|-----------|------------|--------|
| HTTP | axios / fetch | request |
| Validation | zod | manual |
| Assertions | Jest expect | chai |
| Framework | Jest / Vitest | Mocha |
```

---

## Расположение файла

```
project-root/
└── CLAUDE.md    # В корне проекта
```

---

## Полный гайд

`docs/ai-files-handbook.md` → Часть 1: CLAUDE.md
