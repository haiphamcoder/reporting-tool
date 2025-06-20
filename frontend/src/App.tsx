import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { Route, Routes, Navigate, BrowserRouter } from 'react-router-dom';
import SignInPage from './pages/SignInPage';
import SignUpPage from './pages/SignUpPage';
import Dashboard from './pages/Dashboard';
import ChangePasswordPage from './pages/ChangePasswordPage';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './routes/ProtectedRoute';
import { ContentProvider } from './context/ContentContext';
import { StatisticsProvider } from './context/StatisticsContext';

ReactDOM.createRoot(document.querySelector("#root")!).render(
  <React.StrictMode>
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/auth/signin" element={<SignInPage />} />
          <Route path="/auth/signup" element={<SignUpPage />} />
          <Route path="/change-password" element={<ChangePasswordPage />} />
          <Route path="/dashboard/:section/*"
            element={
              <ProtectedRoute>
                <ContentProvider>
                  <StatisticsProvider>
                    <Dashboard />
                  </StatisticsProvider>
                </ContentProvider>
              </ProtectedRoute>
            }
          />
          <Route path="/dashboard" 
            element={
              <ProtectedRoute>
                <Navigate to="/dashboard/home" />
              </ProtectedRoute>
            }
          />
          <Route path="/" 
            element={
              <ProtectedRoute>
                <Navigate to="/dashboard/home" />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  </React.StrictMode>
);