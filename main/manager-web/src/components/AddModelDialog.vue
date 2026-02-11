<template>
  <el-dialog :visible="dialogVisible" @update:visible="handleVisibleChange" :width="isMobile ? '95%' : '57%'" center
    custom-class="custom-dialog" :show-close="false" class="center-dialog" :modal="false">
    <div :class="['dialog-content', { 'mobile-content': isMobile }]">
      <div :class="['dialog-title', { 'mobile-title': isMobile }]">
        {{ $t('modelConfigDialog.addModel') }}
      </div>

      <button class="custom-close-btn" @click="handleClose">
        ×
      </button>

      <!-- 模型信息部分 -->
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
        <div style="font-size: 20px; font-weight: bold; color: #3d4566;">{{ $t('modelConfigDialog.modelInfo') }}</div>
        <div style="display: flex; align-items: center; gap: 20px;">
          <div style="display: flex; align-items: center;">
            <span style="margin-right: 8px;">{{ $t('modelConfigDialog.enable') }}</span>
            <el-switch v-model="formData.isEnabled" class="custom-switch"></el-switch>
          </div>
          <div style="display: none; align-items: center;">
            <span style="margin-right: 8px;">{{ $t('modelConfigDialog.setDefault') }}</span>
            <el-switch v-model="formData.isDefault" class="custom-switch"></el-switch>
          </div>
        </div>
      </div>

      <div style="height: 2px; background: #e9e9e9; margin-bottom: 22px;"></div>
      <el-form :model="formData" label-width="100px" label-position="left" class="custom-form">
        <div style="display: flex; gap: 20px; margin-bottom: 0;">
          <el-form-item :label="$t('modelConfigDialog.modelId')" prop="id" style="flex: 1;">
            <el-input v-model="formData.id" :placeholder="$t('modelConfigDialog.enterModelId')" class="custom-input-bg"
              maxlength="32"></el-input>
          </el-form-item>
        </div>
        <div style="display: flex; gap: 20px; margin-bottom: 0;">
          <el-form-item :label="$t('modelConfigDialog.modelName')" prop="modelName" style="flex: 1;">
            <el-input v-model="formData.modelName" :placeholder="$t('modelConfigDialog.enterModelName')"
              class="custom-input-bg"></el-input>
          </el-form-item>
          <el-form-item :label="$t('modelConfigDialog.modelCode')" prop="modelCode" style="flex: 1;">
            <el-input v-model="formData.modelCode" :placeholder="$t('modelConfigDialog.enterModelCode')"
              class="custom-input-bg"></el-input>
          </el-form-item>
        </div>

        <div style="display: flex; gap: 20px; margin-bottom: 0;">
          <el-form-item :label="$t('modelConfigDialog.supplier')" prop="supplier" style="flex: 1;">
            <el-select v-model="formData.supplier" :placeholder="$t('modelConfigDialog.selectSupplier')"
              class="custom-select custom-input-bg" style="width: 100%;" @focus="loadProviders" filterable>
              <el-option v-for="item in providers" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('modelConfigDialog.sortOrder')" prop="sortOrder" style="flex: 1;">
            <el-input v-model="formData.sort" type="number" :placeholder="$t('modelConfigDialog.enterSortOrder')"
              class="custom-input-bg"></el-input>
          </el-form-item>
        </div>


        <el-form-item :label="$t('modelConfigDialog.docLink')" prop="docLink" style="margin-bottom: 27px;">
          <el-input v-model="formData.docLink" :placeholder="$t('modelConfigDialog.enterDocLink')"
            class="custom-input-bg"></el-input>
        </el-form-item>

        <el-form-item :label="$t('modelConfigDialog.remark')" prop="remark" class="prop-remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3"
            :placeholder="$t('modelConfigDialog.enterRemark')" :autosize="{ minRows: 3, maxRows: 5 }"
            class="custom-input-bg"></el-input>
        </el-form-item>
      </el-form>

      <div style="font-size: 20px; font-weight: bold; color: #3d4566; margin-bottom: 15px;">{{
        $t('modelConfigDialog.callInfo') }}</div>
      <div style="height: 2px; background: #e9e9e9; margin-bottom: 22px;"></div>

      <el-form :model="formData.configJson" label-width="auto" label-position="left" class="custom-form">
        <div v-for="(row, rowIndex) in chunkedCallInfoFields" :key="rowIndex"
          style="display: flex; gap: 20px; margin-bottom: 0;">
          <el-form-item v-for="field in row" :key="field.prop" :label="field.label" :prop="field.prop" style="flex: 1;">
            <el-input v-model="formData.configJson[field.prop]" :placeholder="field.placeholder"
              :type="field.type || 'text'" class="custom-input-bg" :show-password="field.type === 'password'">
            </el-input>
          </el-form-item>
        </div>
      </el-form>
    </div>

    <div style="display: flex;justify-content: center;">
      <el-button type="primary" @click="confirm" class="save-btn" :loading="saving" :disabled="saving">
        {{ $t('modelConfigDialog.save') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import Api from '@/apis/api';
export default {
  name: 'AddModelDialog',
  props: {
    visible: { type: Boolean, required: true },
    modelType: { type: String, required: true }
  },
  data() {
    return {
      saving: false,
      providers: [],
      dialogVisible: false,
      providersLoaded: false,
      providerFields: [],
      currentProvider: null,
      formData: {
        id: '',
        modelName: '',
        modelCode: '',
        supplier: '',
        sort: 1,
        docLink: '',
        remark: '',
        isEnabled: true,
        isDefault: true,
        configJson: {}
      }
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val) {
        this.initConfigJson();
      } else {
        this.resetForm();
      }
    },
    'formData.supplier'(newVal) {
      this.currentProvider = this.providers.find(p => p.value === newVal);
      this.providerFields = this.currentProvider?.fields || [];
      this.initDynamicConfig();
    }
  },
  computed: {
    dynamicCallInfoFields() {
      return this.providerFields;
    },
    chunkedCallInfoFields() {
      const chunkSize = 2;
      const result = [];
      for (let i = 0; i < this.dynamicCallInfoFields.length; i += chunkSize) {
        result.push(this.dynamicCallInfoFields.slice(i, i + chunkSize));
      }
      return result;
    }
  },
  methods: {
    loadProviders() {
      if (this.providersLoaded)
        return

      Api.model.getModelProviders(this.modelType, (data) => {
        this.providers = data.map(item => ({
          label: item.name,
          value: item.providerCode,
          fields: JSON.parse(item.fields || '[]').map(f => ({
            label: f.label,
            prop: f.key,
            type: f.type === 'password' ? 'password' : 'text',
            placeholder: `请输入${f.key}`
          }))
        }))
        this.providersLoaded = true
      })
    },
    initConfigJson() {
      const defaultConfig = {};
      this.providerFields.forEach(field => {
        defaultConfig[field.prop] = '';
      });
      this.formData.configJson = { ...defaultConfig };
    },
    handleVisibleChange(val) {
      this.dialogVisible = val;
      this.$emit('update:visible', val);
      if (!val) {
        this.resetForm();
      }
    },

    handleClose() {
      this.saving = false;
      this.$emit('update:visible', false);
    },
    initDynamicConfig() {
      const newConfig = {};
      this.providerFields.forEach(field => {
        newConfig[field.prop] = this.formData.configJson[field.prop] || '';
      });
      this.formData.configJson = newConfig;
    },
    confirm() {
      this.saving = true;

      // 校验模型ID不能为纯文字或空格
      if (this.formData.id && !this.validateModelId(this.formData.id)) {
        this.$message.error(this.$t('modelConfigDialog.invalidModelId'));
        this.saving = false;
        return;
      }

      if (!this.formData.supplier) {
        this.$message.error(this.$t('addModelDialog.requiredSupplier'));
        this.saving = false;
        return;
      }

      const submitData = {
        id: this.formData.id || '',
        modelName: this.formData.modelName || '',
        modelCode: this.formData.modelCode || '',
        supplier: this.formData.supplier,
        sort: this.formData.sort || 1,
        docLink: this.formData.docLink || '',
        remark: this.formData.remark || '',
        isEnabled: this.formData.isEnabled ? 1 : 0,
        isDefault: this.formData.isDefault ? 1 : 0,
        provideCode: this.formData.supplier,
        configJson: {
          ...this.formData.configJson,
          type: this.formData.supplier
        }
      };

      try {
        this.$emit('confirm', submitData);
        this.$emit('update:visible', false);
        this.resetForm();
      } catch (e) {
        console.error(e);
      } finally {
        this.saving = false;
      }
    },
    resetForm() {
      this.saving = false;
      this.formData = {
        id: '',
        modelName: '',
        modelCode: '',
        supplier: '',
        sort: 1,
        docLink: '',
        remark: '',
        isEnabled: true,
        isDefault: true,
        configJson: {}
      };
      // 重置加载状态
      this.providers = [];
      this.providersLoaded = false;
      // 重置字段配置
      this.providerFields = [];
      this.currentProvider = null;
    },

    // 校验模型ID：不能为纯文字或空格
    validateModelId(modelId) {
      if (!modelId || typeof modelId !== 'string') {
        return false;
      }

      // 去除首尾空格
      const trimmedId = modelId.trim();

      // 检查是否为空或纯空格
      if (trimmedId === '') {
        return false;
      }

      // 检查是否只包含字母（纯文字）
      if (/^[a-zA-Z]+$/.test(trimmedId)) {
        return false;
      }

      // 检查是否包含空格
      if (/\s/.test(trimmedId)) {
        return false;
      }

      // 允许字母、数字、下划线、连字符
      if (!/^[a-zA-Z0-9_-]+$/.test(trimmedId)) {
        return false;
      }

      return true;
    }
  }
}
</script>

<style lang="scss" scoped>
// 弹窗样式
.custom-dialog {
  position: relative;
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


// 弹窗内容容器
.dialog-content {
  margin: 0 18px;
  text-align: left;
  padding: 10px;
  border-radius: 10px;

  &.mobile-content {
    margin: 0 12px;
    padding: 8px;
  }
}

// 弹窗标题
.dialog-title {
  font-size: 30px;
  color: #3d4566;
  margin-top: -10px;
  margin-bottom: 10px;
  text-align: center;

  &.mobile-title {
    font-size: 24px;
    margin-bottom: 15px;
  }
}

// 区块头部
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;

  &.mobile-section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #3d4566;

  &.mobile-section-title {
    font-size: 18px;
  }
}

.switch-group {
  display: flex;
  align-items: center;
  gap: 20px;

  &.mobile-switch-group {
    gap: 15px;
  }
}

.switch-item {
  display: flex;
  align-items: center;
}

.switch-label {
  margin-right: 8px;

  &.mobile-switch-label {
    font-size: 14px;
  }
}

// 分割线
.section-divider {
  height: 2px;
  background: #e9e9e9;
  margin-bottom: 22px;
}

// 表单行
.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 0;

  &.mobile-form-row {
    flex-direction: column;
    gap: 0;
  }
}

