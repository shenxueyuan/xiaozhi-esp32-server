---
description: 一键部署/更新/运维全服务（核心 + RAGFlow + Voiceprint + MCP + Certd + Context Provider）
---

本工作流用于把当前仓库的“全量服务部署”标准化执行。

# 权威入口（已有）

- 脚本入口：`deploy_server_full.sh`
- 从 0 到 1 文档：`docs/server-deployment-guide.md`
- 上游部署文档（参考）：
  - `docs/Deployment_all.md`（Docker 全模块）
  - `docs/Deployment.md`（Docker 仅 Server）

# 适用服务器

- 目标服务器：`root@106.15.33.103`
- 约定部署根目录：`/root/xiaozhi-server/`
- 约定仓库目录：`/root/xiaozhi-server/xiaozhi-esp32-server/`

# 0. 前置检查（仅检查，不改动）

1. 连通性：

```bash
ssh root@106.15.33.103 "echo ok"
```

2. Docker/Compose：

```bash
ssh root@106.15.33.103 "docker --version && docker compose version"
```

3. Nginx：

```bash
ssh root@106.15.33.103 "nginx -v"
```

# 1. 首次全量部署（install）

> 首次部署会创建目录、拉取/构建镜像、创建数据库并启动全部服务。

```bash
ssh root@106.15.33.103 "mkdir -p /root/xiaozhi-server"
ssh root@106.15.33.103 "test -d /root/xiaozhi-server/xiaozhi-esp32-server || git clone https://github.com/froest2012/xiaozhi-esp32-server.git /root/xiaozhi-server/xiaozhi-esp32-server"
ssh root@106.15.33.103 "cd /root/xiaozhi-server/xiaozhi-esp32-server && git checkout dev-20260211 && git pull"
ssh root@106.15.33.103 "cd /root/xiaozhi-server/xiaozhi-esp32-server && bash deploy_server_full.sh install"
```

# 2. 更新部署（update）

> 用于日常更新：拉取最新代码、重建核心服务、重启外部组件。

```bash
ssh root@106.15.33.103 "cd /root/xiaozhi-server/xiaozhi-esp32-server && git checkout dev-20260211 && git pull"
ssh root@106.15.33.103 "cd /root/xiaozhi-server/xiaozhi-esp32-server && bash deploy_server_full.sh update"
```

# 3. 启动/停止/状态

```bash
ssh root@106.15.33.103 "cd /root/xiaozhi-server/xiaozhi-esp32-server && bash deploy_server_full.sh start"
ssh root@106.15.33.103 "cd /root/xiaozhi-server/xiaozhi-esp32-server && bash deploy_server_full.sh stop"
ssh root@106.15.33.103 "cd /root/xiaozhi-server/xiaozhi-esp32-server && bash deploy_server_full.sh status"
```

# 4. 单服务确认（常用容器）

> 用于“我只想确认 voiceprint-api / mcp-endpoint-server 是怎么部署的”。

```bash
ssh root@106.15.33.103 "docker ps --format 'table {{.Names}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}' | egrep -i 'xiaozhi|ragflow|voiceprint|mcp|certd|context' || true"
ssh root@106.15.33.103 "docker inspect voiceprint-api --format '{{.Config.Image}} {{json .HostConfig.NetworkMode}}'"
ssh root@106.15.33.103 "docker inspect mcp-endpoint-server --format '{{.Config.Image}} {{json .HostConfig.NetworkMode}}'"
```

# 5. 关键验证点（对外可用性）

- 智控台：`http://<服务器IP>:8002`
- Python AI（WS）：`ws://<服务器IP>:8000/xiaozhi/v1/`
- RAGFlow：`http://<服务器IP>:8008`
- Voiceprint：`http://<服务器IP>:8005`
- MCP Endpoint：`http://<服务器IP>:8004`
- Certd：`http://<服务器IP>:7001`
- Context Provider：`http://<服务器IP>:8081/health`

# 6. 故障排查（最小集合）

1. 看容器：

```bash
ssh root@106.15.33.103 "docker ps -a"
```

2. 看单容器日志：

```bash
ssh root@106.15.33.103 "docker logs --tail 200 voiceprint-api"
ssh root@106.15.33.103 "docker logs --tail 200 mcp-endpoint-server"
ssh root@106.15.33.103 "docker logs --tail 200 xiaozhi-esp32-server"
ssh root@106.15.33.103 "docker logs --tail 200 xiaozhi-esp32-server-web"
```

3. 看端口监听：

```bash
ssh root@106.15.33.103 "ss -lntp | egrep ':(8000|8002|8003|8004|8005|8008|8081|7001) ' || true"
```
