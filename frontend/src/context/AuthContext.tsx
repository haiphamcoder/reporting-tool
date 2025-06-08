import { createContext, useState, useEffect, useContext } from 'react';
import { API_CONFIG } from '../config/api';
import { Alert, Snackbar } from '@mui/material';

interface AuthContextType {
    userId: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    checkAuth: () => Promise<void>;
    logout: () => Promise<void>;
}

// Create the context object with a default value
const AuthContext = createContext<AuthContextType | null>({
    userId: null,
    isAuthenticated: false,
    isLoading: true,
    checkAuth: async () => { },
    logout: async () => { },
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [userId, setUserId] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const checkAuth = async () => {
        try {
            // Comment out actual API call for now
            // const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.AUTH_INFO}`, { method: "GET", credentials: "include" });
            // if (response.ok) {
            //     const data = await response.json();
            //     if (data.user_id) {
            //         setUserId(data.user_id);
            //     } else {
            //         setUserId(null);
            //     }
            // }

            // Mock authentication check
            setUserId("mock-user-id-123");
            
        } catch (error) {
            console.error("Error checking authentication status", error);
            setError("Not connected to the server! Please try again later.");
            setUserId(null);
        }
        setIsLoading(false);
    }

    useEffect(() => {
        checkAuth();
    }, []);

    const logout = async () => {
        try {
            await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.LOGOUT}`, { method: "POST", credentials: "include" });
        } catch (error) {
            console.error("Error logging out", error);
        }
        setUserId(null);
    };

    return (
        <AuthContext.Provider value={{ userId, isAuthenticated: !!userId, isLoading, checkAuth, logout }}>
            {children}

            {/* Display an error message if one exists */}
            <Snackbar
                open={!!error}
                autoHideDuration={4000}
                onClose={() => setError(null)}
                anchorOrigin={{ vertical: "top", horizontal: "right" }}
            >
                <Alert severity="error" sx={{ width: "100%" }} onClose={() => setError(null)}>
                    {error}
                </Alert>
            </Snackbar>
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error("useAuth must be used within an AuthProvider");
    }
    return context;
};

export { AuthContext };