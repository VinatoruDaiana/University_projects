import api from './axios'

// changed "likes" to "like" for backend matching
const LIKES_BASE = (postId: number) => `/api/posts/${postId}/like`

export interface LikeResponse {
  postId: number;
  liked: boolean;
}

export async function likePost(postId: number): Promise<LikeResponse> {
  const { data: like } = await api.post<LikeResponse>(LIKES_BASE(postId))
  return like
}

export async function unlikePost(postId: number): Promise<void> {
  await api.delete(LIKES_BASE(postId))
}
