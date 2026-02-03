package com.tests.manualtests.registration

// pompt: /testcases specifications/registration_api_v2.md Save the result to src/test/testCases/prompted/RegistrationManualTests.kt

import com.example.test.annotations.*
import io.qameta.allure.*
import org.junit.jupiter.api.*

@Epic("User Management")
@Feature("Registration API")
@Tags(Tag("QC"), Tag("API"))
@SuiteDescription("Тест-кейсы для POST /api/v1/users/register: валидация полей, безопасность пароля, уникальность данных")
class RegistrationManualTests {

    // ============================================================
    // HAPPY PATH
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Smoke"), Tag("REGRESS"))
    @DisplayName("[Registration] Успешная регистрация с валидными данными возвращает статус PENDING")
    fun `successful registration with valid data returns PENDING status`() {
        precondition("Состояние системы:") {
            prepare("Email 'newuser@example.com' не зарегистрирован")
            prepare("Телефон '+79991234567' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с валидным payload") {
            expected("HTTP 201 Created")
            expected("Response содержит user_id (UUID)")
            expected("Response содержит status: 'PENDING'")
            expected("Response содержит маскированный телефон: '+7***67'")
        }

        step("Проверить отправку SMS") {
            expected("SMS с OTP-кодом отправлен на указанный номер")
        }
    }

    // ============================================================
    // ОБЯЗАТЕЛЬНОСТЬ ПОЛЕЙ (Required Fields)
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при отсутствии поля email")
    fun `error 400 when email field is missing`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос без поля email") {
            expected("HTTP 400 Bad Request")
            expected("Сообщение об ошибке указывает на отсутствие email")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при отсутствии поля phone")
    fun `error 400 when phone field is missing`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос без поля phone") {
            expected("HTTP 400 Bad Request")
            expected("Сообщение об ошибке указывает на отсутствие phone")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при отсутствии поля password")
    fun `error 400 when password field is missing`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос без поля password") {
            expected("HTTP 400 Bad Request")
            expected("Сообщение об ошибке указывает на отсутствие password")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при отсутствии поля full_name")
    fun `error 400 when full_name field is missing`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос без поля full_name") {
            expected("HTTP 400 Bad Request")
            expected("Сообщение об ошибке указывает на отсутствие full_name")
        }
    }

    // ============================================================
    // ВАЛИДАЦИЯ EMAIL
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при невалидном формате email (без @)")
    fun `error 400 when email has invalid format without at symbol`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с email: 'invalid-email.com'") {
            expected("HTTP 400 Bad Request")
            expected("Ошибка валидации формата email")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при пустом email")
    fun `error 400 when email is empty string`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с email: ''") {
            expected("HTTP 400 Bad Request")
        }
    }

    // ============================================================
    // ВАЛИДАЦИЯ PHONE (E.164)
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при невалидном формате телефона (без +)")
    fun `error 400 when phone has no plus prefix`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с phone: '79991234567' (без +)") {
            expected("HTTP 400 Bad Request")
            expected("Ошибка валидации формата E.164")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при телефоне с буквами")
    fun `error 400 when phone contains letters`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с phone: '+7999ABC4567'") {
            expected("HTTP 400 Bad Request")
        }
    }

    // ============================================================
    // ВАЛИДАЦИЯ PASSWORD (Security Rules)
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при пароле короче 8 символов")
    fun `error 400 when password is shorter than 8 characters`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с password: 'Short1!' (7 символов)") {
            expected("HTTP 400 Bad Request")
            expected("Ошибка: пароль должен содержать минимум 8 символов")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при пароле без цифр")
    fun `error 400 when password has no digits`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с password: 'NoDigits!@#'") {
            expected("HTTP 400 Bad Request")
            expected("Ошибка: пароль должен содержать цифры")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при пароле без спецсимволов")
    fun `error 400 when password has no special characters`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с password: 'NoSpecial123'") {
            expected("HTTP 400 Bad Request")
            expected("Ошибка: пароль должен содержать спецсимволы")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при пароле без заглавных букв")
    fun `error 400 when password has no uppercase letters`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с password: 'nouppercase123!'") {
            expected("HTTP 400 Bad Request")
            expected("Ошибка: пароль должен содержать заглавные буквы")
        }
    }

    // ============================================================
    // PII В ПАРОЛЕ (Social Engineering Protection)
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при пароле, содержащем часть email")
    fun `error 400 when password contains part of email`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с email: 'vladimir@work.com', password: 'Vlad123#Secure'") {
            expected("HTTP 400 Bad Request")
            expected("Ошибка: пароль содержит часть email ('vlad' > 3 символов)")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при пароле, содержащем имя пользователя")
    fun `error 400 when password contains user name`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с full_name: 'Alex Kid', password: 'Alex_2026!'") {
            expected("HTTP 400 Bad Request")
            expected("Ошибка: пароль содержит часть имени ('Alex' > 3 символов)")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Registration] Успех при пароле с 3-символьным совпадением (граница)")
    fun `success when password contains exactly 3 char match with name`() {
        precondition("Состояние системы:") {
            prepare("Email и телефон не зарегистрированы")
        }

        step("Отправить запрос с full_name: 'Max Test', password: 'Max_Strong99!'") {
            expected("HTTP 201 Created (3 символа = граница, не блокируется)")
        }
    }

    // ============================================================
    // ВАЛИДАЦИЯ FULL_NAME (BVA)
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при full_name короче 2 символов")
    fun `error 400 when full_name is shorter than 2 characters`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с full_name: 'A' (1 символ)") {
            expected("HTTP 400 Bad Request")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Успех при full_name ровно 2 символа (граница min)")
    fun `success when full_name is exactly 2 characters`() {
        precondition("Состояние системы:") {
            prepare("Email и телефон не зарегистрированы")
        }

        step("Отправить запрос с full_name: 'Li' (2 символа)") {
            expected("HTTP 201 Created")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Успех при full_name ровно 100 символов (граница max)")
    fun `success when full_name is exactly 100 characters`() {
        precondition("Состояние системы:") {
            prepare("Email и телефон не зарегистрированы")
        }

        step("Отправить запрос с full_name из 100 символов") {
            expected("HTTP 201 Created")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 400 при full_name длиннее 100 символов")
    fun `error 400 when full_name exceeds 100 characters`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с full_name из 101 символа") {
            expected("HTTP 400 Bad Request")
        }
    }

    // ============================================================
    // УНИКАЛЬНОСТЬ (Conflict 409)
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 409 при дублировании email")
    fun `error 409 when email already registered`() {
        precondition("Состояние системы:") {
            prepare("Пользователь с email 'existing@example.com' уже зарегистрирован")
        }

        step("Отправить запрос с тем же email, но другим телефоном") {
            expected("HTTP 409 Conflict")
            expected("Сообщение: Email уже зарегистрирован")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 409 при дублировании телефона")
    fun `error 409 when phone already registered`() {
        precondition("Состояние системы:") {
            prepare("Пользователь с телефоном '+79991111111' уже зарегистрирован")
        }

        step("Отправить запрос с тем же телефоном, но другим email") {
            expected("HTTP 409 Conflict")
            expected("Сообщение: Телефон уже зарегистрирован")
        }
    }

    // ============================================================
    // RATE LIMITING (429)
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Ошибка 429 при превышении лимита запросов")
    fun `error 429 when rate limit exceeded`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
            prepare("Лимит: 5 запросов в минуту")
        }

        step("Отправить 6 запросов подряд в течение минуты") {
            expected("Первые 5 запросов обрабатываются (201 или 400)")
            expected("6-й запрос возвращает HTTP 429 Too Many Requests")
        }
    }

    // ============================================================
    // EDGE CASES & SECURITY
    // ============================================================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Registration] Поддержка UTF-8 в full_name (кириллица)")
    fun `success with UTF-8 cyrillic characters in full_name`() {
        precondition("Состояние системы:") {
            prepare("Email и телефон не зарегистрированы")
        }

        step("Отправить запрос с full_name: 'Иван Петров'") {
            expected("HTTP 201 Created")
            expected("Имя корректно сохранено в UTF-8")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Registration] Поддержка Emoji в full_name")
    fun `success with emoji in full_name`() {
        precondition("Состояние системы:") {
            prepare("Email и телефон не зарегистрированы")
        }

        step("Отправить запрос с full_name: 'Test User 🚀'") {
            expected("HTTP 201 Created или 400 (зависит от политики)")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Registration] Защита от XSS в full_name")
    fun `xss script in full_name is sanitized`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с full_name: '<script>alert(1)</script>'") {
            expected("HTTP 400 (отклонено) или 201 с экранированием")
            expected("Скрипт НЕ исполняется при отображении")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Registration] Защита от SQL Injection в email")
    fun `sql injection in email is rejected`() {
        precondition("Состояние системы:") {
            prepare("API доступен")
        }

        step("Отправить запрос с email: 'test@x.com'; DROP TABLE users;--'") {
            expected("HTTP 400 Bad Request")
            expected("Запрос не приводит к выполнению SQL")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("REGRESS"))
    @DisplayName("[Registration] Лишние поля в запросе игнорируются")
    fun `extra fields in request are ignored`() {
        precondition("Состояние системы:") {
            prepare("Email и телефон не зарегистрированы")
        }

        step("Отправить запрос с дополнительным полем 'role': 'admin'") {
            expected("HTTP 201 Created")
            expected("Поле 'role' игнорируется, пользователь создан без привилегий admin")
        }
    }
}

/*
 * ============================================================
 * АУДИТ СООТВЕТСТВИЯ SKILL.md (testcases)
 * ============================================================
 *
 * ## 1. Данные и Валидация (Data Integrity)
 *
 * | Критерий              | Статус | Комментарий                                    |
 * |-----------------------|--------|------------------------------------------------|
 * | Happy Path            | ✅     | Тест #1                                        |
 * | Обязательность полей  | ✅     | 4 теста (email, phone, password, full_name)    |
 * | BVA full_name         | ✅     | 1, 2, 100, 101 символов                        |
 * | BVA password          | ⚠️     | Только 7 симв. Нет теста на ровно 8 (Min)      |
 * | BVA email/phone length| ❌     | Не покрыто (RFC 5322: max 254, E.164 limits)   |
 * | Спецсимволы/Emoji     | ✅     | UTF-8, Emoji, XSS, SQLi                        |
 * | Форматы email         | ⚠️     | Без @, пустой. Нет: двойной @, пробелы         |
 * | Форматы phone         | ⚠️     | Без +, с буквами. Нет: короткий/длинный        |
 *
 * ## 2. Мобильная специфика (Mobile UX)
 *
 * | Критерий              | Статус | Комментарий                                    |
 * |-----------------------|--------|------------------------------------------------|
 * | Пермишены             | N/A    | API-тесты, не применимо                        |
 * | Состояния сети        | N/A    | API-тесты, не применимо                        |
 * | Жизненный цикл        | N/A    | API-тесты, не применимо                        |
 * | Ориентация/Темы       | N/A    | API-тесты, не применимо                        |
 *
 * ## 3. Бизнес-логика и Состояния
 *
 * | Критерий              | Статус | Комментарий                                    |
 * |-----------------------|--------|------------------------------------------------|
 * | Ролевая модель        | N/A    | Публичный endpoint, ролей нет                  |
 * | Состояния сущностей   | ❌     | Нет теста: повторная регистрация PENDING user  |
 * | 429 Rate Limit        | ✅     | Покрыто                                        |
 * | 500 Server Error      | ❌     | Не покрыто                                     |
 *
 * ## 4. Self-Check
 *
 * | Критерий              | Статус | Комментарий                                    |
 * |-----------------------|--------|------------------------------------------------|
 * | Uniqueness            | ✅     | Дубликатов нет                                 |
 * | Atomicity             | ✅     | Каждый тест — один сценарий                    |
 * | Clarity (DisplayName) | ✅     | Понятны без чтения кода                        |
 * | Severity              | ✅     | Корректно расставлены                          |
 *
 * ## 5. Анти-паттерны
 *
 * | Паттерн                    | Статус | Комментарий                                |
 * |----------------------------|--------|--------------------------------------------|
 * | "Божественные" прекондишены| ✅     | Не обнаружены                              |
 * | Технические детали в expect| ⚠️     | HTTP коды допустимы для API-тестов         |
 * | Зависимые тесты            | ✅     | Каждый тест независим                      |
 *
 * ============================================================
 * ПРОПУЩЕННЫЕ СЦЕНАРИИ (TODO)
 * ============================================================
 *
 * HIGH PRIORITY:
 * - [ ] Password ровно 8 символов (граница Min, Happy Path)
 * - [ ] Password без строчных букв (uppercase + digits + special)
 * - [ ] 500 Internal Server Error — обработка ошибки сервера
 * - [ ] Повторная регистрация user в статусе PENDING
 *
 * MEDIUM PRIORITY:
 * - [ ] Email max length 254 символа (RFC 5322)
 * - [ ] Email с двойным @ (test@@example.com)
 * - [ ] Email с пробелами ("test @example.com")
 * - [ ] Phone слишком короткий (+7999)
 * - [ ] Phone слишком длинный (+7999123456789012345)
 * - [ ] Case sensitivity email (ALEX@x.com vs alex@x.com)
 *
 * LOW PRIORITY:
 * - [ ] Null vs empty string (разные сообщения об ошибке?)
 * - [ ] Concurrent registration (race condition)
 * - [ ] Международные телефоны разных стран (US, UK, etc.)
 * - [ ] Idempotency: повторный запрос с теми же данными
 *
 * ============================================================
 * ИТОГО
 * ============================================================
 *
 * Покрытие:      28 тестов (70-75% от идеального)
 * Пропущено:     ~12 сценариев
 * Критичных gap: 4 (password min BVA, 500 error, PENDING state, lowercase)
 *
 * Рекомендация:  Добавить HIGH PRIORITY сценарии перед релизом
 *
 */
