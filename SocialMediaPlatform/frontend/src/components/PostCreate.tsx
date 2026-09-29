import { useState, useRef } from 'react'
import { isAxiosError } from 'axios'
import Button from './Button'
import { createPost } from '../api/posts'
import { uploadPhoto } from '../api/photos'
import type { Post } from '../models/post'
import { CloseIcon, AttachPhotoIcon } from '../assets/icons'
import './PostCreate.css'

interface PostCreateProps {
  onCreated: (post: Post) => void
}

const MAX = 500

export default function PostCreate({ onCreated }: PostCreateProps) {
  const [content, setContent] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [mediaPreview, setMediaPreview] = useState<string | null>(null)
  const [mediaType, setMediaType] = useState<'image' | 'video' | null>(null)
  const [mediaCaption, setMediaCaption] = useState('')
  const fileInputRef = useRef<HTMLInputElement>(null)

  function handleFileChange(e: React.ChangeEvent<HTMLInputElement>) {
    const file = e.target.files?.[0]
    if (!file) return
    const isImage = file.type.startsWith('image/')
    const isVideo = file.type.startsWith('video/')
    if (!isImage && !isVideo) {
      setError('Only image and video files are supported.')
      if (fileInputRef.current) fileInputRef.current.value = ''
      return
    }
    const reader = new FileReader()
    reader.onload = () => {
      setMediaPreview(reader.result as string)
      setMediaType(isImage ? 'image' : 'video')
    }
    reader.readAsDataURL(file)
  }

  function handleRemoveMedia() {
    setMediaPreview(null)
    setMediaType(null)
    setMediaCaption('')
    if (fileInputRef.current) fileInputRef.current.value = ''
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    if (!content.trim() && !mediaPreview) return

    setLoading(true)
    setError(null)

    try {
      let photoId: number | null = null

      if (mediaPreview) {
        const photo = await uploadPhoto({
          fileUrl: mediaPreview,
          caption: mediaCaption.trim() || null,
        })
        photoId = photo.id
      }

      const post = await createPost({ content: content.trim(), photoId })
      setContent('')
      setMediaPreview(null)
      setMediaType(null)
      setMediaCaption('')
      if (fileInputRef.current) fileInputRef.current.value = ''
      onCreated(post)
    } catch (err) {
      const message = isAxiosError(err) ? err.response?.data?.message : undefined
      setError(message ?? 'Failed to create post. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const canSubmit = !loading && (!!content.trim() || !!mediaPreview)

  return (
    <form className="post-create" onSubmit={handleSubmit}>
      <textarea
        className={`post-create__textarea${error ? ' post-create__textarea--error' : ''}`}
        placeholder="What's on your mind?"
        value={content}
        onChange={(e) => {
          setContent(e.target.value)
          if (error) setError(null)
        }}
        maxLength={MAX}
        rows={3}
        disabled={loading}
      />

      {mediaPreview && (
        <div className="post-create__image-preview">
          {mediaType === 'video' ? (
            <video src={mediaPreview} controls className="post-create__preview-img" />
          ) : (
            <img src={mediaPreview} alt="Attachment preview" className="post-create__preview-img" />
          )}
          <input
            className="post-create__caption-input"
            type="text"
            placeholder="Add a caption (optional)"
            value={mediaCaption}
            onChange={(e) => setMediaCaption(e.target.value)}
            disabled={loading}
          />
          <button
            type="button"
            className="post-create__remove-img"
            onClick={handleRemoveMedia}
            aria-label="Remove media"
          >
            <CloseIcon />
          </button>
        </div>
      )}

      {error && <p className="post-create__error">{error}</p>}

      <div className="post-create__footer">
        <div className="post-create__footer-left">
          <button
            type="button"
            className="post-create__attach-btn"
            onClick={() => fileInputRef.current?.click()}
            disabled={loading}
            aria-label="Attach photo or video"
          >
            <AttachPhotoIcon />
            Media
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*,video/*"
            className="post-create__file-input"
            onChange={handleFileChange}
          />
          <span className={`post-create__count${content.length >= MAX * 0.9 ? ' post-create__count--warn' : ''}`}>
            {content.length} / {MAX}
          </span>
        </div>
        <Button type="submit" variant="primary" size="sm" disabled={!canSubmit}>
          {loading ? 'Posting…' : 'Post'}
        </Button>
      </div>
    </form>
  )
}
