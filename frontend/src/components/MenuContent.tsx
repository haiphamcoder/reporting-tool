import React from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import Divider from '@mui/material/Divider';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import SourceRoundedIcon from '@mui/icons-material/SourceRounded';
import BarChartRoundedIcon from '@mui/icons-material/BarChartRounded';
import AssignmentRoundedIcon from '@mui/icons-material/AssignmentRounded';
import PeopleRoundedIcon from '@mui/icons-material/PeopleRounded';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

type MenuItem = {
  text: string;
  icon: React.JSX.Element;
  type: 'home' | 'sources' | 'charts' | 'reports' | 'users';
};

const menuItems: MenuItem[] = [
  { text: 'Home', icon: <HomeRoundedIcon />, type: 'home' },
  { text: 'Sources', icon: <SourceRoundedIcon />, type: 'sources' },
  { text: 'Charts', icon: <BarChartRoundedIcon />, type: 'charts' },
  { text: 'Reports', icon: <AssignmentRoundedIcon />, type: 'reports' },
];

const adminMenuItems: MenuItem[] = [
  { text: 'Users', icon: <PeopleRoundedIcon />, type: 'users' },
];

interface MenuContentProps {
  collapsed: boolean;
}

export default function MenuContent({ collapsed }: MenuContentProps) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();
  const isAdmin = user?.role === 'admin';

  // Determine current content based on pathname
  let currentContent: string = 'home';
  if (location.pathname.startsWith('/dashboard/sources')) {
    currentContent = 'sources';
  } else if (location.pathname.startsWith('/dashboard/charts')) {
    currentContent = 'charts';
  } else if (location.pathname.startsWith('/dashboard/reports')) {
    currentContent = 'reports';
  } else if (location.pathname.startsWith('/dashboard/users')) {
    currentContent = 'users';
  } else if (location.pathname.startsWith('/dashboard/settings')) {
    currentContent = 'settings';
  } else if (location.pathname.startsWith('/dashboard/home')) {
    currentContent = 'home';
  }

  const handleNavigation = (contentType: string) => {
    console.log('MenuContent - Navigating to:', contentType);
    navigate(`/dashboard/${contentType}`);
  };

  const renderMenuItem = (item: MenuItem, index: number) => (
    <ListItem key={index} disablePadding sx={{ display: 'block', justifyContent: 'center', my: collapsed ? 0.5 : 0 }}>
      {collapsed ? (
        <Tooltip title={item.text} placement="right">
          <IconButton
            onClick={() => handleNavigation(item.type)}
            sx={{
              width: 40,
              height: 40,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              borderRadius: 2,
              backgroundColor: currentContent === item.type ? '#f5f5f5' : 'transparent',
              color: currentContent === item.type ? 'primary.main' : 'text.primary',
              boxShadow: currentContent === item.type ? 1 : 0,
              transition: 'all 0.2s',
              '&:hover': {
                backgroundColor: currentContent === item.type ? '#ffffff !important' : '#eeeeee !important',
              },
            }}
          >
            {React.cloneElement(item.icon, {
              fontSize: 'inherit',
              sx: {
                fontSize: collapsed ? 40 : undefined,
                fontWeight: currentContent === item.type ? 700 : undefined,
              },
            })}
          </IconButton>
        </Tooltip>
      ) : (
        <ListItemButton
          selected={currentContent === item.type}
          onClick={() => handleNavigation(item.type)}
        >
          <ListItemIcon>{item.icon}</ListItemIcon>
          <ListItemText primary={item.text} />
        </ListItemButton>
      )}
    </ListItem>
  );

  return (
    <Stack spacing={1} sx={{ width: '100%' }}>
      <List>
        {menuItems.map((item, index) => renderMenuItem(item, index))}
      </List>

      {isAdmin && (
        <>
          <Divider sx={{ my: 1 }} />
          <List>
            {adminMenuItems.map((item, index) => renderMenuItem(item, index))}
          </List>
        </>
      )}
    </Stack>
  );
}
