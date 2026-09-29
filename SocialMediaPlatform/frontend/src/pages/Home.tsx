import { Link } from 'react-router-dom'
import Button from '../components/Button'
import Layout from '../components/Layout'
import FeatureCard from '../components/FeatureCard'
import TestimonialCard from '../components/TestimonialCard'
import { NetworkIcon, ShareWorldIcon, PrivacyIcon } from '../assets/icons'
import './Home.css'

const features = [
  {
    icon: <NetworkIcon />,
    title: 'Build Your Network',
    desc: 'Connect with people who share your passions. Follow creators, meet friends, and grow your community organically.',
  },
  {
    icon: <ShareWorldIcon />,
    title: 'Share Your World',
    desc: 'Post photos, stories, and moments. Express yourself and inspire the people around you every single day.',
  },
  {
    icon: <PrivacyIcon />,
    title: 'Stay in Control',
    desc: 'Your privacy, your rules. Choose exactly who sees what, and manage your digital presence with full confidence.',
  },
]

const stats = [
  { value: '50K+', label: 'Active Users' },
  { value: '2M+', label: 'Posts Shared' },
  { value: '150+', label: 'Countries' },
  { value: '99.9%', label: 'Uptime' },
]

const testimonials = [
  {
    text: "Pulse changed how I stay in touch with my creative community. The interface feels like it was made just for me.",
    name: "Alex M.",
    role: "Photographer",
    avatarClass: 'avatar-gradient--1',
  },
  {
    text: "Finally a platform that respects both my creativity and my privacy. Couldn't ask for more.",
    name: "Irina S.",
    role: "Designer",
    avatarClass: 'avatar-gradient--2',
  },
  {
    text: "The community here is unlike anything else. Real people, real connections, real conversations.",
    name: "Dan P.",
    role: "Developer",
    avatarClass: 'avatar-gradient--3',
  },
]

const trustBrands = ['Dribbble', 'Behance', 'ProductHunt', 'Indie Hackers', 'Dev.to']

export default function Home() {
  return (
    <Layout>
    <div className="home">

      {/* HERO */}
      <section className="hero">
        <div className="hero__orb hero__orb--1" />
        <div className="hero__orb hero__orb--2" />
        <div className="hero__orb hero__orb--3" />

        <div className="hero__content">
          <div className="hero__badge">
            <span className="hero__badge-dot" />
            Now in public beta
          </div>
          <h1 className="hero__title">Connect.<br />Share.<br />Belong.</h1>
          <p className="hero__subtitle">
            The next-gen social platform where every voice matters.
            Join thousands of people sharing ideas, stories, and moments that inspire.
          </p>
          <div className="hero__actions">
            <Link to="/register">
              <Button variant="primary" size="lg">Start for free</Button>
            </Link>
            <a href="#features">
              <Button variant="outline" size="lg">See how it works</Button>
            </a>
          </div>
          <p className="hero__disclaimer">No credit card required · Free forever</p>
        </div>

        <div className="hero__visual" aria-hidden="true">
          <div className="hero__card hero__card--main">
            <div className="hero__card-header">
              <div className="hero__avatar avatar-gradient--1" />
              <div>
                <div className="hero__username">Alexandra M.</div>
                <div className="hero__time">2 minutes ago</div>
              </div>
            </div>
            <div className="hero__post-img" />
            <div className="hero__post-lines">
              <div className="hero__line hero__line--wide" />
              <div className="hero__line hero__line--medium" />
            </div>
            <div className="hero__reactions">
              <span className="hero__reaction">❤️ <strong>248</strong></span>
              <span className="hero__reaction">💬 <strong>32</strong></span>
              <span className="hero__reaction">🔁 <strong>18</strong></span>
            </div>
          </div>

          <div className="hero__card hero__card--secondary">
            <div className="hero__card-header">
              <div className="hero__avatar hero__avatar--sm avatar-gradient--2" />
              <div>
                <div className="hero__username hero__username--sm">Daniel P.</div>
                <div className="hero__time">Just now</div>
              </div>
            </div>
            <div className="hero__post-lines">
              <div className="hero__line hero__line--wide" />
              <div className="hero__line hero__line--short" />
            </div>
            <div className="hero__reactions hero__reactions--sm">
              <span className="hero__reaction">❤️ <strong>41</strong></span>
              <span className="hero__reaction">💬 <strong>7</strong></span>
            </div>
          </div>

          <div className="hero__card hero__card--notif">
            <div className="hero__online-dot" />
            <div className="hero__avatar hero__avatar--sm avatar-gradient--3" />
            <div>
              <div className="hero__username hero__username--sm">Irina S.</div>
              <div className="hero__time">liked your post</div>
            </div>
          </div>

          <div className="hero__card hero__card--stat">
            <div className="hero__stat-value">+24%</div>
            <div className="hero__stat-label">Reach this week</div>
            <div className="hero__stat-bar">
              <div className="hero__stat-fill" />
            </div>
          </div>
        </div>
      </section>

      {/* TRUST STRIP */}
      <div className="trust-strip">
        <p className="trust-strip__label">Trusted by creators worldwide</p>
        <div className="trust-strip__logos">
          {trustBrands.map((name) => (
            <span key={name} className="trust-strip__logo">{name}</span>
          ))}
        </div>
      </div>

      {/* FEATURES */}
      <section className="features" id="features">
        <div className="features__inner">
          <div className="features__header">
            <p className="section-eyebrow">Why Pulse?</p>
            <h2 className="section-title">Everything you need<br />to thrive online</h2>
            <p className="section-sub">Designed from the ground up for real people who want real connections — not just more noise.</p>
          </div>
          <div className="features__grid">
            {features.map((f) => (
              <FeatureCard key={f.title} icon={f.icon} title={f.title} desc={f.desc} />
            ))}
          </div>
        </div>
      </section>

      {/* STATS */}
      <section className="stats">
        <div className="stats__glow" />
        <div className="stats__inner">
          {stats.map((s) => (
            <div key={s.label} className="stat">
              <span className="stat__value">{s.value}</span>
              <span className="stat__label">{s.label}</span>
            </div>
          ))}
        </div>
      </section>

      {/* TESTIMONIALS */}
      <section className="testimonials" id="community">
        <div className="testimonials__inner">
          <p className="section-eyebrow">Community love</p>
          <h2 className="section-title">People are talking</h2>
          <div className="testimonials__grid">
            {testimonials.map((t) => (
              <TestimonialCard key={t.name} {...t} />
            ))}
          </div>
        </div>
      </section>

      {/* CTA */}
      <section className="cta-section" id="about">
        <div className="cta-section__bg" />
        <div className="cta-section__inner">
          <p className="section-eyebrow">Join the community</p>
          <h2 className="cta-section__title">Ready to find<br />your people?</h2>
          <p className="cta-section__sub">
            Sign up in seconds. No ads, no algorithms tricking you.<br />
            Just genuine connections.
          </p>
          <Link to="/register">
            <Button variant="primary" size="lg">Create your free account</Button>
          </Link>
          <p className="cta-section__fine">
            Already have an account?{' '}
            <Link to="/login" className="cta-section__link">Sign in</Link>
          </p>
        </div>
      </section>

    </div>
    </Layout>
  )
}
