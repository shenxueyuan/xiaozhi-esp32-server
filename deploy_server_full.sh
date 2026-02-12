#!/bin/bash
###############################################################################
# 小新 ESP32 Server 全功能一键部署脚本（服务器端）
# 适用于: Linux 服务器 (Ubuntu/CentOS/Debian)
# 部署内容:
#   1. 核心服务 (Python AI + Web/Java API + MySQL + Redis)
#   2. RAGFlow 知识库
#   3. 声纹识别 Voiceprint API
#   4. MCP 接入点
#   5. Certd SSL 证书自动化
#   6. Context Provider 上下文源（青少年心理健康）
#   7. MQTT 网关 (可选)
#
# 用法:
#   首次部署:  bash deploy_server_full.sh install
#   更新部署:  bash deploy_server_full.sh update
#   仅启动:    bash deploy_server_full.sh start
#   全部停止:  bash deploy_server_full.sh stop
#   查看状态:  bash deploy_server_full.sh status
###############################################################################

set -e

# ========================== 配置区域（可通过环境变量覆盖） ==========================
# 根目录
BASE_DIR="${BASE_DIR:-/root/xiaozhi-server}"
# 主项目目录
PROJECT_DIR="${PROJECT_DIR:-${BASE_DIR}/xiaozhi-esp32-server}"
# Docker Compose 文件
COMPOSE_FILE="${COMPOSE_FILE:-docker-compose_all_custom.yml}"
# MySQL 密码
MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-qweQWE331792784}"
# 服务器公网 IP（自动检测，也可手动指定）
SERVER_IP="${SERVER_IP:-$(curl -s --connect-timeout 3 ifconfig.me 2>/dev/null || echo '127.0.0.1')}"
# 服务器局域网 IP（自动检测，兼容 macOS 和 Linux）
if [ -z "${LAN_IP}" ]; then
    LAN_IP=$(hostname -I 2>/dev/null | awk '{print $1}')
    [ -z "${LAN_IP}" ] && LAN_IP=$(ipconfig getifaddr en0 2>/dev/null || echo '127.0.0.1')
fi

# 外部服务目录
RAGFLOW_DIR="${RAGFLOW_DIR:-${BASE_DIR}/ragflow}"
VOICEPRINT_DIR="${VOICEPRINT_DIR:-${BASE_DIR}/voiceprint-api}"
MCP_ENDPOINT_DIR="${MCP_ENDPOINT_DIR:-${BASE_DIR}/mcp-endpoint-server}"
MQTT_GATEWAY_DIR="${MQTT_GATEWAY_DIR:-${BASE_DIR}/xiaozhi-mqtt-gateway}"
CONTEXT_PROVIDER_DIR="${CONTEXT_PROVIDER_DIR:-${BASE_DIR}/context-provider}"
CERTD_DATA_DIR="${CERTD_DATA_DIR:-/root/certd-data}"

# 检测操作系统（macOS sed 需要不同参数）
SED_INPLACE="sed -i"
if [[ "$(uname)" == "Darwin" ]]; then
    SED_INPLACE="sed -i ''"
fi
sed_inplace() { if [[ "$(uname)" == "Darwin" ]]; then sed -i '' "$@"; else sed -i "$@"; fi; }

