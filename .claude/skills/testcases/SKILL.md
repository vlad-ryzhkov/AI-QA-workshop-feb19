---
name: testcases
description: Генерирует ручные тест-кейсы (Kotlin DSL + Allure) с применением BVA, EP и строгим бизнес-контекстом. Запрещает хардкод данных и технические детали в expected. Используй для создания мануальных тестов, покрытия требований тест-кейсами или структурированных ручных проверок. Не используй для автотестов — для этого /api-tests.
allowed-tools: "Read Write Edit Glob Grep"
agent: agents/sdet.md
context: fork
---

## 🔒 SYSTEM REQUIREMENTS

Перед выполнением агент ОБЯЗАН загрузить: `.claude/protocols/gardener.md`

---

# Manual Test Case Generator (Kotlin DSL)

## Protocol

1. **Format:** Kotlin DSL (`@Manual`, `@Epic`, `@Feature`, `@AllureId`).
2. **Data Strategy:** NO hardcode. Плейсхолдеры (`{UNIQUE_EMAIL}`, `+7{RANDOM_7_DIGITS}`).
3. **Design:**
   - **BVA:** Numeric (Min-1/Min, Max/Max+1) AND Logic Boundaries (e.g. >3 chars → 3 (Pass), 4 (Fail)).
   - **Negative:** Empty, Null, Malformed, Injection (XSS/SQLi), State Conflicts (Action on PENDING/BLOCKED entity).
   - **Error Codes:** 400, 401, 403, 404, 409, 422, 429, 500.
4. **Алгоритм:** Analyze (spec/audit) → Draft (DSL) → Verify (constraints) → Output.

## Constraints (Нарушение = REJECT)

| Категория | Правило | Нарушение → Правильно |
|-----------|---------|----------------------|
| **Data** | Плейсхолдеры | `test@test.com` (❌) → `{UNIQUE_EMAIL}` (✅) |
| **Privacy** | RFC 2606 Only | `@gmail.com` (❌) → `@example.com` (✅) |
| **Privacy** | NO PII | `ivan.petrov@gmail.com` (❌) → `user_{timestamp}@example.com` (✅) |
| **Expectations** | Конкретный результат | "Всё работает" (❌) → "Тост 'Успех' отображен" (✅) |
| **Expectations** | NO Vague | "HTTP 201 или 400" (❌) → "HTTP 201 Created" (✅) |
| **UI Tests** | Бизнес-язык | `visibility=gone` (❌) → "Элемент скрыт" (✅) |
| **Atomicity** | 1 Case = 1 Scenario | UI + API + Analytics в одном тесте (❌) |
| **BVA** | Full Coverage | Только Min-1 (fail) (❌) → Min-1 (fail) + Min (success) (✅) |
| **Coverage** | State Logic | Happy Path only (❌) → Happy Path + State Conflict (✅) |
| **Duplication** | NO Duplicates | Same Action + Same Expected = Удалить дубликат |

## Template

```kotlin
package com.tests.manualtests.{feature}

import com.example.test.annotations.*
import io.qameta.allure.*
import org.junit.jupiter.api.*

@Epic("EPIC") @Feature("FEATURE") @Tags(Tag("QC"), Tag("ANDROID"))
class FeatureTests {

    @Test @AllureId("") @Manual @Severity(SeverityLevel.CRITICAL)
    @DisplayName("[Домен] Действие → Ожидаемый результат")
    fun `method_name`() {
        precondition("Подготовка:") {
            prepare("Пользователь с email '{UNIQUE_EMAIL}' не зарегистрирован")
        }
        step("Действие") {
            expected("Экран успеха отображен")
            expected("HTTP 201 Created")
        }
    }
}
```

## Severity Levels

| Уровень | Критерий |
|---------|----------|
| `BLOCKER` | Краш, не запускается, основные флоу заблокированы |
| `CRITICAL` | Бизнес-функция не работает (нельзя оплатить/заказать) |
| `NORMAL` | Функция работает с workaround |
| `MINOR` | Визуальные баги, опечатки |

## Execution Flow

1. **Analyze:** Извлеки границы, статусы, роли из spec/audit.
2. **Draft:** Генерируй DSL по Template. Примени BVA + EP + Error Guessing.
3. **Verify:**
   - BVA полный? (Min-1/Min, Max/Max+1)
   - NO hardcode? (email/phone/name)
   - NO PII? (@gmail/@yandex, реальные ФИО)
   - Expectations конкретны? (нет "или", "зависит от")
   - Atomic? (1 тест = 1 сценарий)
4. **Output:**
   ```
   ✅ SKILL COMPLETE: /testcases
   ├─ Артефакты: src/test/kotlin/manualtests/{feature}/*.kt
   ├─ Тестов: N
   ├─ BVA Coverage: X/Y границ (NN%)
   └─ Data Strategy: ✅
   ```
