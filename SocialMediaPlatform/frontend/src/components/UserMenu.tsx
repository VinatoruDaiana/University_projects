import { useState, useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'
import { UserIcon, SettingsIcon, SignOutIcon } from '../assets/icons'
import './UserMenu.css'

interface User {
  username: string
  email: string
  photoUrl?: string
  role?: string
}

interface UserMenuProps {
  user: User
  avatarVariant: number
  onLogout: () => void
}

export default function UserMenu({ user, avatarVariant, onLogout }: UserMenuProps) {
  const [open, setOpen] = useState(false)
  const menuRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    function onMouseDown(e: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setOpen(false)
      }
    }
    document.addEventListener('mousedown', onMouseDown)
    return () => document.removeEventListener('mousedown', onMouseDown)
  }, [])

  function handleItemClick() {
    setOpen(false)
  }

  return (
    <div className="user-menu" ref={menuRef}>
      <button
        className="user-menu__trigger"
        onClick={() => setOpen((v) => !v)}
        aria-label="User menu"
        aria-expanded={open}
      >
        <div className={`user-menu__avatar${user.photoUrl ? ' user-menu__avatar--photo' : ` avatar-gradient--${avatarVariant}`}`}>
          {user.photoUrl
            ? <img src={user.photoUrl} alt={user.username} className="user-menu__avatar-img" />
            : (user.username?.[0] ?? user.email?.[0] ?? '?').toUpperCase()}
        </div>
      </button>

      {open && (
        <div className="user-menu__dropdown">
          <div className="user-menu__header">
            <div className={`user-menu__avatar user-menu__avatar--lg${user.photoUrl ? ' user-menu__avatar--photo' : ` avatar-gradient--${avatarVariant}`}`}>
              {user.photoUrl
                ? <img src={user.photoUrl} alt={user.username} className="user-menu__avatar-img" />
                : (user.username?.[0] ?? user.email?.[0] ?? '?').toUpperCase()}
            </div>
            <div className="user-menu__info">
              <div className="user-menu__name">{user.username}</div>
              <div className="user-menu__email">{user.email}</div>
            </div>
          </div>

          <div className="user-menu__divider" />

          <Link
            to={user.role === 'ADMIN' ? '/admin' : `/profile/${user.username}`}
            className="user-menu__item"
            onClick={handleItemClick}
          >
            <UserIcon />
            {user.role === 'ADMIN' ? 'Dashboard' : 'Profile'}
          </Link>

          <Link
            to="/settings"
            className="user-menu__item"
            onClick={handleItemClick}
          >
            <SettingsIcon />
            Settings
          </Link>

          <div className="user-menu__divider" />

          <button className="user-menu__item user-menu__item--danger" onClick={onLogout}>
            <SignOutIcon />
            Sign out
          </button>
        </div>
      )}
    </div>
  )
}
