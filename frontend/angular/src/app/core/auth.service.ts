import { Injectable, inject, signal } from '@angular/core'
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http'
import { Router } from '@angular/router'
import { firstValueFrom } from 'rxjs'
import { API_BASE } from './constants'
import { parseApiError } from './parse-error'
import { isBenignListError, isSessionErrorMessage } from './session-errors'
import { ToastService } from './toast.service'
import {
  AuthResponse,
  FavoriteFilters,
  FavoritePair,
  Order,
  OrderBookFilters,
  OrderBookLevel,
  OrderFilters,
  PageResponse,
  TradingPair,
  Wallet,
} from './models'

const STORAGE_KEY = 'crypto-exchange-auth-angular'

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

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly session = signal<AuthResponse | null>(this.read())

  private readonly http = inject(HttpClient)
  private readonly toast = inject(ToastService)
  private readonly router = inject(Router)

  private handleSessionExpired() {
    this.logout()
    void this.router.navigateByUrl('/login')
  }

  private read(): AuthResponse | null {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    try {
      return JSON.parse(raw) as AuthResponse
    } catch {
      return null
    }
  }

  private save(session: AuthResponse | null) {
    if (!session) localStorage.removeItem(STORAGE_KEY)
    else localStorage.setItem(STORAGE_KEY, JSON.stringify(session))
    this.session.set(session)
  }

  private headers(auth = true): HttpHeaders {
    let headers = new HttpHeaders({ 'Content-Type': 'application/json' })
    const token = this.session()?.accessToken
    if (auth && token) headers = headers.set('Authorization', `Bearer ${token}`)
    return headers
  }

  private async request<T>(method: 'GET' | 'POST' | 'DELETE', path: string, options: {
    body?: unknown
    params?: HttpParams
    auth?: boolean
  } = {}): Promise<T> {
    const auth = options.auth ?? true
    try {
      if (method === 'GET') {
        return await firstValueFrom(
          this.http.get<T>(`${API_BASE}${path}`, {
            headers: this.headers(auth),
            params: options.params,
          }),
        )
      }
      if (method === 'POST') {
        return await firstValueFrom(
          this.http.post<T>(`${API_BASE}${path}`, options.body, {
            headers: this.headers(auth),
          }),
        )
      }
      return await firstValueFrom(
        this.http.delete<T>(`${API_BASE}${path}`, { headers: this.headers(auth) }),
      )
    } catch (err) {
      if (err instanceof HttpErrorResponse) {
        const raw = typeof err.error === 'string'
          ? err.error
          : JSON.stringify(err.error ?? {})
        const message = parseApiError(raw, err.status)
        if (auth) {
          if (err.status === 401 || isSessionErrorMessage(message)) {
            this.toast.error(message)
            this.handleSessionExpired()
          } else if (!isBenignListError(message)) {
            this.toast.error(message)
          }
        }
        throw new Error(message)
      }
      if (auth) {
        this.toast.error('Не удалось подключиться к серверу. Проверьте соединение.')
      }
      throw err
    }
  }

  async register(email: string, username: string, password: string) {
    await this.request<void>('POST', '/iam/users', {
      body: { email, username, password },
      auth: false,
    })
    await this.login(email, password)
  }

  async login(email: string, password: string) {
    const session = await this.request<AuthResponse>('POST', '/iam/auth/login', {
      body: { email, password },
      auth: false,
    })
    this.save(session)
  }

  logout() {
    this.save(null)
  }

  pairs() {
    return this.request<TradingPair[]>('GET', '/market/pairs', { auth: false })
  }

  async orderBook(symbol: string, filters: OrderBookFilters = {}) {
    const page = filters.page ?? 0
    const size = filters.size ?? 10
    let params = new HttpParams().set('page', String(page)).set('size', String(size))
    if (filters.side) params = params.set('side', filters.side)
    if (filters.username) params = params.set('username', filters.username)
    const data = await this.request<unknown>('GET', `/market/pairs/${symbol}/orderbook`, {
      params,
      auth: false,
    })
    return normalizePageResponse<OrderBookLevel>(data, page, size)
  }

  async orders(filters: OrderFilters = {}) {
    const page = filters.page ?? 0
    const size = filters.size ?? 10
    let params = new HttpParams().set('page', String(page)).set('size', String(size))
    if (filters.side) params = params.set('side', filters.side)
    if (filters.status) params = params.set('status', filters.status)
    if (filters.tradingPairId) params = params.set('tradingPairId', String(filters.tradingPairId))
    const data = await this.request<unknown>('GET', '/trading/orders', { params })
    return normalizePageResponse<Order>(data, page, size)
  }

  placeOrder(body: {
    tradingPairId: number
    side: 'BUY' | 'SELL'
    type: 'LIMIT' | 'MARKET'
    price?: string
    quantity: string
  }) {
    return this.request<Order>('POST', '/trading/orders', { body })
  }

  cancelOrder(id: string) {
    return this.request<Order>('DELETE', `/trading/orders/${id}`)
  }

  async favorites(filters: FavoriteFilters = {}) {
    const page = filters.page ?? 0
    const size = filters.size ?? 10
    let params = new HttpParams().set('page', String(page)).set('size', String(size))
    if (filters.symbol) params = params.set('symbol', filters.symbol)
    if (filters.tradingPairId) params = params.set('tradingPairId', String(filters.tradingPairId))
    const data = await this.request<unknown>('GET', '/trading/favorite-pairs', { params })
    return normalizePageResponse<FavoritePair>(data, page, size)
  }

  addFavorite(tradingPairId: number) {
    return this.request<FavoritePair>('POST', '/trading/favorite-pairs', { body: { tradingPairId } })
  }

  removeFavorite(tradingPairId: number) {
    return this.request<void>('DELETE', `/trading/favorite-pairs/${tradingPairId}`)
  }

  wallets() {
    return this.request<Wallet[]>('GET', '/clearing/wallets')
  }

  deposit(body: { eventId: string; currencyId: number; amount: string }) {
    return this.request<Wallet>('POST', '/clearing/wallets/deposit', { body })
  }
}
