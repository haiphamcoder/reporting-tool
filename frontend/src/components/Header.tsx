import Stack from '@mui/material/Stack';
import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import MenuButton from './MenuButton';
import ColorSchemeToggle from '../theme/ColorSchemeToggle';

import Search from './Search';
import { Avatar, Box, Typography } from '@mui/material';
import OptionsMenu from './OptionsMenu';
import { useState, useEffect } from 'react';
import { API_CONFIG } from '../config/api';

interface UserData {
  first_name: string;
  last_name: string;
  email: string;
  avatar_url: string;
};

export default function Header() {
  const [userData, setUserData] = useState<UserData | null>(null);

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const response = await fetch(`${API_CONFIG.BASE_URL}${API_CONFIG.ENDPOINTS.USER}`, {
          method: 'GET', credentials: 'include'
        });
        if (response.ok) {
          const data = await response.json();
          if (data.data) {
            setUserData(data.data);
          }
        }
      } catch (error) {
        console.error('Error fetching user data', error);
      }
    };

    fetchUserData();
  }, []);

  return (
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
        <Search />
        {/* <CustomDatePicker /> */}
        <MenuButton showBadge aria-label="Open notifications">
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
            src={userData? userData.avatar_url : ''}
            sx={{ width: 36, height: 36 }}
          />
          <Box sx={{ mr: 'auto' }}>
            <Typography variant="body2" sx={{ fontWeight: 500, lineHeight: '16px' }}>
              {userData? `${userData.first_name} ${userData.last_name}` : 'User'}
            </Typography>
            <Typography variant="caption" sx={{ color: 'text.secondary' }}>
              {userData? userData.email : 'user@example.com'}
            </Typography>
          </Box>
          <OptionsMenu />
        </Stack>
      </Stack>
    </Stack>
  );
}
