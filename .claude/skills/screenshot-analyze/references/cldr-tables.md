# CLDR Reference Tables

> Справочные таблицы для проверки форматов. Используется skill `/screenshot-analyze`.

---

## Currency Formats by Region

| Регион | Код | Валюта | Формат | Пример |
|--------|-----|--------|--------|--------|
| Brazil | BR | BRL | R$ #.###,## | R$ 1.234,56 |
| Russia | RU | RUB | # ###,## ₽ | 1 234,56 ₽ |
| USA | US | USD | $#,###.## | $1,234.56 |
| Germany | DE | EUR | #.###,## € | 1.234,56 € |
| UK | GB | GBP | £#,###.## | £1,234.56 |
| Saudi Arabia | SA | SAR | # ###.## ر.س | ١٬٢٣٤٫٥٦ ر.س |
| Mexico | MX | MXN | $#,###.## | $1,234.56 |
| Indonesia | ID | IDR | Rp #.### | Rp 1.234.567 |
| India | IN | INR | ₹#,##,###.## | ₹1,23,456.78 |

---

## Number Formats by Locale

| Locale | Тысячи | Дробь | Пример |
|--------|--------|-------|--------|
| en-US | , | . | 1,234.56 |
| de-DE | . | , | 1.234,56 |
| ru-RU | (space) | , | 1 234,56 |
| fr-FR | (space) | , | 1 234,56 |
| pt-BR | . | , | 1.234,56 |
| ar-SA | ٬ | ٫ | ١٬٢٣٤٫٥٦ |

---

## Time Formats by Locale

| Locale | Format | Пример |
|--------|--------|--------|
| en-US | 12h | 2:00 PM |
| en-GB | 24h | 14:00 |
| de-DE | 24h | 14:00 Uhr |
| ru-RU | 24h | 14:00 |
| ar-SA | 12h/24h | ٢:٠٠ م / ١٤:٠٠ |

---

## Address Formats by Region

| Регион | Формат | Пример |
|--------|--------|--------|
| BR | {street}, {number} - {district} | Rua Augusta, 123 - Consolação |
| RU | {street}, д. {number} | ул. Пушкина, д. 10 |
| US | {number} {street} | 123 Main Street |
| DE | {street} {number} | Hauptstraße 42 |
| SA | {number} {street} | ٢٠ شارع الملك فهد |

---

## Числовые системы (Critical for RTL)

| Система | Символы | Локали |
|---------|---------|--------|
| Западные арабские | 0 1 2 3 4 5 6 7 8 9 | Большинство |
| Восточно-арабские | ٠ ١ ٢ ٣ ٤ ٥ ٦ ٧ ٨ ٩ | ar-SA, ar-EG |
| Персидские | ۰ ۱ ۲ ۳ ۴ ۵ ۶ ۷ ۸ ۹ | fa-IR |

**ПРАВИЛО: Не смешивать системы на одном экране!**

---

## Layout Expansion Reference

| Язык | Типичное расширение vs EN | Типичная проблема |
|------|---------------------------|-------------------|
| German (de) | +30-40% | Truncation в кнопках |
| Russian (ru) | +20-30% | Truncation в labels |
| Spanish (es) | +20-30% | Overflow в headers |
| Portuguese (pt) | +20-30% | Overflow в CTA |
| Arabic (ar) | ~same length | RTL issues, смысловые ошибки |
| Chinese (zh) | -30-50% shorter | Требует другой line-height |
| Japanese (ja) | ~same/-10% | Смешение 3 систем письма |
| Korean (ko) | ~same | Spacing issues |

**Layout Stress Test Priority:**
1. DE (максимальное расширение)
2. RU, ES, PT-BR (значительное расширение)
3. AR (RTL + смысловые проблемы)
4. ZH, JA (typography issues)

---

## Plural Rules Reference (CLDR)

| Локаль | Формы | Правило | Пример (минуты) |
|--------|-------|---------|-----------------|
| en | 2 | one, other | 1 minute, 2 minutes |
| ru | 3 | one, few, many | 1 минута, 2 минуты, 5 минут |
| ar | 6 | zero, one, two, few, many, other | دقيقة, دقيقتان, دقائق... |
| pl | 3 | one, few, many | 1 minuta, 2 minuty, 5 minut |
| uk | 3 | one, few, many | 1 хвилина, 2 хвилини, 5 хвилин |
| zh/ja/ko | 1 | other (no plural) | 1分钟, 5分钟 |

**Типичная ошибка:** `if (n > 1) + "s"` — для русского это даёт "5 минуты" вместо "5 минут".

**Числа для проверки:** 1, 2, 5, 11, 21, 22, 25 — покрывают все формы.

---

## Quick Reference: RTL Locales

| Код | Язык | Цифры | Особенности |
|-----|------|-------|-------------|
| ar-SA | Arabic (Saudi) | ٠١٢٣٤٥٦٧٨٩ или 0123456789 | Полный RTL, восточно-арабские цифры опционально |
| ar-EG | Arabic (Egypt) | 0123456789 чаще | RTL, западные цифры более распространены |
| he-IL | Hebrew | 0123456789 | RTL, только западные цифры |
| fa-IR | Persian | ۰۱۲۳۴۵۶۷۸۹ | RTL, персидские цифры |
| ur-PK | Urdu | ۰۱۲۳۴۵۶۷۸۹ | RTL, персидские цифры |

---

## Quick Reference: Text Expansion

| Исходный язык | Целевой язык | Expansion Factor |
|---------------|--------------|------------------|
| EN | DE | 1.3-1.4x |
| EN | RU | 1.2-1.3x |
| EN | ES | 1.2-1.3x |
| EN | PT-BR | 1.2-1.3x |
| EN | FR | 1.15-1.25x |
| EN | AR | 0.9-1.1x |
| EN | ZH | 0.5-0.7x |
| EN | JA | 0.8-1.0x |

**Rule of Thumb:** Если DE перевод НЕ длиннее EN на 30% — проверь на truncation.

---

## Country Code Mapping

| Код | Название | Валюта |
|-----|----------|--------|
| BR | Brazil | R$ (BRL) |
| RU | Russia | ₽ (RUB) |
| SA | Saudi Arabia | SAR |
| US | United States | $ (USD) |
| MX | Mexico | $ (MXN) |
| ID | Indonesia | Rp (IDR) |
| IN | India | ₹ (INR) |
| EG | Egypt | E£ (EGP) |
| PK | Pakistan | Rs (PKR) |
