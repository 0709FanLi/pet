#!/bin/bash

# IP地址更新脚本
# 用于快速更换项目中的IP地址配置

if [ $# -eq 0 ]; then
    echo "Usage: $0 <new-ip>"
    echo "Example: $0 192.168.1.10"
    exit 1
fi

NEW_IP="$1"
OLD_IP_PATTERN="192\.168\.1\.[0-9]+"

echo "🔄 正在更新IP地址配置..."
echo "   新IP: $NEW_IP"
echo

# 定义需要更新的文件列表
declare -a files=(
    "../fpet/common/config.ts"
    "../fpet/common/request.ts"
    "../manage-web/src/views/audit/Pending.vue"
    "../manage-web/src/views/audit/Detail.vue"
    "../manage-web/src/views/audit/History.vue"
    "../manage-web/src/views/detectives/List.vue"
    "../manage-web/src/views/detectives/Detail.vue"
    "../manage-web/src/views/detectives/Rejected.vue"
)

# 更新文件
for file in "${files[@]}"; do
    if [ -f "$file" ]; then
        echo "📝 更新文件: $file"
        sed -i.bak "s/$OLD_IP_PATTERN/$NEW_IP/g" "$file"
        rm -f "$file.bak"
    else
        echo "⚠️  文件不存在: $file"
    fi
done

echo
echo "✅ IP地址更新完成！"
echo "📱 重新启动前端应用以生效:"
echo "   cd fpet && npm run dev:h5"
echo "   cd manage-web && npm run dev"
echo
