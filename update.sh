#!/bin/bash

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}开始更新小新ESP32服务器...${NC}"

# 拉取最新代码
echo -e "${YELLOW}拉取最新代码...${NC}"
git pull

# 设置最大重试次数
MAX_RETRIES=5
TIMEOUT=600
MIRROR_SOURCE="https://mirrors.aliyun.com/pypi/simple/"

# 创建临时 Dockerfile
echo -e "${YELLOW}创建临时构建文件...${NC}"
cp Dockerfile-server Dockerfile-server.bak
sed -i "s|pip install --no-cache-dir -r requirements.txt|pip install --no-cache-dir -r requirements.txt --timeout=$TIMEOUT -i $MIRROR_SOURCE|g" Dockerfile-server

# 构建函数，包含重试逻辑
build_with_retry() {
    retry_count=0
    build_success=false

    while [ $retry_count -lt $MAX_RETRIES ] && [ "$build_success" != "true" ]; do
        echo -e "${YELLOW}尝试构建 (尝试 $((retry_count+1))/$MAX_RETRIES)...${NC}"

        if docker-compose build --no-cache; then
            build_success=true
            echo -e "${GREEN}构建成功!${NC}"
        else
            retry_count=$((retry_count+1))
            if [ $retry_count -lt $MAX_RETRIES ]; then
                echo -e "${YELLOW}构建失败，等待 30 秒后重试...${NC}"
                sleep 30
            else
                echo -e "${RED}达到最大重试次数，构建失败。${NC}"
            fi
        fi
    done

    # 恢复原始 Dockerfile
    mv Dockerfile-server.bak Dockerfile-server

    return $build_success
}

# 执行构建
build_with_retry

# 如果构建成功，启动服务
if [ "$build_success" = "true" ]; then
    echo -e "${GREEN}重启服务...${NC}"
    docker-compose down
    docker-compose up -d
    echo -e "${GREEN}更新完成!${NC}"
else
    echo -e "${RED}构建失败，请检查网络连接或手动构建。${NC}"
    # 恢复原始 Dockerfile
    if [ -f Dockerfile-server.bak ]; then
        mv Dockerfile-server.bak Dockerfile-server
    fi
    exit 1
fi