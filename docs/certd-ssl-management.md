# Certd SSL 证书自动化管理

## 概述

使用开源工具 [Certd](https://github.com/certd/certd) 自动管理 SSL 证书，实现证书的自动申请、自动续期、自动部署，告别手动更换证书。

- **替代方案**：阿里云免费证书（3 个月有效期，需手动续期）
- **新方案**：Let's Encrypt 免费证书（自动续期，永不过期）
- **泛域名**：一张 `*.chat-ai.cc` 证书覆盖所有子域名

## 服务信息

| 项目 | 值 |
|---|---|
| 服务器 | 106.15.33.103 |
| 容器名 | certd |
| Web 管理界面 | http://106.15.33.103:7001 |
| 数据目录 | /root/certd-data |
| 镜像 | registry.cn-shenzhen.aliyuncs.com/handsfree/certd:latest |
| 端口 | 7001 (HTTP), 7002 (HTTPS) |

## 一、部署

### Docker 部署（已完成）

```bash
# 创建数据目录
mkdir -p /root/certd-data

# 拉取镜像（国内镜像）
docker pull registry.cn-shenzhen.aliyuncs.com/handsfree/certd:latest

# 启动容器
docker run -d --name certd \
  --restart always \
  -p 7001:7001 \
  -p 7002:7002 \
  -v /root/certd-data:/app/data \
  registry.cn-shenzhen.aliyuncs.com/handsfree/certd:latest
```

### 验证

```bash
# 检查容器状态
docker ps --filter name=certd

# 访问管理界面
curl -sI http://127.0.0.1:7001
```

## 二、初始配置

### 1. 登录管理界面

访问 `http://106.15.33.103:7001`，首次访问需注册管理员账号。

### 2. 配置阿里云 DNS 授权

证书申请需要通过 DNS 验证域名所有权，需要配置阿里云 DNS API 授权：

1. 进入 **系统设置 → 授权管理 → 新增授权**
2. 选择类型：**阿里云 DNS**
3. 填写：
   - AccessKey ID：（阿里云 RAM 子账号的 AK，需要 DNS 管理权限）
   - AccessKey Secret：对应的 SK

> **安全建议**：创建 RAM 子账号，仅授予 `AliyunDNSFullAccess` 权限，不要使用主账号 AK。

### 3. 配置 SSH 部署授权

证书申请成功后需要部署到 Nginx 服务器：

1. 进入 **系统设置 → 授权管理 → 新增授权**
2. 选择类型：**SSH 主机**
3. 填写：
   - 主机地址：`127.0.0.1`（certd 和 Nginx 在同一台服务器）
   - 端口：`22`
   - 用户名：`root`
   - 认证方式：密码 或 私钥

## 三、创建证书流水线

### 1. 新建流水线

进入 **流水线 → 新建流水线**

### 2. 添加证书申请任务

- **任务类型**：证书申请
- **域名列表**：
  ```
  *.chat-ai.cc
  chat-ai.cc
  ```
- **验证方式**：DNS-01（阿里云 DNS）
- **DNS 授权**：选择上面配置的阿里云 DNS 授权
- **证书机构**：Let's Encrypt（默认，免费）

### 3. 添加部署任务

- **任务类型**：部署到主机
- **SSH 授权**：选择上面配置的 SSH 授权
- **证书文件部署路径**：
  - 证书文件：`/etc/nginx/cert/chat-ai.cc.pem`
  - 私钥文件：`/etc/nginx/cert/chat-ai.cc.key`
- **部署后执行命令**：
  ```bash
  # 复制泛域名证书给各子域名（它们共享同一张证书）
  cp /etc/nginx/cert/chat-ai.cc.pem /etc/nginx/cert/admin.chat-ai.cc.pem
  cp /etc/nginx/cert/chat-ai.cc.key /etc/nginx/cert/admin.chat-ai.cc.key
  cp /etc/nginx/cert/chat-ai.cc.pem /etc/nginx/cert/rag.chat-ai.cc.pem
  cp /etc/nginx/cert/chat-ai.cc.key /etc/nginx/cert/rag.chat-ai.cc.key
  # 重载 Nginx
  nginx -s reload
  ```

### 4. 配置定时执行

- **定时策略**：每天凌晨 3:00 执行一次
- Certd 会自动判断证书是否即将过期，仅在需要时才申请新证书

### 5. 手动执行一次

点击 **立即执行**，验证整个流程是否正常。

## 四、当前域名证书对应关系

| 域名 | Nginx 配置 | 证书文件 | 来源 |
|---|---|---|---|
| chat-ai.cc | /etc/nginx/nginx.conf | /etc/nginx/cert/chat-ai.cc.pem | Certd 自动管理 |
| admin.chat-ai.cc | /etc/nginx/nginx.conf | /etc/nginx/cert/admin.chat-ai.cc.pem | Certd 自动管理 |
| rag.chat-ai.cc | /etc/nginx/conf.d/rag.chat-ai.cc.conf | /etc/nginx/cert/rag.chat-ai.cc.pem | Certd 自动管理 |

> 所有证书实际是同一张泛域名证书 `*.chat-ai.cc` 的副本。

## 五、运维命令

```bash
# 查看 certd 容器状态
docker ps --filter name=certd

# 查看 certd 日志
docker logs certd --tail 50

# 重启 certd
docker restart certd

# 升级 certd
docker pull registry.cn-shenzhen.aliyuncs.com/handsfree/certd:latest
docker stop certd && docker rm certd
docker run -d --name certd \
  --restart always \
  -p 7001:7001 \
  -p 7002:7002 \
  -v /root/certd-data:/app/data \
  registry.cn-shenzhen.aliyuncs.com/handsfree/certd:latest
```

## 六、安全建议

1. **端口安全**：7001 端口仅在配置时开放，配置完成后建议通过安全组限制访问
2. **使用 RAM 子账号**：不要使用阿里云主账号 AK/SK
3. **定期备份**：`/root/certd-data` 目录包含所有配置和证书数据
4. **防火墙规则**：建议仅允许管理员 IP 访问 7001 端口

## 七、故障排查

| 问题 | 排查方法 |
|---|---|
| 证书申请失败 | 检查阿里云 DNS 授权是否正确，AK/SK 权限是否包含 DNS 管理 |
| 部署失败 | 检查 SSH 授权配置，确认目标目录权限 |
| Nginx reload 失败 | 检查证书文件路径是否正确，`nginx -t` 验证配置 |
| 容器无法启动 | `docker logs certd` 查看错误日志 |
