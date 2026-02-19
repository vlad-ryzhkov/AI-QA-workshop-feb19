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
| HTTP Client | [common-test-libs ApiClient/requests/axios] | [alternatives] |
| Serialization | [Jackson/Pydantic/zod] | [alternatives] |
| Assertions | [assertEquals с message/pytest/Jest] | [alternatives] |
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

<!-- СЕКЦИЯ: Architecture — только для infra/backend-проектов (нет src/test/) -->
## Architecture

[Нарратив о key design decisions: компоненты, схема взаимодействия, нетривиальные конфигурации]

<!-- СЕКЦИЯ: Key Values — если есть нетривиальные дефолты в values.yaml / application.yml / .env -->
## Key Values

### [Подсекция по компоненту]
- `[ключ]` — [что делает, почему важно]

<!-- СЕКЦИЯ: CI/CD Flow — если найдены .github/workflows/, .gitlab-ci.yml, Jenkinsfile -->
## CI/CD Flow

```
[pipeline диаграмма: шаг → шаг → шаг]
```

<!-- СЕКЦИЯ: QA Skills — только если в проекте существует .claude/skills/ -->
## QA Skills

| Skill | Назначение |
|-------|------------|
| `/spec-audit` | QA-аудит требований |
| `/test-cases` | Тест-кейсы из спецификации |
| `/api-tests` | API автотесты |

**Workflow:** `/spec-audit` → `/test-cases` → `/api-tests`
```

---

## Tech Stack по языкам

### Kotlin
```
| Компонент | Технология | BANNED |
|-----------|------------|--------|
| HTTP | common-test-libs ApiClient + ApiRequestBaseJson<T> | Custom HTTP wrappers |
| JSON | Jackson (SNAKE_CASE) | Gson |
| Assertions | assertEquals с message + Hamcrest checkAll | Assertions без message |
| Polling | Awaitility (await.atMost().until {}) | Thread.sleep(), delay() |
| Framework | JUnit 5 | TestNG |
| Code Style | ktlint | — |
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

### Infrastructure (Helm / Kubernetes / Terraform)
```
| Компонент | Технология | BANNED |
|-----------|------------|--------|
| Package Manager | Helm 3 | Kustomize, raw kubectl apply |
| Service Mesh | Istio / Linkerd | — |
| IaC | Terraform | manual kubectl |
| Registry | OCI (Harbor) | Docker Hub (prod) |
| Lint | helm lint . / tflint | — |
| CI/CD | GitHub Actions / GitLab CI | — |
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
