# HTML Report Template

> Шаблон HTML отчёта для `/screenshot-analyze`.

---

## CSS Classes Reference

### Issue Categories (Tags)

| Tag | CSS Class | Описание |
|-----|-----------|----------|
| **Семантика** | `.tag.sem` | Смысловые ошибки перевода |
| **L10N** | `.tag.l10n` | Локализационные проблемы |
| **CURRENCY** | `.tag.cur` | Ошибки формата валют |
| **UI** | `.tag.ui` | Верстка: наложения, обрезания |
| **RTL** | `.tag.rtl` | RTL-специфичные проблемы |
| **UX** | `.tag.ux` | UX проблемы |
| **Консист.** | `.tag.cons` | Проблемы консистентности |

### Severity Levels

| Severity | CSS Class | Русский | Цвет |
|----------|-----------|---------|------|
| CRITICAL | `.sev.crit` | КРИТИЧНО | #dc2626 |
| ERROR | `.sev.err` | ОШИБКА | #ef4444 |
| WARNING | `.sev.warn` | ВНИМАНИЕ | #f59e0b |
| INFO | `.sev.info` | ИНФО | #3b82f6 |

---

## Template

```html
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>L10n Report — {COUNTRY_NAME}</title>
    <style>
        :root {
            --pass: #22c55e; --fail: #ef4444; --warn: #f59e0b;
            --info: #3b82f6; --crit: #dc2626; --bg: #f8fafc;
            --card: #fff; --border: #e2e8f0; --text: #1e293b; --muted: #64748b;
        }
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: system-ui, sans-serif; background: var(--bg); color: var(--text); line-height: 1.5; padding: 1.5rem; }
        .container { max-width: 1400px; margin: 0 auto; }
        header { text-align: center; margin-bottom: 1.5rem; padding-bottom: 1rem; border-bottom: 2px solid var(--border); }
        h1 { font-size: 1.5rem; margin-bottom: 0.25rem; }
        .sub { color: var(--muted); font-size: 0.85rem; }
        .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(120px, 1fr)); gap: 0.75rem; margin-bottom: 1.5rem; }
        .card { background: var(--card); border-radius: 8px; padding: 0.75rem; border: 1px solid var(--border); text-align: center; }
        .card .v { font-size: 1.5rem; font-weight: 700; }
        .card .l { color: var(--muted); font-size: 0.75rem; }
        .card.crit .v { color: var(--crit); }
        .card.fail .v { color: var(--fail); }
        .card.warn .v { color: var(--warn); }
        .banner { background: #dbeafe; border: 1px solid #93c5fd; border-radius: 8px; padding: 0.75rem 1rem; margin-bottom: 1.5rem; font-size: 0.85rem; color: #1e40af; }
        .section { background: var(--card); border-radius: 10px; border: 1px solid var(--border); margin-bottom: 1rem; overflow: hidden; }
        .section-hdr { display: flex; justify-content: space-between; align-items: center; padding: 0.75rem 1rem; background: #f1f5f9; border-bottom: 1px solid var(--border); font-weight: 600; }
        .tag { display: inline-block; padding: 0.15rem 0.5rem; border-radius: 4px; font-size: 0.7rem; font-weight: 600; background: #e2e8f0; }
        .tag.sem { background: #fef3c7; color: #92400e; }
        .tag.l10n { background: #dcfce7; color: #166534; }
        .tag.cur { background: #fce7f3; color: #be185d; }
        .tag.ui { background: #f3e8ff; color: #7c3aed; }
        .tag.rtl { background: #ffedd5; color: #c2410c; }
        .tag.ux { background: #dbeafe; color: #1e40af; }
        .tag.cons { background: #e0e7ff; color: #4338ca; }
        .sev { display: inline-block; padding: 0.15rem 0.5rem; border-radius: 4px; font-size: 0.7rem; font-weight: 700; }
        .sev.crit { background: #fee2e2; color: #dc2626; }
        .sev.err { background: #fecaca; color: #b91c1c; }
        .sev.warn { background: #fef3c7; color: #b45309; }
        .sev.info { background: #dbeafe; color: #1d4ed8; }
        table { width: 100%; border-collapse: collapse; font-size: 0.85rem; }
        th, td { padding: 0.5rem 0.75rem; text-align: left; border-bottom: 1px solid var(--border); }
        th { background: #f8fafc; font-weight: 600; }
        .screenshot { max-width: 100%; border-radius: 8px; margin: 1rem auto; display: block; }
        .verdict { padding: 0.25rem 0.75rem; border-radius: 9999px; font-size: 0.75rem; font-weight: 600; }
        .verdict.pass { background: #dcfce7; color: #166534; }
        .verdict.fail { background: #fee2e2; color: #991b1b; }
        .verdict.warn { background: #fef3c7; color: #92400e; }
    </style>
</head>
<body>
<div class="container">
    <header>
        <h1>L10n Report — {COUNTRY_NAME}</h1>
        <p class="sub">Region: {REGION_CODE} ({CURRENCY}) | {DATE}</p>
    </header>

    <div class="grid">
        <div class="card crit"><span class="v">{CRITICAL_COUNT}</span><span class="l">КРИТИЧНО</span></div>
        <div class="card fail"><span class="v">{ERROR_COUNT}</span><span class="l">ОШИБКИ</span></div>
        <div class="card warn"><span class="v">{WARNING_COUNT}</span><span class="l">ВНИМАНИЕ</span></div>
        <div class="card"><span class="v">{INFO_COUNT}</span><span class="l">ИНФО</span></div>
    </div>

    <div class="banner">
        ℹ️ Region: {COUNTRY_NAME} ({REGION_CODE}) | Currency: {CURRENCY_SYMBOL}
    </div>

    <!-- Per-screenshot sections -->
    <div class="section">
        <div class="section-hdr">
            <span>{FILENAME} ({LOCALE})</span>
            <span class="verdict {VERDICT_CLASS}">{VERDICT}</span>
        </div>
        <img class="screenshot" src="data:image/png;base64,{BASE64_IMAGE}" alt="{FILENAME}">
        <table>
            <thead><tr><th>#</th><th>Категория</th><th>Severity</th><th>Элемент</th><th>Описание</th></tr></thead>
            <tbody>
                <!-- Issues rows -->
                <tr>
                    <td>1</td>
                    <td><span class="tag sem">Семантика</span></td>
                    <td><span class="sev crit">КРИТИЧНО</span></td>
                    <td>CTA Button</td>
                    <td>Описание проблемы</td>
                </tr>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
```

