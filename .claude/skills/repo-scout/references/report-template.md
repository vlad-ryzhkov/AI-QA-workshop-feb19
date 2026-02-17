# Шаблон отчёта repo-scout-report.md

```markdown
# Repo Scout Report: {repo-name}

> Сгенерировано: {дата} | Скилл: /repo-scout

## 1. Repository Profile

| Параметр | Значение |
|----------|----------|
| Module | {module path из go.mod} |
| Go Version | {версия} |
| Service Type | {REST API / gRPC / Mixed / CLI / Consumer} |
| Services | {список из cmd/} |
| Source Files | {N .go файлов} |
| Test Files | {N _test.go файлов} |

### Ключевые зависимости

| Категория | Библиотека |
|-----------|-----------|
| HTTP | {chi / gin / echo / stdlib} |
| gRPC | {google.golang.org/grpc / нет} |
| DB | {mysql / postgres / mongo} |
| Queue | {sarama / segmentio-kafka / нет} |
| Cache | {go-redis / нет} |

## 2. API Surface Catalog

**Summary:** {N REST endpoints} + {M gRPC RPCs} = {total}

### REST Endpoints
| # | Method | Path | Description | Auth |
|---|--------|------|-------------|------|

### gRPC RPCs
| # | Service | Method | Request → Response | Streaming |
|---|---------|--------|--------------------|-----------|

### Дополнительные источники
- [ ] HTTP client файлы: {путь или "нет"}
- [ ] Postman коллекции: {путь или "нет"}

## 3. Specification Inventory

| Файл | Формат | Endpoints | Полнота |
|------|--------|-----------|---------|
| {путь} | {OpenAPI 3.0 / Swagger 2.0 / Proto3} | {N} | {Complete / Partial / Stale} |

**Coverage:** {X}/{total} endpoints имеют спецификацию = {%}

Формула: покрытые endpoints / (REST + gRPC) × 100

## 4. Existing Test Coverage

| Тип | Файлов | Расположение | Фреймворк |
|-----|--------|-------------|-----------|
| Unit | {N} | {internal/...} | {testify / stdlib} |
| Integration | {N} | {путь} | {testify + sqlmock} |
| E2E/API | {N или "внешний репо"} | {путь или ссылка} | {фреймворк} |

## 5. Infrastructure

| Компонент | Наличие | Детали |
|-----------|---------|--------|
| CI/CD | {✅/❌} | {GitHub Actions / GitLab CI} |
| Docker | {✅/❌} | {N сервисов в compose} |
| DB | {✅/❌} | {MySQL / PostgreSQL / MongoDB} |
| Migrations | {✅/❌} | {Liquibase / goose}, {N changesets} |
| Message Queue | {✅/❌} | {Kafka / RabbitMQ / NATS} |
| Cache | {✅/❌} | {Redis / Memcached} |
| Dev-Platform | {✅/❌} | {shared services} |

## 6. AI Setup Status

| Файл | Статус |
|------|--------|
| CLAUDE.md | {✅ есть / ❌ нет} |
| qa_agent.md | {✅ / ❌} |
| Skills | {N скиллов: список / ❌} |
| .agents/ | {✅ / ❌} |
| .cursor/rules/ | {✅ / ❌} |

## 7. Readiness Assessment

| Критерий | Статус | Комментарий |
|----------|--------|-------------|
| API Specs | {🟢 Complete / 🟡 Partial / 🔴 Missing} | {детали} |
| Test Infrastructure | {🟢 Ready / 🟡 Needs Setup / 🔴 Missing} | {детали} |
| Documentation | {🟢 / 🟡 / 🔴} | {детали} |
| AI Setup | {🟢 / 🟡 / 🔴} | {детали} |

### Blockers

{Список блокеров или "Нет блокеров"}

### Рекомендуемый следующий шаг

{Конкретная рекомендация: /test-plan, /init-project, "получить спецификацию от команды" и т.д.}
```
