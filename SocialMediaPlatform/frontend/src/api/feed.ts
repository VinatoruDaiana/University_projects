import api from './axios'
import type { FeedPost } from '../models/feed'

export async function getFeed(page: number, size: number = 10): Promise<FeedPost[]> {
  const { data } = await api.get<FeedPost[]>(`/api/feed?page=${page}&size=${size}`)
  return data
}
