import Card from './Card'
import './TestimonialCard.css'

interface TestimonialCardProps {
  text: string
  name: string
  role: string
  avatarClass: string
}

export default function TestimonialCard({ text, name, role, avatarClass }: TestimonialCardProps) {
  return (
    <Card className="testimonial-card">
      <p className="testimonial-card__text">"{text}"</p>
      <div className="testimonial-card__author">
        <div className={`testimonial-card__avatar ${avatarClass}`} />
        <div>
          <div className="testimonial-card__name">{name}</div>
          <div className="testimonial-card__role">{role}</div>
        </div>
      </div>
    </Card>
  )
}
