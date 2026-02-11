# 小新ESP32管理平台 Web功能模块梳理

## 1. 登录注册相关

**相关文件:**
- `src/views/login.vue` - 登录页面
- `src/views/register.vue` - 注册页面
- `src/views/retrievePassword.vue` - 找回密码页面
- `src/apis/module/user.js` - 用户相关API

**主要功能:**
- 用户登录
- 用户注册
- 找回密码
- 短信验证码发送
- 图形验证码

**API接口:**
- `/user/login` - 登录
- `/user/register` - 注册
- `/user/smsVerification` - 发送短信验证码
- `/user/captcha` - 获取验证码
- `/user/retrieve-password` - 找回密码

## 2. 智能体管理

**相关文件:**
- `src/views/home.vue` - 主页(包含智能体列表)
- `src/components/AddWisdomBodyDialog.vue` - 添加智能体对话框
- `src/components/DeviceItem.vue` - 设备项组件
- `src/components/AddDeviceDialog.vue` - 添加设备对话框
- `src/components/ChatHistoryDialog.vue` - 聊天历史记录对话框
- `src/apis/module/agent.js` - 智能体相关API
- `src/apis/module/device.js` - 设备相关API

**主要功能:**
- 智能体列表展示
- 添加/删除智能体
- 智能体配置
- 设备绑定/解绑
- 聊天历史记录查看
- MCP工具接入

**API接口:**
- `/agent/list` - 获取智能体列表
- `/agent` - 添加智能体
- `/agent/{agentId}` - 获取/更新/删除智能体
- `/agent/{agentId}/sessions` - 获取会话列表
- `/agent/{agentId}/chat-history/{sessionId}` - 获取聊天记录
- `/agent/mcp/address/{agentId}` - 获取MCP接入点地址
- `/agent/mcp/tools/{agentId}` - 获取MCP工具列表
- `/device/bind/{agentId}` - 获取绑定设备/绑定设备
- `/device/unbind` - 解绑设备
- `/device/update/{id}` - 更新设备信息

## 3. 模型配置

**相关文件:**
- `src/views/ModelConfig.vue` - 模型配置页面
- `src/components/AddModelDialog.vue` - 添加模型对话框
- `src/components/ModelEditDialog.vue` - 编辑模型对话框
- `src/components/TtsModel.vue` - TTS模型组件
- `src/components/EditVoiceDialog.vue` - 编辑音色对话框
- `src/views/ProviderManagement.vue` - 供应商管理页面
- `src/components/ProviderDialog.vue` - 供应商对话框
- `src/apis/module/model.js` - 模型相关API
- `src/apis/module/timbre.js` - 音色相关API

**主要功能:**
- 模型列表管理(LLM、TTS、ASR)
- 添加/编辑/删除模型
- 模型启用/禁用
- 设置默认模型
- 音色管理
- 供应商管理

**API接口:**
- `/models/list` - 获取模型列表
- `/models/{modelType}/{provideCode}` - 添加模型
- `/models/{id}` - 获取/删除模型
- `/models/{modelType}/{provideCode}/{id}` - 更新模型
- `/models/enable/{id}/{status}` - 更新模型状态
- `/models/default/{id}` - 设置默认模型
- `/models/{modelType}/provideTypes` - 获取模型供应器列表
- `/models/provider` - 获取/添加/更新供应商
- `/models/provider/delete` - 删除供应商
- `/ttsVoice` - 获取/添加音色
- `/ttsVoice/{id}` - 更新音色
- `/ttsVoice/delete` - 删除音色

## 4. 用户管理

**相关文件:**
- `src/views/UserManagement.vue` - 用户管理页面
- `src/components/ViewPasswordDialog.vue` - 查看密码对话框
- `src/components/ChangePasswordDialog.vue` - 修改密码对话框
- `src/apis/module/admin.js` - 管理员相关API
- `src/apis/module/user.js` - 用户相关API

**主要功能:**
- 用户列表管理
- 删除用户
- 重置用户密码
- 修改用户状态
- 修改个人密码

