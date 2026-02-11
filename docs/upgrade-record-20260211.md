# 小新 ESP32 Server 代码升级记录

> **升级日期**：2026-02-11
> **操作人**：chenxueyuan
> **分叉基点**：`d28750da`（2025-09-23）
> **目标版本**：upstream/main（v0.9.1+）
> **合并规模**：648+ commits，跨越 8 个版本（v0.8.3 → v0.9.1+）

---

## 一、项目背景

### 1.1 项目结构

- **上游仓库**：`xinnan-tech/xiaozhi-esp32-server`（upstream）
- **Fork 仓库**：`froest2012/xiaozhi-esp32-server`（origin）
- **工作分支**：`dev-20250923`
- **分叉基点**：`d28750da`（2025-09-23）

### 1.2 四个子模块

| 子模块 | 技术栈 | 说明 |
|---|---|---|
| `main/manager-web/` | Vue 2 + Ant Design Vue | 前端智控台（改动最大） |
| `main/manager-api/` | Java 21 + Maven + Shiro | 后端 API 服务 |
| `main/xiaozhi-server/` | Python 3.10 | AI 核心服务 |
| `main/manager-mobile/` | uni-app | 移动端 |

### 1.3 自定义改动（Fork 独有功能）

- **manager-web**：HealthReport（心理健康报告+PDF导出）、VoicePrint（声纹识别UI）、roleConfig、WifiGuideDialog、HeaderBar/home/login 等 UI 改造、youth-mental-health-theme.css、i18n 三语言文件、新增 echarts/html2canvas/jspdf/lucide-vue/element-ui 依赖
- **manager-api**：riskAssessment 完整模块（风险评估，约 4222 行新增）、阿里云 SMS 短信、Shiro 权限调整、Redis 扩展
- **xiaozhi-server**：wav 音频转码、config.yaml 微调、helloHandle.py 小改
- **manager-mobile**：开发环境配置微调

### 1.4 服务器部署架构

通过 `update2.sh` + `docker-compose_all_custom.yml` 部署，包含四个 Docker 服务：

| 服务 | 容器名 | 端口 |
|---|---|---|
| Python AI 服务 | `xiaozhi-esp32-server` | 8000、8003 |
| Web/Java API 服务 | `xiaozhi-esp32-server-web` | 8002 |
| MySQL | `xiaozhi-esp32-server-db` | 3306 |
| Redis | `xiaozhi-esp32-server-redis` | 6379（expose） |

**更新脚本** `update2.sh`：
```bash
cd xiaozhi-esp32-server
git pull
cd ..
docker compose -f docker-compose_all_custom.yml build xiaozhi-esp32-server xiaozhi-esp32-server-web
docker compose -f docker-compose_all_custom.yml down
docker compose -f docker-compose_all_custom.yml up -d
```

---

## 二、合并过程与冲突解决

### 2.1 合并策略

上游领先分叉基点 648+ commits，冲突面积大。主要冲突集中在：

- `main/manager-web/src/router/index.js` — 路由定义
- `main/manager-web/src/i18n/zh_CN.js` — 中文翻译
- `main/manager-web/src/i18n/en.js` — 英文翻译
- `main/manager-web/src/i18n/zh_TW.js` — 繁体中文翻译

### 2.2 冲突解决详情

#### 2.2.1 路由文件 `router/index.js`

**问题**：存在两个冲突区域，涉及路由数组定义。上游新增了知识库管理、文档上传管理等多个路由，我方有自定义路由如 `health-report`。

**解决方案**：
- 采用上游格式作为基础
- 合并我方的 `health-report` 路由和上游新增路由
- 清除重复路由，统一路由数组格式
- 确保所有功能路由均存在且 meta 信息完整

#### 2.2.2 i18n 翻译文件（zh_CN.js / en.js / zh_TW.js）

**问题**：翻译文件包含大量自定义 key（mcpToolCall、voicePrint、wifiGuide、healthReport），上游也有大量新增翻译。

**解决方案**：
- 先 checkout 上游版本，确保上游新增翻译完整
- 再补回自定义翻译 key 到文件末尾
- 三种语言文件均采用相同策略

### 2.3 Java 兼容性修复

#### 2.3.1 `List.getFirst()` → `List.get(0)`

**问题**：上游代码使用了 Java 21 的 `List.getFirst()` 方法，在 Java 17 环境下编译失败。

**涉及文件**：
- `main/manager-api/src/main/java/xiaozhi/modules/device/service/impl/OtaServiceImpl.java`（第 69 行）
- `main/manager-api/src/main/java/xiaozhi/modules/sys/service/impl/SysUserServiceImpl.java`（第 59 行）

