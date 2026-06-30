import { useCallback, useEffect, useId, useLayoutEffect, useRef, useState } from 'react'
import { createPortal } from 'react-dom'
import { CheckIcon, ChevronDownIcon, ChevronSidebarIcon } from '../icons'
import './select.css'

export type SelectOption = {
  value: string
  label: string
}

type Props = {
  value: string
  onChange: (value: string) => void
  options: SelectOption[]
  placeholder?: string
  label?: string
  emptyValue?: string
  variant?: 'default' | 'filter' | 'sidebar'
  block?: boolean
  className?: string
  disabled?: boolean
}

type MenuPosition = {
  top: number
  left: number
  minWidth: number
  width?: number
}

export function Select({
  value,
  onChange,
  options,
  placeholder = 'Выберите',
  label,
  emptyValue = '',
  variant = 'default',
  block = false,
  className = '',
  disabled = false,
}: Props) {
  const [open, setOpen] = useState(false)
  const [menuPos, setMenuPos] = useState<MenuPosition | null>(null)
  const rootRef = useRef<HTMLDivElement>(null)
  const triggerRef = useRef<HTMLButtonElement>(null)
  const menuRef = useRef<HTMLUListElement>(null)
  const listId = useId()
  const selected = options.find((o) => o.value === value)
  const isFilterEmpty = variant === 'filter' && label != null && value === emptyValue
  const hasFilterValue = variant === 'filter' && !isFilterEmpty
  const triggerText = isFilterEmpty
    ? label
    : (selected?.label ?? (variant === 'filter' && label ? label : placeholder))

  const updateMenuPosition = useCallback(() => {
    const trigger = triggerRef.current
    if (!trigger) return
    const anchor =
      variant === 'sidebar'
        ? (trigger.closest('.side-panel-field') as HTMLElement | null)
        : null
    const rect = (anchor ?? trigger).getBoundingClientRect()
    setMenuPos({
      top: rect.bottom + 4,
      left: rect.left,
      minWidth: rect.width,
      width: variant === 'sidebar' ? rect.width : undefined,
    })
  }, [variant])

  useLayoutEffect(() => {
    if (!open) {
      setMenuPos(null)
      return
    }
    updateMenuPosition()
    window.addEventListener('scroll', updateMenuPosition, true)
    window.addEventListener('resize', updateMenuPosition)
    window.visualViewport?.addEventListener('resize', updateMenuPosition)
    return () => {
      window.removeEventListener('scroll', updateMenuPosition, true)
      window.removeEventListener('resize', updateMenuPosition)
      window.visualViewport?.removeEventListener('resize', updateMenuPosition)
    }
  }, [open, updateMenuPosition])

  useEffect(() => {
    if (!open) return
    function onDocClick(e: MouseEvent) {
      const target = e.target as Node
      if (rootRef.current?.contains(target) || menuRef.current?.contains(target)) return
      setOpen(false)
    }
    function onKey(e: KeyboardEvent) {
      if (e.key === 'Escape') setOpen(false)
    }
    document.addEventListener('mousedown', onDocClick)
    document.addEventListener('keydown', onKey)
    return () => {
      document.removeEventListener('mousedown', onDocClick)
      document.removeEventListener('keydown', onKey)
    }
  }, [open])

  const menu = open && menuPos
    ? createPortal(
        <ul
          ref={menuRef}
          id={listId}
          className={['ui-select__menu', `ui-select__menu--${variant}`, 'scroll-hidden'].filter(Boolean).join(' ')}
          role="listbox"
          style={{
            top: menuPos.top,
            left: menuPos.left,
            minWidth: menuPos.minWidth,
            width: menuPos.width ?? 'max-content',
            maxWidth: 'calc(100vw - 16px)',
          }}
        >
          {options.map((option) => (
            <li key={option.value} role="presentation">
              <button
                type="button"
                role="option"
                aria-selected={option.value === value}
                className={[
                  'ui-select__option',
                  option.value === value ? 'is-selected' : '',
                ].filter(Boolean).join(' ')}
                onClick={() => {
                  onChange(option.value)
                  setOpen(false)
                }}
              >
                <span className="ui-select__option-label">{option.label}</span>
                {option.value === value && (
                  <CheckIcon size={16} className="ui-select__option-check" />
                )}
              </button>
            </li>
          ))}
        </ul>,
        document.body,
      )
    : null

  return (
    <div
      ref={rootRef}
      className={[
        'ui-select',
        `ui-select--${variant}`,
        open ? 'is-open' : '',
        hasFilterValue ? 'has-value' : '',
        block ? 'ui-select--block' : '',
        className,
      ].filter(Boolean).join(' ')}
    >
      <button
        ref={triggerRef}
        type="button"
        className="ui-select__trigger"
        disabled={disabled}
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={listId}
        aria-label={variant === 'filter' && label ? label : undefined}
        onClick={() => setOpen((v) => !v)}
      >
        <span
          className={[
            'ui-select__value',
            isFilterEmpty || (!selected && variant !== 'filter') ? 'is-placeholder' : '',
          ].filter(Boolean).join(' ')}
        >
          {triggerText}
        </span>
        {variant !== 'sidebar' && (
          <span className="ui-select__chevron" aria-hidden>
            <ChevronDownIcon size={12} />
          </span>
        )}
      </button>
      {variant === 'sidebar' && (
        <span className="ui-select__chevron" aria-hidden>
          <ChevronSidebarIcon className="ui-select__chevron-icon" />
        </span>
      )}
      {menu}
    </div>
  )
}
