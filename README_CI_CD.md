# CI/CD Pipeline cho Reporting Tool

Hệ thống CI/CD tự động triển khai ứng dụng lên Google Cloud VM khi có code mới được push vào nhánh `main`.

## 🚀 Tính năng

- ✅ **Tự động triển khai** khi push vào nhánh `main`
- ✅ **Rollback tự động** nếu triển khai thất bại
- ✅ **Health checks** cho tất cả services
- ✅ **Backup deployment** trước khi cập nhật
- ✅ **Test kết nối** trước khi triển khai
- ✅ **Monitoring và logging** chi tiết
- ✅ **Bảo mật** với SSH keys và secrets
- ✅ **Frontend checks riêng biệt** để tránh xung đột
- ✅ **Environment management** thông minh
- ✅ **Dependency management** cho frontend và backend

## 📁 Cấu trúc Files

```
.github/workflows/
├── deploy.yml              # Workflow triển khai chính
├── deploy-status.yml       # Kiểm tra trạng thái sau triển khai
├── pre-deploy-check.yml    # Kiểm tra code cơ bản trước triển khai
├── frontend-check.yml      # Kiểm tra frontend riêng biệt
└── test-connection.yml     # Test kết nối SSH

scripts/
├── deploy.sh               # Script triển khai nâng cao
└── test-connection.sh      # Script test kết nối

docs/
└── CICD_SETUP.md          # Hướng dẫn chi tiết cấu hình

env.ci.example             # Template biến môi trường cho CI/CD
frontend/
├── package.json           # Frontend dependencies
└── package-lock.json      # Locked dependency versions
```

## ⚡ Quick Start

### 1. Cấu hình GitHub Secrets

Vào **Repository Settings** → **Secrets and variables** → **Actions** và thêm:

| Secret | Mô tả |
|--------|-------|
| `HOST` | IP của Google Cloud VM |
| `USERNAME` | Username SSH (thường là `ubuntu`) |
| `SSH_PRIVATE_KEY` | Private SSH key |
| `PORT` | SSH port (thường là `22`) |
| `PROJECT_PATH` | Đường dẫn project trên VM |

### 2. Cấu hình Google Cloud VM

```bash
# Cài đặt Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Cài đặt Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Clone repository
git clone https://github.com/your-username/reporting-tool.git
cd reporting-tool

# Cấu hình environment
cp env.ci.example .env
# Chỉnh sửa file .env với giá trị thật
nano .env
```

### 3. Test Kết nối

```bash
# Chạy workflow test kết nối
# Vào GitHub Actions → Test Connection → Run workflow
```

### 4. Triển khai

```bash
# Push code vào nhánh main để trigger deployment
git push origin main
```

## 🔧 Quy trình Triển khai

1. **Pre-deploy Check** (tự động)
   - Kiểm tra cấu trúc file
   - Validate Dockerfile syntax
   - Check for sensitive data
   - Verify file permissions
   - Sử dụng `env.ci.example` cho docker-compose check
   - Verify required files exist (bao gồm package-lock.json)

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

## 🛠️ Sử dụng Scripts

### Script Triển khai

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

### Script Test Kết nối

```bash
# Test kết nối SSH
export HOST="your-vm-ip"
export USERNAME="ubuntu"
export SSH_KEY="your-private-key"
export PROJECT_PATH="/home/ubuntu/reporting-tool"

./scripts/test-connection.sh
```

## 🌍 Environment Management

### File env.ci.example
- **Template** cho CI/CD testing
- **KHÔNG** chứa thông tin nhạy cảm thật
- Được commit vào repository
- Dùng để kiểm tra docker-compose syntax

### File .env (Production)
- **CHỨA** thông tin nhạy cảm thật
- **KHÔNG** được commit vào repository
- Tạo từ `env.ci.example` và chỉnh sửa

### Các biến môi trường cần thiết:

| Variable | Mô tả |
|----------|-------|
| `MYSQL_ROOT_PASSWORD` | MySQL root password |
| `MYSQL_USER` | MySQL user |
| `MYSQL_PASSWORD` | MySQL password |
| `MYSQL_DATABASE` | MySQL database name |
| `JWT_SECRET_KEY` | JWT secret key |
| `OAUTH2_GOOGLE_CLIENT_ID` | Google OAuth2 client ID |
| `OAUTH2_GOOGLE_CLIENT_SECRET` | Google OAuth2 client secret |
| `OAUTH2_GOOGLE_REDIRECT_URI` | Google OAuth2 redirect URI |
| `TELEGRAM_BOT_TOKEN` | Telegram bot token |
| `TELEGRAM_BOT_USERNAME` | Telegram bot username |
| `TIDB_USERNAME` | TiDB username |
| `TIDB_PASSWORD` | TiDB password |

## 📦 Dependency Management

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

## 📊 Monitoring

### Xem Logs Triển khai

```bash
# Logs script triển khai
tail -f /tmp/deployment.log

# Logs containers
docker compose -f docker-compose.prod.yml logs -f
```

### Health Checks

```bash
# Kiểm tra services
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:8765/actuator/health  # API Gateway
curl http://localhost:80                     # Frontend
```

## 🔒 Bảo mật

### SSH Key Security
- Sử dụng SSH key riêng cho GitHub Actions
- Không commit SSH keys vào repository
- Rotate keys định kỳ

### Environment Variables
- Không commit file `.env`
- Sử dụng `env.ci.example` cho CI/CD testing
- Sử dụng GitHub Secrets
- Validate environment variables

### Dependency Security
- Regular security audits với `npm audit`
- Update dependencies định kỳ
- Monitor for known vulnerabilities

## 🚨 Troubleshooting

### Lỗi thường gặp

| Lỗi | Giải pháp |
|-----|-----------|
| SSH connection failed | Kiểm tra SSH key và firewall |
| Build failed | Kiểm tra Dockerfile và dependencies |
| Service unhealthy | Kiểm tra logs và configuration |
| Rollback failed | Kiểm tra backup và git history |
| NPM cache error | Frontend checks được tách riêng |
| Environment variables missing | Đảm bảo file .env tồn tại |
| Package-lock.json missing | Chạy `npm install --package-lock-only` |

### Debug Commands

```bash
# Kiểm tra containers
docker compose -f docker-compose.prod.yml ps

# Kiểm tra logs
docker compose -f docker-compose.prod.yml logs [service-name]

# Kiểm tra network
docker network ls
docker network inspect reporting-tool-network

# Kiểm tra volumes
docker volume ls

# Kiểm tra environment
cat .env

# Kiểm tra frontend dependencies
cd frontend
npm list
```

## 📈 Tối ưu hóa

### Build Performance
- Sử dụng Docker layer caching
- Optimize Dockerfile
- Multi-stage builds

### Deployment Speed
- Parallel service deployment
- Optimized health checks
- Efficient rollback strategy

### Workflow Optimization
- Tách frontend checks riêng biệt
- Path-based triggers
- Conditional job execution
- Environment management thông minh
- Dependency version locking

## 📞 Hỗ trợ

Nếu gặp vấn đề:

1. Kiểm tra logs trong GitHub Actions
2. Xem deployment logs trên VM
3. Chạy test connection workflow
4. Tạo issue với thông tin chi tiết

## 📚 Tài liệu tham khảo

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [SSH Key Management](https://docs.github.com/en/authentication/connecting-to-github-with-ssh)
- [Google Cloud VM Setup](https://cloud.google.com/compute/docs/instances)
- [npm Documentation](https://docs.npmjs.com/)

---

**Lưu ý**: Đảm bảo backup dữ liệu quan trọng trước khi triển khai lần đầu tiên. 