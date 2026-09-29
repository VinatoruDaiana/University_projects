export interface Notification {
  notificationId: number
  userId: number
  senderId: number
  type: string
  referenceId: number | null
  content: string
  isRead: boolean
  createdAt: string
}
