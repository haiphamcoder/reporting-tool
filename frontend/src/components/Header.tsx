import Stack from '@mui/material/Stack';
import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import MenuButton from './MenuButton';
import ColorSchemeToggle from '../theme/ColorSchemeToggle';

import { Avatar, Box, Typography } from '@mui/material';
import OptionsMenu from './OptionsMenu';
import { useState, useEffect } from 'react';
import { authApi, UserInfo } from '../api/auth/authApi';
import NotificationDialog from './NotificationDialog';
import { useNotifications } from '../context/NotificationContext';

export default function Header() {
  const [userData, setUserData] = useState<UserInfo | null>(null);
  const [notificationDialogOpen, setNotificationDialogOpen] = useState(false);
  const { unreadCount } = useNotifications();

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const user = await authApi.getCurrentUser();
        setUserData(user);
      } catch (error) {
        console.error('Error fetching user data', error);
      }
    };

    fetchUserData();
  }, []);

  const handleNotificationClick = () => {
    setNotificationDialogOpen(true);
  };

  const handleNotificationClose = () => {
    setNotificationDialogOpen(false);
  };

  return (
    <>
      <Stack
        direction="row"
        sx={{
          display: { xs: 'none', md: 'flex' },
          width: '100%',
          alignItems: { xs: 'flex-start', md: 'center' },
          justifyContent: 'flex-end',
          maxWidth: { sm: '100%', md: '1700px' },
          pt: 1.5,
        }}
        spacing={2}
      >
        <Stack direction="row" sx={{ gap: 1, alignItems: 'center' }}>
          {/* <Search /> */}
          {/* <CustomDatePicker /> */}
          <MenuButton 
            showBadge={unreadCount > 0}
            aria-label="Open notifications"
            onClick={handleNotificationClick}
          >
            <NotificationsRoundedIcon />
          </MenuButton>
          <ColorSchemeToggle />
          <Stack
            direction="row"
            sx={{
              gap: 1,
              alignItems: 'center',
            }}
          >
            <Avatar
              sizes="small"
              alt="Riley Carter"
              src={userData ? userData.avatar_url : ''}
              sx={{ width: 36, height: 36 }}
            />
            <Box sx={{ mr: 'auto' }}>
              <Typography variant="body2" sx={{ fontWeight: 500, lineHeight: '16px' }}>
                {userData ? `${userData.first_name} ${userData.last_name}` : 'User'}
              </Typography>
              <Typography variant="caption" sx={{ color: 'text.secondary' }}>
                {userData ? userData.email : 'user@example.com'}
              </Typography>
            </Box>
            <OptionsMenu />
          </Stack>
        </Stack>
      </Stack>

      <NotificationDialog 
        open={notificationDialogOpen}
        onClose={handleNotificationClose}
      />
    </>
  );
}
