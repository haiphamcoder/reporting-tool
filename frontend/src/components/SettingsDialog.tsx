import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import Box from '@mui/material/Box';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import FormGroup from '@mui/material/FormGroup';
import FormControlLabel from '@mui/material/FormControlLabel';
import Switch from '@mui/material/Switch';
import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';

interface SettingsDialogProps {
  open: boolean;
  onClose: () => void;
}

const MENU = [
  { key: 'notification', label: 'Notifications' },
  { key: 'security', label: 'Security' },
];

export default function SettingsDialog({ open, onClose }: SettingsDialogProps) {
  const [selectedMenu, setSelectedMenu] = React.useState('notification');
  // Notification state
  const [emailNotif, setEmailNotif] = React.useState(false);
  const [browserNotif, setBrowserNotif] = React.useState(false);
  // Security state
  const [passcode, setPasscode] = React.useState('');
  const [confirmPasscode, setConfirmPasscode] = React.useState('');

  const renderActions = (saveDisabled: boolean = false) => (
    <Box display="flex" gap={2} mt={4}>
      <Button
        variant="outlined"
        color="primary"
        onClick={onClose}
        sx={{ flex: 1, minWidth: 0 }}
      >
        Close
      </Button>
      <Button
        variant="contained"
        color="primary"
        disabled={saveDisabled}
        sx={{ flex: 1, minWidth: 0 }}
      >
        Save
      </Button>
    </Box>
  );

  const renderContent = () => {
    if (selectedMenu === 'notification') {
      return (
        <Box mt={1} px={1} display="flex" flexDirection="column" height="100%">
          <FormGroup>
            <FormControlLabel
              control={
                <Switch
                  checked={emailNotif}
                  onChange={(_, checked) => setEmailNotif(checked)}
                />
              }
              label="Receive notifications via Email"
            />
            <FormControlLabel
              control={
                <Switch
                  checked={browserNotif}
                  onChange={(_, checked) => setBrowserNotif(checked)}
                />
              }
              label="Receive notifications in Browser"
            />
          </FormGroup>
          {renderActions()}
        </Box>
      );
    }
    if (selectedMenu === 'security') {
      return (
        <Box mt={1} px={1} display="flex" flexDirection="column" height="100%">
          <TextField
            label="New passcode"
            type="password"
            value={passcode}
            onChange={e => setPasscode(e.target.value)}
            fullWidth
            autoComplete="new-password"
            sx={{ mb: 2 }}
          />
          <TextField
            label="Confirm passcode"
            type="password"
            value={confirmPasscode}
            onChange={e => setConfirmPasscode(e.target.value)}
            fullWidth
            autoComplete="new-password"
          />
          {renderActions(!passcode || passcode !== confirmPasscode)}
        </Box>
      );
    }
    return null;
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle sx={{ textAlign: 'center', fontWeight: 600, fontSize: 22, pb: 1 }}>
        Settings
      </DialogTitle>
      <DialogContent sx={{ pt: 1, pb: 2 }}>
        <Box display="flex" minHeight={250}>
          <Box minWidth={170} borderRight={1} borderColor="divider" pr={0} py={1}>
            <List sx={{ p: 0 }}>
              {MENU.map(item => (
                <ListItem key={item.key} disablePadding>
                  <ListItemButton
                    selected={selectedMenu === item.key}
                    onClick={() => setSelectedMenu(item.key)}
                    sx={{ borderRadius: 2, mx: 1, my: 0.5 }}
                  >
                    <ListItemText primary={item.label} primaryTypographyProps={{ fontWeight: selectedMenu === item.key ? 600 : 400 }} />
                  </ListItemButton>
                </ListItem>
              ))}
            </List>
          </Box>
          <Box flex={1} pl={4} pr={2} py={1} display="flex" flexDirection="column" justifyContent="space-between" minHeight={250}>
            {renderContent()}
          </Box>
        </Box>
      </DialogContent>
    </Dialog>
  );
} 