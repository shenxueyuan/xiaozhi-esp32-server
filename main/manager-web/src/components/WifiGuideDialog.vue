<template>
  <el-dialog
    :title="$t('wifiGuide.title')"
    :visible.sync="dialogVisible"
    :width="isMobile ? '100%' : '80%'"
    :before-close="handleClose"
    custom-class="wifi-guide-dialog"
  >
    <div class="wifi-guide-container">
      <!-- 图片轮播展示 -->
      <el-carousel
        :interval="0"
        arrow="always"
        :height="carouselHeight"
        indicator-position="outside"
        @change="handleSlideChange"
      >
        <el-carousel-item v-for="(image, index) in images" :key="index">
          <div class="image-wrapper">
            <img :src="image.src" :alt="image.alt" class="guide-image" />
            <div class="image-caption">{{ image.caption }}</div>
          </div>
        </el-carousel-item>
      </el-carousel>

      <!-- 当前步骤指示 -->
      <div class="step-indicator">
        {{ $t('wifiGuide.step') }} {{ currentStep }} / {{ images.length }}
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
    images() {
      const imagePaths = [
        { name: 'con_wifi_1.PNG', step: 'step1' },
        { name: 'con_wifi_2.PNG', step: 'step2' },
        { name: 'con_wifi_3.PNG', step: 'step3' },
        { name: 'con_wifi_4.PNG', step: 'step4' },
        { name: 'con_wifi_5.PNG', step: 'step5' }
      ];
      
      return imagePaths.map((img, index) => ({
        src: this.getImageUrl(img.name),
        alt: `联网步骤${index + 1}`,
        caption: this.$t(`wifiGuide.${img.step}`)
      }));
    },
    carouselHeight() {
      return this.isMobile ? '400px' : '500px';
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
    getImageUrl(imageName) {
      try {
        return require(`@/assets/conwifi/${imageName}`);
      } catch (e) {
        console.error(`Failed to load image: ${imageName}`, e);
        return '';
      }
    },
    checkScreenSize() {
      this.isMobile = window.innerWidth <= 768;
    },
    handleClose() {
      this.dialogVisible = false;
    },
    handleSlideChange(index) {
      this.currentStep = index + 1;
    }
  }
};
</script>

<style lang="scss" scoped>
.wifi-guide-container {
  padding: 20px;
}

.image-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  padding: 20px;
}

.guide-image {
  max-width: 100%;
  max-height: 80%;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.image-caption {
  margin-top: 20px;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  text-align: center;
}

.step-indicator {
  text-align: center;
  margin-top: 20px;
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
    padding: 10px;
  }

  .guide-image {
    max-height: 70%;
  }

  .image-caption {
    font-size: 14px;
    margin-top: 15px;
  }

  .step-indicator {
    font-size: 13px;
    margin-top: 15px;
  }
}

/* 轮播箭头样式优化 */
::v-deep .el-carousel__arrow {
  background-color: rgba(31, 45, 61, 0.8);

  &:hover {
    background-color: rgba(31, 45, 61, 0.9);
  }
}

/* 指示器样式优化 */
::v-deep .el-carousel__indicator {
  .el-carousel__button {
    background-color: #dcdfe6;
    opacity: 0.5;
  }

  &.is-active .el-carousel__button {
    background-color: #409eff;
    opacity: 1;
  }
}
</style>

