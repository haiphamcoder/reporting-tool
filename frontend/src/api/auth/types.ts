export interface SignUpRequest {
    first_name: string;
    last_name: string;
    email: string;
    username: string;
    password: string;
}

export interface SignUpResponse {
    userId: string;
    message: string;
}

export interface SignInRequest {
    username: string;
    password: string;
}

export interface SignInResponse {
    userId: string;
    token: string;
}

export interface AuthError {
    message: string;
    code?: string;
} 