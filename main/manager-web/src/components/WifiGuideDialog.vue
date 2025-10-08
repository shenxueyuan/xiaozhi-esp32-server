<template>
  <el-dialog
    :title="$t('wifiGuide.title')"
    :visible.sync="dialogVisible"
    :width="isMobile ? '100%' : '90%'"
    :before-close="handleClose"
    custom-class="wifi-guide-dialog"
  >
    <div class="wifi-guide-container">
      <!-- 步骤导航 -->
      <div class="step-navigation">
        <div
          v-for="(step, index) in steps"
          :key="index"
          class="step-nav-item"
          :class="{ active: currentStep === index + 1 }"
          @click="goToStep(index + 1)"
        >
          <div class="step-number">{{ index + 1 }}</div>
          <div class="step-title">{{ step.title }}</div>
        </div>
      </div>

      <!-- 当前步骤内容 -->
      <div class="step-content">
        <div class="step-image-container">
          <img
            :src="currentStepData.src"
            :alt="currentStepData.alt"
            class="step-image"
          />
        </div>
        <div class="step-description">
          <h3 class="step-heading">{{ currentStepData.title }}</h3>
          <p class="step-text">{{ currentStepData.description }}</p>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="step-actions">
        <el-button
          :disabled="currentStep === 1"
          @click="prevStep"
          icon="el-icon-arrow-left"
        >
          {{ $t('wifiGuide.previous') }}
        </el-button>
        <span class="step-counter">{{ currentStep }} / {{ steps.length }}</span>
        <el-button
          :disabled="currentStep === steps.length"
          @click="nextStep"
          icon="el-icon-arrow-right"
        >
          {{ $t('wifiGuide.next') }}
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: 'WifiGuideDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      dialogVisible: false,
      currentStep: 1,
      isMobile: false
    };
  },
  computed: {
    steps() {
      return [
        {
          title: this.$t('wifiGuide.stepTitle1'),
          src: '/images/conwifi/con_wifi_1.PNG',
          alt: '联网步骤1',
          description: this.$t('wifiGuide.step1')
        },
        {
          title: this.$t('wifiGuide.stepTitle2'),
          src: '/images/conwifi/con_wifi_2.PNG',
          alt: '联网步骤2',
          description: this.$t('wifiGuide.step2')
        },
        {
          title: this.$t('wifiGuide.stepTitle3'),
          src: '/images/conwifi/con_wifi_3.PNG',
          alt: '联网步骤3',
          description: this.$t('wifiGuide.step3')
        },
        {
          title: this.$t('wifiGuide.stepTitle4'),
          src: '/images/conwifi/con_wifi_4.PNG',
          alt: '联网步骤4',
          description: this.$t('wifiGuide.step4')
        },
        {
          title: this.$t('wifiGuide.stepTitle5'),
          src: '/images/conwifi/con_wifi_5.PNG',
          alt: '联网步骤5',
          description: this.$t('wifiGuide.step5')
        }
      ];
    },
    currentStepData() {
      return this.steps[this.currentStep - 1] || this.steps[0];
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val) {
        this.currentStep = 1;
      }
    },
    dialogVisible(val) {
      if (!val) {
        this.$emit('update:visible', false);
      }
    }
  },
  mounted() {
    this.checkScreenSize();
    window.addEventListener('resize', this.checkScreenSize);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkScreenSize);
  },
  methods: {
    checkScreenSize() {
      this.isMobile = window.innerWidth <= 768;
    },
    handleClose() {
      this.dialogVisible = false;
    },
    goToStep(step) {
      this.currentStep = step;
    },
    nextStep() {
      if (this.currentStep < this.steps.length) {
        this.currentStep++;
      }
    },
    prevStep() {
      if (this.currentStep > 1) {
        this.currentStep--;
      }
    }
  }
};
</script>

<style lang="scss" scoped>
.wifi-guide-container {
  padding: 0;
  min-height: 600px;
}

/* 步骤导航 */
.step-navigation {
  display: flex;
  justify-content: space-between;
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.step-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 10px;
  border-radius: 8px;
  min-width: 80px;

  &:hover {
    background: #e9ecef;
  }

  &.active {
    background: #409eff;
    color: white;

    .step-number {
      background: white;
      color: #409eff;
    }
  }
}

.step-number {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #ddd;
  color: #666;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.step-title {
  font-size: 12px;
  text-align: center;
  line-height: 1.2;
}

/* 步骤内容 */
.step-content {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
  padding: 0 20px;
}

.step-image-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  min-height: 400px;
}

.step-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.step-description {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 20px;
}

.step-heading {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  line-height: 1.4;
}

.step-text {
  font-size: 16px;
  line-height: 1.6;
  color: #666;
  margin: 0;
}

/* 操作按钮 */
.step-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #eee;
}

.step-counter {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .wifi-guide-dialog {
    min-width: 100%;
    max-width: 100%;
    width: 100% !important;
    height: 100vh !important;
    max-height: 100vh;
    top: 0 !important;
    left: 0 !important;
    transform: none !important;
    margin: 0 !important;
    border-radius: 0;
  }

  .wifi-guide-container {
    min-height: calc(100vh - 120px);
  }

  .step-navigation {
    flex-wrap: wrap;
    gap: 8px;
    padding: 15px;
    margin-bottom: 20px;
  }

  .step-nav-item {
    min-width: 60px;
    padding: 8px;
  }

  .step-number {
    width: 28px;
    height: 28px;
    margin-bottom: 6px;
  }

  .step-title {
    font-size: 11px;
  }

  .step-content {
    flex-direction: column;
    gap: 20px;
    padding: 0 15px;
    margin-bottom: 20px;
  }

  .step-image-container {
    min-height: 250px;
    padding: 15px;
  }

  .step-description {
    padding: 15px;
  }

  .step-heading {
    font-size: 18px;
    margin-bottom: 12px;
  }

  .step-text {
    font-size: 14px;
    line-height: 1.5;
  }

  .step-actions {
    padding: 15px;
  }
}

/* 按钮样式优化 */
::v-deep .el-button {
  padding: 10px 20px;
  border-radius: 6px;

  &:disabled {
    opacity: 0.5;
  }
}
</style>

