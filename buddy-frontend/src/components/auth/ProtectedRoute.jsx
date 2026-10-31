import { Navigate, Outlet } from 'react-router-dom'


function ProtectedRoute() {
    const accessToken =
        localStorage.getItem('buddyAccessToken') ||
        sessionStorage.getItem('buddyAccessToken')

    if (!accessToken) {
        return <Navigate to="/login" replace />
    }

    return <Outlet />
}

export default ProtectedRoute