**解决方案**：将 `getFirst()` 替换为 `get(0)`，兼容 Java 17。

#### 2.3.2 `getUserAgents` 方法签名变更

**问题**：上游 `AgentService.getUserAgents()` 方法签名从 1 个参数变为 3 个参数 `(Long userId, String keyword, String searchType)`，导致移动端 `MobileAgentController.java` 调用参数不匹配。

**解决方案**：移动端调用传 `null` 表示不搜索：
```java
agentService.getUserAgents(userId, null, null)
```

---

## 三、本次更新的新增功能（按版本线）

### 3.1 分叉前已有 vs 本次真正新增

| 功能 | 分叉时已有 | 本次新增 |
|---|:---:|:---:|
| 知识库（RAGFlow） | ❌ | ✅ |
| 音色克隆（火山引擎） | ⚠️ 文档已有 | ✅ 大幅重构 |
| PowerMem 智能记忆 | ❌ | ✅ |
| 上下文源（Context Provider） | ❌ | ✅ |
| 语音盒子在线烧录 | ❌ | ✅ |
| Live2D 虚拟形象 | ❌ | ✅ |
| 拍照识物 | ❌ | ✅ |
| ARM64 Docker 支持 | ❌ | ✅ |
| SM2 国密加密 | ❌ | ✅ |
| WebSocket Token 认证 | ❌ | ✅ |
| 系统功能配置页面 | ❌ | ✅ |
| 声纹识别 | ✅ 已有 | — |
| MCP 接入点 | ✅ 已有 | — |
| MQTT 网关 | ✅ 已有 | — |
| HomeAssistant | ✅ 已有 | — |
| 阿里云短信 | ✅ 已有 | — |
| Fish-Speech / Index-TTS | ✅ 已有 | — |

### 3.2 各版本详细变更

#### v0.8.3 — 安全与体验升级
- SM2 国密加密：登录、注册、密码重置全链路加密传输
- 聊天记录导出：支持下载智能体的对话记录
- 智能体搜索：首页新增搜索功能
- 移动端优化：注册/登录页面交互改进

#### v0.8.5 — 音色克隆
- 音色克隆（火山引擎）：完整的音色资源管理、音频上传、一键复刻、多用户分配
- OTA Token 认证：设备连接增加安全令牌校验
- 测试页面重构：使用 OTA 返回的连接信息和认证信息

#### v0.8.6 — MCP 能力增强
- MCP Streamable HTTP 协议：服务端 MCP 新增支持 Streamable HTTP 传输
- 设备隐私保护：默认隐藏设备 ID 敏感信息，按需开启
- TTS 音频发送延迟：可配置音频包发送间隔
- 自定义系统提示词模板：支持自定义 `agent-base-prompt.txt`
- 测试页面 MCP 工具编辑：可自定义添加/编辑 MCP Tools

#### v0.8.8 — 知识库
- 知识库（RAGFlow 集成）：完整的知识库管理 — 创建、上传文档、解析、召回测试、智能体关联
- 多语言扩展：新增德语、越南语
- 音频测试页面：新增打断功能、音波样式优化、声音卡顿修复

#### v0.8.9 — 性能与稳定性
- 内存泄漏修复：GC 全局化，避免频繁触发 GIL 锁
- 未授权设备管理：会话周期管理，防止资源占用
- RAGFlow 教程升级：支持 v0.22.0 版本

#### v0.8.10 — 上下文感知
- 上下文源（Context Provider）：小新唤醒时自动获取外部系统数据注入 Prompt
- WebSocket 认证：默认开启 `server.auth.enabled`
- 系统功能配置页面：统一管理知识库、音色克隆、声纹识别等功能开关
- 长按说话：不走 VAD 直接触发 ASR，降低延迟
- OTA 固件自动升级：单模块部署支持自动检测版本推送固件
- 火山 TTS 情感字段：支持多情感音色

#### v0.8.11 — 智能记忆
- PowerMem 智能记忆：基于本地 LLM 的对话记忆总结 + 用户画像自动提取
- 阿里百炼流式 ASR：新增 paraformer-realtime-v2 实时语音识别
- 豆包流式多语种识别：支持中英文、方言等多语种
- MCP 并发初始化优化：串行改并发 + 超时机制
- MQTT 设备在线状态：仅在 MQTT 可用时显示设备状态列
- 多语言 Logo：登录/注册/首页根据语言切换 Logo

