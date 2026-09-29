import { useState } from 'react'
import { Link } from 'react-router-dom'
import Card from '../components/Card'
import Input from '../components/Input'
import Button from '../components/Button'
import { requestPasswordReset } from '../api/auth'
import './Auth.css'

export default function ForgotPassword() {
  const [email, setEmail] = useState('')
  const [emailError, setEmailError] = useState('')
  const [loading, setLoading] = useState(false)
  const [submitted, setSubmitted] = useState(false)

  function validate(): boolean {
    if (!email.trim()) {
      setEmailError('Email is required')
      return false
    }
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      setEmailError('Enter a valid email address')
      return false
    }
    return true
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!validate()) return

    setLoading(true)
    try {
      await requestPasswordReset(email)
    } catch {
      // Swallow errors — never reveal if email exists
    } finally {
      setLoading(false)
      setSubmitted(true)
    }
  }

  return (
    <div className="auth-page">
      <Card className="auth-card">
        <div className="auth-header">
          <span className="auth-brand">DISI</span>
          <p className="auth-subtitle">Reset your password</p>
        </div>

        {submitted ? (
          <div className="forgot-success">
            <p>If that email is registered, you will receive a reset link shortly.</p>
            <p className="register-login" style={{ marginTop: 16 }}>
              <Link to="/login">Back to Sign in</Link>
            </p>
          </div>
        ) : (
          <>
            <form className="auth-form" onSubmit={handleSubmit} noValidate>
              <Input
                id="email"
                label="Email"
                type="email"
                placeholder="john@email.com"
                value={email}
                onChange={e => {
                  setEmail(e.target.value)
                  if (emailError) setEmailError('')
                }}
                error={emailError}
              />

              <Button
                type="submit"
                size="lg"
                className="auth-submit"
                disabled={loading}
              >
                {loading ? 'Sending…' : 'Send reset link'}
              </Button>
            </form>

            <p className="register-login">
              Remembered it? <Link to="/login">Sign in</Link>
            </p>
          </>
        )}
      </Card>
    </div>
  )
}
