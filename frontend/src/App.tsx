import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { Route, Routes, Navigate, BrowserRouter } from 'react-router-dom';
import SignInPage from './pages/SignInPage';
import SignUpPage from './pages/SignUpPage';
import Dashboard from './pages/Dashboard';
import ChangePasswordPage from './pages/ChangePasswordPage';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute, AdminFirstLoginRoute } from './routes/ProtectedRoute';
import { StatisticsProvider } from './context/StatisticsContext';
import { NotificationProvider } from './context/NotificationContext';
import './config/chartjs'; // Import Chart.js configuration early

ReactDOM.createRoot(document.querySelector("#root")!).render(
  <React.StrictMode>
    <AuthProvider>
      <NotificationProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/auth/signin" element={<SignInPage />} />
            <Route path="/auth/signup" element={<SignUpPage />} />
            <Route path="/change-password" 
              element={
                <AdminFirstLoginRoute>
                  <ChangePasswordPage />
                </AdminFirstLoginRoute>
              } 
            />
            <Route path="/dashboard/*"
              element={
                <ProtectedRoute>
                  <StatisticsProvider>
                    <Dashboard />
                  </StatisticsProvider>
                </ProtectedRoute>
              }
            />
            <Route path="/" 
              element={
                <ProtectedRoute>
                  <Navigate to="/dashboard" />
                </ProtectedRoute>
              }
            />
          </Routes>
        </BrowserRouter>
      </NotificationProvider>
    </AuthProvider>
  </React.StrictMode>
);