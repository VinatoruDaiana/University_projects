import type { ReactNode } from 'react'
import Card from './Card'
import { ArrowRightIcon } from '../assets/icons'
import './FeatureCard.css'

interface FeatureCardProps {
  icon: ReactNode
  title: string
  desc: string
}

export default function FeatureCard({ icon, title, desc }: FeatureCardProps) {
  return (
    <Card className="feature-card">
      <div className="feature-card__icon">{icon}</div>
      <h3 className="feature-card__title">{title}</h3>
      <p className="feature-card__desc">{desc}</p>
      <div className="feature-card__arrow">
        <ArrowRightIcon />
        Learn more
      </div>
    </Card>
  )
}
