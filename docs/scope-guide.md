# Краткая справка: Scopes для /skill-audit и /doc-lint

## Context

Скиллы `/skill-audit` и `/doc-lint` используют Read для всех файлов в scope. При большом количестве файлов это дорого.

**Проблема:** Запуск без scope читает все файлы (74 .md файла в проекте).
**Решение:** Использовать параметры `scope` и `focus`.

## Текущие изменения (git status)

По директориям:
- `.claude/qa-antipatterns/` — 14 файлов (.md)
- `.claude/skills/` — 10 SKILL.md файлов
- `.claude/references/` — 3 файла
- `docs/` — 3 файла
- Корневые: CLAUDE.md, README.md, qa_agent.md

## Рекомендованные Scopes для текущих изменений

### /skill-audit

```bash
# Все SKILL.md (10 изменённых + qa_agent.md)
/skill-audit scope=all
```
**Читает:** ~13 файлов вместо всех MD в проекте

### /doc-lint

```bash
# Antipatterns (приоритет 1: 14 файлов)
/doc-lint scope=.claude/qa-antipatterns/

# Docs (приоритет 2: 3 файла)
/doc-lint scope=docs/

# References (приоритет 3: 3 файла)
/doc-lint scope=.claude/references/
```

**Или по фазам:**
```bash
# Только дубликаты в antipatterns
/doc-lint scope=.claude/qa-antipatterns/ focus=duplicates

# Быстрая проверка размеров всех файлов (без полного чтения)
/doc-lint focus=size
```

## Экономия токенов

| Команда | Файлов | Экономия vs полный скан |
|---------|--------|-------------------------|
| `без scope` | 74 | baseline |
| `/skill-audit scope=all` | ~13 | ~82% |
| `/doc-lint scope=.claude/qa-antipatterns/` | 14 | ~81% |
| `/doc-lint scope=docs/` | 3 | ~96% |
| `/doc-lint focus=size` | 0 (только wc) | ~98% |
