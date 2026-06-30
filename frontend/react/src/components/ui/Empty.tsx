import type { ComponentType, SVGProps } from 'react'
import { Button } from './Button'
import './empty.css'

type IconType = ComponentType<SVGProps<SVGSVGElement> & { size?: number }>

type Action = {
  label: string
  onClick?: () => void
  href?: string
  variant?: 'primary' | 'ghost'
}

type Props = {
  icon?: IconType
  title: string
  subtitle?: string
  primaryAction?: Action
  secondaryAction?: Action
}

export function Empty({ icon: Icon, title, subtitle, primaryAction, secondaryAction }: Props) {
  return (
    <div className="ui-empty">
      {Icon && (
        <div className="ui-empty__icon">
          <Icon size={28} />
        </div>
      )}
      <div className="ui-empty__text">
        <p className="ui-empty__title">{title}</p>
        {subtitle && <p className="ui-empty__subtitle">{subtitle}</p>}
      </div>
      {(primaryAction || secondaryAction) && (
        <div className="ui-empty__actions">
          {secondaryAction && (
            <Button
              block={false}
              variant="ghost"
              onClick={secondaryAction.onClick}
            >
              {secondaryAction.label}
            </Button>
          )}
          {primaryAction && (
            <Button block={false} onClick={primaryAction.onClick}>
              {primaryAction.label}
            </Button>
          )}
        </div>
      )}
    </div>
  )
}
