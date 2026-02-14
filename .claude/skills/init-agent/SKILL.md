---
name: init-agent
description: Генерирует qa_agent.md — должностную инструкцию для AI с культурой QA-команды, принципами и anti-patterns. Используй при настройке AI под проект, онбординге новых AI-агентов или стандартизации подходов к тестированию. Не используй для редактирования существующего qa_agent.md — правь вручную.
allowed-tools: "Read Write Edit Glob Grep"
---

# /init-agent — Генератор qa_agent.md

<purpose>
Создание "должностной инструкции" для AI: mindset, anti-patterns, quality gates.
Фокус: QA-инженеры широкого профиля (API, UI, Mobile, Performance).
</purpose>

## Когда использовать

- Настройка AI под культуру конкретной QA-команды
- Стандартизация подходов к тестированию
- Онбординг новых AI-агентов в проект

## Алгоритм выполнения

### Шаг 1: Определи профиль QA

Спроси пользователя или определи по проекту:

```
Какой тип тестирования преобладает?

1. API Testing — REST/GraphQL, контрактное тестирование
2. UI/E2E Testing — Web (Playwright/Selenium), Mobile (Appium)
3. Performance Testing — нагрузочное, стресс-тестирование
4. Security Testing — OWASP, пентест
5. Mixed — комбинация нескольких типов
```

### Шаг 2: Выбери Core Principles

По профилю выбери 3-5 принципов:

#### Универсальные (для всех профилей)
```markdown
1. **Trust No One** — проверяй требования на противоречия
2. **Isolation First** — тесты не зависят друг от друга
3. **Cleanup Always** — удаляй созданные данные
4. **Fail Fast** — падай рано, падай громко
5. **Evidence-Based** — каждый баг с доказательствами
```

#### API Testing
```markdown
1. **Contract First** — тест проверяет контракт, не реализацию
2. **Boundary Obsession** — граничные значения важнее happy path
3. **Negative > Positive** — негативных сценариев больше
4. **Idempotency Check** — повторный запрос = тот же результат
```

#### UI/E2E Testing
```markdown
1. **User Perspective** — думай как пользователь
2. **Stable Selectors** — data-testid лучше CSS классов
3. **Visual Regression** — скриншоты критичных экранов
4. **Flaky = Bug** — нестабильный тест — это баг теста
```

#### Performance Testing
```markdown
1. **Baseline First** — сначала измерь, потом оптимизируй
2. **Realistic Load** — профиль нагрузки из продакшена
3. **Percentiles > Average** — p95/p99 важнее среднего
4. **Resource Monitoring** — CPU/RAM/IO во время теста
```

#### Security Testing
```markdown
1. **OWASP Top 10** — минимальный чек-лист
2. **AuthZ ≠ AuthN** — авторизация и аутентификация разные вещи
3. **Trust Nothing** — все входные данные потенциально вредоносны
4. **Least Privilege** — минимум прав для работы
```

### Шаг 3: Собери Anti-Patterns

Выбери релевантные по стеку:

```markdown
## Anti-Patterns (BANNED)

| ❌ Плохо | ✅ Хорошо | Почему |
|----------|-----------|--------|
| Thread.sleep() | Polling с таймаутом | Flaky тесты |
| Хардкод ID/email | UUID/timestamp генерация | Коллизии |
| Общие тестовые данные | Изолированные данные | Зависимости |
| Игнорировать cleanup | try-finally | Мусор в системе |
| assertEquals | shouldBe (Kotest) | Читаемость |
| var вместо val | val везде | Иммутабельность |
| Catch без обработки | Логирование + rethrow | Потеря информации |
| Магические числа | Именованные константы | Понятность |
```

### Шаг 4: Определи Quality Gates

```markdown
## Quality Gates

### Перед коммитом
- [ ] Код компилируется без ошибок
- [ ] Тесты проходят локально
- [ ] Нет хардкода (ID, email, телефоны)
- [ ] Cleanup работает

### Перед PR
- [ ] Тесты изолированы (можно запустить в любом порядке)
- [ ] Naming convention соблюдён
- [ ] Нет закомментированного кода
- [ ] Покрытие критичных сценариев

### Перед релизом
- [ ] Все тесты зелёные в CI
- [ ] Нет flaky тестов
- [ ] Performance baseline не деградировал
```

### Шаг 5: Генерация qa_agent.md

```markdown
# QA Agent: [Профиль]

## Core Mindset

[3-5 принципов из Шага 2]

## Anti-Patterns (BANNED)

[Таблица из Шага 3]

## Quality Gates

[Чек-листы из Шага 4]

## Test Design

### Структура теста (AAA)
```[язык]
// Arrange — подготовка
// Act — действие
// Assert — проверка
```

### Naming Convention
```
`[actor] can [action] when [condition]`
`[actor] cannot [action] when [condition]`
```

## Data Management

### Генерация уникальных данных
[Пример для выбранного языка]

### Cleanup Pattern
[Пример try-finally для выбранного языка]

## Cross-Skill Protocol

1. `/analyze` → проверь требования
2. `/testcases` → напиши тест-кейсы
3. `/api-tests` → автоматизируй

**Не прыгай сразу к коду!**
```

## Вывод

Сохрани в `.claude/qa_agent.md`

## Пример диалога

```
User: /init-agent

AI: Какой профиль QA-инженера?
1. API Testing
2. UI/E2E Testing
3. Performance Testing
4. Security Testing
5. Mixed (универсальный)

User: 5

AI: Генерирую qa_agent.md для универсального QA...

[Показывает файл с принципами из всех категорий]

Сохранить в .claude/qa_agent.md? (y/n)
```

## Self-Check (перед сохранением)

- [ ] **Core Principles:** 3-5 принципов выбраны и заполнены?
- [ ] **Anti-Patterns:** Таблица содержит минимум 5 записей?
- [ ] **Quality Gates:** Все 3 секции (коммит/PR/релиз) присутствуют?
- [ ] **No placeholders:** Нет оставшихся `[xxx]` в тексте?
- [ ] **Cross-Skill Protocol:** Секция присутствует с `/analyze` → `/testcases` → `/api-tests`?

## Связанные файлы

- Шаблон: `.claude/references/qa-agent-template.md`
- Полный гайд: `docs/ai-files-handbook.md`
