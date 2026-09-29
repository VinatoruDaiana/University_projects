export interface User {
  id: number | string
  username: string
  email: string
  token?: string
  role?: string
  firstName?: string
  lastName?: string
  bio?: string
  birthdate?: string
  location?: string
  photoUrl?: string
  status?: string
  isBanned?: boolean
}
