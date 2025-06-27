# SSE Message Specification cho Notification System

## Tổng quan

Tài liệu này định nghĩa cấu trúc message cho Server-Sent Events (SSE) endpoint `/notifications/stream` để triển khai hệ thống notification realtime.

## Endpoint

```
GET /notifications/stream
```

### Headers
```
Content-Type: text/event-stream
Cache-Control: no-cache
Connection: keep-alive
Access-Control-Allow-Origin: [your-domain]
Access-Control-Allow-Credentials: true
```

### Authentication
- Sử dụng session cookies hoặc Bearer token
- User phải được authenticated để access endpoint

## Message Format

### 1. **Notification Event**

#### Event Type: `notification`

#### Cấu trúc Message:
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
  "notificationIds": ["uuid1", "uuid2"], // For delete/mark_read actions
  "unreadCount": 5 // Updated unread count
}
```

#### Ví dụ Messages:

**Create Notification:**
```json
{
  "action": "create",
  "notification": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "type": "success",
    "category": "report",
    "title": "Report Created",
    "message": "Monthly Sales Report has been created successfully",
    "timestamp": "2024-01-15T10:30:00Z",
    "read": false,
    "userId": "user-123"
  },
  "unreadCount": 3
}
```

**Update Notification:**
```json
{
  "action": "update",
  "notification": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "type": "success",
    "category": "report",
    "title": "Report Updated",
    "message": "Monthly Sales Report has been updated",
    "timestamp": "2024-01-15T10:30:00Z",
    "read": true,
    "userId": "user-123"
  },
  "unreadCount": 2
}
```

**Delete Notifications:**
```json
{
  "action": "delete",
  "notificationIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "550e8400-e29b-41d4-a716-446655440001"
  ],
  "unreadCount": 1
}
```

**Mark as Read:**
```json
{
  "action": "mark_read",
  "notificationIds": ["550e8400-e29b-41d4-a716-446655440000"],
  "unreadCount": 2
}
```

### 2. **System Event**

#### Event Type: `system`

#### Cấu trúc Message:
```json
{
  "type": "maintenance|update|alert",
  "message": "System message content",
  "timestamp": "2024-01-15T10:30:00Z",
  "severity": "info|warning|error",
  "actionRequired": false
}
```

#### Ví dụ Messages:

**Maintenance Notification:**
```json
{
  "type": "maintenance",
  "message": "Scheduled maintenance will occur tonight at 2:00 AM for 2 hours",
  "timestamp": "2024-01-15T10:30:00Z",
  "severity": "warning",
  "actionRequired": false
}
```

**System Update:**
```json
{
  "type": "update",
  "message": "System updated to version 2.1.0",
  "timestamp": "2024-01-15T10:30:00Z",
  "severity": "info",
  "actionRequired": false
}
```

**Security Alert:**
```json
{
  "type": "alert",
  "message": "Multiple failed login attempts detected",
  "timestamp": "2024-01-15T10:30:00Z",
  "severity": "error",
  "actionRequired": true
}
```

### 3. **Ping Event**

#### Event Type: `ping`

#### Cấu trúc Message:
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "serverTime": "2024-01-15T10:30:00Z"
}
```

## SSE Stream Format

### Cấu trúc Event Stream:
```
event: notification
data: {"action":"create","notification":{...}}

event: system
data: {"type":"maintenance","message":"..."}

event: ping
data: {"timestamp":"2024-01-15T10:30:00Z"}

```

### Ví dụ Complete Stream:
```
event: notification
data: {"action":"create","notification":{"id":"123","type":"success","category":"report","title":"Report Created","message":"Monthly report created","timestamp":"2024-01-15T10:30:00Z","read":false},"unreadCount":1}

event: ping
data: {"timestamp":"2024-01-15T10:30:00Z","serverTime":"2024-01-15T10:30:00Z"}

event: system
data: {"type":"maintenance","message":"Scheduled maintenance tonight","timestamp":"2024-01-15T10:30:00Z","severity":"warning","actionRequired":false}

event: notification
data: {"action":"mark_read","notificationIds":["123"],"unreadCount":0}
```

## Backend Implementation Guide

