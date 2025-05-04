import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { Route, Routes, Navigate, BrowserRouter } from 'react-router-dom';
import SignInPage from './pages/SignInPage';
import SignUpPage from './pages/SignUpPage';
import Dashboard from './pages/Dashboard';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './routes/ProtectedRoute';
import OAuth2RedirectHandler from './routes/OAuth2RedirectHandler';
import { ContentProvider } from './context/ContentContext';

ReactDOM.createRoot(document.querySelector("#root")!).render(
  <React.StrictMode>
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/auth/signin" element={<SignInPage />} />
          <Route path="/auth/signup" element={<SignUpPage />} />
          <Route path="/dashboard"
            element={
              <ProtectedRoute>
                <ContentProvider>
                  <Dashboard />
                </ContentProvider>
              </ProtectedRoute>
            } />
          <Route path="/" element={<Navigate to="/dashboard" />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  </React.StrictMode>
);