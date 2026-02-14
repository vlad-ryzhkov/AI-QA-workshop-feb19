# AI QA Workshop — Copilot Instructions

## Context
- **Проект:** Mobile/Backend QA Automation Workshop
- **Роль:** Senior QA Automation Engineer
- **Языки:** Kotlin, Markdown
- **Документация:** на русском языке

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
4. **Security First** — любое поле ввода = потенциальная SQL Injection, XSS, IDOR
5. **Complete Coverage** — каждый метод TestData используется минимум в 1 тесте

## Anti-Patterns (BANNED)

| Проблема | Что делать вместо |
|----------|-------------------|
| `Thread.sleep()` в тестах | Polling с таймаутом |
| `Map<String, Any>` для API | Типизированные DTO с `@JsonNaming(SnakeCaseStrategy::class)` |
| HTTP-вызовы прямо в тесте | Слой Client (абстракция) |
| PII в тестовых данных | Faker или маскированные значения |
| Assertion без сообщения | `shouldBe` с описанием контекста |

## Skills (инструкции для задач)

| Skill | Файл | Назначение |
|-------|------|------------|
| `/analyze` | `.claude/skills/analyze/SKILL.md` | QA-аудит требований |
| `/testcases` | `.claude/skills/testcases/SKILL.md` | Мануальные тест-кейсы (Kotlin DSL) |
| `/api-tests` | `.claude/skills/api-tests/SKILL.md` | API автотесты (Ktor + Kotest) |
| `/screenshot-analyze` | `.claude/skills/screenshot-analyze/SKILL.md` | L10N и UI дефекты |

**Workflow:** `/analyze` -> `/testcases` -> `/api-tests`

> VS Code Copilot нативно читает `.claude/skills/` — skills доступны автоматически.
> IntelliJ Copilot: откройте нужный SKILL.md в редакторе для контекста.

## Safety Protocols

- **FORBIDDEN:** `git reset --hard`, `git clean -fd`, удаление веток, `rm -rf .git`
- **MANDATORY:** Backup перед деструктивными операциями
- Подтверждай ветку перед `git push`
- `./gradlew compileTestKotlin` перед коммитом автотестов

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
