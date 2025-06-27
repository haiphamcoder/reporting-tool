import { API_CONFIG } from '../../config/api';

export interface Notification {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  category: 'report' | 'chart' | 'source' | 'user' | 'system';
  title: string;
  message: string;
  timestamp: Date;
  read: boolean;
  actionUrl?: string;
  userId?: string;
}

export interface NotificationResponse {
  success: boolean;
  result: {
    notifications: Notification[];
    total: number;
    unread_count: number;
  };
  message?: string;
}

export interface MarkAsReadRequest {
  notification_ids: string[];
}

export const notificationApi = {
  // Get all notifications for current user
  getNotifications: async (page: number = 0, limit: number = 50): Promise<NotificationResponse> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/notifications?page=${page}&limit=${limit}`, {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return {
        success: data.success,
        result: {
          notifications: data.result.notifications.map((n: any) => ({
            ...n,
            timestamp: new Date(n.timestamp)
          })),
          total: data.result.total,
          unread_count: data.result.unread_count
        },
        message: data.message
      };
    } catch (error) {
      console.error('Error fetching notifications:', error);
      throw error;
    }
  },

  // Mark notifications as read
  markAsRead: async (notificationIds: string[]): Promise<{ success: boolean; message?: string }> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/notifications/mark-read`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify({ notification_ids: notificationIds }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return {
        success: data.success,
        message: data.message
      };
    } catch (error) {
      console.error('Error marking notifications as read:', error);
      throw error;
    }
  },

  // Mark all notifications as read
  markAllAsRead: async (): Promise<{ success: boolean; message?: string }> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/notifications/mark-all-read`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return {
        success: data.success,
        message: data.message
      };
    } catch (error) {
      console.error('Error marking all notifications as read:', error);
      throw error;
    }
  },

  // Delete notification
  deleteNotification: async (notificationId: string): Promise<{ success: boolean; message?: string }> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/notifications/${notificationId}`, {
        method: 'DELETE',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return {
        success: data.success,
        message: data.message
      };
    } catch (error) {
      console.error('Error deleting notification:', error);
      throw error;
    }
  },

  // Clear all notifications
  clearAllNotifications: async (): Promise<{ success: boolean; message?: string }> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/notifications/clear-all`, {
        method: 'DELETE',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return {
        success: data.success,
        message: data.message
      };
    } catch (error) {
      console.error('Error clearing all notifications:', error);
      throw error;
    }
  },

  // Get unread count
  getUnreadCount: async (): Promise<{ success: boolean; result?: number; message?: string }> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/notifications/unread-count`, {
        method: 'GET',
        credentials: 'include',
        headers: {
          'Accept': 'application/json',
        },
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return {
        success: data.success,
        result: data.result,
        message: data.message
      };
    } catch (error) {
      console.error('Error getting unread count:', error);
      throw error;
    }
  },

  // Create notification (for testing or admin purposes)
  createNotification: async (notification: Omit<Notification, 'id' | 'timestamp' | 'read'>): Promise<{ success: boolean; result?: Notification; message?: string }> => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/notifications`, {
        method: 'POST',
        credentials: 'include',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json',
        },
        body: JSON.stringify(notification),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return {
        success: data.success,
        result: data.result ? {
          ...data.result,
          timestamp: new Date(data.result.timestamp)
        } : undefined,
        message: data.message
      };
    } catch (error) {
      console.error('Error creating notification:', error);
      throw error;
    }
  }
}; 