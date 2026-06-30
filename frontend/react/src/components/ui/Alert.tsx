import type { ReactNode } from 'react'
import { CircleXIcon } from '../icons'
import './alert.css'

type Props = {
  children: ReactNode
  variant?: 'danger' | 'success' | 'warning'
  reveal?: boolean
}

export function Alert({ children, variant = 'danger', reveal }: Props) {
  return (
    <div
      className={`ui-alert ui-alert--${variant}`}
      role="alert"
      {...(reveal ? { 'data-reveal': '' } : {})}
    >
      {variant === 'danger' && <CircleXIcon size={20} className="ui-alert__icon" />}
      <span className="ui-alert__text">{children}</span>
    </div>
  )
}