#### v0.9.1 — 数字人 & 在线烧录
- 语音盒子在线烧录：通过智控台直接在线烧录 ESP32 固件
- 自定义主题 & 表情：支持上传自定义表情固件
- Live2D 虚拟形象：集成 Live2D 数字人（含男性/女性角色切换）
- 拍照识物：测试页面集成摄像头 + 视觉分析
- ARM64 Docker 镜像：支持 ARM 架构服务器部署
- 摄像头验证码绑定：通过摄像头扫码绑定设备
- 系统错误默认回复：可配置系统异常时的兜底回复

#### v0.9.1+ — 最新（未发版）
- PowerMem 升级至 0.3.1：支持角色过滤
- 模型配置启用开关：支持关闭/启用模型配置
- 前后摄像头切换
- 数据库清理：删除旧的 `ai_voiceprint` 表

---

## 四、新功能部署方案

### 4.1 核心升级（必做）

运行现有 `update2.sh`，Liquibase 自动执行数据库迁移：

```bash
cd xiaozhi-esp32-server && git pull && cd ..
docker compose -f docker-compose_all_custom.yml build xiaozhi-esp32-server xiaozhi-esp32-server-web
docker compose -f docker-compose_all_custom.yml down
docker compose -f docker-compose_all_custom.yml up -d
```

**Redis 端口调整**（如需接入 RAGFlow 等外部服务）：

```yaml
# docker-compose_all_custom.yml 中 Redis 需从 expose 改为 ports
xiaozhi-esp32-server-redis:
    image: redis
    ports:
      - "6379:6379"
```

### 4.2 知识库（RAGFlow）

**类型**：独立 Docker 服务 | **端口**：8008

1. 创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS rag_flow CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'rag_flow'@'%' IDENTIFIED BY 'infini_rag_flow';
GRANT ALL PRIVILEGES ON rag_flow.* TO 'rag_flow'@'%';
FLUSH PRIVILEGES;
```

2. 克隆 RAGFlow（v0.22.0），修改配置共用现有 MySQL/Redis
3. `.env` 关键配置：`MYSQL_HOST=host.docker.internal`、`REDIS_HOST=host.docker.internal`
4. 智控台配置：参数字典 → 系统功能配置 → 勾选「知识库」→ 模型配置 → 填写 RAGFlow 地址和 API Key

**详细步骤**：见 `docs/ragflow-integration.md`

### 4.3 音色克隆（火山引擎）

**类型**：纯智控台配置 | **端口**：无

1. 火山引擎控制台开通「语音合成大模型 + 声音复刻大模型」
2. 获取 App Id、Access Token、声音 ID（S_xxxxx）
3. 智控台 → 模型配置 → 语音合成 → 「火山双流式语音合成」→ 填入配置
4. 参数字典 → 系统功能配置 → 勾选「音色克隆」
5. 音色克隆 → 音色资源 → 新增 → 分配给用户

**详细步骤**：见 `docs/huoshan-streamTTS-voice-cloning.md`

### 4.4 PowerMem 智能记忆

**类型**：配置文件修改 | **端口**：无

编辑 `data/.config.yaml`：

```yaml
selected_module:
  Memory: powermem

Memory:
  powermem:
    type: powermem
    enable_user_profile: true
    llm:
      provider: openai
      config:
        api_key: 你的密钥
        model: glm-4-flash
        openai_base_url: https://open.bigmodel.cn/api/paas/v4/
    embedder:
      provider: openai
      config:
        api_key: 你的密钥
        model: embedding-3
        openai_base_url: https://open.bigmodel.cn/api/paas/v4/
    vector_store:
      provider: sqlite
      config: {}
