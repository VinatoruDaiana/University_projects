import api from './axios';
import type { Comment, CommentRequest } from '../models/comment';

export async function getComments(postId: number): Promise<Comment[]> {
  const { data } = await api.get<Comment[]>(`/api/posts/${postId}/comments`);
  return data;
}

export async function createComment(postId: number, request: CommentRequest): Promise<Comment> {
  const { data } = await api.post<Comment>(`/api/posts/${postId}/comments`, request);
  return data;
}

export async function updateComment(commentId: number, request: CommentRequest): Promise<Comment> {
  const { data } = await api.patch<Comment>(`/api/comments/${commentId}`, request);
  return data;
}

export async function deleteComment(commentId: number): Promise<void> {
  await api.delete(`/api/comments/${commentId}`);
}

export async function likeComment(commentId: number): Promise<void> {
  await api.post(`/api/comments/${commentId}/like`);
}

export async function unlikeComment(commentId: number): Promise<void> {
  await api.delete(`/api/comments/${commentId}/like`);
}