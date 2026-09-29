import './Card.css'
import type { ReactNode } from 'react'
import React from 'react'

interface CardProps {
  children: ReactNode
  className?: string
  style?: React.CSSProperties
  onClick?: () => void
}

export default function Card({ children, className = '', style, onClick }: CardProps) {
  return (
    <div className={`card ${className}`.trim()} style={style} onClick={onClick}>
      {children}
    </div>
  )
}
