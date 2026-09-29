import api from './axios'
import type { Photo, CreatePhotoRequest } from '../models/photo'

const PHOTOS_BASE = '/api/photos'

export async function uploadPhoto(data: CreatePhotoRequest): Promise<Photo> {
  const { data: photo } = await api.post<Photo>(PHOTOS_BASE, data)
  return photo
}

export async function getPhoto(id: number): Promise<Photo> {
  const { data: photo } = await api.get<Photo>(`${PHOTOS_BASE}/${id}`)
  return photo
}
