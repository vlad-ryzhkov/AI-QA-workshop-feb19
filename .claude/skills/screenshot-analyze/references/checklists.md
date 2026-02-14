# L10n Checklists

> Чек-листы для проверки скриншотов. Используется skill `/screenshot-analyze`.

---

## Геометрия и Верстка

- [ ] **Text Overflow:** Текст не выходит за границы контейнера
- [ ] **Truncation:** Текст не обрезан ("..." или cut off), особенно в CTA кнопках
- [ ] **Vertical Truncation:** Диакритические знаки не обрезаны — актуально для Thai, Hindi, Arabic, Myanmar
- [ ] **Line Wrapping:** Переносы не разрывают число+юнит (`15 <br> min` — ошибка)
- [ ] **Element Overlap:** Элементы не перекрывают друг друга
- [ ] **Spacing Consistency:** Отступы равномерные, visual rhythm сохранен
- [ ] **Alignment:** Элементы выровнены (левый край, центр, правый край)
- [ ] **Font Support:** Нет tofu boxes (шрифт поддерживает все символы локали)
- [ ] **UI Scaling:** Элементы не "поехали" из-за разницы в шрифтах

---

## RTL Specific (ar/he/fa/ur)

- [ ] **Text Direction:** Весь текст идет справа налево
- [ ] **Back Button Position:** Справа, стрелка указывает вправо (>)
- [ ] **Navigation Icons:** Chevrons, стрелки отзеркалены
- [ ] **Car Icons:** НЕ отзеркалены (автомобиль едет вперед)
- [ ] **Route Direction:** Линия маршрута отзеркалена
- [ ] **Progress Bars:** Заполняются справа налево
- [ ] **List Bullets:** Справа от текста
- [ ] **Input Alignment:** Поля ввода выровнены справа
- [ ] **Number Consistency:** Одна система цифр на экране
- [ ] **Margins/Paddings:** Отзеркалены (левый отступ → правый)
- [ ] **BiDi Isolation:** Телефоны, email, промокоды не "рассыпаются"

---

## CLDR Data Formats

- [ ] **Number Separators:** Тысячи/дробь соответствуют локали
- [ ] **Currency Position:** Префикс/суффикс по CLDR
- [ ] **Currency Symbol:** Символ или код соответствует региону
- [ ] **Currency Spacing:** Пробел между символом и числом по CLDR
- [ ] **Date Format:** DMY/MDY/YMD по локали
- [ ] **Time Format:** 12h/24h по локали
- [ ] **Distance Units:** km/mi соответствует региону
- [ ] **Unit Spacing:** Пробел между числом и юнитом (`2.5 km`, не `2.5km`)

---

## Семантика (Смысловые ошибки)

- [ ] **False Friends:** Слова похожие по звучанию, разные по смыслу
- [ ] **Literal Translation:** Буквальный перевод составных слов
- [ ] **Wrong Context:** Слово верное, контекст неправильный
- [ ] **Offensive Content:** Перевод может быть оскорбительным в культуре
- [ ] **Ambiguous Terms:** Двусмысленные слова
- [ ] **Transliteration Errors:** Ошибки в транслитерации имен
- [ ] **Industry Terms:** Нестандартные термины
- [ ] **Placeholder Variables:** `{name}`, `%s` не отображаются как текст

---

## Консистентность

- [ ] **Language Consistency:** Все UI тексты на одном языке
- [ ] **Currency Consistency:** Один символ валюты на экране
- [ ] **Format Consistency:** Одинаковый формат цен
- [ ] **Number System Consistency:** Одна система цифр
- [ ] **Terminology Consistency:** Одни термины
- [ ] **Register Consistency:** Единый стиль (формальный/неформальный)

---

## UX Quality

- [ ] **Readable Text:** Текст читаем (контраст, размер)
- [ ] **Close/Back Available:** Есть способ закрыть экран или вернуться
- [ ] **Clear CTA:** Кнопка действия понятна и корректно переведена
- [ ] **Error Context:** Ошибки с объяснением что делать
- [ ] **Loading State:** Состояние загрузки с индикатором

---

## N/A Markers (Неприменимые проверки)

Маркируй как **N/A** с причиной:

| Проверка | Когда N/A |
|----------|-----------|
| Keyboard Overlap | Клавиатура не показана |
| Empty State | Экран с данными |
| Error Context | Нет ошибок на экране |
| Loading State | Нет состояния загрузки |
| Date Format | Нет дат на экране |
| Time Format | Нет времени на экране |
| RTL Checks | Не RTL локаль |

**N/A не снижает coverage score** — считай только релевантные пункты.

---

## Self-Check Protocol (перед завершением)

- [ ] **Locale specified:** Все проблемы указывают целевую локаль?
- [ ] **Location provided:** У каждой проблемы есть локация на экране?
- [ ] **Severity assigned:** У каждой проблемы есть severity?
- [ ] **Suggestion actionable:** Каждое предложение конкретно?
- [ ] **No guessing:** Все выводы основаны на видимых данных?
- [ ] **RTL checked:** Для RTL локалей проверены все RTL-специфичные пункты?
- [ ] **CLDR validated:** Форматы проверены против CLDR таблиц?
- [ ] **No false positives:** Валюта региона не считается багом?
- [ ] **No style nitpicking:** Нет придирок к стилистике без смысловой ошибки?
- [ ] **Cross-comparison done:** Если несколько локалей — есть сравнительная таблица?
