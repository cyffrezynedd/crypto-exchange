-include .env
export

POSTGRES_USER ?= postgres
POSTGRES_PASSWORD ?= postgres
POSTGRES_DB ?= crypto_exchange
POSTGRES_PORT ?= 5433
COMPOSE_PROJECT_NAME ?= crypto-exchange

export COMPOSE_PROJECT_NAME
JDBC_DIR := backend/jdbc-console

.PHONY: help db-up db-down db-reset db-wait migrate psql run-jdbc smoke

help:
	@echo.
	@echo   make db-up      - start postgres (docker)
	@echo   make db-down    - stop postgres
	@echo   make db-reset   - drop volume, start db, apply migrations
	@echo   make migrate    - apply SQL from backend/migrations
	@echo   make psql       - open psql shell
	@echo   make run-jdbc   - run jdbc console
	@echo   make smoke      - quick db check
	@echo.

db-up:
	docker compose up -d postgres

db-down:
	docker compose down

db-wait: db-up
	docker compose exec postgres sh -c "until pg_isready -U $(POSTGRES_USER) -d $(POSTGRES_DB); do sleep 1; done"

db-reset: db-down
	docker compose down -v
	$(MAKE) db-wait migrate

migrate: db-wait
	@echo Applying migrations...
	powershell -NoProfile -ExecutionPolicy Bypass -File scripts/migrate.ps1

psql: db-wait
	docker compose exec postgres psql -U $(POSTGRES_USER) -d $(POSTGRES_DB)

run-jdbc:
	powershell -NoProfile -ExecutionPolicy Bypass -File scripts/run-jdbc.ps1

smoke: db-wait
	powershell -NoProfile -Command "Get-Content devtools/jdbc/smoke.sql -Raw | docker compose exec -T postgres psql -U $(POSTGRES_USER) -d $(POSTGRES_DB)"
