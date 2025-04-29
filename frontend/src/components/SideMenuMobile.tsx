import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Divider from '@mui/material/Divider';
import Drawer, { drawerClasses } from '@mui/material/Drawer';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import LogoutRoundedIcon from '@mui/icons-material/LogoutRounded';
import NotificationsRoundedIcon from '@mui/icons-material/NotificationsRounded';
import MenuButton from './MenuButton';
import MenuContent from './MenuContent';
import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { API_CONFIG } from '../config/api';
import { useAuth } from '../context/AuthContext';

interface UserData {
  first_name: string;
  last_name: string;
  email: string;
  avatar_url: string;
};

interface SideMenuMobileProps {
  open: boolean | undefined;
  toggleDrawer: (newOpen: boolean) => () => void;
}

export default function SideMenuMobile({ open, toggleDrawer }: SideMenuMobileProps) {
  const navigate = useNavigate();

  const [userData, setUserData] = useState<UserData | null>(null);

  const { logout } = useAuth();

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

  const handleLogout = () => {
    logout();
    navigate('/auth/signin');
  };

  return (
    <Drawer
      anchor="right"
      open={open}
      onClose={toggleDrawer(false)}
      sx={{
        zIndex: (theme) => theme.zIndex.drawer + 1,
        [`& .${drawerClasses.paper}`]: {
          backgroundImage: 'none',
          backgroundColor: 'background.paper',
        },
      }}
    >
      <Stack
        sx={{
          maxWidth: '70dvw',
          height: '100%',
        }}
      >
        <Stack direction="row" sx={{ p: 2, pb: 0, gap: 1 }}>
          <Stack
            direction="row"
            sx={{ gap: 1, alignItems: 'center', flexGrow: 1, p: 1 }}
          >
            <Avatar
              sizes="small"
              alt="Riley Carter"
              src={userData ? userData.avatar_url : ''}
              sx={{ width: 24, height: 24 }}
            />
            <Typography component="p" variant="h6">
              {userData ? `${userData.first_name} ${userData.last_name}` : 'User'}
            </Typography>
          </Stack>
          <MenuButton showBadge>
            <NotificationsRoundedIcon />
          </MenuButton>
        </Stack>
        <Divider />
        <Stack sx={{ flexGrow: 1 }}>
          <MenuContent />
          <Divider />
        </Stack>
        <Stack sx={{ p: 2 }}>
          <Button variant="outlined" fullWidth startIcon={<LogoutRoundedIcon />} onClick={handleLogout}>
            Logout
          </Button>
        </Stack>
      </Stack>
    </Drawer>
  );
}
