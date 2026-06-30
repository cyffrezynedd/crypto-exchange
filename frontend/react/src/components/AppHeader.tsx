import { useEffect, useRef, useState } from 'react'
import { BrandLogo } from './BrandLogo'
import { LogoutIcon } from './icons'
import './header.css'

type Props = {
  username?: string
  onLogout: () => void
}

export function AppHeader({ username, onLogout }: Props) {
  const [open, setOpen] = useState(false)
  const rootRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    function onDocClick(e: MouseEvent) {
      if (!rootRef.current?.contains(e.target as Node)) setOpen(false)
    }
    document.addEventListener('mousedown', onDocClick)
    return () => document.removeEventListener('mousedown', onDocClick)
  }, [])

  const initial = (username?.trim()?.[0] ?? 'U').toUpperCase()

  return (
    <header className="app-header">
      <BrandLogo compact className="app-header__brand" />

      <div className="user-menu" ref={rootRef}>
        <button type="button" className="user-menu__trigger" onClick={() => setOpen((v) => !v)}>
          <span className="user-menu__avatar">{initial}</span>
          <span className="user-menu__name">{username}</span>
        </button>

        {open && (
          <div className="user-menu__panel">
            <div className="user-menu__profile">
              <span className="user-menu__avatar user-menu__avatar--lg">{initial}</span>
              <div>
                <div className="user-menu__name user-menu__name--lg">{username}</div>
                <div className="user-menu__meta">CryptoX</div>
              </div>
            </div>
            <button type="button" className="user-menu__action" onClick={onLogout}>
              <LogoutIcon size={18} />
              Выйти
            </button>
          </div>
        )}
      </div>
    </header>
  )
}
