import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import Layout from '../components/Layout'
import Button from '../components/Button'
import {
  fetchReports,
  ignoreReport,
  warnUser,
  banUserFromReport,
  type ModerationReport,
  type ReportStatus,
} from '../api/moderation'
import { mockReports } from '../api/mockModeration'
import './ModerationDashboard.css'

type FilterStatus = 'ALL' | ReportStatus
type SortKey = 'newest' | 'confidence'

const STATUS_FILTERS: FilterStatus[] = ['ALL', 'PENDING', 'IGNORED', 'WARNED', 'BANNED']

function confidenceLevel(conf?: number): 'high' | 'medium' | 'low' {
  if (conf == null) return 'low'
  if (conf >= 0.9) return 'high'
  if (conf >= 0.7) return 'medium'
  return 'low'
}

function confidenceLabel(conf?: number): string {
  if (conf == null) return 'N/A'
  return `${Math.round(conf * 100)}%`
}

export default function ModerationDashboard() {
  const navigate = useNavigate()

  const [reports, setReports] = useState<ModerationReport[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)

  const [filterStatus, setFilterStatus] = useState<FilterStatus>('PENDING')
  const [sortKey, setSortKey] = useState<SortKey>('newest')

  const [actionLoading, setActionLoading] = useState<string | null>(null)
  const [confirmBan, setConfirmBan] = useState<ModerationReport | null>(null)
  const [expandedId, setExpandedId] = useState<string | null>(null)

  const showSuccess = useCallback((msg: string) => {
    setSuccess(msg)
    setTimeout(() => setSuccess(null), 3500)
  }, [])

  useEffect(() => {
    loadReports()
  }, [])

  async function loadReports() {
    setLoading(true)
    setError(null)
    try {
      const data = await fetchReports()
      setReports(data)
    } catch {
      setReports(mockReports)
    } finally {
      setLoading(false)
    }
  }

  async function handleIgnore(report: ModerationReport) {
    const key = `ignore-${report.reportId}`
    setActionLoading(key)
    setError(null)
    try {
      await ignoreReport(report.reportId)
      setReports(prev =>
        prev.map(r => (r.reportId === report.reportId ? { ...r, status: 'IGNORED' } : r)),
      )
      showSuccess(`Report #${report.reportId} ignored.`)
      setExpandedId(null)
    } catch {
      setError('Failed to ignore report.')
    } finally {
      setActionLoading(null)
    }
  }

  async function handleWarn(report: ModerationReport) {
    const key = `warn-${report.reportId}`
    setActionLoading(key)
    setError(null)
    try {
      await warnUser(report.reportId)
      setReports(prev =>
        prev.map(r => (r.reportId === report.reportId ? { ...r, status: 'WARNED' } : r)),
      )
      showSuccess(`@${report.reportedUser.username} has been warned.`)
      setExpandedId(null)
    } catch {
      setError('Failed to warn user.')
    } finally {
      setActionLoading(null)
    }
  }

  async function executeBan() {
    if (!confirmBan) return
    const report = confirmBan
    setConfirmBan(null)
    const key = `ban-${report.reportId}`
    setActionLoading(key)
    setError(null)
    try {
      await banUserFromReport(report.reportId)
      setReports(prev =>
        prev.map(r => (r.reportId === report.reportId ? { ...r, status: 'BANNED' } : r)),
      )
      showSuccess(`@${report.reportedUser.username} has been banned.`)
      setExpandedId(null)
    } catch {
      setError('Failed to ban user.')
    } finally {
      setActionLoading(null)
    }
  }

  const counts: Record<FilterStatus, number> = {
    ALL: reports.length,
    PENDING: reports.filter(r => r.status === 'PENDING').length,
    IGNORED: reports.filter(r => r.status === 'IGNORED').length,
    WARNED: reports.filter(r => r.status === 'WARNED').length,
    BANNED: reports.filter(r => r.status === 'BANNED').length,
  }

  const filtered = reports
    .filter(r => filterStatus === 'ALL' || r.status === filterStatus)
    .sort((a, b) =>
      sortKey === 'newest'
        ? new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
        : (b.aiConfidence ?? 0) - (a.aiConfidence ?? 0),
    )

  function toggleExpand(id: string) {
    setExpandedId(prev => (prev === id ? null : id))
  }

  return (
    <Layout>
      <div className="mod-page">
        <div className="mod-header">
          <div className="mod-header-text">
            <h1 className="mod-title">Content Moderation</h1>
            <p className="mod-subtitle">Review AI-flagged content and take action</p>
          </div>
          <button className="mod-back-btn" onClick={() => navigate('/admin')}>
            ← Admin Dashboard
          </button>
        </div>

        {/* Stats */}
        <div className="mod-stats">
          <div className="mod-stat-card mod-stat-card--pending">
            <div className="mod-stat-label">Pending</div>
            <div className="mod-stat-value">{counts.PENDING}</div>
          </div>
          <div className="mod-stat-card">
            <div className="mod-stat-label">Warned</div>
            <div className="mod-stat-value">{counts.WARNED}</div>
          </div>
          <div className="mod-stat-card">
            <div className="mod-stat-label">Banned</div>
            <div className="mod-stat-value">{counts.BANNED}</div>
          </div>
          <div className="mod-stat-card">
            <div className="mod-stat-label">Ignored</div>
            <div className="mod-stat-value">{counts.IGNORED}</div>
          </div>
          <div className="mod-stat-card">
            <div className="mod-stat-label">Total</div>
            <div className="mod-stat-value">{counts.ALL}</div>
          </div>
        </div>

        {error && <div className="mod-error">{error}</div>}
        {success && <div className="mod-success">{success}</div>}

        {/* Controls */}
        <div className="mod-controls">
          <div className="mod-filters">
            {STATUS_FILTERS.map(f => (
              <button
                key={f}
                className={`mod-filter-btn ${filterStatus === f ? 'mod-filter-btn--active' : ''}`}
                onClick={() => setFilterStatus(f)}
              >
                {f}
                <span className="mod-filter-count">{counts[f]}</span>
              </button>
            ))}
          </div>
          <div className="mod-sort">
            <span>Sort:</span>
            <select value={sortKey} onChange={e => setSortKey(e.target.value as SortKey)}>
              <option value="newest">Newest first</option>
              <option value="confidence">Highest confidence</option>
            </select>
          </div>
        </div>

        {/* Reports */}
        {loading && <div className="mod-loading">Loading reports…</div>}

        {!loading && (
          <div className="mod-reports-list">
            {filtered.length === 0 && (
              <div className="mod-empty">
                No {filterStatus === 'ALL' ? '' : filterStatus.toLowerCase() + ' '}reports found.
              </div>
            )}

            {filtered.map(report => {
              const isExpanded = expandedId === report.reportId
              const isProcessed = report.status !== 'PENDING'
              const isActing = actionLoading?.endsWith(report.reportId) ?? false
              const confLevel = confidenceLevel(report.aiConfidence)

              return (
                <div
                  key={report.reportId}
                  className={[
                    'mod-report-card',
                    isProcessed ? 'mod-report-card--processed' : '',
                    isExpanded ? 'mod-report-card--expanded' : '',
                  ]
                    .filter(Boolean)
                    .join(' ')}
                >
                  {/* Summary row — click to expand */}
                  <div
                    className="mod-report-summary"
                    onClick={() => toggleExpand(report.reportId)}
                  >
                    <div className="mod-report-left">
                      <div className="mod-report-meta">
                        <span
                          className={`mod-badge mod-badge--${report.status.toLowerCase()}`}
                        >
                          {report.status}
                        </span>
                        <span
                          className={`mod-badge mod-badge--confidence-${confLevel}`}
                        >
                          AI {confidenceLabel(report.aiConfidence)}
                        </span>
                        <span className="mod-report-id">#{report.reportId}</span>
                        <span className="mod-report-date">
                          {new Date(report.createdAt).toLocaleString()}
                        </span>
                      </div>

                      <p className="mod-report-content">{report.contentPreview}</p>

                      <div className="mod-report-footer">
                        <span className="mod-reason">{report.flaggedReason}</span>
                      </div>
                    </div>

                    <div className="mod-report-right">
                      <div className="mod-user-cell">
                        <div className="mod-avatar">
                          {report.reportedUser.photoUrl ? (
                            <img
                              src={report.reportedUser.photoUrl}
                              alt={report.reportedUser.username}
                            />
                          ) : (
                            <span>
                              {report.reportedUser.username[0]?.toUpperCase()}
                            </span>
                          )}
                        </div>
                        <span className="mod-username">
                          @{report.reportedUser.username}
                        </span>
                      </div>
                      <span className={`mod-chevron ${isExpanded ? 'mod-chevron--open' : ''}`}>
                        ▼
                      </span>
                    </div>
                  </div>

                  {/* Expanded detail */}
                  {isExpanded && (
                    <div className="mod-report-detail">
                      <div className="mod-detail-section">
                        <div className="mod-detail-label">Full Content</div>
                        <div className="mod-detail-content">{report.contentPreview}</div>
                      </div>

                      {report.aiExplanation && (
                        <div className="mod-detail-section">
                          <div className="mod-detail-label">AI Explanation</div>
                          <div className="mod-detail-explanation">
                            {report.aiExplanation}
                          </div>
                        </div>
                      )}

                      <div className="mod-detail-section">
                        <div className="mod-detail-label">Reported User</div>
                        <div className="mod-user-info-row">
                          <div className="mod-avatar">
                            {report.reportedUser.photoUrl ? (
                              <img
                                src={report.reportedUser.photoUrl}
                                alt={report.reportedUser.username}
                              />
                            ) : (
                              <span>
                                {report.reportedUser.username[0]?.toUpperCase()}
                              </span>
                            )}
                          </div>
                          <div className="mod-user-info-detail">
                            <span className="mod-username">
                              @{report.reportedUser.username}
                            </span>
                            <span className="mod-user-email">
                              {report.reportedUser.email}
                            </span>
                            <span className="mod-user-status">
                              Status: {report.reportedUser.status}
                            </span>
                          </div>
                        </div>
                      </div>

                      <div className="mod-actions-row">
                        <Button
                          variant="outline"
                          size="sm"
                          disabled={isActing || isProcessed}
                          onClick={() => handleIgnore(report)}
                        >
                          Ignore
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          className="mod-btn--warn"
                          disabled={isActing || isProcessed}
                          onClick={() => handleWarn(report)}
                        >
                          Warn User
                        </Button>
                        <Button
                          variant="outline"
                          size="sm"
                          className="mod-btn--ban"
                          disabled={isActing || isProcessed}
                          onClick={() => setConfirmBan(report)}
                        >
                          Ban User
                        </Button>
                      </div>
                    </div>
                  )}
                </div>
              )
            })}
          </div>
        )}

        {/* Ban confirmation modal */}
        {confirmBan && (
          <div className="mod-modal-overlay" onClick={() => setConfirmBan(null)}>
            <div className="mod-modal" onClick={e => e.stopPropagation()}>
              <h3>Ban User</h3>
              <p>
                Are you sure you want to ban{' '}
                <strong>@{confirmBan.reportedUser.username}</strong>? They will be
                blocked from logging in. This can be reversed from the Admin Dashboard.
              </p>
              <div className="mod-modal-actions">
                <Button variant="outline" onClick={() => setConfirmBan(null)}>
                  Cancel
                </Button>
                <Button
                  variant="primary"
                  className="mod-btn--ban"
                  onClick={executeBan}
                >
                  Ban User
                </Button>
              </div>
            </div>
          </div>
        )}
      </div>
    </Layout>
  )
}
