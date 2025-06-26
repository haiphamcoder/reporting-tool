# CI/CD Setup với GitHub Actions

Tài liệu này hướng dẫn cách cấu hình CI/CD pipeline để tự động triển khai ứng dụng lên Google Cloud VM khi có code mới được push vào nhánh `main`.

## Tổng quan

Hệ thống CI/CD bao gồm:
- **GitHub Actions Workflow**: Tự động trigger khi push vào nhánh `main`
- **Deployment Script**: Script triển khai nâng cao với khả năng rollback
- **Health Checks**: Kiểm tra trạng thái các service sau khi triển khai
- **Status Monitoring**: Theo dõi và báo cáo trạng thái triển khai
- **Environment Management**: Quản lý biến môi trường cho CI/CD và production
- **Dependency Management**: Quản lý dependencies cho frontend và backend

## Cấu hình GitHub Secrets

Để GitHub Actions có thể kết nối đến Google Cloud VM, bạn cần cấu hình các secrets sau trong repository:

### 1. Truy cập GitHub Repository Settings
- Vào repository trên GitHub
- Chọn tab **Settings**
- Chọn **Secrets and variables** → **Actions**

### 2. Thêm các Secrets sau:

| Secret Name | Mô tả | Ví dụ |
|-------------|-------|-------|
| `HOST` | IP address của Google Cloud VM | `34.123.45.67` |
| `USERNAME` | Username để SSH vào VM | `ubuntu` hoặc `root` |
| `SSH_PRIVATE_KEY` | Private key để SSH authentication | Nội dung file private key |
| `PORT` | SSH port (thường là 22) | `22` |
| `PROJECT_PATH` | Đường dẫn đến project trên VM | `/home/ubuntu/reporting-tool` |

### 3. Tạo SSH Key Pair

Nếu chưa có SSH key, tạo mới:

```bash
# Tạo SSH key pair
ssh-keygen -t rsa -b 4096 -C "your-email@example.com" -f ~/.ssh/github_actions

# Copy public key lên VM
ssh-copy-id -i ~/.ssh/github_actions.pub username@your-vm-ip

# Hoặc thêm public key vào ~/.ssh/authorized_keys trên VM
cat ~/.ssh/github_actions.pub >> ~/.ssh/authorized_keys
```

### 4. Cấu hình SSH Key trong GitHub

- Copy nội dung file private key: `cat ~/.ssh/github_actions`
- Thêm vào GitHub Secret `SSH_PRIVATE_KEY`

## Cấu hình trên Google Cloud VM

### 1. Cài đặt Docker và Docker Compose

```bash
# Cài đặt Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Cài đặt Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Thêm user vào docker group
sudo usermod -aG docker $USER
```

### 2. Clone Repository

```bash
# Clone repository
git clone https://github.com/your-username/reporting-tool.git
cd reporting-tool

# Tạo file .env từ template
cp env.ci.example .env
# Chỉnh sửa các biến môi trường trong file .env với giá trị thật
```

### 3. Cấu hình Environment Variables

#### File env.ci.example
File này chứa các biến môi trường dummy cho CI/CD testing:
- **KHÔNG** chứa thông tin nhạy cảm thật
- Chỉ dùng để kiểm tra syntax của docker-compose
- Được commit vào repository

#### File .env (Production)
File này chứa các biến môi trường thật cho production:
- **CHỨA** thông tin nhạy cảm thật
- **KHÔNG** được commit vào repository
- Được tạo từ `env.ci.example` và chỉnh sửa

```bash
# Tạo file .env từ template
cp env.ci.example .env

# Chỉnh sửa file .env với giá trị thật
nano .env
```

### 4. Cấu hình Frontend Dependencies

#### File package-lock.json
File này được tạo tự động khi chạy `npm install`:
- **ĐƯỢC** commit vào repository để đảm bảo dependency versions
- Đảm bảo build consistency giữa các environments
- Nếu file bị thiếu, chạy lệnh sau để tạo:

```bash
cd frontend
npm install --package-lock-only
```

### 5. Cấu hình Firewall

```bash
# Mở các port cần thiết
sudo ufw allow 22    # SSH
sudo ufw allow 80    # Frontend
sudo ufw allow 8761  # Eureka Discovery Server
sudo ufw allow 8765  # API Gateway
sudo ufw allow 8091  # Authentication Service
sudo ufw allow 8092  # User Management Service
sudo ufw allow 8093  # Reporting Service
sudo ufw allow 8094  # Data Processing Service
sudo ufw allow 8095  # Integrated Service
```

## Workflow Files

### 1. `.github/workflows/deploy.yml`
Workflow chính để triển khai ứng dụng:
- Trigger khi push vào nhánh `main`
- Sử dụng SSH để kết nối đến VM
- Chạy script triển khai nâng cao

### 2. `.github/workflows/deploy-status.yml`
Workflow kiểm tra trạng thái triển khai:
- Chạy sau khi deploy thành công
- Kiểm tra health của các service
- Hiển thị logs và trạng thái

### 3. `.github/workflows/pre-deploy-check.yml`
Workflow kiểm tra code trước triển khai:
- Kiểm tra cấu trúc file
- Validate Dockerfile syntax
- Check for sensitive data
- Sử dụng `env.ci.example` cho docker-compose check
- Verify required files exist (bao gồm package-lock.json)

### 4. `.github/workflows/frontend-check.yml`
Workflow kiểm tra frontend riêng biệt:
- Chỉ chạy khi có thay đổi trong `frontend/`
- Kiểm tra dependencies và build

