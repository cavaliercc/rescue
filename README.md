# Rescue

前后端分离项目模板。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + TypeScript + Vite + Element Plus + Pinia + Tailwind CSS |
| 后端 | Java 17 + Spring Boot 3 + Spring Security + JWT + MyBatis-Plus |
| 数据库 | PostgreSQL |

## 目录结构

```
rescue/
├── frontend/          # Vue 3 前端
└── backend/           # Spring Boot 后端
```

## 启动步骤

### 1. 准备数据库

创建 PostgreSQL 数据库并建表：

```sql
CREATE DATABASE rescue;

\c rescue

CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    username   VARCHAR(50) UNIQUE NOT NULL,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100),
    status     INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- 插入测试用户（密码: admin123）
INSERT INTO users (username, password, email)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyBumqTYm', 'admin@rescue.com');
```

### 2. 启动后端

```bash
cd backend

# 配置数据库（可通过环境变量覆盖）
export DB_USERNAME=postgres
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key-at-least-256-bits-long

# 构建并运行
mvn clean package -DskipTests
java -jar target/rescue-backend-0.0.1-SNAPSHOT.jar
```

后端默认运行在 http://localhost:8080

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端默认运行在 http://localhost:5173

### 4. 构建前端（生产）

```bash
cd frontend
npm run build
# 产物在 frontend/dist/
```

## 接口说明

| 方法 | 路径 | 说明 | 是否需要认证 |
|---|---|---|---|
| POST | /api/auth/login | 用户登录 | 否 |
| GET | /api/health | 健康检查 | 否 |

### 登录示例

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

响应格式：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "eyJ...",
    "username": "admin"
  }
}
```
