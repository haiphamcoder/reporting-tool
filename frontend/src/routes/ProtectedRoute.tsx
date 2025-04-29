import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { JSX } from 'react';
import LoadingPage from '../pages/LoadingPage';

export const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
    const { isAuthenticated , isLoading } = useAuth();
    const location = useLocation();

    if (isLoading) {
        return <LoadingPage />;
    }

    if (!isAuthenticated) {
        return <Navigate to="/auth/signin" state={{ from: location }} replace />;
    }

    return children;
};