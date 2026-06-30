export const API_BASE = import.meta.env.VITE_API_BASE ?? '/api/v1'

export const BREAKPOINTS = {
  tablet: 768,
  desktop: 1280,
} as const

export const PAIR_IDS: Record<string, number> = {
  BTC_USDT: 1,
  ETH_USDT: 2,
}

export const CURRENCY_LABELS: Record<number, string> = {
  1: 'BTC',
  2: 'ETH',
  3: 'USDT',
}

export const PAIR_LABELS: Record<number, string> = {
  1: 'BTC/USDT',
  2: 'ETH/USDT',
}
