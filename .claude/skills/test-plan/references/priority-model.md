# Модель приоритизации endpoints — Справочник для /test-plan

## Формула скоринга

```
Score = (Public × 3) + (HasSpec × 2) + (NoTests × 1) + (Critical × 2) + (SecurityRisk × 1)

Max Score = 9
```

### Факторы

| Фактор | Вес | 1 (да) | 0 (нет) |
|--------|-----|--------|---------|
| **Public** | ×3 | Публичный endpoint (доступен клиентам) | Внутренний / admin / service-to-service |
| **HasSpec** | ×2 | Есть swagger/proto спецификация | Нет спецификации |
| **NoTests** | ×1 | Нет автотестов для endpoint | Есть хотя бы 1 тест |
| **Critical** | ×2 | Бизнес-критичный (см. таблицу ниже) | Обычный CRUD / utility |
| **SecurityRisk** | ×1 | Обрабатывает PII, auth, payments | Нет чувствительных данных |

## Распределение по приоритетам

| Приоритет | Score | Описание | Когда покрывать |
|-----------|-------|----------|-----------------|
| **P0 — Smoke** | 7-9 | Критические публичные endpoints без тестов | Первая неделя |
| **P1 — Core** | 4-6 | Основная бизнес-логика | Недели 2-3 |
| **P2 — Extended** | 1-3 | Edge cases, admin, internal | Неделя 4+ |

## Бизнес-критичные категории (Critical = 1)

| Категория | Примеры endpoints |
|-----------|-------------------|
| **Authentication** | /login, /register, /logout, /refresh-token |
| **Authorization** | /roles, /permissions, проверка доступа |
| **Payments** | /charge, /refund, /balance, /withdraw |
| **User Data** | /profile, /settings, /delete-account (GDPR) |
| **Core Flow** | Основной бизнес-процесс сервиса (зависит от домена) |
| **Notifications** | /send-push, /send-sms (дорогостоящие операции) |

## Security Risk категории (SecurityRisk = 1)

| Признак | Примеры |
|---------|---------|
| Обработка PII | email, phone, passport, address |
| Auth tokens | JWT creation/validation, session management |
| Financial data | Суммы, балансы, транзакции |
| File uploads | Аватары, документы |
| Rate-sensitive | OTP, password reset, registration |

## Пример расчёта

### POST /api/v1/register

| Фактор | Значение | Расчёт |
|--------|----------|--------|
| Public | 1 (клиентский) | 1 × 3 = 3 |
| HasSpec | 1 (есть в swagger) | 1 × 2 = 2 |
| NoTests | 1 (нет автотестов) | 1 × 1 = 1 |
| Critical | 1 (registration = core flow) | 1 × 2 = 2 |
| SecurityRisk | 1 (PII: email, phone) | 1 × 1 = 1 |

**Score = 3 + 2 + 1 + 2 + 1 = 9 → P0**

### GET /internal/healthcheck

| Фактор | Значение | Расчёт |
|--------|----------|--------|
| Public | 0 (internal) | 0 × 3 = 0 |
| HasSpec | 0 (нет в swagger) | 0 × 2 = 0 |
| NoTests | 1 (нет тестов) | 1 × 1 = 1 |
| Critical | 0 (utility) | 0 × 2 = 0 |
| SecurityRisk | 0 (нет данных) | 0 × 1 = 0 |

**Score = 0 + 0 + 1 + 0 + 0 = 1 → P2**
