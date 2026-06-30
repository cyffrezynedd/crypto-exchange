import { API_BASE } from '../constants'
import { parseApiError } from './parseError'
import {
  isBenignListError,
  isSessionErrorMessage,
  notifySessionExpired,
  toastError,
} from '../toast/notify'
import type {
  AuthResponse,
  FavoriteFilters,
  FavoritePair,
  Order,
  OrderBookLevel,
  OrderBookFilters,
  OrderFilters,
  PageResponse,
  TradingPair,
  Wallet,
} from './types'

const STORAGE_KEY = 'crypto-exchange-auth'

export type Session = AuthResponse

export function loadSession(): Session | null {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) return null
  try {
    return JSON.parse(raw) as Session
  } catch {
    return null
  }
}

export function saveSession(session: Session | null) {
  if (!session) {
    localStorage.removeItem(STORAGE_KEY)
    return
  }
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
}

function buildQuery(params: Record<string, string | number | undefined>) {
  const query = new URLSearchParams()
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== '') query.set(key, String(value))
  })
  const qs = query.toString()
  return qs ? `?${qs}` : ''
}

const RETRYABLE_STATUSES = new Set([502, 503, 504])
const RETRY_DELAY_MS = 800

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms))
}


function normalizePageResponse<T>(data: unknown, page: number, size: number): PageResponse<T> {
  if (Array.isArray(data)) {
    const content = data as T[]
    return {
      content,
      page: 0,
      size: content.length || size,
      totalElements: content.length,
      totalPages: content.length > 0 ? 1 : 0,
    }
  }

  const paged = data as Partial<PageResponse<T>>
  const content = paged.content ?? []
  return {
    content,
    page: paged.page ?? page,
    size: paged.size ?? size,
    totalElements: paged.totalElements ?? content.length,
    totalPages: paged.totalPages ?? (content.length > 0 ? 1 : 0),
  }
}

async function request<T>(path: string, init: RequestInit = {}, auth = true): Promise<T> {
  const headers = new Headers(init.headers)
  headers.set('Content-Type', 'application/json')
  const session = loadSession()
  if (auth && session?.accessToken) {
    headers.set('Authorization', `Bearer ${session.accessToken}`)
  }

  let lastError = 'Что-то пошло не так. Попробуйте ещё раз'

  for (let attempt = 0; attempt < 2; attempt++) {
    let response: Response
    try {
      response = await fetch(`${API_BASE}${path}`, { ...init, headers })
    } catch {
      if (attempt === 0) {
        await sleep(RETRY_DELAY_MS)
        continue
      }
      if (auth) {
        toastError('Не удалось подключиться к серверу. Проверьте соединение.')
      }
      throw new Error('Не удалось подключиться к серверу. Проверьте соединение.')
    }

    if (!response.ok) {
      const text = await response.text()
      lastError = parseApiError(text, response.status)
      if (auth) {
        const isSession = response.status === 401 || isSessionErrorMessage(lastError)
        if (isSession) {
          toastError(lastError)
          notifySessionExpired()
        } else if (!isBenignListError(lastError)) {
          toastError(lastError)
        }
      }
      if (RETRYABLE_STATUSES.has(response.status) && attempt === 0) {
        await sleep(RETRY_DELAY_MS)
        continue
      }
      throw new Error(lastError)
    }

    if (response.status === 204) return undefined as T
    return response.json() as Promise<T>
  }

  throw new Error(lastError)
}

export const api = {
  register(body: { email: string; username: string; password: string }) {
    return request<void>('/iam/users', { method: 'POST', body: JSON.stringify(body) }, false)
  },
  login(body: { email: string; password: string }) {
    return request<AuthResponse>('/iam/auth/login', { method: 'POST', body: JSON.stringify(body) }, false)
  },
  pairs() {
    return request<TradingPair[]>('/market/pairs', {}, false)
  },
  orderBook(symbol: string, filters: OrderBookFilters = {}) {
    const page = filters.page ?? 0
    const size = filters.size ?? 10
    const qs = buildQuery({
      side: filters.side,
      username: filters.username,
      page,
      size,
    })
    return request<unknown>(`/market/pairs/${symbol}/orderbook${qs}`, {}, false).then((data) =>
      normalizePageResponse<OrderBookLevel>(data, page, size),
    )
  },
  orders(filters: OrderFilters = {}) {
    const page = filters.page ?? 0
    const size = filters.size ?? 10
    const qs = buildQuery({
      side: filters.side,
      status: filters.status,
      tradingPairId: filters.tradingPairId,
      page,
      size,
    })
    return request<unknown>(`/trading/orders${qs}`).then((data) =>
      normalizePageResponse<Order>(data, page, size),
    )
  },
  placeOrder(body: {
    tradingPairId: number
    side: 'BUY' | 'SELL'
    type: 'LIMIT' | 'MARKET'
    price?: string
    quantity: string
  }) {
    return request<Order>('/trading/orders', { method: 'POST', body: JSON.stringify(body) })
  },
  cancelOrder(id: string) {
    return request<Order>(`/trading/orders/${id}`, { method: 'DELETE' })
  },
  favorites(filters: FavoriteFilters = {}) {
    const page = filters.page ?? 0
    const size = filters.size ?? 10
    const qs = buildQuery({
      symbol: filters.symbol,
      tradingPairId: filters.tradingPairId,
      page,
      size,
    })
    return request<unknown>(`/trading/favorite-pairs${qs}`).then((data) =>
      normalizePageResponse<FavoritePair>(data, page, size),
    )
  },
  addFavorite(tradingPairId: number) {
    return request<FavoritePair>('/trading/favorite-pairs', {
      method: 'POST',
      body: JSON.stringify({ tradingPairId }),
    })
  },
  removeFavorite(tradingPairId: number) {
    return request<void>(`/trading/favorite-pairs/${tradingPairId}`, { method: 'DELETE' })
  },
  wallets() {
    return request<Wallet[]>('/clearing/wallets')
  },
  deposit(body: { eventId: string; currencyId: number; amount: string }) {
    return request<Wallet>('/clearing/wallets/deposit', { method: 'POST', body: JSON.stringify(body) })
  },
}
