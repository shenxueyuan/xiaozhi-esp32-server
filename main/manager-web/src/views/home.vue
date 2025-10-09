<template>
  <div class="welcome">
    <!-- 公共头部 -->
    <HeaderBar :devices="devices" @search="handleSearch" @search-reset="handleSearchReset" />
    <el-main style="padding: 20px;display: flex;flex-direction: column;">
      <div>
        <!-- 首页内容 -->
        <div class="add-device">
          <div class="add-device-bg">
            <div class="hellow-text" style="margin-top: 30px;">
              <span class="wave-emoji">🌱</span> {{ $t('home.greeting') }}
            </div>
            <div class="hellow-text">
              {{ $t('home.wish') }}
              <div style="display: inline-block;color: #4A90A4;">
                <span class="sparkle-emoji">🌟</span>
              </div>
            </div>
            <div class="hi-hint">
              {{ $t('home.subtitle') }} <span class="heart-emoji">💚</span>
            </div>
            <div class="add-device-btn" @click="showAddDialog">
              <div class="add-btn-content">
                <span class="add-btn-text">{{ $t('home.addAgent') }}</span>
                <i class="el-icon-plus add-btn-icon"></i>
              </div>
            </div>
          </div>
        </div>
        <div class="device-list-container">
          <template v-if="isLoading">
            <div v-for="i in skeletonCount" :key="'skeleton-' + i" class="skeleton-item">
              <div class="skeleton-image"></div>
              <div class="skeleton-content">
                <div class="skeleton-line"></div>
                <div class="skeleton-line-short"></div>
              </div>
            </div>
          </template>

          <template v-else>
            <DeviceItem v-for="(item, index) in devices" :key="index" :device="item" @configure="goToRoleConfig"
              @deviceManage="handleDeviceManage" @delete="handleDeleteAgent" @chat-history="handleShowChatHistory" />
          </template>
        </div>
      </div>
      <AddWisdomBodyDialog :visible.sync="addDeviceDialogVisible" @confirm="handleWisdomBodyAdded" />
    </el-main>
    <el-footer>
      <version-footer />
    </el-footer>
    <chat-history-dialog :visible.sync="showChatHistory" :agent-id="currentAgentId" :agent-name="currentAgentName" />
  </div>

</template>

<script>
import Api from '@/apis/api';
import AddWisdomBodyDialog from '@/components/AddWisdomBodyDialog.vue';
import ChatHistoryDialog from '@/components/ChatHistoryDialog.vue';
import DeviceItem from '@/components/DeviceItem.vue';
import HeaderBar from '@/components/HeaderBar.vue';
import VersionFooter from '@/components/VersionFooter.vue';