.form-item {
  flex: 1;

  &.mobile-form-item {
    flex: none;
    width: 100%;
  }
}

.form-item-full {
  margin-bottom: 27px;

  &.mobile-form-item {
    margin-bottom: 20px;
  }
}

// 底部按钮区域
.dialog-footer {
  display: flex;
  justify-content: center;

  &.mobile-footer {
    padding: 10px 0;
  }
}

.custom-close-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 35px;
  height: 35px;
  border-radius: 50%;
  border: 2px solid #cfcfcf;
  background: none;
  font-size: 30px;
  font-weight: lighter;
  color: #cfcfcf;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  padding: 0;
  outline: none;
}

.custom-close-btn:hover {
  color: #409EFF;
  border-color: #409EFF;
}

.custom-select .el-input__suffix {
  background: #e6e8ea;
  right: 6px;
  width: 20px;
  height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  top: 9px;
}

.custom-select .el-input__suffix-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.custom-select .el-icon-arrow-up:before {
  content: "";
  display: inline-block;
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 7px solid #c0c4cc;
  position: relative;
  top: -2px;
  transform: rotate(180deg);
}

.custom-form .el-form-item {
  margin-bottom: 20px;
}

.custom-form .el-form-item__label {
  color: #3d4566;
  font-weight: normal;
  text-align: right;
  padding-right: 20px;

}

