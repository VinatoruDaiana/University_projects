import { Navigate } from 'react-router-dom'

export default function AdminRoute({ children }: { children: React.ReactNode }) {
  const storedUser = JSON.parse(localStorage.getItem('pulse_user') ?? 'null')
  if (!storedUser || storedUser.role !== 'ADMIN') {
    return <Navigate to="/login" replace />
  }
  return <>{children}</>
}
