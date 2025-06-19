import React, { useState } from 'react';
import {
    Box,
    Card,
    CardContent,
    TextField,
    Button,
    Typography,
    Alert,
    CircularProgress,
    InputAdornment,
    IconButton,
    Container,
    Stack
} from '@mui/material';
import { Visibility, VisibilityOff, LockOutlined, LogoutRounded } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth/authApi';
import { useAuth } from '../context/AuthContext';
import AppTheme from '../theme/AppTheme';
import ColorSchemeToggle from '../theme/ColorSchemeToggle';

const ChangePasswordPage: React.FC = () => {
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        
        if (!user) {
            setError('User information not available');
            return;
        }

        // Validate passwords
        if (newPassword.length < 8) {
            setError('Password must be at least 8 characters long');
            return;
        }

        // Check for at least one uppercase letter, one lowercase letter, one number, and one special character
        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/;
        if (!passwordRegex.test(newPassword)) {
            setError('Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)');
            return;
        }

        if (newPassword !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        setIsSubmitting(true);
        setError('');

        try {
            await authApi.updatePassword(user.user_id, newPassword);
            // Don't refresh user info here as it might cause unwanted redirects
            // Just navigate to dashboard directly
            navigate('/dashboard');
        } catch (error) {
            setError(error instanceof Error ? error.message : 'Failed to update password');
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleClickShowNewPassword = () => {
        setShowNewPassword(!showNewPassword);
    };

    const handleClickShowConfirmPassword = () => {
        setShowConfirmPassword(!showConfirmPassword);
    };

    const handleLogout = async () => {
        try {
            await logout();
            navigate('/auth/signin');
        } catch (error) {
            console.error('Failed to logout:', error);
            // Still navigate to signin even if logout fails
            navigate('/auth/signin');
        }
    };

    if (!user) {
        return (
            <AppTheme>
                <Box
                    sx={{
                        minHeight: '100vh',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
                    }}
                >
                    <CircularProgress />
                </Box>
            </AppTheme>
        );
    }

    return (
        <AppTheme>
            <Box
                sx={{
                    minHeight: '100vh',
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    p: 2
                }}
            >
                <Container maxWidth="sm">
                    <Stack spacing={2} alignItems="center">
                        {/* Header */}
                        <Box textAlign="center" mb={2} sx={{ position: 'relative' }}>
                            <LockOutlined sx={{ fontSize: 48, color: 'white', mb: 1 }} />
                            <Typography variant="h4" color="white" gutterBottom>
                                Change Password Required
                            </Typography>
                            <Typography variant="body1" color="white" sx={{ opacity: 0.9 }}>
                                As an admin user, you need to change your password on first login
                            </Typography>
                            
                            {/* Logout Button */}
                            <Button
                                variant="outlined"
                                color="inherit"
                                startIcon={<LogoutRounded />}
                                onClick={handleLogout}
                                sx={{ 
                                    mt: 2, 
                                    color: 'white', 
                                    borderColor: 'white',
                                    '&:hover': {
                                        borderColor: 'white',
                                        backgroundColor: 'rgba(255, 255, 255, 0.1)'
                                    }
                                }}
                            >
                                Logout
                            </Button>
                        </Box>

                        {/* Color Scheme Toggle */}
                        <Box position="absolute" top={16} right={16}>
                            <ColorSchemeToggle />
                        </Box>

                        {/* Password Form Card */}
                        <Card sx={{ width: '100%', maxWidth: 450, boxShadow: 3 }}>
                            <CardContent sx={{ p: 4 }}>
                                <form onSubmit={handleSubmit}>
                                    {error && (
                                        <Alert severity="error" sx={{ mb: 3 }}>
                                            {error}
                                        </Alert>
                                    )}

                                    <Stack spacing={3}>
                                        <TextField
                                            fullWidth
                                            label="New Password"
                                            type={showNewPassword ? 'text' : 'password'}
                                            value={newPassword}
                                            onChange={(e) => setNewPassword(e.target.value)}
                                            required
                                            disabled={isSubmitting}
                                            InputProps={{
                                                endAdornment: (
                                                    <InputAdornment position="end">
                                                        <IconButton
                                                            onClick={handleClickShowNewPassword}
                                                            edge="end"
                                                            disabled={isSubmitting}
                                                        >
                                                            {showNewPassword ? <VisibilityOff /> : <Visibility />}
                                                        </IconButton>
                                                    </InputAdornment>
                                                ),
                                            }}
                                            helperText="Password must be at least 8 characters with uppercase, lowercase, number, and special character"
                                        />

                                        <TextField
                                            fullWidth
                                            label="Confirm New Password"
                                            type={showConfirmPassword ? 'text' : 'password'}
                                            value={confirmPassword}
                                            onChange={(e) => setConfirmPassword(e.target.value)}
                                            required
                                            disabled={isSubmitting}
                                            InputProps={{
                                                endAdornment: (
                                                    <InputAdornment position="end">
                                                        <IconButton
                                                            onClick={handleClickShowConfirmPassword}
                                                            edge="end"
                                                            disabled={isSubmitting}
                                                        >
                                                            {showConfirmPassword ? <VisibilityOff /> : <Visibility />}
                                                        </IconButton>
                                                    </InputAdornment>
                                                ),
                                            }}
                                        />

                                        <Button
                                            type="submit"
                                            variant="contained"
                                            size="large"
                                            fullWidth
                                            disabled={isSubmitting || !newPassword || !confirmPassword}
                                            startIcon={isSubmitting ? <CircularProgress size={20} /> : null}
                                            sx={{ mt: 2 }}
                                        >
                                            {isSubmitting ? 'Updating Password...' : 'Update Password'}
                                        </Button>
                                    </Stack>
                                </form>
                            </CardContent>
                        </Card>
                    </Stack>
                </Container>
            </Box>
        </AppTheme>
    );
};

export default ChangePasswordPage; 