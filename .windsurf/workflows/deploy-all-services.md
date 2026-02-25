---
description: 一键部署/更新/运维全服务（核心 + RAGFlow + Voiceprint + MCP + Certd + Context Provider）
---

本工作流用于把当前仓库的“全量服务部署”标准化执行。

# 变量配置（先复制这段到你的终端执行）

```bash
export SERVER_USER=root
export SERVER_HOST=106.15.33.103
export BASE_DIR=/root/xiaozhi-server
export PROJECT_DIR=/root/xiaozhi-server/xiaozhi-esp32-server
export REPO_URL=https://github.com/froest2012/xiaozhi-esp32-server.git
export BRANCH=dev-20260211

export SSH_TARGET="${SERVER_USER}@${SERVER_HOST}"
```

# 权威入口（已有）

- 脚本入口：`deploy_server_full.sh`
- 从 0 到 1 文档：`docs/server-deployment-guide.md`
- 上游部署文档（参考）：
  - `docs/Deployment_all.md`（Docker 全模块）
  - `docs/Deployment.md`（Docker 仅 Server）

# 适用服务器

- 目标服务器：`${SSH_TARGET}`
- 约定部署根目录：`${BASE_DIR}`
- 约定仓库目录：`${PROJECT_DIR}`

# 0. 前置检查（仅检查，不改动）

1. 连通性：

```bash
ssh "${SSH_TARGET}" "echo ok"
```

2. Docker/Compose：

```bash
ssh "${SSH_TARGET}" "docker --version && docker compose version"
```

3. Nginx：

```bash
ssh "${SSH_TARGET}" "nginx -v"
```

# 1. 首次全量部署（install）

> 首次部署会创建目录、拉取/构建镜像、创建数据库并启动全部服务。

```bash
ssh "${SSH_TARGET}" "mkdir -p '${BASE_DIR}'"
ssh "${SSH_TARGET}" "test -d '${PROJECT_DIR}' || git clone '${REPO_URL}' '${PROJECT_DIR}'"
ssh "${SSH_TARGET}" "cd '${PROJECT_DIR}' && git fetch --all --prune && git checkout '${BRANCH}' && git pull"
ssh "${SSH_TARGET}" "cd '${PROJECT_DIR}' && bash deploy_server_full.sh install"
```

# 2. 更新部署（update）

> 用于日常更新：拉取最新代码、重建核心服务、重启外部组件。

```bash
ssh "${SSH_TARGET}" "cd '${PROJECT_DIR}' && git fetch --all --prune && git checkout '${BRANCH}' && git pull"
ssh "${SSH_TARGET}" "cd '${PROJECT_DIR}' && bash deploy_server_full.sh update"
```

# 3. 启动/停止/状态

```bash
ssh "${SSH_TARGET}" "cd '${PROJECT_DIR}' && bash deploy_server_full.sh start"
ssh "${SSH_TARGET}" "cd '${PROJECT_DIR}' && bash deploy_server_full.sh stop"
ssh "${SSH_TARGET}" "cd '${PROJECT_DIR}' && bash deploy_server_full.sh status"
```

# 4. 单服务确认（常用容器）

> 用于“我只想确认 voiceprint-api / mcp-endpoint-server 是怎么部署的”。

```bash
ssh "${SSH_TARGET}" "docker ps --format 'table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}' | egrep -i 'xiaozhi|ragflow|voiceprint|mcp|certd|context' || true"
ssh "${SSH_TARGET}" "docker inspect voiceprint-api --format '{{.Config.Image}} {{json .HostConfig.NetworkMode}}'"
ssh "${SSH_TARGET}" "docker inspect mcp-endpoint-server --format '{{.Config.Image}} {{json .HostConfig.NetworkMode}}'"
```

# 5. 关键验证点（对外可用性）

- 智控台：`http://${SERVER_HOST}:8002`
- Python AI（WS）：`ws://${SERVER_HOST}:8000/xiaozhi/v1/`
- RAGFlow：`http://${SERVER_HOST}:8008`
- Voiceprint：`http://${SERVER_HOST}:8005`
- MCP Endpoint：`http://${SERVER_HOST}:8004`
- Certd：`http://${SERVER_HOST}:7001`
- Context Provider：`http://${SERVER_HOST}:8081/health`

# 6. 故障排查（最小集合）

1. 看容器：

```bash
ssh "${SSH_TARGET}" "docker ps -a"
```

2. 看单容器日志：

```bash
ssh "${SSH_TARGET}" "docker logs --tail 200 voiceprint-api"
ssh "${SSH_TARGET}" "docker logs --tail 200 mcp-endpoint-server"
ssh "${SSH_TARGET}" "docker logs --tail 200 xiaozhi-esp32-server"
ssh "${SSH_TARGET}" "docker logs --tail 200 xiaozhi-esp32-server-web"
```

3. 看端口监听：

```bash
ssh "${SSH_TARGET}" "ss -lntp | egrep ':(8000|8002|8003|8004|8005|8008|8081|7001) ' || true"
```