---

## Placeholders

| Placeholder | Описание |
|-------------|----------|
| `{COUNTRY_NAME}` | Полное название страны (Brazil, Russia) |
| `{REGION_CODE}` | ISO код региона (BR, RU, SA) |
| `{CURRENCY}` | Код валюты (BRL, RUB, SAR) |
| `{CURRENCY_SYMBOL}` | Символ валюты (R$, ₽, SAR) |
| `{DATE}` | Дата генерации отчёта |
| `{CRITICAL_COUNT}` | Количество CRITICAL ошибок |
| `{ERROR_COUNT}` | Количество ERROR ошибок |
| `{WARNING_COUNT}` | Количество WARNING ошибок |
| `{INFO_COUNT}` | Количество INFO ошибок |
| `{FILENAME}` | Имя файла скриншота |
| `{LOCALE}` | Локаль скриншота (ru-RU, ar-SA) |
| `{VERDICT_CLASS}` | CSS класс вердикта (pass/fail/warn) |
| `{VERDICT}` | Текст вердикта |
| `{BASE64_IMAGE}` | Base64 encoded изображение |

---

## Команды для подготовки изображений

```bash
# Проверить размер
sips -g pixelWidth -g pixelHeight screenshot.png
```

**ОБЯЗАТЕЛЬНО:** Открой отчёт командой `open {path}/analysis-report.html`
