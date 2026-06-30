import { useId, useState, type ChangeEvent, type InputHTMLAttributes } from 'react'
import { CrossIcon, SearchIcon } from '../icons'
import './search-input.css'

type Props = Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> & {
  label?: string
  className?: string
  variant?: 'default' | 'filter'
}

export function SearchInput({
  label = 'Поиск',
  className,
  variant = 'default',
  value,
  onChange,
  onFocus,
  onBlur,
  ...rest
}: Props) {
  const id = useId()
  const [focused, setFocused] = useState(false)
  const filled = String(value ?? '').length > 0
  const active = variant === 'filter' ? filled : focused || filled

  function clear() {
    onChange?.({ target: { value: '' } } as ChangeEvent<HTMLInputElement>)
  }

  return (
    <div
      className={[
        'search-input-wrap',
        variant === 'filter' ? 'search-input-wrap--filter' : '',
        className ?? '',
      ].filter(Boolean).join(' ')}
    >
      <label
        htmlFor={id}
        className={['search-input-shell', active ? 'is-active' : ''].filter(Boolean).join(' ')}
      >
        <span className="search-input__icon" aria-hidden>
          <SearchIcon />
        </span>
        <input
          id={id}
          className="search-input__control"
          placeholder={label}
          value={value}
          onChange={onChange}
          onFocus={(e) => {
            setFocused(true)
            onFocus?.(e)
          }}
          onBlur={(e) => {
            setFocused(false)
            onBlur?.(e)
          }}
          {...rest}
        />
        {filled && (
          <button
            type="button"
            className="search-input__clear"
            aria-label="Очистить"
            tabIndex={-1}
            onMouseDown={(e) => e.preventDefault()}
            onClick={clear}
          >
            <CrossIcon size={14} />
          </button>
        )}
      </label>
    </div>
  )
}
