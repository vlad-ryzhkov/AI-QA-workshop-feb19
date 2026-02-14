# Go Backend Patterns — Справочник для /repo-scout

## Build Files

| Файл | Назначение |
|------|-----------|
| `go.mod` | Модуль, версия Go, зависимости |
| `go.sum` | Checksums зависимостей |
| `Makefile` | Build targets, утилиты |
| `.golangci.yaml` / `.golangci.yml` | Linter config |

## Route Registration Patterns

### REST Frameworks

| Фреймворк | Import | Route Patterns |
|-----------|--------|----------------|
| **go-chi** | `github.com/go-chi/chi` | `r.Get(`, `r.Post(`, `r.Put(`, `r.Delete(`, `r.Route(`, `r.HandleFunc(` |
| **gin** | `github.com/gin-gonic/gin` | `gin.GET(`, `gin.POST(`, `engine.GET(`, `group.GET(` |
| **echo** | `github.com/labstack/echo` | `e.GET(`, `e.POST(`, `echo.GET(` |
| **stdlib** | `net/http` | `http.HandleFunc(`, `mux.Handle(`, `mux.HandleFunc(` |
| **gorilla/mux** | `github.com/gorilla/mux` | `r.HandleFunc(`, `r.Methods(` |
| **fiber** | `github.com/gofiber/fiber` | `app.Get(`, `app.Post(` |

### gRPC

| Паттерн | Назначение |
|---------|-----------|
| `pb.Register*Server(` | Регистрация gRPC-сервиса |
| `google.golang.org/grpc` | gRPC framework import |
| `*.proto` файлы | Service + RPC definitions |
| `protoc-gen-go-grpc` | Генератор Go-кода из proto |

### Grep-строка для поиска маршрутов

```
r\.Get\(|r\.Post\(|r\.Put\(|r\.Delete\(|r\.Route\(|r\.HandleFunc\(|\.GET\(|\.POST\(|\.PUT\(|\.DELETE\(|HandleFunc\(|pb\.Register|echo\.|fiber\.
```

## Test Patterns

| Тип | Признаки |
|-----|----------|
| **Unit** | `*_test.go` без build tags, imports: `testing`, `testify`, `gomock` |
| **Integration** | `//go:build integration`, imports: `sqlmock`, `testcontainers`, `dockertest` |
| **Benchmark** | Функции `Benchmark*` в `*_test.go` |
| **Fuzz** | Функции `Fuzz*` в `*_test.go` (Go 1.18+) |

### Test Frameworks в go.mod

| Библиотека | Назначение |
|-----------|-----------|
| `github.com/stretchr/testify` | Assertions (assert/require) + mocking |
| `go.uber.org/mock` / `github.com/golang/mock` | GoMock code generation |
| `github.com/DATA-DOG/go-sqlmock` | SQL mocking |
| `github.com/testcontainers/testcontainers-go` | Docker-based integration tests |
| `github.com/ory/dockertest` | Docker test helpers |

## Specification Files

| Glob | Формат |
|------|--------|
| `**/swagger.json`, `**/swagger.yaml` | Swagger 2.0 |
| `**/openapi.json`, `**/openapi.yaml` | OpenAPI 3.x |
| `**/*.swagger.json` | gRPC-gateway generated |
| `**/*.proto` | Protocol Buffers |
| `**/*.http`, `**/api.http` | JetBrains HTTP Client |

## Infrastructure Markers

| Glob | Что это |
|------|---------|
| `.github/workflows/*.yml` | GitHub Actions CI/CD |
| `.gitlab-ci.yml` | GitLab CI |
| `Jenkinsfile` | Jenkins pipeline |
| `Dockerfile`, `docker-compose.yaml` | Контейнеризация |
| `migrations/`, `**/changesets/` | DB миграции (Liquibase) |
| `**/goose/`, `**/atlas.hcl` | DB миграции (goose/Atlas) |
| `.dev-platform/` | inDriver Dev-Platform |
| `config/*.yaml` | Конфигурация по средам |
| `deployments/` | Helm charts, K8s manifests |

## AI Setup Files

| Файл | Инструмент |
|------|-----------|
| `CLAUDE.md` | Claude Code |
| `.claude/qa_agent.md` | Claude Code QA Agent |
| `.claude/skills/*/SKILL.md` | Claude Code Skills |
| `.agents/skills/*/SKILL.md` | Альтернативная структура |
| `AGENTS.md` | Zed/Cline/Continue.dev |
| `.cursor/rules/*.mdc` | Cursor IDE |
| `.github/copilot-instructions.md` | GitHub/VS Code Copilot |
