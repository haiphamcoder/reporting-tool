# Hệ thống Thông báo Realtime

## Tổng quan

Hệ thống thông báo realtime được thiết kế để cung cấp thông tin tức thì cho người dùng thông qua WebSocket connections, đảm bảo notifications được đồng bộ realtime giữa các clients và server.

## Kiến trúc hệ thống

### 1. **WebSocket Service** (`src/services/realtimeService.ts`)
- Quản lý kết nối WebSocket
- Auto-reconnect với exponential backoff
- Heartbeat mechanism
- Message handling và routing
- Connection state management

### 2. **Notification Context** (`src/context/NotificationContext.tsx`)
- Global state management cho notifications
- Tích hợp với WebSocket service
- Real-time updates
- Error handling và recovery

### 3. **Notification API** (`src/api/notification/notificationApi.ts`)
- RESTful API endpoints
- CRUD operations cho notifications
- Pagination support
- Authentication required

### 4. **Trigger Service** (`src/services/notificationTriggerService.ts`)
- Tự động tạo notifications cho các actions
- Predefined notification templates
- Easy integration với existing components

## Cách hoạt động

### 1. **Kết nối WebSocket**
```typescript
// Tự động kết nối khi app khởi động
useEffect(() => {
  initializeRealtimeConnection();
}, []);

const initializeRealtimeConnection = async () => {
  // Subscribe to notification updates
  const unsubscribe = realtimeService.subscribe('notification', handleRealtimeNotification);
  
  // Connect to WebSocket
  await realtimeService.connect();
};
```

### 2. **Real-time Updates**
```typescript
const handleRealtimeNotification = (data: NotificationUpdate) => {
  switch (data.action) {
    case 'create':
      // Thêm notification mới
      setNotifications(prev => [data.notification, ...prev]);
      break;
    case 'update':
      // Cập nhật notification
      setNotifications(prev => 
        prev.map(n => n.id === data.notification.id ? data.notification : n)
      );
      break;
    case 'delete':
      // Xóa notification
      setNotifications(prev => 
        prev.filter(n => !data.notificationIds!.includes(n.id))
      );
      break;
    case 'mark_read':
      // Đánh dấu đã đọc
      setNotifications(prev => 
        prev.map(n => 
          data.notificationIds!.includes(n.id) ? { ...n, read: true } : n
        )
      );
      break;
  }
};
```

### 3. **Auto-reconnect**
- Exponential backoff strategy
- Max 5 attempts với delay tăng dần
- Tự động reconnect khi network online
- Pause connection khi tab không active

## Các tính năng chính

### 1. **Connection Management**
- **Auto-reconnect**: Tự động kết nối lại khi mất kết nối
- **Heartbeat**: Ping/pong để duy trì kết nối
- **State monitoring**: Theo dõi trạng thái kết nối realtime
- **Network awareness**: Phát hiện online/offline

### 2. **Real-time Synchronization**
- **Instant updates**: Notifications xuất hiện ngay lập tức
- **Bidirectional sync**: Client ↔ Server
- **Conflict resolution**: Xử lý xung đột dữ liệu
- **Optimistic updates**: UI cập nhật trước khi confirm

### 3. **Error Handling**
- **Graceful degradation**: Fallback về polling nếu WebSocket fail
- **Retry mechanism**: Tự động thử lại khi có lỗi
- **User feedback**: Hiển thị trạng thái kết nối cho user
- **Manual reconnect**: Cho phép user reconnect thủ công

### 4. **Performance Optimization**
- **Connection pooling**: Tái sử dụng connections
- **Message batching**: Gộp nhiều messages
- **Lazy loading**: Chỉ load notifications khi cần
- **Memory management**: Cleanup resources

## API Endpoints

### WebSocket Endpoints
```
ws://your-domain/ws/notifications
```

### Message Types
```typescript
interface RealtimeMessage {
  type: 'notification' | 'system' | 'ping' | 'pong' | 'auth';
  data?: any;
  timestamp: number;
}
```

### Notification Updates
```typescript
interface NotificationUpdate {
  action: 'create' | 'update' | 'delete' | 'mark_read';
  notification?: Notification;
  notificationIds?: string[];
  unreadCount?: number;
}
```

## Cách sử dụng

### 1. **Trong Components**
```typescript
import { useNotifications } from '../context/NotificationContext';
import { useNotificationTriggers } from '../services/notificationTriggerService';

function MyComponent() {
  const { unreadCount, connectionState } = useNotifications();
  const notificationTriggers = useNotificationTriggers();
  
  const handleCreateReport = async () => {
    try {
      // Create report logic
      await createReport(data);
      
      // Trigger notification
      await notificationTriggers.reportCreated(reportName);
    } catch (error) {
      // Handle error
    }
  };
}
```

