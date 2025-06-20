import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Avatar from '@mui/material/Avatar';
import Stack from '@mui/material/Stack';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import Divider from '@mui/material/Divider';
import { useAuth } from '../context/AuthContext';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import Tooltip from '@mui/material/Tooltip';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import googlePng from '../assets/google.png';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';

interface AccountInfoDialogProps {
  open: boolean;
  onClose: () => void;
  onChangePassword?: () => void;
}

const AccountInfoDialog: React.FC<AccountInfoDialogProps> = ({ open, onClose, onChangePassword }) => {
  const { user } = useAuth();

  const fullName = user ? `${user.first_name || ''} ${user.last_name || ''}`.trim() : '';
  const avatarFallback = user?.username ? user.username[0].toUpperCase() : '?';

  function getProviderIcon(provider: string | undefined) {
    if (!provider) return <AccountCircleIcon sx={{ mr: 1 }} fontSize="small" color="disabled" />;
    const p = provider.toLowerCase();
    if (p === 'google') return <img src={googlePng} alt="Google" style={{ width: 20, height: 20, marginRight: 6, verticalAlign: 'middle' }} />;
    // Có thể mở rộng thêm các provider khác ở đây
    return <AccountCircleIcon sx={{ mr: 1 }} fontSize="small" color="disabled" />;
  }

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle align="center">Account Information</DialogTitle>
      <DialogContent dividers>
        {user ? (
          <Stack spacing={2} alignItems="center" mb={2}>
            <Avatar
              src={user.avatar_url || undefined}
              sx={{ width: 72, height: 72, fontSize: 32 }}
            >
              {(!user.avatar_url && avatarFallback) || '?'}
            </Avatar>
            <Typography variant="h6">{fullName || 'No full name'}</Typography>
            <Typography variant="subtitle2" color="text.secondary">
            {user.username}
            </Typography>
            {getProviderIcon(user.provider)}
          </Stack>
        ) : null}
        <Divider sx={{ mb: 2 }} />
        {user ? (
          <Grid container spacing={2}>
            <Grid item xs={12} md={6}>
              <List dense>
                <ListItem>
                  <ListItemText
                    primary="Email"
                    secondary={
                      <Box display="flex" alignItems="center">
                        <span>{user.email || '-'}</span>
                        {typeof user.email_verified === 'boolean' && (
                          <Tooltip title={user.email_verified ? 'Email verified' : 'Email not verified'}>
                            {user.email_verified ? (
                              <CheckCircleIcon sx={{ ml: 1.5, fontSize: 18, color: '#43a047 !important' }} />
                            ) : (
                              <CancelIcon sx={{ ml: 1.5, fontSize: 18, color: '#e53935 !important' }} />
                            )}
                          </Tooltip>
                        )}
                      </Box>
                    }
                  />
                </ListItem>
                <ListItem>
                  <ListItemText primary="User ID" secondary={user.user_id || '-'} />
                </ListItem>
                <ListItem>
                  <ListItemText primary="Role" secondary={user.role || '-'} />
                </ListItem>
              </List>
            </Grid>
            <Grid item xs={12} md={6}>
              <List dense>
                <ListItem>
                  <ListItemText primary="First Name" secondary={user.first_name || '-'} />
                </ListItem>
                <ListItem>
                  <ListItemText primary="Last Name" secondary={user.last_name || '-'} />
                </ListItem>
                <ListItem>
                  <ListItemText primary="Username" secondary={user.username || '-'} />
                </ListItem>
                <ListItem>
                  <ListItemText primary="Provider" secondary={user.provider || '-'} />
                </ListItem>
              </List>
            </Grid>
          </Grid>
        ) : (
          <Typography variant="body2">No user information available.</Typography>
        )}
      </DialogContent>
      <DialogActions>
        {user?.provider === 'local' && (
          <Button
            onClick={onChangePassword}
            variant="outlined"
            color="secondary"
            fullWidth
            sx={{ mr: 1 }}
          >
            Change Password
          </Button>
        )}
        <Button onClick={onClose} variant="contained" color="primary" fullWidth>Close</Button>
      </DialogActions>
    </Dialog>
  );
};

export default AccountInfoDialog; 