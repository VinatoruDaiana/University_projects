import api from './axios'

const MODERATION_BASE = '/api/admin/moderation'

export type ReportStatus = 'PENDING' | 'IGNORED' | 'WARNED' | 'BANNED'

export interface ReportedUser {
  id: string
  username: string
  email: string
  photoUrl?: string
  status: string
}

export interface ModerationReport {
  reportId: string
  postId?: number
  contentPreview: string
  flaggedReason: string
  status: ReportStatus
  reportedUser: ReportedUser
  aiExplanation?: string
  aiConfidence?: number
  createdAt: string
}

export async function fetchReports(): Promise<ModerationReport[]> {
  const { data } = await api.get<ModerationReport[]>(`${MODERATION_BASE}/reports`)
  return data
}

export async function ignoreReport(reportId: string): Promise<void> {
  await api.patch(`${MODERATION_BASE}/reports/${reportId}/ignore`)
}

export async function warnUser(reportId: string): Promise<void> {
  await api.patch(`${MODERATION_BASE}/reports/${reportId}/warn`)
}

export async function banUserFromReport(reportId: string): Promise<void> {
  await api.patch(`${MODERATION_BASE}/reports/${reportId}/ban`)
}