export default {
  name: 'HomePage',
  components: { DeviceItem, AddWisdomBodyDialog, HeaderBar, VersionFooter, ChatHistoryDialog },
  data() {
    return {
      addDeviceDialogVisible: false,
      devices: [],
      originalDevices: [],
      isSearching: false,
      searchRegex: null,
      isLoading: true,
      skeletonCount: localStorage.getItem('skeletonCount') || 8,
      showChatHistory: false,
      currentAgentId: '',
      currentAgentName: ''
    }
  },

  mounted() {
    this.fetchAgentList();
  },

  methods: {
    showAddDialog() {
      this.addDeviceDialogVisible = true
    },
    goToRoleConfig() {
      // 点击配置角色后跳转到角色配置页
      this.$router.push('/role-config')
    },
    handleWisdomBodyAdded(res) {
      this.fetchAgentList();
      this.addDeviceDialogVisible = false;
    },
    handleDeviceManage() {
      this.$router.push('/device-management');
    },
    handleSearch(regex) {
      this.isSearching = true;
      this.searchRegex = regex;
      this.applySearchFilter();
    },
    handleSearchReset() {
      this.isSearching = false;
      this.searchRegex = null;
      this.devices = [...this.originalDevices];
    },
    applySearchFilter() {
      if (!this.isSearching || !this.searchRegex) {
        this.devices = [...this.originalDevices];
        return;
      }

      this.devices = this.originalDevices.filter(device => {
        return this.searchRegex.test(device.agentName);
      });
    },
    // 搜索更新智能体列表
    handleSearchResult(filteredList) {
      this.devices = filteredList; // 更新设备列表
    },
    // 获取智能体列表
    fetchAgentList() {
      this.isLoading = true;
      Api.agent.getAgentList(({ data }) => {
        if (data?.data) {
          this.originalDevices = data.data.map(item => ({
            ...item,
            agentId: item.id
          }));

          // 动态设置骨架屏数量（可选）
          this.skeletonCount = Math.min(
            Math.max(this.originalDevices.length, 3), // 最少3个
            10 // 最多10个
          );

          this.handleSearchReset();
        }
        this.isLoading = false;
      }, (error) => {
        console.error('Failed to fetch agent list:', error);
        this.isLoading = false;
      });
    },
    // 删除智能体
    handleDeleteAgent(agentId) {
      this.$confirm(this.$t('home.confirmDeleteAgent'), '提示', {
        confirmButtonText: this.$t('button.ok'),
        cancelButtonText: this.$t('button.cancel'),
        type: 'warning'
      }).then(() => {
        Api.agent.deleteAgent(agentId, (res) => {
          if (res.data.code === 0) {
            this.$message.success({
              message: this.$t('home.deleteSuccess'),
              showClose: true
            });
            this.fetchAgentList(); // 刷新列表
          } else {
            this.$message.error({
              message: res.data.msg || this.$t('home.deleteFailed'),
              showClose: true
            });
          }
        });
      }).catch(() => { });
    },
    handleShowChatHistory({ agentId, agentName }) {
      this.currentAgentId = agentId;
      this.currentAgentName = agentName;
      this.showChatHistory = true;
    }
  }
}
</script>

<style scoped>
.welcome {
  min-width: 900px;
  min-height: 506px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 50%, #f1f5f9 100%);
  background-size: cover;
  position: relative;
}

.welcome::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 20%, rgba(102, 187, 106, 0.05), transparent 50%),
              radial-gradient(circle at 70% 80%, rgba(165, 214, 167, 0.05), transparent 50%);
  pointer-events: none;
  z-index: 1;
}

.add-device {
  height: 200px;
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg,
      rgba(102, 187, 106, 0.1) 0%,
      rgba(129, 199, 132, 0.1) 30%,
      rgba(165, 214, 167, 0.1) 60%,
      rgba(200, 230, 201, 0.1) 100%);
  box-shadow: 0 10px 30px rgba(102, 187, 106, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.3);
  z-index: 2;
}

.add-device-bg {
  width: 100%;
  height: 100%;
  text-align: left;
  background: linear-gradient(135deg,
    rgba(102, 187, 106, 0.1) 0%,
    rgba(129, 199, 132, 0.15) 30%,
    rgba(165, 214, 167, 0.1) 60%,
    rgba(200, 230, 201, 0.08) 100%);
  overflow: hidden;
  position: relative;
  box-sizing: border-box;
}

.add-device-bg::before {
  content: '';
  position: absolute;
  top: 20%;
  right: 10%;
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(129, 199, 132, 0.2) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 6s ease-in-out infinite;
}

.add-device-bg::after {
  content: '';
  position: absolute;
  bottom: 20%;
  left: 15%;
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, rgba(200, 230, 201, 0.3) 0%, transparent 70%);
  border-radius: 50%;
  animation: float 8s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

.wave-emoji {
  display: inline-block;
  animation: grow 3s ease-in-out infinite;
  transform-origin: center;
}

