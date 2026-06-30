import { Navigate, Route, Routes } from 'react-router-dom'
import type { ReactNode } from 'react'
import { AuthProvider, useAuth } from './auth/AuthContext'
import { AppLayout, AuthLayout } from './components/Layout'
import { ToastHost } from './components/ToastHost'
import { FavoritesPage } from './pages/FavoritesPage'
import { LoginPage } from './pages/LoginPage'
import { MarketPage } from './pages/MarketPage'
import { OrdersPage } from './pages/OrdersPage'
import { RegisterPage } from './pages/RegisterPage'
import { WalletsPage } from './pages/WalletsPage'

function PrivateRoute({ children }: { children: ReactNode }) {
  const { session } = useAuth()
  if (!session) return <Navigate to="/login" replace />
  return <>{children}</>
}

export default function App() {
  return (
    <AuthProvider>
      <ToastHost />
      <Routes>
        <Route element={<AuthLayout />}>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
        </Route>
        <Route
          element={
            <PrivateRoute>
              <AppLayout />
            </PrivateRoute>
          }
        >
          <Route path="/" element={<FavoritesPage />} />
          <Route path="/market" element={<MarketPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/wallets" element={<WalletsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AuthProvider>
  )
}
