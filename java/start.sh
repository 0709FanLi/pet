#!/bin/bash

# 宠物找回平台启动脚本
# 支持已有数据的数据库快速启动

echo "=== 宠物找回平台后端服务启动脚本 ==="
echo "🐾 支持完整的宠物找回生态系统："
echo "   • 后端API服务 (Spring Boot)"
echo "   • 移动端应用 (Vue3 + Vant)"  
echo "   • PC端应用 (Vue3 + Element Plus)"
echo "   • 管理后台 (Vue3 + Element Plus)"
echo "   • uni-app应用 (Vue3 + uView Plus)"
echo "   • 宠物侦探端 (Vue3 + 移动端优化)"
echo

# 检查已运行的进程
echo "0. 检查已运行的服务..."
EXISTING_JAVA=$(ps aux | grep "spring-boot:run\|PetRecoveryApplication" | grep -v grep | awk '{print $2}' | head -1)
if [ -n "$EXISTING_JAVA" ]; then
    echo "⚠️  检测到Spring Boot应用已在运行 (PID: $EXISTING_JAVA)"
    
    # 检查服务是否响应
    if curl -s -f "http://localhost:8080/api/users/test" > /dev/null 2>&1; then
        echo "✅ 服务正在正常运行"
        echo "🌐 API服务地址: http://localhost:8080"
        echo "📋 进程PID: $EXISTING_JAVA"
        echo "💡 如需重启服务，请先执行: kill $EXISTING_JAVA"
        exit 0
    else
        echo "⚠️  进程存在但服务无响应，将停止并重新启动"
        echo "正在停止无响应的进程..."
        kill $EXISTING_JAVA 2>/dev/null
        sleep 3
        echo "✅ 已停止无响应进程"
    fi
fi

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

# 检查数据库和数据
echo "4. 检查数据库和数据..."

# 更稳定的数据库检查方法
DB_CHECK=$(mysql -u root -e "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'pet_recovery';" 2>/dev/null | grep -c "pet_recovery" || echo "0")

if [ "$DB_CHECK" = "0" ]; then
    echo "⚠️  数据库 pet_recovery 不存在，正在创建..."
    mysql -u root -e "CREATE DATABASE pet_recovery CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null || {
        echo "❌ 创建数据库失败，请手动创建数据库"
        echo "SQL命令: CREATE DATABASE pet_recovery CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
        exit 1
    }
    echo "✅ 数据库创建成功"
else
    echo "✅ 数据库 pet_recovery 已存在"
    
    # 检查并显示数据统计
    echo "📊 数据库统计："
    
    # 检查用户表
    USER_COUNT=$(mysql -u root -D pet_recovery -e "SELECT COUNT(*) FROM users;" 2>/dev/null | tail -n 1 2>/dev/null || echo "0")
    echo "   👥 用户数量: $USER_COUNT"
    
    # 检查宠物启事表  
    PET_COUNT=$(mysql -u root -D pet_recovery -e "SELECT COUNT(*) FROM lost_pets;" 2>/dev/null | tail -n 1 2>/dev/null || echo "0")
    echo "   🐕 宠物启事: $PET_COUNT"
    
    # 检查侦探申请表
    DETECTIVE_COUNT=$(mysql -u root -D pet_recovery -e "SELECT COUNT(*) FROM detective_applications;" 2>/dev/null | tail -n 1 2>/dev/null || echo "0")
    echo "   🕵️ 侦探申请: $DETECTIVE_COUNT"
    
    # 检查侦探申请缓存表（新功能）
    CACHE_COUNT=$(mysql -u root -D pet_recovery -e "SELECT COUNT(*) FROM detective_application_cache;" 2>/dev/null | tail -n 1 2>/dev/null || echo "0")
    if [ "$CACHE_COUNT" != "0" ] || mysql -u root -D pet_recovery -e "SHOW TABLES LIKE 'detective_application_cache';" 2>/dev/null | grep -q "detective_application_cache"; then
        echo "   💾 侦探申请缓存: $CACHE_COUNT"
    fi
