import { useState, useEffect, useRef } from 'react'
import { useLocation } from 'react-router-dom'
import Layout from '../components/Layout'
import Button from '../components/Button'
import { getConversationsList, getConversation, sendMessage } from '../api/messages'
import type { ConversationResponse, MessageResponse } from '../api/messages'
import './Chat.css'

export default function Chat() {
    const location = useLocation()
    const userStr = localStorage.getItem('pulse_user')
    const currentUser = userStr ? JSON.parse(userStr) : null

    const [conversations, setConversations] = useState<ConversationResponse[]>([])
    const [selectedUser, setSelectedUser] = useState<ConversationResponse | null>(null)
    const [messages, setMessages] = useState<MessageResponse[]>([])
    const [newMessage, setNewMessage] = useState('')
    const [sending, setSending] = useState(false)
    const [loadingInitial, setLoadingInitial] = useState(true)
    const [errorMsg, setErrorMsg] = useState('')

    const messagesEndRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        fetchConversations().finally(() => setLoadingInitial(false))
    }, [])

    useEffect(() => {
        if (location.state?.newUser) {
            const newUser = location.state.newUser as ConversationResponse
            setSelectedUser(newUser)

            setConversations(prev => {
                if (!prev.find(c => c.userId === newUser.userId)) {
                    return [newUser, ...prev]
                }
                return prev
            })

            window.history.replaceState({}, document.title)
        }
    }, [location.state])

    useEffect(() => {
        if (!selectedUser) return

        setErrorMsg('')
        fetchMessages(selectedUser.userId)

        const interval = setInterval(() => {
            fetchMessages(selectedUser.userId)
        }, 3000)

        return () => clearInterval(interval)
    }, [selectedUser])

    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }, [messages])

    async function fetchConversations() {
        try {
            const data = await getConversationsList()
            setConversations(data)
        } catch (error) {
            console.error('Error loading conversations', error)
        }
    }

    async function fetchMessages(userId: number) {
        try {
            const data = await getConversation(userId)
            setMessages(data)
        } catch (error: any) {
            if (error.response?.status === 403) {
                setErrorMsg('You do not have permission to message this user.')
            }
            console.error('Error loading messages', error)
        }
    }

    async function handleSendMessage(e: React.FormEvent) {
        e.preventDefault()
        if (!newMessage.trim() || !selectedUser) return

        setSending(true)
        setErrorMsg('')
        try {
            const sentMsg = await sendMessage(selectedUser.userId, newMessage)

            setMessages(prev => [...prev, sentMsg])
            setNewMessage('')

            await fetchConversations()
        } catch (error: any) {
            if (error.response?.status === 403) {
                setErrorMsg('You cannot message this user (friends or admins only).')
            }
            console.error('Error sending message', error)
        } finally {
            setSending(false)
        }
    }

    function formatTime(dateString: string) {
        return new Date(dateString).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    }

    return (
        <Layout>
            <div className="chat-page">
                <aside className="chat-sidebar">
                    <div className="chat-sidebar__header">Conversations</div>
                    <div className="chat-sidebar__list">
                        {loadingInitial ? (
                            <div style={{ padding: '24px', color: 'var(--text)' }}>Loading...</div>
                        ) : conversations.length === 0 ? (
                            <div style={{ padding: '24px', color: 'var(--text)' }}>You have no active conversations.</div>
                        ) : (
                            conversations.map(conv => (
                                <div
                                    key={conv.userId}
                                    className={`chat-sidebar__item ${selectedUser?.userId === conv.userId ? 'chat-sidebar__item--active' : ''}`}
                                    onClick={() => setSelectedUser(conv)}
                                >
                                    <div className="chat-sidebar__name">{conv.username}</div>
                                </div>
                            ))
                        )}
                    </div>
                </aside>

                <main className="chat-window">
                    {selectedUser ? (
                        <>
                            <div className="chat-window__header">
                                {selectedUser.username}
                            </div>

                            {errorMsg && (
                                <div style={{ padding: '12px 24px', background: '#ffebee', color: '#c62828', fontSize: '14px' }}>
                                    {errorMsg}
                                </div>
                            )}

                            <div className="chat-window__messages">
                                {messages.map(msg => {
                                    const isMine = msg.senderId === Number(currentUser?.id)
                                    return (
                                        <div key={msg.messageId} className={`message ${isMine ? 'message--sent' : 'message--received'}`}>
                                            <div>{msg.content}</div>
                                            <div style={{ fontSize: '11px', opacity: 0.7, marginTop: '4px', textAlign: isMine ? 'right' : 'left' }}>
                                                {formatTime(msg.createdAt)}
                                            </div>
                                        </div>
                                    )
                                })}
                                <div ref={messagesEndRef} />
                            </div>
                            <form className="chat-window__input-area" onSubmit={handleSendMessage}>
                                <input
                                    type="text"
                                    className="chat-window__input"
                                    placeholder="Type a message..."
                                    value={newMessage}
                                    onChange={e => setNewMessage(e.target.value)}
                                    disabled={sending || !!errorMsg}
                                />
                                <Button type="submit" variant="primary" disabled={!newMessage.trim() || sending || !!errorMsg}>
                                    Send
                                </Button>
                            </form>
                        </>
                    ) : (
                        <div className="chat-empty">
                            Select a conversation to start chatting.
                        </div>
                    )}
                </main>
            </div>
        </Layout>
    )
}