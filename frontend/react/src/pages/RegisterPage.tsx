import type { FormEvent } from 'react'
import { useState } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { Button } from '../components/ui/Button'
import { Alert } from '../components/ui/Alert'
import { AuthCard, Field } from '../components/ui/Field'
import '../styles/forms.css'

export function RegisterPage() {
  const { register, session } = useAuth()
  const [email, setEmail] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [pending, setPending] = useState(false)

  if (session) return <Navigate to="/" replace />

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setPending(true)
    setError('')
    try {
      await register(email, username, password)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка регистрации')
    } finally {
      setPending(false)
    }
  }

  return (
    <AuthCard
      title="Регистрация"
      footer={
        <>
          Уже есть аккаунт? <Link to="/login">Войти</Link>
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
          label="Никнейм"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          maxLength={64}
          autoComplete="username"
        />
        <Field
          reveal
          label="Пароль"
          isPassword
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          minLength={8}
          autoComplete="new-password"
        />
        {error && <Alert reveal>{error}</Alert>}
        <Button type="submit" block reveal disabled={pending}>
          {pending ? 'Создание…' : 'Создать аккаунт'}
        </Button>
      </form>
    </AuthCard>
  )
}