fi

# 编译项目
echo "5. 编译项目..."
echo "正在检查依赖并编译项目，请稍候..."

# 检查target目录
if [ -f "target/pet-recovery-1.0-SNAPSHOT.jar" ]; then
    echo "✅ 发现已编译的JAR文件，跳过编译"
else
    echo "   正在编译Spring Boot项目..."
    mvn clean compile -q || {
        echo "❌ 项目编译失败"
        exit 1
    }
    echo "✅ 项目编译成功"
fi

# 启动项目（后台）
echo "6. 启动项目..."
echo "📡 启动Spring Boot后端API服务..."

# 创建或清空日志文件
> spring-boot.log

# 启动方式选择
if [ -f "target/pet-recovery-1.0-SNAPSHOT.jar" ]; then
    echo "   使用JAR包方式启动..."
    nohup java -jar target/pet-recovery-1.0-SNAPSHOT.jar > spring-boot.log 2>&1 &
else
    echo "   使用Maven方式启动..."
    nohup mvn -q spring-boot:run > spring-boot.log 2>&1 &
fi

APP_PID=$!
echo "   后台进程PID: $APP_PID"
sleep 5

# 增强的健康检查
echo "7. 健康检查..."
RETRY=30
HEALTH_CHECK_URL="http://localhost:8080/api/users/test"

echo "   等待服务启动..."
while [ $RETRY -gt 0 ]; do
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_CHECK_URL 2>/dev/null)
    if [ "$HTTP_CODE" = "200" ]; then
        break
    fi
    
    RETRY=$((RETRY-1))
    if [ $RETRY -le 0 ]; then
        echo ""
        echo "❌ 应用启动检测超时"
        echo "💡 请检查日志文件: $(pwd)/spring-boot.log"
        echo "💡 常见问题："
        echo "   • 端口8080被占用"
        echo "   • 数据库连接失败"  
        echo "   • 配置文件错误"
        echo "📋 最近10行日志："
        tail -n 10 spring-boot.log 2>/dev/null || echo "   无法读取日志文件"
        echo "🔍 进程状态："
        ps aux | grep "spring-boot:run\|PetRecoveryApplication" | grep -v grep || echo "   未找到相关进程"
        exit 1
    fi
    printf "."
    sleep 1
done

echo ""
echo "✅ 🎉 宠物找回平台后端服务启动成功！"
echo
echo "🌐 API服务地址："
echo "   • 主接口: http://localhost:8080"
echo "   • 测试接口: http://localhost:8080/api/users/test"
echo "   • API文档: http://localhost:8080/swagger-ui.html"
echo
echo "🔧 配置接口："
echo "   • 宠物种类: http://localhost:8080/api/config/pet-types"
echo "   • 城市列表: http://localhost:8080/api/config/cities"
echo
echo "📱 前端应用："
echo "   • 移动端: mobile/ (Vue3 + Vant)"
echo "   • PC端: pc/ (Vue3 + Element Plus)"
echo "   • 管理后台: manage-web/ (Vue3 + Element Plus)"
echo "   • uni-app: fpet/ (Vue3 + uView Plus)"
echo "   • 宠物侦探端: detective-mobile/ (Vue3)"
echo
echo "📋 进程信息："
echo "   • 后台进程PID: $APP_PID"
echo "   • 日志文件: $(pwd)/spring-boot.log"
echo "   • 停止命令: kill $APP_PID"
echo
echo "💡 启动前端应用："
echo "   cd ../mobile && npm run dev      # 移动端"
echo "   cd ../pc && npm run dev          # PC端"  
echo "   cd ../manage-web && npm run dev  # 管理后台"
echo "   cd ../fpet && npm run dev:h5     # uni-app H5"
echo
echo "==========================================="
echo "🐾 宠物找回平台已就绪！"
echo "==========================================="
exit 0