```

重启 Python 服务：`docker compose -f docker-compose_all_custom.yml restart xiaozhi-esp32-server`

**详细步骤**：见 `docs/powermem-integration.md`

### 4.5 上下文源（Context Provider）

**类型**：智控台配置 | **端口**：无

智能体 → 配置角色 → 上下文源 → 编辑源 → 添加 HTTP API 地址

API 返回格式：`{ "code": 0, "data": { "客厅温度": "26℃" } }`

**详细步骤**：见 `docs/context-provider-integration.md`

### 4.6 语音盒子在线烧录 & Live2D

**类型**：智控台内置 | **端口**：无

升级智控台后直接在页面中使用，无需额外部署。

### 4.7 服务器防火墙端口汇总

| 端口 | 协议 | 服务 | 状态 |
|---|---|---|---|
| 8000 | TCP | Python AI 服务 | 已开放 |
| 8002 | TCP | 智控台 | 已开放 |
| 3306 | TCP | MySQL | 已开放 |
| 6379 | TCP | Redis | 需开放（如接入外部服务） |
| 8008 | TCP | RAGFlow 知识库 | 需开放 |

---

## 五、对外展示的核心能力

### 5.1 功能特性清单

**AI 能力**：
- **知识库问答** — 上传文档，让小新基于企业私有知识精准回答
- **智能记忆** — 自动总结对话、提取用户画像，越聊越懂你
- **情境感知** — 唤醒即感知实时数据（体温、待办、设备状态等）
- **多模态交互** — 拍照识物 + Live2D 数字人虚拟形象

**语音能力**：
- **音色克隆** — 上传一段语音，一键复刻专属音色
- **声纹识别** — 识别"谁在说话"，实现个性化对话
- **多语种 ASR** — 支持中英日韩粤方言，流式实时识别
- **多情感 TTS** — 火山引擎双流式语音合成，支持喜怒哀乐多种情感

**平台能力**：
- **MCP 工具扩展** — 通过 MCP 接入点无限扩展能力
- **语音盒子在线烧录** — 智控台一键烧录 ESP32 固件，零门槛上手
- **OTA 空中升级** — 设备固件自动检测版本、自动推送升级

**安全与管理**：
- **SM2 国密加密** — 登录注册全链路加密
- **WebSocket Token 认证** — 设备连接安全校验
- **多语言智控台** — 中/英/繁/德/越五语言

**差异化能力（Fork 独有）**：
- **心理健康报告** — 设备情绪分析 + 风险评估 + PDF 导出
- **风险评估系统** — 完整的风险评估模块
- **声纹识别 UI** — 完整的声纹管理前端界面

### 5.2 推广一句话

> **「知识库 + 音色克隆 + 智能记忆 + 情境感知 + 数字人 + 拍照识物 + 心理健康」**
> —— 从"能说话的玩具"进化为"懂你的智能伙伴"

---

## 六、已知问题与后续计划

### 6.1 已知问题

- 根目录 `manager-web/node_modules/echarts/` 被误提交到 Git，需要清理
- Java 项目 pom.xml 中 `java.version` 为 21，本地开发环境为 Java 17，需要注意兼容性
- Redis 使用 `expose` 模式，需改为 `ports` 才能让外部 Docker 服务访问

### 6.2 后续计划

- [ ] 部署 RAGFlow 知识库服务
- [ ] 配置音色克隆（火山引擎）
- [ ] 接入 PowerMem 智能记忆
- [ ] 测试上下文源功能
- [ ] 清理 Git 中误提交的 node_modules
- [ ] 评估是否需要接入 MQTT 网关（4G 设备场景）

---

## 附录

### A. 关键文件路径

| 文件 | 说明 |
|---|---|
| `docker-compose_all_custom.yml` | Docker Compose 部署配置 |
| `update2.sh` | 自动化更新脚本 |
| `Dockerfile-server` | Python AI 服务镜像构建 |
| `Dockerfile-web` | Web 服务镜像构建（Vue + Java + Nginx） |
| `data/.config.yaml` | Python AI 服务运行时配置 |
| `main/xiaozhi-server/config.yaml` | 默认配置模板 |
| `docs/docker/nginx.conf` | Nginx 反向代理配置 |
| `docs/docker/start.sh` | Docker 容器启动脚本 |

### B. 参考文档

| 文档 | 说明 |
|---|---|
| `docs/Deployment.md` | 单模块 Docker 部署 |
| `docs/Deployment_all.md` | 全模块部署（Docker + 源码） |
| `docs/ragflow-integration.md` | 知识库 RAGFlow 集成 |
| `docs/huoshan-streamTTS-voice-cloning.md` | 火山双流式 TTS + 音色克隆 |
| `docs/voiceprint-integration.md` | 声纹识别集成 |
| `docs/mcp-endpoint-enable.md` | MCP 接入点启用 |
| `docs/powermem-integration.md` | PowerMem 智能记忆集成 |
| `docs/context-provider-integration.md` | 上下文源使用教程 |
| `docs/mqtt-gateway-integration.md` | MQTT 网关部署 |
| `docs/homeassistant-integration.md` | HomeAssistant 集成 |
| `docs/ali-sms-integration.md` | 阿里云短信集成 |
| `docs/ota-upgrade-guide.md` | OTA 固件升级配置 |
| `docs/fish-speech-integration.md` | Fish-Speech TTS 集成 |
| `docs/index-stream-integration.md` | Index-TTS 集成 |
