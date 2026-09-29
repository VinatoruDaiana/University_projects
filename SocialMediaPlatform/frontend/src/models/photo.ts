export interface Photo {
  id: number
  albumId: number | null
  uploadedBy: number
  fileUrl: string
  caption: string | null
}

export interface CreatePhotoRequest {
  albumId?: number | null
  fileUrl: string
  caption?: string | null
}
