# 部署指南

## 部署概述

本文档介绍如何在不同环境中部署Z AI Agent项目，包括开发环境、测试环境和生产环境。

## 环境要求

### 系统要求

- **操作系统**: Linux (Ubuntu 20.04+), macOS, Windows 10+
- **Java**: OpenJDK 21+
- **Node.js**: 18.0+
- **数据库**: MySQL 8.0+ 或 PostgreSQL 13+
- **内存**: 最少4GB，推荐8GB+
- **存储**: 最少10GB可用空间

### 网络要求

- 能够访问阿里云DashScope API
- 能够访问Ollama服务（如果使用本地模型）
- 前端和后端服务之间的网络连通性

## 开发环境部署

### 1. 后端部署

```bash
# 1. 克隆项目
git clone <repository-url>
cd z-ai-agent

# 2. 配置数据库
mysql -u root -p
CREATE DATABASE z_ai_agent;
CREATE USER 'zaiagent'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON z_ai_agent.* TO 'zaiagent'@'localhost';
FLUSH PRIVILEGES;

# 3. 配置环境变量
export DASHSCOPE_API_KEY=your_api_key_here
export DB_USERNAME=zaiagent
export DB_PASSWORD=password

# 4. 启动应用
mvn spring-boot:run
```

### 2. 前端部署

```bash
# 1. 进入前端目录
cd z-ai-agent-frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev
```

### 3. 验证部署

- 后端服务: http://localhost:8123/api/health
- 前端应用: http://localhost:5173
- API文档: http://localhost:8123/api/swagger-ui.html

## 生产环境部署

### 1. 使用Docker部署

#### 创建Dockerfile

**后端Dockerfile**:
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/z-ai-agent-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8123

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**前端Dockerfile**:
```dockerfile
FROM node:18-alpine as build

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

#### Docker Compose配置

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: z_ai_agent
      MYSQL_USER: zaiagent
      MYSQL_PASSWORD: password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  postgres:
    image: pgvector/pgvector:pg15
    environment:
      POSTGRES_DB: z_ai_agent
      POSTGRES_USER: zaiagent
      POSTGRES_PASSWORD: password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: .
    ports:
      - "8123:8123"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DASHSCOPE_API_KEY: ${DASHSCOPE_API_KEY}
      DB_USERNAME: zaiagent
      DB_PASSWORD: password
      DB_HOST: mysql
      PG_HOST: postgres
    depends_on:
      - mysql
      - postgres

  frontend:
    build: ./z-ai-agent-frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
  postgres_data:
```

#### 部署命令

```bash
# 构建和启动服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f backend
```

### 2. 传统部署方式

#### 后端部署

```bash
# 1. 构建应用
mvn clean package -DskipTests

# 2. 创建部署目录
mkdir -p /opt/z-ai-agent
cp target/z-ai-agent-0.0.1-SNAPSHOT.jar /opt/z-ai-agent/

# 3. 创建配置文件
cat > /opt/z-ai-agent/application-prod.yml << EOF
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/z_ai_agent
    username: zaiagent
    password: password
  ai:
    alibaba:
      dashscope:
        api-key: ${DASHSCOPE_API_KEY}

server:
  port: 8123
  servlet:
    context-path: /api

logging:
  level:
    root: INFO
    com.zluolan.zaiagent: INFO
  file:
    name: /var/log/z-ai-agent/application.log
EOF

# 4. 创建systemd服务文件
sudo cat > /etc/systemd/system/z-ai-agent.service << EOF
[Unit]
Description=Z AI Agent Backend Service
After=network.target

[Service]
Type=simple
User=zaiagent
WorkingDirectory=/opt/z-ai-agent
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod z-ai-agent-0.0.1-SNAPSHOT.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
EOF

# 5. 启动服务
sudo systemctl daemon-reload
sudo systemctl enable z-ai-agent
sudo systemctl start z-ai-agent
```

#### 前端部署

```bash
# 1. 构建前端
cd z-ai-agent-frontend
npm run build

# 2. 部署到Nginx
sudo cp -r dist/* /var/www/html/

# 3. 配置Nginx
sudo cat > /etc/nginx/sites-available/z-ai-agent << EOF
server {
    listen 80;
    server_name your-domain.com;
    
    root /var/www/html;
    index index.html;
    
    location / {
        try_files \$uri \$uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8123;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    }
}
EOF

# 4. 启用站点
sudo ln -s /etc/nginx/sites-available/z-ai-agent /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## 云平台部署

### 1. AWS部署

#### 使用ECS + RDS

```yaml
# docker-compose.yml for ECS
version: '3.8'
services:
  backend:
    image: your-account.dkr.ecr.region.amazonaws.com/z-ai-agent:latest
    environment:
      - SPRING_PROFILES_ACTIVE=aws
      - DASHSCOPE_API_KEY=${DASHSCOPE_API_KEY}
      - DB_HOST=${RDS_ENDPOINT}
    ports:
      - "8123:8123"
```

#### 使用Elastic Beanstalk

```bash
# 1. 安装EB CLI
pip install awsebcli

# 2. 初始化EB应用
eb init z-ai-agent

# 3. 创建环境
eb create production

# 4. 部署应用
eb deploy
```

### 2. 阿里云部署

#### 使用容器服务ACK

```yaml
# k8s-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: z-ai-agent-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: z-ai-agent-backend
  template:
    metadata:
      labels:
        app: z-ai-agent-backend
    spec:
      containers:
      - name: backend
        image: registry.cn-hangzhou.aliyuncs.com/your-namespace/z-ai-agent:latest
        ports:
        - containerPort: 8123
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "k8s"
        - name: DASHSCOPE_API_KEY
          valueFrom:
            secretKeyRef:
              name: ai-secrets
              key: dashscope-api-key
