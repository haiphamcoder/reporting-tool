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

## 📁 Cấu trúc Files

```
.github/workflows/
├── deploy.yml              # Workflow triển khai chính
├── deploy-status.yml       # Kiểm tra trạng thái sau triển khai
├── pre-deploy-check.yml    # Kiểm tra code trước triển khai
└── test-connection.yml     # Test kết nối SSH

scripts/
├── deploy.sh               # Script triển khai nâng cao
└── test-connection.sh      # Script test kết nối

docs/
└── CICD_SETUP.md          # Hướng dẫn chi tiết cấu hình
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
cp .env.example .env
# Chỉnh sửa file .env
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
   - Kiểm tra code formatting
   - Validate Dockerfile syntax
   - Check for sensitive data

2. **Test Connection** (tự động)
   - Test SSH connection
   - Verify project access
   - Check Docker/Git installation

3. **Deployment** (tự động)
   - Backup current deployment
   - Stop old containers
   - Clean old images
   - Pull latest code
   - Build and start new containers
   - Health checks
   - Rollback if needed

4. **Status Check** (tự động)
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
- Sử dụng GitHub Secrets
- Validate environment variables

## 🚨 Troubleshooting

### Lỗi thường gặp

| Lỗi | Giải pháp |
|-----|-----------|
| SSH connection failed | Kiểm tra SSH key và firewall |
| Build failed | Kiểm tra Dockerfile và dependencies |
| Service unhealthy | Kiểm tra logs và configuration |
| Rollback failed | Kiểm tra backup và git history |

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

---

**Lưu ý**: Đảm bảo backup dữ liệu quan trọng trước khi triển khai lần đầu tiên. 