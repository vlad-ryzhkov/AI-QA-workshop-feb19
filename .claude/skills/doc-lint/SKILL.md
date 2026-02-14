---
name: doc-lint
description: Аудит качества документации — размер, структура, дубликаты между файлами, нарушения SSOT. Используй для контроля качества human-readable файлов, поиска дублирования и проверки структуры. Не используй для code review или анализа исходного кода.
allowed-tools: "Read Write Edit Glob Grep Bash(wc*)"
---

# /doc-lint — Аудит качества документации

<purpose>
Сканирует все human-readable файлы проекта, находит проблемы с размером, структурой, дубликатами между файлами и нарушения SSOT (Single Source of Truth). Генерирует отчёт с приоритизированными findings и планом рефакторинга.
</purpose>

## Когда использовать

- После добавления нового документа или skill
- При подозрении на дублирование контента между файлами
- Для периодического аудита документации (раз в спринт)
- Перед рефакторингом документации

## Входные данные

| Параметр | Обязательность | Описание |
|----------|:--------------:|----------|
| Scope | Опционально | Конкретные файлы/директории. По умолчанию — весь проект |
| Focus | Опционально | Только определённые фазы (size, structure, duplicates) |

---

## Алгоритм (6 фаз)

### Фаза 1: Discovery & File Inventory

**Цель:** Собрать полный каталог human-readable файлов.

1. Glob: `**/*.md`, `**/*.yaml`, `**/*.yml`, `**/*.txt`
2. **Исключения:** `node_modules/`, `.gradle/`, `build/`, `*.lock`, `*.bin`, `vendor/`
3. Для каждого файла определить:
   - Path (относительный)
   - Line count (`wc -l`)
   - Type classification (по правилам из `references/check-rules.md` § 1)
4. Сформировать таблицу-инвентарь:

```markdown
| # | Файл | Строк | Тип | Status |
|---|------|------:|-----|--------|
| 1 | CLAUDE.md | 107 | CLAUDE.md | — |
```

**Checkpoint:** Все файлы в scope найдены, line counts верифицированы.

---

### Фаза 2: Size Analysis

**Цель:** Определить файлы, превышающие пороги.

1. Загрузить пороги из `references/check-rules.md` § 1
2. Для каждого файла из инвентаря:
   - Определить applicable threshold по типу файла
   - Сравнить line count с порогами
   - Присвоить severity: **OK** / **WARNING** / **CRITICAL**
3. Обновить колонку Status в инвентаре

**Формула:**
```
Если lines > CRITICAL threshold → CRITICAL
Если lines > WARNING threshold → WARNING
Иначе → OK
```

---

### Фаза 3: Structure Analysis

**Цель:** Проверить внутреннюю структуру каждого файла.

Для каждого .md файла:

**3.1 Heading Hierarchy**
- Извлечь все заголовки (`# `, `## `, `### `, ...)
- Проверить на пропуски уровней: H1→H3 (минуя H2) → **CRITICAL**
- Проверить глубину: >H4 → **INFO** "Consider restructuring"

**3.2 Section Balance**
- Посчитать строки между заголовками
- Если одна секция > 40% от всего файла → **WARNING**

**3.3 Empty Sections**
- Заголовок → следующий заголовок без контента (только whitespace) → **WARNING**

**3.4 TOC Check**
- Файл >200 строк без `## Table of Contents`, `## Содержание`, `## TOC` → **INFO**

**3.5 Readability**
- Wall-of-text: >20 строк подряд без заголовков/списков/пустых строк/code blocks → **WARNING**
- Строки >200 символов → **INFO**

---

### Фаза 4: Cross-File Duplicate Detection

**Цель:** Найти дублирование контента между файлами. Ключевая фаза.

#### 4.1 Block Extraction

Для каждого файла извлечь семантические блоки:
- Таблицы (от `|` до конца таблицы)
- Code blocks (от ``` до ```)
- Списки (последовательные строки с `- `, `* `, `1. `)
- Параграфы (>3 строк подряд)

#### 4.2 Known Pattern Matching (быстрый проход)

