export interface FeedPost {
  id: number
  userId: number
  username: string
  content: string
  photoUrl: string | null
  photoId: number | null
  profilePhotoUrl: string | null
  createdAt: string
}
