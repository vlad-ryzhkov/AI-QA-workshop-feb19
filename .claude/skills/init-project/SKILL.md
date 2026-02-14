---
name: init-project
description: Генерирует CLAUDE.md для QA-проекта: сканирует репозиторий, анализирует tech stack, создаёт онбординг-документ. Используй для нового QA-проекта без CLAUDE.md или настройки AI-assisted workflow. Не используй если CLAUDE.md уже настроен — редактируй вручную.
allowed-tools: "Read Write Edit Glob Grep Bash(ls*)"
---

# /init-project — Генератор CLAUDE.md

<purpose>
Автоматическое создание CLAUDE.md (онбординг AI в проект) на основе анализа репозитория.
Фокус: QA-проекты (API тесты, UI тесты, нагрузочное тестирование).
</purpose>

## Когда использовать

- Новый QA-проект без CLAUDE.md
- Миграция существующего проекта на AI-assisted workflow
- Стандартизация CLAUDE.md по команде

## Алгоритм выполнения

### Шаг 1: Сканирование проекта

Найди и проанализируй:

```
1. Build файлы:
   - build.gradle.kts / build.gradle → Kotlin/Java + зависимости
   - pom.xml → Maven + зависимости
   - package.json → Node.js + зависимости
   - requirements.txt / pyproject.toml → Python + зависимости

2. Структуру тестов:
   - src/test/ → стандартная JVM структура
   - tests/ → Python/JS структура
   - __tests__/ → Jest структура

3. Конфигурации:
   - allure.properties → Allure reporting
   - pytest.ini → pytest конфиг
   - jest.config.js → Jest конфиг

4. CI/CD:
   - .github/workflows/ → GitHub Actions
   - .gitlab-ci.yml → GitLab CI
   - Jenkinsfile → Jenkins
```

### Шаг 2: Определение Tech Stack

На основе зависимостей определи:

| Категория | Что искать | BANNED альтернативы |
|-----------|------------|---------------------|
| HTTP Client | ktor-client, requests, axios | retrofit, okhttp, urllib |
| Serialization | jackson, pydantic, zod | gson, moshi |
| Assertions | kotest, pytest, jest | junit assertEquals, unittest |
| Test Framework | junit5, pytest, jest | testng, nose |
| Reporting | allure | — |

### Шаг 3: Генерация CLAUDE.md

Используй шаблон:

```markdown
# [Название из git remote или папки]

## Context
- **Project:** [Тип: API Tests / E2E Tests / Load Tests] для [сервис]
- **Role:** Senior QA Automation Engineer
- **Language:** [Kotlin/Python/TypeScript]

## Tech Stack (LOCKED)

| Компонент | Технология | BANNED |
|-----------|------------|--------|
| HTTP Client | [найденный] | [альтернативы] |
| Serialization | [найденный] | [альтернативы] |
| Assertions | [найденный] | [альтернативы] |
| Test Framework | [найденный] | [альтернативы] |
| Reporting | [найденный] | — |

## Project Structure

```text
[Реальная структура из проекта]
```

## Commands

| Действие | Команда |
|----------|---------|
| Build | [из build файла] |
| Test | [из build файла] |
| Single test | [специфичная для фреймворка] |

## Safety Protocols

⛔ **FORBIDDEN:** `git reset --hard`, `git clean -fd`, удаление веток
✅ **MANDATORY:** Backup перед деструктивными операциями
⚠️ **OVERRIDE:** Требуется слово **DESTROY** от пользователя

## Token Economy

- PAUSE при задаче > 20,000 токенов
- Полное сканирование только по keyword **FULL_SCAN**

## Workflow

Для задач > 3 файлов: Analysis → Plan → Execute → Verify

## QA Skills

| Skill | Назначение |
|-------|------------|
| `/analyze` | QA-аудит требований |
| `/testcases` | Ручные тест-кейсы |
| `/api-tests` | API автотесты |

**Workflow:** `/analyze` → `/testcases` → `/api-tests`
```

### Шаг 4: Валидация

Перед сохранением проверь:

- [ ] Tech Stack соответствует реальным зависимостям
- [ ] Commands работают (проверь наличие gradlew/npm/pytest)
- [ ] Structure отражает реальные папки
- [ ] Нет placeholder'ов вида `[xxx]`

## Вывод

Сохрани результат в `CLAUDE.md` в корне проекта.

## Пример диалога

```
User: /init-project

AI: Сканирую проект...

Найдено:
- build.gradle.kts → Kotlin 1.9
- Зависимости: ktor-client, jackson, kotest, allure
- Структура: src/test/kotlin/
- CI: .github/workflows/test.yml

Генерирую CLAUDE.md...

[Показывает сгенерированный файл]

Сохранить в ./CLAUDE.md? (y/n)
```

## Связанные файлы

- Шаблон: `.claude/references/claude-md-template.md`
- Полный гайд: `docs/ai-files-handbook.md`
- Примеры: существующий `CLAUDE.md` в проекте (если есть)
