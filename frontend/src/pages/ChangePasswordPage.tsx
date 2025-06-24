import React, { useState } from 'react';
import {
    Box,
    Card,
    TextField,
    Button,
    Typography,
    Alert,
    CircularProgress,
    InputAdornment,
    IconButton,
    Stack,
    CssBaseline,
    FormLabel
} from '@mui/material';
import { Visibility, VisibilityOff, LockOutlined, LogoutRounded } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { styled } from '@mui/material/styles';
import { authApi } from '../api/auth/authApi';
import { useAuth } from '../context/AuthContext';
import AppTheme from '../theme/AppTheme';
import ColorSchemeToggle from '../theme/ColorSchemeToggle';

const StyledCard = styled(Card)(({ theme }) => ({
    display: 'flex',
    flexDirection: 'column',
    alignSelf: 'center',
    width: '100%',
    padding: theme.spacing(4),
    gap: theme.spacing(2),
    margin: 'auto',
    maxHeight: '100vh',
    overflow: 'auto',
    boxShadow:
        'hsla(220, 30%, 5%, 0.05) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.05) 0px 15px 35px -5px',
    [theme.breakpoints.up('sm')]: {
        width: '450px',
    },
    ...theme.applyStyles('dark', {
        boxShadow:
            'hsla(220, 30%, 5%, 0.5) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.08) 0px 15px 35px -5px',
    }),
}));

const ChangePasswordContainer = styled(Stack)(({ theme }) => ({
    height: 'calc((1 - var(--template-frame-height, 0)) * 100dvh)',
    minHeight: '100%',
    padding: theme.spacing(2),
    display: 'flex',
    alignItems: 'center',
    [theme.breakpoints.up('sm')]: {
        padding: theme.spacing(4),
    },
    '&::before': {
        content: '""',
        display: 'block',
        position: 'absolute',
        zIndex: -1,
        inset: 0,
        backgroundImage:
            'radial-gradient(ellipse at 50% 50%, hsl(210, 100%, 97%), hsl(0, 0%, 100%))',
        backgroundRepeat: 'no-repeat',
        ...theme.applyStyles('dark', {
            backgroundImage:
                'radial-gradient(at 50% 50%, hsla(210, 100%, 16%, 0.5), hsl(220, 30%, 5%))',
        }),
    },
}));

const ChangePasswordPage: React.FC = () => {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [showNewPassword, setShowNewPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [showOldPassword, setShowOldPassword] = useState(false);
    const [error, setError] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const { setUser, user, logout } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();

        if (!user) {
            setError('User information not available');
            return;
        }

        if (!oldPassword) {
            setError('Please enter your current password');
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
            const userInfo = await authApi.updatePassword(user.user_id, oldPassword, newPassword);
            setUser(userInfo);
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

    const handleClickShowOldPassword = () => {
        setShowOldPassword(!showOldPassword);
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
                <CssBaseline enableColorScheme />
                <Box
                    sx={{
                        minHeight: '100vh',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                    }}
                >
                    <CircularProgress />
                </Box>
            </AppTheme>
        );
    }

    return (
        <AppTheme>
            <CssBaseline enableColorScheme />
            <ChangePasswordContainer direction="column" justifyContent="space-between">
                <ColorSchemeToggle sx={{ position: 'fixed', top: '1rem', right: '1rem' }} />
                <StyledCard variant="outlined">
                    <Box textAlign="center" mb={2}>
                        <LockOutlined sx={{ fontSize: 48, color: 'primary.main', mb: 1 }} />
                        <Typography variant="h4" gutterBottom>
                            Change Password Required
                        </Typography>
                        <Typography variant="body1" color="text.secondary">
                            As an admin user, you need to change your password on first login
                        </Typography>

                        {/* Logout Button */}
                        <Button
                            variant="outlined"
                            color="primary"
                            startIcon={<LogoutRounded />}
                            onClick={handleLogout}
                            sx={{ mt: 2 }}
                        >
                            Logout
                        </Button>
                    </Box>

                    <form onSubmit={handleSubmit}>
                        {error && (
                            <Alert severity="error" sx={{ mb: 3 }}>
                                {error}
                            </Alert>
                        )}

                        <Stack spacing={2}>
                            <Box>
                                <FormLabel htmlFor="current-password" required>Current Password</FormLabel>
                                <TextField
                                    id="current-password"
                                    fullWidth
                                    type={showOldPassword ? 'text' : 'password'}
                                    value={oldPassword}
                                    onChange={(e) => setOldPassword(e.target.value)}
                                    required
                                    disabled={isSubmitting}
                                    variant="outlined"
                                    InputProps={{
                                        endAdornment: (
                                            <InputAdornment position="end">
                                                <IconButton
                                                    onClick={handleClickShowOldPassword}
                                                    edge="end"
                                                    disabled={isSubmitting}
                                                >
                                                    {showOldPassword ? <VisibilityOff /> : <Visibility />}
                                                </IconButton>
                                            </InputAdornment>
                                        ),
                                    }}
                                />
                            </Box>
                            <Box>
                                <FormLabel htmlFor="new-password" required>New Password</FormLabel>
                                <TextField
                                    id="new-password"
                                    fullWidth
                                    type={showNewPassword ? 'text' : 'password'}
                                    value={newPassword}
                                    onChange={(e) => setNewPassword(e.target.value)}
                                    required
                                    disabled={isSubmitting}
                                    variant="outlined"
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
                            </Box>
                            <Box>
                                <FormLabel htmlFor="confirm-password" required>Confirm New Password</FormLabel>
                                <TextField
                                    id="confirm-password"
                                    fullWidth
                                    type={showConfirmPassword ? 'text' : 'password'}
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    required
                                    disabled={isSubmitting}
                                    variant="outlined"
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
                            </Box>

                            <Button
                                type="submit"
                                variant="contained"
                                size="large"
                                fullWidth
                                disabled={isSubmitting || !newPassword || !confirmPassword}
                                startIcon={isSubmitting ? <CircularProgress size={20} /> : null}
                            >
                                {isSubmitting ? 'Updating Password...' : 'Update Password'}
                            </Button>
                        </Stack>
                    </form>
                </StyledCard>
            </ChangePasswordContainer>
        </AppTheme>
    );
};

export default ChangePasswordPage; 