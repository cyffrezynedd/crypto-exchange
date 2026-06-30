# Frontend

| Папка | Стек | Порт (dev) | Docker |
|-------|------|-----:|--------|
| `react/` | Vite + React 18 | 5173 | **:3000** (`make up`) |
| `angular/` | Angular 19 | 4200 | только dev |

## API (через gateway `/api/v1`)

- `GET /trading/favorite-pairs?page&size&symbol&tradingPairId` — M2M, пагинация на backend
- `POST /trading/favorite-pairs` — добавить в избранное
- `DELETE /trading/favorite-pairs/{tradingPairId}` — удалить
- `GET /trading/orders?page&size&side&status&tradingPairId` — ордера с фильтрами

```bash
make up
# UI: http://localhost:3000

cd react && npm install && npm run dev
cd angular && npm install && npm start
```
