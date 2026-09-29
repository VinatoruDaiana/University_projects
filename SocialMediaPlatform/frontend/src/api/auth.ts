import api from './axios'
import type { RegisterRequest, LoginRequest, LoginResponse } from '../models/auth'
import type { User } from '../models/user'

const AUTH_BASE = '/api/auth'

export async function registerUser(data: RegisterRequest): Promise<User> {
  const { data: responseData } = await api.post(`${AUTH_BASE}/register`, data)
  return responseData
}

export async function loginUser(data: LoginRequest): Promise<LoginResponse> {
  const { data: responseData } = await api.post<LoginResponse>(`${AUTH_BASE}/login`, data)
  return responseData
}

export async function requestPasswordReset(email: string): Promise<void> {
  await api.post(`${AUTH_BASE}/reset`, { email })
}

export async function confirmPasswordReset(token: string, newPassword: string): Promise<void> {
  await api.post(`${AUTH_BASE}/reset-confirm?token=${encodeURIComponent(token)}`, { newPassword })
}