### 2. **Tích hợp với existing actions**
```typescript
// Trong Charts.tsx
const handleCreateSuccess = async () => {
  await notificationTriggers.chartCreated(chartName);
  setSuccess('Chart created successfully');
  setShowSuccessPopup(true);
};

// Trong Reports.tsx
const handleDeleteSuccess = async () => {
  await notificationTriggers.reportDeleted(reportName);
  setSuccess('Report deleted successfully');
  setShowSuccessPopup(true);
};

// Trong Sources.tsx
const handleAddSuccess = async () => {
  await notificationTriggers.sourceAdded(sourceName, sourceType);
  setSuccess('Source added successfully');
  setShowSuccessPopup(true);
};
```

### 3. **Custom notifications**
```typescript
await notificationTriggers.customNotification({
  type: 'warning',
  category: 'system',
  title: 'Custom Alert',
  message: 'This is a custom notification message'
});
```

## UI Components

### 1. **RealtimeStatus** (`src/components/RealtimeStatus.tsx`)
- Hiển thị trạng thái kết nối ở header
- Icon thay đổi theo trạng thái
- Click để reconnect
- Tooltip với thông tin chi tiết

### 2. **NotificationDialog** (Updated)
- Hiển thị trạng thái kết nối
- Nút reconnect khi mất kết nối
- Warning message khi offline
- Real-time updates trong dialog

## Backend Requirements

### WebSocket Server
```python
# Example Python WebSocket server
import asyncio
import websockets
import json

async def notification_handler(websocket, path):
    # Authenticate user
    user = await authenticate_user(websocket)
    
    # Subscribe to user's notifications
    await subscribe_to_notifications(user.id, websocket)
    
    try:
        async for message in websocket:
            data = json.loads(message)
            
            if data['type'] == 'ping':
                await websocket.send(json.dumps({
                    'type': 'pong',
                    'timestamp': data['timestamp']
                }))
            elif data['type'] == 'notification':
                # Handle notification actions
                await handle_notification_action(data['data'])
                
    except websockets.exceptions.ConnectionClosed:
        await unsubscribe_from_notifications(user.id, websocket)
```

### Database Schema
```sql
-- Notifications table
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('success', 'error', 'warning', 'info')),
    category VARCHAR(20) NOT NULL CHECK (category IN ('report', 'chart', 'source', 'user', 'system')),
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    read BOOLEAN DEFAULT FALSE,
    action_url TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(read);
CREATE INDEX idx_notifications_created_at ON notifications(created_at);
```

## Monitoring & Debugging

### 1. **Connection Monitoring**
```typescript
// Log connection state changes
realtimeService.subscribe('connection', (state) => {
  console.log('Connection state changed:', state);
});

// Monitor message flow
realtimeService.subscribe('message', (message) => {
  console.log('Message received:', message);
});
```

### 2. **Performance Metrics**
- Connection uptime
- Message latency
- Reconnection frequency
- Error rates

### 3. **Debug Tools**
- Browser DevTools WebSocket tab
- Network tab monitoring
- Console logging
- Connection state indicators

## Security Considerations

### 1. **Authentication**
- WebSocket authentication via session cookies
- Token-based authentication
- User session validation

### 2. **Authorization**
- User-specific notification channels
- Role-based access control
- Notification permission checks

### 3. **Data Validation**
- Message format validation
- Input sanitization
- Rate limiting

## Testing

### 1. **Unit Tests**
```typescript
describe('RealtimeService', () => {
  it('should connect to WebSocket', async () => {
    const service = new RealtimeService();
    await service.connect();
    expect(service.isConnected()).toBe(true);
  });
  
  it('should handle reconnection', async () => {
    // Test reconnection logic
  });
});
```

### 2. **Integration Tests**
- WebSocket server integration
- Notification flow testing
- Error handling scenarios

### 3. **E2E Tests**
- Complete notification workflow
- Connection loss/recovery
- Multi-user scenarios

## Deployment

### 1. **WebSocket Server**
- Load balancing cho WebSocket connections
- Horizontal scaling
- Connection pooling
- Health checks

### 2. **Client Configuration**
```typescript
// Environment-based WebSocket URL
const wsUrl = process.env.NODE_ENV === 'production' 
  ? 'wss://your-domain.com/ws/notifications'
  : 'ws://localhost:8000/ws/notifications';
```

### 3. **Monitoring**
- Connection metrics
- Error tracking
- Performance monitoring
- User analytics

## Troubleshooting

### Common Issues

1. **Connection fails**
   - Check WebSocket server status
   - Verify authentication
   - Check network connectivity

2. **Messages not received**
   - Verify subscription
   - Check message format
   - Monitor server logs

3. **High reconnection rate**
   - Check network stability
   - Verify server health
   - Review connection settings

### Debug Commands
```typescript
// Check connection state
console.log('Connection state:', realtimeService.getConnectionState());

// Force reconnect
await realtimeService.connect();

// Send test message
realtimeService.send({
  type: 'notification',
  data: { action: 'test' }
});
``` 