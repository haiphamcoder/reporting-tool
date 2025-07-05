import * as React from 'react';
import { useState } from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import OutlinedInput from '@mui/material/OutlinedInput';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import Alert from '@mui/material/Alert';
import CircularProgress from '@mui/material/CircularProgress';
import { authApi } from '../api/auth/authApi';

interface ForgotPasswordProps {
    open: boolean;
    handleClose: () => void;
}

type Step = 'email' | 'otp' | 'newPassword' | 'success';

export default function ForgotPasswordDialog({ open, handleClose }: ForgotPasswordProps) {
    const [step, setStep] = useState<Step>('email');
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const resetState = () => {
        setStep('email');
        setEmail('');
        setOtp('');
        setNewPassword('');
        setConfirmPassword('');
        setLoading(false);
        setError('');
        setSuccess('');
    };

    const handleCloseDialog = () => {
        resetState();
        handleClose();
    };

    const handleEmailSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!email) {
            setError('Please enter your email address');
            return;
        }

        setLoading(true);
        setError('');

        try {
            // Step 1: Check provider
            const providerResponse = await authApi.checkProvider(email);
            
            if (providerResponse.result.provider === 'local') {
                // Step 2: Send forgot password email
                await authApi.forgotPassword(email);
                setStep('otp');
                setSuccess('OTP has been sent to your email address');
            } else {
                setError('This email is associated with an external provider. Please use the original sign-in method.');
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'An error occurred');
        } finally {
            setLoading(false);
        }
    };

    const handleOtpSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!otp) {
            setError('Please enter the OTP');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const verifyResponse = await authApi.verifyOtp(email, otp);
            
            if (verifyResponse.success) {
                setStep('newPassword');
                setSuccess('OTP verified successfully. Please enter your new password.');
            } else {
                setError('OTP verification failed or user-id cookie not found');
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to verify OTP');
        } finally {
            setLoading(false);
        }
    };

    const handlePasswordSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!newPassword || !confirmPassword) {
            setError('Please enter both password fields');
            return;
        }

        if (newPassword !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (newPassword.length < 6) {
            setError('Password must be at least 6 characters long');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const resetResponse = await authApi.resetPassword(email, newPassword);
            
            if (resetResponse.success) {
                setStep('success');
                setSuccess('Password reset successfully! You can now sign in with your new password.');
            } else {
                setError('Failed to reset password');
            }
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to reset password');
        } finally {
            setLoading(false);
        }
    };

    const renderEmailStep = () => (
        <form onSubmit={handleEmailSubmit}>
            <DialogContentText>
                Enter your account&apos;s email address, and we&apos;ll send you a link to
                reset your password.
            </DialogContentText>
            <FormControl fullWidth margin="dense">
                <InputLabel htmlFor="email">Email address</InputLabel>
                <OutlinedInput
                    autoFocus
                    required
                    id="email"
                    name="email"
                    label="Email address"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    disabled={loading}
                />
            </FormControl>
            <DialogActions sx={{ pb: 3, px: 3 }}>
                <Button onClick={handleCloseDialog} variant="outlined" sx={{ width: 100 }} disabled={loading}>
                    Cancel
                </Button>
                <Button 
                    variant="contained" 
                    type="submit" 
                    sx={{ width: 100 }}
                    disabled={loading}
                    startIcon={loading ? <CircularProgress size={20} /> : null}
                >
                    Continue
                </Button>
            </DialogActions>
        </form>
    );

    const renderOtpStep = () => (
        <form onSubmit={handleOtpSubmit}>
            <DialogContentText>
                Enter the OTP sent to your email address.
            </DialogContentText>
            <FormControl fullWidth margin="dense">
                <InputLabel htmlFor="otp">OTP</InputLabel>
                <OutlinedInput
                    autoFocus
                    required
                    id="otp"
                    name="otp"
                    label="OTP"
                    type="text"
                    value={otp}
                    onChange={(e) => setOtp(e.target.value)}
                    disabled={loading}
                    inputProps={{ maxLength: 6 }}
                />
            </FormControl>
            <DialogActions sx={{ pb: 3, px: 3 }}>
                <Button onClick={handleCloseDialog} variant="outlined" sx={{ width: 100 }} disabled={loading}>
                    Cancel
                </Button>
                <Button 
                    variant="contained" 
                    type="submit" 
                    sx={{ width: 100 }}
                    disabled={loading}
                    startIcon={loading ? <CircularProgress size={20} /> : null}
                >
                    Verify
                </Button>
            </DialogActions>
        </form>
    );

    const renderNewPasswordStep = () => (
        <form onSubmit={handlePasswordSubmit}>
            <DialogContentText>
                Enter your new password.
            </DialogContentText>
            <FormControl fullWidth margin="dense">
                <InputLabel htmlFor="newPassword">New Password</InputLabel>
                <OutlinedInput
                    autoFocus
                    required
                    id="newPassword"
                    name="newPassword"
                    label="New Password"
                    type="password"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                    disabled={loading}
                />
            </FormControl>
            <FormControl fullWidth margin="dense">
                <InputLabel htmlFor="confirmPassword">Confirm Password</InputLabel>
                <OutlinedInput
                    required
                    id="confirmPassword"
                    name="confirmPassword"
                    label="Confirm Password"
                    type="password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    disabled={loading}
                />
            </FormControl>
            <DialogActions sx={{ pb: 3, px: 3 }}>
                <Button onClick={handleCloseDialog} variant="outlined" sx={{ width: 100 }} disabled={loading}>
                    Cancel
                </Button>
                <Button 
                    variant="contained" 
                    type="submit" 
                    sx={{ width: 100 }}
                    disabled={loading}
                    startIcon={loading ? <CircularProgress size={20} /> : null}
                >
                    Reset Password
                </Button>
            </DialogActions>
        </form>
    );

    const renderSuccessStep = () => (
        <>
            <DialogContentText>
                {success}
            </DialogContentText>
            <DialogActions sx={{ pb: 3, px: 3 }}>
                <Button onClick={handleCloseDialog} variant="contained" sx={{ width: 100 }}>
                    Close
                </Button>
            </DialogActions>
        </>
    );

    const getStepTitle = () => {
        switch (step) {
            case 'email':
                return 'Reset password';
            case 'otp':
                return 'Enter OTP';
            case 'newPassword':
                return 'Set new password';
            case 'success':
                return 'Success';
            default:
                return 'Reset password';
        }
    };

    const renderStepContent = () => {
        switch (step) {
            case 'email':
                return renderEmailStep();
            case 'otp':
                return renderOtpStep();
            case 'newPassword':
                return renderNewPasswordStep();
            case 'success':
                return renderSuccessStep();
            default:
                return renderEmailStep();
        }
    };

    return (
        <Dialog
            open={open}
            onClose={handleCloseDialog}
            slotProps={{
                paper: {
                    sx: { backgroundImage: 'none' },
                },
            }}
        >
            <DialogTitle>{getStepTitle()}</DialogTitle>
            <DialogContent
                sx={{ display: 'flex', flexDirection: 'column', gap: 2, width: '100%' }}
            >
                {error && (
                    <Alert severity="error" onClose={() => setError('')}>
                        {error}
                    </Alert>
                )}
                {success && (
                    <Alert severity="success" onClose={() => setSuccess('')}>
                        {success}
                    </Alert>
                )}
                {renderStepContent()}
            </DialogContent>
        </Dialog>
    );
}
