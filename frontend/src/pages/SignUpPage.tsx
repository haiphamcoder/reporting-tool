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
import CDPLogo from '../assets/logo.svg';
import MuiCard from '@mui/material/Card';
import { GoogleIcon } from '../components/CustomIcons';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth/authApi';
import { Dialog, DialogTitle, DialogContent, DialogActions } from '@mui/material';

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
        width: '550px',
    },
    ...theme.applyStyles('dark', {
        boxShadow:
            'hsla(220, 30%, 5%, 0.5) 0px 5px 15px 0px, hsla(220, 25%, 10%, 0.08) 0px 15px 35px -5px',
    }),
}));

const SignUpContainer = styled(Stack)(({ theme }) => ({
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

export default function SignUpPage(props: { disableCustomTheme?: boolean }) {
    const navigate = useNavigate();
    const [firstNameError, setFirstNameError] = React.useState(false);
    const [firstNameErrorMessage, setFirstNameErrorMessage] = React.useState('');
    const [lastNameError, setLastNameError] = React.useState(false);
    const [lastNameErrorMessage, setLastNameErrorMessage] = React.useState('');
    const [emailError, setEmailError] = React.useState(false);
    const [emailErrorMessage, setEmailErrorMessage] = React.useState('');
    const [usernameError, setUsernameError] = React.useState(false);
    const [usernameErrorMessage, setUsernameErrorMessage] = React.useState('');
    const [passwordError, setPasswordError] = React.useState(false);
    const [passwordErrorMessage, setPasswordErrorMessage] = React.useState('');
    const [passwordConfirmationError, setPasswordConfirmationError] = React.useState(false);
    const [passwordConfirmationErrorMessage, setPasswordConfirmationErrorMessage] = React.useState('');
    const [agreedToTerms, setAgreedToTerms] = React.useState(false);
    const [errorDialogOpen, setErrorDialogOpen] = React.useState(false);
    const [errorMessage, setErrorMessage] = React.useState('');

    const handleAgreeToTerms = (event: React.ChangeEvent<HTMLInputElement>) => {
        setAgreedToTerms(event.target.checked);
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (!validateInputs() || !agreedToTerms) {
            return;
        }

        const data = new FormData(event.currentTarget);
        const signUpData = {
            first_name: data.get('firstName') as string,
            last_name: data.get('lastName') as string,
            email: data.get('email') as string,
            username: data.get('username') as string,
            password: data.get('password') as string,
        };

        try {
            await authApi.signUp(signUpData);
            navigate('/auth/signin');
        } catch (error) {
            setErrorMessage(error instanceof Error ? error.message : 'Failed to sign up');
            setErrorDialogOpen(true);
        }
    };

    const validateInputs = () => {
        const firstName = document.getElementById('firstName') as HTMLInputElement;
        const lastName = document.getElementById('lastName') as HTMLInputElement;
        const email = document.getElementById('email') as HTMLInputElement;
        const username = document.getElementById('username') as HTMLInputElement;
        const password = document.getElementById('password') as HTMLInputElement;
        const passwordConfirmation = document.getElementById('passwordConfirmation') as HTMLInputElement;

        let isValid = true;

        if (!firstName.value || firstName.value.length < 3) {
            setFirstNameError(true);
            setFirstNameErrorMessage('Please enter a valid first name.');
            isValid = false;
        } else {
            setFirstNameError(false);
            setFirstNameErrorMessage('');
        }

        if (!lastName.value || lastName.value.length < 3) {
            setLastNameError(true);
            setLastNameErrorMessage('Please enter a valid last name.');
            isValid = false;
        } else {
            setLastNameError(false);
            setLastNameErrorMessage('');
        }

        if (!email.value || !/\S+@\S+\.\S+/.test(email.value)) {
            setEmailError(true);
            setEmailErrorMessage('Please enter a valid email address.');
            isValid = false;
        } else {
            setEmailError(false);
            setEmailErrorMessage('');
        }

        if (!username.value || username.value.length < 4) {
            setUsernameError(true);
            setUsernameErrorMessage('Please enter a valid username.');
            isValid = false;
        } else {
            setUsernameError(false);
            setUsernameErrorMessage('');
        }

        if (!password.value || password.value.length < 6) {
            setPasswordError(true);
            setPasswordErrorMessage('Password must be at least 6 characters long.');
            isValid = false;
        } else {
            setPasswordError(false);
            setPasswordErrorMessage('');
        }

        if (!passwordConfirmation.value || passwordConfirmation.value !== password.value) {
            setPasswordConfirmationError(true);
            setPasswordConfirmationErrorMessage('Passwords do not match.');
            isValid = false;
        } else {
            setPasswordConfirmationError(false);
            setPasswordConfirmationErrorMessage('');
        }

        if (!agreedToTerms) {
            setErrorMessage('Please agree to the terms and conditions.');
            setErrorDialogOpen(true);
            isValid = false;
        }

        return isValid;
    };

    return (
        <AppTheme {...props}>
            <CssBaseline enableColorScheme />
            <SignUpContainer direction="column" justifyContent="space-between">
                <ColorSchemeToggle sx={{ position: 'fixed', top: '1rem', right: '1rem' }} />
                <Card variant="outlined">
                    <img src={CDPLogo} height={25} width={150} alt="CDP Logo" />
                    <Typography
                        component="h1"
                        variant="h4"
                        sx={{ width: '100%', fontSize: 'clamp(2rem, 10vw, 2.15rem)' }}
                    >
                        Sign up
                    </Typography>
                    <Box
                        component="form"
                        onSubmit={handleSubmit}
                        noValidate
                        sx={{
                            display: 'flex',
                            flexWrap: 'wrap',
                            width: '100%',
                            gap: 2,
                        }}
                    >
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)' } }}>
                            <FormControl fullWidth>
                                <FormLabel htmlFor="firstName">First Name</FormLabel>
                                <TextField
                                    error={firstNameError}
                                    helperText={firstNameErrorMessage}
                                    id="firstName"
                                    name="firstName"
                                    autoComplete="firstName"
                                    autoFocus
                                    required
                                    fullWidth
                                    variant="outlined"
                                    color={firstNameError ? 'error' : 'primary'}
                                />
                            </FormControl>
                        </Box>
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)' } }}>
                            <FormControl fullWidth>
                                <FormLabel htmlFor="lastName">Last Name</FormLabel>
                                <TextField
                                    error={lastNameError}
                                    helperText={lastNameErrorMessage}
                                    id="lastName"
                                    name="lastName"
                                    autoComplete="lastName"
                                    required
                                    fullWidth
                                    variant="outlined"
                                    color={lastNameError ? 'error' : 'primary'}
                                />
                            </FormControl>
                        </Box>
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)' } }}>
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
                                    required
                                    fullWidth
                                    variant="outlined"
                                    color={usernameError ? 'error' : 'primary'}
                                />
                            </FormControl>
                        </Box>
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)' } }}>
                            <FormControl fullWidth>
                                <FormLabel htmlFor="email">Email</FormLabel>
                                <TextField
                                    required
                                    fullWidth
                                    id="email"
                                    placeholder="your@email.com"
                                    name="email"
                                    autoComplete="email"
                                    variant="outlined"
                                    error={emailError}
                                    helperText={emailErrorMessage}
                                    color={passwordError ? 'error' : 'primary'}
                                />
                            </FormControl>
                        </Box>
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)' } }}>
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
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 8px)' } }}>
                            <FormControl fullWidth>
                                <FormLabel htmlFor="passwordConfirmation">Confirm Password</FormLabel>
                                <TextField
                                    error={passwordConfirmationError}
                                    helperText={passwordConfirmationErrorMessage}
                                    name="passwordConfirmation"
                                    placeholder="••••••"
                                    type="password"
                                    id="passwordConfirmation"
                                    autoComplete="current-password"
                                    required
                                    fullWidth
                                    variant="outlined"
                                    color={passwordConfirmationError ? 'error' : 'primary'}
                                />
                            </FormControl>
                        </Box>
                        <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 100%' } }}>
                            <FormControlLabel
                                control={<Checkbox
                                    checked={agreedToTerms}
                                    onChange={handleAgreeToTerms}
                                    color='primary'
                                    name='termsAndConditions'
                                />}
                                label="I agree to the terms and conditions."
                            />
                        </Box>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            onClick={validateInputs}
                        >
                            Sign up
                        </Button>
                    </Box>
                    <Divider>or</Divider>
                    <Button
                        fullWidth
                        variant="outlined"
                        onClick={() => alert('Sign up with Google')}
                        startIcon={<GoogleIcon />}
                    >
                        Sign up with Google
                    </Button>
                    <Typography sx={{ textAlign: 'center' }}>
                        Already have an account?{' '}
                        <Link
                            href="/auth/signin"
                            variant="body2"
                            sx={{ alignSelf: 'center' }}
                        >
                            Sign in
                        </Link>
                    </Typography>
                </Card>
            </SignUpContainer>
            <Dialog
                open={errorDialogOpen}
                onClose={() => setErrorDialogOpen(false)}
            >
                <DialogTitle>Error</DialogTitle>
                <DialogContent>
                    <Typography id="error-dialog-description">
                        {errorMessage}
                    </Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setErrorDialogOpen(false)}>OK</Button>
                </DialogActions>
            </Dialog>
        </AppTheme>
    );
}
