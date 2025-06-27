import { useState } from 'react';
import { styled } from '@mui/material/styles';
import MuiDrawer, { drawerClasses } from '@mui/material/Drawer';
import Box from '@mui/material/Box';
import Divider from '@mui/material/Divider';
import MenuContent from './MenuContent';
import Logo from '../assets/logo.svg';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import Button from '@mui/material/Button';
import { useNavigate } from 'react-router-dom';

const drawerWidth = 240;
const collapsedWidth = 60;

const Drawer = styled(MuiDrawer)<{ collapsed: boolean }>(({ collapsed }) => ({
  width: collapsed ? collapsedWidth : drawerWidth,
  flexShrink: 0,
  boxSizing: 'border-box',
  mt: 10,
  [`& .${drawerClasses.paper}`]: {
    width: collapsed ? collapsedWidth : drawerWidth,
    boxSizing: 'border-box',
    transition: 'width 0.3s',
  },
}));

export default function SideMenu() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const handleLogoClick = () => {
    navigate('/');
  };

  return (
    <Drawer
      variant="permanent"
      collapsed={collapsed}
      sx={{
        display: { xs: 'none', md: 'block' },
        [`& .${drawerClasses.paper}`]: {
          backgroundColor: 'background.paper',
        },
      }}
    >
      <Box
        sx={{
          display: 'flex',
          mt: 'calc(var(--template-frame-height, 0px) + 4px)',
          py: 1.5,
          justifyContent: 'center',
        }}
      >
        {collapsed ? (
          <Box
            onClick={handleLogoClick}
            sx={{
              width: 40,
              height: 40,
              borderRadius: 1,
              backgroundColor: 'primary.main',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'primary.contrastText',
              fontWeight: 'bold',
              fontSize: '18px',
              boxShadow: 1,
              transition: 'all 0.3s',
              cursor: 'pointer',
              '&:hover': {
                boxShadow: 2,
                transform: 'scale(1.05)',
                backgroundColor: 'primary.dark',
              },
            }}
          >
            R
          </Box>
        ) : (
          <img
            src={Logo}
            height={25}
            width={150}
            alt="CDP Logo"
            style={{ transition: 'width 0.3s, height 0.3s' }}
            onClick={handleLogoClick}
            onKeyDown={(e) => e.key === 'Enter' && handleLogoClick()}
            role="button"
            tabIndex={0}
            className="cursor-pointer"
          />
        )}
      </Box>
      <Divider />
      <Box
        sx={{
          overflow: 'auto',
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        <MenuContent
          collapsed={collapsed}
        />
      </Box>
      <Box sx={{
        width: '100%',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        py: 1.5,
        borderTop: '1px solid',
        borderColor: 'divider',
        mt: 'auto',
      }}>
        {collapsed ? (
          <Button
            onClick={() => setCollapsed(false)}
            variant="text"
            sx={{
              minWidth: 0,
              width: 40,
              height: 40,
              p: 0,
              borderRadius: 1,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
            }}
          >
            <ChevronRightIcon sx={{ fontSize: 32 }} />
          </Button>
        ) : (
          <Button
            onClick={() => setCollapsed(true)}
            variant="text"
            fullWidth
            sx={{
              justifyContent: 'center',
              fontWeight: 600,
              fontSize: 16,
              borderRadius: 1,
              py: 1.5,
              textTransform: 'none',
            }}
            startIcon={<ChevronLeftIcon />}
          >
            Collapse
          </Button>
        )}
      </Box>
    </Drawer>
  );
}
