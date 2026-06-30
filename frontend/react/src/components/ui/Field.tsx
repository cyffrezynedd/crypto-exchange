import { useId, useState, type InputHTMLAttributes, type ReactNode } from 'react'
import { EyeIcon, EyeOffIcon } from '../icons'
import './field.css'

type Props = Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> & {
  label: string
  error?: string
  isPassword?: boolean
  reveal?: boolean
  variant?: 'default' | 'plain' | 'sidebar'
}

export function Field({ label, error, isPassword, reveal, variant = 'default', className, value, onFocus, onBlur, ...rest }: Props) {
  const id = useId()
  const [focused, setFocused] = useState(false)
  const [visible, setVisible] = useState(false)
  const filled = String(value ?? '').length > 0
  const floated = focused || filled

  return (
    <div className={`field-wrap ${className ?? ''}`} {...(reveal ? { 'data-reveal': '' } : {})}>
      <label
        htmlFor={id}
        className={[
          'field-shell',
          variant === 'plain' ? 'field-shell--plain' : '',
          variant === 'sidebar' ? 'field-shell--sidebar' : '',
          focused ? 'is-focused' : '',
          error ? 'is-error' : '',
          isPassword ? 'is-password' : '',
        ].filter(Boolean).join(' ')}
      >
        <span className={['field-label', floated ? 'is-floated' : ''].filter(Boolean).join(' ')}>
          {label}
        </span>
        <div className="field-input-row">
          <input
            id={id}
            className="field-input"
            value={value}
            type={isPassword && !visible ? 'password' : rest.type ?? 'text'}
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
          {isPassword && (
            <button
              type="button"
              className="field-eye"
              tabIndex={-1}
              onClick={() => setVisible((v) => !v)}
              aria-label={visible ? 'Скрыть пароль' : 'Показать пароль'}
            >
              {visible ? <EyeOffIcon size={18} /> : <EyeIcon size={18} />}
            </button>
          )}
        </div>
      </label>
      {error && <p className="field-error">{error}</p>}
    </div>
  )
}

export function AuthCard({ title, children, footer }: { title: string; children: ReactNode; footer?: ReactNode }) {
  return (
    <div className="auth-card">
      <div className="auth-card-head" data-reveal>
        <h1 className="auth-card-title">{title}</h1>
        <div className="auth-card-divider" />
      </div>
      <div className="auth-card-scroll">{children}</div>
      {footer && <div className="auth-card-footer" data-reveal>{footer}</div>}
    </div>
  )
}