.custom-form .el-form-item.prop-remark .el-form-item__label {
  margin-top: -4px;
}

.custom-input-bg .el-input__inner::-webkit-input-placeholder,
.custom-input-bg .el-textarea__inner::-webkit-input-placeholder {
  color: #9c9f9e;
}


.custom-input-bg .el-input__inner,
.custom-input-bg .el-textarea__inner {
  background-color: #ffffff;
}


.save-btn {
  background: linear-gradient(135deg, #66bb6a 0%, #4ade80 100%);
  color: #fff;
  border: none;
  width: 150px;
  height: 40px;
  font-size: 16px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 187, 106, 0.3);

  &.mobile-save-btn {
    width: 120px;
    height: 36px;
    font-size: 14px;
  }
}

.save-btn:hover {
  background: linear-gradient(135deg, #5ca85c 0%, #22c55e 100%);
  color: #fff;
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(102, 187, 106, 0.4);
}


.custom-switch .el-switch__core {
  border-radius: 20px;
  height: 23px;
  background-color: #c0ccda;
  width: 35px;
  padding: 0 20px;
}

.custom-switch .el-switch__core:after {
  width: 15px;
  height: 15px;
  background-color: white;
  top: 3px;
  left: 4px;
  transition: all .3s;
}

.custom-switch.is-checked .el-switch__core {
  border-color: #b5bcf0;
  background-color: #cfd7fa;
  padding: 0 20px;
}

.custom-switch.is-checked .el-switch__core:after {
  left: 100%;
  margin-left: -18px;
  background-color: #1b47ee;
}


[style*="display: flex"] {
  gap: 20px;
}

.custom-input-bg .el-input__inner {
  height: 32px;
}

</style>