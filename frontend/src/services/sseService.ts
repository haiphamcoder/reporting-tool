import { API_CONFIG } from '../config/api';

export interface SSEMessage {
  id?: string;
  event: 'notification' | 'system' | 'ping' | 'auth';
  data: string;
  retry?: number;
}

export interface NotificationUpdate {
  action: 'create' | 'update' | 'delete' | 'mark_read';
  notification?: any;
  notificationIds?: string[];
  unreadCount?: number;
}

export interface SystemMessage {
  type: 'maintenance' | 'update' | 'alert';
  message: string;
  timestamp: string;
}

class SSEService {
  private eventSource: EventSource | null = null;
  private reconnectAttempts = 0;
  private maxReconnectAttempts = 5;
  private reconnectDelay = 1000; // Start with 1 second
  private maxReconnectDelay = 30000; // Max 30 seconds
  private messageHandlers: Map<string, ((data: any) => void)[]> = new Map();
  private isConnecting = false;
  private shouldReconnect = true;
  private connectionState: 'disconnected' | 'connecting' | 'connected' | 'closed' = 'disconnected';

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
      if (this.eventSource?.readyState === EventSource.OPEN) {
        resolve();
        return;
      }

      if (this.isConnecting) {
        reject(new Error('Connection already in progress'));
        return;
      }

      this.isConnecting = true;
      this.connectionState = 'connecting';

      try {
        const sseUrl = this.getSSEUrl();
        this.eventSource = new EventSource(sseUrl, {
          withCredentials: true // Include cookies for authentication
        });

        this.eventSource.onopen = () => {
          console.log('SSE connected');
          this.isConnecting = false;
          this.connectionState = 'connected';
          this.reconnectAttempts = 0;
          this.reconnectDelay = 1000;
          resolve();
        };

        this.eventSource.onmessage = (event) => {
          this.handleMessage(event);
        };

        this.eventSource.addEventListener('notification', (event) => {
          this.handleNotificationEvent(event);
        });

        this.eventSource.addEventListener('system', (event) => {
          this.handleSystemEvent(event);
        });

        this.eventSource.addEventListener('ping', (event) => {
          this.handlePingEvent(event);
        });

        this.eventSource.onerror = (error) => {
          console.error('SSE error:', error);
          this.isConnecting = false;
          this.connectionState = 'disconnected';
          
          if (this.shouldReconnect) {
            this.scheduleReconnect();
          }
          
          reject(error);
        };

      } catch (error) {
        this.isConnecting = false;
        this.connectionState = 'disconnected';
        reject(error);
      }
    });
  }

  private getSSEUrl(): string {
    // Convert HTTP URL to SSE endpoint
    const baseUrl = API_CONFIG.BASE_URL;
    return `${baseUrl}/notifications/stream`;
  }

  private handleMessage(event: MessageEvent) {
    try {
      // Handle default message event
      if (event.type === 'message') {
        const data = JSON.parse(event.data);
        console.log('SSE message received:', data);
        
        // Trigger message handlers
        const handlers = this.messageHandlers.get('message');
        if (handlers) {
          handlers.forEach(handler => handler(data));
        }
      }
    } catch (error) {
      console.error('Error parsing SSE message:', error);
    }
  }

  private handleNotificationEvent(event: MessageEvent) {
    try {
      const data: NotificationUpdate = JSON.parse(event.data);
      console.log('SSE notification event received:', data);
      
      // Trigger notification handlers
      const handlers = this.messageHandlers.get('notification');
      if (handlers) {
        handlers.forEach(handler => handler(data));
      }
    } catch (error) {
      console.error('Error parsing SSE notification event:', error);
    }
  }

  private handleSystemEvent(event: MessageEvent) {
    try {
      const data: SystemMessage = JSON.parse(event.data);
      console.log('SSE system event received:', data);
      
      // Trigger system handlers
      const handlers = this.messageHandlers.get('system');
      if (handlers) {
        handlers.forEach(handler => handler(data));
      }
    } catch (error) {
      console.error('Error parsing SSE system event:', error);
    }
  }

  private handlePingEvent(event: MessageEvent) {
    try {
      const data = JSON.parse(event.data);
      console.log('SSE ping received:', data);
      
      // Trigger ping handlers
      const handlers = this.messageHandlers.get('ping');
      if (handlers) {
        handlers.forEach(handler => handler(data));
      }
    } catch (error) {
      console.error('Error parsing SSE ping event:', error);
    }
  }

  private scheduleReconnect() {
    if (this.reconnectAttempts >= this.maxReconnectAttempts) {
      console.log('Max reconnection attempts reached');
      return;
    }

    this.reconnectAttempts++;
    const delay = Math.min(this.reconnectDelay * Math.pow(2, this.reconnectAttempts - 1), this.maxReconnectDelay);

    console.log(`Scheduling SSE reconnect attempt ${this.reconnectAttempts} in ${delay}ms`);

    setTimeout(() => {
      if (this.shouldReconnect) {
        this.connect().catch(error => {
          console.error('SSE reconnection failed:', error);
        });
      }
    }, delay);
  }

  public subscribe(eventType: string, handler: (data: any) => void) {
    if (!this.messageHandlers.has(eventType)) {
      this.messageHandlers.set(eventType, []);
    }
    this.messageHandlers.get(eventType)!.push(handler);

    // Return unsubscribe function
    return () => {
      const handlers = this.messageHandlers.get(eventType);
      if (handlers) {
        const index = handlers.indexOf(handler);
        if (index > -1) {
          handlers.splice(index, 1);
        }
      }
    };
  }

  public unsubscribe(eventType: string, handler: (data: any) => void) {
    const handlers = this.messageHandlers.get(eventType);
    if (handlers) {
      const index = handlers.indexOf(handler);
      if (index > -1) {
        handlers.splice(index, 1);
      }
    }
  }

  public disconnect() {
    this.shouldReconnect = false;
    this.connectionState = 'closed';
    
    if (this.eventSource) {
      this.eventSource.close();
      this.eventSource = null;
    }
  }

  public pauseConnection() {
    this.shouldReconnect = false;
    if (this.eventSource) {
      this.eventSource.close();
      this.connectionState = 'disconnected';
    }
  }

  public resumeConnection() {
    this.shouldReconnect = true;
    this.connect().catch(error => {
      console.error('Failed to resume SSE connection:', error);
    });
  }

  public isConnected(): boolean {
    return this.eventSource?.readyState === EventSource.OPEN;
  }

  public getConnectionState(): string {
    return this.connectionState;
  }

  public getReadyState(): number {
    return this.eventSource?.readyState || EventSource.CLOSED;
  }
}

// Create singleton instance
export const sseService = new SSEService(); 