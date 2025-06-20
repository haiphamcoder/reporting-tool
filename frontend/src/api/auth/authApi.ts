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

    updatePassword: async (userId: string, newPassword: string): Promise<void> => {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.UPDATE_USER.replace(':user_id', userId)}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                password: newPassword
            }),
            credentials: 'include',
        });
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
    }
}; 