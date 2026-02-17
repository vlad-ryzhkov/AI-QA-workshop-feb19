# LQA правила и таблицы проверок

## Типы семантических ошибок

| Тип | Severity | Пример |
|-----|----------|--------|
| **False Friends** | CRITICAL | Economy → Скупость |
| **Wrong Context** | CRITICAL | Comfort → Соболезнования |
| **Literal Translation** | ERROR | Kettlebell → Чайник+Колокол |
| **Transliteration Error** | ERROR | Ingram → Преступление |
| **Offensive/Taboo** | CRITICAL | — |
| **Ambiguous** | ERROR | Auto → Автомобиль vs Автоматически |

## Ride-Hailing Specific Checks

### Деньги (Currency & Pricing)

| Проверка | Severity |
|----------|----------|
| CLDR Currency Position (префикс/суффикс) | ERROR |
| CLDR Number Separators | ERROR |
| Currency Symbol Consistency | ERROR |
| Price Format Consistency | WARNING |
| Fare/Tariff Names (не переводить буквально) | ERROR |
| RTL Price Concatenation | CRITICAL |

### Время (Time & Duration)

| Проверка | Severity |
|----------|----------|
| ETA Format (пробел числом и юнитом) | WARNING |
| Time Format (12h/24h по локали) | ERROR |
| min vs m (m может означать метры) | ERROR |
| Complex Plurals (1/2/5 минут) | ERROR |

### RTL Layout

| Элемент | RTL Behavior |
|---------|--------------|
| Back Button | Справа, стрелка вправо (>) |
| Car Icons | НЕ зеркалить |
| Progress Bar | Заполняется справа налево |
