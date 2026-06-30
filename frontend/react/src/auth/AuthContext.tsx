import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import { api, loadSession, saveSession, type Session } from '../api/client'
import { registerSessionExpired, unregisterSessionExpired } from '../toast/notify'

type AuthContextValue = {
  session: Session | null
  login: (email: string, password: string) => Promise<void>
  register: (email: string, username: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => loadSession())

  useEffect(() => {
    registerSessionExpired(() => {
      saveSession(null)
      setSession(null)
    })
    return () => unregisterSessionExpired()
  }, [])

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      async login(email, password) {
        const next = await api.login({ email, password })
        saveSession(next)
        setSession(next)
      },
      async register(email, username, password) {
        await api.register({ email, username, password })
        const next = await api.login({ email, password })
        saveSession(next)
        setSession(next)
      },
      logout() {
        saveSession(null)
        setSession(null)
      },
    }),
    [session],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth outside AuthProvider')
  return ctx
}
