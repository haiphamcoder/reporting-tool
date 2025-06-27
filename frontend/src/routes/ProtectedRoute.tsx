import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { JSX } from 'react';
import LoadingPage from '../pages/LoadingPage';

export const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
    const { isAuthenticated, isLoading, user} = useAuth();
    const location = useLocation();

    if (isLoading) {
        return <LoadingPage />;
    }

    if (!isAuthenticated || (user && user.provider === 'local' && user.role === 'admin' && user.first_login)) {
        if (user && user.provider === 'local' && user.role === 'admin' && user.first_login) {
            return <Navigate to="/change-password" state={{ from: location }} replace />;
        } else {
            return <Navigate to="/auth/signin" state={{ from: location }} replace />;
        }
    }

    return children;
};

export const AdminFirstLoginRoute = ({ children }: { children: JSX.Element }) => {
    const { isAuthenticated, isLoading, user } = useAuth();
    const location = useLocation();

    if (isLoading) {
        return <LoadingPage />;
    }

    // Nếu chưa đăng nhập, chuyển về trang signin
    if (!isAuthenticated) {
        return <Navigate to="/auth/signin" state={{ from: location }} replace />;
    }

    // Nếu đã đăng nhập nhưng không phải admin local lần đầu, chuyển về dashboard
    if (!user || user.role !== 'admin' || user.provider !== 'local' || !user.first_login) {
        return <Navigate to="/dashboard" replace />;
    }

    // Chỉ admin local lần đầu mới có thể truy cập
    return children;
};