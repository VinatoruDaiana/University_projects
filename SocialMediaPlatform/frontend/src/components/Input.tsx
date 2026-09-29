import './Input.css'
import type { InputHTMLAttributes } from 'react'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
}

export default function Input({ label, error, id, className = '', ...props }: InputProps) {
  return (
    <div className={`input-wrapper ${className}`.trim()}>
      {label && <label htmlFor={id} className="input-label">{label}</label>}
      <input id={id} className={`input ${error ? 'input--error' : ''}`} {...props} />
      {error && <span className="input-error">{error}</span>}
    </div>
  )
}