@keyframes grow {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.sparkle-emoji {
  display: inline-block;
  animation: twinkle 2.5s ease-in-out infinite;
}

@keyframes twinkle {
  0%, 100% {
    transform: scale(1) rotate(0deg);
    opacity: 1;
  }
  25% {
    transform: scale(1.1) rotate(5deg);
    opacity: 0.9;
  }
  50% {
    transform: scale(1.2) rotate(-5deg);
    opacity: 0.8;
  }
  75% {
    transform: scale(1.1) rotate(3deg);
    opacity: 0.9;
  }
}

.heart-emoji {
  display: inline-block;
  animation: heartbeat 3s ease-in-out infinite;
}

@keyframes heartbeat {
  0%, 40%, 100% {
    transform: scale(1);
  }
  20% {
    transform: scale(1.1);
  }
}

.hellow-text {
  margin-left: 75px;
  color: #3d4566;
  font-size: 33px;
  font-weight: 700;
  letter-spacing: 0;
}

.hi-hint {
  font-weight: 400;
  font-size: 12px;
  text-align: left;
  color: #818cae;
  margin-left: 75px;
  margin-top: 5px;
}

.add-device-btn {
  display: inline-block;
  margin-left: 75px;
  margin-top: 15px;
  cursor: pointer;
}

.add-btn-content {
  display: flex;
  align-items: center;
  height: 40px;
  border-radius: 20px;
  background: linear-gradient(135deg, #4A90A4 0%, #83C5BE 100%);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  padding: 0 20px;
  gap: 10px;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(74, 144, 164, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
}

.add-btn-content:hover {
  background: linear-gradient(135deg, #3A7A8A 0%, #6BB6AA 100%);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(74, 144, 164, 0.4);
}

.add-btn-text {
  white-space: nowrap;
}

.add-btn-icon {
  font-size: 16px;
  font-weight: bold;
}

.device-list-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 30px;
  padding: 30px 0;
}

/* 在 DeviceItem.vue 的样式中 */
.device-item {
  margin: 0 !important;
  /* 避免冲突 */
  width: auto !important;
}

.footer {
  font-size: 12px;
  font-weight: 400;
  margin-top: auto;
  padding-top: 30px;
  color: #979db1;
  text-align: center;
  /* 居中显示 */
}

/* 骨架屏动画 */
@keyframes shimmer {
  100% {
    transform: translateX(100%);
  }
}

.skeleton-item {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 120px;
  position: relative;
  overflow: hidden;
  margin-bottom: 20px;
}

.skeleton-image {
  width: 80px;
  height: 80px;
  background: #f0f2f5;
  border-radius: 4px;
  float: left;
  position: relative;
  overflow: hidden;
}

.skeleton-content {
  margin-left: 100px;
}

.skeleton-line {
  height: 16px;
  background: #f0f2f5;
  border-radius: 4px;
  margin-bottom: 12px;
  width: 70%;
  position: relative;
  overflow: hidden;
}

.skeleton-line-short {
  height: 12px;
  background: #f0f2f5;
  border-radius: 4px;
  width: 50%;
}

.skeleton-item::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 50%;
  height: 100%;
  background: linear-gradient(90deg,
      rgba(255, 255, 255, 0),
      rgba(255, 255, 255, 0.3),
      rgba(255, 255, 255, 0));
  animation: shimmer 1.5s infinite;
}

/* 移动端适配样式 */
@media screen and (max-width: 768px) {
  .welcome {
    min-width: unset;
    min-height: unset;
  }

  /* 在首页的移动端视图中，隐藏HeaderBar右侧多余的桌面端元素 */
  :deep(.header .header-right) {
    display: none;
  }

  .add-device {
    height: 160px;
  }

  .hellow-text {
    margin-left: 20px;
    font-size: 24px;
  }

  .hi-hint {
    margin-left: 20px;
  }

  .add-device-btn {
    margin-left: 20px;
  }

  .device-list-container {
    grid-template-columns: 1fr;
    gap: 15px;
    padding: 15px 0;
  }

  .el-main {
    padding: 10px !important;
  }

  .el-footer {
    padding: 10px 0;
    height: auto !important;
  }
}
</style>