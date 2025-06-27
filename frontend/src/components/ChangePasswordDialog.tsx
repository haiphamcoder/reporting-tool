import React, { useState, useMemo } from 'react';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import Stack from '@mui/material/Stack';
import Alert from '@mui/material/Alert';
import CircularProgress from '@mui/material/CircularProgress';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import LinearProgress from '@mui/material/LinearProgress';
import Box from '@mui/material/Box';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/auth/authApi';

interface ChangePasswordDialogProps {
  open: boolean;
  onClose: () => void;
  onCancel: () => void;
}

// Password strength checker
const getPasswordStrength = (password: string) => {
  if (!password) return { score: 0, label: '', color: 'error' };
  
  let score = 0;
  const checks = {
    length: password.length >= 8,
    lowercase: /[a-z]/.test(password),
    uppercase: /[A-Z]/.test(password),
    numbers: /\d/.test(password),
    symbols: /[!@#$%^&*(),.?":{}|<>]/.test(password),
  };

  score += checks.length ? 1 : 0;
  score += checks.lowercase ? 1 : 0;
  score += checks.uppercase ? 1 : 0;
  score += checks.numbers ? 1 : 0;
  score += checks.symbols ? 1 : 0;

  // Bonus for length
  if (password.length >= 12) score += 1;

  const percentage = (score / 6) * 100;
  
  let label = '';
  let color: 'error' | 'warning' | 'info' | 'success' = 'error';
  
  if (percentage <= 20) {
    label = 'Very Weak';
    color = 'error';
  } else if (percentage <= 40) {
    label = 'Weak';
    color = 'error';
  } else if (percentage <= 60) {
    label = 'Fair';
    color = 'warning';
  } else if (percentage <= 80) {
    label = 'Good';
    color = 'info';
  } else {
    label = 'Strong';
    color = 'success';
  }

  return { score, label, color, percentage };
};

const ChangePasswordDialog: React.FC<ChangePasswordDialogProps> = ({ open, onClose, onCancel }) => {
  const { user } = useAuth();
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);

  // Password strength
  const passwordStrength = useMemo(() => getPasswordStrength(newPassword), [newPassword]);

  // Validation logic
  const isFormValid = useMemo(() => {
    const hasOldPassword = oldPassword.trim().length > 0;
    const hasNewPassword = newPassword.trim().length >= 6;
    const passwordsMatch = newPassword === confirmPassword;
    const isDifferent = oldPassword !== newPassword;
    
    return hasOldPassword && hasNewPassword && passwordsMatch && isDifferent;
  }, [oldPassword, newPassword, confirmPassword]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!isFormValid) {
      setError('Please fill in all fields correctly');
      return;
    }

    setLoading(true);
    setError('');

    try {
      if (!user?.user_id) {
        throw new Error('User information not available');
      }

      await authApi.updatePassword(user.user_id, oldPassword, newPassword);
      setSuccess(true);
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
      
      // Close dialog after 2 seconds
      setTimeout(() => {
        onClose();
        setSuccess(false);
      }, 2000);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update password');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    if (!loading) {
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
      setError('');
      setSuccess(false);
      onClose();
    }
  };

  const handleCancel = () => {
    if (!loading) {
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
      setError('');
      setSuccess(false);
      onCancel();
    }
  };

  if (!open) return null;

  return (
    <>
      <DialogTitle>
        <Stack direction="row" alignItems="center" spacing={1}>
          <Button
            onClick={handleClose}
            disabled={loading}
            startIcon={<ArrowBackIcon />}
            sx={{ minWidth: 'auto', p: 0.5, position: 'absolute', left: 16 }}
          >
            Back
          </Button>
          <Typography variant="h6" sx={{ flex: 1, textAlign: 'center' }}>
            Change Password
          </Typography>
        </Stack>
      </DialogTitle>
      <form onSubmit={handleSubmit}>
        <DialogContent dividers>
          <Stack spacing={3}>
            {success && (
              <Alert severity="success">
                Password updated successfully!
              </Alert>
            )}
            
            {error && (
              <Alert severity="error">
                {error}
              </Alert>
            )}

            <Typography variant="body2" color="text.secondary">
              Please enter your current password and choose a new password.
            </Typography>

            <Stack spacing={1}>
              <Typography variant="body2" fontWeight="medium">
                Current Password
              </Typography>
              <TextField
                type="password"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
                fullWidth
                required
                disabled={loading}
                autoComplete="current-password"
                placeholder="Enter your current password"
              />
            </Stack>

            <Stack spacing={1}>
              <Typography variant="body2" fontWeight="medium">
                New Password
              </Typography>
              <TextField
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                fullWidth
                required
                disabled={loading}
                autoComplete="new-password"
                placeholder="Enter new password (min 6 characters)"
              />
              
              {/* Password Strength Indicator */}
              {newPassword && (
                <Stack spacing={1}>
                  <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="caption" color="text.secondary">
                      Password Strength
                    </Typography>
                    <Typography 
                      variant="caption" 
                      color={`${passwordStrength.color}.main`}
                      fontWeight="medium"
                    >
                      {passwordStrength.label}
                    </Typography>
                  </Box>
                  <LinearProgress
                    variant="determinate"
                    value={passwordStrength.percentage}
                    sx={{ 
                      height: 6, 
                      borderRadius: 3,
                      '& .MuiLinearProgress-bar': {
                        backgroundColor: `${passwordStrength.color}.main`
                      }
                    }}
                  />
                  <Typography variant="caption" color="text.secondary">
                    Include uppercase, lowercase, numbers, and symbols for a stronger password
                  </Typography>
                </Stack>
              )}
            </Stack>

            <Stack spacing={1}>
              <Typography variant="body2" fontWeight="medium">
                Confirm New Password
              </Typography>
              <TextField
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                fullWidth
                required
                disabled={loading}
                autoComplete="new-password"
                placeholder="Confirm your new password"
                error={confirmPassword.length > 0 && newPassword !== confirmPassword}
                helperText={confirmPassword.length > 0 && newPassword !== confirmPassword ? "Passwords do not match" : ""}
              />
            </Stack>
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button 
            onClick={handleCancel} 
            disabled={loading}
            variant="outlined"
            fullWidth
            sx={{ mr: 1 }}
          >
            Cancel
          </Button>
          <Button 
            type="submit" 
            variant="contained" 
            color="primary" 
            fullWidth
            disabled={loading || !isFormValid}
            startIcon={loading ? <CircularProgress size={20} /> : null}
          >
            {loading ? 'Updating...' : 'Update Password'}
          </Button>
        </DialogActions>
      </form>
    </>
  );
};

export default ChangePasswordDialog; 