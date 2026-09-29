import { Link } from 'react-router-dom'
import '../pages/Home.css'

export default function Footer() {
  return (
    <footer className="home-footer">
      <div className="home-footer__inner">
        <Link to="/" className="navbar__logo">Pulse</Link>
        <p className="home-footer__copy">© 2026 Pulse. Built with ❤️ for the community.</p>
        <div className="home-footer__links">
          <a href="#">Privacy</a>
          <a href="#">Terms</a>
          <a href="#">Contact</a>
        </div>
      </div>
    </footer>
  )
}
