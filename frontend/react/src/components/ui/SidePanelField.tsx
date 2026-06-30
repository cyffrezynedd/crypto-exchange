import type { ReactNode } from 'react'
import './side-panel-field.css'

type Props = {
  label: string
  children: ReactNode
  className?: string
}

export function SidePanelField({ label, children, className }: Props) {
  return (
    <label className={['side-panel-field', className ?? ''].filter(Boolean).join(' ')}>
      <span className="side-panel-field__label">{label}</span>
      <div className="side-panel-field__control">{children}</div>
    </label>
  )
}
