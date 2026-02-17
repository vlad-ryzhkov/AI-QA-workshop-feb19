# Шаблон отчёта test-plan.md

```markdown
# Test Coverage Plan: {repo-name}

> Сгенерировано: {дата} | Скилл: /test-plan
> Источник: audit/repo-scout-report.md

## 1. Scope Summary

| Метрика | Значение |
|---------|----------|
| Total Endpoints | {N REST + M gRPC = total} |
| Spec Coverage | {X}/{total} = {%} |
| Existing Tests | {Y unit + Z integration + W e2e} |
| Untested Endpoints | {count} |

## 2. Specification Gaps

### Отсутствуют спеки (нужно создать/получить)

| # | Endpoint | Источник (код) | Приоритет | Action |
|---|----------|---------------|-----------|--------|

### Неполные спеки (нужно обогатить перед /spec-audit)

| # | Spec File | Проблема | Action |
|---|-----------|----------|--------|

### Рекомендация

{Что делать: попросить команду дописать, генерировать из кода, etc.}

## 3. Execution List for SDET

### P0 (Critical) - Score ≥ 7

| # | Endpoint | HTTP Method | Spec Location | Test Scenarios | Context |
|---|----------|-------------|---------------|----------------|---------|
| 1 | /api/register | POST | docs/api.yaml:L42 | 400 (validation), 409 (duplicate), 200 (success) | Auth Flow, Public |
| 2 | /api/login | POST | docs/api.yaml:L58 | 401 (bad creds), 200 (success) | Auth Flow, Public |

**Формула:** Score = (Public × 3) + (HasSpec × 2) + (NoTests × 1) + (Critical × 2) + (SecurityRisk × 1)

### P1 (Major) - Score 4-6

| # | Endpoint | HTTP Method | Spec Location | Test Scenarios | Context |
|---|----------|-------------|---------------|----------------|---------|
| 3 | /api/profile | GET | docs/api.yaml:L102 | 401 (no token), 200 (success) | Authed, User data |

### P2 (Minor) - Score 1-3

| # | Endpoint | HTTP Method | Spec Location | Test Scenarios | Context |
|---|----------|-------------|---------------|----------------|---------|
| 4 | /api/healthcheck | GET | docs/api.yaml:L15 | 200 (always) | Internal, monitoring |

**Описание колонок:**

1. **#** — порядковый номер (глобальный, не сбрасывается между P0/P1/P2)
2. **Endpoint** — путь API endpoint (e.g., `/api/register`)
3. **HTTP Method** — GET/POST/PUT/DELETE/PATCH
4. **Spec Location** — файл:строка спецификации для чтения DTOs (e.g., `docs/api.yaml:L42`)
5. **Test Scenarios** — список кейсов через запятую (e.g., `400 (validation), 200 (success)`)
6. **Context** — теги для фильтрации (e.g., `Auth Flow, Public, Security`)

## 4. AI Setup Roadmap

| # | Шаг | Скилл | Статус | Артефакт |
|---|-----|-------|--------|----------|
| 1 | Разведка репо | /repo-scout | ✅ Done | audit/repo-scout-report.md |
| 2 | План покрытия | /test-plan | ✅ Done | audit/test-plan.md |
| 3 | CLAUDE.md для тест-проекта | /init-project | {⬜ TODO / ✅} | CLAUDE.md |
| 4 | QA Agent | /init-agent | {⬜ TODO / ✅} | .claude/qa_agent.md |
| 5 | Аудит P0 спек | /spec-audit | ⬜ TODO | audit/*_audit.md |
| 6 | Тест-кейсы P0 | /testcases | ⬜ TODO | src/test/testCases/ |
| 7 | Автотесты P0 | /api-tests | ⬜ TODO | src/test/kotlin/ |

## 5. Test Architecture

### Размещение тестов

{Рекомендация: отдельный репо / в том же репо / существующий тест-репо}

### Структура тест-проекта

```text
{Предложенное дерево директорий}
```

### Tech Stack

| Компонент | Технология |
|-----------|-----------|
{Таблица стека}

## 6. Execution Roadmap

### Фаза 1: Foundation ({оценка времени})
- [ ] Создать/настроить тест-проект
- [ ] `/init-project` → CLAUDE.md
- [ ] `/init-agent` → qa_agent.md
- [ ] Настроить CI для тестов

### Фаза 2: P0 Smoke ({оценка})
Для каждого P0 endpoint:
- [ ] `/spec-audit` на спецификацию
- [ ] `/testcases` → мануальные тест-кейсы
- [ ] `/api-tests` → автотесты

### Фаза 3: P1 Core Business ({оценка})
{Аналогично Фазе 2 для P1}

### Фаза 4: P2 Edge Cases ({оценка})
{Аналогично для P2}

## 7. Risks & Blockers

| # | Risk | Impact | Mitigation |
|---|------|--------|------------|
```
