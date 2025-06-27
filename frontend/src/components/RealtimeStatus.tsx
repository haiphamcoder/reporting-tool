import { Box, Tooltip, IconButton, Badge } from '@mui/material';
import {
  Language as NetworkIcon,
  LanguageOutlined as NetworkOffIcon,
} from '@mui/icons-material';
import { useNotifications } from '../context/NotificationContext';

export default function RealtimeStatus() {
  const { connectionState, reconnect } = useNotifications();

  const getConnectionStatusIcon = () => {
    switch (connectionState) {
      case 'connected':
        return <NetworkIcon color="success" />;
      case 'connecting':
        return <NetworkIcon color="warning" />;
      case 'disconnected':
      case 'closed':
        return <NetworkOffIcon color="error" />;
      default:
        return <NetworkOffIcon color="warning" />;
    }
  };

  const getConnectionStatusText = () => {
    switch (connectionState) {
      case 'connected':
        return 'Realtime connected';
      case 'connecting':
        return 'Connecting to realtime service...';
      case 'disconnected':
        return 'Realtime disconnected';
      case 'closed':
        return 'Realtime connection closed';
      default:
        return 'Realtime status unknown';
    }
  };

  const handleReconnect = async () => {
    try {
      await reconnect();
    } catch (error) {
      console.error('Failed to reconnect:', error);
    }
  };

  return (
    <Box sx={{ display: 'flex', alignItems: 'center' }}>
      <Tooltip title={getConnectionStatusText()}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          {connectionState === 'connected' ? (
            <Badge
              color="success"
              variant="dot"
              sx={{
                '& .MuiBadge-dot': {
                  backgroundColor: '#4caf50',
                }
              }}
            >
              {getConnectionStatusIcon()}
            </Badge>
          ) : (
            <IconButton 
              size="small" 
              onClick={handleReconnect}
              sx={{ 
                p: 0.5,
                '&:hover': {
                  backgroundColor: 'action.hover',
                }
              }}
            >
              {getConnectionStatusIcon()}
            </IconButton>
          )}
        </Box>
      </Tooltip>
    </Box>
  );
} 