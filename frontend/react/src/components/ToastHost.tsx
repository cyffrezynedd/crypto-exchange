import { useCallback, useEffect, useState } from 'react'
import { CheckIcon, CircleXIcon, CrossIcon } from './icons'
import {
  createToastId,
  registerToast,
  type ToastInput,
  type ToastItem,
  unregisterToast,
} from '../toast/notify'
import './ui/toast.css'

const MAX_TOASTS = 3

const FADE_MS = 300

export function ToastHost() {
  const [toasts, setToasts] = useState<ToastItem[]>([])

  const pushToast = useCallback((input: ToastInput) => {
    const item: ToastItem = {
      id: createToastId(),
      variant: input.variant ?? 'danger',
      text: input.text,
      title: input.title,
      delay: input.delay ?? 4000,
    }
    setToasts((prev) => [item, ...prev].slice(0, MAX_TOASTS))
  }, [])

  const dismiss = useCallback((id: number) => {
    setToasts((prev) => prev.map((t) => (t.id === id ? { ...t, isFadeOut: true } : t)))
    window.setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id))
    }, FADE_MS)
  }, [])

  useEffect(() => {
    registerToast(pushToast)
    return () => unregisterToast()
  }, [pushToast])

  useEffect(() => {
    const timers = toasts
      .filter((t) => !t.isFadeOut && t.delay > 0)
      .map((t) => window.setTimeout(() => dismiss(t.id), t.delay))
    return () => timers.forEach((id) => window.clearTimeout(id))
  }, [toasts, dismiss])

  if (toasts.length === 0) return null

  return (
    <div className="toast-host" aria-live="polite">
      {toasts.map((item) => {
        const Icon = item.variant === 'success' ? CheckIcon : CircleXIcon
        return (
        <div
          key={item.id}
          className={[
            'toast-item',
            `toast-item--${item.variant}`,
            item.title ? 'toast-item--has-title' : 'toast-item--solo',
            item.isFadeOut ? 'is-fade-out' : '',
          ].filter(Boolean).join(' ')}
          role="alert"
        >
          <span className="toast-item__icon" aria-hidden>
            <Icon size={20} />
          </span>
          <div className="toast-item__body">
            {item.title && <p className="toast-item__title">{item.title}</p>}
            <p className={item.title ? 'toast-item__text' : 'toast-item__text toast-item__text--solo'}>{item.text}</p>
          </div>
          <button
            type="button"
            className="toast-item__close"
            aria-label="Закрыть"
            onClick={() => dismiss(item.id)}
          >
            <CrossIcon size={16} />
          </button>
        </div>
        )
      })}
    </div>
  )
}
