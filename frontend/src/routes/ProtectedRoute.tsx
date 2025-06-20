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