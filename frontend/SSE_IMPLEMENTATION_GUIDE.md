# Hướng dẫn triển khai SSE Notification System

## Tổng quan

Hệ thống notification realtime sử dụng Server-Sent Events (SSE) đã được triển khai hoàn chỉnh. Tài liệu này hướng dẫn cách sử dụng và tích hợp hệ thống.

## Cấu trúc Files đã tạo

### 1. **Frontend Files**
```
src/
├── services/
│   ├── sseService.ts              # SSE service implementation
│   └── notificationTriggerService.ts  # Auto notification triggers
├── context/
│   └── NotificationContext.tsx    # Updated for SSE
├── components/
│   ├── NotificationDialog.tsx     # Updated for SSE
│   ├── RealtimeStatus.tsx         # Connection status indicator
│   └── Header.tsx                 # Updated with RealtimeStatus
└── api/
    └── notification/
        └── notificationApi.ts     # REST API for notifications
```

### 2. **Documentation Files**
```
├── SSE_MESSAGE_SPECIFICATION.md   # Backend message format
├── SSE_IMPLEMENTATION_GUIDE.md    # This file
└── NOTIFICATION_SYSTEM.md         # General notification system
```

## Cách sử dụng

### 1. **Khởi tạo SSE Connection**

SSE connection sẽ tự động được khởi tạo khi app load:

```typescript
// Tự động trong NotificationProvider
useEffect(() => {
  initializeSSEConnection();
  refreshUnreadCount();
}, []);

const initializeSSEConnection = async () => {
  // Subscribe to notification updates
  const unsubscribe = sseService.subscribe('notification', handleSSENotification);
  
  // Connect to SSE
  await sseService.connect();
};
```

### 2. **Sử dụng trong Components**

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
  
  return (
    <div>
      <p>Connection: {connectionState}</p>
      <p>Unread: {unreadCount}</p>
    </div>
  );
}
```

### 3. **Auto Notification Triggers**

```typescript
// Tự động tạo notifications cho các actions
await notificationTriggers.reportCreated('Monthly Report');
await notificationTriggers.chartDeleted('Revenue Chart');
await notificationTriggers.sourceAdded('Database', 'MySQL');
await notificationTriggers.userAdded('john@example.com', 'admin');
await notificationTriggers.systemMaintenance('2:00 AM', '2 hours');
```

## Backend Requirements

### 1. **SSE Endpoint**

```
GET /notifications/stream
```

**Headers:**
```
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive
Access-Control-Allow-Origin: [your-domain]
Access-Control-Allow-Credentials: true
```

### 2. **Message Format**

#### Notification Event:
```json
{
  "action": "create|update|delete|mark_read",
  "notification": {
    "id": "uuid-string",
    "type": "success|error|warning|info",
    "category": "report|chart|source|user|system",
    "title": "Notification Title",
    "message": "Notification message content",
    "timestamp": "2024-01-15T10:30:00Z",
    "read": false,
    "actionUrl": "optional-url",
    "userId": "user-uuid"
  },
  "notificationIds": ["uuid1", "uuid2"],
  "unreadCount": 5
}
```

#### System Event:
```json
{
  "type": "maintenance|update|alert",
  "message": "System message content",
  "timestamp": "2024-01-15T10:30:00Z",
  "severity": "info|warning|error",
  "actionRequired": false
}
```

#### Ping Event:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "serverTime": "2024-01-15T10:30:00Z"
}
```

### 3. **SSE Stream Format**

```
event: notification
data: {"action":"create","notification":{...}}

event: system
data: {"type":"maintenance","message":"..."}

event: ping
data: {"timestamp":"2024-01-15T10:30:00Z"}
```

## Tích hợp với Existing Components

### 1. **Charts.tsx**
```typescript
import { useNotificationTriggers } from '../services/notificationTriggerService';

export default function Charts() {
  const notificationTriggers = useNotificationTriggers();
  
  const handleCreateSuccess = async () => {
    await notificationTriggers.chartCreated(chartName);
    setSuccess('Chart created successfully');
    setShowSuccessPopup(true);
  };
  
  const handleDeleteSuccess = async () => {
    await notificationTriggers.chartDeleted(chartName);
    setSuccess('Chart deleted successfully');
    setShowSuccessPopup(true);
  };
}
```

### 2. **Reports.tsx**
```typescript
import { useNotificationTriggers } from '../services/notificationTriggerService';

export default function Reports() {
  const notificationTriggers = useNotificationTriggers();
  
  const handleCreateSuccess = async () => {
    await notificationTriggers.reportCreated(reportName);
    setSuccess('Report created successfully');
    setShowSuccessPopup(true);
  };
  
  const handleDeleteSuccess = async () => {
    await notificationTriggers.reportDeleted(reportName);
    setSuccess('Report deleted successfully');
    setShowSuccessPopup(true);
  };
}
```

### 3. **Sources.tsx**
```typescript
import { useNotificationTriggers } from '../services/notificationTriggerService';

export default function Sources() {
  const notificationTriggers = useNotificationTriggers();
  
  const handleAddSuccess = async () => {
    await notificationTriggers.sourceAdded(sourceName, sourceType);
    setSuccess('Source added successfully');
    setShowSuccessPopup(true);
  };
  
  const handleConnectionError = async () => {
    await notificationTriggers.sourceConnectionError(sourceName, errorMessage);
  };
}
```

