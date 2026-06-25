# Crypto Exchange

Индивидуальное задание
- Тема: криптобиржа

## SonarCloud

https://sonarcloud.io/project/overview?id=cyffrezynedd_crypto-exchange

## План

Оценка до задачи: `E = (P + O + 4 * BG) / 6`
- P — пессимистичная оценка,
- O — оптимистичная оценка,
- BG — наиболее вероятная оценка.


| Этап | Задача | Запланированное время | Фактическое время |
|------|--------|-----:|-------------------:|
| 1 | БД — схема (мин. 7 таблиц, M2M), PostgreSQL, схему и SQL в git | 3.2ч | 2.5ч |
| 2 | JDBC — консольное приложение, CRUD (отдельная ветка) | 2.3ч | 2ч |
| 3 | Backend — Spring Boot + Hibernate | | |
| 4 | Frontend — Angular | | |
| 5 | Frontend — React | | |
| 6 | Запуск приложения в Docker | | |

## Этап 1 — БД

- PostgreSQL
- 12 таблиц
- SQL: `backend/migrations/`
- Диаграмма: `docs/schema.png` (`docs/schema.dbml`)
- M2M: `user_roles`, `user_favorite_pairs`

Оценка времени (этап 1):
- пессимистичная (P) — 5 ч
- оптимистичная (O) — 2 ч
- наиболее вероятная (BG) — 3 ч

`E = (P + O + 4 * BG) / 6 = (5 + 2 + 4 * 3) / 6 = 19 / 6 ≈ 3.2 ч`

### Схема

![schema](docs/schema.png)

## Этап 2 — JDBC (ветка `feature/jdbc`)

- Код: `backend/jdbc-console/`
- CRUD: users, currencies, user_roles (M2M)
- Dev: `Makefile`, `docker-compose.yml`

Оценка времени (этап 2):
- пессимистичная (P) — 4 ч
- оптимистичная (O) — 1.5 ч
- наиболее вероятная (BG) — 2 ч

`E = (P + O + 4 * BG) / 6 = (4 + 1.5 + 4 * 2) / 6 = 13.5 / 6 ≈ 2.3 ч`

Фактически: **2 ч**

```bash
copy .env.example .env
copy backend\jdbc-console\src\main\resources\db.properties.example backend\jdbc-console\src\main\resources\db.properties

make db-up
make migrate
make run-jdbc
```

Нужен `make` (Git Bash / `choco install make`) и Docker.
