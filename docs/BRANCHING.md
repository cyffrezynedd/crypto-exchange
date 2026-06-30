# Git workflow

## Branches

| Branch | Назначение |
|--------|------------|
| `main` | стабильный релиз, только через merge из `dev` после проверки |
| `dev` | интеграционная ветка, сюда сливаются feature-ветки |
| `feature/*` | одна фича или логический пакет изменений |

## Поток

```text
feature/...  →  dev  →  (CI + smoke)  →  main
```

1. Создать ветку от `dev`: `git checkout -b feature/my-change dev`
2. Короткие коммиты по смыслу (см. примеры ниже)
3. Push и PR в `dev`
4. Дождаться зелёного CI (`make smoke-api` локально при необходимости)
5. Merge в `dev`, прогнать полный стек: `make up && make smoke-api`
6. PR `dev` → `main`, merge после успешной проверки

## Именование feature-веток

- `feature/openapi-swagger` — SpringDoc, Swagger UI на gateway
- `feature/security-gateway-trust` — секрет gateway, internal API, deposit flag
- `feature/events-outbox` — ORDER_CANCELLED в стакане, лимит retry outbox
- `feature/ci` — GitHub Actions
- `feature/stage6-docker` — UI в Docker

## Примеры коммитов

```text
feat(gateway): springdoc and aggregated swagger ui
feat(security): gateway trust filter and internal api key
fix(market-data): remove cancelled orders from order book
fix(trading): cap outbox retries at 10
ci: maven verify and react build on push
docs: branching workflow and mvp limitations
```

## CI

Workflow: `.github/workflows/ci.yml`

- `mvn verify` в `backend/`
- `npm ci && npm run build` в `frontend/react/`

Angular в CI не собирается (dev-only UI).