**API接口:**
- `/admin/users` - 获取用户列表
- `/admin/users/{id}` - 删除用户/重置密码
- `/admin/users/changeStatus/{status}` - 修改用户状态
- `/user/change-password` - 修改密码
- `/user/info` - 获取用户信息

## 5. OTA管理

**相关文件:**
- `src/views/OtaManagement.vue` - OTA管理页面
- `src/components/FirmwareDialog.vue` - 固件对话框
- `src/apis/module/ota.js` - OTA相关API

**主要功能:**
- 固件列表管理
- 添加/编辑/删除固件
- 固件上传
- 获取固件下载链接

**API接口:**
- `/otaMag` - 获取/添加固件列表
- `/otaMag/{id}` - 获取/更新/删除固件
- `/otaMag/upload` - 上传固件
- `/otaMag/getDownloadUrl/{id}` - 获取下载链接

## 6. 参数字典管理

**相关文件:**
- `src/views/ParamsManagement.vue` - 参数管理页面
- `src/components/ParamDialog.vue` - 参数对话框
- `src/views/DictManagement.vue` - 字典管理页面
- `src/components/DictTypeDialog.vue` - 字典类型对话框
- `src/components/DictDataDialog.vue` - 字典数据对话框
- `src/apis/module/admin.js` - 管理员相关API(包含参数管理)
- `src/apis/module/dict.js` - 字典相关API

**主要功能:**
- 参数列表管理
- 添加/编辑/删除参数
- 字典类型管理
- 字典数据管理

**API接口:**
- `/admin/params/page` - 获取参数列表
- `/admin/params` - 添加/更新参数
- `/admin/params/delete` - 删除参数
- `/admin/dict/type/page` - 获取字典类型列表
- `/admin/dict/type/{id}` - 获取字典类型详情
- `/admin/dict/type/save` - 添加字典类型
- `/admin/dict/type/update` - 更新字典类型
- `/admin/dict/type/delete` - 删除字典类型
- `/admin/dict/data/page` - 获取字典数据列表
- `/admin/dict/data/{id}` - 获取字典数据详情
- `/admin/dict/data/save` - 添加字典数据
- `/admin/dict/data/update` - 更新字典数据
- `/admin/dict/data/delete` - 删除字典数据
- `/admin/dict/data/type/{dictType}` - 根据类型获取字典数据

## 7. 其他功能

### 7.1 服务端管理

**相关文件:**
- `src/views/ServerSideManager.vue` - 服务端管理页面
- `src/apis/module/admin.js` - 管理员相关API(包含服务端管理)

**主要功能:**
- 获取WS服务端列表
- 发送WS服务器动作指令

**API接口:**
- `/admin/server/server-list` - 获取WS服务端列表
- `/admin/server/emit-action` - 发送WS服务器动作指令

### 7.2 公共配置

**相关文件:**
- `src/apis/module/user.js` - 用户相关API(包含公共配置)
- `src/store/index.js` - Vuex存储(包含公共配置)

**主要功能:**
- 获取公共配置

**API接口:**
- `/user/pub-config` - 获取公共配置

### 7.3 缓存管理

**相关文件:**
- `src/components/CacheViewer.vue` - 缓存查看器组件
- `src/utils/cacheViewer.js` - 缓存查看工具

**主要功能:**
- 查看缓存状态
- 清除缓存

## 8. 项目结构

```
src/
├── apis/               # API接口
│   ├── api.js          # API入口
│   ├── httpRequest.js  # HTTP请求封装
│   └── module/         # 模块化API
├── assets/             # 静态资源
├── components/         # 组件
├── router/             # 路由
├── store/              # Vuex存储
├── styles/             # 样式
├── utils/              # 工具函数
└── views/              # 页面视图
```

## 9. 技术栈

- Vue.js - 前端框架
- Vuex - 状态管理
- Vue Router - 路由管理
- Element UI - UI组件库
- Axios(Fly) - HTTP请求库
- Service Worker - 离线缓存