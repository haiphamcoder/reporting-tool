import { createContext, useState, useContext, useEffect } from 'react';
import { authApi, UserInfo } from '../api/auth/authApi';
import { Alert, Snackbar } from '@mui/material';

interface AuthContextType {
    user: UserInfo | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    setAuthenticated: (value: boolean) => void;
    setUser: (user: UserInfo | null) => void;
    logout: () => Promise<void>;
}

// Create the context object with a default value
const AuthContext = createContext<AuthContextType | null>({
    user: null,
    isAuthenticated: false,
    isLoading: false,
    setAuthenticated: () => { },
    setUser: () => { },
    logout: async () => { },
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<UserInfo | null>(null);
    const [isLoading, setIsLoading] = useState(true); // Set initial loading to true
    const [error, setError] = useState<string | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    const setAuthenticated = (value: boolean) => {
        setIsAuthenticated(value);
        if (!value) {
            setUser(null);
        }
    };

    const logout = async () => {
        try {
            await authApi.logout();
            setAuthenticated(false);
            setUser(null);
        } catch (error) {
            console.error("Error logging out", error);
            setError("Failed to logout. Please try again.");
        }
    };

    // Check authentication status when component mounts
    useEffect(() => {
        const checkAuthStatus = async () => {
            try {
                const userInfo = await authApi.getCurrentUser();
                setUser(userInfo);
                setAuthenticated(true);
            } catch (error) {
                setAuthenticated(false);
                setUser(null);
            } finally {
                setIsLoading(false);
            }
        };

        checkAuthStatus();
    }, []);

    return (
        <AuthContext.Provider value={{ 
            user,
            isAuthenticated, 
            isLoading, 
            setAuthenticated,
            setUser,
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