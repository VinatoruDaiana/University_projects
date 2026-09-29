import { useState, useEffect } from 'react'
import './PostCard.css'
import type { Post } from '../models/post'
import type { Photo } from '../models/photo'
import { getPhoto } from '../api/photos'
import { likePost, unlikePost } from '../api/postLikes'
import { HeartIcon } from '../assets/icons'
import { getComments } from '../api/comments'
import { useNavigate, useLocation } from 'react-router-dom';

interface PostCardProps {
  post: Post
}

function formatPostDate(createdAt: string, updatedAt: string | null | undefined): string {
  if (!createdAt) return '';
  
  const created = new Date(createdAt);
  const isEdited = updatedAt && new Date(updatedAt).getTime() > created.getTime();
  const targetDate = isEdited ? new Date(updatedAt!) : created;

  const day = String(targetDate.getDate()).padStart(2, '0');
  const month = String(targetDate.getMonth() + 1).padStart(2, '0');
  const year = targetDate.getFullYear();
  const hours = String(targetDate.getHours()).padStart(2, '0');
  const minutes = String(targetDate.getMinutes()).padStart(2, '0');

  const formattedStr = `${day}/${month}/${year} ${hours}:${minutes}`;
  return isEdited ? `Edited ${formattedStr}` : formattedStr;
}

export default function PostCard({ post }: PostCardProps) {
  const navigate = useNavigate();
  const location = useLocation();
  const [photo, setPhoto] = useState<Photo | null>(null)
  const [liked, setLiked] = useState(post.likedByCurrentUser);
  const [likeCount, setLikeCount] = useState(post.likeCount);
  const [likeLoading, setLikeLoading] = useState(false);
  const [commentCount, setCommentCount] = useState(0)
  useEffect(() => {
    if (post.photoId != null) {
      getPhoto(post.photoId).then(setPhoto).catch(() => setPhoto(null))
    }
  }, [post.photoId])

  useEffect(() => {
    getComments(post.id)
      .then(data => setCommentCount(data.length))
      .catch(() => setCommentCount(0))
  }, [post.id])

  useEffect(() => {
    setLiked(post.likedByCurrentUser);
    setLikeCount(post.likeCount);
  }, [post.likedByCurrentUser, post.likeCount]);

  async function handleLike() {
    if (likeLoading) return
    setLikeLoading(true)
    try {
      if (liked) {
        await unlikePost(post.id)
        setLiked(false)
        setLikeCount((c) => c - 1)
      } else {
        await likePost(post.id)
        setLiked(true)
        setLikeCount((c) => c + 1)
      }
    } catch {
      // keep current state if request fails
    } finally {
      setLikeLoading(false)
    }
  }

  return (
    <article className="post-card">
      <div 
        className="post-card__clickable-area" 
        style={{ cursor: 'pointer' }}
        onClick={() => navigate(`/post/${post.id}`, { state: { backgroundLocation: location } })}
      >
        {post.content && (
          <p className="post-card__content">{post.content}</p>
        )}
        {photo && (
          <div className="post-card__photo">
            <img src={photo.fileUrl} alt={photo.caption ?? 'Post photo'} className="post-card__photo-img" />
            {photo.caption && (
              <p className="post-card__photo-caption">{photo.caption}</p>
            )}
          </div>
        )}
      </div>
      <div className="post-card__footer">
        <time className="post-card__date" dateTime={post.createdAt}>
          {formatPostDate(post.createdAt, post.updatedAt)}
        </time>
        
        {/* Wrap both items in this new container */}
        <div className="post-card__actions">
          <div className="post-card__comments-indicator">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
              <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
            </svg>
            <span>{commentCount}</span>
          </div>
          
          <button
            className={`post-card__like-btn${liked ? ' post-card__like-btn--liked' : ''}`}
            onClick={handleLike}
            disabled={likeLoading}
            aria-label={liked ? 'Unlike' : 'Like'}
          >
            <HeartIcon fill={liked ? 'currentColor' : 'none'} />
            {likeCount > 0 && <span>{likeCount}</span>}
          </button>
        </div>
      </div>
    </article>
  )
}