---
apiVersion: v1
kind: Service
metadata:
  name: z-ai-agent-backend-service
spec:
  selector:
    app: z-ai-agent-backend
  ports:
  - port: 80
    targetPort: 8123
  type: LoadBalancer
```

### 3. 腾讯云部署

#### 使用TKE

```bash
# 1. 创建集群
tke cluster create --name z-ai-agent-cluster

# 2. 部署应用
kubectl apply -f k8s-deployment.yaml

# 3. 配置负载均衡
kubectl expose deployment z-ai-agent-backend --type=LoadBalancer
```

## 数据库部署

### 1. MySQL部署

```bash
# 使用Docker部署MySQL
docker run -d \
  --name mysql \
  -e MYSQL_ROOT_PASSWORD=rootpassword \
  -e MYSQL_DATABASE=z_ai_agent \
  -e MYSQL_USER=zaiagent \
  -e MYSQL_PASSWORD=password \
  -p 3306:3306 \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0

# 初始化数据库
mysql -h localhost -u root -p < src/main/resources/sql/schema.sql
```

### 2. PostgreSQL部署

```bash
# 使用Docker部署PostgreSQL with pgvector
docker run -d \
  --name postgres \
  -e POSTGRES_DB=z_ai_agent \
  -e POSTGRES_USER=zaiagent \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  pgvector/pgvector:pg15

# 创建向量扩展
psql -h localhost -U zaiagent -d z_ai_agent -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

## 监控和日志

### 1. 应用监控

```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'z-ai-agent'
    static_configs:
      - targets: ['localhost:8123']
    metrics_path: '/actuator/prometheus'
```

### 2. 日志收集

```yaml
# filebeat.yml
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /var/log/z-ai-agent/*.log
  fields:
    service: z-ai-agent
    environment: production

output.elasticsearch:
  hosts: ["elasticsearch:9200"]
```

### 3. 健康检查

```bash
# 检查服务状态
curl http://localhost:8123/api/health

# 检查数据库连接
curl http://localhost:8123/api/health/db

# 检查AI服务连接
curl http://localhost:8123/api/health/ai
```

## 安全配置

### 1. 网络安全

```bash
# 配置防火墙
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable

# 配置SSL证书
sudo certbot --nginx -d your-domain.com
```

### 2. 应用安全

```yaml
# application-prod.yml
spring:
  security:
    user:
      name: admin
      password: ${ADMIN_PASSWORD}
  datasource:
    url: jdbc:mysql://localhost:3306/z_ai_agent?useSSL=true&serverTimezone=UTC
```

### 3. 数据加密

```bash
# 加密敏感配置
echo "your-api-key" | openssl enc -aes-256-cbc -base64

# 在应用中使用
export DASHSCOPE_API_KEY=$(echo "encrypted-key" | openssl enc -aes-256-cbc -d -base64)
```

## 备份和恢复

### 1. 数据库备份

```bash
# MySQL备份
mysqldump -u zaiagent -p z_ai_agent > backup_$(date +%Y%m%d).sql

# PostgreSQL备份
pg_dump -h localhost -U zaiagent z_ai_agent > backup_$(date +%Y%m%d).sql
```

### 2. 应用备份

```bash
# 备份应用文件
tar -czf z-ai-agent-backup-$(date +%Y%m%d).tar.gz /opt/z-ai-agent/

# 备份配置文件
cp -r /etc/z-ai-agent/ /backup/config/
```

### 3. 恢复流程

```bash
# 恢复数据库
mysql -u zaiagent -p z_ai_agent < backup_20240101.sql

# 恢复应用
tar -xzf z-ai-agent-backup-20240101.tar.gz -C /
sudo systemctl restart z-ai-agent
```

## 故障排除

### 1. 常见问题

**问题**: 服务启动失败
```bash
# 检查日志
sudo journalctl -u z-ai-agent -f

# 检查端口占用
sudo netstat -tlnp | grep 8123
```

**问题**: 数据库连接失败
```bash
# 检查数据库状态
sudo systemctl status mysql

# 测试连接
mysql -h localhost -u zaiagent -p
```

**问题**: AI服务调用失败
```bash
# 检查API Key
echo $DASHSCOPE_API_KEY

# 测试网络连接
curl -I https://dashscope.aliyuncs.com
```

### 2. 性能调优

```bash
# JVM参数调优
java -Xms2g -Xmx4g -XX:+UseG1GC -jar app.jar

# 数据库连接池调优
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

## 更新和维护

### 1. 应用更新

```bash
# 停止服务
sudo systemctl stop z-ai-agent

# 备份当前版本
cp z-ai-agent-0.0.1-SNAPSHOT.jar z-ai-agent-backup.jar

# 部署新版本
cp new-version.jar z-ai-agent-0.0.1-SNAPSHOT.jar

# 启动服务
sudo systemctl start z-ai-agent
```

### 2. 数据库迁移

```bash
# 执行数据库迁移脚本
mysql -u zaiagent -p z_ai_agent < migration.sql
```

### 3. 配置更新

```bash
# 更新配置文件
sudo nano /opt/z-ai-agent/application-prod.yml

# 重启服务
sudo systemctl restart z-ai-agent
```
