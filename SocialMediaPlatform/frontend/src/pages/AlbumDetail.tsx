import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getAlbumPosts, addPostToAlbum } from '../api/albums'
import { getUserPosts } from '../api/posts'
import type { Post } from '../models/post'
import Button from '../components/Button'
import Card from '../components/Card'
import './AlbumDetail.css'

export default function AlbumDetail() {
  const { albumId } = useParams<{ albumId: string }>()
  const navigate = useNavigate()
  const user = JSON.parse(localStorage.getItem('pulse_user') || '{}')
  const userId = Number(user.id)

  const [posts, setPosts] = useState<Post[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [showModal, setShowModal] = useState(false)
  const [userPosts, setUserPosts] = useState<Post[]>([])
  const [selectedIds, setSelectedIds] = useState<Set<number>>(new Set())
  const [loadingPosts, setLoadingPosts] = useState(false)
  const [adding, setAdding] = useState(false)

  useEffect(() => {
    if (!albumId) return
    fetchAlbumPosts()
  }, [albumId])

  async function fetchAlbumPosts() {
    try {
      setLoading(true)
      setError('')
      const data = await getAlbumPosts(Number(albumId))
      setPosts(data)
    } catch {
      setError('Failed to load posts. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  async function openModal() {
    setShowModal(true)
    setSelectedIds(new Set())
    setLoadingPosts(true)
    try {
      const data = await getUserPosts(userId)
      setUserPosts(data)
    } catch {
      setError('Failed to load your posts.')
    } finally {
      setLoadingPosts(false)
    }
  }

  function toggleSelect(postId: number) {
    setSelectedIds(prev => {
      const next = new Set(prev)
      next.has(postId) ? next.delete(postId) : next.add(postId)
      return next
    })
  }

  async function handleDone() {
    if (selectedIds.size === 0) {
      setShowModal(false)
      return
    }
    setAdding(true)
    try {
      for (const postId of selectedIds) {
        await addPostToAlbum(Number(albumId), postId)
      }
      await fetchAlbumPosts()
      setShowModal(false)
    } catch {
      setError('Failed to add some posts to album.')
    } finally {
      setAdding(false)
    }
  }

  return (
    <div className="album-detail-page">
      <div className="album-detail-header">
        <Button variant="secondary" onClick={() => navigate(`/profile/${user.username}`)}>
          ← Back to Profile
        </Button>
        <h1 className="album-detail-title">Album Posts</h1>
        <Button onClick={openModal}>+ Select Posts</Button>
      </div>

      {error && <p className="album-detail-error">{error}</p>}

      {loading ? (
        <p className="album-detail-loading">Loading posts…</p>
      ) : posts.length === 0 ? (
        <p className="album-detail-empty">No posts in this album yet.</p>
      ) : (
        <div className="posts-grid">
          {posts.map(post => (
            <Card key={post.id} className="post-card">
              {post.photoUrl && (
                <img src={post.photoUrl} alt={post.content || 'Post photo'} className="post-image" />
              )}
              {post.content && <p className="post-caption">{post.content}</p>}
              <p className="post-date">{new Date(post.createdAt).toLocaleDateString()}</p>
            </Card>
          ))}
        </div>
      )}

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2 className="modal-title">Select Posts</h2>

            {loadingPosts ? (
              <p className="modal-loading">Loading your posts…</p>
            ) : userPosts.length === 0 ? (
              <p className="modal-empty">You have no posts yet.</p>
            ) : (
              <div className="modal-grid">
                {userPosts.map(post => (
                  <div
                    key={post.id}
                    className={`modal-photo ${selectedIds.has(post.id) ? 'selected' : ''}`}
                    onClick={() => toggleSelect(post.id)}
                  >
                    {post.photoUrl
                      ? <img src={post.photoUrl} alt={post.content || 'post'} />
                      : <div className="modal-no-photo">{post.content || 'No photo'}</div>
                    }
                    {selectedIds.has(post.id) && <div className="modal-check">✓</div>}
                    {post.content && <p className="modal-caption">{post.content}</p>}
                  </div>
                ))}
              </div>
            )}

            <div className="modal-actions">
              <Button onClick={handleDone} disabled={adding}>
                {adding ? 'Adding…' : `Done (${selectedIds.size} selected)`}
              </Button>
              <Button variant="secondary" onClick={() => setShowModal(false)}>
                Cancel
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
