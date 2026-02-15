# Корпоративные практики документирования

Краткие выжимки из индустриальных стандартов, на которых основаны правила doc-lint.

---

## 1. Google Technical Writing

- **Single Source of Truth (SSOT):** Каждый факт — ровно в одном месте. Остальные — ссылаются.
- **One idea per sentence:** Одно предложение = одна мысль. Легче переводить, легче тестировать.
- **Link, don't duplicate:** Если контент уже существует — ставь ссылку, не копируй.

> Источник: [Google Technical Writing](https://developers.google.com/tech-writing)

---

## 2. Amazon 6-Pager

- **Дисциплина длины:** Жёсткий лимит заставляет приоритизировать.
- **Нарратив важнее слайдов:** Структурированный текст > россыпь bullet points.
- **Принцип "Working backwards":** Начни с результата для читателя, затем детали.

> Источник: Amazon Leadership Principles, internal docs practice

---

## 3. Diataxis Framework

Четыре типа документов — не смешивать в одном файле:

| Тип | Цель | Ориентация |
|-----|------|------------|
| **Tutorial** | Научить | Learning-oriented |
| **How-to** | Решить задачу | Task-oriented |
| **Reference** | Дать факты | Information-oriented |
| **Explanation** | Объяснить "почему" | Understanding-oriented |

> Источник: [diataxis.fr](https://diataxis.fr)

---

## 4. Microsoft Docs

- **200-800 строк:** Идеальный диапазон для одного документа.
- **Консистентная иерархия:** H1→H2→H3, без пропусков уровней.
- **Scannable:** Заголовки, списки, таблицы — читатель должен найти нужное за 30 секунд.
- **TOC для длинных документов:** >200 строк требуют оглавление.

> Источник: [Microsoft Style Guide](https://learn.microsoft.com/style-guide)

---

## 5. GitLab Handbook

- **DRI (Directly Responsible Individual):** У каждого документа — один ответственный.
- **Link, don't duplicate:** Жёсткое правило: если дублирование найдено — merge request на удаление копии.
- **Single source of truth:** Handbook — единственный источник правды, wiki запрещена.

> Источник: [GitLab Handbook](https://handbook.gitlab.com)

---

## 6. Stripe Docs

- **Cross-reference вместо копирования:** Каждый фрагмент кода / таблицы живёт в одном месте.
- **Progressive disclosure:** Базовый пример → расширенные опции → edge cases.
- **Версионирование:** Документация привязана к версии API.

> Источник: [Stripe API Docs](https://stripe.com/docs/api)

---

## Синтез для doc-lint

| Практика | Правило в doc-lint |
|----------|--------------------|
| SSOT (Google, GitLab) | Cross-file duplicate detection + SSOT Owner |
| Дисциплина длины (Amazon, Microsoft) | Size thresholds per file type |
| Не смешивать типы (Diataxis) | Mixed Diataxis type detection |
| Консистентные заголовки (Microsoft) | Heading hierarchy check |
| Cross-reference (Stripe, GitLab) | Рекомендация "ссылка вместо копии" |
| Progressive Disclosure (Stripe) | Уже реализовано в skill architecture |
