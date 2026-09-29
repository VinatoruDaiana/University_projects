export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  user: {
    id: string
    email: string
    username: string
    role: string
    firstName?: string
    lastName?: string
    bio?: string
    birthdate?: string
    location?: string
    photoUrl?: string
    status?: string
    isBanned?: boolean
  }
}