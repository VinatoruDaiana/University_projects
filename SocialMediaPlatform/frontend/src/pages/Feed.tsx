import { useState, useEffect, useRef, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import Layout from '../components/Layout'
import Card from '../components/Card'
import { getFeed } from '../api/feed'
import type { FeedPost } from '../models/feed'
import './Feed.css'

function getAvatarVariant(username: string): number {
  return (username.charCodeAt(0) % 3) + 1
}

function formatDate(iso: string): string {
  return new Date(iso).toLocaleString('en-US', {
    year: 'numeric', month: 'short', day: 'numeric',
    hour: '2-digit', minute: '2-digit',
  })
}

export default function Feed() {
  const navigate = useNavigate()
  const [posts, setPosts] = useState<FeedPost[]>([])
  const [page, setPage] = useState(0)
  const [loading, setLoading] = useState(false)
  const [initialLoading, setInitialLoading] = useState(true)
  const [hasMore, setHasMore] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const observerRef = useRef<IntersectionObserver | null>(null)
  const bottomRef = useRef<HTMLDivElement | null>(null)

  const loadPosts = useCallback(async (pageNum: number) => {
    if (loading || !hasMore) return
    setLoading(true)
    setError(null)
    try {
      const data = await getFeed(pageNum)
      if (data.length === 0) {
        setHasMore(false)
      } else {
        setPosts(prev => {
          const existingIds = new Set(prev.map(p => p.id))
          const newPosts = data.filter(p => !existingIds.has(p.id))
          return [...prev, ...newPosts]
        })
        if (data.length < 10) setHasMore(false)
      }
    } catch {
      setError('Failed to load feed. Please try again.')
    } finally {
      setLoading(false)
      setInitialLoading(false)
    }
  }, [loading, hasMore])

  useEffect(() => {
    loadPosts(0)
  }, [])

  useEffect(() => {
    if (!bottomRef.current) return
    observerRef.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && hasMore && !loading) {
        setPage(prev => {
          const next = prev + 1
          loadPosts(next)
          return next
        })
      }
    }, { threshold: 1.0 })
    observerRef.current.observe(bottomRef.current)
    return () => observerRef.current?.disconnect()
  }, [hasMore, loading])

  return (
    <Layout>
      <div className="feed-page">
        <h1 className="feed-title">Your Feed</h1>

        {initialLoading && (
          <div className="feed-loading">
            <div className="feed-spinner" />
          </div>
        )}

        {error && (
          <div className="feed-error">
            <p>{error}</p>
            <button onClick={() => loadPosts(page)}>Retry</button>
          </div>
        )}

        {!initialLoading && posts.length === 0 && !error && (
          <p className="feed-empty">No posts yet. Add some friends to see their posts!</p>
        )}

        <div className="feed-list">
          {posts.map(post => (
            <Card key={post.id} className="feed-card" onClick={() => navigate(`/post/${post.id}`)}>
              <div
                className="feed-card__header"
                onClick={(e) => { e.stopPropagation(); navigate(`/profile/${post.username}`) }}
              >
                <div className={`feed-card__avatar${post.profilePhotoUrl ? ' feed-card__avatar--photo' : ` avatar-gradient--${getAvatarVariant(post.username)}`}`}>
                  {post.profilePhotoUrl
                    ? <img src={post.profilePhotoUrl} alt={post.username} className="feed-card__avatar-img" />
                    : post.username[0].toUpperCase()
                  }
                </div>
                <div>
                  <span className="feed-card__username">{post.username}</span>
                  <span className="feed-card__date">{formatDate(post.createdAt)}</span>
                </div>
              </div>
              {post.content && <p className="feed-card__content">{post.content}</p>}
              {post.photoUrl && (
                <img src={post.photoUrl} alt="post" className="feed-card__image" />
              )}
            </Card>
          ))}
        </div>

        {loading && !initialLoading && (
          <div className="feed-loading-more">
            <div className="feed-spinner" />
          </div>
        )}

        <div ref={bottomRef} />
      </div>
    </Layout>
  )
}
