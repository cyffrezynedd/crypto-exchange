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
|------|--------|-----:|-----:|
| 1 | БД — схема (мин. 7 таблиц, M2M), PostgreSQL, схему и SQL в git | 3.2ч | |
| 2 | JDBC — консольное приложение, CRUD (отдельная ветка) | | |
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