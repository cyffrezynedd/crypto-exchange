import type { ButtonHTMLAttributes, ReactNode } from 'react'
import './button.css'

type Props = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'ghost' | 'stroke'
  block?: boolean
  reveal?: boolean
  children: ReactNode
}

export function Button({
  variant = 'primary',
  block = false,
  reveal,
  className,
  children,
  type = 'button',
  ...rest
}: Props) {
  return (
    <button
      type={type}
      {...(reveal ? { 'data-reveal': '' } : {})}
      className={[
        'q-btn',
        variant === 'primary' ? 'q-btn-primary' : '',
        variant === 'ghost' ? 'q-btn-ghost' : '',
        variant === 'stroke' ? 'q-btn-stroke' : '',
        block ? 'q-btn-block' : '',
        className ?? '',
      ].filter(Boolean).join(' ')}
      {...rest}
    >
      {children}
    </button>
  )
}
