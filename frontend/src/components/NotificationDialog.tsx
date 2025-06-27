import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  IconButton,
  Typography,
  Box,
  List,
  ListItem,
  ListItemText,
  ListItemIcon,
  Chip,
  Button,
  Divider,
  Badge,
  Tabs,
  Tab,
  CircularProgress,
  Alert,
} from '@mui/material';
import {
  Close as CloseIcon,
  Notifications as NotificationsIcon,
  CheckCircle as CheckCircleIcon,
  Error as ErrorIcon,
  Warning as WarningIcon,
  Info as InfoIcon,
  Edit as EditIcon,
  Add as AddIcon,
  Refresh as RefreshIcon,
  SystemUpdate as SystemUpdateIcon,
} from '@mui/icons-material';
import { notificationApi, Notification } from '../api/notification/notificationApi';
import { useNotifications } from '../context/NotificationContext';

interface NotificationDialogProps {
  open: boolean;
  onClose: () => void;
}

export default function NotificationDialog({ open, onClose }: NotificationDialogProps) {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [selectedTab, setSelectedTab] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [unreadCount, setUnreadCount] = useState(0);
  
  const { 
    fetchNotifications: contextFetchNotifications,
    markAsRead: contextMarkAsRead,
    markAllAsRead: contextMarkAllAsRead,
    clearAll: contextClearAll,
    error: contextError
  } = useNotifications();

  const categories = ['all', 'report', 'chart', 'source', 'user', 'system'];
  const categoryLabels = ['All', 'Reports', 'Charts', 'Sources', 'Users', 'System'];

  // Fetch notifications when dialog opens
  useEffect(() => {
    if (open) {
      fetchNotifications();
    }
  }, [open]);

  // Sync with context state
  useEffect(() => {
    if (contextError) {
      setError(contextError);
    }
  }, [contextError]);

  const fetchNotifications = async () => {
    setLoading(true);
    setError(null);
    try {
      await contextFetchNotifications();
      // Get notifications from context
      const response = await notificationApi.getNotifications(0, 100);
      if (response.success) {
        setNotifications(response.result.notifications);
        setUnreadCount(response.result.unread_count);
      } else {
        setError(response.message || 'Failed to fetch notifications');
      }
    } catch (error) {
      console.error('Error fetching notifications:', error);
      setError('Failed to load notifications');
    } finally {
      setLoading(false);
    }
  };

  const getIconByType = (type: Notification['type']) => {
    switch (type) {
      case 'success':
        return <CheckCircleIcon color="success" />;
      case 'error':
        return <ErrorIcon color="error" />;
      case 'warning':
        return <WarningIcon color="warning" />;
      case 'info':
        return <InfoIcon color="info" />;
      default:
        return <NotificationsIcon />;
    }
  };

  const getCategoryIcon = (category: Notification['category']) => {
    switch (category) {
      case 'report':
        return <AddIcon fontSize="small" />;
      case 'chart':
        return <EditIcon fontSize="small" />;
      case 'source':
        return <RefreshIcon fontSize="small" />;
      case 'user':
        return <AddIcon fontSize="small" />;
      case 'system':
        return <SystemUpdateIcon fontSize="small" />;
      default:
        return <NotificationsIcon fontSize="small" />;
    }
  };

  const getTypeColor = (type: Notification['type']) => {
    switch (type) {
      case 'success':
        return 'success';
      case 'error':
        return 'error';
      case 'warning':
        return 'warning';
      case 'info':
        return 'info';
      default:
        return 'default';
    }
  };

  const formatTimeAgo = (date: Date) => {
    const now = new Date();
    const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60));
    
    if (diffInMinutes < 1) return 'Just now';
    if (diffInMinutes < 60) return `${diffInMinutes}m ago`;
    
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) return `${diffInHours}h ago`;
    
    const diffInDays = Math.floor(diffInHours / 24);
    return `${diffInDays}d ago`;
  };

  const filteredNotifications = selectedTab === 0 
    ? notifications 
    : notifications.filter(n => n.category === categories[selectedTab]);

  const markAsRead = async (notificationId: string) => {
    try {
      await contextMarkAsRead(notificationId);
      setNotifications(prev => 
        prev.map(n => n.id === notificationId ? { ...n, read: true } : n)
      );
      setUnreadCount(prev => Math.max(0, prev - 1));
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const markAllAsRead = async () => {
    try {
      await contextMarkAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, read: true })));
      setUnreadCount(0);
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  const clearAll = async () => {
    try {
      await contextClearAll();
      setNotifications([]);
      setUnreadCount(0);
    } catch (error) {
      console.error('Error clearing all notifications:', error);
    }
  };

  const handleTabChange = (_event: React.SyntheticEvent, newValue: number) => {
    setSelectedTab(newValue);
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
      PaperProps={{
        sx: {
          borderRadius: 2,
          maxHeight: '80vh',
        }
      }}
    >
      <DialogTitle sx={{ 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'space-between',
        pb: 1
      }}>
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <NotificationsIcon />
          <Typography variant="h6">Notifications</Typography>
          {unreadCount > 0 && (
            <Badge badgeContent={unreadCount} color="error" />
          )}
        </Box>
        <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
          {unreadCount > 0 && (
            <Button size="small" onClick={markAllAsRead}>
              Mark all read
            </Button>
          )}
          <IconButton onClick={onClose} size="small">
            <CloseIcon />
          </IconButton>
        </Box>
      </DialogTitle>

      <Tabs 
        value={selectedTab} 
        onChange={handleTabChange}
        variant="scrollable"
        scrollButtons="auto"
        sx={{ px: 2, borderBottom: 1, borderColor: 'divider' }}
      >
        {categoryLabels.map((label) => (
          <Tab 
            key={label} 
            label={label} 
            sx={{ 
              minWidth: 'auto',
              textTransform: 'none',
              fontSize: '0.875rem'
            }} 
          />
        ))}
      </Tabs>

      <DialogContent sx={{ p: 0 }}>
        {error && (
          <Alert severity="error" sx={{ m: 2 }}>
            {error}
          </Alert>
        )}

        {loading ? (
          <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
            <CircularProgress />
          </Box>
        ) : filteredNotifications.length === 0 ? (
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', p: 4 }}>
            <NotificationsIcon sx={{ fontSize: 48, color: 'text.secondary', mb: 2 }} />
            <Typography variant="body2" color="text.secondary">
              No notifications
            </Typography>
          </Box>
        ) : (
          <List sx={{ p: 0 }}>
            {filteredNotifications.map((notification, index) => (
              <React.Fragment key={notification.id}>
                <ListItem 
                  sx={{ 
                    px: 2, 
                    py: 1.5,
                    backgroundColor: notification.read ? 'transparent' : 'action.hover',
                    '&:hover': {
                      backgroundColor: 'action.hover',
                    }
                  }}
                  onClick={() => markAsRead(notification.id)}
                >
                  <ListItemIcon sx={{ minWidth: 40 }}>
                    {getIconByType(notification.type)}
                  </ListItemIcon>
                  <ListItemText
                    primary={
                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1, mb: 0.5 }}>
                        <Typography 
                          variant="body2" 
                          sx={{ 
                            fontWeight: notification.read ? 400 : 600,
                            flex: 1
                          }}
                        >
                          {notification.title}
                        </Typography>
                        <Chip
                          label={notification.category}
                          size="small"
                          icon={getCategoryIcon(notification.category)}
                          color={getTypeColor(notification.type) as any}
                          variant="outlined"
                        />
                      </Box>
                    }
                    secondary={
                      <Box>
                        <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                          {notification.message}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {formatTimeAgo(notification.timestamp)}
                        </Typography>
                      </Box>
                    }
                  />
                </ListItem>
                {index < filteredNotifications.length - 1 && <Divider />}
              </React.Fragment>
            ))}
          </List>
        )}
      </DialogContent>

      {filteredNotifications.length > 0 && (
        <Box sx={{ p: 2, borderTop: 1, borderColor: 'divider' }}>
          <Button 
            fullWidth 
            variant="outlined" 
            onClick={clearAll}
            color="error"
          >
            Clear All Notifications
          </Button>
        </Box>
      )}
    </Dialog>
  );
} 