import api from './axios'
import type { Album, AlbumRequest } from '../models/album'
import type { Post } from '../models/post'

export async function getUserAlbums(userId: number): Promise<Album[]> {
  const { data } = await api.get<Album[]>(`/api/users/${userId}/albums`)
  return data
}

export async function createAlbum(request: AlbumRequest): Promise<Album> {
  const { data } = await api.post<Album>('/api/albums', request)
  return data
}

export async function updateAlbum(albumId: number, request: AlbumRequest): Promise<Album> {
  const { data } = await api.patch<Album>(`/api/albums/${albumId}`, request)
  return data
}

export async function deleteAlbum(albumId: number): Promise<void> {
  await api.delete(`/api/albums/${albumId}`)
}

export async function getAlbumPosts(albumId: number): Promise<Post[]> {
  const { data } = await api.get<Post[]>(`/api/albums/${albumId}/posts`)
  return data
}

export async function addPostToAlbum(albumId: number, postId: number): Promise<Post> {
  const { data } = await api.post<Post>(`/api/albums/${albumId}/posts/${postId}`)
  return data
}
