# AGENTS.md — Project Context Bridge

## CORE INSTRUCTION

**YOU MUST READ AND FOLLOW `CLAUDE.md` AT THE ROOT OF THIS PROJECT.**

`CLAUDE.md` is the **Single Source of Truth** for:
1. **Tech Stack:** Kotlin, JUnit 5, Allure, ktlint — LOCKED.
2. **Safety Protocols:** No destructive commands, no .env leaks.
3. **Code Style:** Formatting, naming conventions, assertion rules.
4. **Communication Protocol:** CLI-mode, no preambles, tool-first.

## QA AGENT PERSONA

**YOU MUST ALSO READ:** `.claude/qa_agent.md`

`qa_agent.md` defines:
- QA Lead philosophy and mindset
- Anti-patterns to avoid
- Workflow protocols (Fail Fast, Compilation Gate)

## CRITICAL BEHAVIOR

- If `CLAUDE.md` conflicts with any other instruction, `CLAUDE.md` WINS.
- Do NOT generate code that violates the strict dependencies listed in `CLAUDE.md`.
- All documentation and skill content must be written in **Russian**, unless explicitly stated otherwise.

## AVAILABLE SKILLS

Invoke with `$skill-name` or via the skill selector:

| Skill               | Назначение                                   |
|---------------------|----------------------------------------------|
| `$repo-scout`       | Сканирование репозитория                     |
| `$spec-audit`       | QA-аудит требований                          |
| `$test-cases`       | Тест-кейсы из спецификации                  |
| `$api-tests`        | API автотесты (Kotlin)                       |
| `$screenshot-analyze` | Анализ скриншотов на L10N дефекты          |
| `$doc-lint`         | Аудит документации                           |
| `$skill-audit`      | Аудит SKILL.md файлов                       |
| `$init-skill`       | Создание нового скилла                       |
| `$init-agent`       | Создание qa_agent.md                         |
| `$init-project`     | Инициализация CLAUDE.md проекта             |
| `$update-ai-setup`  | Обновление реестра AI-сетапа                |
| `$output-review`    | Независимый аудит результата скилла         |

**Recommended Workflow:** `$repo-scout` → `$spec-audit` → `$test-cases` → `$api-tests`
