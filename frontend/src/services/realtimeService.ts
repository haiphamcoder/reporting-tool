import { API_CONFIG } from '../config/api';

export interface RealtimeMessage {
  type: 'notification' | 'system' | 'ping' | 'pong';
  data?: any;
  timestamp: number;
}

export interface NotificationUpdate {
  action: 'create' | 'update' | 'delete' | 'mark_read';
  notification?: any;
  notificationIds?: string[];
  unreadCount?: number;
}

class RealtimeService {
  private ws: WebSocket | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 1000; // Start with 1 second
  private maxReconnectDelay = 30000; // Max 30 seconds
  private heartbeatInterval: NodeJS.Timeout | null = null;
  private messageHandlers: Map<string, ((data: any) => void)[]> = new Map();
  private isConnecting = false;
  private shouldReconnect = true;

  constructor() {
    this.setupEventListeners();
  }

  private setupEventListeners() {
    // Handle page visibility changes
    document.addEventListener('visibilitychange', () => {
      if (document.hidden) {
        this.pauseConnection();
      } else {
        this.resumeConnection();
      }
    });

    // Handle online/offline events
    window.addEventListener('online', () => {
      console.log('Network is online, attempting to reconnect...');
      this.connect();
    });

    window.addEventListener('offline', () => {
      console.log('Network is offline, pausing connection...');
      this.pauseConnection();
    });
  }

  public connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.ws?.readyState === WebSocket.OPEN) {
        resolve();
        return;
      }

      if (this.isConnecting) {
        reject(new Error('Connection already in progress'));
        return;
      }

      this.isConnecting = true;

      try {
        // Get WebSocket URL from API config or use a default
        const wsUrl = this.getWebSocketUrl();
        this.ws = new WebSocket(wsUrl);

        this.ws.onopen = () => {
          console.log('WebSocket connected');
          this.isConnecting = false;
          this.reconnectAttempts = 0;
          this.reconnectDelay = 1000;
          this.startHeartbeat();
          this.authenticate();
          resolve();
        };

        this.ws.onmessage = (event) => {
          this.handleMessage(event);
        };

        this.ws.onclose = (event) => {
          console.log('WebSocket disconnected:', event.code, event.reason);
          this.isConnecting = false;
          this.stopHeartbeat();
          
          if (this.shouldReconnect && !event.wasClean) {
            this.scheduleReconnect();
          }
        };

        this.ws.onerror = (error) => {
          console.error('WebSocket error:', error);
          this.isConnecting = false;
          reject(error);
        };

      } catch (error) {
        this.isConnecting = false;
        reject(error);
      }
    });
  }

  private getWebSocketUrl(): string {
    // Convert HTTP URL to WebSocket URL
    const baseUrl = API_CONFIG.BASE_URL.replace(/^http/, 'ws');
    return `${baseUrl}/ws/notifications`;
  }

  private authenticate() {
    // Send authentication message with session cookie
    this.send({
      type: 'auth',
      data: {
        // The session cookie will be automatically included
        timestamp: Date.now()
      }
    });
  }

  private startHeartbeat() {
    this.heartbeatInterval = setInterval(() => {
      if (this.ws?.readyState === WebSocket.OPEN) {
        this.send({ type: 'ping', timestamp: Date.now() });
      }
    }, 30000); // Send ping every 30 seconds
  }

  private stopHeartbeat() {
    if (this.heartbeatInterval) {
      clearInterval(this.heartbeatInterval);
      this.heartbeatInterval = null;
    }
  }

  private scheduleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.log('Max reconnection attempts reached');
      return;
    }

    this.reconnectAttempts++;
    const delay = Math.min(this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1), this.maxReconnectDelay);

    console.log(`Scheduling reconnect attempt ${this.reconnectAttempts} in ${delay}ms`);

    setTimeout(() => {
      if (this.shouldReconnect) {
        this.connect().catch(error => {
          console.error('Reconnection failed:', error);
        });
      }
    }, delay);
  }

  private handleMessage(event: MessageEvent) {
    try {
      const message: RealtimeMessage = JSON.parse(event.data);
      
      switch (message.type) {
        case 'pong':
          // Heartbeat response, no action needed
          break;
          
        case 'notification':
          this.handleNotificationUpdate(message.data);
          break;
          
        case 'system':
          this.handleSystemMessage(message.data);
          break;
          
        default:
          console.log('Unknown message type:', message.type);
      }

      // Trigger message handlers
      const handlers = this.messageHandlers.get(message.type);
      if (handlers) {
        handlers.forEach(handler => handler(message.data));
      }

    } catch (error) {
      console.error('Error parsing WebSocket message:', error);
    }
  }

  private handleNotificationUpdate(data: NotificationUpdate) {
    // This will be handled by the notification context
    console.log('Notification update received:', data);
  }

  private handleSystemMessage(data: any) {
    console.log('System message received:', data);
  }

  public send(message: Partial<RealtimeMessage>) {
    if (this.ws?.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify({
        ...message,
        timestamp: Date.now()
      }));
    } else {
      console.warn('WebSocket is not connected, cannot send message');
    }
  }

  public subscribe(messageType: string, handler: (data: any) => void) {
    if (!this.messageHandlers.has(messageType)) {
      this.messageHandlers.set(messageType, []);
    }
    this.messageHandlers.get(messageType)!.push(handler);

    // Return unsubscribe function
    return () => {
      const handlers = this.messageHandlers.get(messageType);
      if (handlers) {
        const index = handlers.indexOf(handler);
        if (index > -1) {
          handlers.splice(index, 1);
        }
      }
    };
  }

  public unsubscribe(messageType: string, handler: (data: any) => void) {
    const handlers = this.messageHandlers.get(messageType);
    if (handlers) {
      const index = handlers.indexOf(handler);
      if (index > -1) {
        handlers.splice(index, 1);
      }
    }
  }

  public disconnect() {
    this.shouldReconnect = false;
    this.stopHeartbeat();
    
    if (this.ws) {
      this.ws.close(1000, 'Client disconnecting');
      this.ws = null;
    }
  }

  public pauseConnection() {
    this.shouldReconnect = false;
    if (this.ws) {
      this.ws.close(1000, 'Pausing connection');
    }
  }

  public resumeConnection() {
    this.shouldReconnect = true;
    this.connect().catch(error => {
      console.error('Failed to resume connection:', error);
    });
  }

  public isConnected(): boolean {
    return this.ws?.readyState === WebSocket.OPEN;
  }

  public getConnectionState(): string {
    if (!this.ws) return 'disconnected';
    
    switch (this.ws.readyState) {
      case WebSocket.CONNECTING:
        return 'connecting';
      case WebSocket.OPEN:
        return 'connected';
      case WebSocket.CLOSING:
        return 'closing';
      case WebSocket.CLOSED:
        return 'closed';
      default:
        return 'unknown';
    }
  }
}

// Create singleton instance
export const realtimeService = new RealtimeService(); 