# AI QA Workshop — Agent Instructions

> Этот файл — pointer для IDE, поддерживающих `AGENTS.md` (Zed, Cline, Continue.dev и др.).
> Единственный источник истины: [`CLAUDE.md`](CLAUDE.md).

## Ключевые файлы

| Файл | Назначение |
|------|------------|
| [`CLAUDE.md`](CLAUDE.md) | Полный контекст проекта: стек, безопасность, конвенции |
| [`.claude/qa_agent.md`](.claude/qa_agent.md) | QA Mindset, анти-паттерны, протоколы |

## Skills

| Skill | Файл | Назначение |
|-------|------|------------|
| `/analyze` | [`.claude/skills/requirements-analysis/SKILL.md`](.claude/skills/requirements-analysis/SKILL.md) | QA-аудит требований |
| `/testcases` | [`.claude/skills/testcases/SKILL.md`](.claude/skills/testcases/SKILL.md) | Мануальные тест-кейсы (Kotlin DSL) |
| `/api-tests` | [`.claude/skills/api-tests/SKILL.md`](.claude/skills/api-tests/SKILL.md) | API автотесты (Ktor + Kotest) |
| `/screenshot-analyze` | [`.claude/skills/screenshot-analyze/SKILL.md`](.claude/skills/screenshot-analyze/SKILL.md) | L10N и UI дефекты |

## Правила

1. **Tech Stack LOCKED:** Ktor Client, Jackson (SNAKE_CASE), Kotest (`shouldBe`), JUnit 5, Allure. Запрещены: Retrofit, OkHttp, Gson, Moshi, TestNG.
2. **Документация на русском** — если явно не указано иное.
3. **Safety:** `git reset --hard`, `git clean -fd`, `rm -rf .git` — запрещены без явного подтверждения.
4. **Compilation Gate:** `./gradlew compileTestKotlin` перед коммитом автотестов.
5. **Workflow:** `/analyze` -> `/testcases` -> `/api-tests` (последовательно).
