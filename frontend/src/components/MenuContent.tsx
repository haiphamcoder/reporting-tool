import React from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import SourceRoundedIcon from '@mui/icons-material/SourceRounded';
import BarChartRoundedIcon from '@mui/icons-material/BarChartRounded';
import AssignmentRoundedIcon from '@mui/icons-material/AssignmentRounded';
import { useContent } from '../context/ContentContext';

const mainListItems = [
  { text: 'Home', icon: <HomeRoundedIcon />, type: 'home' as const },
  { text: 'Sources', icon: <SourceRoundedIcon />, type: 'sources' as const },
  { text: 'Charts', icon: <BarChartRoundedIcon />, type: 'charts' as const },
  { text: 'Reports', icon: <AssignmentRoundedIcon />, type: 'reports' as const },
];

interface MenuContentProps {
  collapsed: boolean;
  onToggleCollapse: () => void;
}

export default function MenuContent({ collapsed }: MenuContentProps) {
  const { currentContent, setCurrentContent } = useContent();

  return (
    <Stack sx={{ flexGrow: 1, p: 1, justifyContent: 'space-between', alignItems: collapsed ? 'center' : 'stretch' }}>
      <List
        dense
        sx={collapsed ? { display: 'flex', flexDirection: 'column', alignItems: 'center', p: 0 } : {}}
      >
        {mainListItems.map((item, index) => (
          <ListItem key={index} disablePadding sx={{ display: 'block', justifyContent: 'center', my: collapsed ? 0.5 : 0 }}>
            {collapsed ? (
              <Tooltip title={item.text} placement="right">
                <IconButton
                  onClick={() => setCurrentContent(item.type)}
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
                onClick={() => setCurrentContent(item.type)}
              >
                <ListItemIcon>{item.icon}</ListItemIcon>
                <ListItemText primary={item.text} />
              </ListItemButton>
            )}
          </ListItem>
        ))}
      </List>
    </Stack>
  );
}
