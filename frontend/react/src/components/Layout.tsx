import { useRef } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { useAuthReveal } from '../motion/useAuthReveal'
import { AppHeader } from './AppHeader'
import { AuthBanner } from './AuthBanner'
import { PageReveal } from './PageReveal'
import {
  FoldersIcon,
  HouseIcon,
  NoteStackIcon,
  WalletOutlinedIcon,
} from './icons'
import './layout.css'
import '../styles/auth.css'

const links = [
  { to: '/', label: 'Главная', icon: HouseIcon, end: true },
  { to: '/market', label: 'Рынок', icon: FoldersIcon, end: false },
  { to: '/orders', label: 'Ордера', icon: NoteStackIcon, end: false },
  { to: '/wallets', label: 'Кошельки', icon: WalletOutlinedIcon, end: false },
]

function AnimatedOutlet() {
  const location = useLocation()
  return (
    <PageReveal key={location.pathname}>
      <Outlet />
    </PageReveal>
  )
}

export function AppLayout() {
  const { session, logout } = useAuth()

  return (
    <div className="shell">
      <AppHeader username={session?.username} onLogout={logout} />
      <div className="app-body">
        <aside className="sidebar scroll-hidden">
          <nav className="nav">
            {links.map(({ to, label, icon: Icon, end }) => (
              <NavLink
                key={to}
                to={to}
                end={end}
                className={({ isActive }) => (isActive ? 'nav-link active' : 'nav-link')}
              >
                {({ isActive }) => (
                  <>
                    <span className="nav-link__icon">
                      <Icon style={{ color: isActive ? 'var(--q-base)' : 'var(--q-grey-fading)' }} />
                    </span>
                    <span className="nav-link__label">{label}</span>
                  </>
                )}
              </NavLink>
            ))}
          </nav>
        </aside>
        <div className="app-page">
          <AnimatedOutlet />
        </div>
      </div>
    </div>
  )
}

export function AuthLayout() {
  const location = useLocation()
  const bannerRef = useRef<HTMLElement>(null)
  const formRef = useRef<HTMLDivElement>(null)

  useAuthReveal({
    bannerRef,
    formRef,
    resetKey: location.pathname,
  })

  return (
    <div className="auth-page">
      <div className="auth-page__grid">
        <AuthBanner ref={bannerRef} />
        <div className="auth-page__form">
          <div ref={formRef} className="auth-page__form-inner">
            <Outlet />
          </div>
        </div>
      </div>
    </div>
  )
}
