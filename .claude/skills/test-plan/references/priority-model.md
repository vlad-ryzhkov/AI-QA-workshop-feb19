# Модель приоритизации endpoints

Для каждого endpoint рассчитай приоритет по формуле:

```
Score = (Public × 3) + (HasSpec × 2) + (NoTests × 1) + (Critical × 2) + (SecurityRisk × 1)

Где:
- Public: 1 = публичный endpoint, 0 = внутренний/admin
- HasSpec: 1 = есть спецификация, 0 = нет
- NoTests: 1 = нет тестов, 0 = есть тесты
- Critical: 1 = бизнес-критичный (auth, payment, registration), 0 = обычный
- SecurityRisk: 1 = обрабатывает PII/auth/payments, 0 = нет

Max Score = 9 (публичный + есть спек + нет тестов + критичный + security)
```

## Распределение по приоритетам

- **P0 (Score 7-9):** Smoke — покрыть первыми
- **P1 (Score 4-6):** Core Business — основное покрытие
- **P2 (Score 1-3):** Edge Cases — расширенное покрытие
