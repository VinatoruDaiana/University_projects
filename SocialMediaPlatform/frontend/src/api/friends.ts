import api from './axios'
import type { Friendship } from '../models/friends'

const FRIENDS_BASE = '/api/friends'

export async function getFriendsList(): Promise<Friendship[]> {
    const token = localStorage.getItem('jwt_token');

    const { data } = await api.get<Friendship[]>(FRIENDS_BASE, {
        headers: {
            Authorization: token ? `Bearer ${token}` : ''
        }
    })

    return data
}

export async function sendFriendRequest(addresseeId: string) {
    const token = localStorage.getItem('jwt_token');

    const { data } = await api.post(`${FRIENDS_BASE}/request/${addresseeId}`, {}, {
        headers: {
            Authorization: token ? `Bearer ${token}` : ''
        }
    });

    return data;
}

export async function acceptFriendRequest(requestId: string) {
    const token = localStorage.getItem('jwt_token');

    const { data } = await api.patch(`${FRIENDS_BASE}/${requestId}/accept`, {}, {
        headers: {
            Authorization: token ? `Bearer ${token}` : ''
        }
    });

    return data;
}

export async function declineFriendRequest(requestId: string) {
    const token = localStorage.getItem('jwt_token');

    const { data } = await api.patch(`${FRIENDS_BASE}/${requestId}/decline`, {}, {
        headers: {
            Authorization: token ? `Bearer ${token}` : ''
        }
    });

    return data;
}