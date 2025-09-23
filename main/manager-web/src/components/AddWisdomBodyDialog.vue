<template>
  <el-dialog :visible="visible" @close="handleClose" :width="isMobile ? '80%' : '25%'" center @open="handleOpen">
    <div class="dialog-header">
      <div class="header-icon">
        <img loading="lazy" src="@/assets/home/equipment.png" alt="" />
      </div>
      {{ $t('addAgentDialog.title') }}
    </div>
    <div class="dialog-divider"></div>
    <div class="dialog-content">
      <div class="input-label">
        <span class="required">*</span> {{ $t('addAgentDialog.agentName') }}：
      </div>
      <div class="input-container">
        <el-input ref="inputRef" :placeholder="$t('addAgentDialog.placeholder')" v-model="wisdomBodyName" @keyup.enter.native="confirm" />
      </div>
    </div>
    <div class="dialog-footer">
      <div class="dialog-btn confirm-btn" @click="confirm">
        {{ $t('addAgentDialog.confirm') }}
      </div>
      <div class="dialog-btn cancel-btn" @click="cancel">
        {{ $t('addAgentDialog.cancel') }}
      </div>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/apis/api';
import { isMobileDevice } from '@/utils';

export default {
  name: 'AddWisdomBodyDialog',
  props: {
    visible: { type: Boolean, required: true }
  },
  data() {
    return {
      wisdomBodyName: "",
      inputRef: null,
      isMobile: false
    }
  },
  mounted() {
    this.checkDeviceType();
    window.addEventListener('resize', this.checkDeviceType);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.checkDeviceType);
  },
  methods: {
    checkDeviceType() {
      this.isMobile = isMobileDevice();
    },
    handleOpen() {
      this.$nextTick(() => {
        this.$refs.inputRef.focus();
      });
    },
    confirm() {
      if (!this.wisdomBodyName.trim()) {
        this.$message.error(this.$t('addAgentDialog.nameRequired'));
        return;
      }
      Api.agent.addAgent(this.wisdomBodyName, (res) => {
        this.$message.success({
          message: this.$t('addAgentDialog.addSuccess'),
          showClose: true
        });
        this.$emit('confirm', res);
        this.$emit('update:visible', false);
        this.wisdomBodyName = "";
      });
    },
    cancel() {
      this.$emit('update:visible', false)
      this.wisdomBodyName = ""
    },
    handleClose() {
      this.$emit('update:visible', false);
    },
  }
}
</script>

<style scoped>
.dialog-header {
  margin: 0 10px 10px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 20px;
  text-align: left;
  color: #3d4566;
}

.header-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4A90A4 0%, #83C5BE 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(74, 144, 164, 0.3);
}

.header-icon img {
  width: 18px;
  height: 15px;
  filter: brightness(0) invert(1);
}

.header-title {
  background: linear-gradient(135deg, #4A90A4 0%, #83C5BE 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.dialog-divider {
  height: 1px;
  background: linear-gradient(90deg,
    rgba(74, 144, 164, 0.1) 0%,
    rgba(74, 144, 164, 0.3) 50%,
    rgba(74, 144, 164, 0.1) 100%);
  margin: 0 10px;
}

.dialog-content {
  margin: 22px 15px;
}

.input-label {
  font-weight: 400;
  text-align: left;
  color: #3d4566;
  margin-bottom: 12px;
}

.required {
  color: #ff6b6b;
  font-weight: 600;
}

.input-container {
  border: 1px solid rgba(74, 144, 164, 0.2);
  background: rgba(255, 255, 255, 0.8);
  border-radius: 15px;
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.input-container:focus-within {
  border-color: rgba(74, 144, 164, 0.5);
  box-shadow: 0 0 0 3px rgba(74, 144, 164, 0.1);
}

.dialog-footer {
  display: flex;
  margin: 15px 15px;
  gap: 12px;
}

.dialog-btn {
  cursor: pointer;
  flex: 1;
  border-radius: 20px;
  height: 40px;
  font-weight: 500;
  font-size: 14px;
  line-height: 40px;
  text-align: center;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.confirm-btn {
  background: linear-gradient(135deg, #4A90A4 0%, #83C5BE 100%);
  color: #fff;
  box-shadow: 0 4px 15px rgba(74, 144, 164, 0.3);
}

.confirm-btn:hover {
  background: linear-gradient(135deg, #3A7A8A 0%, #6BB6AA 100%);
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(74, 144, 164, 0.4);
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(74, 144, 164, 0.3);
  color: #4A90A4;
  backdrop-filter: blur(10px);
}

.cancel-btn:hover {
  background: rgba(74, 144, 164, 0.1);
  border-color: rgba(74, 144, 164, 0.5);
  transform: translateY(-1px);
}

::v-deep .el-dialog {
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(74, 144, 164, 0.2);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

::v-deep .el-dialog__headerbtn {
  display: none;
}

::v-deep .el-dialog__body {
  padding: 4px 6px;
}

::v-deep .el-dialog__header {
  padding: 10px;
}

::v-deep .el-input__inner {
  background: transparent;
  border: none;
  font-size: 14px;
  padding: 12px 15px;
}

::v-deep .el-input__inner:focus {
  background: transparent;
  border: none;
}

/* 移动端适配样式 */
@media screen and (max-width: 768px) {
  ::v-deep .el-dialog {
    width: 80% !important;
    margin-top: 20vh !important;
  }

  ::v-deep .el-dialog__body {
    padding: 10px;
  }

  .dialog-btn {
    height: 36px;
    line-height: 36px;
    font-size: 13px;
  }

  .dialog-header {
    font-size: 18px;
  }
}
</style>