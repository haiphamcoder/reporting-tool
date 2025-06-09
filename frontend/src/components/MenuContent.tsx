import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Stack from '@mui/material/Stack';
import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import SourceRoundedIcon from '@mui/icons-material/SourceRounded';
import BarChartRoundedIcon from '@mui/icons-material/BarChartRounded';
import AssignmentRoundedIcon from '@mui/icons-material/AssignmentRounded';
import SettingsRoundedIcon from '@mui/icons-material/SettingsRounded';
import { useContent } from '../context/ContentContext';

const mainListItems = [
  { text: 'Home', icon: <HomeRoundedIcon />, type: 'home' as const },
  { text: 'Sources', icon: <SourceRoundedIcon />, type: 'sources' as const },
  { text: 'Charts', icon: <BarChartRoundedIcon />, type: 'charts' as const },
  { text: 'Reports', icon: <AssignmentRoundedIcon />, type: 'reports' as const },
];

const secondaryListItems = [
  { text: 'Settings', icon: <SettingsRoundedIcon />, type: 'settings' as const },
];

export default function MenuContent() {
  const { currentContent, setCurrentContent } = useContent();

  return (
    <Stack sx={{ flexGrow: 1, p: 1, justifyContent: 'space-between' }}>
      <List dense>
        {mainListItems.map((item, index) => (
          <ListItem key={index} disablePadding sx={{ display: 'block' }}>
            <ListItemButton
              selected={currentContent === item.type}
              onClick={() => setCurrentContent(item.type)}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
      <List dense>
        {secondaryListItems.map((item, index) => (
          <ListItem key={index} disablePadding sx={{ display: 'block' }}>
            <ListItemButton
              selected={currentContent === item.type}
              onClick={() => setCurrentContent(item.type)}
            >
              <ListItemIcon>{item.icon}</ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItemButton>
          </ListItem>
        ))}
      </List>
    </Stack>
  );
}
