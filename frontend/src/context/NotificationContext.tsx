import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { notificationApi, Notification } from '../api/notification/notificationApi';
import { sseService, NotificationUpdate } from '../services/sseService';

interface NotificationContextType {
  unreadCount: number;
  notifications: Notification[];
  loading: boolean;
  error: string | null;
  connectionState: string;
  fetchNotifications: () => Promise<void>;
  markAsRead: (notificationId: string) => Promise<void>;
  markAllAsRead: () => Promise<void>;
  clearAll: () => Promise<void>;
  addNotification: (notification: Omit<Notification, 'id' | 'timestamp' | 'read'>) => Promise<void>;
  refreshUnreadCount: () => Promise<void>;
  reconnect: () => Promise<void>;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

interface NotificationProviderProps {
  children: ReactNode;
}

export function NotificationProvider({ children }: NotificationProviderProps) {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [connectionState, setConnectionState] = useState('disconnected');

  // Initialize SSE connection and fetch initial data
  useEffect(() => {
    initializeSSEConnection();
    refreshUnreadCount();

    return () => {
      // Cleanup SSE connection on unmount
      sseService.disconnect();
    };
  }, []);

  const initializeSSEConnection = async () => {
    try {
      // Subscribe to notification updates
      const unsubscribe = sseService.subscribe('notification', handleSSENotification);

      // Connect to SSE
      await sseService.connect();
      setConnectionState(sseService.getConnectionState());

      // Set up connection state monitoring
      const connectionInterval = setInterval(() => {
        setConnectionState(sseService.getConnectionState());
      }, 1000);

      // Cleanup function
      return () => {
        unsubscribe();
        clearInterval(connectionInterval);
      };
    } catch (error) {
      console.error('Failed to initialize SSE connection:', error);
      setError('Failed to connect to realtime service');
    }
  };

  const handleSSENotification = (data: NotificationUpdate) => {
    console.log('SSE notification received:', data);

    switch (data.action) {
      case 'create':
        if (data.notification) {
          setNotifications(prev => [data.notification, ...prev]);
          if (!data.notification.read) {
            setUnreadCount(prev => prev + 1);
          }
        }
        break;

      case 'update':
        if (data.notification) {
          setNotifications(prev =>
            prev.map(n => n.id === data.notification.id ? data.notification : n)
          );
        }
        break;

      case 'delete':
        if (data.notificationIds) {
          setNotifications(prev =>
            prev.filter(n => !data.notificationIds!.includes(n.id))
          );
          // Recalculate unread count
          refreshUnreadCount();
        }
        break;

      case 'mark_read':
        if (data.notificationIds) {
          setNotifications(prev =>
            prev.map(n =>
              data.notificationIds!.includes(n.id) ? { ...n, read: true } : n
            )
          );
          if (data.unreadCount !== undefined) {
            setUnreadCount(data.unreadCount);
          } else {
            // Recalculate unread count
            refreshUnreadCount();
          }
        }
        break;
    }
  };

  const fetchNotifications = async () => {
    setLoading(true);
    setError(null);
    try {
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

  const refreshUnreadCount = async () => {
    try {
      const response = await notificationApi.getUnreadCount();
      if (response.success && response.result !== undefined) {
        setUnreadCount(response.result);
      }
    } catch (error) {
      console.error('Error fetching unread count:', error);
    }
  };

  const markAsRead = async (notificationId: string) => {
    try {
      const response = await notificationApi.markAsRead([notificationId]);
      if (response.success) {
        setNotifications(prev =>
          prev.map(n => n.id === notificationId ? { ...n, read: true } : n)
        );
        setUnreadCount(prev => Math.max(0, prev - 1));

        // Note: SSE doesn't support client-to-server communication
        // Server should handle this via REST API and broadcast via SSE
      }
    } catch (error) {
      console.error('Error marking notification as read:', error);
    }
  };

  const markAllAsRead = async () => {
    try {
      const response = await notificationApi.markAllAsRead();
      if (response.success) {
        setNotifications(prev => prev.map(n => ({ ...n, read: true })));
        setUnreadCount(0);

        // Note: SSE doesn't support client-to-server communication
        // Server should handle this via REST API and broadcast via SSE
      }
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
    }
  };

  const clearAll = async () => {
    try {
      const response = await notificationApi.clearAllNotifications();
      if (response.success) {
        setNotifications([]);
        setUnreadCount(0);

        // Note: SSE doesn't support client-to-server communication
        // Server should handle this via REST API and broadcast via SSE
      }
    } catch (error) {
      console.error('Error clearing all notifications:', error);
    }
  };

  const addNotification = async (notification: Omit<Notification, 'id' | 'timestamp' | 'read'>) => {
    try {
      const response = await notificationApi.createNotification(notification);
      if (response.success && response.result) {
        setNotifications(prev => [response.result!, ...prev]);
        if (!response.result.read) {
          setUnreadCount(prev => prev + 1);
        }

        // Note: SSE doesn't support client-to-server communication
        // Server should handle this via REST API and broadcast via SSE
      }
    } catch (error) {
      console.error('Error creating notification:', error);
    }
  };

  const reconnect = async () => {
    try {
      await sseService.connect();
      setConnectionState(sseService.getConnectionState());
      setError(null);
    } catch (error) {
      console.error('Failed to reconnect:', error);
      setError('Failed to reconnect to realtime service');
    }
  };

  const value: NotificationContextType = {
    unreadCount,
    notifications,
    loading,
    error,
    connectionState,
    fetchNotifications,
    markAsRead,
    markAllAsRead,
    clearAll,
    addNotification,
    refreshUnreadCount,
    reconnect,
  };

  return (
    <NotificationContext.Provider value={value}>
      {children}
    </NotificationContext.Provider>
  );
}

export function useNotifications() {
  const context = useContext(NotificationContext);
  if (context === undefined) {
    throw new Error('useNotifications must be used within a NotificationProvider');
  }
  return context;
} 