# RAGFlow 版本
RAGFLOW_VERSION="v0.22.0"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info()  { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step()  { echo -e "\n${BLUE}===== $1 =====${NC}"; }

# ========================== 前置检查 ==========================
check_prerequisites() {
    log_step "前置环境检查"

    # 检查 Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi
    log_info "Docker: $(docker --version)"

    # 检查 Docker Compose
    if ! docker compose version &> /dev/null; then
        log_error "Docker Compose 未安装"
        exit 1
    fi
    log_info "Docker Compose: $(docker compose version --short)"

    # 检查 Git
    if ! command -v git &> /dev/null; then
        log_error "Git 未安装"
        exit 1
    fi

    log_info "服务器公网 IP: ${SERVER_IP}"
    log_info "服务器局域网 IP: ${LAN_IP}"
    log_info "项目根目录: ${BASE_DIR}"
}

# ========================== 获取主项目 Docker 网络 ==========================
get_main_network() {
    # 查找包含 xiaozhi-esp32-server-db 容器的 Docker 网络
    local net=$(docker inspect xiaozhi-esp32-server-db --format '{{range $k,$v := .NetworkSettings.Networks}}{{$k}}{{end}}' 2>/dev/null)
    if [ -z "$net" ]; then
        # 回退: 根据 compose 文件所在目录推导网络名
        local dir_name=$(basename "${PROJECT_DIR}")
        net="${dir_name}_default"
    fi
    echo "$net"
}

# 修改外部服务的 docker-compose.yml，使其服务加入主项目网络（启动时即可访问 MySQL/Redis）
# 用法: patch_compose_network <docker-compose文件路径> <服务名>
patch_compose_network() {
    local compose_file=$1
    local service_name=$2
    local main_net=$(get_main_network)

    if [ ! -f "${compose_file}" ]; then
        log_error "compose 文件不存在: ${compose_file}"
        return 1
    fi

    log_info "将 ${service_name} 加入主网络 ${main_net}..."

    python3 -c "
import yaml, sys

with open('${compose_file}', 'r') as f:
    cfg = yaml.safe_load(f)

# 添加顶层 networks 声明（external）
cfg.setdefault('networks', {})
cfg['networks']['xiaozhi-main'] = {'external': True, 'name': '${main_net}'}

# 为目标服务添加网络
svc = cfg.get('services', {}).get('${service_name}', {})
if svc:
    existing_nets = svc.get('networks', ['default'])
    if isinstance(existing_nets, list):
        if 'xiaozhi-main' not in existing_nets:
            existing_nets.append('xiaozhi-main')
        svc['networks'] = existing_nets
    elif isinstance(existing_nets, dict):
        existing_nets['xiaozhi-main'] = {}
        svc['networks'] = existing_nets
    else:
        svc['networks'] = ['default', 'xiaozhi-main']

with open('${compose_file}', 'w') as f:
    yaml.dump(cfg, f, default_flow_style=False, allow_unicode=True, sort_keys=False)
print('patched ${service_name} -> ${main_net}')
" || log_warn "patch_compose_network 失败，尝试 docker network connect 回退"
}

# ========================== 等待 MySQL 就绪 ==========================
wait_for_mysql() {
    log_info "等待 MySQL 就绪..."
    for i in $(seq 1 30); do
        if docker exec xiaozhi-esp32-server-db mysqladmin ping -h localhost --silent 2>/dev/null; then
            log_info "MySQL 已就绪"
            return 0
        fi
        echo -n "."
        sleep 2
    done
    log_error "MySQL 启动超时"
    return 1
}

# ========================== 1. 核心服务 ==========================
deploy_core() {
    log_step "1/7 部署核心服务 (Python AI + Web + MySQL + Redis)"

    cd "${BASE_DIR}"

    # 确保 Redis 端口对外暴露（外部服务需要访问）
    if grep -q "expose:" "${PROJECT_DIR}/${COMPOSE_FILE}" 2>/dev/null; then
        log_warn "检测到 Redis 使用 expose，修改为 ports 以支持外部服务访问"
        sed_inplace 's/    expose:/    ports:/g' "${PROJECT_DIR}/${COMPOSE_FILE}"
        sed_inplace 's/      - 6379/      - "6379:6379"/g' "${PROJECT_DIR}/${COMPOSE_FILE}"
    fi

    # 构建并启动
    cd "${PROJECT_DIR}"
    docker compose -f ${COMPOSE_FILE} build xiaozhi-esp32-server xiaozhi-esp32-server-web
    docker compose -f ${COMPOSE_FILE} down 2>/dev/null || true
    docker compose -f ${COMPOSE_FILE} up -d

    wait_for_mysql
    log_info "核心服务部署完成"
}

# ========================== 2. RAGFlow 知识库 ==========================
deploy_ragflow() {
    log_step "2/7 部署 RAGFlow 知识库"

    # 创建 RAGFlow 数据库
    log_info "创建 RAGFlow 数据库..."
    docker exec xiaozhi-esp32-server-db mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "
        CREATE DATABASE IF NOT EXISTS rag_flow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
        CREATE USER IF NOT EXISTS 'rag_flow'@'%' IDENTIFIED BY 'infini_rag_flow';
        GRANT ALL PRIVILEGES ON rag_flow.* TO 'rag_flow'@'%';
        FLUSH PRIVILEGES;
    " 2>/dev/null || log_warn "RAGFlow 数据库可能已存在"

    # 克隆或更新项目
    if [ ! -d "${RAGFLOW_DIR}" ]; then
        log_info "克隆 RAGFlow 项目..."
        cd "${BASE_DIR}"
        git clone https://ghfast.top/https://github.com/infiniflow/ragflow.git
        cd ragflow
        git checkout ${RAGFLOW_VERSION}
    else
        log_info "RAGFlow 项目已存在，跳过克隆"
        cd "${RAGFLOW_DIR}"
    fi

    # 配置 RAGFlow
    cd "${RAGFLOW_DIR}/docker"

    # 修改 .env 文件
    if [ -f ".env" ]; then
        log_info "配置 RAGFlow 环境变量..."
        # 备份原始 .env
        cp .env .env.bak 2>/dev/null || true

        # 修改端口
        sed_inplace 's/^SVR_WEB_HTTP_PORT=.*/SVR_WEB_HTTP_PORT=8008/' .env
        sed_inplace 's/^SVR_WEB_HTTPS_PORT=.*/SVR_WEB_HTTPS_PORT=8009/' .env

        # 修改 MySQL 配置（使用主项目 MySQL 容器名，通过共享网络访问）
        sed_inplace 's/^MYSQL_HOST=.*/MYSQL_HOST=xiaozhi-esp32-server-db/' .env
        sed_inplace 's/^MYSQL_PORT=.*/MYSQL_PORT=3306/' .env
        sed_inplace 's/^MYSQL_PASSWORD=.*/MYSQL_PASSWORD=infini_rag_flow/' .env
        sed_inplace 's/^MYSQL_DBNAME=.*/MYSQL_DBNAME=rag_flow/' .env

        # 确保 MYSQL_USER 存在
        if ! grep -q "^MYSQL_USER=" .env; then
            echo "MYSQL_USER=rag_flow" >> .env
        else
            sed_inplace 's/^MYSQL_USER=.*/MYSQL_USER=rag_flow/' .env
        fi

        # 修改 Redis 配置（使用主项目 Redis 容器名）
        sed_inplace 's/^REDIS_HOST=.*/REDIS_HOST=xiaozhi-esp32-server-redis/' .env
        sed_inplace 's/^REDIS_PORT=.*/REDIS_PORT=6379/' .env
        sed_inplace 's/^REDIS_PASSWORD=.*/REDIS_PASSWORD=/' .env
    fi

    # 修改 service_conf.yaml.template，清除 Redis 默认密码
    if [ -f "service_conf.yaml.template" ]; then
        sed_inplace "s/password: '\${REDIS_PASSWORD:-infini_rag_flow}'/password: '\${REDIS_PASSWORD:-}'/" service_conf.yaml.template
    fi

    # 去掉 docker-compose.yml 中 ragflow 对 mysql 的 depends_on
    if [ -f "docker-compose.yml" ]; then
        log_info "修改 RAGFlow docker-compose 配置..."
        # 使用 python 来可靠地修改 YAML（如果有python的话），否则用 sed
        if command -v python3 &> /dev/null; then
            python3 -c "
import re
with open('docker-compose.yml', 'r') as f:
    content = f.read()
# 简单删除 ragflow-cpu 和 ragflow-gpu 的 depends_on 块
content = re.sub(r'(\s+ragflow-(?:cpu|gpu):\n)(\s+depends_on:\n(?:\s+\w+:\n\s+condition:.*\n)+)', r'\1', content)
with open('docker-compose.yml', 'w') as f:
    f.write(content)
" 2>/dev/null || log_warn "自动修改 docker-compose.yml 失败，请手动移除 depends_on"
        fi
    fi

    # 去掉 docker-compose-base.yml 中的 mysql 和 redis 服务（共用主项目的）
    if [ -f "docker-compose-base.yml" ]; then
        log_info "修改 RAGFlow base 配置，移除内置 MySQL/Redis..."
        if command -v python3 &> /dev/null; then
            python3 -c "
import re
with open('docker-compose-base.yml', 'r') as f:
    content = f.read()
# 删除 mysql 和 redis 服务块
content = re.sub(r'\n  mysql:.*?(?=\n  \w|\nvolumes:|\nnetworks:|\Z)', '', content, flags=re.DOTALL)
content = re.sub(r'\n  redis:.*?(?=\n  \w|\nvolumes:|\nnetworks:|\Z)', '', content, flags=re.DOTALL)
with open('docker-compose-base.yml', 'w') as f:
    f.write(content)
" 2>/dev/null || log_warn "自动修改 docker-compose-base.yml 失败，请手动移除 mysql/redis"
        fi
    fi

    # 将 RAGFlow 服务加入主项目网络（启动前 patch，使其可访问 MySQL/Redis 容器）
    patch_compose_network "${RAGFLOW_DIR}/docker/docker-compose.yml" "ragflow-cpu"

    # 启动 RAGFlow
    log_info "启动 RAGFlow..."
    docker compose -f docker-compose.yml up -d 2>/dev/null || \
    docker-compose -f docker-compose.yml up -d 2>/dev/null || \
    log_error "RAGFlow 启动失败，请检查配置"

    log_info "RAGFlow 部署完成 → http://${SERVER_IP}:8008"
}

# ========================== 3. 声纹识别 ==========================
deploy_voiceprint() {
    log_step "3/7 部署声纹识别服务"

    # 创建声纹数据库
    log_info "创建声纹识别数据库..."
    docker exec xiaozhi-esp32-server-db mysql -uroot -p${MYSQL_ROOT_PASSWORD} -e "
        CREATE DATABASE IF NOT EXISTS voiceprint_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
        USE voiceprint_db;
        CREATE TABLE IF NOT EXISTS voiceprints (
            id INT AUTO_INCREMENT PRIMARY KEY,
            speaker_id VARCHAR(255) NOT NULL UNIQUE,
            feature_vector LONGBLOB NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            INDEX idx_speaker_id (speaker_id)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
    " 2>/dev/null || log_warn "声纹数据库可能已存在"

    # 克隆或更新项目
    if [ ! -d "${VOICEPRINT_DIR}" ]; then
        log_info "克隆声纹识别项目..."
        cd "${BASE_DIR}"
        git clone https://ghfast.top/https://github.com/xinnan-tech/voiceprint-api.git
    else
        log_info "声纹识别项目已存在，跳过克隆"
    fi

    cd "${VOICEPRINT_DIR}"

    # 创建 data 目录和配置文件
    mkdir -p data
    if [ -f "voiceprint.yaml" ] && [ ! -f "data/.voiceprint.yaml" ]; then
        cp voiceprint.yaml data/.voiceprint.yaml
    fi

    # 配置数据库连接（使用 python 精确修改 mysql 段，避免误改 server 段）
    if [ -f "data/.voiceprint.yaml" ]; then
        log_info "配置声纹识别数据库连接..."
        python3 -c "
import yaml, sys
with open('data/.voiceprint.yaml', 'r') as f:
    cfg = yaml.safe_load(f)
cfg.setdefault('mysql', {})
cfg['mysql']['host'] = 'xiaozhi-esp32-server-db'
cfg['mysql']['port'] = 3306
cfg['mysql']['user'] = 'root'
cfg['mysql']['password'] = '${MYSQL_ROOT_PASSWORD}'
cfg['mysql']['database'] = 'voiceprint_db'
# 确保 server.host 保持 0.0.0.0
cfg.setdefault('server', {})
cfg['server']['host'] = '0.0.0.0'
with open('data/.voiceprint.yaml', 'w') as f:
    yaml.dump(cfg, f, default_flow_style=False, allow_unicode=True)
print('voiceprint config updated')
" || {
            log_warn "python3 不可用，使用 sed 回退方案"
            sed_inplace "s/host: .*/host: \"xiaozhi-esp32-server-db\"/" data/.voiceprint.yaml
            sed_inplace "s/password: .*/password: \"${MYSQL_ROOT_PASSWORD}\"/" data/.voiceprint.yaml
            sed_inplace "s/database: .*/database: \"voiceprint_db\"/" data/.voiceprint.yaml
        }
    fi

    # 将声纹服务加入主项目网络（启动前 patch）
    patch_compose_network "${VOICEPRINT_DIR}/docker-compose.yml" "voiceprint-api"

    # 启动声纹识别
    log_info "启动声纹识别服务..."
    docker compose -f docker-compose.yml down 2>/dev/null || true
    docker compose -f docker-compose.yml up -d

    log_info "声纹识别部署完成 → 端口 8005"
}

# ========================== 4. MCP 接入点 ==========================
deploy_mcp_endpoint() {
    log_step "4/7 部署 MCP 接入点"

    # 克隆或更新项目
    if [ ! -d "${MCP_ENDPOINT_DIR}" ]; then
        log_info "克隆 MCP 接入点项目..."
        cd "${BASE_DIR}"
        git clone https://ghfast.top/https://github.com/xinnan-tech/mcp-endpoint-server.git
    else
        log_info "MCP 接入点项目已存在，跳过克隆"
    fi

    cd "${MCP_ENDPOINT_DIR}"

    # 将 MCP 接入点加入主项目网络（启动前 patch）
    patch_compose_network "${MCP_ENDPOINT_DIR}/docker-compose.yml" "mcp-endpoint-server"

    # 启动 MCP 接入点
    log_info "启动 MCP 接入点服务..."
    docker compose -f docker-compose.yml down 2>/dev/null || true
    docker compose -f docker-compose.yml up -d

    log_info "MCP 接入点部署完成 → 端口 8004"
}

# ========================== 5. Certd SSL 证书自动化 ==========================
deploy_certd() {
    log_step "5/7 部署 Certd SSL 证书自动化"

    if docker ps --format '{{.Names}}' | grep -q "^certd$"; then
        log_info "Certd 已运行，跳过部署"
        return 0
    fi

    mkdir -p "${CERTD_DATA_DIR}"

    log_info "启动 Certd 容器..."
    docker run -d \
        --name certd \
        --restart always \
        -p 7001:7001 \
        -p 7002:7002 \
        -v "${CERTD_DATA_DIR}":/app/data \
        registry.cn-shenzhen.aliyuncs.com/handsfree/certd:latest

    log_info "Certd 部署完成 → http://${SERVER_IP}:7001"
    log_warn "首次登录使用 admin/123456，请立即修改密码"
    log_warn "后续需手动配置：阿里云 DNS 授权、SSH 主机授权、证书流水线（详见 docs/certd-ssl-management.md）"
}

# ========================== 6. Context Provider 上下文源 ==========================
deploy_context_provider() {
    log_step "6/7 部署 Context Provider 上下文源（青少年心理健康）"

    # 从项目中复制上下文源代码
    local src_dir="${PROJECT_DIR}/context-provider"
    if [ ! -d "${src_dir}" ]; then
        log_error "context-provider 源码目录不存在: ${src_dir}"
        return 1
    fi

    # 复制到部署目录
    if [ ! -d "${CONTEXT_PROVIDER_DIR}" ]; then
        log_info "复制 Context Provider 到部署目录..."
        cp -r "${src_dir}" "${CONTEXT_PROVIDER_DIR}"
    else
        log_info "更新 Context Provider 代码..."
        cp "${src_dir}"/*.py "${CONTEXT_PROVIDER_DIR}/"
        cp "${src_dir}/requirements.txt" "${CONTEXT_PROVIDER_DIR}/"
        cp "${src_dir}/Dockerfile" "${CONTEXT_PROVIDER_DIR}/"
        cp "${src_dir}/docker-compose.yml" "${CONTEXT_PROVIDER_DIR}/"
    fi

    cd "${CONTEXT_PROVIDER_DIR}"

    # 构建并启动
    log_info "构建并启动 Context Provider..."
    docker compose down 2>/dev/null || true
    docker compose up -d --build

    # 等待服务就绪
    sleep 3
    if curl -s http://localhost:8081/health | grep -q '"code": 0'; then
        log_info "Context Provider 健康检查通过 ✅"
    else
        log_warn "Context Provider 健康检查未通过，请检查日志: docker logs context-provider"
    fi

    log_info "Context Provider 部署完成 → 端口 8081"
    log_info "智控台上下文源地址: http://172.17.0.1:8081/mental-health"
}

# ========================== 7. MQTT 网关（可选） ==========================
deploy_mqtt_gateway() {
    log_step "7/7 部署 MQTT 网关（可选）"

    # 检查 Node.js
    if ! command -v node &> /dev/null; then
        log_warn "Node.js 未安装，跳过 MQTT 网关部署"
        log_warn "如需部署，请先安装 Node.js: curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash - && sudo apt-get install -y nodejs"
        return 0
    fi

    # 克隆或更新项目
    if [ ! -d "${MQTT_GATEWAY_DIR}" ]; then
        log_info "克隆 MQTT 网关项目..."
        cd "${BASE_DIR}"
        git clone https://ghfast.top/https://github.com/xinnan-tech/xiaozhi-mqtt-gateway.git
    else
        log_info "MQTT 网关项目已存在，跳过克隆"
    fi

    cd "${MQTT_GATEWAY_DIR}"

    # 安装依赖
    log_info "安装 MQTT 网关依赖..."
    npm install 2>/dev/null
    npm install -g pm2 2>/dev/null || true

    # 创建配置
    if [ ! -f "config/mqtt.json" ] && [ -f "config/mqtt.json.example" ]; then
        cp config/mqtt.json.example config/mqtt.json
        log_info "配置 MQTT 网关..."
        # 修改 websocket 地址
        sed_inplace "s|ws://.*xiaozhi/v1/|ws://${LAN_IP}:8000/xiaozhi/v1/?from=mqtt_gateway|g" config/mqtt.json 2>/dev/null || true
    fi

    # 使用 PM2 启动
    pm2 delete xiaozhi-mqtt-gateway 2>/dev/null || true
    pm2 start npm --name "xiaozhi-mqtt-gateway" -- start 2>/dev/null || log_warn "MQTT 网关启动失败"

    log_info "MQTT 网关部署完成 → 端口 1883/8884/8007"
}

# ========================== 部署总览 ==========================
show_status() {
    log_step "服务状态"
    echo ""
    echo "Docker 容器:"
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" 2>/dev/null
    echo ""

    # 检查 PM2
    if command -v pm2 &> /dev/null; then
        echo "PM2 进程:"
        pm2 list 2>/dev/null || true
        echo ""
    fi

    log_step "访问地址"
    echo -e "  ${GREEN}智控台（前端）${NC}      http://${SERVER_IP}:8002"
    echo -e "  ${GREEN}API 文档${NC}           http://${SERVER_IP}:8002/xiaozhi/doc.html"
    echo -e "  ${GREEN}Python AI (WS)${NC}     ws://${SERVER_IP}:8000/xiaozhi/v1/"
    echo -e "  ${GREEN}RAGFlow 知识库${NC}     http://${SERVER_IP}:8008"
    echo -e "  ${GREEN}声纹识别${NC}           http://${SERVER_IP}:8005"
    echo -e "  ${GREEN}MCP 接入点${NC}         http://${SERVER_IP}:8004"
    echo -e "  ${GREEN}Certd SSL 管理${NC}     http://${SERVER_IP}:7001"
    echo -e "  ${GREEN}Context Provider${NC}   http://${SERVER_IP}:8081/mental-health"
    echo -e "  ${GREEN}MQTT 网关${NC}          端口 1883 (TCP) / 8884 (UDP)"
    echo ""

    log_step "端口清单（确保服务器防火墙/安全组已开放）"
    echo "  8000  - Python AI 服务 (WebSocket)"
    echo "  8002  - 智控台 Web 前端"
    echo "  8003  - Java API HTTP"
    echo "  8004  - MCP 接入点"
    echo "  8005  - 声纹识别"
    echo "  8008  - RAGFlow 知识库"
    echo "  8081  - Context Provider 上下文源"
    echo "  7001  - Certd SSL 证书管理"
    echo "  3306  - MySQL"
    echo "  6379  - Redis"
    echo "  1883  - MQTT (TCP)"
    echo "  8884  - MQTT (UDP)"
    echo "  8007  - MQTT HTTP"
    echo ""

    log_step "智控台配置提示"
    echo "  1. RAGFlow:   模型配置 → 知识库 → RAG_RAGFlow → 服务地址填 http://${LAN_IP}:8008"
    echo "  2. 声纹识别:  参数管理 → 搜索 server.voiceprint → 填 http://${LAN_IP}:8005/voiceprint/health?key=xxx"
    echo "  3. MCP接入点: 参数管理 → 搜索 server.mcp_endpoint → 填 http://${LAN_IP}:8004/mcp_endpoint/health?key=xxx"
    echo "  4. 音色克隆:  模型配置 → TTS → 火山双流式 → 填入火山引擎 app_id/access_token/voice_type"
    echo "  5. 上下文源: 角色配置 → 上下文源 → 编辑源 → 添加 http://172.17.0.1:8081/mental-health"
    echo "  6. Certd:    访问 http://${SERVER_IP}:7001 配置 DNS 授权/SSH 授权/证书流水线（详见 docs/certd-ssl-management.md）"
    echo ""
}

# ========================== 全量安装 ==========================
install_all() {
    check_prerequisites

    mkdir -p "${BASE_DIR}"

    deploy_core
    deploy_ragflow
    deploy_voiceprint
    deploy_mcp_endpoint
    deploy_certd
    deploy_context_provider
    deploy_mqtt_gateway

    show_status
    log_info "全量部署完成！"
}

# ========================== 更新部署 ==========================
update_all() {
    check_prerequisites

    log_step "更新主项目代码"
    cd "${PROJECT_DIR}"
    git pull

    deploy_core

    # 重启外部服务
    if [ -d "${RAGFLOW_DIR}" ]; then
        log_info "重启 RAGFlow..."
        cd "${RAGFLOW_DIR}/docker"
        docker compose -f docker-compose.yml restart 2>/dev/null || true
    fi

    if [ -d "${VOICEPRINT_DIR}" ]; then
        log_info "重启声纹识别..."
        cd "${VOICEPRINT_DIR}"
        docker compose -f docker-compose.yml restart 2>/dev/null || true
    fi

    if [ -d "${MCP_ENDPOINT_DIR}" ]; then
        log_info "重启 MCP 接入点..."
        cd "${MCP_ENDPOINT_DIR}"
        docker compose -f docker-compose.yml restart 2>/dev/null || true
    fi

    # 更新 Context Provider
    deploy_context_provider

    show_status
    log_info "更新完成！"
}

# ========================== 启动所有 ==========================
start_all() {
    log_step "启动所有服务"

    cd "${PROJECT_DIR}"
    docker compose -f ${COMPOSE_FILE} up -d

    wait_for_mysql

    if [ -d "${RAGFLOW_DIR}/docker" ]; then
        cd "${RAGFLOW_DIR}/docker"
        docker compose -f docker-compose.yml up -d 2>/dev/null || true
    fi

    if [ -d "${VOICEPRINT_DIR}" ]; then
        cd "${VOICEPRINT_DIR}"
        docker compose -f docker-compose.yml up -d 2>/dev/null || true
    fi

    if [ -d "${MCP_ENDPOINT_DIR}" ]; then
        cd "${MCP_ENDPOINT_DIR}"
        docker compose -f docker-compose.yml up -d 2>/dev/null || true
    fi

    if [ -d "${CONTEXT_PROVIDER_DIR}" ]; then
        cd "${CONTEXT_PROVIDER_DIR}"
        docker compose up -d 2>/dev/null || true
    fi

    # Certd 是 always restart，无需手动启动

    if command -v pm2 &> /dev/null; then
        pm2 resurrect 2>/dev/null || true
    fi

    show_status
}

# ========================== 停止所有 ==========================
stop_all() {
    log_step "停止所有服务"

    if command -v pm2 &> /dev/null; then
        pm2 stop all 2>/dev/null || true
    fi

    if [ -d "${CONTEXT_PROVIDER_DIR}" ]; then
        cd "${CONTEXT_PROVIDER_DIR}"
        docker compose down 2>/dev/null || true
    fi

    docker stop certd 2>/dev/null || true

    if [ -d "${MCP_ENDPOINT_DIR}" ]; then
        cd "${MCP_ENDPOINT_DIR}"
        docker compose -f docker-compose.yml down 2>/dev/null || true
    fi

    if [ -d "${VOICEPRINT_DIR}" ]; then
        cd "${VOICEPRINT_DIR}"
        docker compose -f docker-compose.yml down 2>/dev/null || true
    fi

    if [ -d "${RAGFLOW_DIR}/docker" ]; then
        cd "${RAGFLOW_DIR}/docker"
        docker compose -f docker-compose.yml down 2>/dev/null || true
    fi

    cd "${PROJECT_DIR}"
    docker compose -f ${COMPOSE_FILE} down

    log_info "所有服务已停止"
}

# ========================== 主入口 ==========================
case "${1:-}" in
    install)
        install_all
        ;;
    update)
        update_all
        ;;
    start)
        start_all
        ;;
    stop)
        stop_all
        ;;
    status)
        show_status
        ;;
    *)
        echo "小新 ESP32 Server 全功能部署脚本"
        echo ""
        echo "用法: bash $0 {install|update|start|stop|status}"
        echo ""
        echo "  install  - 首次全量部署（克隆所有项目、创建数据库、启动服务）"
        echo "  update   - 更新部署（拉取最新代码、重新构建、重启）"
        echo "  start    - 启动所有服务"
        echo "  stop     - 停止所有服务"
        echo "  status   - 查看服务状态和访问地址"
        echo ""
        ;;
esac
