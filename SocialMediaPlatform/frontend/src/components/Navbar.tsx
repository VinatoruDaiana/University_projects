import { useState, useEffect, useRef } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Button from './Button'
import UserMenu from './UserMenu'
import { SearchIcon, CloseIcon } from '../assets/icons'
import { getFriendsList, acceptFriendRequest, declineFriendRequest } from '../api/friends'
import { searchUsers, UserSearchResult, getUserByUsername} from '../api/users'
import { getNotifications, markAsRead } from '../api/notifications'
import type { Friendship } from '../models/friends'
import type { Notification } from '../models/notification'
import './Navbar.css'

interface User {
  id?: number | string
  userId?: number | string
  username: string
  email: string
  photoUrl?: string
}

function getAvatarVariant(username: string | undefined): number {
  if (!username) return 1
  return (username.charCodeAt(0) % 3) + 1
}

function getUserId(u: any): string | null {
  if (!u) return null;
  if (u.id !== undefined && u.id !== null) return String(u.id);
  if (u.userId !== undefined && u.userId !== null) return String(u.userId);
  return null;
}

export default function Navbar() {
  const [user, setUser] = useState<User | null>(() => {
    try {
      return JSON.parse(localStorage.getItem('pulse_user') ?? 'null')
    } catch {
      return null
    }
  })

  const [query, setQuery]             = useState('')
  const [results, setResults]         = useState<UserSearchResult[]>([])
  const [showResults, setShowResults] = useState(false)
  // loading state
  const [isSearching, setIsSearching] = useState(false)

  const [friends, setFriends] = useState<Friendship[]>([])
  const [friendsLoading, setFriendsLoading] = useState(false)
  const [showFriendsMenu, setShowFriendsMenu] = useState(false)

  const [notifications, setNotifications] = useState<Notification[]>([])
  const [showNotifications, setShowNotifications] = useState(false)
  const notificationsRef = useRef<HTMLDivElement>(null)

  const [requestPhotos, setRequestPhotos] = useState<Record<string, string | null>>({})
  const fetchedUsernamesRef = useRef(new Set<string>())

  const searchRef = useRef<HTMLDivElement>(null)
  const friendsRef = useRef<HTMLDivElement>(null)
  const navigate  = useNavigate()

  useEffect(() => {
    const q = query.trim().toLowerCase()
    if (q.length < 2) {
      setResults([])
      setIsSearching(false)
      return
    }

    setIsSearching(true)
    const delayDebounceFn = setTimeout(() => {
      searchUsers(q)
        .then((data) => {
          setResults(data)
        })
        .catch((err) => {
          console.error("Failed to search users:", err)
          setResults([])
        })
        .finally(() => {
          setIsSearching(false)
        })
    }, 300)
    return () => clearTimeout(delayDebounceFn)
  }, [query])

  useEffect(() => {
    function onMouseDown(e: MouseEvent) {
      if (searchRef.current && !searchRef.current.contains(e.target as Node)) {
        setShowResults(false)
      }
      if (friendsRef.current && !friendsRef.current.contains(e.target as Node)) {
        setShowFriendsMenu(false)
      }
      if (notificationsRef.current && !notificationsRef.current.contains(e.target as Node)) {
        setShowNotifications(false)
      }
    }
    document.addEventListener('mousedown', onMouseDown)
    return () => document.removeEventListener('mousedown', onMouseDown)
  }, [])

  useEffect(() => {
    function onProfileUpdated(e: Event) {
      const updated = (e as CustomEvent<User>).detail
      if (updated) setUser(updated)
    }
    window.addEventListener('profileUpdated', onProfileUpdated)
    return () => window.removeEventListener('profileUpdated', onProfileUpdated)
  }, [])

  function handleLogout() {
    localStorage.removeItem('pulse_user')
    localStorage.removeItem('jwt_token')
    setUser(null)
    navigate('/')
  }

  function handleResultClick(username: string) {
    setShowResults(false)
    setQuery('')
    navigate(`/profile/${username}`)
  }

  function handleToggleFriendsMenu() {
    const willShow = !showFriendsMenu;
    setShowFriendsMenu(willShow);

    if (willShow) {
      setFriendsLoading(true);
      getFriendsList()
          .then((data) => setFriends(data))
          .catch(() => console.error('Failed to load friends list.'))
          .finally(() => setFriendsLoading(false));
    }
  }

  const pendingRequests = friends.filter(f => {
    const addId = f.addressee_id || (f as any).addresseeId;
    const myId = getUserId(user);
    return f.status === 'PENDING' && myId !== null && addId !== null && String(addId) === myId;
  });

  useEffect(() => {
    pendingRequests.forEach(f => {
      if (!fetchedUsernamesRef.current.has(f.other_user_username)) {
        fetchedUsernamesRef.current.add(f.other_user_username)
        getUserByUsername(f.other_user_username)
          .then(u => setRequestPhotos(prev => ({ ...prev, [f.other_user_username]: u.photoUrl ?? null })))
          .catch(() => setRequestPhotos(prev => ({ ...prev, [f.other_user_username]: null })))
      }
    })
  }, [friends])

  return (
      <nav className="navbar">
        <div className="navbar__inner">

          <Link to={user ? "/feed" : "/"} className="navbar__logo">Pulse</Link>

          {user ? (
              <>
                <div className="navbar__search" ref={searchRef}>
                  <div className="navbar__search-wrap">
                    <SearchIcon className="navbar__search-icon" />
                    <input
                        className="navbar__search-input"
                        type="search"
                        placeholder="Search people..."
                        value={query}
                        autoComplete="off"
                        onChange={(e) => {
                          setQuery(e.target.value)
                          setShowResults(true)
                        }}
                        onFocus={() => {
                          if (query.trim().length >= 2) setShowResults(true)
                        }}
                    />
                    {query && (
                        <button
                            className="navbar__search-clear"
                            onClick={() => { setQuery(''); setResults([]); setShowResults(false) }}
                            aria-label="Clear search"
                        >
                          <CloseIcon />
                        </button>
                    )}
                  </div>

                  {showResults && query.trim().length >= 2 && (
                      <div className="navbar__dropdown">
                        {isSearching ? (
                            <p className="navbar__dropdown-empty">Searching...</p>
                        ) : results.length > 0 ? (
                            <>
                            <p className="navbar__dropdown-label">People</p>
                              {results.map((u) => {
                                const displayName = u.firstName && u.lastName
                                    ? `${u.firstName} ${u.lastName}`
                                    : (u.firstName || u.username);
                                const avatarVar = getAvatarVariant(u.username);

                                return (
                                  <button
                                      key={u.id}
                                      className="navbar__result"
                                      onClick={() => handleResultClick(u.username)}
                                  >
                                    {/* Use profile photo if available, fallback to letter avatar */}
                                    <div className={`navbar__result-avatar${u.photoUrl ? ' navbar__result-avatar--photo' : ` avatar-gradient--${avatarVar}`}`}>
                                      {u.photoUrl ? (
                                          <img src={u.photoUrl} alt={u.username} className="navbar__result-avatar-img" />
                                      ) : (
                                          displayName[0].toUpperCase()
                                      )}
                                    </div>
                                    <div className="navbar__result-info">
                                      <span className="navbar__result-name">{displayName}</span>
                                      <span className="navbar__result-username">@{u.username}</span>
                                    </div>
                                  </button>
                                )
                              })}
                            </>
                        ) : (
                            <p className="navbar__dropdown-empty">
                              No users found for "<strong>{query}</strong>"
                            </p>
                        )}
                      </div>
                  )}
                </div>

                <div className="navbar__right-actions">
                  <div className="navbar__friends" ref={notificationsRef}>
                    <button
                        className="navbar__friends-trigger"
                        onClick={() => {
                          const willShow = !showNotifications
                          setShowNotifications(willShow)
                          if (willShow) {
                            getNotifications().then(setNotifications).catch(() => {})
                          }
                        }}
                        aria-label="Notifications"
                    >
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                        <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                      </svg>
                      {notifications.filter(n => !n.isRead).length > 0 && (
                        <span className="navbar__friends-badge" />
                      )}
                    </button>
                    {showNotifications && (
                      <div className="navbar__dropdown navbar__dropdown--friends">
                        <p className="navbar__dropdown-label">Notifications</p>
                        <div className="navbar__friends-list">
                          {notifications.length === 0 ? (
                            <p className="navbar__dropdown-empty">No notifications yet.</p>
                          ) : (
                            notifications.map(n => (
                              <div
                                key={n.notificationId}
                                className={`navbar__result navbar__result--friend${n.read ? '' : ' navbar__result--unread'}`}
                                onClick={() => {
                                  markAsRead(n.notificationId).then(() => {
                                    setNotifications(prev => prev.map(x => x.notificationId === n.notificationId ? {...x, isRead: true} : x))
                                  }).catch(() => {})
                                  if (n.referenceId) navigate(`/post/${n.referenceId}`)
                                  setShowNotifications(false)
                                }}
                              >
                                <div className="navbar__result-info">
                                  <span className="navbar__result-name">{n.content}</span>
                                  <span className="navbar__result-username">{new Date(n.createdAt).toLocaleString()}</span>
                                </div>
                                {!n.isRead && <span className="navbar__unread-dot" />}
                              </div>
                            ))
                          )}
                        </div>
                      </div>
                    )}
                  </div>
                  <Link
                      to="/chat"
                      className="navbar__friends-trigger"
                      style={{ marginRight: '8px', textDecoration: 'none' }}
                      aria-label="Open Chat"
                  >
                    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
                    </svg>
                  </Link>
                  <div className="navbar__friends" ref={friendsRef}>
                    <button
                        className="navbar__friends-trigger"
                        onClick={handleToggleFriendsMenu}
                        aria-label="Friends and Requests"
                    >
                      <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                        <circle cx="9" cy="7" r="4"></circle>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
                      </svg>
                      {pendingRequests.length > 0 && (
                          <span className="navbar__friends-badge" />
                      )}
                    </button>

                    {showFriendsMenu && (
                        <div className="navbar__dropdown navbar__dropdown--friends">
                          <p className="navbar__dropdown-label">Friend Requests</p>
                          <div className="navbar__friends-list">
                            {friendsLoading ? (
                                <p className="navbar__dropdown-empty">Loading...</p>
                            ) : pendingRequests.length === 0 ? (
                                <p className="navbar__dropdown-empty">No pending requests.</p>
                            ) : (
                                pendingRequests.map((f) => (
                                    <div
                                        key={f.request_id}
                                        className="navbar__result navbar__result--friend"
                                        onClick={() => { setShowFriendsMenu(false); navigate(`/profile/${f.other_user_username}`) }}
                                    >
                                      <div className={`navbar__result-avatar${requestPhotos[f.other_user_username] ? ' navbar__result-avatar--photo' : ` avatar-gradient--${(f.other_user_username.charCodeAt(0) % 3) + 1}`}`}>
                                        {requestPhotos[f.other_user_username]
                                          ? <img src={requestPhotos[f.other_user_username]!} alt={f.other_user_username} className="navbar__result-avatar-img" />
                                          : f.other_user_username[0].toUpperCase()}
                                      </div>
                                      <div className="navbar__result-info">
                                        <span className="navbar__result-name">{f.other_user_username}</span>
                                      </div>
                                      <div className="navbar__friend-actions">
                                        <button
                                            className="navbar__friend-btn navbar__friend-btn--accept"
                                            onClick={(e) => {
                                              e.stopPropagation()
                                              acceptFriendRequest(f.request_id).then(() => {
                                                setFriends(prev => prev.filter(fr => fr.request_id !== f.request_id))
                                                window.dispatchEvent(new CustomEvent('friendsUpdated'))
                                              }).catch(() => console.error('Failed to accept friend request'))
                                            }}
                                        >
                                          ✓
                                        </button>
                                        <button
                                            className="navbar__friend-btn navbar__friend-btn--decline"
                                            onClick={(e) => {
                                              e.stopPropagation()
                                              declineFriendRequest(f.request_id).then(() => {
                                                setFriends(prev => prev.filter(fr => fr.request_id !== f.request_id))
                                              }).catch(() => console.error('Failed to decline friend request'))
                                            }}
                                        >
                                          ✕
                                        </button>
                                      </div>
                                    </div>
                                ))
                            )}
                          </div>
                        </div>
                    )}
                  </div>

                  <UserMenu
                      user={user as any}
                      avatarVariant={getAvatarVariant(user.username)}
                      onLogout={handleLogout}
                  />
                </div>
              </>
          ) : (
              <>
                <div className="navbar__links">
                  <a href="#features">Features</a>
                  <a href="#community">Community</a>
                  <a href="#about">About</a>
                </div>
                <div className="navbar__actions">
                  <Link to="/login" className="navbar__signin">Sign In</Link>
                  <Link to="/register">
                    <Button variant="primary" size="sm">Get Started</Button>
                  </Link>
                </div>
              </>
          )}
        </div>
      </nav>
  )
}