import api from './axios'
import type { User } from '../models/user'
import type { Post } from '../models/post'

const USERS_BASE = '/api/users'
const POSTS_BASE = '/api/posts'

export async function fetchAllUsers(): Promise<User[]> {
  const { data } = await api.get<User[]>(`${USERS_BASE}/`)
  return data
}

export async function banUser(userId: string | number, reason: string): Promise<void> {
  await api.patch(`${USERS_BASE}/${userId}/ban`, { reason })
}

export async function unbanUser(userId: string): Promise<void> {
  await api.patch(`${USERS_BASE}/${userId}/unban`)
}

export async function deleteUser(userId: string): Promise<void> {
  await api.delete(`${USERS_BASE}/${userId}`)
}

export async function fetchAllPosts(): Promise<Post[]> {
  const { data } = await api.get<Post[]>(`${POSTS_BASE}`)
  return data
}

export async function deletePost(postId: number): Promise<void> {
  await api.delete(`${POSTS_BASE}/${postId}`)
}
