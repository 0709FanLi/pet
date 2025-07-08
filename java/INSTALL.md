# 环境安装指南

## 1. Java环境检查

首先检查Java是否已安装：
```bash
java -version
```

如果没有安装Java，请下载并安装JDK 11或更高版本：
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/
- OpenJDK: https://adoptium.net/

## 2. Maven安装

### 方法一：使用Homebrew（推荐）
```bash
# 更新Homebrew
brew update

# 安装Maven
brew install maven

# 验证安装
mvn -version
```

### 方法二：手动安装

1. 下载Maven：
   - 访问 https://maven.apache.org/download.cgi
   - 下载 apache-maven-3.9.x-bin.tar.gz

2. 解压并配置：
```bash
# 解压到/opt目录
sudo tar -xzf apache-maven-3.9.x-bin.tar.gz -C /opt

# 创建软链接
sudo ln -s /opt/apache-maven-3.9.x /opt/maven

# 配置环境变量（添加到 ~/.zshrc 或 ~/.bash_profile）
export MAVEN_HOME=/opt/maven
export PATH=$MAVEN_HOME/bin:$PATH

# 重新加载配置
source ~/.zshrc

# 验证安装
mvn -version
```

## 3. MySQL安装

### 使用Homebrew安装MySQL：
```bash
# 安装MySQL
brew install mysql

# 启动MySQL服务
brew services start mysql

# 安全配置（可选）
mysql_secure_installation
```

### 创建数据库：
```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE pet_recovery CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户（可选）
CREATE USER 'petuser'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON pet_recovery.* TO 'petuser'@'localhost';
FLUSH PRIVILEGES;

# 退出
exit;
```

## 4. 配置项目

1. 修改数据库配置文件 `src/main/resources/application.properties`：
```properties
# 根据你的MySQL配置修改以下内容
spring.datasource.url=jdbc:mysql://localhost:3306/pet_recovery?useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password
```

## 5. 运行项目

```bash
# 进入项目目录
cd /Users/Zhuanz/Desktop/pet/java

# 清理并编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

## 6. 验证安装

项目启动后，访问以下URL验证：
- 测试接口：http://localhost:8080/api/users/test
- Swagger文档：http://localhost:8080/swagger-ui.html

## 常见问题解决

### Maven下载依赖慢
可以配置国内镜像，在 `~/.m2/settings.xml` 中添加：
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/central</url>
  </mirror>
</mirrors>
```

### 端口被占用
修改 `application.properties` 中的端口：
```properties
server.port=8081
```

### 数据库连接失败
1. 确保MySQL服务正在运行
2. 检查用户名密码是否正确
3. 确认数据库已创建