Загрузить паттерны из `references/check-rules.md` § 2.
Для каждого паттерна KP-1..KP-5:

1. Grep по сигнатуре
2. Собрать файлы с совпадением
3. Если файлов ≥2 → зафиксировать кластер дубликатов

#### 4.3 Heuristic Cross-Comparison

Для таблиц:
1. Сравнить header rows (строки с `|`)
2. Если headers совпадают >70% → сравнить содержимое
3. Содержимое совпадает >70% → **WARNING** near-duplicate

Для code blocks и списков:
1. Нормализовать по правилам из `references/check-rules.md` § 5
2. Exact match ≥5 строк → **CRITICAL**
3. Exact match 3-5 строк → **WARNING**

#### 4.4 Intra-file Duplicates

Внутри одного файла:
- Повторяющиеся секции (одинаковые заголовки + похожий контент)
- Повторяющиеся таблицы
- Copy-paste параграфы

#### 4.5 SSOT Owner Assignment

Для каждого кластера дубликатов:
1. Определить категорию контента по `references/check-rules.md` § 3
2. Назначить SSOT Owner
3. Сформировать рекомендацию: "Оставить в {Owner}, остальные заменить ссылкой"

---

### Фаза 5: Content Hygiene

**Цель:** Найти проблемы с содержимым.

**5.1 Markers**
- `TODO`, `FIXME`, `HACK`, `XXX`, `TEMP` → **INFO**

**5.2 Broken Internal Links**
- Найти все `[text](path)` где path — относительный путь
- Проверить существование файла → не существует → **CRITICAL**
- Пустые ссылки `[text]()` или `[](path)` → **WARNING**

**5.3 Stale Dates**
- Даты в формате YYYY-MM-DD старше 6 месяцев от текущей даты → **INFO** "Potentially stale"

**5.4 Diataxis Type Mix**
- Загрузить маркеры из `references/check-rules.md` § 4
- Если файл содержит маркеры ≥2 типов → **INFO**

---

### Фаза 6: Report Generation

**Цель:** Собрать все findings в структурированный отчёт.

---

## Severity Model

| Severity | Критерии | Действие |
|----------|----------|----------|
| **CRITICAL** | >700 строк generic; SKILL.md >500; CLAUDE.md/qa_agent.md >300; cross-file exact duplicate >5 строк; пропуск уровня заголовка; битая ссылка | Fix before next commit |
| **WARNING** | >500 строк generic; intra-file duplicate; wall-of-text >20; пустая секция; секция >40%; near-duplicate cross-file; пустая ссылка | Fix in next iteration |
| **INFO** | >400 строк generic; нет TOC >200; TODO маркеры; mixed Diataxis; stale dates >6mo; depth >H4; длинные строки >200 | Track in backlog |

---

## Health Score

```
Score = 100 - (CRITICAL × 15) - (WARNING × 5) - (INFO × 1)
```

| Диапазон | Оценка | Интерпретация |
|----------|--------|---------------|
| 90-100 | Excellent | Документация в отличном состоянии |
| 70-89 | Good | Есть незначительные проблемы |
| 50-69 | Needs attention | Требуется рефакторинг |
| <50 | Refactoring needed | Срочный рефакторинг документации |

**Формула должна быть показана с подстановкой значений:**
```
Score = 100 - (2 × 15) - (5 × 5) - (8 × 1) = 100 - 30 - 25 - 8 = 37/100
```

---

## Формат вывода

### Артефакт: `audit/doc-lint-report.md`

