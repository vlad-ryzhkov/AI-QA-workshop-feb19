# YAML Frontmatter Reference

## Обязательные поля

### name
- **Формат:** kebab-case
- **Ограничения:**
  - Только строчные буквы, цифры, дефисы
  - Должно совпадать с именем папки скилла
  - Без префиксов "claude", "anthropic"
  - Уникально в пределах проекта
- **Примеры:**
  - ✅ `test-plan`, `api-tests`, `screenshot-analyze`
  - ❌ `TestPlan`, `api_tests`, `claude-helper`

### description
- **Формат:** `[Что делает]. [Когда использовать]. [Когда НЕ использовать]`
- **Ограничения:**
  - Максимум 1024 символа
  - Без XML тегов (<>, &lt;, &gt;)
  - Без переносов строк (однострочный)
  - Используй trigger-фразы из примеров использования
- **Структура:**
  1. Что делает (1-2 предложения)
  2. Когда использовать (конкретные сценарии)
  3. Когда НЕ использовать (anti-use-cases)
- **Примеры:**
  - ✅ `Генерирует тест-кейсы из спецификации API. Используй после /spec-audit для покрытия endpoints тестами. Не используй для UI тестирования.`
  - ❌ `Полезный инструмент для тестирования` (слишком общее)

## Опциональные поля

### allowed-tools
- **Формат:** Строка с перечислением через пробел
- **Примеры:**
  - `"Read Write Edit Glob Grep"`
  - `"Read Write Bash(wc*) Bash(git*)"`
- **Wildcards:** Bash команды можно ограничить паттерном: `Bash(ls*)` разрешает только `ls`

### agent
- **Формат:** Путь к файлу агента относительно `.claude/`
- **Пример:** `agents/sdet.md`, `agents/auditor.md`

### context
- **Варианты:**
  - `fork` — изолированный контекст (Process Isolation)
  - `inherit` — унаследованный контекст (по умолчанию)

## Примеры готовых YAML

### Analysis Skill
```yaml
---
name: spec-audit
description: Аудит OpenAPI/Proto спецификации на полноту и корректность. Используй перед /testcases для валидации endpoints. Не используй для code review.
allowed-tools: "Read Write Edit Glob Grep"
agent: agents/auditor.md
context: fork
---
```

### Generation Skill
```yaml
---
name: api-tests
description: Генерирует Kotlin автотесты из тест-кейсов. Используй после /testcases для автоматизации. Не используй без готовых test cases.
allowed-tools: "Read Write Edit Glob Grep Bash"
agent: agents/sdet.md
context: fork
---
```

### Validation Skill
```yaml
---
name: lint-tests
description: Проверяет автотесты на соответствие стандартам. Используй после /api-tests для контроля качества. Не используй для production кода.
allowed-tools: "Read Glob Grep"
agent: agents/auditor.md
context: fork
---
```

## Валидация

После написания YAML проверь:
- [ ] `name` = имя директории скилла
- [ ] `description` содержит все 3 части (что/когда/не когда)
- [ ] `description` < 1024 символов
- [ ] Нет XML символов в `description`
- [ ] YAML синтаксически корректен (triple-dash начало и конец)
