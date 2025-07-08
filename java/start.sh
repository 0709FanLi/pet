#!/bin/bash

# 宠物找回平台启动脚本

echo "=== 宠物找回平台后端服务启动脚本 ==="
echo

# 检查Java环境
echo "1. 检查Java环境..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo "✅ Java已安装: $JAVA_VERSION"
else
    echo "❌ Java未安装，请先安装JDK 11或更高版本"
    echo "下载地址: https://adoptium.net/"
    exit 1
fi

# 检查Maven环境
echo "2. 检查Maven环境..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo "✅ Maven已安装: $MVN_VERSION"
else
    echo "❌ Maven未安装，请先安装Maven"
    echo "安装命令: brew install maven"
    echo "或参考 INSTALL.md 文件进行手动安装"
    exit 1
fi

# 检查MySQL环境
echo "3. 检查MySQL环境..."
if command -v mysql &> /dev/null; then
    echo "✅ MySQL已安装"
    
    # 检查MySQL服务是否运行
    if pgrep -x "mysqld" > /dev/null; then
        echo "✅ MySQL服务正在运行"
    else
        echo "⚠️  MySQL服务未运行，尝试启动..."
        brew services start mysql 2>/dev/null || {
            echo "❌ 无法启动MySQL服务，请手动启动"
            echo "启动命令: brew services start mysql"
            exit 1
        }
        sleep 3
        echo "✅ MySQL服务已启动"
    fi
else
    echo "❌ MySQL未安装，请先安装MySQL"
    echo "安装命令: brew install mysql"
    exit 1
fi

# 检查数据库是否存在
echo "4. 检查数据库..."
DB_EXISTS=$(mysql -u root -e "SHOW DATABASES LIKE 'pet_recovery';" 2>/dev/null | grep pet_recovery || echo "")
if [ -z "$DB_EXISTS" ]; then
    echo "⚠️  数据库 pet_recovery 不存在，正在创建..."
    mysql -u root -e "CREATE DATABASE pet_recovery CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null || {
        echo "❌ 创建数据库失败，请手动创建数据库"
        echo "SQL命令: CREATE DATABASE pet_recovery CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
        exit 1
    }
    echo "✅ 数据库创建成功"
else
    echo "✅ 数据库 pet_recovery 已存在"
fi

# 编译项目
echo "5. 编译项目..."
echo "正在下载依赖并编译项目，请稍候..."
mvn clean compile -q || {
    echo "❌ 项目编译失败"
    exit 1
}
echo "✅ 项目编译成功"

# 启动项目
echo "6. 启动项目..."
echo "正在启动Spring Boot应用..."
echo "请等待应用完全启动后再进行测试"
echo
echo "启动完成后可访问:"
echo "- 测试接口: http://localhost:8080/api/users/test"
echo "- API文档: http://localhost:8080/swagger-ui.html"
echo
echo "按 Ctrl+C 停止服务"
echo "==========================================="
echo

mvn spring-boot:run