import api from './axios'
import type { Post, CreatePostRequest } from '../models/post'

const POSTS_BASE = '/api/posts'
const USERS_BASE = '/api/users'

export async function createPost(data: CreatePostRequest): Promise<Post> {
  const { data: post } = await api.post<Post>(POSTS_BASE, data)
  return post
}

export async function getUserPosts(userId: number): Promise<Post[]> {
  const { data: posts } = await api.get<Post[]>(`${USERS_BASE}/${userId}/posts`)
  return posts
}
export async function getPostById(postId: number): Promise<Post> {
  const { data } = await api.get<Post>(`/api/posts/${postId}`);
  return data;
}

export async function updatePost(postId: number, data: { content: string; photoId?: number | null; caption?: string | null }): Promise<void> {
  await api.patch(`${POSTS_BASE}/${postId}`, data);
}

export async function deletePost(postId: number): Promise<void> {
  await api.delete(`${POSTS_BASE}/${postId}`);
}