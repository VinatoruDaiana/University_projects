import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { isAxiosError } from 'axios'
import Card from '../components/Card'
import Input from '../components/Input'
import Button from '../components/Button'
import { registerUser } from '../api/auth'
import type { RegisterRequest } from '../models/auth'
import './Auth.css'

type FormFields = RegisterRequest & { confirmPassword: string }
type FormErrors = Partial<Record<keyof FormFields | 'general', string>>

function validate({ username, email, password, confirmPassword }: FormFields): FormErrors {
  const errors: FormErrors = {}

  if (!username.trim()) {
    errors.username = 'Username is required'
  } else if (username.length < 3) {
    errors.username = 'Username must be at least 3 characters'
  } else if (username.length > 20) {
    errors.username = 'Username must be at most 20 characters'
  } else if (!/^[a-zA-Z0-9_]+$/.test(username)) {
    errors.username = 'Only letters, numbers and underscores allowed'
  }

  if (!email.trim()) {
    errors.email = 'Email is required'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    errors.email = 'Enter a valid email address'
  }

  if (!password) {
    errors.password = 'Password is required'
  } else if (password.length < 8) {
    errors.password = 'Password must be at least 8 characters'
  } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(password)) {
    errors.password = 'Must contain uppercase, lowercase and a number'
  }

  if (!confirmPassword) {
    errors.confirmPassword = 'Please confirm your password'
  } else if (password && password !== confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
  }

  return errors
}

export default function Register() {
  const navigate = useNavigate()

  const [form, setForm] = useState<FormFields>({ username: '', email: '', password: '', confirmPassword: '' })
  const [errors, setErrors] = useState<FormErrors>({})
  const [loading, setLoading] = useState(false)

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { id, value } = e.target
    setForm(prev => ({ ...prev, [id]: value }))
    if (errors[id as keyof FormErrors]) setErrors(prev => ({ ...prev, [id]: undefined }))
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()

    const validationErrors = validate(form)
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      return
    }

    setLoading(true)
    setErrors({})

    try {
      await registerUser({ username: form.username, email: form.email, password: form.password })
      navigate('/login')
    } catch (err) {
      const message = isAxiosError(err) ? err.response?.data?.message : undefined

      if (message) {
        setErrors({ general: message })
      } else if (isAxiosError(err) && err.request) {
        setErrors({ general: 'Network error. Check your connection and try again.' })
      } else {
        setErrors({ general: 'Something went wrong. Please try again.' })
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-page">
      <Card className="auth-card">
        <div className="auth-header">
          <span className="auth-brand">DISI</span>
          <p className="auth-subtitle">Create your account</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit} noValidate>
          <Input
            id="username"
            label="Username"
            placeholder="john_doe"
            value={form.username}
            onChange={handleChange}
            error={errors.username}
          />
          <Input
            id="email"
            label="Email"
            type="email"
            placeholder="john@email.com"
            value={form.email}
            onChange={handleChange}
            error={errors.email}
          />
          <Input
            id="password"
            label="Password"
            type="password"
            placeholder="••••••••"
            value={form.password}
            onChange={handleChange}
            error={errors.password}
          />
          <Input
            id="confirmPassword"
            label="Confirm password"
            type="password"
            placeholder="••••••••"
            value={form.confirmPassword}
            onChange={handleChange}
            error={errors.confirmPassword}
          />

          {errors.general && (
            <p className="auth-error-general">{errors.general}</p>
          )}

          <Button
            type="submit"
            size="lg"
            className="auth-submit"
            disabled={loading}
          >
            {loading ? 'Creating account…' : 'Create account'}
          </Button>
        </form>

        <p className="register-login">
          Already have an account? <Link to="/login">Sign in</Link>
        </p>
      </Card>
    </div>
  )
}