## Connection Management

### 1. **Auto-reconnect**
- Exponential backoff (1s, 2s, 4s, 8s, 16s)
- Max 5 attempts
- Tự động reconnect khi network online

### 2. **Connection States**
- `disconnected`: Không kết nối
- `connecting`: Đang kết nối
- `connected`: Đã kết nối
- `closed`: Đã đóng

### 3. **UI Indicators**
- **RealtimeStatus**: Hiển thị ở header
- **NotificationDialog**: Hiển thị trạng thái và nút reconnect
- **Connection alerts**: Warning khi mất kết nối

## Error Handling

### 1. **Connection Errors**
```typescript
// Tự động xử lý trong sseService
this.eventSource.onerror = (error) => {
  console.error('SSE error:', error);
  this.connectionState = 'disconnected';
  
  if (this.shouldReconnect) {
    this.scheduleReconnect();
  }
};
```

### 2. **Message Parsing Errors**
```typescript
try {
  const data = JSON.parse(event.data);
  // Process data
} catch (error) {
  console.error('Error parsing SSE message:', error);
}
```

### 3. **Fallback Strategy**
- SSE connection fails → Fallback to polling
- Network offline → Pause connection
- Server error → Retry with exponential backoff

## Performance Optimization

### 1. **Connection Management**
- Single connection per user
- Automatic cleanup on disconnect
- Connection pooling on server

### 2. **Message Batching**
- Batch multiple notifications
- Reduce server load
- Improve client performance

### 3. **Memory Management**
- Cleanup event listeners
- Remove dead connections
- Garbage collection friendly

## Security Considerations

### 1. **Authentication**
- Session-based authentication
- User-specific channels
- Rate limiting

### 2. **Data Validation**
- Validate all incoming messages
- Sanitize user input
- Check message size limits

### 3. **CORS Configuration**
```javascript
// Backend CORS settings
app.use(cors({
  origin: 'https://your-domain.com',
  credentials: true
}));
```

## Testing

### 1. **Unit Tests**
```typescript
describe('SSEService', () => {
  it('should connect to SSE endpoint', async () => {
    const service = new SSEService();
    await service.connect();
    expect(service.isConnected()).toBe(true);
  });
  
  it('should handle reconnection', async () => {
    // Test reconnection logic
  });
});
```

### 2. **Integration Tests**
```typescript
describe('Notification System', () => {
  it('should receive real-time notifications', async () => {
    // Test complete notification flow
  });
  
  it('should handle connection loss', async () => {
    // Test connection recovery
  });
});
```

### 3. **Load Testing**
```bash
# Test with multiple connections
for i in {1..100}; do
  curl -N -H "Accept: text/event-stream" \
       -H "Cookie: session=session-$i" \
       http://localhost:8000/notifications/stream &
done
```

## Monitoring & Debugging

### 1. **Connection Monitoring**
```typescript
// Log connection state changes
sseService.subscribe('connection', (state) => {
  console.log('Connection state changed:', state);
});

// Monitor message flow
sseService.subscribe('message', (message) => {
  console.log('Message received:', message);
});
```

### 2. **Browser DevTools**
- Network tab: Monitor SSE connection
- Console: Connection logs
- Application tab: Connection state

### 3. **Server Monitoring**
- Connection count
- Message throughput
- Error rates
- Response times

## Deployment Checklist

### 1. **Frontend**
- [ ] SSE service configured
- [ ] Notification context integrated
- [ ] UI components updated
- [ ] Error handling implemented
- [ ] Testing completed

### 2. **Backend**
- [ ] SSE endpoint implemented
- [ ] Message format defined
- [ ] Authentication configured
- [ ] CORS settings applied
- [ ] Connection management implemented
- [ ] Error handling added
- [ ] Load testing completed

### 3. **Infrastructure**
- [ ] Load balancer configured
- [ ] SSL certificates installed
- [ ] Monitoring setup
- [ ] Logging configured
- [ ] Backup strategy defined

## Troubleshooting

### Common Issues

1. **Connection fails**
   - Check SSE endpoint URL
   - Verify authentication
   - Check CORS settings

2. **Messages not received**
   - Verify event types
   - Check message format
   - Monitor server logs

3. **High reconnection rate**
   - Check network stability
   - Verify server health
   - Review connection settings

### Debug Commands
```typescript
// Check connection state
console.log('Connection state:', sseService.getConnectionState());

// Force reconnect
await sseService.connect();

// Check ready state
console.log('Ready state:', sseService.getReadyState());
```

## Cost Benefits

### SSE vs WebSocket Comparison

| Aspect | SSE | WebSocket |
|--------|-----|-----------|
| Development Cost | $10,000-15,000 | $20,000-30,000 |
| Infrastructure Cost | $100-300/month | $200-500/month |
| Maintenance Cost | $300-600/month | $500-1000/month |
| Complexity | Medium | High |
| Scalability | Easy | Complex |

**Total Savings: 40-60%** cho development và operational costs. 