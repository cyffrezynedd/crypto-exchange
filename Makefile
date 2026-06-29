-include .env
export

JAVA_HOME ?= C:/Program Files/Eclipse Adoptium/jdk-21.0.8.9-hotspot
export JAVA_HOME

POSTGRES_USER ?= postgres
POSTGRES_PASSWORD ?= postgres
POSTGRES_DB ?= crypto_exchange
POSTGRES_PORT ?= 5433
COMPOSE_PROJECT_NAME ?= crypto-exchange

export COMPOSE_PROJECT_NAME
BACKEND_DIR := backend
IAM_DIR := backend/iam-service
GATEWAY_DIR := backend/api-gateway

.PHONY: help db-up infra-up db-down db-reset db-wait migrate psql smoke smoke-api build run-iam run-gateway up down

help:
	@echo ""
	@echo   make db-up       - start postgres
	@echo   make infra-up    - start postgres + redis + kafka
	@echo   make migrate     - apply SQL migrations
	@echo   make build       - mvn install all backend modules
	@echo   make run-iam     - run iam-service (needs db + migrate)
	@echo   make run-gateway - run api-gateway
	@echo   make up          - docker compose up --build (full stack)
	@echo   make down        - stop all containers
	@echo   make smoke       - quick db check
	@echo   make smoke-api   - HTTP smoke test via gateway (services must be running)
	@echo ""

db-up:
	docker compose up -d postgres

infra-up:
	docker compose up -d postgres redis kafka consul

db-down:
	docker compose down

db-wait: db-up
	docker compose exec postgres sh -c "until pg_isready -U $(POSTGRES_USER) -d $(POSTGRES_DB); do sleep 1; done"

db-reset: db-down
	docker compose down -v
	$(MAKE) db-wait migrate

migrate: db-wait
	@echo Applying migrations...
	docker compose exec -e POSTGRES_USER=$(POSTGRES_USER) -e POSTGRES_DB=$(POSTGRES_DB) postgres sh /devtools/db/migrate.sh

psql: db-wait
	docker compose exec postgres psql -U $(POSTGRES_USER) -d $(POSTGRES_DB)

smoke: db-wait
	docker compose exec -T postgres psql -U $(POSTGRES_USER) -d $(POSTGRES_DB) -f /devtools/db/smoke.sql

smoke-api:
	@echo "=== API smoke (gateway on :8080) ==="
	curl -sf http://localhost:8080/actuator/health
	curl -sf http://localhost:8080/api/gateway/info
	curl -sf http://localhost:8080/api/v1/market/pairs
	@echo OK    public endpoints
	powershell -NoProfile -Command "$$b='http://localhost:8080'; $$e='smoke-'+[guid]::NewGuid().ToString('N').Substring(0,8)+'@test.local'; $$u='smoke_'+[guid]::NewGuid().ToString('N').Substring(0,6); $$reg=@{email=$$e;username=$$u;password='SmokeTest123!'}|ConvertTo-Json; $$r=Invoke-RestMethod -Method POST -Uri ($$b+'/api/v1/iam/users') -ContentType 'application/json' -Body $$reg; $$login=@{email=$$e;password='SmokeTest123!'}|ConvertTo-Json; $$a=Invoke-RestMethod -Method POST -Uri ($$b+'/api/v1/iam/auth/login') -ContentType 'application/json' -Body $$login; $$h=@{Authorization=('Bearer '+$$a.accessToken)}; Invoke-RestMethod -Uri ($$b+'/api/v1/trading/orders') -Headers $$h|Out-Null; Write-Host 'OK    auth flow'"

build:
	cd $(BACKEND_DIR) && mvn -q install

run-iam: db-wait migrate
	cd $(IAM_DIR) && mvn -q spring-boot:run

run-gateway:
	cd $(GATEWAY_DIR) && mvn -q spring-boot:run

up: build
	docker compose up -d --build

down:
	docker compose down
