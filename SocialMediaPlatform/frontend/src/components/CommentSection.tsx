import { useState, useEffect, useRef } from 'react';
import { 
  getComments, 
  createComment, 
  updateComment, 
  deleteComment, 
  likeComment, 
  unlikeComment 
} from '../api/comments';
import type { Comment } from '../models/comment';
import Button from './Button';
import './CommentSection.css';
import { useNavigate } from 'react-router-dom';
import { getUserByUsername } from '../api/users';
interface CommentSectionProps {
  postId: number;
  currentUserId: number | null; // Pass down from auth context/localStorage
  isPostOwner?: boolean;
}

function formatCommentDate(createdAt: string, updatedAt: string | null | undefined): string {
  if (!createdAt) return '';
  
  const created = new Date(createdAt);
  // It's edited if updatedAt exists and is chronologically newer than createdAt
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

function parseMentions(content: string, navigate: (path: string) => void) {
  // Splits the string while capturing the @username tokens as separate array entries
  const parts = content.split(/(@[a-zA-Z0-9_]+)/g);
  
  return parts.map((part, index) => {
    if (part.startsWith('@')) {
      const username = part.substring(1); // Strips the '@' character
      return (
        <span
          key={index}
          onClick={(e) => {
            e.stopPropagation(); // Prevents clicking the mention from triggering parent handlers
            navigate(`/profile/${username}`);
          }}
          className="comment-mention"
          style={{ 
            color: 'var(--primary-color, #007bff)', 
            cursor: 'pointer', 
            fontWeight: '600',
            textDecoration: 'underline'
          }}
        >
          {part}
        </span>
      );
    }
    return part;
  });
}

export default function CommentSection({ postId, currentUserId, isPostOwner = false }: CommentSectionProps) {
  const [comments, setComments] = useState<Comment[]>([]);
  const [loading, setLoading] = useState(true);
  const [newComment, setNewComment] = useState('');
  const [submitting, setSubmitting] = useState(false);
  
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState('');

  const navigate = useNavigate();

  const [commentPhotos, setCommentPhotos] = useState<Record<string, string | null>>({})
  const fetchedCommentPhotosRef = useRef(new Set<string>())

  useEffect(() => {
    comments.forEach(c => {
      const username = c.username
      if (username && !fetchedCommentPhotosRef.current.has(username)) {
        fetchedCommentPhotosRef.current.add(username)
        getUserByUsername(username)
          .then(u => setCommentPhotos(prev => ({ ...prev, [username]: u.photoUrl ?? null })))
          .catch(() => setCommentPhotos(prev => ({ ...prev, [username]: null })))
      }
    })
  }, [comments])

  useEffect(() => {
    fetchComments();
  }, [postId]);

  async function fetchComments() {
    try {
      const data = await getComments(postId);
      setComments(data);
    } catch (err) {
      console.error("Failed to fetch comments", err);
    } finally {
      setLoading(false);
    }
  }

  async function handleAddComment(e: React.FormEvent) {
    e.preventDefault();
    if (!newComment.trim() || submitting) return;
    
    setSubmitting(true);
    try {
      const added = await createComment(postId, { content: newComment });
      setComments([...comments, added]); // Appends to the end (ASC order)
      setNewComment('');
    } catch (err) {
      console.error("Failed to add comment", err);
    } finally {
      setSubmitting(false);
    }
  }

  async function handleDelete(commentId: number) {
    if (!window.confirm("Are you sure you want to delete this comment?")) return;
    try {
      await deleteComment(commentId);
      setComments(prev => prev.filter(c => c.commentId !== commentId));
    } catch (err) {
      console.error("Failed to delete comment", err);
    }
  }

  async function handleEditSave(commentId: number) {
    if (!editContent.trim()) return;
    try {
      const updated = await updateComment(commentId, { content: editContent });
      setComments(prev => prev.map(c => c.commentId === commentId ? updated : c));
      setEditingId(null);
    } catch (err) {
      console.error("Failed to update comment", err);
    }
  }

  async function handleLikeToggle(comment: Comment) {
    // Optimistic UI Update
    const isLiked = comment.likedByCurrentUser;
    setComments(prev => prev.map(c => c.commentId === comment.commentId ? {
      ...c,
      likedByCurrentUser: !isLiked,
      likeCount: isLiked ? c.likeCount - 1 : c.likeCount + 1
    } : c));

    try {
      if (isLiked) {
        await unlikeComment(comment.commentId);
      } else {
        await likeComment(comment.commentId);
      }
    } catch (err) {
      // Revert on failure
      setComments(prev => prev.map(c => c.commentId === comment.commentId ? {
        ...c,
        likedByCurrentUser: isLiked,
        likeCount: isLiked ? c.likeCount + 1 : c.likeCount - 1
      } : c));
    }
  }

  if (loading) return <div className="comments-loading">Loading comments...</div>;

  return (
    <div className="comment-section">
      <div className="comments-list">
        {comments.length === 0 ? (
          <p className="comments-empty">No comments yet.</p>
        ) : (
          comments.map(comment => (
            <div key={comment.commentId} className="comment-item">

              
              <div 
                className="comment-user-profile-trigger" 
                onClick={() => navigate(`/profile/${comment.username}`)}
                style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px' }}
              >
                <div className="profile-avatar-wrap" style={{ width: '32px', height: '32px', flexShrink: 0 }}>
                  {commentPhotos[comment.username] ? (
                    <img
                      className="profile-avatar profile-avatar--photo"
                      src={commentPhotos[comment.username]!}
                      alt={comment.username}
                      style={{ width: '100%', height: '100%', borderRadius: '50%', objectFit: 'cover' }}
                    />
                  ) : (
                    <div 
                      className={`profile-avatar avatar-gradient--${((comment.username?.charCodeAt(0) ?? 0) % 3) + 1}`}
                      style={{ width: '100%', height: '100%', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold', fontSize: '14px' }}
                    >
                      {(comment.username ? comment.username[0] : '?').toUpperCase()}
                    </div>
                  )}
                </div>

                <div 
                  className="comment-header" 
                  style={{ 
                    display: 'flex', 
                    flexDirection: 'column', 
                    justifyContent: 'center', 
                    alignItems: 'flex-start', // Forces text elements to align to the left edge
                    textAlign: 'left',        // Prevents text wandering inside the block
                    lineHeight: '1.1' 
                  }}
                >
                  <span className="comment-author" style={{ fontWeight: '600', margin: 0, padding: 0 }}>
                    @{comment.username}
                  </span>
                  <span className="comment-date" style={{ fontSize: '11px', opacity: 0.6, margin: '2px 0 0 0', padding: 0 }}>
                    {formatCommentDate(comment.createdAt, comment.updatedAt)}
                  </span>
                </div>
              </div>
              {editingId === comment.commentId ? (
                <div className="comment-edit-form">
                  <input 
                    type="text" 
                    value={editContent} 
                    onChange={e => setEditContent(e.target.value)} 
                  />
                  <div className="comment-actions">
                    <Button size="sm" onClick={() => handleEditSave(comment.commentId)}>Save</Button>
                    <Button size="sm" variant="secondary" onClick={() => setEditingId(null)}>Cancel</Button>
                  </div>
                </div>
              ) : (
                <p className="comment-text">
                  {parseMentions(comment.content, navigate)}
                </p>
              )}

              <div className="comment-footer-actions">
                <button 
                  className={`comment-like-btn ${comment.likedByCurrentUser ? 'liked' : ''}`}
                  onClick={() => handleLikeToggle(comment)}
                >
                  {comment.likedByCurrentUser ? '♥' : '♡'} {comment.likeCount}
                </button>
                
                {currentUserId === comment.userId && editingId !== comment.commentId && (
                  <button className="comment-action-btn" onClick={() => {
                    setEditingId(comment.commentId);
                    setEditContent(comment.content);
                  }}>Edit</button>
                )}
                {/* Delete Button: For the comment owner OR the post owner */}
                {(currentUserId === comment.userId || isPostOwner) && editingId !== comment.commentId && (
                  <button className="comment-action-btn danger" onClick={() => handleDelete(comment.commentId)}>
                    Delete
                  </button>
                )}
              </div>
            </div>
          ))
        )}
      </div>

      <form className="comment-input-form" onSubmit={handleAddComment}>
        <input 
          type="text" 
          placeholder="Write a comment..." 
          value={newComment}
          onChange={e => setNewComment(e.target.value)}
          disabled={submitting}
        />
        <Button type="submit" size="sm" disabled={!newComment.trim() || submitting}>
          {submitting ? '...' : 'Post'}
        </Button>
      </form>
    </div>
  );
}