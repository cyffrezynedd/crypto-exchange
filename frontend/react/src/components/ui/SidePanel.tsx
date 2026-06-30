import { useEffect, useState, type ReactNode } from 'react'
import { createPortal } from 'react-dom'
import { CrossIcon } from '../icons'
import { Button } from './Button'
import './side-panel.css'

const TRANSITION_MS = 200

type Props = {
  isOpen: boolean
  title: string
  onClose: () => void
  children: ReactNode
  primaryLabel?: string
  primaryFormId?: string
  onPrimary?: () => void
  primaryDisabled?: boolean
  cancelLabel?: string
  footer?: ReactNode
}

export function SidePanel({
  isOpen,
  title,
  onClose,
  children,
  primaryLabel,
  primaryFormId,
  onPrimary,
  primaryDisabled,
  cancelLabel = 'Отменить',
  footer,
}: Props) {
  const [mounted, setMounted] = useState(isOpen)
  const [shown, setShown] = useState(false)

  useEffect(() => {
    if (isOpen) {
      setMounted(true)
      const frame = requestAnimationFrame(() => {
        requestAnimationFrame(() => setShown(true))
      })
      return () => cancelAnimationFrame(frame)
    }

    setShown(false)
    const timer = window.setTimeout(() => setMounted(false), TRANSITION_MS)
    return () => window.clearTimeout(timer)
  }, [isOpen])

  useEffect(() => {
    if (!mounted) return
    const prev = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    return () => {
      document.body.style.overflow = prev
    }
  }, [mounted])

  useEffect(() => {
    if (!mounted) return
    function onKey(e: KeyboardEvent) {
      if (e.key === 'Escape') onClose()
    }
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [mounted, onClose])

  if (!mounted) return null

  const openClass = shown ? ' is-open' : ''
  const showFooter = Boolean(footer || primaryLabel)

  return createPortal(
    <div
      className={`side-panel__root side-panel__root--flush-bottom${openClass}`}
      role="presentation"
      onClick={onClose}
    >
      <div className="side-panel__backdrop" aria-hidden />
      <aside
        className="side-panel__panel"
        role="dialog"
        aria-modal="true"
        aria-label={title}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="side-panel__head">
          <h2 className="side-panel__title">{title}</h2>
          <button type="button" className="side-panel__close" onClick={onClose} aria-label="Закрыть">
            <CrossIcon size={24} />
          </button>
        </div>
        <div className="side-panel__body scroll-hidden">{children}</div>
        {showFooter && (
          <div className="side-panel__footer">
            {footer ?? (
              <div className="side-panel__actions">
                <Button
                  type="button"
                  variant="ghost"
                  block
                  className="side-panel__cancel"
                  onClick={onClose}
                >
                  {cancelLabel}
                </Button>
                {primaryLabel && (
                  <Button
                    type={primaryFormId ? 'submit' : 'button'}
                    form={primaryFormId}
                    block
                    disabled={primaryDisabled}
                    onClick={primaryFormId ? undefined : onPrimary}
                  >
                    {primaryLabel}
                  </Button>
                )}
              </div>
            )}
          </div>
        )}
      </aside>
    </div>,
    document.body,
  )
}
