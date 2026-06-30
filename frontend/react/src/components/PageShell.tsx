import type { ReactNode } from 'react'
import '../styles/page-shell.css'

export function PageShell({ children }: { children: ReactNode }) {
  return <div className="page-shell">{children}</div>
}

export function PageTitle({
  title,
  icon,
  action,
  stats,
}: {
  title: string
  icon?: ReactNode
  action?: ReactNode
  stats?: ReactNode
}) {
  return (
    <div className="page-title" data-reveal>
      <div className="page-title__row">
        <h1>
          {icon}
          {title}
        </h1>
        {(stats || action) && (
          <div className="page-title__aside">
            {stats}
            {action && <div className="page-title__action">{action}</div>}
          </div>
        )}
      </div>
    </div>
  )
}

export function PageContent({ children }: { children: ReactNode }) {
  return (
    <div className="page-content">
      <div className="page-divider" aria-hidden />
      {children}
    </div>
  )
}

export function PageBody({ children }: { children: ReactNode }) {
  return (
    <div className="page-body scroll-hidden">
      <div className="page-divider" aria-hidden />
      {children}
    </div>
  )
}

export function Panel({
  children,
  className = '',
  grow,
}: {
  children: ReactNode
  className?: string
  grow?: boolean
}) {
  return (
    <section
      className={['card panel', grow ? 'panel--grow' : '', className].filter(Boolean).join(' ')}
      data-reveal
    >
      {children}
    </section>
  )
}

export function StatChips({ children }: { children: ReactNode }) {
  return <div className="stat-chips">{children}</div>
}

export function StatChip({ label, value }: { label: string; value: string | number }) {
  return (
    <span className="stat-chip">
      {label} <strong>{value}</strong>
    </span>
  )
}
