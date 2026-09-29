import api from './axios'

export interface MessageResponse {
    messageId: number
    senderId: number
    receiverId: number
    content: string
    createdAt: string
    read: boolean
}

export interface ConversationResponse {
    userId: number
    username: string
    email: string
    role: string
}

export async function sendMessage(receiverId: number, content: string): Promise<MessageResponse> {
    const { data } = await api.post<MessageResponse>('/api/messages', { receiverId, content })
    return data
}

export async function getConversation(userId: number): Promise<MessageResponse[]> {
    const { data } = await api.get<MessageResponse[]>(`/api/messages/${userId}`)
    return data
}

export async function getConversationsList(): Promise<ConversationResponse[]> {
    const { data } = await api.get<ConversationResponse[]>('/api/messages')
    return data
}