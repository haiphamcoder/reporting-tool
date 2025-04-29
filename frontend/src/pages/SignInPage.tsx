import * as React from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Checkbox from '@mui/material/Checkbox';
import CssBaseline from '@mui/material/CssBaseline';
import FormControlLabel from '@mui/material/FormControlLabel';
import Divider from '@mui/material/Divider';
import FormLabel from '@mui/material/FormLabel';
import FormControl from '@mui/material/FormControl';
import Link from '@mui/material/Link';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import { styled } from '@mui/material/styles';
import AppTheme from '../theme/AppTheme';
import ColorSchemeToggle from '../theme/ColorSchemeToggle';
import ForgotPasswordDialog from '../components/ForgotPasswordDialog';
import MuiCard from '@mui/material/Card';
import CDPLogo from '../assets/logo.svg';
import { useNavigate } from 'react-router-dom';
import { GoogleIcon } from '../components/CustomIcons';
import { API_CONFIG } from '../config/api';
import { useAuth } from '../context/AuthContext';
import { Dialog, DialogTitle, DialogContent, DialogActions } from "@mui/material";

const Card = styled(MuiCard)(({ theme }) => ({
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

const SignInContainer = styled(Stack)(({ theme }) => ({
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

export default function SignInPage(props: { disableCustomTheme?: boolean }) {
    const [usernameError, setUsernameError] = React.useState(false);
    const [usernameErrorMessage, setUsernameErrorMessage] = React.useState('');
    const [passwordError, setPasswordError] = React.useState(false);
    const [passwordErrorMessage, setPasswordErrorMessage] = React.useState('');
    const [forgotPasswordDialogOpen, setForgotPasswordDialogOpen] = React.useState(false);
    const [errorDialogOpen, setErrorDialogOpen] = React.useState(false);

    const { checkAuth } = useAuth();

    const handleForgotPasswordDialogOpen = () => {
        setForgotPasswordDialogOpen(true);
    };

    const handleForgotPasswordDialogClose = () => {
        setForgotPasswordDialogOpen(false);
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (usernameError || passwordError) {
            return;
        }

        const data = new FormData(event.currentTarget);
        const username = data.get('username');
        const password = data.get('password');
        
        try {
            const response = fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.AUTHENTICATE}`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    username: username,
                    password: password,
                }),
                credentials: 'include',
            });

            if ((await response).status === 200) {
                await checkAuth();
                navigate('/dashboard');
            } else {
                setErrorDialogOpen(true);
            }
        } catch (error) {
            console.error('Error signing in:', error);
            setErrorDialogOpen(true);
        }
    };

    const navigate = useNavigate();

    const validateInputs = () => {
        const username = document.getElementById('username') as HTMLInputElement;
        const password = document.getElementById('password') as HTMLInputElement;

        let isValid = true;

        if (!username.value || username.value.length === 0) {
            setUsernameError(true);
            setUsernameErrorMessage('Please enter a username.');
            isValid = false;
        } else {
            setUsernameError(false);
            setUsernameErrorMessage('');
        }

        if (!password.value || password.value.length === 0) {
            setPasswordError(true);
            setPasswordErrorMessage('Please enter a password.');
            isValid = false;
        } else {
            setPasswordError(false);
            setPasswordErrorMessage('');
        }

        return isValid;
    };

    const handleSignInWithGoogle = async () => {
        const redirectUrl = `${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.REDIRECT_LOGIN_GOOGLE}`;
        window.open(redirectUrl, '_self');
    }

    return (
        <AppTheme {...props}>
            <CssBaseline enableColorScheme />
            <SignInContainer direction="column" justifyContent="space-between">
                <ColorSchemeToggle sx={{ position: 'fixed', top: '1rem', right: '1rem' }} />
                <Card variant="outlined">
                    <img src={CDPLogo} height={25} width={150} alt="CDP Logo" />
                    <Typography
                        component="h1"
                        variant="h4"
                        sx={{ width: '100%', fontSize: 'clamp(2rem, 10vw, 2.15rem)' }}
                    >
                        Sign in
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        noValidate
                        sx={{
                            display: 'flex',
                            flexDirection: 'column',
                            width: '100%',
                            gap: 2,
                        }}
                    >
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 100%' } }}>
                            <FormControl fullWidth>
                                <FormLabel htmlFor="username">Username</FormLabel>
                                <TextField
                                    error={usernameError}
                                    helperText={usernameErrorMessage}
                                    id="username"
                                    type="username"
                                    name="username"
                                    placeholder="admin"
                                    autoComplete="current-username"
                                    autoFocus
                                    required
                                    fullWidth
                                    variant="outlined"
                                    color={usernameError ? 'error' : 'primary'}
                                />
                            </FormControl>
                        </Box>
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 100%' } }}>
                            <FormControl fullWidth>
                                <FormLabel htmlFor="password">Password</FormLabel>
                                <TextField
                                    error={passwordError}
                                    helperText={passwordErrorMessage}
                                    name="password"
                                    placeholder="••••••"
                                    type="password"
                                    id="password"
                                    autoComplete="current-password"
                                    required
                                    fullWidth
                                    variant="outlined"
                                    color={passwordError ? 'error' : 'primary'}
                                />
                            </FormControl>
                        </Box>
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 100%' } }}>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between' }}>
                                <FormControlLabel
                                    control={<Checkbox value="remember" color="primary" />}
                                    label="Remember me"
                                />
                                <Link
                                    component="button"
                                    type="button"
                                    onClick={handleForgotPasswordDialogOpen}
                                    variant="body2"
                                    sx={{ alignSelf: 'center' }}
                                >
                                    Forgot your password?
                                </Link>
                            </Box>
                        </Box>
                        <ForgotPasswordDialog open={forgotPasswordDialogOpen} handleClose={handleForgotPasswordDialogClose} />
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            onClick={validateInputs}
                        >
                            Sign in
                        </Button>
                    </Box>
                    <Divider>or</Divider>
                    <Button
                        fullWidth
                        variant="outlined"
                        onClick={() => handleSignInWithGoogle()}
                        startIcon={<GoogleIcon />}
                    >
                        Sign in with Google
                    </Button>
                    <Typography sx={{ textAlign: 'center' }}>
                        Don&apos;t have an account?{' '}
                        <Link
                            href="/auth/signup"
                            variant="body2"
                            sx={{ alignSelf: 'center' }}
                        >
                            Sign up
                        </Link>
                    </Typography>
                </Card>
            </SignInContainer>
            <Dialog
                open={errorDialogOpen}
                onClose={() => setErrorDialogOpen(false)}
            >
                <DialogTitle>Error</DialogTitle>
                <DialogContent>
                    <Typography id="error-dialog-description">
                        Username or password is incorrect. Please try again.
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setErrorDialogOpen(false)}>OK</Button>
                </DialogActions>
            </Dialog>
        </AppTheme>
    );
}
