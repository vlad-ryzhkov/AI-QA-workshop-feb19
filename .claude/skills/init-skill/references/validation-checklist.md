# Чеклист валидации нового Skill

## Структура

- [ ] Файл называется точно `SKILL.md` (case-sensitive)
- [ ] Папка в kebab-case, совпадает с полем `name`
- [ ] Нет README.md внутри skill папки
- [ ] **SKILL.md ≤ 500 строк** (если больше — разбей на references/)

## YAML Frontmatter

- [ ] Поле `name` присутствует и в kebab-case
- [ ] Поле `name` НЕ содержит "claude" или "anthropic"
- [ ] YAML description < 1024 символов
- [ ] Description содержит **What** + **When**
- [ ] Description без XML тегов (`<`, `>`)

## Контент

- [ ] Есть секция "Когда использовать"
- [ ] Шаги пронумерованы и конкретны
- [ ] Стиль — императивный (без "вы должны", "следует")
- [ ] Ссылки на resources указывают на реальные файлы
- [ ] Есть пример вывода
- [ ] Есть Quality Gates
- [ ] Большие файлы вынесены в references/

## Если SKILL.md > 500 строк

```
Skill слишком большой ([N] строк > 500).

Предлагаю вынести:
1. Примеры кода → references/examples.md
2. Чек-листы → references/checklist.md
3. Таблицы → references/tables.md

Разбить? (да / оставить как есть)
```
