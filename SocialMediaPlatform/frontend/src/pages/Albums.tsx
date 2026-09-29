import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { isAxiosError } from 'axios'
import { getUserAlbums, createAlbum, updateAlbum, deleteAlbum } from '../api/albums'
import type { Album } from '../models/album'
import Button from '../components/Button'
import Input from '../components/Input'
import Card from '../components/Card'
import './Albums.css'

export default function Albums() {
  const navigate = useNavigate()
  const user = JSON.parse(localStorage.getItem('pulse_user') || '{}')
  const userId = Number(user.id)

  const [albums, setAlbums] = useState<Album[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [showCreate, setShowCreate] = useState(false)
  const [newName, setNewName] = useState('')
  const [creating, setCreating] = useState(false)
  const [createError, setCreateError] = useState('')

  const [editingId, setEditingId] = useState<number | null>(null)
  const [editName, setEditName] = useState('')
  const [saving, setSaving] = useState(false)

  const [deletingId, setDeletingId] = useState<number | null>(null)

  useEffect(() => {
    if (!userId) {
      navigate('/login')
      return
    }
    fetchAlbums()
  }, [])

  async function fetchAlbums() {
    try {
      setLoading(true)
      setError('')
      const data = await getUserAlbums(userId)
      setAlbums(data)
    } catch {
      setError('Failed to load albums. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  async function handleCreate() {
    if (!newName.trim()) {
      setCreateError('Album name is required')
      return
    }
    setCreating(true)
    setCreateError('')
    try {
      const album = await createAlbum({ name: newName.trim() })
      setAlbums(prev => [...prev, album])
      setNewName('')
      setShowCreate(false)
    } catch (err) {
      setCreateError(isAxiosError(err) ? err.response?.data?.name ?? 'Failed to create album' : 'Failed to create album')
    } finally {
      setCreating(false)
    }
  }

  async function handleUpdate(albumId: number) {
    if (!editName.trim()) return
    setSaving(true)
    try {
      const updated = await updateAlbum(albumId, { name: editName.trim() })
      setAlbums(prev => prev.map(a => a.albumId === albumId ? updated : a))
      setEditingId(null)
    } catch {
      setError('Failed to rename album.')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(albumId: number) {
    if (!window.confirm('Are you sure you want to delete this album?')) return
    setDeletingId(albumId)
    try {
      await deleteAlbum(albumId)
      setAlbums(prev => prev.filter(a => a.albumId !== albumId))
    } catch {
      setError('Failed to delete album.')
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div className="albums-page">
      <div className="albums-header">
        <h1 className="albums-title">My Albums</h1>
        <Button onClick={() => { setShowCreate(true); setCreateError('') }}>
          + Create Album
        </Button>
      </div>

      {showCreate && (
        <Card className="albums-create-card">
          <Input
            label="Album name"
            value={newName}
            onChange={e => { setNewName(e.target.value); setCreateError('') }}
            placeholder="e.g. Summer 2024"
            error={createError}
          />
          <div className="albums-create-actions">
            <Button onClick={handleCreate} disabled={creating}>
              {creating ? 'Creating…' : 'Create'}
            </Button>
            <Button variant="secondary" onClick={() => { setShowCreate(false); setNewName('') }}>
              Cancel
            </Button>
          </div>
        </Card>
      )}

      {error && <p className="albums-error">{error}</p>}

      {loading ? (
        <p className="albums-loading">Loading albums…</p>
      ) : albums.length === 0 ? (
        <p className="albums-empty">No albums yet. Create your first album!</p>
      ) : (
        <div className="albums-grid">
          {albums.map(album => (
            <Card key={album.albumId} className="album-card">
              {editingId === album.albumId ? (
                <div className="album-edit">
                  <Input
                    value={editName}
                    onChange={e => setEditName(e.target.value)}
                    placeholder="Album name"
                  />
                  <div className="album-card-actions">
                    <Button size="sm" onClick={() => handleUpdate(album.albumId)} disabled={saving}>
                      {saving ? 'Saving…' : 'Save'}
                    </Button>
                    <Button size="sm" variant="secondary" onClick={() => setEditingId(null)}>
                      Cancel
                    </Button>
                  </div>
                </div>
              ) : (
                <>
                  <p
                    className="album-card-name"
                    onClick={() => navigate(`/albums/${album.albumId}`)}
                  >
                    {album.name}
                  </p>
                  <p className="album-card-date">
                    {new Date(album.createdAt).toLocaleDateString()}
                  </p>
                  <div className="album-card-actions">
                    <Button
                      size="sm"
                      variant="secondary"
                      onClick={() => { setEditingId(album.albumId); setEditName(album.name) }}
                    >
                      Rename
                    </Button>
                    <Button
                      size="sm"
                      variant="secondary"
                      disabled={deletingId === album.albumId}
                      onClick={() => handleDelete(album.albumId)}
                    >
                      {deletingId === album.albumId ? 'Deleting…' : 'Delete'}
                    </Button>
                  </div>
                </>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}
