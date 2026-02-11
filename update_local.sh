#!/bin/bash
# 本地部署/更新脚本
# 用法: bash update_local.sh

set -e
COMPOSE_FILE="docker-compose_local.yml"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$PROJECT_DIR"

echo "=== 1. 拉取最新代码 ==="
git pull

echo "=== 2. 重新构建应用镜像（仅构建有代码变更的服务）==="
docker compose -f $COMPOSE_FILE build xiaozhi-esp32-server xiaozhi-esp32-server-web

echo "=== 3. 重启服务 ==="
docker compose -f $COMPOSE_FILE down
docker compose -f $COMPOSE_FILE up -d

echo "=== 4. 等待服务就绪 ==="
sleep 5
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "=== 部署完成 ==="
echo "智控台:   http://localhost:8002"
echo "API文档:  http://localhost:8002/xiaozhi/doc.html"
