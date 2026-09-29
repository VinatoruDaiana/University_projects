import { useState, useEffect, useRef } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { isAxiosError } from 'axios'
import Layout from '../components/Layout'
import Card from '../components/Card'
import Button from '../components/Button'
import { LocationIcon, CalendarIcon, EditIcon } from '../assets/icons'
import PostCard from '../components/PostCard'
import PostCreate from '../components/PostCreate'
import { getUserPosts } from '../api/posts'
import { updateProfile, getUserByUsername, type UpdateProfileRequest } from '../api/users'
import { sendFriendRequest, getFriendsList } from '../api/friends'
import { getUserAlbums, createAlbum, updateAlbum, deleteAlbum } from '../api/albums'
import type { User } from '../models/user'
import type { Post } from '../models/post'
import type { Album } from '../models/album'
import type { Friendship } from '../models/friends'
import './Profile.css'

function getAvatarVariant(username: string | undefined): number {
  if (!username) return 1
  return (username.charCodeAt(0) % 3) + 1
}

function getAvatarLetter(user: User): string {
  const src = user.firstName || user.lastName || user.username || user.email || '?'
  return src[0].toUpperCase()
}

function getUserId(u: any): string | null {
  if (!u) return null;
  if (u.id !== undefined && u.id !== null) return String(u.id);
  if (u.userId !== undefined && u.userId !== null) return String(u.userId);
  return null;
}

const ALLOWED_PHOTO_TYPES = ['image/jpeg', 'image/png']
const MAX_PHOTO_SIZE = 5 * 1024 * 1024

