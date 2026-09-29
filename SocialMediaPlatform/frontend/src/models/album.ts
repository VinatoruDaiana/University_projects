export interface Album {
  albumId: number
  name: string
  userId: number
  createdAt: string
}

export interface AlbumPost {
  photoId: number
  photoUrl: string
  caption: string
  createdAt: string
}

export interface AlbumRequest {
  name: string
}

export interface UserPhoto {
  id: number
  albumId: number | null
  fileUrl: string
  caption: string | null
}
