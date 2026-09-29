import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { isAxiosError } from 'axios'
import Card from '../components/Card'
import Input from '../components/Input'
import Button from '../components/Button'
import { loginUser } from '../api/auth'
import type { LoginRequest } from '../models/auth'
import './Auth.css' 

type FormFields = LoginRequest
type FormErrors = Partial<Record<keyof FormFields | 'general', string>>

function validate({ email, password }: FormFields): FormErrors {
  const errors: FormErrors = {}

  if (!email.trim()) {
    errors.email = 'Email is required'
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    errors.email = 'Enter a valid email address'
  }

  if (!password) {
    errors.password = 'Password is required'
  }

  return errors
}

export default function Login() {
  const navigate = useNavigate()

  const [form, setForm] = useState<FormFields>({ email: '', password: '' })
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
      const response = await loginUser(form)

      localStorage.setItem('jwt_token', response.token)
      localStorage.setItem('pulse_user', JSON.stringify(response.user))

      if (response.user.role === 'ADMIN') {
        navigate('/admin')
      } else {
        navigate('/feed')
      }
    } catch (err) {
      if (isAxiosError(err) && err.response) {
        const status = err.response.status
        
        // Handle specific status codes based on task requirements
        if (status === 400) {
          setErrors({ general: 'Validation error. Please check your inputs.' })
        } else if (status === 401) {
          setErrors({ general: 'Invalid credentials.' })
        } else if (status === 403) {
          setErrors({ general: 'Account blocked / inactive.' })
        } else {
          setErrors({ general: 'Something went wrong on the server.' })
        }
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
          <p className="auth-subtitle">Sign in to your account</p>
        </div>

        <form className="auth-form" onSubmit={handleSubmit} noValidate>
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

          {errors.general && (
            <p className="auth-error-general">{errors.general}</p>
          )}

          <Button
            type="submit"
            size="lg"
            className="auth-submit"
            disabled={loading}
          >
            {loading ? 'Signing in…' : 'Sign in'}
          </Button>
        </form>

        <p className="register-login">
          <Link to="/forgot-password">Forgot password?</Link>
        </p>

        <p className="register-login">
          Don't have an account? <Link to="/register">Sign up</Link>
        </p>
      </Card>
    </div>
  )
}