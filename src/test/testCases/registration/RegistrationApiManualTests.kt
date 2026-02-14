package com.tests.manualtests.registration

import com.example.test.annotations.*
import io.qameta.allure.*
import org.junit.jupiter.api.*

@Epic("User Management")
@Feature("Registration API")
@Tags(Tag("QC"), Tag("API"))
@SuiteDescription("Тестирование API регистрации пользователей POST /api/v1/users/register")
class RegistrationApiManualTests {

    // ==================== HAPPY PATH ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Smoke"), Tag("REGRESS"))
    @DisplayName("[Registration] Успешная регистрация с валидными данными")
    fun `successful registration with valid data`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован в системе")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован в системе")
        }

        step("Отправить POST /api/v1/users/register с валидными данными") {
            expected("HTTP 201 Created")
            expected("Response содержит user_id (UUID формат)")
            expected("Response содержит status = 'PENDING'")
            expected("Response содержит message с маскированным номером телефона")
        }
    }

    // ==================== EMAIL VALIDATION ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Email] Ошибка 400 при пустом email")
    fun `error 400 when email is empty`() {
        precondition("Подготовка окружения:") {
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с email = ''") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит описание ошибки валидации email")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Email] Ошибка 400 при отсутствии поля email")
    fun `error 400 when email field is missing`() {
        precondition("Подготовка окружения:") {
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register без поля email") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит описание ошибки: email обязателен")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Email] Ошибка 400 при невалидном формате email (без @)")
    fun `error 400 when email has no at symbol`() {
        precondition("Подготовка окружения:") {
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с email = 'userexample.com'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит описание ошибки формата email")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Email] Ошибка 400 при невалидном формате email (без домена)")
    fun `error 400 when email has no domain`() {
        precondition("Подготовка окружения:") {
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с email = 'user@'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит описание ошибки формата email")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Email] Ошибка 409 при дублировании email")
    fun `error 409 when email already registered`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{EXISTING_EMAIL}' уже зарегистрирован в системе")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с email = '{EXISTING_EMAIL}'") {
            expected("HTTP 409 Conflict")
            expected("Response содержит описание ошибки: email уже зарегистрирован")
        }
    }

    // ==================== PHONE VALIDATION ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Phone] Ошибка 400 при пустом phone")
    fun `error 400 when phone is empty`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с phone = ''") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит описание ошибки валидации phone")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Phone] Ошибка 400 при отсутствии поля phone")
    fun `error 400 when phone field is missing`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register без поля phone") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит описание ошибки: phone обязателен")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Phone] Ошибка 400 при невалидном формате phone (без +)")
    fun `error 400 when phone has no plus prefix`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с phone = '79990000000'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит описание ошибки формата E.164")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Phone] Ошибка 400 при невалидном формате phone (буквы)")
    fun `error 400 when phone contains letters`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с phone = '+7999ABC0000'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит описание ошибки формата E.164")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Phone] Ошибка 409 при дублировании phone")
    fun `error 409 when phone already registered`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '{EXISTING_PHONE}' уже зарегистрирован в системе")
        }

        step("Отправить POST /api/v1/users/register с phone = '{EXISTING_PHONE}'") {
            expected("HTTP 409 Conflict")
            expected("Response содержит описание ошибки: телефон уже зарегистрирован")
        }
    }

    // ==================== PASSWORD VALIDATION ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("BVA"), Tag("REGRESS"))
    @DisplayName("[Password] Успех при пароле ровно 8 символов (BVA Min)")
    fun `success when password is exactly 8 characters`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с password = 'Aa1!xxxx' (8 символов)") {
            expected("HTTP 201 Created")
            expected("Response содержит user_id (UUID)")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("BVA"), Tag("REGRESS"))
    @DisplayName("[Password] Ошибка 400 при пароле 7 символов (BVA Min-1)")
    fun `error 400 when password is 7 characters`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с password = 'Aa1!xxx' (7 символов)") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку: пароль должен быть минимум 8 символов")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Password] Ошибка 400 при пустом пароле")
    fun `error 400 when password is empty`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с password = ''") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку валидации пароля")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Password] Ошибка 400 при пароле без цифр")
    fun `error 400 when password has no digits`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с password = 'Abcdefgh!'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку: пароль должен содержать цифры")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Password] Ошибка 400 при пароле без спецсимволов")
    fun `error 400 when password has no special characters`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с password = 'Abcdefg1'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку: пароль должен содержать спецсимволы")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[Password] Ошибка 400 при пароле без заглавных букв")
    fun `error 400 when password has no uppercase letters`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с password = 'abcdefg1!'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку: пароль должен содержать заглавные буквы")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Password] Ошибка 400 при пароле содержащем часть email")
    fun `error 400 when password contains part of email`() {
        precondition("Подготовка окружения:") {
            prepare("Email 'testuser_{TIMESTAMP}@example.com' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST с email='testuser_{TIMESTAMP}@example.com', password='Testuser1!'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку: пароль не должен содержать части email")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Password] Ошибка 400 при пароле содержащем часть full_name")
    fun `error 400 when password contains part of full_name`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST с full_name='Test User', password='TestPass1!'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку: пароль не должен содержать части имени")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Password] Успех при пароле с 3-символьной частью имени (граница)")
    fun `success when password contains exactly 3 char part of name`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST с full_name='TestUser', password='Tes1pass!'") {
            expected("HTTP 201 Created")
            expected("'Tes' (3 символа) не блокирует регистрацию согласно требованиям (>3)")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[Password] Ошибка 400 при 4-символьной части имени в пароле (граница+1)")
    fun `error 400 when password contains 4 char part of name`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST с full_name='TestUser', password='Test1pass!'") {
            expected("HTTP 400 Bad Request")
            expected("'Test' (4 символа) блокирует регистрацию согласно требованиям (>3)")
        }
    }

    // ==================== FULL_NAME VALIDATION ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("BVA"), Tag("REGRESS"))
    @DisplayName("[FullName] Успех при full_name ровно 2 символа (BVA Min)")
    fun `success when full_name is exactly 2 characters`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с full_name = 'Ab'") {
            expected("HTTP 201 Created")
            expected("Response содержит user_id (UUID)")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("BVA"), Tag("REGRESS"))
    @DisplayName("[FullName] Ошибка 400 при full_name 1 символ (BVA Min-1)")
    fun `error 400 when full_name is 1 character`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с full_name = 'A'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку: full_name минимум 2 символа")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("BVA"), Tag("REGRESS"))
    @DisplayName("[FullName] Успех при full_name ровно 100 символов (BVA Max)")
    fun `success when full_name is exactly 100 characters`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с full_name = '{100_CHAR_STRING}'") {
            expected("HTTP 201 Created")
            expected("Response содержит user_id (UUID)")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("BVA"), Tag("REGRESS"))
    @DisplayName("[FullName] Ошибка 400 при full_name 101 символ (BVA Max+1)")
    fun `error 400 when full_name is 101 characters`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с full_name = '{101_CHAR_STRING}'") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку: full_name максимум 100 символов")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Validation"), Tag("REGRESS"))
    @DisplayName("[FullName] Ошибка 400 при пустом full_name")
    fun `error 400 when full_name is empty`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с full_name = ''") {
            expected("HTTP 400 Bad Request")
            expected("Response содержит ошибку валидации full_name")
        }
    }

    // ==================== RATE LIMITING ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[RateLimit] Ошибка 429 при превышении лимита 5 запросов/мин")
    fun `error 429 when rate limit exceeded`() {
        precondition("Подготовка окружения:") {
            prepare("С текущего IP отправлено 5 запросов за последнюю минуту")
        }

        step("Отправить 6-й запрос POST /api/v1/users/register") {
            expected("HTTP 429 Too Many Requests")
            expected("Response содержит описание ошибки rate-limit")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("Security"), Tag("REGRESS"))
    @DisplayName("[RateLimit] Успех после истечения окна rate-limit")
    fun `success after rate limit window expires`() {
        precondition("Подготовка окружения:") {
            prepare("С текущего IP отправлено 5 запросов")
            prepare("Прошла 1 минута с момента первого запроса")
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с валидными данными") {
            expected("HTTP 201 Created")
            expected("Rate-limit сброшен, регистрация успешна")
        }
    }

    // ==================== SECURITY ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("XSS"), Tag("REGRESS"))
    @DisplayName("[Security] Обработка XSS payload в full_name")
    fun `xss payload in full_name is sanitized or rejected`() {
        precondition("Подготовка окружения:") {
            prepare("Email '{UNIQUE_EMAIL}' не зарегистрирован")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST с full_name = '<script>alert(1)</script>'") {
            expected("HTTP 400 Bad Request ИЛИ данные экранированы при сохранении")
        }
    }

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.CRITICAL)
    @Tags(Tag("Security"), Tag("SQLi"), Tag("REGRESS"))
    @DisplayName("[Security] Обработка SQL Injection в email")
    fun `sql injection in email is rejected`() {
        precondition("Подготовка окружения:") {
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST с email = \"test'OR'1'='1@example.com\"") {
            expected("HTTP 400 Bad Request")
            expected("Невалидный формат email отклонён")
        }
    }

    // ==================== SERVER ERROR ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("ErrorHandling"), Tag("REGRESS"))
    @DisplayName("[Error] Корректный ответ 500 при внутренней ошибке сервера")
    fun `error 500 returns proper error response`() {
        precondition("Подготовка окружения:") {
            prepare("Сервер сконфигурирован для симуляции внутренней ошибки")
        }

        step("Отправить POST /api/v1/users/register с валидными данными") {
            expected("HTTP 500 Internal Server Error")
            expected("Response содержит generic error message без stack trace")
        }
    }

    // ==================== IDEMPOTENCY / STATE ====================

    @Test
    @AllureId("")
    @Manual
    @Severity(SeverityLevel.NORMAL)
    @Tags(Tag("State"), Tag("REGRESS"))
    @DisplayName("[State] Повторная регистрация PENDING пользователя возвращает 409")
    fun `error 409 when registering with credentials of pending user`() {
        precondition("Подготовка окружения:") {
            prepare("Пользователь с email '{EXISTING_EMAIL}' в статусе PENDING")
            prepare("Телефон '+7{RANDOM_9_DIGITS}' не зарегистрирован")
        }

        step("Отправить POST /api/v1/users/register с email = '{EXISTING_EMAIL}'") {
            expected("HTTP 409 Conflict")
            expected("Response указывает что email уже используется")
        }
    }
}
