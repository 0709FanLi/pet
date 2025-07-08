# 宠物找回平台后端API

## 项目简介

宠物找回平台是一个帮助宠物主人发布丢失信息和志愿者接单帮助寻找的平台。本项目是后端API服务，基于Spring Boot开发。

## 技术栈

- **Java 11+**
- **Spring Boot 2.7.x**
- **Spring Data JPA**
- **Spring Security**
- **MySQL 8.0**
- **JWT**
- **Swagger/OpenAPI 3**
- **Maven**

## 功能特性

- ✅ 用户注册/登录
- ✅ JWT身份认证
- ✅ 密码加密存储
- ✅ API文档自动生成
- ✅ 统一异常处理
- ✅ 日志记录
- ✅ 单元测试

## 快速开始

### 环境要求

- JDK 11 或更高版本
- Maven 3.6+
- MySQL 8.0+

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd pet-recovery-platform
   ```

2. **配置数据库**
   
   创建MySQL数据库：
   ```sql
   CREATE DATABASE pet_recovery CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
   
   修改 `src/main/resources/application.properties` 中的数据库配置：
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pet_recovery?useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **安装依赖**
   ```bash
   mvn clean install
   ```

4. **运行项目**
   ```bash
   mvn spring-boot:run
   ```

5. **访问应用**
   - API服务：http://localhost:8080
   - Swagger文档：http://localhost:8080/swagger-ui.html
   - API文档：http://localhost:8080/api-docs

## API接口

### 用户相关接口

#### 1. 测试接口
- **URL**: `GET /api/users/test`
- **描述**: 测试服务是否正常运行
- **响应**:
  ```json
  {
    "code": 200,
    "message": "服务运行正常",
    "data": "Pet Recovery Platform API v1.0"
  }
  ```

#### 2. 用户注册
- **URL**: `POST /api/users/register`
- **请求体**:
  ```json
  {
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "phoneNumber": "13800138000"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "注册成功",
    "data": {
      "user": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phoneNumber": "13800138000",
        "createdAt": "2024-01-01T10:00:00"
      },
      "token": "eyJhbGciOiJIUzUxMiJ9..."
    }
  }
  ```

#### 3. 用户登录
- **URL**: `POST /api/users/login`
- **请求体**:
  ```json
  {
    "username": "testuser",
    "password": "password123"
  }
  ```
- **响应**:
  ```json
  {
    "code": 200,
    "message": "登录成功",
    "data": {
      "user": {
        "id": 1,
        "username": "testuser",
        "email": "test@example.com",
        "phoneNumber": "13800138000",
        "createdAt": "2024-01-01T10:00:00"
      },
      "token": "eyJhbGciOiJIUzUxMiJ9..."
    }
  }
  ```

## 数据库设计

### users表
| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| username | VARCHAR(20) | 用户名 | UNIQUE, NOT NULL |
| password | VARCHAR(255) | 密码(加密) | NOT NULL |
| email | VARCHAR(255) | 邮箱 | UNIQUE |
| phone_number | VARCHAR(20) | 手机号 | |
| created_at | DATETIME | 创建时间 | |
| updated_at | DATETIME | 更新时间 | |

## 开发指南

### 运行测试
```bash
mvn test
```

### 打包部署
```bash
mvn clean package
java -jar target/pet-recovery-platform-0.0.1-SNAPSHOT.jar
```

### 代码规范
- 使用驼峰命名法
- 添加适当的注释
- 遵循RESTful API设计原则
- 统一返回格式：`{"code": 200, "message": "success", "data": {}}`

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库用户名密码正确
- 确认数据库已创建

### 2. 端口冲突
- 修改 `application.properties` 中的 `server.port` 配置

### 3. JWT Token过期
- 默认过期时间为24小时，可在配置文件中修改 `jwt.expiration`

## 后续开发计划

- [ ] 宠物信息管理
- [ ] 丢失信息发布
- [ ] 接单功能
- [ ] 图片上传
- [ ] 消息通知
- [ ] 地理位置服务

## 联系方式

如有问题，请联系开发团队。