### 5. `scripts/deploy.sh`
Script triển khai nâng cao với các tính năng:
- Backup deployment hiện tại
- Rollback tự động nếu có lỗi
- Health checks cho các service
- Logging chi tiết
- Tự động sử dụng `env.ci.example` nếu không có `.env`

## Quy trình Triển khai

Khi có code mới được push vào nhánh `main`:

1. **Pre-deploy Check** (tự động)
   - Kiểm tra cấu trúc file
   - Validate Dockerfile syntax
   - Check for sensitive data
   - Sử dụng `env.ci.example` cho docker-compose check
   - Verify required files exist

2. **Frontend Check** (tự động, chỉ khi có thay đổi frontend)
   - Install dependencies
   - Code linting
   - Build test

3. **Test Connection** (tự động)
   - Test SSH connection
   - Verify project access
   - Check Docker/Git installation
   - Test environment file và docker-compose syntax

4. **Deployment** (tự động)
   - Backup current deployment
   - Stop old containers
   - Clean old images
   - Pull latest code
   - Build and start new containers
   - Health checks
   - Rollback if needed

5. **Status Check** (tự động)
   - Verify all services are healthy
   - Display deployment logs
   - Report final status

## Sử dụng Script Triển khai

Script `deploy.sh` hỗ trợ các lệnh sau:

```bash
# Triển khai ứng dụng
./scripts/deploy.sh deploy

# Rollback về phiên bản trước
./scripts/deploy.sh rollback

# Kiểm tra trạng thái
./scripts/deploy.sh status

# Xem logs
./scripts/deploy.sh logs
```

## Environment Variables Management

### Các biến môi trường cần thiết:

| Variable | Mô tả | Ví dụ |
|----------|-------|-------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `your_secure_password` |
| `MYSQL_USER` | MySQL user | `reporting_user` |
| `MYSQL_PASSWORD` | MySQL password | `your_secure_password` |
| `MYSQL_DATABASE` | MySQL database name | `reporting_db` |
| `JWT_SECRET_KEY` | JWT secret key | `your_jwt_secret_key` |
| `OAUTH2_GOOGLE_CLIENT_ID` | Google OAuth2 client ID | `your_google_client_id` |
| `OAUTH2_GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret | `your_google_client_secret` |
| `OAUTH2_GOOGLE_REDIRECT_URI` | Google OAuth2 redirect URI | `http://your-domain.com/oauth2/callback` |
| `TELEGRAM_BOT_TOKEN` | Telegram bot token | `your_telegram_bot_token` |
| `TELEGRAM_BOT_USERNAME` | Telegram bot username | `your_bot_username` |
| `TIDB_USERNAME` | TiDB username | `tidb_user` |
| `TIDB_PASSWORD` | TiDB password | `your_tidb_password` |

### Tạo file .env cho production:

```bash
# Copy template
cp env.ci.example .env

# Chỉnh sửa với giá trị thật
nano .env
```

## Dependency Management

### Frontend Dependencies
- **package.json**: Định nghĩa dependencies và scripts
- **package-lock.json**: Lock exact versions của dependencies
- **node_modules/**: Thư mục chứa installed packages (không commit)

### Backend Dependencies
- **pom.xml**: Maven dependencies cho Java services
- **target/**: Build output directory (không commit)

### Quản lý Dependencies

```bash
# Frontend - Cập nhật dependencies
cd frontend
npm install
npm update

# Backend - Cập nhật dependencies
cd [service-directory]
mvn clean install
```

## Monitoring và Troubleshooting

### 1. Xem Logs Triển khai

```bash
# Xem logs của script triển khai
tail -f /tmp/deployment.log

# Xem logs của containers
docker compose -f docker-compose.prod.yml logs -f
```

### 2. Kiểm tra Trạng thái Services

```bash
# Kiểm tra containers
docker compose -f docker-compose.prod.yml ps

# Kiểm tra health endpoints
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:8765/actuator/health  # API Gateway
curl http://localhost:80                     # Frontend
```

### 3. Troubleshooting

- **SSH Connection Failed**: Kiểm tra SSH key và firewall settings
- **Build Failed**: Kiểm tra Dockerfile và dependencies
- **Service Unhealthy**: Kiểm tra logs và configuration
- **Rollback Failed**: Kiểm tra backup và git history
- **Environment Variables Missing**: Đảm bảo file .env tồn tại và có đầy đủ biến
- **Package-lock.json Missing**: Chạy `npm install --package-lock-only` trong frontend directory

## Bảo mật

### 1. SSH Key Security
- Sử dụng SSH key riêng cho GitHub Actions
- Không sử dụng key cá nhân
- Rotate key định kỳ

### 2. Environment Variables
- Không commit file `.env` vào repository
- Sử dụng `env.ci.example` cho CI/CD testing
- Sử dụng GitHub Secrets cho sensitive data
- Validate environment variables trước khi deploy

### 3. Access Control
- Giới hạn quyền truy cập SSH
- Sử dụng non-root user khi có thể
- Monitor SSH access logs

### 4. Dependency Security
- Regular security audits với `npm audit`
- Update dependencies định kỳ
- Monitor for known vulnerabilities

## Tối ưu hóa

### 1. Build Cache
- Sử dụng Docker layer caching
- Optimize Dockerfile để giảm build time
- Sử dụng multi-stage builds

### 2. Parallel Deployment
- Deploy các service độc lập song song
- Sử dụng health checks để đảm bảo dependency order

### 3. Monitoring
- Setup alerts cho deployment failures
- Monitor resource usage
- Track deployment metrics

## Liên hệ

Nếu có vấn đề với CI/CD setup, vui lòng:
1. Kiểm tra logs trong GitHub Actions
2. Xem deployment logs trên VM
3. Tạo issue với thông tin chi tiết về lỗi 