```markdown
# Doc-Lint Report

> Дата: {YYYY-MM-DD}
> Scope: {описание scope}
> Health Score: {N}/100 ({оценка})

## Summary

| Метрика | Значение |
|---------|----------|
| Файлов просканировано | N |
| CRITICAL | N |
| WARNING | N |
| INFO | N |
| Health Score | N/100 |
| Кластеров дубликатов | N |

## File Inventory

| # | Файл | Строк | Тип | Size Status |
|---|------|------:|-----|-------------|
| 1 | ... | ... | ... | OK/WARNING/CRITICAL |

## CRITICAL Findings

| # | Файл | Фаза | Описание | Рекомендация |
|---|------|------|----------|--------------|

## WARNING Findings

| # | Файл | Фаза | Описание | Рекомендация |
|---|------|------|----------|--------------|

## INFO Findings

| # | Файл | Фаза | Описание | Рекомендация |
|---|------|------|----------|--------------|

## Duplicate Map

### Кластер D-1: {название паттерна}
- **Тип:** Exact / Near-duplicate / Conceptual
- **SSOT Owner:** {файл}
- **Найдено в:** {список файлов с номерами строк}
- **Рекомендация:** Оставить в {Owner}, в остальных заменить ссылкой

### Кластер D-N: ...

## SSOT Refactoring Plan

| # | Действие | Файл | Что сделать |
|---|----------|------|-------------|
| 1 | REMOVE | file.md:10-25 | Удалить копию Tech Stack, добавить ссылку |

## Statistics

- Общий объём документации: {N} строк в {M} файлах
- Средний размер файла: {N/M} строк
- Файлов в пределах нормы: {X}/{M} = {%}
- Health Score: {формула с подстановкой}
```

### Self-Review: `audit/doc-lint-report_self_review.md`

Формат Self-Review — аналогично `/analyze` (см. qa_agent.md § Skill Completion Protocol).

**Scorecard Self-Review:**

```markdown
## Scorecard

| Критерий | Результат |
|----------|-----------|
| Все файлы просканированы | X/Y = NN% |
| Line counts верифицированы | ✅/❌ |
| Cross-file detection выполнен | ✅/❌ |
| Каждый finding имеет severity + рекомендацию | X/Y = NN% |
| Нет placeholder {xxx} | ✅/❌ |
| SSOT owner назначен для каждого кластера | X/Y = NN% |
| Формулы с числителем/знаменателем | ✅/❌ |

### Итоговый Score: NN%
```

---

## Quality Gates

- [ ] Все файлы в scope отсканированы (Glob + count verification)
- [ ] Line counts верифицированы через `wc -l`
- [ ] Cross-file duplicate detection выполнен (Фаза 4)
- [ ] Каждый finding имеет severity + рекомендацию
- [ ] Нет placeholder `{xxx}` в отчёте
- [ ] SSOT owner назначен для каждого кластера дубликатов
- [ ] Формулы показаны с числителем и знаменателем (CLAUDE.md requirement)
- [ ] Health Score рассчитан и показан с подстановкой

---

## Anti-Patterns (BANNED)

### False Positive на одинаковых заголовках

```
❌ Flagging таблиц с одинаковыми заголовками но разными данными как "duplicate"
✅ Сравнивать содержимое ячеек, не только headers
```

### Phantom Findings

```
❌ Генерировать findings на основе предположений без чтения файла
✅ Каждый finding подтверждён содержимым файла (строка, фрагмент)
```

### Missing Context

```
❌ "File is too long"
✅ "CLAUDE.md: 305 строк > порог CRITICAL (300). Рекомендация: вынести секцию X в qa_agent.md"
```

### Over-flagging Intentional Repetition

```
❌ Flagging ссылок на паттерны как дубликатов (ссылки — не дубликаты)
✅ Отличать полное копирование от ссылок и краткого упоминания
```

---

## Связанные файлы

| Файл | Содержание |
|------|------------|
| `references/check-rules.md` | Пороги размеров, сигнатуры дубликатов, SSOT-матрица, Diataxis-маркеры |
| `references/best-practices.md` | Корпоративные практики: Google, Amazon, Diataxis, Microsoft, GitLab, Stripe |

---

## Завершение

После создания отчёта и self-review — напечатай блок `SKILL COMPLETE` (формат в qa_agent.md).

```
✅ SKILL COMPLETE: /doc-lint
├─ Артефакты: audit/doc-lint-report.md
├─ Self-Review: audit/doc-lint-report_self_review.md
├─ Compilation: N/A
├─ Upstream: нет
└─ Score: {итоговый % из self-review scorecard}
```
