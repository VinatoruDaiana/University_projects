export interface Post {
  id: number
  userId: number
  photoId: number | null
  photoUrl: string | null
  content: string
  createdAt: string
  updatedAt?: string;
  username: string
  likeCount: number
  likedByCurrentUser: boolean
}

export interface CreatePostRequest {
  content: string
  photoId?: number | null
}
