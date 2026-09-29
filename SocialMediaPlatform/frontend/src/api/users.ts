import api from './axios'
import type { User } from '../models/user'

export interface UpdateProfileRequest {
  firstName?: string
  lastName?: string
  bio?: string
  birthdate?: string
  location?: string
  photoUrl?: string
}

export interface UserSearchResult {
    id: string;
    username: string;
    firstName?: string;
    lastName?: string;
    photoUrl?: string;
}

export async function updateProfile(userId: number, data: UpdateProfileRequest): Promise<User> {
  const { data: responseData } = await api.patch(`/api/users/${userId}`, data)
  return responseData
}

export async function getUserByUsername(username: string): Promise<User> {
  const { data } = await api.get(`/api/users/profile/${username}`)
  return data
}

export async function uploadProfilePhoto(userId: number, file: File): Promise<User> {
  const formData = new FormData()
  formData.append('file', file)
  const { data } = await api.post<User>(`/api/users/${userId}/profile/photo`, formData)
  return data
}

export const searchUsers = async (query: string): Promise<UserSearchResult[]> => {
    const response = await api.get(`/api/users/search?q=${encodeURIComponent(query)}`);
    return response.data;
};