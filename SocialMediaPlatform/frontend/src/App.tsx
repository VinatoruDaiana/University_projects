import { Routes, Route, useLocation } from 'react-router-dom'
import Home from './pages/Home'
import Register from './pages/Register'
import Login from './pages/Login'
import ForgotPassword from './pages/ForgotPassword'
import ResetPassword from './pages/ResetPassword'
import Profile from './pages/Profile'
import Albums from './pages/Albums'
import AlbumDetail from './pages/AlbumDetail'
import Feed from './pages/Feed'
import Chat from './pages/Chat'
import PostDetail from './pages/PostDetail';
import AdminDashboard from './pages/AdminDashboard'
import ModerationDashboard from './pages/ModerationDashboard'
import AdminRoute from './components/AdminRoute'

export default function App() {
    const location = useLocation();
    const state = location.state as { backgroundLocation?: Location };
    return (
        <>
            <Routes location={state?.backgroundLocation || location}>
                <Route path="/" element={<Home />} />
                <Route path="/register" element={<Register />} />
                <Route path="/login" element={<Login />} />
                <Route path="/forgot-password" element={<ForgotPassword />} />
                <Route path="/reset-password" element={<ResetPassword />} />
                <Route path="/profile/:username" element={<Profile />} />
                <Route path="/albums" element={<Albums />} />
                <Route path="/albums/:albumId" element={<AlbumDetail />} />
                <Route path="/feed" element={<Feed />} />
                <Route path="/chat" element={<Chat />} />
                <Route path="/post/:postId" element={<PostDetail />} />
                <Route path="/admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
                <Route path="/admin/moderation" element={<AdminRoute><ModerationDashboard /></AdminRoute>} />
            </Routes>

            {state?.backgroundLocation && (
                <Routes>
                    <Route path="/post/:postId" element={<PostDetail />} />
                    <Route path="/admin" element={<AdminRoute><AdminDashboard /></AdminRoute>} />
                </Routes>
            )}
        </>
    )
}