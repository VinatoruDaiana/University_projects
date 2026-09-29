import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { isAxiosError } from 'axios'
import Card from '../components/Card'
import Input from '../components/Input'
import Button from '../components/Button'
import { confirmPasswordReset } from '../api/auth'
import './Auth.css'

type FormFields = { newPassword: string; confirmPassword: string }
type FormErrors = Partial<Record<keyof FormFields | 'general', string>>

function validate({ newPassword, confirmPassword }: FormFields): FormErrors {
  const errors: FormErrors = {}

  if (!newPassword) {
    errors.newPassword = 'Password is required'
  } else if (newPassword.length < 8) {
    errors.newPassword = 'Password must be at least 8 characters'
  } else if (!/[A-Z]/.test(newPassword)) {
    errors.newPassword = 'Password must contain at least one uppercase letter'
  } else if (!/[a-z]/.test(newPassword)) {
    errors.newPassword = 'Password must contain at least one lowercase letter'
  } else if (!/[0-9]/.test(newPassword)) {
    errors.newPassword = 'Password must contain at least one number'
  }

  if (!confirmPassword) {
    errors.confirmPassword = 'Please confirm your password'
  } else if (newPassword !== confirmPassword) {
    errors.confirmPassword = 'Passwords do not match'
  }

  return errors
}

export default function ResetPassword() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token') ?? ''

  const [form, setForm] = useState<FormFields>({ newPassword: '', confirmPassword: '' })
  const [errors, setErrors] = useState<FormErrors>({})
  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState(false)

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    const { id, value } = e.target
    setForm(prev => ({ ...prev, [id]: value }))
    if (errors[id as keyof FormErrors]) setErrors(prev => ({ ...prev, [id]: undefined }))
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()

    if (!token) {
      setErrors({ general: 'Invalid or missing reset token.' })
      return
    }

    const validationErrors = validate(form)
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors)
      return
    }

    setLoading(true)
    setErrors({})

    try {
      await confirmPasswordReset(token, form.newPassword)
      setSuccess(true)
      setTimeout(() => navigate('/login'), 3000)
    } catch (err) {
      if (isAxiosError(err) && err.response) {
        const msg = err.response.data?.message
        if (msg === 'Token has already been used') {
          setErrors({ general: 'This reset link has already been used.' })
        } else if (msg === 'Token has expired') {
          setErrors({ general: 'This reset link has expired. Please request a new one.' })
        } else {
          setErrors({ general: 'Invalid or expired reset link.' })
        }
      } else {
        setErrors({ general: 'Something went wrong. Please try again.' })
      }
    } finally {
      setLoading(false)
    }
  }

  if (!token) {
    return (
      <div className="auth-page">
        <Card className="auth-card">
          <div className="auth-header">
            <span className="auth-brand">DISI</span>
          </div>
          <p className="auth-error-general">Invalid or missing reset token.</p>
          <p className="register-login" style={{ marginTop: 16 }}>
            <Link to="/forgot-password">Request a new link</Link>
          </p>
        </Card>
      </div>
    )
  }

  return (
    <div className="auth-page">
      <Card className="auth-card">
        <div className="auth-header">
          <span className="auth-brand">DISI</span>
          <p className="auth-subtitle">Set a new password</p>
        </div>

        {success ? (
          <div className="forgot-success">
            <p>Password reset successful! Redirecting to sign in…</p>
            <p className="register-login" style={{ marginTop: 16 }}>
              <Link to="/login">Sign in now</Link>
            </p>
          </div>
        ) : (
          <>
            <form className="auth-form" onSubmit={handleSubmit} noValidate>
              <Input
                id="newPassword"
                label="New password"
                type="password"
                placeholder="••••••••"
                value={form.newPassword}
                onChange={handleChange}
                error={errors.newPassword}
              />
              <p className="auth-hint">Min. 8 characters, uppercase, lowercase and number</p>
              <Input
                id="confirmPassword"
                label="Confirm new password"
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
                {loading ? 'Saving…' : 'Reset password'}
              </Button>
            </form>

            <p className="register-login">
              <Link to="/forgot-password">Request a new link</Link>
            </p>
          </>
        )}
      </Card>
    </div>
  )
}
