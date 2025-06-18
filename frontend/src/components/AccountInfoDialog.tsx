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

interface AccountInfoDialogProps {
  open: boolean;
  onClose: () => void;
}

const AccountInfoDialog: React.FC<AccountInfoDialogProps> = ({ open, onClose }) => {
  const { user } = useAuth();

  const fullName = user ? `${user.first_name || ''} ${user.last_name || ''}`.trim() : '';
  const avatarFallback = user?.username ? user.username[0].toUpperCase() : '?';

  return (
    <Dialog open={open} onClose={onClose} maxWidth="xs" fullWidth>
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
            <Typography variant="h6">{user.username}</Typography>
            <Typography variant="subtitle2" color="text.secondary">
              {fullName || 'No full name'}
            </Typography>
          </Stack>
        ) : null}
        <Divider sx={{ mb: 2 }} />
        {user ? (
          <List dense>
            <ListItem>
              <ListItemText primary="Email" secondary={user.email || '-'} />
            </ListItem>
            <ListItem>
              <ListItemText primary="User ID" secondary={user.id || '-'} />
            </ListItem>
          </List>
        ) : (
          <Typography variant="body2">No user information available.</Typography>
        )}
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant="contained" color="primary" fullWidth>Close</Button>
      </DialogActions>
    </Dialog>
  );
};

export default AccountInfoDialog; 