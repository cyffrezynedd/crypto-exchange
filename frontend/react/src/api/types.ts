export type PageResponse<T> = {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
}

export type AuthResponse = {
  accessToken: string
  refreshToken: string
  expiresInSeconds: number
  userId: number
  username: string
}

export type TradingPair = {
  symbol: string
}

export type FavoritePair = {
  userId: number
  tradingPairId: number
  symbol: string
  addedAt: string
}

export type OrderBookLevel = {
  orderId: string
  price: string
  quantity: string
  side: 'BUY' | 'SELL'
  username: string
}

export type OrderBookFilters = {
  side?: 'BUY' | 'SELL'
  username?: string
  page?: number
  size?: number
}

export type Order = {
  id: string
  userId: number
  tradingPairId: number
  clientOrderId: string | null
  side: 'BUY' | 'SELL'
  type: 'LIMIT' | 'MARKET'
  price: string | null
  quantity: string
  filledQuantity: string
  status: string
  createdAt: string
  updatedAt: string
}

export type Wallet = {
  id: number
  userId: number
  currencyId: number
  availableBalance: string
  lockedBalance: string
  createdAt: string
  updatedAt: string
}

export type OrderFilters = {
  side?: 'BUY' | 'SELL'
  status?: string
  tradingPairId?: number
  page?: number
  size?: number
}

export type FavoriteFilters = {
  symbol?: string
  tradingPairId?: number
  page?: number
  size?: number
}
