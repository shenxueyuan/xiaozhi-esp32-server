# ChatAI 官网运维 Skills（chat-ai.cc）

## 1. 官网代码在哪里（本地仓库）

- **仓库内源码文件**
  - `main/xiaozhi-server/test/commercial-landing.html`
- **说明**
  - 该文件用于生成/替换线上站点的静态首页。
  - 线上实际访问的是 Nginx 的静态目录 `/var/www/chat-ai.cc/index.html`。

## 2. 当前线上访问链接

- **官网**
  - https://chat-ai.cc
- **管理后台**
  - https://admin.chat-ai.cc

## 3. 服务器与落地路径

- **服务器**
  - `106.15.33.103`
- **Nginx 静态站点根目录**
  - `/var/www/chat-ai.cc/`
- **官网首页文件**
  - `/var/www/chat-ai.cc/index.html`

## 3.2 服务器登录方式（已确认）

结论：你之前能够直接 `scp/ssh` 登录并上传部署，是因为 **本机使用 SSH 公钥登录（publickey）**，并非依赖“密码写在本地隐藏文件”。

- **登录用户**
  - `root`
- **端口**
  - `22`
- **认证方式（已验证）**
  - `Authenticated ... using "publickey"`
- **本机实际命中的默认私钥**
  - `~/.ssh/id_rsa`

验证命令（只读）：

```bash
ssh -vvv -o BatchMode=yes -o PreferredAuthentications=publickey root@106.15.33.103 "echo ok"
ssh -G root@106.15.33.103 | egrep -i "identityfile|user|hostname|port"
```

### 3.1 线上“更新网站”的本质

把本地 `commercial-landing.html` 上传并覆盖：

- 本地：`main/xiaozhi-server/test/commercial-landing.html`
- 服务器：`/var/www/chat-ai.cc/index.html`

## 4. Nginx 配置要点（域名/HTTPS/root）

### 4.1 chat-ai.cc（官网 + 业务反向代理）

在服务器 `/etc/nginx/nginx.conf` 中已配置：

- **server_name**
  - `chat-ai.cc` / `www.chat-ai.cc`
- **证书**
  - `ssl_certificate cert/chat-ai.cc.pem;`
  - `ssl_certificate_key cert/chat-ai.cc.key;`
- **静态首页（只匹配根路径）**
  - `location = /` → `root /var/www/chat-ai.cc;` + `index index.html;`
  - `location = /index.html` → `root /var/www/chat-ai.cc;`
- **其余路径（AI 服务代理）**
  - `location /` → `proxy_pass http://127.0.0.1:8000;`

含义：

- 访问 `https://chat-ai.cc/` 返回静态官网首页。
- 访问 `https://chat-ai.cc/xxx` 会被转发到 `127.0.0.1:8000`（后端业务服务）。

### 4.2 HTTP → HTTPS

- `listen 80; server_name chat-ai.cc www.chat-ai.cc;`
- `rewrite ^(.*)$ https://$host$1;`

### 4.3 admin.chat-ai.cc（管理后台反向代理）

- `server_name admin.chat-ai.cc ...`
- `proxy_pass http://127.0.0.1:8002;`

## 5. 发布/更新流程（最常用）

### 5.1 本地修改后，更新线上官网（静态首页）

1. 确认本地文件已保存：
   - `main/xiaozhi-server/test/commercial-landing.html`
2. 上传覆盖线上 `index.html`：

```bash
scp main/xiaozhi-server/test/commercial-landing.html \
  root@106.15.33.103:/var/www/chat-ai.cc/index.html
```

3. 浏览器强刷验证：

- Mac：`Cmd + Shift + R`

### 5.2 需要重载 Nginx 的情况

通常 **只替换静态文件不需要 reload**。

只有当你修改了 `/etc/nginx/nginx.conf` 或 `/etc/nginx/conf.d/*.conf` 才需要：

```bash
ssh root@106.15.33.103 "nginx -t && nginx -s reload"
```

## 6. 常见排查清单（“刷新没更新”）

1. **确认你上传到的路径是否正确**
   - 必须是：`/var/www/chat-ai.cc/index.html`

2. **确认 Nginx 读的 root 是不是这个目录**

```bash
ssh root@106.15.33.103 "nginx -T 2>/dev/null | grep -n '/var/www/chat-ai.cc'"
```

3. **确认浏览器缓存**

- 强制刷新（Cmd+Shift+R）
- 或用无痕窗口访问

