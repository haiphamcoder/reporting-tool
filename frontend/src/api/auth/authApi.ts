import { API_CONFIG } from '../../config/api';
import { SignUpRequest, SignUpResponse, SignInRequest, SignInResponse } from './types';

export interface UserInfo {
    user_id: string;
    username: string;
    email: string;
    email_verified: boolean;
    role: string;
    first_name: string;
    last_name: string;
    avatar_url: string;
    provider: string;
    first_login: boolean;
}

export interface CheckProviderResponse {
    result: {
        provider: string;
    }
}

export interface ForgotPasswordResponse {
    message: string;
}

export interface VerifyOtpResponse {
    success: boolean;
    message: string;
}

export interface ResetPasswordResponse {
    success: boolean;
    message: string;
}

export const authApi = {
    signUp: async (data: SignUpRequest): Promise<SignUpResponse> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REGISTER}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to sign up');
        }

        return response.json();
    },

    updatePassword: async (userId: string, oldPassword: string, newPassword: string): Promise<UserInfo> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.UPDATE_USER.replace(':user_id', userId)}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                old_password: oldPassword,
                new_password: newPassword
            }),
            credentials: 'include',
        });
        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to update password');
        }

        return response.json().then(data => data.result);
    },

    signIn: async (data: SignInRequest): Promise<SignInResponse> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.AUTHENTICATE}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
            credentials: 'include',
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to sign in');
        }

        return response.json();
    },

    getCurrentUser: async (): Promise<UserInfo> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.GET_CURRENT_USER}`, {
            method: 'GET',
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error('Not authenticated');
        }

        return response.json().then(data => data.result);
    },

    logout: async (): Promise<void> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.LOGOUT}`, {
            method: 'POST',
            credentials: 'include',
        });

        if (!response.ok) {
            throw new Error('Failed to logout');
        }
    },

    // User Management APIs for password reset
    checkProvider: async (email: string): Promise<CheckProviderResponse> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}/user-management/check-provider`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email }),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to check provider');
        }

        return response.json();
    },

    forgotPassword: async (email: string): Promise<ForgotPasswordResponse> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}/user-management/forgot-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email }),
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to send reset email');
        }

        return response.json();
    },

    verifyOtp: async (email: string, otp: string): Promise<VerifyOtpResponse> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}/user-management/verify-otp`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, otp }),
            credentials: 'include',
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to verify OTP');
        }

        return response.json();
    },

    resetPassword: async (email: string, password: string): Promise<ResetPasswordResponse> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}/user-management/reset-password`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, password }),
            credentials: 'include',
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Failed to reset password');
        }

        return response.json();
    }
}; 