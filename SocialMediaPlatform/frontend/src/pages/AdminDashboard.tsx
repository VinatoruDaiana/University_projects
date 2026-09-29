import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import Layout from '../components/Layout'
import Button from '../components/Button'
import Card from '../components/Card'
import {
  fetchAllUsers,
  fetchAllPosts,
  banUser,
  unbanUser,
  deleteUser,
  deletePost,
} from '../api/admin'
import type { User } from '../models/user'
import type { Post } from '../models/post'
import './AdminDashboard.css'

type Tab = 'users' | 'posts'

type ConfirmAction =
  | { type: 'banUser'; userId: string | number; username: string }
  | { type: 'deleteUser'; userId: string | number; username: string }
  | { type: 'deletePost'; postId: number; username: string }

export default function AdminDashboard() {
  const navigate = useNavigate()
  const loggedInUser = JSON.parse(localStorage.getItem('pulse_user') ?? 'null')
  const loggedInUserId: string = loggedInUser?.id ?? ''

  const [activeTab, setActiveTab] = useState<Tab>('users')

  const [users, setUsers] = useState<User[]>([])
  const [usersLoading, setUsersLoading] = useState(false)
  const [usersError, setUsersError] = useState<string | null>(null)
  const [usersSuccess, setUsersSuccess] = useState<string | null>(null)
  const [userSearch, setUserSearch] = useState('')

  const [posts, setPosts] = useState<Post[]>([])
  const [postsLoading, setPostsLoading] = useState(false)
  const [postsError, setPostsError] = useState<string | null>(null)
  const [postsSuccess, setPostsSuccess] = useState<string | null>(null)

  const [actionLoading, setActionLoading] = useState<string | null>(null)
  const [confirmAction, setConfirmAction] = useState<ConfirmAction | null>(null)
  const [banReason, setBanReason] = useState('')

  const showUsersSuccess = useCallback((msg: string) => {
    setUsersSuccess(msg)
    setTimeout(() => setUsersSuccess(null), 3500)
  }, [])

  const showPostsSuccess = useCallback((msg: string) => {
    setPostsSuccess(msg)
    setTimeout(() => setPostsSuccess(null), 3500)
  }, [])

  useEffect(() => {
    if (activeTab === 'users') {
      loadUsers()
    } else {
      loadPosts()
    }
  }, [activeTab])

  async function loadUsers() {
    setUsersLoading(true)
    setUsersError(null)
    try {
      const data = await fetchAllUsers()
      setUsers(data)
    } catch {
      setUsersError('Failed to load users. Please try again.')
    } finally {
      setUsersLoading(false)
    }
  }

  async function loadPosts() {
    setPostsLoading(true)
    setPostsError(null)
    try {
      const data = await fetchAllPosts()
      setPosts(data)
    } catch {
      setPostsError('Failed to load posts. Please try again.')
    } finally {
      setPostsLoading(false)
    }
  }

  function handleBanToggle(user: User) {
    if (user.status === 'BLOCKED') {
      executeUnban(user.id, user.username)
    } else {
      setConfirmAction({ type: 'banUser', userId: user.id, username: user.username })
    }
  }

  async function executeUnban(userId: string, username: string) {
    const key = `unban-${userId}`
    setActionLoading(key)
    setUsersError(null)
    try {
      await unbanUser(userId)
      setUsers(prev =>
        prev.map(u => (u.id === userId ? { ...u, isBanned: false, status: 'ACTIVE' } : u)),
      )
      showUsersSuccess(`@${username} has been unbanned.`)
    } catch {
      setUsersError('Failed to unban user.')
    } finally {
      setActionLoading(null)
    }
  }

  function cancelConfirm() {
    setConfirmAction(null)
    setBanReason('')
  }

  async function executeConfirmedAction() {
    if (!confirmAction) return
    const action = confirmAction
    setConfirmAction(null)
    setBanReason('')

    if (action.type === 'banUser') {
      const key = `ban-${action.userId}`
      setActionLoading(key)
      setUsersError(null)
      try {
        await banUser(action.userId, banReason)
        setUsers(prev =>
          prev.map(u =>
            u.id === action.userId ? { ...u, isBanned: true, status: 'BLOCKED' } : u,
          ),
        )
        showUsersSuccess(`@${action.username} has been banned.`)
      } catch {
        setUsersError('Failed to ban user.')
      } finally {
        setActionLoading(null)
      }
    } else if (action.type === 'deleteUser') {
      const key = `deleteUser-${action.userId}`
      setActionLoading(key)
      setUsersError(null)
      try {
        await deleteUser(action.userId)
        setUsers(prev => prev.filter(u => u.id !== action.userId))
        showUsersSuccess(`@${action.username} has been deleted.`)
      } catch {
        setUsersError('Failed to delete user.')
      } finally {
        setActionLoading(null)
      }
    } else if (action.type === 'deletePost') {
      const key = `deletePost-${action.postId}`
      setActionLoading(key)
      setPostsError(null)
      try {
        await deletePost(action.postId)
        setPosts(prev => prev.filter(p => p.id !== action.postId))
        showPostsSuccess('Post deleted successfully.')
      } catch {
        setPostsError('Failed to delete post.')
      } finally {
        setActionLoading(null)
      }
    }
  }

  const filteredUsers = users.filter(
    u =>
      userSearch === '' ||
      u.username.toLowerCase().includes(userSearch.toLowerCase()) ||
      u.email.toLowerCase().includes(userSearch.toLowerCase()),
  )

  function confirmLabel(action: ConfirmAction): string {
    if (action.type === 'banUser') return `ban @${action.username}`
    if (action.type === 'deleteUser')
      return `permanently delete @${action.username} and all their content`
    return `delete this post by @${action.username}`
  }

  return (
    <Layout>
      <div className="admin-page">
        <div className="admin-header">
          <div>
            <h1 className="admin-title">Admin Dashboard</h1>
            <p className="admin-subtitle">Manage users and platform content</p>
          </div>
          <Button variant="outline" size="sm" onClick={() => navigate('/admin/moderation')}>
            Content Moderation
          </Button>
        </div>

        <div className="admin-tabs">
          <button
            className={`admin-tab ${activeTab === 'users' ? 'admin-tab--active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            Users
          </button>
          <button
            className={`admin-tab ${activeTab === 'posts' ? 'admin-tab--active' : ''}`}
            onClick={() => setActiveTab('posts')}
          >
            Content
          </button>
        </div>

        {activeTab === 'users' && (
          <Card className="admin-section">
            <div className="admin-section-header">
              <h2>Users Management</h2>
              <input
                className="admin-search"
                type="text"
                placeholder="Search by username or email..."
                value={userSearch}
                onChange={e => setUserSearch(e.target.value)}
              />
            </div>

            {usersLoading && <div className="admin-loading">Loading users…</div>}
            {usersError && <div className="admin-error">{usersError}</div>}
            {usersSuccess && <div className="admin-success">{usersSuccess}</div>}

            {!usersLoading && (
              <div className="admin-table-wrapper">
                <table className="admin-table">
                  <thead>
                    <tr>
                      <th>User</th>
                      <th>Email</th>
                      <th>Role</th>
                      <th>Status</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredUsers.map(user => {
                      const isSelf = user.id === loggedInUserId
                      const isBlocked = user.status === 'BLOCKED'
                      return (
                        <tr key={user.id}>
                          <td>
                            <div className="admin-user-cell">
                              <div className="admin-avatar">
                                {user.photoUrl ? (
                                  <img src={user.photoUrl} alt={user.username} />
                                ) : (
                                  <span>{user.username[0]?.toUpperCase()}</span>
                                )}
                              </div>
                              <div>
                                <div className="admin-username">
                                  {user.username}
                                  {isSelf && (
                                    <span className="admin-self-tag"> (you)</span>
                                  )}
                                </div>
                                {(user.firstName || user.lastName) && (
                                  <div className="admin-fullname">
                                    {[user.firstName, user.lastName].filter(Boolean).join(' ')}
                                  </div>
                                )}
                              </div>
                            </div>
                          </td>
                          <td className="admin-email">{user.email}</td>
                          <td>
                            <span
                              className={`admin-badge admin-badge--${user.role.toLowerCase()}`}
                            >
                              {user.role}
                            </span>
                          </td>
                          <td>
                            <span
                              className={`admin-badge ${isBlocked ? 'admin-badge--banned' : 'admin-badge--active'}`}
                            >
                              {user.status ?? (isBlocked ? 'BLOCKED' : 'ACTIVE')}
                            </span>
                          </td>
                          <td>
                            <div className="admin-actions">
                              <Button
                                variant="outline"
                                size="sm"
                                onClick={() => navigate(`/profile/${user.username}`)}
                              >
                                View
                              </Button>
                              <Button
                                variant="outline"
                                size="sm"
                                disabled={
                                  isSelf ||
                                  actionLoading === `ban-${user.id}` ||
                                  actionLoading === `unban-${user.id}`
                                }
                                title={isSelf ? 'Cannot ban yourself' : undefined}
                                onClick={() => handleBanToggle(user)}
                              >
                                {isBlocked ? 'Unban' : 'Ban'}
                              </Button>
                              <Button
                                variant="outline"
                                size="sm"
                                className="admin-btn--danger"
                                disabled={
                                  isSelf || actionLoading === `deleteUser-${user.id}`
                                }
                                title={isSelf ? 'Cannot delete yourself' : undefined}
                                onClick={() =>
                                  setConfirmAction({
                                    type: 'deleteUser',
                                    userId: user.id,
                                    username: user.username,
                                  })
                                }
                              >
                                Delete
                              </Button>
                            </div>
                          </td>
                        </tr>
                      )
                    })}
                    {filteredUsers.length === 0 && !usersLoading && (
                      <tr>
                        <td colSpan={5} className="admin-empty">
                          No users found
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </Card>
        )}

        {activeTab === 'posts' && (
          <Card className="admin-section">
            <div className="admin-section-header">
              <h2>Content Management</h2>
              <span style={{ color: 'var(--text)', fontSize: '0.85rem' }}>
                {posts.length} post{posts.length !== 1 ? 's' : ''}
              </span>
            </div>

            {postsLoading && <div className="admin-loading">Loading posts…</div>}
            {postsError && <div className="admin-error">{postsError}</div>}
            {postsSuccess && <div className="admin-success">{postsSuccess}</div>}

            {!postsLoading && (
              <div className="admin-posts-list">
                {posts.map(post => (
                  <div key={post.id} className="admin-post-item">
                    <div className="admin-post-info">
                      <div className="admin-post-author">
                        <button
                          className="admin-link"
                          onClick={() => navigate(`/profile/${post.username}`)}
                        >
                          @{post.username}
                        </button>
                        <span className="admin-post-date">
                          {new Date(post.createdAt).toLocaleString()}
                        </span>
                      </div>
                      {post.content && (
                        <p className="admin-post-content">
                          {post.content.length > 200
                            ? post.content.slice(0, 200) + '…'
                            : post.content}
                        </p>
                      )}
                      {post.photoUrl && (
                        <img className="admin-post-thumb" src={post.photoUrl} alt="Post" />
                      )}
                    </div>
                    <div className="admin-post-actions">
                      <Button
                        variant="outline"
                        size="sm"
                        className="admin-btn--danger"
                        disabled={actionLoading === `deletePost-${post.id}`}
                        onClick={() =>
                          setConfirmAction({
                            type: 'deletePost',
                            postId: post.id,
                            username: post.username,
                          })
                        }
                      >
                        Delete
                      </Button>
                    </div>
                  </div>
                ))}
                {posts.length === 0 && (
                  <div className="admin-empty">No posts found</div>
                )}
              </div>
            )}
          </Card>
        )}

        {confirmAction && (
          <div className="admin-modal-overlay" onClick={cancelConfirm}>
            <div className="admin-modal" onClick={e => e.stopPropagation()}>
              <h3>Confirm Action</h3>
              <p>
                Are you sure you want to {confirmLabel(confirmAction)}? This action cannot be
                undone.
              </p>
              {confirmAction.type === 'banUser' && (
                <div className="admin-modal-reason">
                  <label className="admin-modal-reason-label" htmlFor="ban-reason">
                    Reason for ban <span className="admin-modal-reason-required">*</span>
                  </label>
                  <textarea
                    id="ban-reason"
                    className="admin-modal-reason-input"
                    placeholder="Explain why this user is being banned…"
                    value={banReason}
                    onChange={e => setBanReason(e.target.value)}
                    rows={3}
                  />
                </div>
              )}
              <div className="admin-modal-actions">
                <Button variant="outline" onClick={cancelConfirm}>
                  Cancel
                </Button>
                <Button
                  variant="primary"
                  className="admin-btn--danger"
                  disabled={confirmAction.type === 'banUser' && banReason.trim() === ''}
                  onClick={executeConfirmedAction}
                >
                  Confirm
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </Layout>
  )
}
