---
name: testcases
description: Генерирует ручные тест-кейсы (Kotlin DSL + Allure) с применением BVA, EP и строгим бизнес-контекстом. Запрещает хардкод данных и технические детали в expected.
---

# Manual Test Case Generation (Kotlin DSL)

Превращает требования в атомарные, поддерживаемые тест-кейсы для Allure TestOps.

## Алгоритм

1. **Analyze Context:** Если был запуск `/analyze`, используй найденные граничные случаи.
2. **Data Strategy:** Определи переменные данные. **Не хардкодь!** Используй плейсхолдеры.
3. **Test Design:** Примени BVA (все границы), Equivalence Partitioning, Error Guessing.
4. **Drafting:** Напиши код по шаблону.
5. **Review:** Проверь на анти-паттерны (см. ⛔ BANNED).

---

## Quality Gates (Чек-лист)

### 1. Покрытие и Данные
- [ ] **BVA обязательно:** Min, Max, Min-1, Max+1, Empty, Null.
- [ ] **BVA Happy Path:** Тест на **ровно граничное валидное значение** (не только невалидное).
- [ ] **Security:** XSS payloads, SQLi, спецсимволы в текстовых полях.
- [ ] **Validation:** Невалидные форматы (email без @, phone без +).
- [ ] **State Conflicts:** Повторное действие на сущность в PENDING/BLOCKED статусе.
- [ ] **Error Codes:** 400, 409, 429, 500 — все покрыты.

### 2. Data Strategy (ОБЯЗАТЕЛЬНО)
- [ ] **Плейсхолдеры:** Вместо `test@test.com` → `{UNIQUE_EMAIL}` или `user_{timestamp}@example.com`.
- [ ] **RFC 2606 домены:** Только `@example.com`, `@example.org`, `@test.example`.
- [ ] **Fake phones:** `+70000000000` или `+7{RANDOM_7_DIGITS}`.
- [ ] **Нет PII:** Запрещены реальные ФИО, email с @gmail.com/@yandex.ru.

### 3. Analytics & Observability (если применимо)
- [ ] **Hostname в precondition:** При проверке аналитики указывай `prepare("Сниффер включен. Hostname: [ANALYTICS_HOST]")`.
- [ ] **Event params:** Проверяй конкретные параметры события, не просто "событие отправлено".

---

## Строгий Шаблон (Kotlin DSL)

```kotlin
package com.tests.manualtests.{feature_package}

import com.example.test.annotations.*
import io.qameta.allure.*
import org.junit.jupiter.api.*

@Epic("EPIC_NAME")
@Feature("FEATURE_NAME")
@Tags(Tag("QC"), Tag("ANDROID"), Tag("IOS"))
@SuiteDescription("Описание сьюта на русском")
class FeatureNameManualTests {

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Smoke"), Tag("REGRESS"))
    @DisplayName("[Домен] Действие + Ожидаемый результат")
    fun `method name in backticks`() {
        precondition("Подготовка окружения:") {
            prepare("Пользователь с email '{UNIQUE_EMAIL}' не зарегистрирован")
            // Для аналитики:
            prepare("Включен сниффер. Hostname: [ANALYTICS_HOST]")
        }

        step("Действие пользователя") {
            // Для UI: бизнес-результат
            expected("Отображается экран успеха")
            // Для API: допустим HTTP статус
            expected("HTTP 201 Created")
            expected("Response содержит user_id (UUID)")
        }
    }
}
```

---

## Уровни Severity

| Уровень | Критерий |
|---------|----------|
| `BLOCKER` | Краш, не запускается, основные флоу заблокированы |
| `CRITICAL` | Бизнес-функция не работает (нельзя оплатить/заказать) |
| `NORMAL` | Функция работает с workaround |
| `MINOR` | Визуальные баги, опечатки |

---

## ⛔ BANNED (Анти-паттерны)

### 1. Techno-Babble в Expected (для UI тестов)
```kotlin
// ❌ BANNED
expected("Элемент имеет android:visibility='gone'")
expected("RecyclerView.adapter.itemCount == 0")

// ✅ CORRECT
expected("Элемент скрыт")
expected("Список пустой")
```
> **Исключение:** Для API тестов HTTP коды допустимы (`HTTP 201`, `HTTP 400`).

### 2. Hardcoded Test Data
```kotlin
// ❌ BANNED
prepare("Email 'test@test.com' не зарегистрирован")
prepare("Телефон '+79161234567' свободен")

// ✅ CORRECT
prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
prepare("Телефон '+7{RANDOM_7_DIGITS}' свободен")
```

### 3. PII-подобные данные
```kotlin
// ❌ BANNED
email: "ivan.petrov@gmail.com"
email: "vladimir@work.com"
fullName: "Петров Иван Сергеевич"

// ✅ CORRECT
email: "user_{timestamp}@example.com"
fullName: "Test User"
```

### 4. Vague Expectations (неопределенность)
```kotlin
// ❌ BANNED
expected("HTTP 201 Created или 400 (зависит от политики)")
expected("Всё работает корректно")
expected("Данные сохранены")

// ✅ CORRECT
expected("HTTP 201 Created")
expected("Появился тост 'Регистрация успешна'")
expected("В БД создана запись с email = '{UNIQUE_EMAIL}'")
```

### 5. God-Test (Mixed Concerns)
```kotlin
// ❌ BANNED: Один тест проверяет UI + аналитику + валидацию + оплату

// ✅ CORRECT: Разбивай на атомарные тесты
// - ui_display_success_test
// - analytics_registration_event_test
// - validation_email_format_test
```

### 6. Missing BVA Happy Path
```kotlin
// ❌ INCOMPLETE: Есть только Min-1 (невалидная граница)
@DisplayName("Ошибка 400 при пароле короче 8 символов")

// ✅ COMPLETE: Добавь Min (валидная граница)
@DisplayName("Успех при пароле ровно 8 символов")
```

### 7. Duplicate Scenarios
> Same Action + Same Expected Outcome = **Дубликат. Удаляй.**

---

## Self-Check (перед завершением)

- [ ] **BVA полный:** Для каждой границы есть Min-1 (fail) И Min (success)?
- [ ] **No hardcode:** Все email/phone/name — плейсхолдеры или генерируемые?
- [ ] **No PII:** Нет @gmail.com, @yandex.ru, реальных ФИО?
- [ ] **No vague:** Каждый expected конкретен, без "или", без "зависит от"?
- [ ] **Uniqueness:** Нет дублирующихся сценариев?
- [ ] **Atomicity:** Каждый тест — один сценарий?
- [ ] **Error codes:** 400, 409, 429, 500 покрыты?