export default function Profile() {
  const navigate = useNavigate()
  const { username: profileUsername } = useParams()
  const fileInputRef = useRef<HTMLInputElement>(null)

  const loggedInUser = JSON.parse(localStorage.getItem('pulse_user') ?? 'null')

  const [user, setUser] = useState<User | null>(null)
  const [activeTab, setActiveTab] = useState<'posts' | 'albums' | 'friends'>('posts')

  const [posts, setPosts] = useState<Post[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [albums, setAlbums] = useState<Album[]>([])
  const [albumsLoading, setAlbumsLoading] = useState(false)
  const [albumsError, setAlbumsError] = useState('')
  const [showCreateAlbum, setShowCreateAlbum] = useState(false)
  const [newAlbumName, setNewAlbumName] = useState('')
  const [creatingAlbum, setCreatingAlbum] = useState(false)
  const [editingAlbumId, setEditingAlbumId] = useState<number | null>(null)
  const [editAlbumName, setEditAlbumName] = useState('')
  const [deletingAlbumId, setDeletingAlbumId] = useState<number | null>(null)

  const [, setFriends] = useState<Friendship[]>([])
  const [profileFriends, setProfileFriends] = useState<Friendship[]>([])
  const [relationship, setRelationship] = useState<Friendship | null>(null)
  const [friendPhotos, setFriendPhotos] = useState<Record<string, string | null>>({})
  const fetchedFriendPhotosRef = useRef(new Set<string>())

  const [editing, setEditing] = useState(false)
  const [saving, setSaving] = useState(false)
  const [saveError, setSaveError] = useState<string | null>(null)
  const [selectedPhotoFile, setSelectedPhotoFile] = useState<File | null>(null)

  const [requestStatus, setRequestStatus] = useState<'idle' | 'loading' | 'success'>('idle')
  const [requestError, setRequestError] = useState<string | null>(null)

  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    bio: '',
    birthdate: '',
    location: '',
    photoUrl: '',
  })

  useEffect(() => {
    async function loadProfile() {
      if (!profileUsername) return

      setLoading(true)
      setError(null)
      setRequestStatus('idle')
      setRequestError(null)

      try {
        let targetUser = loggedInUser
        if (profileUsername !== loggedInUser?.username) {
          targetUser = await getUserByUsername(profileUsername)
        }

        setUser(targetUser)
        setForm({
          firstName: targetUser.firstName ?? '',
          lastName: targetUser.lastName ?? '',
          bio: targetUser.bio ?? '',
          birthdate: targetUser.birthdate ?? '',
          location: targetUser.location ?? '',
          photoUrl: targetUser.photoUrl ?? '',
        })

        const targetId = getUserId(targetUser);
        if (targetId) {
          const userPosts = await getUserPosts(Number(targetId))
          setPosts([...userPosts].sort(
              (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
          ))
        }

        const friendsData = await getFriendsList()
        setFriends(friendsData)

        const myId = getUserId(loggedInUser)
        const isOwnProfile = (myId !== null && targetId !== null && myId === targetId) || (loggedInUser?.username === targetUser.username)

        if (isOwnProfile) {
          setProfileFriends(friendsData.filter(f => f.status === 'ACCEPTED'))
          setRelationship(null)
        } else {
          setProfileFriends([])

          const rel = friendsData.find(f => {
            const otherId = f.other_user_id || (f as any).otherUserId;
            const otherUsername = f.other_user_username || (f as any).otherUserUsername;

            const matchByUsername = otherUsername && targetUser.username && otherUsername === targetUser.username;
            const matchById = otherId && targetId && String(otherId) === targetId;

            return matchByUsername || matchById;
          });

          setRelationship(rel || null)
        }

      } catch (err) {
        setError('User not found')
        setUser(null)
      } finally {
        setLoading(false)
      }
    }

    loadProfile()
  }, [profileUsername])

  useEffect(() => {
    if (activeTab === 'albums' && user) {
      fetchAlbums()
    }
  }, [activeTab, user])

  useEffect(() => {
    async function refreshFriends() {
      try {
        const friendsData = await getFriendsList()
        setFriends(friendsData)
        setProfileFriends(friendsData.filter(f => f.status === 'ACCEPTED'))
      } catch {
        // ignore
      }
    }
    window.addEventListener('friendsUpdated', refreshFriends)
    return () => window.removeEventListener('friendsUpdated', refreshFriends)
  }, [])

  useEffect(() => {
    profileFriends.forEach(f => {
      const username = f.other_user_username || (f as any).otherUserUsername
      if (username && !fetchedFriendPhotosRef.current.has(username)) {
        fetchedFriendPhotosRef.current.add(username)
        getUserByUsername(username)
          .then(u => setFriendPhotos(prev => ({ ...prev, [username]: u.photoUrl ?? null })))
          .catch(() => setFriendPhotos(prev => ({ ...prev, [username]: null })))
      }
    })
  }, [profileFriends])

  useEffect(() => {
    function handlePostUpdated(e: Event) {
      const detail = (e as CustomEvent).detail;
      setPosts(prev => prev.map(p =>
        p.id === detail.id
          ? {
              ...p,
              content: detail.content,
              updatedAt: detail.updatedAt,
              photoId: detail.photoId,
              photoUrl: detail.photoUrl
            }
          : p
      ));
    }

    function handlePostDeleted(e: Event) {
      const detail = (e as CustomEvent).detail;
      setPosts(prev => prev.filter(p => p.id !== detail.id));
    }

    window.addEventListener('postUpdated', handlePostUpdated);
    window.addEventListener('postDeleted', handlePostDeleted);

    return () => {
      window.removeEventListener('postUpdated', handlePostUpdated);
      window.removeEventListener('postDeleted', handlePostDeleted);
    };
  }, []);

  function handleCreated(post: Post) {
    setPosts((prev) => [post, ...prev])
  }

  async function fetchAlbums() {
    if (!user) return
    setAlbumsLoading(true)
    setAlbumsError('')
    try {
      const uId = getUserId(user)
      if (uId) {
        const data = await getUserAlbums(Number(uId))
        setAlbums(data)
      }
    } catch {
      setAlbumsError('Failed to load albums.')
    } finally {
      setAlbumsLoading(false)
    }
  }

  async function handleCreateAlbum() {
    if (!newAlbumName.trim()) return
    setCreatingAlbum(true)
    try {
      const album = await createAlbum({ name: newAlbumName.trim() })
      setAlbums(prev => [...prev, album])
      setNewAlbumName('')
      setShowCreateAlbum(false)
    } catch {
      setAlbumsError('Failed to create album.')
    } finally {
      setCreatingAlbum(false)
    }
  }

  async function handleUpdateAlbum(albumId: number) {
    if (!editAlbumName.trim()) return
    try {
      const updated = await updateAlbum(albumId, { name: editAlbumName.trim() })
      setAlbums(prev => prev.map(a => a.albumId === albumId ? updated : a))
      setEditingAlbumId(null)
    } catch {
      setAlbumsError('Failed to rename album.')
    }
  }

  async function handleDeleteAlbum(albumId: number) {
    if (!window.confirm('Are you sure you want to delete this album?')) return
    setDeletingAlbumId(albumId)
    try {
      await deleteAlbum(albumId)
      setAlbums(prev => prev.filter(a => a.albumId !== albumId))
    } catch {
      setAlbumsError('Failed to delete album.')
    } finally {
      setDeletingAlbumId(null)
    }
  }

  function handlePhotoChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file) return
    if (!ALLOWED_PHOTO_TYPES.includes(file.type)) {
      setSaveError('Only jpg and png files are supported.')
      if (fileInputRef.current) fileInputRef.current.value = ''
      return
    }
    if (file.size > MAX_PHOTO_SIZE) {
      setSaveError('File size must be under 5MB.')
      if (fileInputRef.current) fileInputRef.current.value = ''
      return
    }
    setSaveError(null)
    setSelectedPhotoFile(file)
    const reader = new FileReader()
    reader.onload = () => {
      setForm((prev) => ({ ...prev, photoUrl: reader.result as string }))
    }
    reader.readAsDataURL(file)
  }

  function handleEditOpen() {
    setSaveError(null)
    setEditing(true)
  }

  function handleEditCancel() {
    setEditing(false)
    setSaveError(null)
    setSelectedPhotoFile(null)
  }

  async function handleSave() {
    if (!user) return
    setSaving(true)
    setSaveError(null)
    try {
      const uId = getUserId(user)
      if (!uId) return

      const profileUpdate: UpdateProfileRequest = {
        firstName: form.firstName,
        lastName: form.lastName,
        bio: form.bio,
        birthdate: form.birthdate,
        location: form.location,
        ...(selectedPhotoFile && form.photoUrl.startsWith('data:') ? { photoUrl: form.photoUrl } : {}),
      }

      const updated = await updateProfile(Number(uId), profileUpdate)
      const newUser = { ...user, ...updated }
      setUser(newUser)
      setForm(prev => ({ ...prev, photoUrl: updated.photoUrl ?? '' }))
      localStorage.setItem('pulse_user', JSON.stringify(newUser))
      window.dispatchEvent(new CustomEvent('profileUpdated', { detail: newUser }))
      setEditing(false)
      setSelectedPhotoFile(null)
    } catch (err) {
      const message = isAxiosError(err) ? err.response?.data?.message : undefined
      setSaveError(message ?? 'Failed to save profile. Please try again.')
    } finally {
      setSaving(false)
    }
  }

  async function handleAddFriend() {
    if (requestStatus === 'loading' || requestStatus === 'success' || !user) return

    setRequestStatus('loading')
    setRequestError(null)

    try {
      const targetId = getUserId(user)
      if (!targetId) return;

      await sendFriendRequest(targetId)
      setRequestStatus('success')

      const myId = getUserId(loggedInUser)
      setRelationship({
        request_id: 'temp',
        requester_id: myId || '',
        addressee_id: targetId,
        status: 'PENDING',
        other_user_id: targetId,
        other_user_username: user.username
      })
    } catch (error: any) {
      setRequestStatus('idle')
      if (isAxiosError(error)) {
        const status = error.response?.status
        if (status === 400) setRequestError('Invalid request parameters.')
        else if (status === 401) {
          localStorage.removeItem('pulse_user')
          localStorage.removeItem('jwt_token')
          navigate('/login')
        }
        else if (status === 403) setRequestError('You cannot send a request to yourself.')
        else if (status === 409) setRequestError('Friend request already exists.')
        else setRequestError('Something went wrong. Please try again.')
      } else {
        setRequestError('Something went wrong. Please try again.')
      }
    }
  }

  if (loading) return <Layout><div className="profile-feed__loading"><div className="profile-spinner" /></div></Layout>
  if (error || !user) return <Layout><div className="profile-feed__error">{error || 'User not found'}</div></Layout>

  const avatarVariant = getAvatarVariant(user.username)
  const displayName = (user.firstName || user.lastName)
      ? `${user.firstName ?? ''} ${user.lastName ?? ''}`.trim()
      : user.username

  const myId = getUserId(loggedInUser)
  const targetId = getUserId(user)

  const isOwnProfile = (myId !== null && targetId !== null && myId === targetId) || loggedInUser?.username === user.username
  const isLoggedInAdmin = loggedInUser?.role === 'ADMIN'
  const isBannedUser = user.status === 'BLOCKED' || user.isBanned === true
  const isFriend = relationship?.status === 'ACCEPTED'

  const reqId = relationship?.requester_id || (relationship as any)?.requesterId;
  const addId = relationship?.addressee_id || (relationship as any)?.addresseeId;

  const isPendingSent = relationship?.status === 'PENDING' && String(reqId) === myId
  const isPendingReceived = relationship?.status === 'PENDING' && String(addId) === myId

  const canMessageUser =
      loggedInUser?.role === 'ADMIN' ||
      (user as any)?.role === 'ADMIN' ||
      isFriend

  return (
      <Layout>
        <div className="profile-page">
          <div className="profile-content">

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
                    <span className="profile-stat__value">{posts.length}</span>
                    <span className="profile-stat__label">Posts</span>
                  </div>
                </div>

                {isOwnProfile ? (
                    <button className="profile-edit-btn" onClick={handleEditOpen}>
                      Edit profile
                    </button>
                ) : (
                    <div className="profile-add-friend-section">
                      {!isLoggedInAdmin && (
                        <>
                          {isFriend ? (
                              <button className="btn-add-friend btn-add-friend--success" disabled>Friend</button>
                          ) : isPendingSent ? (
                              <button className="btn-add-friend btn-add-friend--success" disabled>Request Sent</button>
                          ) : isPendingReceived ? (
                              <button className="btn-add-friend" disabled>Respond in Navbar</button>
                          ) : (
                              <button
                                  className={`btn-add-friend ${requestStatus === 'success' ? 'btn-add-friend--success' : ''}`}
                                  onClick={handleAddFriend}
                                  disabled={requestStatus === 'loading' || requestStatus === 'success'}
                              >
                                {requestStatus === 'idle' && 'Add Friend'}
                                {requestStatus === 'loading' && 'Sending...'}
                                {requestStatus === 'success' && 'Request Sent'}
                              </button>
                          )}
                          {requestError && <p className="profile-add-friend-error">{requestError}</p>}
                        </>
                      )}
                      {canMessageUser && (
                          <Button
                              variant="secondary"
                              onClick={() => navigate('/chat', {
                                state: {
                                  newUser: {
                                    userId: Number(getUserId(user)),
                                    username: user.username,
                                    email: user.email,
                                    role: (user as any).role || 'USER'
                                  }
                                }
                              })}
                          >
                            Message
                          </Button>
                      )}
                    </div>
                )}
              </div>
            </Card>

            {isBannedUser && (
                <div className="profile-banned-banner">
                  <span className="profile-banned-banner__icon">⛔</span>
                  <div>
                    <p className="profile-banned-banner__title">Account suspended</p>
                    <p className="profile-banned-banner__sub">This account has been suspended and is no longer active.</p>
                  </div>
                </div>
            )}

            {editing && !isBannedUser && (
                <Card className="profile-edit-card">
                  <h3 className="profile-edit-title">Edit profile</h3>

                  <div className="profile-edit-photo-row">
                    <div
                        className="profile-edit-photo-preview"
                        onClick={() => fileInputRef.current?.click()}
                        role="button"
                        tabIndex={0}
                        onKeyDown={(e) => e.key === 'Enter' && fileInputRef.current?.click()}
                        aria-label="Change profile photo"
                    >
                      {form.photoUrl ? (
                          <img src={form.photoUrl} alt="Preview" className="profile-edit-photo-img" />
                      ) : (
                          <div className={`profile-edit-photo-placeholder avatar-gradient--${avatarVariant}`}>
                            {getAvatarLetter(user)}
                          </div>
                      )}
                      <div className="profile-edit-photo-overlay">
                        <EditIcon />
                        Change photo
                      </div>
                    </div>
                    <input
                        ref={fileInputRef}
                        type="file"
                        accept=".jpg,.jpeg,.png"
                        className="profile-edit-file-input"
                        onChange={handlePhotoChange}
                    />
                  </div>

                  <div className="profile-edit-grid">
                    <div className="profile-edit-field">
                      <label className="profile-edit-label">First name</label>
                      <input
                          className="profile-edit-input"
                          type="text"
                          value={form.firstName}
                          onChange={(e) => setForm((p) => ({ ...p, firstName: e.target.value }))}
                          placeholder="First name"
                      />
                    </div>
                    <div className="profile-edit-field">
                      <label className="profile-edit-label">Last name</label>
                      <input
                          className="profile-edit-input"
                          type="text"
                          value={form.lastName}
                          onChange={(e) => setForm((p) => ({ ...p, lastName: e.target.value }))}
                          placeholder="Last name"
                      />
                    </div>
                    <div className="profile-edit-field profile-edit-field--full">
                      <label className="profile-edit-label">Bio</label>
                      <textarea
                          className="profile-edit-input profile-edit-textarea"
                          value={form.bio}
                          onChange={(e) => setForm((p) => ({ ...p, bio: e.target.value }))}
                          placeholder="Tell people a little about yourself"
                          rows={3}
                      />
                    </div>
                    <div className="profile-edit-field">
                      <label className="profile-edit-label">Birthdate</label>
                      <input
                          className="profile-edit-input"
                          type="date"
                          value={form.birthdate}
                          onChange={(e) => setForm((p) => ({ ...p, birthdate: e.target.value }))}
                      />
                    </div>
                    <div className="profile-edit-field">
                      <label className="profile-edit-label">Location</label>
                      <input
                          className="profile-edit-input"
                          type="text"
                          value={form.location}
                          onChange={(e) => setForm((p) => ({ ...p, location: e.target.value }))}
                          placeholder="City, Country"
                      />
                    </div>
                  </div>

                  {saveError && <p className="profile-edit-error">{saveError}</p>}

                  <div className="profile-edit-actions">
                    <button className="profile-edit-cancel" onClick={handleEditCancel} disabled={saving}>
                      Cancel
                    </button>
                    <button className="profile-edit-save" onClick={handleSave} disabled={saving}>
                      {saving ? 'Saving…' : 'Save changes'}
                    </button>
                  </div>
                </Card>
            )}

            {!isBannedUser && (<>
            <div className="profile-tabs">
              <button
                  className={`profile-tab ${activeTab === 'posts' ? 'profile-tab--active' : ''}`}
                  onClick={() => setActiveTab('posts')}
              >
                Posts
              </button>
              <button
                  className={`profile-tab ${activeTab === 'albums' ? 'profile-tab--active' : ''}`}
                  onClick={() => setActiveTab('albums')}
              >
                Albums
              </button>
              {isOwnProfile && (
                  <button
                      className={`profile-tab ${activeTab === 'friends' ? 'profile-tab--active' : ''}`}
                      onClick={() => setActiveTab('friends')}
                  >
                    Friends ({profileFriends.length})
                  </button>
              )}
            </div>

            {activeTab === 'posts' && (
                <>
                  {loggedInUser?.id === user.id && (
                      <Card className="profile-compose-card">
                        <p className="profile-compose-label">New post</p>
                        <PostCreate onCreated={handleCreated} />
                      </Card>
                  )}

                  <section className="profile-feed">
                    {!loading && !error && posts.length === 0 && <p className="profile-feed__empty">No posts yet. Share something!</p>}
                    {!loading && !error && posts.map((post) => <PostCard key={post.id} post={post} />)}
                  </section>
                </>
            )}

            {activeTab === 'albums' && (
                <section className="profile-albums">
                  {loggedInUser?.id === user.id && (
                      <div className="profile-albums-header">
                        <button className="profile-albums-create-btn" onClick={() => setShowCreateAlbum(true)}>
                          + Create Album
                        </button>
                      </div>
                  )}

                  {showCreateAlbum && (
                      <Card className="profile-albums-create-card">
                        <input
                            className="profile-albums-input"
                            placeholder="Album name"
                            value={newAlbumName}
                            onChange={e => setNewAlbumName(e.target.value)}
                        />
                        <div className="profile-albums-create-actions">
                          <button className="profile-edit-btn" onClick={handleCreateAlbum} disabled={creatingAlbum}>
                            {creatingAlbum ? 'Creating…' : 'Create'}
                          </button>
                          <button className="profile-edit-cancel" onClick={() => { setShowCreateAlbum(false); setNewAlbumName('') }}>
                            Cancel
                          </button>
                        </div>
                      </Card>
                  )}

                  {albumsError && <p className="profile-feed__error">{albumsError}</p>}

                  {albumsLoading ? (
                      <div className="profile-feed__loading"><div className="profile-spinner" /></div>
                  ) : albums.length === 0 ? (
                      <p className="profile-feed__empty">No albums yet.</p>
                  ) : (
                      <div className="profile-albums-grid">
                        {albums.map(album => (
                            <Card key={album.albumId} className="profile-album-card">
                              {editingAlbumId === album.albumId ? (
                                  <div className="profile-album-edit">
                                    <input
                                        className="profile-albums-input"
                                        value={editAlbumName}
                                        onChange={e => setEditAlbumName(e.target.value)}
                                    />
                                    <div className="profile-albums-create-actions">
                                      <button className="profile-edit-btn" onClick={() => handleUpdateAlbum(album.albumId)}>Save</button>
                                      <button className="profile-edit-cancel" onClick={() => setEditingAlbumId(null)}>Cancel</button>
                                    </div>
                                  </div>
                              ) : (
                                  <>
                                    <p className="profile-album-name" onClick={() => navigate(`/albums/${album.albumId}`)}>
                                      {album.name}
                                    </p>
                                    <p className="profile-album-date">{new Date(album.createdAt).toLocaleDateString()}</p>
                                    {loggedInUser?.id === user.id && (
                                        <div className="profile-album-actions">
                                          <button className="profile-album-btn" onClick={() => { setEditingAlbumId(album.albumId); setEditAlbumName(album.name) }}>Rename</button>
                                          <button className="profile-album-btn" disabled={deletingAlbumId === album.albumId} onClick={() => handleDeleteAlbum(album.albumId)}>
                                            {deletingAlbumId === album.albumId ? 'Deleting…' : 'Delete'}
                                          </button>
                                        </div>
                                    )}
                                  </>
                              )}
                            </Card>
                        ))}
                      </div>
                  )}
                </section>
            )}

            {activeTab === 'friends' && isOwnProfile && (
                <section className="profile-friends">
                  {profileFriends.length === 0 ? (
                      <p className="profile-feed__empty">No friends yet.</p>
                  ) : (
                      <div className="profile-friends-grid">
                        {profileFriends.map(friend => {
                          const friendUsername = friend.other_user_username || (friend as any).otherUserUsername;

                          return (
                              <div
                                  key={friend.request_id || Math.random()}
                                  onClick={() => navigate(`/profile/${friendUsername}`)}
                                  style={{ cursor: 'pointer', display: 'flex' }}
                              >
                                <Card className="profile-friend-card profile-friend-card--full-width">
                                  <div className={`profile-friend-avatar${friendPhotos[friendUsername] ? ' profile-friend-avatar--photo' : ` avatar-gradient--${getAvatarVariant(friendUsername)}`}`}>
                                    {friendPhotos[friendUsername]
                                      ? <img src={friendPhotos[friendUsername]!} alt={friendUsername} className="profile-friend-avatar-img" />
                                      : (friendUsername ? friendUsername[0].toUpperCase() : '?')}
                                  </div>
                                  <span className="profile-friend-name">{friendUsername}</span>
                                </Card>
                              </div>
                          );
                        })}
                      </div>
                  )}
                </section>
            )}
            </>)}

          </div>
        </div>
      </Layout>
  )
}