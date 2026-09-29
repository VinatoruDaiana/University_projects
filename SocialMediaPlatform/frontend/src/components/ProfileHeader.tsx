import Card from './Card'
import { LocationIcon, CalendarIcon } from '../assets/icons'
import type { User } from '../models/user'
import '../pages/Profile.css'

interface ProfileHeaderProps {
  user: User
  postCount: number
  onEditClick: () => void
}

function getAvatarVariant(username: string | undefined): number {
  if (!username) return 1
  return (username.charCodeAt(0) % 3) + 1
}

function getAvatarLetter(user: User): string {
  const src = user.firstName || user.lastName || user.username || user.email || '?'
  return src[0].toUpperCase()
}

export default function ProfileHeader({ user, postCount, onEditClick }: ProfileHeaderProps) {
  const avatarVariant = getAvatarVariant(user.username)
  const displayName = (user.firstName || user.lastName)
    ? `${user.firstName ?? ''} ${user.lastName ?? ''}`.trim()
    : user.username

  return (
    <Card className="profile-header-card">
      <div className="profile-avatar-wrap">
        {user.photoUrl ? (
          <img
            className="profile-avatar profile-avatar--photo"
            src={user.photoUrl}
            alt={user.username}
          />
        ) : (
          <div className={`profile-avatar avatar-gradient--${avatarVariant}`}>
            {getAvatarLetter(user)}
          </div>
        )}
      </div>

      <div className="profile-info">
        <h2 className="profile-username">{displayName}</h2>
        {(user.firstName || user.lastName) && (
          <p className="profile-handle">@{user.username}</p>
        )}
        <p className="profile-email">{user.email}</p>
        {user.bio && <p className="profile-bio">{user.bio}</p>}
        <div className="profile-meta">
          {user.location && (
            <span className="profile-meta__item">
              <LocationIcon />
              {user.location}
            </span>
          )}
          {user.birthdate && (
            <span className="profile-meta__item">
              <CalendarIcon />
              {new Date(user.birthdate).toLocaleDateString(undefined, { year: 'numeric', month: 'long', day: 'numeric' })}
            </span>
          )}
        </div>
      </div>

      <div className="profile-header-right">
        <div className="profile-stats">
          <div className="profile-stat">
            <span className="profile-stat__value">{postCount}</span>
            <span className="profile-stat__label">Posts</span>
          </div>
        </div>
        <button className="profile-edit-btn" onClick={onEditClick}>
          Edit profile
        </button>
      </div>
    </Card>
  )
}