### Python (FastAPI) Example:
```python
from fastapi import FastAPI, Request
from fastapi.responses import StreamingResponse
import json
import asyncio
from datetime import datetime
import uuid

app = FastAPI()

# Store active connections
active_connections = {}

@app.get("/notifications/stream")
async def notification_stream(request: Request):
    user_id = get_user_id_from_request(request)  # Implement authentication
    
    async def event_generator():
        # Add connection to active connections
        if user_id not in active_connections:
            active_connections[user_id] = []
        active_connections[user_id].append(request)
        
        try:
            # Send initial connection message
            yield f"event: connected\ndata: {json.dumps({'userId': user_id, 'timestamp': datetime.utcnow().isoformat()})}\n\n"
            
            # Send ping every 30 seconds
            while True:
                await asyncio.sleep(30)
                
                ping_data = {
                    "timestamp": datetime.utcnow().isoformat(),
                    "serverTime": datetime.utcnow().isoformat()
                }
                yield f"event: ping\ndata: {json.dumps(ping_data)}\n\n"
                
        except asyncio.CancelledError:
            # Client disconnected
            pass
        finally:
            # Remove connection from active connections
            if user_id in active_connections:
                active_connections[user_id].remove(request)
                if not active_connections[user_id]:
                    del active_connections[user_id]
    
    return StreamingResponse(
        event_generator(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Credentials": "true"
        }
    )

# Function to broadcast notification to specific user
async def broadcast_notification(user_id: str, notification_data: dict):
    if user_id in active_connections:
        message = f"event: notification\ndata: {json.dumps(notification_data)}\n\n"
        for connection in active_connections[user_id]:
            try:
                await connection.send_text(message)
            except:
                # Remove dead connection
                active_connections[user_id].remove(connection)
```

### Node.js (Express) Example:
```javascript
const express = require('express');
const app = express();

// Store active connections
const activeConnections = new Map();

app.get('/notifications/stream', (req, res) => {
    const userId = getUserIdFromRequest(req); // Implement authentication
    
    // Set SSE headers
    res.writeHead(200, {
        'Content-Type': 'text/event-stream',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Credentials': 'true'
    });
    
    // Add connection to active connections
    if (!activeConnections.has(userId)) {
        activeConnections.set(userId, []);
    }
    activeConnections.get(userId).push(res);
    
    // Send initial connection message
    res.write(`event: connected\ndata: ${JSON.stringify({
        userId: userId,
        timestamp: new Date().toISOString()
    })}\n\n`);
    
    // Send ping every 30 seconds
    const pingInterval = setInterval(() => {
        const pingData = {
            timestamp: new Date().toISOString(),
            serverTime: new Date().toISOString()
        };
        res.write(`event: ping\ndata: ${JSON.stringify(pingData)}\n\n`);
    }, 30000);
    
    // Handle client disconnect
    req.on('close', () => {
        clearInterval(pingInterval);
        const connections = activeConnections.get(userId);
        if (connections) {
            const index = connections.indexOf(res);
            if (index > -1) {
                connections.splice(index, 1);
            }
            if (connections.length === 0) {
                activeConnections.delete(userId);
            }
        }
    });
});

// Function to broadcast notification
function broadcastNotification(userId, notificationData) {
    const connections = activeConnections.get(userId);
    if (connections) {
        const message = `event: notification\ndata: ${JSON.stringify(notificationData)}\n\n`;
        connections.forEach(res => {
            res.write(message);
        });
    }
}
```

## Error Handling

### Connection Errors:
```json
{
  "error": "authentication_failed|rate_limited|server_error",
  "message": "Error description",
  "retry_after": 30
}
```

### Rate Limiting:
- Max 1 connection per user
- Max 1000 connections per server
- Reconnect delay: exponential backoff (1s, 2s, 4s, 8s, 16s)

## Security Considerations

### 1. **Authentication**
- Validate user session/token
- Check user permissions
- Rate limiting per user

### 2. **Data Validation**
- Validate all message data
- Sanitize user input
- Check message size limits

### 3. **Connection Management**
- Limit connections per user
- Cleanup dead connections
- Monitor connection health

## Testing

### Test Messages:
```bash
# Test connection
curl -N -H "Accept: text/event-stream" \
     -H "Cookie: session=your-session-cookie" \
     http://localhost:8000/notifications/stream

# Expected output:
event: connected
data: {"userId":"123","timestamp":"2024-01-15T10:30:00Z"}

event: ping
data: {"timestamp":"2024-01-15T10:30:00Z","serverTime":"2024-01-15T10:30:00Z"}
```

### Load Testing:
```bash
# Test with multiple connections
for i in {1..100}; do
  curl -N -H "Accept: text/event-stream" \
       -H "Cookie: session=session-$i" \
       http://localhost:8000/notifications/stream &
done
``` 