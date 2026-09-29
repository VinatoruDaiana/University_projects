import api from './axios'
import type { Notification } from '../models/notification'

export async function getNotifications(): Promise<Notification[]> {
  const { data } = await api.get<Notification[]>('/api/notifications')
  return data
}

export async function getUnreadCount(): Promise<number> {
  const { data } = await api.get<{ count: number }>('/api/notifications/unread-count')
  return data.count
}

export async function markAsRead(id: number): Promise<Notification> {
  const { data } = await api.patch<Notification>(`/api/notifications/${id}/read`)
  return data
}
