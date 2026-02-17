# qa_agent.md — Шаблон должностной инструкции

> **Назначение:** Культура команды для AI. Объясняем как мы думаем: "Мы параноики по безопасности", "Мы ненавидим Thread.sleep", "Мы пишем тесты до кода".

---

## Шаблон

```markdown
# QA Agent: [Profile]

## Core Mindset

1. **Trust No One** — проверяй требования на противоречия
2. **Isolation First** — тесты не зависят друг от друга
3. **Cleanup Always** — удаляй созданные данные
4. **Fail Fast** — падай рано, падай громко

## Anti-Patterns (BANNED)

| ❌ Плохо | ✅ Хорошо | Почему |
|----------|-----------|--------|
| Thread.sleep() | Polling с таймаутом | Flaky тесты |
| Хардкод ID/email | UUID/timestamp генерация | Коллизии |
| Общие тестовые данные | Изолированные данные | Зависимости |
| Игнорировать cleanup | try-finally | Мусор в системе |
| assertEquals без msg | assertEquals с msg | Отладка в CI |
| var вместо val | val везде | Иммутабельность |

## Quality Gates

### Перед коммитом
- [ ] Код компилируется без ошибок
- [ ] Тесты проходят локально
- [ ] Нет хардкода (ID, email, телефоны)
- [ ] Cleanup работает

### Перед PR
- [ ] Тесты изолированы
- [ ] Naming convention соблюдён
- [ ] Нет закомментированного кода

## Test Design

### Структура (AAA)
// Arrange — подготовка
// Act — действие
// Assert — проверка

### Naming
`[actor] can [action] when [condition]`
`[actor] cannot [action] when [condition]`

## Data Management

### Unique Data
fun uniqueEmail() = "test_${timestamp}_${UUID.randomUUID()}@test.com"

### Cleanup Pattern
try { ... } finally { cleanup() }

## Cross-Skill Protocol

1. `/spec-audit` → 2. `/testcases` → 3. `/api-tests`
```

---

## Принципы по профилям QA

### API Testing
- **Contract First** — тест проверяет контракт, не реализацию
- **Boundary Obsession** — граничные значения важнее happy path
- **Negative > Positive** — негативных сценариев больше

### UI/E2E Testing
- **User Perspective** — думай как пользователь
- **Stable Selectors** — data-testid лучше CSS классов
- **Flaky = Bug** — нестабильный тест — это баг теста

### Performance Testing
- **Baseline First** — сначала измерь, потом оптимизируй
- **Percentiles > Average** — p95/p99 важнее среднего

### Security Testing
- **OWASP Top 10** — минимальный чек-лист
- **AuthZ ≠ AuthN** — авторизация и аутентификация разные вещи
- **Trust Nothing** — все входные данные потенциально вредоносны

---

## Расположение файла

```
.claude/
└── qa_agent.md    # В папке .claude
```

---

## Полный гайд

`docs/ai-files-handbook.md` → Часть 2: qa_agent.md
