# Hệ thống Thông báo (Notification System)

## Tổng quan

Hệ thống thông báo được thiết kế để cung cấp thông tin real-time cho người dùng về các hoạt động quan trọng trong ứng dụng reporting tool.

## Các loại thông báo

### 1. **Reports** 📊
- **Tạo report thành công**: Thông báo khi report mới được tạo
- **Cập nhật report**: Thông báo khi report được cập nhật
- **Xóa report**: Thông báo khi report bị xóa
- **Export report**: Thông báo khi report được export thành công/thất bại

### 2. **Charts** 📈
- **Tạo chart thành công**: Thông báo khi chart mới được tạo
- **Cập nhật chart**: Thông báo khi chart được cập nhật
- **Xóa chart**: Thông báo khi chart bị xóa
- **Lỗi chart**: Thông báo khi có lỗi trong quá trình tạo/cập nhật chart

### 3. **Sources** 🔗
- **Thêm source thành công**: Thông báo khi source mới được thêm
- **Cập nhật source**: Thông báo khi source được cập nhật
- **Xóa source**: Thông báo khi source bị xóa
- **Lỗi kết nối**: Thông báo khi có vấn đề với kết nối database
- **Refresh data**: Thông báo khi data được refresh thành công

### 4. **Users** 👥
- **Thêm user mới**: Thông báo khi user mới được thêm vào hệ thống
- **Cập nhật user**: Thông báo khi thông tin user được cập nhật
- **Xóa user**: Thông báo khi user bị xóa
- **Thay đổi quyền**: Thông báo khi quyền truy cập của user thay đổi

### 5. **System** ⚙️
- **Maintenance**: Thông báo về bảo trì hệ thống
- **System updates**: Thông báo về cập nhật hệ thống
- **Security alerts**: Thông báo về vấn đề bảo mật
- **Performance warnings**: Thông báo về hiệu suất hệ thống

## Cấu trúc dữ liệu

```typescript
interface Notification {
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
```

## Các tính năng chính

### 1. **Badge Counter**
- Hiển thị số lượng thông báo chưa đọc
- Cập nhật real-time khi có thông báo mới
- Hiển thị ở cả desktop và mobile

### 2. **Notification Dialog**
- Modal popup hiển thị danh sách thông báo
- Phân loại theo categories (All, Reports, Charts, Sources, Users, System)
- Hỗ trợ mark as read từng thông báo hoặc tất cả
- Hỗ trợ xóa tất cả thông báo

### 3. **Real-time Updates**
- Context API để quản lý state globally
- Tự động refresh unread count
- Cập nhật UI real-time khi có thay đổi

### 4. **API Integration**
- RESTful API endpoints cho CRUD operations
- Pagination support
- Error handling
- Authentication required

## API Endpoints

### GET `/notifications`
- Lấy danh sách thông báo
- Query params: `page`, `limit`
- Response: `{ success, result: { notifications, total, unread_count } }`

### POST `/notifications/mark-read`
- Đánh dấu thông báo đã đọc
- Body: `{ notification_ids: string[] }`

### POST `/notifications/mark-all-read`
- Đánh dấu tất cả thông báo đã đọc

### DELETE `/notifications/:id`
- Xóa thông báo cụ thể

### DELETE `/notifications/clear-all`
- Xóa tất cả thông báo

### GET `/notifications/unread-count`
- Lấy số lượng thông báo chưa đọc

### POST `/notifications`
- Tạo thông báo mới (admin only)

## Cách sử dụng

### 1. **Trong Components**
```typescript
import { useNotifications } from '../context/NotificationContext';

function MyComponent() {
  const { addNotification, unreadCount } = useNotifications();
  
  const handleSuccess = () => {
    addNotification({
      type: 'success',
      category: 'report',
      title: 'Report Created',
      message: 'Your report has been created successfully'
    });
  };
}
```

### 2. **Tích hợp với các actions**
```typescript
// Trong Charts.tsx, Reports.tsx, Sources.tsx
const handleCreateSuccess = () => {
  addNotification({
    type: 'success',
    category: 'chart', // hoặc 'report', 'source'
    title: 'Chart Created',
    message: `Chart "${chartName}" has been created successfully`
  });
};
```

## UI/UX Features

### 1. **Visual Indicators**
- Icons khác nhau cho từng loại thông báo
- Màu sắc phân biệt (success: green, error: red, warning: orange, info: blue)
- Badge với số lượng unread
- Hover effects

### 2. **Responsive Design**
- Hoạt động tốt trên desktop và mobile
- Dialog responsive với max-width và max-height
- Tabs scrollable trên mobile

### 3. **Accessibility**
- ARIA labels
- Keyboard navigation
- Screen reader support
- Focus management

## Future Enhancements

### 1. **Push Notifications**
- Browser push notifications
- Email notifications
- Mobile push notifications

### 2. **Advanced Features**
- Notification preferences
- Custom notification sounds
- Notification history
- Export notifications

### 3. **Real-time Features**
- WebSocket integration
- Live updates
- Notification sounds
- Desktop notifications

## Testing

### 1. **Unit Tests**
- API functions
- Context functions
- Component rendering

### 2. **Integration Tests**
- API integration
- Context integration
- User interactions

### 3. **E2E Tests**
- Complete notification flow
- Cross-browser testing
- Mobile testing 