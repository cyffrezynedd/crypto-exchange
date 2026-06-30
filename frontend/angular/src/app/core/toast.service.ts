import { Injectable, signal } from '@angular/core'

export type ToastVariant = 'danger' | 'success' | 'warning'

export type ToastItem = {
  id: number
  variant: ToastVariant
  text: string
  title?: string
  delay: number
  isFadeOut?: boolean
}

type ToastInput = {
  variant?: ToastVariant
  text: string
  title?: string
  delay?: number
}

const MAX_TOASTS = 3
const DEDUPE_MS = 3000
const FADE_MS = 300

@Injectable({ providedIn: 'root' })
export class ToastService {
  private nextId = 1
  private readonly lastShownAt = new Map<string, number>()
  private readonly timers = new Map<number, number>()

  readonly items = signal<ToastItem[]>([])

  show(input: ToastInput) {
    const title = input.title ?? ''
    const text = input.text.trim()
    if (!text && !title) return

    const variant = input.variant ?? 'danger'
    const key = `${variant}:${title}:${text}`
    const now = Date.now()
    const last = this.lastShownAt.get(key) ?? 0
    if (now - last < DEDUPE_MS) return
    this.lastShownAt.set(key, now)

    const item: ToastItem = {
      id: this.nextId++,
      variant,
      text,
      title: input.title,
      delay: input.delay ?? 4000,
    }

    this.items.update((prev) => [item, ...prev].slice(0, MAX_TOASTS))

    if (item.delay > 0) {
      const timer = window.setTimeout(() => this.dismiss(item.id), item.delay)
      this.timers.set(item.id, timer)
    }
  }

  error(text: string, title?: string) {
    this.show({ variant: 'danger', text, title })
  }

  success(text: string, title?: string) {
    this.show({ variant: 'success', text, title })
  }

  dismiss(id: number) {
    const timer = this.timers.get(id)
    if (timer != null) {
      window.clearTimeout(timer)
      this.timers.delete(id)
    }

    this.items.update((prev) => prev.map((t) => (t.id === id ? { ...t, isFadeOut: true } : t)))
    window.setTimeout(() => {
      this.items.update((prev) => prev.filter((t) => t.id !== id))
    }, FADE_MS)
  }
}
