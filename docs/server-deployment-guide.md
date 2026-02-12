# 新服务器完整部署指南

> 本文档记录从零开始在一台新服务器上部署所有服务的完整步骤。
> 适用于 Ubuntu 20.04+ / Debian 11+ / CentOS 8+

## 目录

1. [服务器准备](#一服务器准备)
2. [环境安装](#二环境安装)
3. [项目克隆](#三项目克隆)
4. [一键部署](#四一键部署)
5. [Nginx 反向代理配置](#五nginx-反向代理配置)
6. [域名与 DNS 配置](#六域名与-dns-配置)
7. [SSL 证书配置（Certd）](#七ssl-证书配置certd)
8. [智控台配置](#八智控台配置)
9. [RAGFlow 知识库配置](#九ragflow-知识库配置)
10. [端口与安全组](#十端口与安全组)
11. [常用运维命令](#十一常用运维命令)

---

## 一、服务器准备

### 推荐配置

| 项目 | 最低配置 | 推荐配置 |
|---|---|---|
| CPU | 2 核 | 4 核+ |
| 内存 | 4 GB | 8 GB+ |
| 硬盘 | 40 GB SSD | 80 GB+ SSD |
| 系统 | Ubuntu 20.04 | Ubuntu 22.04 |
| 带宽 | 3 Mbps | 5 Mbps+ |

### 云服务商

阿里云、腾讯云、华为云等均可。购买后记录：

- **公网 IP**：`<服务器公网IP>`
- **root 密码**：`<root密码>`

---

## 二、环境安装

SSH 登录服务器后执行：

### 1. 更新系统

```bash
apt update && apt upgrade -y
```

### 2. 安装 Docker

```bash
curl -fsSL https://get.docker.com | bash
systemctl enable docker
systemctl start docker
```

### 3. 安装 Docker Compose（v2 插件）

```bash
apt install -y docker-compose-plugin
docker compose version  # 验证
```

### 4. 安装 Git

```bash
apt install -y git
```

### 5. 安装 Nginx（用于反向代理）

```bash
apt install -y nginx
systemctl enable nginx
```

### 6. 创建项目根目录

```bash
mkdir -p /root/xiaozhi-server
cd /root/xiaozhi-server
```

---

## 三、项目克隆

```bash
cd /root/xiaozhi-server

# 克隆主项目（替换为你的 fork 仓库地址）
git clone https://github.com/froest2012/xiaozhi-esp32-server.git
cd xiaozhi-esp32-server

# 切换到部署分支
git checkout dev-20260211
```

---

## 四、一键部署

项目内置了一键部署脚本，包含以下 7 个服务：

| 序号 | 服务 | 端口 |
|---|---|---|
| 1 | 核心服务（Python AI + Web + MySQL + Redis） | 8000/8002/8003/3306/6379 |
| 2 | RAGFlow 知识库 | 8008 |
| 3 | 声纹识别 Voiceprint API | 8005 |
| 4 | MCP 接入点 | 8004 |
| 5 | Certd SSL 证书自动化 | 7001 |
| 6 | Context Provider 上下文源 | 8081 |
| 7 | MQTT 网关（可选） | 1883/8884 |

### 执行一键部署

```bash
cd /root/xiaozhi-server/xiaozhi-esp32-server
bash deploy_server_full.sh install
```

> 首次部署大约需要 10-30 分钟（取决于网络速度和镜像下载）。

### 其他命令

```bash
bash deploy_server_full.sh status   # 查看所有服务状态
bash deploy_server_full.sh update   # 更新代码并重新部署
bash deploy_server_full.sh start    # 启动所有服务
bash deploy_server_full.sh stop     # 停止所有服务
```

### 验证部署

```bash
# 查看容器状态
docker ps

# 检查核心服务
curl http://localhost:8002          # 智控台前端
curl http://localhost:8008          # RAGFlow
curl http://localhost:8081/health   # Context Provider
```

---

## 五、Nginx 反向代理配置

如果需要通过域名访问（推荐），配置 Nginx 反向代理。

### 示例配置

编辑 `/etc/nginx/nginx.conf` 或在 `/etc/nginx/conf.d/` 下新建配置文件：

```nginx
# 智控台（前端 + API）
server {
    listen 80;
    server_name admin.your-domain.com;

    location / {
        proxy_pass http://127.0.0.1:8002;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# RAGFlow 知识库
server {
    listen 80;
    server_name rag.your-domain.com;

    client_max_body_size 50M;  # 支持大文件上传

    location / {
        proxy_pass http://127.0.0.1:8008;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

# WebSocket（小智 AI 服务）
server {
    listen 80;
    server_name ws.your-domain.com;

    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_read_timeout 3600s;
    }
}
```

```bash
# 检查配置并重载
nginx -t
nginx -s reload
```

---

## 六、域名与 DNS 配置

### 1. 购买域名

在阿里云/腾讯云等注册域名（如 `your-domain.com`）。

### 2. 添加 DNS 解析

在域名管理控制台添加 A 记录：

| 主机记录 | 类型 | 值 |
|---|---|---|
| `@` | A | `<服务器公网IP>` |
| `admin` | A | `<服务器公网IP>` |
| `rag` | A | `<服务器公网IP>` |
| `ws` | A | `<服务器公网IP>` |
| `*` | A | `<服务器公网IP>` |

> 泛域名解析（`*`）可以覆盖所有子域名。

### 3. 等待 DNS 生效

一般 5-10 分钟内生效，可通过以下命令检查：

```bash
ping admin.your-domain.com
```

---

## 七、SSL 证书配置（Certd）

部署脚本已自动启动 Certd。以下是配置 SSL 自动化的步骤。

### 1. 登录 Certd

访问 `http://<服务器IP>:7001`，默认账号：

- **用户名**：`admin`
- **密码**：`123456`

> ⚠️ 登录后请立即修改密码！

### 2. 配置阿里云 DNS 授权

1. 左侧菜单 → **授权管理** → **添加**
2. 类型：**阿里云DNS**
3. 填写：
   - 名称：`aliyun-dns`
   - AccessKey ID：`<你的阿里云 AK>`
   - AccessKey Secret：`<你的阿里云 SK>`

> AK/SK 建议使用 RAM 子账号，仅授予 `AliyunDNSFullAccess` 权限。

### 3. 配置 SSH 主机授权

1. **授权管理** → **添加**
2. 类型：**SSH 主机**
3. 填写：
   - 名称：`nginx-host`
   - 主机地址：`172.17.0.1`（Docker 网关 IP，**不能用 127.0.0.1**）
   - 端口：`22`
   - 用户名：`root`
   - 认证：密码

### 4. 创建证书流水线

1. **证书流水线** → **添加**
2. 阶段一：证书申请
   - 域名：`*.your-domain.com`、`your-domain.com`
   - DNS 服务商：阿里云，授权选 `aliyun-dns`
   - 证书颁发机构：Let's Encrypt
3. 阶段二：部署到 SSH 主机
   - SSH 授权：`nginx-host`
   - 证书路径：`/etc/nginx/cert/your-domain.com.pem`
   - 私钥路径：`/etc/nginx/cert/your-domain.com.key`
   - 后置命令：`nginx -s reload`

### 5. 手动执行一次验证

点击流水线的 **执行** 按钮，检查日志确认证书申请和部署成功。

### 6. 更新 Nginx 为 HTTPS

证书部署成功后，修改 Nginx 配置添加 SSL：

```nginx
server {
    listen 443 ssl;
    server_name admin.your-domain.com;

    ssl_certificate /etc/nginx/cert/your-domain.com.pem;
    ssl_certificate_key /etc/nginx/cert/your-domain.com.key;

    location / {
        proxy_pass http://127.0.0.1:8002;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# HTTP 自动跳转 HTTPS
server {
    listen 80;
    server_name admin.your-domain.com;
    return 301 https://$host$request_uri;
}
```

> 详细配置参见 `docs/certd-ssl-management.md`

---

## 八、智控台配置

部署完成后，访问智控台进行以下配置。

### 1. 登录智控台

访问 `http://<服务器IP>:8002`（或 `https://admin.your-domain.com`）

### 2. 模型配置

顶部导航 → **模型配置** → 各标签下配置：

| 标签 | 说明 | 操作 |
|---|---|---|
| 知识库 | RAGFlow 对接 | 编辑 RAG_RAGFlow，填服务地址 + API Key |

#### RAGFlow 配置

1. 知识库标签 → 找到 `RAG_RAGFlow` → **修改**
2. 服务地址：`http://<服务器内网IP>:8008`
3. API 密钥：从 RAGFlow 管理界面获取（见下一节）
4. 保存

#### 其他模型配置

按需在对应标签下配置声纹识别、MCP 接入点等服务地址。

### 3. 上下文源配置

1. 进入智能体 → **角色配置**
2. 找到 **上下文源** → **编辑源**
3. 添加接口地址：`http://172.17.0.1:8081/mental-health`
4. 保存

---

## 九、RAGFlow 知识库配置

### 1. 访问 RAGFlow

访问 `http://<服务器IP>:8008`（或 `https://rag.your-domain.com`）

### 2. 注册账号

点击 **Sign Up**，注册一个管理员账号。

> 注册后可在 RAGFlow 的 `.env` 中设置 `REGISTER_ENABLED=0` 关闭注册。

### 3. 配置模型

点右上角头像 → 用户设置 → **模型供应商**：

1. 添加模型供应商（如通义千问、智谱AI 等），填入 API Key
2. 设置默认模型：

| 类型 | 推荐模型 |
|---|---|
| LLM | deepseek-v3 / qwen-plus |
| Embedding | embedding-3（智谱）/ text-embedding-v3（通义） |
| VLM | qwen3-vl-plus |
| Rerank | gte-rerank |

### 4. 获取 API Key

用户设置 → **API** → **创建新密钥** → 复制 Token

### 5. 创建知识库

1. 左侧导航 → **知识库** → **新建**
2. 上传文档（PDF/Word/TXT/Excel）
3. 点击 **解析**
4. 解析完成后点 **召回测试** 验证效果

> 详细指南参见 `docs/ragflow-integration.md`

---

## 十、端口与安全组

确保服务器防火墙/云安全组开放以下端口：

| 端口 | 服务 | 必须 |
|---|---|---|
| 22 | SSH | ✅ |
| 80 | HTTP（Nginx） | ✅ |
| 443 | HTTPS（Nginx） | ✅ |
| 8000 | Python AI (WebSocket) | ✅ |
| 8002 | 智控台 Web 前端 | ✅ |
| 8003 | Java API | ✅ |
| 8004 | MCP 接入点 | 可选 |
| 8005 | 声纹识别 | 可选 |
| 8008 | RAGFlow | ✅ |
| 8081 | Context Provider | 内部访问即可 |
| 7001 | Certd SSL 管理 | 配置时开放 |
| 3306 | MySQL | 内部访问即可 |
| 6379 | Redis | 内部访问即可 |
| 1883 | MQTT (TCP) | 可选 |

> 如果配置了 Nginx 反向代理 + 域名，只需开放 80/443/8000 即可。

---

## 十一、常用运维命令

### 服务管理

```bash
cd /root/xiaozhi-server/xiaozhi-esp32-server

bash deploy_server_full.sh status   # 查看状态
bash deploy_server_full.sh start    # 启动所有
bash deploy_server_full.sh stop     # 停止所有
bash deploy_server_full.sh update   # 更新部署
```

### 查看日志

```bash
# 核心服务日志
docker logs xiaozhi-esp32-server -f --tail 100
docker logs xiaozhi-esp32-server-web -f --tail 100

# RAGFlow 日志
docker logs docker-ragflow-cpu-1 -f --tail 100

# Context Provider 日志
docker logs context-provider -f --tail 100

# Certd 日志
docker logs certd -f --tail 100
```

### 重启单个服务

```bash
docker restart xiaozhi-esp32-server       # 重启 AI 服务
docker restart xiaozhi-esp32-server-web   # 重启 Web
docker restart context-provider           # 重启上下文源
docker restart certd                      # 重启 Certd
```

### 数据备份

```bash
# 备份 MySQL 数据
docker exec xiaozhi-esp32-server-db mysqldump -uroot -p<密码> --all-databases > backup_$(date +%Y%m%d).sql

# 备份 Certd 数据
tar -czf certd-data-backup.tar.gz /root/certd-data/

# 备份 RAGFlow 数据
tar -czf ragflow-data-backup.tar.gz /root/xiaozhi-server/ragflow/docker/volumes/
```

---

## 部署检查清单

部署完成后，逐项确认：

- [ ] 智控台可访问（`http://<IP>:8002` 或 `https://admin.your-domain.com`）
- [ ] RAGFlow 可访问（`http://<IP>:8008` 或 `https://rag.your-domain.com`）
- [ ] Context Provider 健康检查通过（`curl http://localhost:8081/health`）
- [ ] Certd 可访问（`http://<IP>:7001`）
- [ ] RAGFlow 模型已配置（LLM + Embedding）
- [ ] RAGFlow API Key 已生成
- [ ] 智控台已配置 RAGFlow 服务地址和 API Key
- [ ] 智控台已配置上下文源（`http://172.17.0.1:8081/mental-health`）
- [ ] 知识库已创建，文档已上传和解析
- [ ] SSL 证书已申请（Certd 流水线执行成功）
- [ ] Nginx HTTPS 配置完成
- [ ] 小智设备能正常连接和对话

---

## 相关文档

| 文档 | 路径 | 说明 |
|---|---|---|
| Certd SSL 管理 | `docs/certd-ssl-management.md` | SSL 证书自动化详细配置 |
| RAGFlow 集成 | `docs/ragflow-integration.md` | RAGFlow 部署与智控台对接 |
| 上下文源集成 | `docs/context-provider-integration.md` | 上下文源 API 规范与配置 |
| Docker 构建 | `docs/docker-build.md` | Docker 镜像构建说明 |
| 部署脚本 | `deploy_server_full.sh` | 一键部署脚本（7 个服务） |
