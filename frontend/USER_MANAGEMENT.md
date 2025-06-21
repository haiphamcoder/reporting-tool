# User Management Feature

## Tổng quan

Tính năng User Management cho phép admin quản lý tất cả user trong hệ thống. Chỉ những user có role "admin" mới có thể truy cập tính năng này.

## Cách truy cập

### 1. Từ Side Menu
- Đăng nhập với tài khoản admin
- Trong side menu sẽ xuất hiện thêm mục "Users" với icon People
- Click vào mục "Users" để truy cập

### 2. Từ Options Menu
- Click vào icon 3 chấm (⋮) ở góc trên bên phải
- Chọn "User Management" từ dropdown menu

## Các tính năng chính

### 1. Xem danh sách users
- Hiển thị danh sách tất cả users với thông tin:
  - Avatar và tên đầy đủ
  - Username
  - Email
  - Role (Admin/User)
  - Provider (local/google)
  - Trạng thái email verification
  - Trạng thái first login

### 2. Thêm user mới
- Click nút "Add User"
- Điền thông tin:
  - Username (bắt buộc)
  - Email (bắt buộc)
  - First Name (bắt buộc)
  - Last Name (bắt buộc)
  - Role: User hoặc Admin
  - Password (bắt buộc)

### 3. Chỉnh sửa user
- Click icon Edit (✏️) bên cạnh user
- Có thể thay đổi tất cả thông tin
- Password: để trống nếu không muốn thay đổi

### 4. Xóa user
- Click icon Delete (🗑️) bên cạnh user
- Xác nhận xóa trong dialog

### 5. Refresh danh sách
- Click nút "Refresh" để cập nhật danh sách

## Phân quyền

- **Admin**: Có thể truy cập và sử dụng tất cả tính năng
- **User thường**: Không thể truy cập, sẽ hiển thị thông báo "Access Denied"

## API Endpoints

Tính năng sử dụng các API endpoints sau:

- `GET /user-management/users` - Lấy danh sách users
- `POST /user-management/users` - Tạo user mới
- `PUT /user-management/users/:id` - Cập nhật user
- `DELETE /user-management/users/:id` - Xóa user

## Giao diện

### Responsive Design
- Tương thích với màn hình desktop và mobile
- Side menu có thể collapse để tiết kiệm không gian
- Table có pagination để xử lý danh sách lớn

### Visual Indicators
- Role được hiển thị bằng Chip với màu sắc khác nhau
- Admin: màu đỏ
- User: màu xanh
- Trạng thái email verification và first login cũng có màu sắc riêng

### Loading States
- Hiển thị loading spinner khi đang fetch data
- Disable buttons khi đang xử lý

## Error Handling

- Hiển thị error messages khi API call thất bại
- Success messages khi thao tác thành công
- Validation cho form inputs

## Security

- Chỉ admin mới có thể truy cập
- Tất cả API calls đều sử dụng credentials
- Xác thực quyền truy cập ở cả frontend và backend 