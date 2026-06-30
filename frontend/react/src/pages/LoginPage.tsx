import type { FormEvent } from 'react'
import { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { Button } from '../components/ui/Button'
import { Alert } from '../components/ui/Alert'
import { AuthCard, Field } from '../components/ui/Field'
import '../styles/forms.css'

export function LoginPage() {
  const { login, session } = useAuth()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [pending, setPending] = useState(false)

  if (session) return <Navigate to="/" replace />

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setPending(true)
    setError('')
    try {
      await login(email, password)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка входа')
    } finally {
      setPending(false)
    }
  }

  return (
    <AuthCard
      title="Вход"
      footer={
        <>
          Нет аккаунта? <Link to="/register">Регистрация</Link>
        </>
      }
    >
      <form className="auth-form" onSubmit={onSubmit}>
        <Field
          reveal
          label="Электронная почта"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          autoComplete="email"
        />
        <Field
          reveal
          label="Пароль"
          isPassword
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={8}
          autoComplete="current-password"
        />
        {error && <Alert reveal>{error}</Alert>}
        <Button type="submit" block reveal disabled={pending}>
          {pending ? 'Вход…' : 'Войти'}
        </Button>
      </form>
    </AuthCard>
  )
}
