import { createContext, useState, useContext, useEffect } from 'react';
import { authApi } from '../api/auth/authApi';
import { Alert, Snackbar } from '@mui/material';

interface AuthContextType {
    userId: string | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    setAuthenticated: (value: boolean) => void;
    logout: () => Promise<void>;
}

// Create the context object with a default value
const AuthContext = createContext<AuthContextType | null>({
    userId: null,
    isAuthenticated: false,
    isLoading: false,
    setAuthenticated: () => { },
    logout: async () => { },
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [userId, setUserId] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(true); // Set initial loading to true
    const [error, setError] = useState<string | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const setAuthenticated = (value: boolean) => {
        setIsAuthenticated(value);
        if (value) {
            setUserId('authenticated'); // Set a dummy value since we don't need the actual userId
        } else {
            setUserId(null);
        }
    };

    const logout = async () => {
        try {
            await authApi.logout();
            setAuthenticated(false);
        } catch (error) {
            console.error("Error logging out", error);
            setError("Failed to logout. Please try again.");
        }
    };

    // Check authentication status when component mounts
    useEffect(() => {
        const checkAuthStatus = async () => {
            try {
                await authApi.getCurrentUser();
                setAuthenticated(true);
            } catch (error) {
                setAuthenticated(false);
            } finally {
                setIsLoading(false);
            }
        };

        checkAuthStatus();
    }, []);

    return (
        <AuthContext.Provider value={{ 
            userId, 
            isAuthenticated, 
            isLoading, 
            setAuthenticated,
            logout 
        }}>
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