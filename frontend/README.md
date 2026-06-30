# Frontend

| Папка | Стек | Порт | Главная |
|-------|------|-----:|---------|
| `react/` | Vite + React 18 | 5173 | Избранные пары (M2M grid) |
| `angular/` | Angular 19 | 4200 | То же самое |

## API (через gateway `/api/v1`)

- `GET /trading/favorite-pairs?page&size&symbol&tradingPairId` — M2M, пагинация на backend
- `POST /trading/favorite-pairs` — добавить в избранное
- `DELETE /trading/favorite-pairs/{tradingPairId}` — удалить
- `GET /trading/orders?page&size&side&status&tradingPairId` — ордера с фильтрами

```bash
make up
cd react && npm install && npm run dev
cd angular && npm install && npm start
```
