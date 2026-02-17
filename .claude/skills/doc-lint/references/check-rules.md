# Правила проверки doc-lint

## 1. Size Thresholds

| Тип файла | Рекомендовано | WARNING | CRITICAL | Обоснование |
|-----------|:------------:|:-------:|:--------:|-------------|
| SKILL.md | ≤500 | N/A | >500 | Правило из qa_agent.md |
| qa_agent.md, agents/*.md | ≤300 | >300 | >500 | System prompts — плотнее обычных docs |
| CLAUDE.md | ≤200 | >200 | >300 | Всегда в контексте = расход токенов |
| docs/*.md | ≤400 | >500 | >700 | Microsoft Docs: 200-800 ideal range |
| README.md | ≤300 | >500 | >700 | Entry point + workshop guide |
| YAML config (.yaml, .yml) | ≤200 | >300 | >500 | Конфиг, не проза |
| Generic .md (fallback) | ≤400 | >500 | >700 | Fallback для прочих markdown |

### Классификация файла

Приоритет (сверху вниз, первое совпадение):

1. Имя `SKILL.md` → SKILL.md
2. Имя `qa_agent.md` или путь содержит `agents/` → qa_agent.md/agents/*.md
3. Имя `CLAUDE.md` → CLAUDE.md
4. Имя `README.md` → README.md
5. Расширение `.yaml` или `.yml` → YAML config
6. Путь содержит `docs/` → docs/*.md
7. Расширение `.md` → Generic .md

---

## 2. Known Duplicate Signatures

Pre-registered паттерны для быстрого поиска через Grep:

| ID | Паттерн | Grep-сигнатура | Min match |
|----|---------|----------------|-----------|
| KP-1 | Tech Stack (LOCKED) | `Компонент.*Технология.*BANNED` | 1 строка |
| KP-2 | Progressive Disclosure | `Уровень 1.*YAML` или `Уровень 1.*Level 1` | 1 строка |
| KP-3 | Core Principles | `Trust No One` + `Production Ready` + `Safety` | 3 строки в пределах 10 строк |
| KP-4 | Skill Size Limit | `500 строк` или `≤500` в контексте skill | 1 строка |
| KP-5 | Safety Protocols | `FORBIDDEN` + `MANDATORY` + `OVERRIDE` в пределах 10 строк | 3 строки |

### Правило KP-match

Файл считается содержащим паттерн, если найдены ВСЕ строки из колонки "Min match".
Дубликат = паттерн найден в ≥2 файлах.

---

## 3. SSOT Ownership Matrix

| Категория контента | SSOT Owner | Обоснование |
|--------------------|------------|-------------|
| Tech Stack, Safety, Conventions | `CLAUDE.md` | Всегда в контексте, минимум дублирования |
| Mindset, Anti-Patterns, Protocols | `qa_agent.md` | Идентичность агента |
| Правила авторинга скиллов | `qa_agent.md` | Общее для всех скиллов |
| Алгоритм конкретного скилла | `SKILL.md` | Scoped context |
| Туториалы, гайды | `docs/*.md` | Документационный слой |
| Обзор проекта | `README.md` | Точка входа |

### Правило SSOT

Если контент из категории X найден вне SSOT Owner — это WARNING (near-duplicate) или CRITICAL (exact duplicate >5 строк). Рекомендация: заменить на ссылку `→ см. {SSOT Owner}`.

---

## 4. Diataxis Type Detection

Маркеры для определения типа документа:

| Тип | Маркеры | Примеры |
|-----|---------|---------|
| **Tutorial** | "шаг 1", "step 1", "давайте создадим", "let's create", пошаговые инструкции с нарастающей сложностью | Workshop guides |
| **How-to** | "как сделать", "how to", "чтобы X, сделайте Y", целевые рецепты | Troubleshooting |
| **Reference** | таблицы параметров, API signatures, enum values, чисто факты без нарратива | API docs, config refs |
| **Explanation** | "почему", "зачем", "архитектура", "принцип", концептуальные объяснения | Architecture docs |

### Правило Diataxis

Один файл содержит маркеры ≥2 типов → INFO "Mixed Diataxis types". Не критично, но рекомендуется разделять.

---

## 5. Heuristic Match Thresholds

| Уровень | Критерий | Severity |
|---------|----------|----------|
| **Exact** | 100% match после нормализации пробелов, ≥3 строки | CRITICAL (>5 строк), WARNING (3-5 строк) |
| **Near-duplicate** | >70% совпадения элементов для таблиц/списков (одинаковые заголовки + >70% строк) | WARNING |
| **Conceptual** | Одинаковые ключевые термины, разная формулировка | INFO (AI judgment) |

### Нормализация перед сравнением

1. Убрать leading/trailing whitespace
2. Свернуть множественные пробелы в один
3. Убрать markdown-разметку (`**`, `*`, `` ` ``)
4. Привести к lowercase
5. Таблицы: сравнивать по содержимому ячеек, игнорируя форматирование `|---|`

---

## 6. Structure Rules

| Правило | Критерий | Severity |
|---------|----------|----------|
| Пропуск уровня заголовка | H1→H3 (минуя H2) или H2→H4 | CRITICAL |
| Глубина заголовков | >H4 используется | INFO |
| Дисбаланс секций | Одна секция >40% от всего файла | WARNING |
| Пустая секция | Заголовок → следующий заголовок без контента (только пустые строки) | WARNING |
| Нет TOC | Файл >200 строк без оглавления / table of contents | INFO |
| Wall-of-text | >20 строк подряд без заголовков/списков/пустых строк/code blocks | WARNING |
| Длинные строки | Строка >200 символов | INFO |