4. **确认是否走了 CDN/缓存层**

- 若后续接入 CDN，需在 CDN 侧做刷新/缓存策略。

## 7. 关键命令速查

- 查看 Nginx 完整配置（含 include 展开）：

```bash
ssh root@106.15.33.103 "nginx -T 2>/dev/null | sed -n '1,220p'"
```

- 查看站点目录当前文件：

```bash
ssh root@106.15.33.103 "ls -lah /var/www/chat-ai.cc/"
```

- 查看线上 index.html 最近更新时间：

```bash
ssh root@106.15.33.103 "stat /var/www/chat-ai.cc/index.html"
```

## 8. 约定与建议

- 线上站点以 `/var/www/chat-ai.cc/index.html` 为准。
- 仓库内 `commercial-landing.html` 是“可维护的源码版本”，建议每次上线后保持 git 记录一致。
- 若未来官网变成多页静态站：
  - 建议把站点独立到 `main/manager-web` 或单独目录（如 `website/`），并用 CI/CD 构建产物同步到 `/var/www/chat-ai.cc/`。

## 9. 从 0 到 1 搭建 Skills（可复用流程）

### 9.1 账号与 SSH Key（免密登录）

1. 本机准备 SSH key（推荐 ed25519；你当前可用的是 `~/.ssh/id_rsa`）：

```bash
ssh-keygen -t ed25519 -C "chatai-deploy"
```

2. 把公钥写入服务器：

```bash
ssh-copy-id root@106.15.33.103
```

3. 验证免密：

```bash
ssh root@106.15.33.103 "whoami"
```

> 建议：为这台机器在 `~/.ssh/config` 配置一个 Host 别名（不存密码，只指向 key）。

### 9.2 域名与 DNS

把域名解析到服务器公网 IP：

- `chat-ai.cc` → `106.15.33.103`（A 记录）
- `www.chat-ai.cc` → `106.15.33.103`（A 记录）
- `admin.chat-ai.cc` → `106.15.33.103`（A 记录）

### 9.3 证书（HTTPS）与证书文件位置

当前 Nginx 配置中引用的证书路径为相对路径：

- `ssl_certificate cert/chat-ai.cc.pem;`
- `ssl_certificate_key cert/chat-ai.cc.key;`

说明：

- 证书文件通常位于 Nginx 配置目录下的 `cert/` 子目录（常见是 `/etc/nginx/cert/`）。
- 如需自动化证书续期，可使用 Certd（仓库已有：`docs/certd-ssl-management.md`）。

### 9.4 Nginx 站点配置（核心要点）

1. 配置文件位置：

- 主配置：`/etc/nginx/nginx.conf`
- include：`/etc/nginx/conf.d/*.conf`

2. chat-ai.cc 的关键逻辑：

- `location = /`：返回静态官网首页（`/var/www/chat-ai.cc/index.html`）
- `location /`：其余路径反向代理到业务服务（`127.0.0.1:8000`）

3. 修改配置后的操作：

```bash
ssh root@106.15.33.103 "nginx -t && nginx -s reload"
```

### 9.5 静态站点目录准备（权限/归属）

```bash
ssh root@106.15.33.103 "mkdir -p /var/www/chat-ai.cc && ls -lah /var/www/chat-ai.cc"
```

### 9.6 首次发布与后续更新

首次发布（或任何更新）都是覆盖 `index.html`：

```bash
scp main/xiaozhi-server/test/commercial-landing.html \
  root@106.15.33.103:/var/www/chat-ai.cc/index.html
```

### 9.7 访问验证

1. 查看线上文件时间戳是否更新：

```bash
ssh root@106.15.33.103 "stat /var/www/chat-ai.cc/index.html"
```

2. 浏览器验证：

- https://chat-ai.cc
- 强刷（Mac：`Cmd + Shift + R`）

3. 命令行验证（可选）：

```bash
curl -I https://chat-ai.cc/
```

### 9.8 回滚策略（建议）

每次上线前先在服务器备份一份：

```bash
ssh root@106.15.33.103 "cp -a /var/www/chat-ai.cc/index.html /var/www/chat-ai.cc/index.html.$(date +%F-%H%M%S).bak"
```

回滚：

```bash
ssh root@106.15.33.103 "ls -1t /var/www/chat-ai.cc/index.html.*.bak | head -1"
ssh root@106.15.33.103 "cp -a /var/www/chat-ai.cc/index.html.<timestamp>.bak /var/www/chat-ai.cc/index.html"
```
