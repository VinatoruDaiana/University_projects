import type { ModerationReport } from './moderation'

export const mockReports: ModerationReport[] = [
  {
    reportId: 'r-001',
    postId: 101,
    contentPreview: 'You are absolutely worthless and should disappear forever.',
    flaggedReason: 'Hate speech / personal attack',
    status: 'PENDING',
    reportedUser: {
      id: 'u-11',
      username: 'shadow_troll99',
      email: 'shadow@example.com',
      status: 'ACTIVE',
    },
    aiExplanation:
      'The message contains direct personal insults and language associated with harassment. High likelihood of targeted abuse.',
    aiConfidence: 0.95,
    createdAt: '2026-05-19T08:14:00Z',
  },
  {
    reportId: 'r-002',
    postId: 205,
    contentPreview: 'Click here to claim your FREE iPhone → bit.ly/totally-not-spam',
    flaggedReason: 'Spam / phishing link',
    status: 'PENDING',
    reportedUser: {
      id: 'u-22',
      username: 'deal_hunter_x',
      email: 'deals@example.com',
      status: 'ACTIVE',
    },
    aiExplanation:
      'Post contains a shortened URL pattern commonly used in phishing campaigns alongside urgency-based promotional language.',
    aiConfidence: 0.88,
    createdAt: '2026-05-18T21:30:00Z',
  },
  {
    reportId: 'r-003',
    postId: 317,
    contentPreview: 'Anyone know a good recipe for banana bread? Mine always turns out dense.',
    flaggedReason: 'Possible self-harm reference',
    status: 'IGNORED',
    reportedUser: {
      id: 'u-33',
      username: 'baker_maggie',
      email: 'maggie@example.com',
      status: 'ACTIVE',
    },
    aiExplanation:
      'Model flagged the word "dense" in an unusual context, but manual review confirmed this is a benign cooking question.',
    aiConfidence: 0.41,
    createdAt: '2026-05-17T14:05:00Z',
  },
  {
    reportId: 'r-004',
    postId: 422,
    contentPreview: 'I know where you live. Keep posting and see what happens.',
    flaggedReason: 'Threat / doxxing',
    status: 'WARNED',
    reportedUser: {
      id: 'u-44',
      username: 'anon_storm',
      email: 'anon@example.com',
      status: 'WARNED',
    },
    aiExplanation:
      'Message constitutes an explicit threat implying knowledge of the target\'s location. User has been issued a formal warning.',
    aiConfidence: 0.97,
    createdAt: '2026-05-16T09:52:00Z',
  },
  {
    reportId: 'r-005',
    postId: 530,
    contentPreview: 'Buy followers cheap!!! 10k for $5, DM me now!!!',
    flaggedReason: 'Spam / commercial solicitation',
    status: 'BANNED',
    reportedUser: {
      id: 'u-55',
      username: 'follower_farm_bot',
      email: 'bot@example.com',
      status: 'BANNED',
    },
    aiExplanation:
      'Repeated spam posts selling fake engagement. Account was confirmed as an automated bot network and has been permanently banned.',
    aiConfidence: 0.99,
    createdAt: '2026-05-15T18:00:00Z',
  },
]
