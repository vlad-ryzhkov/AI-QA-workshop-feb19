# AI QA Workshop — Copilot Instructions

# INSTRUCTIONS FOR GITHUB COPILOT

ALWAYS prioritize the context defined in `CLAUDE.md` and `.claude/qa_agent.md`.
If the user asks for a specific task (like "analyze" or "test"), **YOU MUST** ask them to open the corresponding SKILL file if it is not already in the context.

## Context
- **Проект:** Mobile/Backend QA Automation Workshop
- **Роль:** Senior QA Automation Engineer
- **Языки:** Kotlin, Markdown
- **Документация:** на русском языке

> **Tech Stack, Core Principles, Safety Protocols:** см. `CLAUDE.md` в корне репозитория (SSOT)

## Anti-Patterns (BANNED)

| Проблема | Что делать вместо |
|----------|-------------------|
| `Thread.sleep()` в тестах | Polling с таймаутом (Awaitility) |
| `Map<String, Any>` для API | Типизированные DTO с `@JsonNaming(SnakeCaseStrategy::class)` |
| HTTP-вызовы прямо в тесте | Слой Client (абстракция) |
| PII в тестовых данных | Faker или маскированные значения |
| Assertion без сообщения | `assertEquals` с описанием контекста |

## Skills (Как применять)

GitHub Copilot не читает инструкции автоматически. Чтобы выполнить задачу:

1. **Аудит спецификации:**
   - Напиши в чат: `@workspace Прочитай .claude/skills/spec-audit/SKILL.md и выполни аудит для файла specifications/api.yaml`

2. **Генерация тестов:**
   - Открой файл `src/test/kotlin/MyTest.kt`
   - Открой файл `.claude/skills/api-tests/SKILL.md`
   - Напиши: "Сгенерируй тесты на основе открытого SKILL файла"

| Команда (alias) | Какой файл подключить в контекст | Назначение |
|-----------------|----------------------------------|------------|
| Spec Audit      | `.claude/skills/spec-audit/SKILL.md` | QA-аудит спецификации |
| Тест-кейсы      | `.claude/skills/testcases/SKILL.md` | Мануальные тест-кейсы (Kotlin DSL) |
| API Тесты       | `.claude/skills/api-tests/SKILL.md` | API автотесты (Ktor + JUnit 5) |
| Screenshot      | `.claude/skills/screenshot-analyze/SKILL.md` | L10N и UI дефекты |

**Workflow:** Аудит → Тест-кейсы → API Тесты

## Project Structure

```
CLAUDE.md                        # Полный контекст проекта (Single Source of Truth)
.claude/qa_agent.md              # Mindset + Anti-Patterns + Protocols
.claude/skills/                  # Детальные инструкции по задачам
specifications/                  # Спецификации API для анализа
src/test/testCases/              # Мануальные тесты (Kotlin DSL)
src/test/kotlin/                 # API автотесты
audit/                           # Результаты аудита требований
```

> Полный контекст проекта: см. `CLAUDE.md` в корне репозитория.
> QA-агент и анти-паттерны: см. `.claude/qa_agent.md`.
