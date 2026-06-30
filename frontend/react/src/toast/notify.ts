export type ToastVariant = 'danger' | 'success' | 'warning'

export type ToastItem = {
  id: number
  variant: ToastVariant
  text: string
  title?: string
  delay: number
  isFadeOut?: boolean
}

export type ToastInput = {
  variant?: ToastVariant
  text: string
  title?: string
  delay?: number
}

type PushToast = (item: ToastInput) => void
type SessionExpiredHandler = () => void

let pushToast: PushToast | null = null
let onSessionExpired: SessionExpiredHandler | null = null

const lastShownAt = new Map<string, number>()
const DEDUPE_MS = 3000

export function registerToast(push: PushToast) {
  pushToast = push
}

export function unregisterToast() {
  pushToast = null
}

export function registerSessionExpired(handler: SessionExpiredHandler) {
  onSessionExpired = handler
}

export function unregisterSessionExpired() {
  onSessionExpired = null
}

export function notifySessionExpired() {
  onSessionExpired?.()
}

function shouldSkipToast(title: string, text: string, variant: ToastVariant) {
  const key = `${variant}:${title}:${text}`
  const now = Date.now()
  const last = lastShownAt.get(key) ?? 0
  if (now - last < DEDUPE_MS) return true
  lastShownAt.set(key, now)
  return false
}

let nextId = 1

export function toast(input: ToastInput) {
  const title = input.title ?? ''
  const text = input.text.trim()
  if (!text && !title) return
  const variant = input.variant ?? 'danger'
  if (shouldSkipToast(title, text, variant)) return
  pushToast?.({
    ...input,
    variant,
    delay: input.delay ?? 4000,
  })
}

export function toastError(text: string, title?: string) {
  toast({ variant: 'danger', text, title })
}

export function toastSuccess(text: string, title?: string) {
  toast({ variant: 'success', text, title })
}

export function createToastId() {
  nextId += 1
  return nextId
}

export function isSessionErrorMessage(message: string): boolean {
  const m = message.toLowerCase()
  return (
    m.includes('сессия недействительна')
    || m.includes('сессия истекла')
    || m.includes('нужно войти в аккаунт')
    || /token/.test(m)
  )
}

export function isListNotFoundError(err: unknown): boolean {
  if (!(err instanceof Error)) return false
  const m = err.message.toLowerCase()
  return (
    m.includes('not found')
    || m.includes('не найден')
    || m.includes('запрашиваемые данные не найдены')
  )
}

export function isBenignListError(message: string): boolean {
  return isListNotFoundError(new Error(message))
}
