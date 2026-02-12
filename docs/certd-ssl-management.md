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

### 登录信息

| 项目 | 值 |
|---|---|
| Certd 管理界面 | http://106.15.33.103:7001 |
| 用户名 | admin |
| 密码 | 123456（默认，请修改） |

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

访问 `http://106.15.33.103:7001`，使用默认管理员账号登录：

- **用户名**：`admin`
- **密码**：`123456`

> ⚠️ **登录后请立即修改密码！**

### 2. 配置阿里云 DNS 授权

证书申请需要通过 DNS 验证域名所有权，需要配置阿里云 DNS API 授权：

1. 进入左侧菜单 **设置** → **授权管理** → **添加**
2. 选择类型：**阿里云**
3. 填写：
   - 名称：`aliyun-dns`
   - AccessKey ID：`<在 Certd 管理界面中查看，或联系管理员获取>`
   - AccessKey Secret：`<在 Certd 管理界面中查看，或联系管理员获取>`

> **安全建议**：此 AK 为 RAM 子账号，仅授予 `AliyunDNSFullAccess` 权限。

**已配置 ✅**（2026-02-12）

### 3. 配置 SSH 部署授权

证书申请成功后需要部署到 Nginx 服务器：

1. 进入左侧菜单 **设置** → **授权管理** → **添加**
2. 选择类型：**SSH 主机**
3. 填写：
   - 名称：`nginx-host`
   - 主机地址：`172.17.0.1`（Docker 网关 IP，certd 容器内访问宿主机，**不能用 127.0.0.1**）
   - 端口：`22`
   - 用户名：`root`
   - 认证方式：密码（服务器 root 密码）

**已配置 ✅**（2026-02-12）

## 三、创建证书流水线

### 流水线名称：`*.chat-ai.cc证书自动化`

### 阶段一：证书申请任务

| 配置项 | 值 |
|---|---|
| 证书域名 | `*.chat-ai.cc`、`chat-ai.cc` |
| 邮箱 | shenxueyuan@vip.qq.com |
| 域名验证方式 | DNS直接验证 |
| DNS解析服务商 | 阿里云 |
| DNS解析授权 | aliyun-dns |
| 证书颁发机构 | Let's Encrypt（免费） |
| 加密算法 | RSA 2048 |

### 阶段二：主机-部署证书到SSH主机

| 配置项 | 值 |
|---|---|
| 域名证书 | 域名证书【from: 申请证书】 |
| 证书格式 | pem/crt |
| 证书保存路径 | `/etc/nginx/cert/chat-ai.cc.pem` |
| 私钥保存路径 | `/etc/nginx/cert/chat-ai.cc.key` |
| 主机登录配置 | nginx-host |
| 上传方式 | sftp |
| 自动创建远程目录 | 开启 |
| 后置命令 | 见下方 |

**后置命令**：
```bash
cp /etc/nginx/cert/chat-ai.cc.pem /etc/nginx/cert/admin.chat-ai.cc.pem && cp /etc/nginx/cert/chat-ai.cc.key /etc/nginx/cert/admin.chat-ai.cc.key && cp /etc/nginx/cert/chat-ai.cc.pem /etc/nginx/cert/rag.chat-ai.cc.pem && cp /etc/nginx/cert/chat-ai.cc.key /etc/nginx/cert/rag.chat-ai.cc.key && nginx -s reload
```

### 定时触发

- **下次执行时间**：每天凌晨 02:14（已由 Certd 自动配置）
- Certd 会自动判断证书是否即将过期（到期前 30 天），仅在需要时才申请新证书

### 手动执行验证

**已验证通过 ✅**（2026-02-12 16:32）

首次执行结果：
- 证书申请：✅ 成功
- 部署到SSH主机：✅ 成功
- 证书有效期：2026-02-12 ~ 2026-05-13（90天）

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
