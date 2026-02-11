<template>
  <div class="welcome">
    <HeaderBar />

    <!-- 模型类型选择器 -->
    <div class="model-types-selector">
      <div class="selector-container">
        <div
          v-for="modelType in modelTypes"
          :key="modelType.key"
          class="model-type-item"
          :class="{ 'active': activeTab === modelType.key }"
          @click="handleModelTypeChange(modelType.key)">
          <div class="item-icon">{{ modelType.icon }}</div>
          <div class="item-text">{{ $t('modelConfig.' + modelType.key) }}</div>
          <div class="item-status" :class="getStatusClass(modelType.key)"></div>
        </div>
      </div>
      <div class="action-section">
        <el-button type="primary" icon="el-icon-plus" @click="addModel" class="add-model-btn">
          {{ $t('modelConfig.add') }}
        </el-button>
      </div>
    </div>

    <!-- 当前选中的模型配置详情 -->
    <div class="main-wrapper">
      <div class="content-panel">
        <!-- 左侧导航 -->
        <el-menu
          :default-active="activeTab"
          class="nav-panel"
          @select="handleMenuSelect"
          style="background-size: cover; background-position: center"
        >
          <el-menu-item index="vad">
            <span class="menu-text">{{ $t("modelConfig.vad") }}</span>
          </el-menu-item>
          <el-menu-item index="asr">
            <span class="menu-text">{{ $t("modelConfig.asr") }}</span>
          </el-menu-item>
          <el-menu-item index="llm">
            <span class="menu-text">{{ $t("modelConfig.llm") }}</span>
          </el-menu-item>
          <el-menu-item index="vllm">
            <span class="menu-text">{{ $t("modelConfig.vllm") }}</span>
          </el-menu-item>
          <el-menu-item index="intent">
            <span class="menu-text">{{ $t("modelConfig.intent") }}</span>
          </el-menu-item>
          <el-menu-item index="tts">
            <span class="menu-text">{{ $t("modelConfig.tts") }}</span>
          </el-menu-item>
          <el-menu-item index="memory">
            <span class="menu-text">{{ $t("modelConfig.memory") }}</span>
          </el-menu-item>
          <el-menu-item index="rag">
            <span class="menu-text">{{ $t("modelConfig.rag") }}</span>
          </el-menu-item>
        </el-menu>

        <!-- 右侧内容 -->
        <div class="content-area">
          <el-card class="model-card" shadow="never">
            <el-table
              ref="modelTable"
              style="width: 100%"
              v-loading="loading"
              :element-loading-text="$t('modelConfig.loading')"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(255, 255, 255, 0.7)"
              :header-cell-style="{ background: 'transparent' }"
              :data="modelList"
              class="data-table"
              header-row-class-name="table-header"
              :header-cell-class-name="headerCellClassName"
              @selection-change="handleSelectionChange"
            >
              <el-table-column
                type="selection"
                width="55"
                align="center"
                :cell-class-name="selectionCellClassName"
              ></el-table-column>
              <el-table-column
                :label="$t('modelConfig.modelId')"
                prop="id"
                align="center"
              ></el-table-column>
              <el-table-column
                :label="$t('modelConfig.modelName')"
                prop="modelName"
                align="center"
              ></el-table-column>
              <el-table-column :label="$t('modelConfig.provider')" align="center">
                <template slot-scope="scope">
                  {{ scope.row.configJson.type || $t("modelConfig.unknown") }}
                </template>
              </el-table-column>
              <el-table-column :label="$t('modelConfig.isEnabled')" align="center">
                <template slot-scope="scope">
                  <el-tooltip
                    v-if="scope.row.isDefault === 1 && scope.row.isEnabled === 1"
                    :content="$t('modelConfig.defaultModelCannotDisable')"
                    placement="top"
                    effect="light"
                  >
                    <el-switch
                      v-model="scope.row.isEnabled"
                      class="custom-switch"
                      :active-value="1"
                      :inactive-value="0"
                      disabled
                      @change="handleStatusChange(scope.row)"
                    />
                  </el-tooltip>
                  <el-switch
                    v-else
                    v-model="scope.row.isEnabled"
                    class="custom-switch"
                    :active-value="1"
                    :inactive-value="0"
                    @change="handleStatusChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column :label="$t('modelConfig.isDefault')" align="center">
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.isDefault"
                    class="custom-switch"
                    :active-value="1"
                    :inactive-value="0"
                    @change="handleDefaultChange(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column
                v-if="activeTab === 'tts'"
                :label="$t('modelConfig.voiceManagement')"
                align="center"
              >
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    size="mini"
                    @click="openTtsDialog(scope.row)"
                    class="voice-management-btn"
                  >
                    {{ $t("modelConfig.voiceManagement") }}
                  </el-button>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('modelConfig.action')"
                align="center"
                width="120px"
              >
                <template slot-scope="scope">
                  <div class="action-buttons-vertical">
                    <el-button type="text" size="mini" @click="editModel(scope.row)" class="edit-btn">
                      {{ $t("modelConfig.edit") }}
                    </el-button>
                    <el-button type="text" size="mini" @click="duplicateModel(scope.row)" class="duplicate-btn">
                      {{ $t("modelConfig.duplicate") }}
                    </el-button>
                    <el-button type="text" size="mini" @click="deleteModel(scope.row)" class="delete-btn">
                      {{ $t("modelConfig.delete") }}
                    </el-button>
                  </div>
                </template>
              </el-table-column>
            </el-table>
            <div class="table-footer">
              <div class="batch-actions">
                <el-button size="mini" type="primary" @click="selectAll">
                  {{
                    isAllSelected
                      ? $t("modelConfig.deselectAll")
                      : $t("modelConfig.selectAll")
                  }}
                </el-button>
                <el-button type="success" size="mini" @click="addModel" class="add-btn">
                  {{ $t("modelConfig.add") }}
                </el-button>
                <el-button
                  size="mini"
                  type="danger"
                  icon="el-icon-delete"
                  @click="batchDelete"
                >
                  {{ $t("modelConfig.delete") }}
                </el-button>
              </div>
              <div class="custom-pagination">
                <el-select
                  v-model="pageSize"
                  @change="handlePageSizeChange"
                  class="page-size-select"
                >
                  <el-option
                    v-for="item in pageSizeOptions"
                    :key="item"
                    :label="$t('modelConfig.itemsPerPage', { items: item })"
                    :value="item"
                  >
                  </el-option>
                </el-select>

                <button
                  class="pagination-btn"
                  :disabled="currentPage === 1"
                  @click="goFirst"
                >
                  {{ $t("modelConfig.firstPage") }}
                </button>
                <button
                  class="pagination-btn"
                  :disabled="currentPage === 1"
                  @click="goPrev"
                >
                  {{ $t("modelConfig.prevPage") }}
                </button>

                <button
                  v-for="page in visiblePages"
                  :key="page"
                  class="pagination-btn"
                  :class="{ active: page === currentPage }"
                  @click="goToPage(page)"
                >
                  {{ page }}
                </button>

                <button
                  class="pagination-btn"
                  :disabled="currentPage === pageCount"
                  @click="goNext"
                >
                  {{ $t("modelConfig.nextPage") }}
                </button>
                <span class="total-text">{{
                  $t("modelConfig.totalRecords", { total })
                }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <ModelEditDialog
        :modelType="activeTab"
        :visible.sync="editDialogVisible"
        :modelData="editModelData"
        @save="handleModelSave"
      />
      <TtsModel
        :visible.sync="ttsDialogVisible"
        :ttsModelId="selectedTtsModelId"
        :modelConfig="selectedModelConfig"
      />
      <AddModelDialog
        :modelType="activeTab"
        :visible.sync="addDialogVisible"
        @confirm="handleAddConfirm"
      />
    </div>
    <el-footer>
      <version-footer />
    </el-footer>
  </div>
</template>

<script>
import Api from "@/apis/api";
import AddModelDialog from "@/components/AddModelDialog.vue";
import HeaderBar from "@/components/HeaderBar.vue";
import ModelEditDialog from "@/components/ModelEditDialog.vue";
import TtsModel from "@/components/TtsModel.vue";
import VersionFooter from "@/components/VersionFooter.vue";
export default {
  components: { HeaderBar, ModelEditDialog, TtsModel, AddModelDialog, VersionFooter },
  data() {
    return {
      addDialogVisible: false,
      activeTab: "llm",
      search: "",
      editDialogVisible: false,
      editModelData: {},
      ttsDialogVisible: false,
      selectedTtsModelId: "",
      modelList: [],
      pageSizeOptions: [10, 20, 50, 100],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      selectedModels: [],
      isAllSelected: false,
      loading: false,
      selectedModelConfig: {},
      modelTypes: [
        {
          key: 'llm',
          name: '对话模型',
          icon: '🤖',
          description: '处理心理咨询对话的大语言模型'
        },
        {
          key: 'asr',
          name: '语音识别',
          icon: '🎤',
          description: '将语音转换为文字进行处理'
        },
        {
          key: 'tts',
          name: '语音合成',
          icon: '🔊',
          description: '将文字转换为温暖的语音回应'
        },
        {
          key: 'vllm',
          name: '视觉理解',
          icon: '👁️',
          description: '理解图像内容，辅助心理分析'
        },
        {
          key: 'intent',
          name: '意图识别',
          icon: '🧭',
          description: '识别用户的心理咨询意图'
        },
        {
          key: 'memory',
          name: '记忆模块',
          icon: '🧠',
          description: '记录和回顾咨询历史'
        },
        {
          key: 'vad',
          name: '语音检测',
          icon: '📡',
          description: '检测语音活动和情绪状态'
        }
      ]
    };
  },

  created() {
    this.loadData();
  },

  mounted() {
    // 在组件挂载后确保表头翻译文本正确显示
    setTimeout(() => {
      this.updateSelectionHeaderText();
    }, 100);
  },

  updated() {
    // 在组件更新后重新设置表头翻译文本
    this.updateSelectionHeaderText();
  },

  computed: {
    modelTypeText() {
      return (
        this.$t("modelConfig." + this.activeTab) || this.$t("modelConfig.modelConfig")
      );
    },
    currentModelType() {
      return this.modelTypes.find(type => type.key === this.activeTab) || this.modelTypes[0];
    },
    pageCount() {
      return Math.ceil(this.total / this.pageSize);
    },
    visiblePages() {
      const pages = [];
      const maxVisible = 3;
      let start = Math.max(1, this.currentPage - 1);
      let end = Math.min(this.pageCount, start + maxVisible - 1);

      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1);
      }

      for (let i = start; i <= end; i++) {
        pages.push(i);
      }
      return pages;
    },
  },

  methods: {
    // 更新选择列表头翻译文本
    updateSelectionHeaderText() {
      const thElement = document.querySelector(`.el-table__header th:nth-child(1) .cell`);
      if (thElement) {
        thElement.setAttribute("data-content", this.$t("modelConfig.select"));
      }
    },
    handlePageSizeChange(val) {
      this.pageSize = val;
      this.currentPage = 1;
      this.loadData();
    },
    openTtsDialog(row) {
      this.selectedTtsModelId = row.id;
      this.selectedModelConfig = row;
      this.ttsDialogVisible = true;
    },
    headerCellClassName({ column, columnIndex }) {
      if (columnIndex === 0) {
        return "custom-selection-header";
      }
      return "";
    },
    selectionCellClassName({ row, column, rowIndex, columnIndex }) {
      // 只对表头行设置data-content
      if (rowIndex === undefined) {
        // 使用setTimeout确保DOM已经渲染完成
        setTimeout(() => {
          const thElement = document.querySelector(
            `.el-table__header th:nth-child(1) .cell`
          );
          if (thElement) {
            thElement.setAttribute("data-content", this.$t("modelConfig.select"));
          }
        }, 0);
      }
      return "";
    },
    handleMenuSelect(index) {
      this.activeTab = index;
      this.currentPage = 1; // 重置到第一页
      this.pageSize = 10; // 可选：重置每页条数
      this.loadData();
    },
    handleModelTypeChange(modelKey) {
      this.activeTab = modelKey;
      this.currentPage = 1;
      this.loadData();
    },
    getStatusClass(modelKey) {
      // 模拟状态，实际应该根据模型列表数据来判断
      const hasEnabledModels = this.activeTab === modelKey && this.modelList.some(model => model.isEnabled);
      return hasEnabledModels ? 'active' : 'inactive';
    },
    getStatusText(modelKey) {
      const hasEnabledModels = this.activeTab === modelKey && this.modelList.some(model => model.isEnabled);
      return hasEnabledModels ? '已配置' : '未配置';
    },
    // 批量删除
    batchDelete() {
      if (this.selectedModels.length === 0) {
        this.$message.warning(this.$t("modelConfig.selectModelsFirst"));
        return;
      }

      this.$confirm(this.$t("modelConfig.confirmBatchDelete"), this.$t("message.info"), {
        confirmButtonText: this.$t("common.confirm"),
        cancelButtonText: this.$t("common.cancel"),
        type: "warning",
      })
        .then(() => {
          const deletePromises = this.selectedModels.map(
            (model) =>
              new Promise((resolve) => {
                Api.model.deleteModel(model.id, ({ data }) => resolve(data.code === 0));
              })
          );

          Promise.all(deletePromises).then((results) => {
            if (results.every(Boolean)) {
              this.$message.success({
                message: this.$t("modelConfig.batchDeleteSuccess"),
                showClose: true,
              });
              this.loadData();
            } else {
              this.$message.error({
                message: this.$t("modelConfig.partialDeleteFailed"),
                showClose: true,
              });
            }
          });
        })
        .catch(() => {
          this.$message.info(this.$t("modelConfig.deleteCancelled"));
        });
    },
    addModel() {
      this.addDialogVisible = true;
    },
    editModel(model) {
      this.editModelData = JSON.parse(JSON.stringify(model));
      this.editDialogVisible = true;
    },
    duplicateModel(model) {
      this.editModelData = JSON.parse(JSON.stringify(model));
      this.editModelData.duplicateMode = true;
      this.editDialogVisible = true;
    },
    // 删除单个模型
    deleteModel(model) {
      this.$confirm(this.$t("modelConfig.confirmDelete"), this.$t("message.info"), {
        confirmButtonText: this.$t("common.confirm"),
        cancelButtonText: this.$t("common.cancel"),
        type: "warning",
      })
        .then(() => {
          Api.model.deleteModel(model.id, ({ data }) => {
            if (data.code === 0) {
              this.$message.success({
                message: this.$t("modelConfig.deleteSuccess"),
                showClose: true,
              });
              this.loadData();
            } else {
              this.$message.error({
                message: data.msg || this.$t("modelConfig.deleteFailed"),
                showClose: true,
              });
            }
          });
        })
        .catch(() => {
          this.$message.info(this.$t("modelConfig.deleteCancelled"));
        });
    },
    handleCurrentChange(page) {
      this.currentPage = page;
      this.$refs.modelTable.clearSelection();
    },
    handleModelSave({ provideCode, formData, done }) {
      const modelType = this.activeTab;
      const id = formData.id;

      if (this.editModelData.duplicateMode) {
        formData.id = "";
        Api.model.addModel({ modelType, provideCode, formData }, ({ data }) => {
          if (data.code === 0) {
            this.$message.success(this.$t("modelConfig.duplicateSuccess"));
            this.loadData();
            this.editDialogVisible = false;
          } else {
            this.$message.error(data.msg || this.$t("modelConfig.duplicateFailed"));
          }
          done && done(); // 调用done回调关闭加载状态
        });
      } else {
        Api.model.updateModel({ modelType, provideCode, id, formData }, ({ data }) => {
          if (data.code === 0) {
            this.$message.success(this.$t("modelConfig.saveSuccess"));
            this.loadData();
            this.editDialogVisible = false;
          } else {
            this.$message.error(data.msg || this.$t("modelConfig.saveFailed"));
          }
          done && done(); // 调用done回调关闭加载状态
        });
      }
    },
    selectAll() {
      if (this.isAllSelected) {
        this.$refs.modelTable.clearSelection();
      } else {
        this.$refs.modelTable.toggleAllSelection();
      }
    },
    handleSelectionChange(val) {
      this.selectedModels = val;
      this.isAllSelected = val.length === this.modelList.length;
      if (val.length === 0) {
        this.isAllSelected = false;
      }
    },

    // 新增模型配置
    handleAddConfirm(newModel) {
      const params = {
        modelType: this.activeTab,
        provideCode: newModel.provideCode,
        formData: {
          ...newModel,
          isDefault: newModel.isDefault ? 1 : 0,
          isEnabled: newModel.isEnabled ? 1 : 0,
          configJson: newModel.configJson,
        },
      };

      Api.model.addModel(params, ({ data }) => {
        if (data.code === 0) {
          this.$message.success({
            message: this.$t("modelConfig.addSuccess"),
            showClose: true,
          });
          this.loadData();
        } else {
          this.$message.error({
            message: data.msg || this.$t("modelConfig.addFailed"),
            showClose: true,
          });
        }
      });
    },

    // 分页器
    goFirst() {
      this.currentPage = 1;
      this.loadData();
    },
    goPrev() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.loadData();
      }
    },
    goNext() {
      if (this.currentPage < this.pageCount) {
        this.currentPage++;
        this.loadData();
      }
    },
    goToPage(page) {
      this.currentPage = page;
      this.loadData();
    },

    // 获取模型配置列表
    loadData() {
      this.loading = true; // 开始加载
      const params = {
        modelType: this.activeTab,
        page: this.currentPage,
        limit: this.pageSize,
      };

      // 添加搜索参数
      if (this.search && this.search.trim()) {
        params.search = this.search.trim();
      }

      Api.model.getModelList(params, ({ data }) => {
        this.loading = false; // 结束加载
        if (data.code === 0) {
          this.modelList = data.data.list;
          this.total = data.data.total;
        } else {
          this.$message.error(data.msg || this.$t("modelConfig.fetchModelsFailed"));
        }
      });
    },
    // 处理启用/禁用状态变更
    handleStatusChange(model) {
      const newStatus = model.isEnabled ? 1 : 0;
      const originalStatus = model.isEnabled;

      model.isEnabled = !model.isEnabled;

      Api.model.updateModelStatus(model.id, newStatus, ({ data }) => {
        if (data.code === 0) {
          this.$message.success(
            newStatus === 1
              ? this.$t("modelConfig.enableSuccess")
              : this.$t("modelConfig.disableSuccess")
          );
          // 保持新状态
          model.isEnabled = newStatus;
          // 刷新表格数据
          this.loadData();
        } else {
          // 操作失败时恢复原状态
          model.isEnabled = originalStatus;
          this.$message.error(data.msg || this.$t("modelConfig.operationFailed"));
        }
      });
    },
    handleDefaultChange(model) {
      Api.model.setDefaultModel(model.id, ({ data }) => {
        if (data.code === 0) {
          this.$message.success(this.$t("modelConfig.setDefaultSuccess"));
          this.loadData();
        }
      });
    },
    // 搜索功能
    handleSearch() {
      this.currentPage = 1;
      this.loadData();
    },
  },
};
</script>

<style scoped>
.welcome {
  min-width: 900px;
  min-height: 100vh;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 50%, #f1f5f9 100%);
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

/* 操作区域样式 */
.action-section {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  flex-shrink: 0;
}

.add-model-btn {
  height: 36px;
  padding: 0 20px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  background: linear-gradient(135deg, #66bb6a 0%, #4ade80 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(102, 187, 106, 0.2);
  transition: all 0.3s ease;
}

.add-model-btn:hover {
  background: linear-gradient(135deg, #5ca85c 0%, #22c55e 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 187, 106, 0.3);
}

/* 模型类型选择器 */
.model-types-selector {
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  padding: 16px 40px;
  z-index: 2;
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.selector-container {
  display: flex;
  gap: 8px;
  flex: 1;
  overflow-x: auto;
  padding-bottom: 8px;
  min-width: 0;
}

.model-type-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  flex-shrink: 0;
  position: relative;
  min-width: 100px;
  justify-content: center;
  font-size: 13px;
}

.model-type-item:hover {
  background: #f1f5f9;
  border-color: #cbd5e1;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.model-type-item.active {
  background: linear-gradient(135deg, #66bb6a 0%, #4ade80 100%);
  border-color: #66bb6a;
  color: white;
  box-shadow: 0 6px 20px rgba(102, 187, 106, 0.3);
  transform: translateY(-2px);
}

.item-icon {
  font-size: 20px;
  line-height: 1;
}

.item-text {
  font-size: 14px;
  font-weight: 600;
}

.item-status {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #cbd5e1;
  transition: all 0.3s ease;
}

.item-status.active {
  background: #ffffff;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.8);
}

.model-type-item.active .item-status {
  background: #ffffff;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.8);
}

/* 主内容区域 */
.main-wrapper {
  margin: 0 40px 40px;
  border-radius: 16px;
  background: #ffffff;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  border: 1px solid #f1f5f9;
  overflow: hidden;
  z-index: 2;
  position: relative;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.content-panel {
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}


.content-area {
  padding: 24px 40px;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.model-card {
  border: none;
  box-shadow: none;
  background: transparent;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.model-card ::v-deep .el-card__body {
  padding: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

/* 表格样式 */
.data-table {
  border-radius: 12px;
  overflow: hidden;
  background: white;
  border: 1px solid #f1f5f9;
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.data-table ::v-deep .el-table {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.data-table ::v-deep .el-table__body-wrapper {
  flex: 1;
  overflow-y: auto;
  max-height: calc(100vh - 280px);
}

.data-table ::v-deep .el-table__header-wrapper {
  background: #fafbfc;
}

.data-table ::v-deep .el-table__header th {
  background: transparent;
  color: #1f2937;
  font-weight: 600;
  font-size: 12px;
  border-bottom: 1px solid #e5e7eb;
  padding: 12px 8px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.data-table ::v-deep .el-table__body td {
  border-bottom: 1px solid #f3f4f6;
  padding: 12px 8px;
  font-size: 13px;
  color: #1f2937;
}

.data-table ::v-deep .el-table__row:hover {
  background: #f9fafb;
}

.data-table ::v-deep .el-table__row:hover td {
  background: #f9fafb;
}

/* 开关样式 */
::v-deep .el-switch.is-checked .el-switch__core {
  background-color: #66BB6A;
  border-color: #66BB6A;
}

/* 操作按钮容器 */
.action-buttons {
  display: flex;
  gap: 6px;
  justify-content: center;
  align-items: center;
}

.action-buttons-vertical {
  display: flex;
  flex-direction: column;
  gap: 4px;
  justify-content: center;
  align-items: center;
}

/* 按钮样式 */
.edit-btn, .duplicate-btn, .delete-btn, .voice-management-btn {
  border-radius: 4px;
  padding: 3px 8px;
  font-size: 11px;
  font-weight: 500;
  transition: all 0.2s ease;
  border: none;
  margin: 0;
  min-width: 60px;
  width: 100%;
}

.edit-btn {
  color: #3b82f6;
  background: #eff6ff;
  border: 1px solid #dbeafe;
}

.edit-btn:hover {
  background: #dbeafe;
  color: #1d4ed8;
  transform: translateY(-1px);
}

.duplicate-btn {
  color: #8b5cf6;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
}

.duplicate-btn:hover {
  background: #e5e7eb;
  color: #7c3aed;
  transform: translateY(-1px);
}

.delete-btn {
  color: #ef4444;
  background: #fef2f2;
  border: 1px solid #fecaca;
}

.delete-btn:hover {
  background: #fecaca;
  color: #dc2626;
  transform: translateY(-1px);
}

.voice-management-btn {
  background: linear-gradient(135deg, #66bb6a 0%, #4ade80 100%);
  color: white;
  border: none;
  box-shadow: 0 2px 4px rgba(102, 187, 106, 0.2);
}

.voice-management-btn:hover {
  background: linear-gradient(135deg, #5ca85c 0%, #22c55e 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(102, 187, 106, 0.3);
}

/* 表格底部操作区 */
.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 40px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(248, 250, 252, 0.95) 100%);
  backdrop-filter: blur(10px);
  border-top: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 0 0 16px 16px;
}

.batch-actions {
  display: flex;
  gap: 12px;
}

.batch-actions .el-button {
  border-radius: 10px;
  padding: 10px 20px;
  font-weight: 600;
  font-size: 14px;
  border: none;
  transition: all 0.3s ease;
  height: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.batch-actions .el-button--primary {
  background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
  color: white;
}

.batch-actions .el-button--primary:hover {
  background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.batch-actions .el-button--success {
  background: linear-gradient(135deg, #66bb6a 0%, #4ade80 100%);
  color: white;
}

.batch-actions .el-button--success:hover {
  background: linear-gradient(135deg, #5ca85c 0%, #22c55e 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 187, 106, 0.4);
}

.batch-actions .el-button--danger {
  background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  color: white;
}

.batch-actions .el-button--danger:hover {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(239, 68, 68, 0.4);
}

/* 分页器样式 */
.custom-pagination {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-info {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
  margin-left: 12px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 8px;
  backdrop-filter: blur(5px);
}

.pagination-btn {
  border: 1px solid rgba(209, 213, 219, 0.6);
  background: rgba(255, 255, 255, 0.8);
  color: #374151;
  border-radius: 10px;
  padding: 8px 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.pagination-btn:hover {
  background: rgba(243, 244, 246, 0.9);
  border-color: #9ca3af;
  color: #1f2937;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.pagination-btn.active {
  background: linear-gradient(135deg, #66bb6a 0%, #4ade80 100%);
  color: white;
  border-color: #66bb6a;
  box-shadow: 0 4px 12px rgba(102, 187, 106, 0.3);
  transform: translateY(-1px);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: rgba(249, 250, 251, 0.8);
  color: #9ca3af;
  transform: none;
}

::v-deep .page-size-select .el-input__inner {
  border-radius: 10px;
  border: 1px solid rgba(209, 213, 219, 0.6);
  background: rgba(255, 255, 255, 0.8);
  height: 40px;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

::v-deep .page-size-select .el-input__inner:focus {
  border-color: #66bb6a;
  box-shadow: 0 0 0 3px rgba(102, 187, 106, 0.1), 0 4px 8px rgba(0, 0, 0, 0.1);
  background: rgba(255, 255, 255, 0.95);
}

/* 中等屏幕适配 */
@media screen and (max-width: 1024px) {
  .model-types-selector {
    gap: 16px;
  }
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .welcome {
    min-width: unset;
    padding: 0;
  }

  /* 模型类型选择器移动端适配 */
  .model-types-selector {
    padding: 12px 16px;
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .selector-container {
    order: 1;
    overflow-x: auto;
    padding-bottom: 4px;
    gap: 6px;
  }

  .action-section {
    order: 2;
    justify-content: center;
    width: 100%;
  }

  .add-model-btn {
    height: 32px;
    padding: 0 16px;
    font-size: 13px;
  }

  /* 模型类型选择器项目移动端适配 */
  .model-type-item {
    padding: 8px 12px;
    min-width: 80px;
    font-size: 12px;
    gap: 6px;
  }

  .item-icon {
    font-size: 14px;
  }

  /* 主内容区域移动端适配 */
  .main-wrapper {
    margin: 0 16px 16px;
    border-radius: 12px;
  }

  .content-area {
    padding: 16px;
  }

  /* 表格移动端适配 */
  .data-table ::v-deep .el-table__body-wrapper {
    max-height: calc(100vh - 200px);
  }

  .data-table ::v-deep .el-table__header th {
    padding: 8px 4px;
    font-size: 11px;
  }

  .data-table ::v-deep .el-table__body td {
    padding: 8px 4px;
    font-size: 12px;
  }

  /* 操作按钮移动端适配 */
  .action-buttons-vertical {
    gap: 2px;
  }

  .edit-btn, .duplicate-btn, .delete-btn, .voice-management-btn {
    padding: 2px 6px;
    font-size: 10px;
    min-width: 50px;
  }

  /* 表格底部移动端适配 */
  .table-footer {
    flex-direction: column;
    gap: 12px;
    padding: 12px 16px;
  }

  .batch-actions {
    justify-content: center;
    gap: 8px;
  }

  .custom-pagination {
    justify-content: center;
  }

  .pagination-btn {
    padding: 4px 8px;
    font-size: 12px;
  }
}

@media screen and (max-width: 480px) {
  .model-types-selector {
    padding: 8px 12px;
  }

  .selector-container {
    gap: 4px;
  }

  .model-type-item {
    padding: 6px 10px;
    min-width: 70px;
    font-size: 11px;
  }

  .action-section {
    justify-content: center;
  }

  .add-model-btn {
    height: 30px;
    padding: 0 12px;
    font-size: 12px;
    flex-shrink: 0;
  }

  .main-wrapper {
    margin: 0 8px 8px;
  }

  .content-area {
    padding: 12px;
  }

  .data-table ::v-deep .el-table__header th {
    padding: 6px 2px;
    font-size: 10px;
  }

  .data-table ::v-deep .el-table__body td {
    padding: 6px 2px;
    font-size: 11px;
  }

  .model-type-item {
    padding: 8px 16px;
    font-size: 12px;
  }

  .item-icon {
    font-size: 16px;
  }

  .main-wrapper {
    margin: 0 20px 20px;
  }

  .content-header {
    flex-direction: column;
    gap: 15px;
    padding: 20px;
  }

  .content-area {
    padding: 20px;
  }
}

.operation-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
}

.page-title {
  font-size: 24px;
  margin: 0;
}

.content-panel {
  flex: 1;
  display: flex;
  overflow: hidden;
  height: 100%;
  border-radius: 15px;
  background: transparent;
  border: 1px solid #fff;
}

.nav-panel {
  min-width: 242px;
  height: 100%;
  border-right: 1px solid #ebeef5;
  background: linear-gradient(
      120deg,
      rgba(107, 140, 255, 0.3) 0%,
      rgba(169, 102, 255, 0.3) 25%,
      transparent 60%
    ),
    url("../assets/model/model.png") no-repeat center / cover;
  padding: 16px 0;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.nav-panel .el-menu-item {
  height: 50px;
  background: #e9f0ff;
  line-height: 50px;
  border-radius: 4px 0 0 4px !important;
  transition: all 0.3s;
  display: flex !important;
  justify-content: flex-end;
  padding-right: 12px !important;
  width: fit-content;
  margin: 8px 0 8px auto;
  min-width: unset;
}

.nav-panel .el-menu-item.is-active {
  background: #5778ff;
  position: relative;
  padding-left: 40px !important;
}

.nav-panel .el-menu-item.is-active::before {
  content: "";
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  width: 13px;
  height: 13px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 4px rgba(64, 158, 255, 0.5);
}

.menu-text {
  font-size: 14px;
  color: #606266;
  text-align: right;
  width: 100%;
  padding-right: 8px;
}

.content-area {
  flex: 1;
  padding: 24px;
  height: 100%;
  min-width: 600px;
  overflow: hidden;
  background-color: white;
  display: flex;
  flex-direction: column;
}

.action-group {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-group {
  display: flex;
  gap: 10px;
}

.search-input {
  width: 240px;
}

.btn-search {
  background: linear-gradient(135deg, #6b8cff, #a966ff);
  border: none;
  color: white;
}

.btn-search:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

::v-deep .search-input .el-input__inner {
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  background-color: white;
  transition: border-color 0.2s;
}

::v-deep .page-size-select {
  width: 100px;
  margin-right: 8px;
}

::v-deep .page-size-select .el-input__inner {
  height: 32px;
  line-height: 32px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  background: #dee7ff;
  color: #606266;
  font-size: 14px;
}

::v-deep .page-size-select .el-input__suffix {
  right: 6px;
  width: 15px;
  height: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  top: 6px;
  border-radius: 4px;
}

::v-deep .page-size-select .el-input__suffix-inner {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
}

::v-deep .page-size-select .el-icon-arrow-up:before {
  content: "";
  display: inline-block;
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 9px solid #606266;
  position: relative;
  transform: rotate(0deg);
  transition: transform 0.3s;
}

::v-deep .search-input .el-input__inner:focus {
  border-color: #6b8cff;
  outline: none;
}

.data-table {
  border-radius: 6px;
  overflow: hidden;
  background-color: transparent !important;
}

.data-table /deep/ .el-table__row {
  background-color: transparent !important;
}

.table-header th {
  background-color: transparent !important;
  color: #606266;
  font-weight: 600;
}

.table-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  width: 100%;
  flex-shrink: 0;
  min-height: 60px;
  background: white;
}

.batch-actions {
  display: flex;
  gap: 8px;
}

.batch-actions .el-button {
  min-width: 72px;
  height: 32px;
  padding: 7px 12px 7px 10px;
  font-size: 12px;
  border-radius: 4px;
  line-height: 1;
  font-weight: 500;
  border: none;
  transition: all 0.3s ease;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.batch-actions .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.batch-actions .el-button--primary {
  background: #5f70f3 !important;
  color: white;
}

.batch-actions .el-button--success {
  background: #5bc98c;
  color: white;
}

.batch-actions .el-button--danger {
  background: #fd5b63;
  color: white;
}

.batch-actions .el-button:first-child {
  background: linear-gradient(135deg, #409eff, #6b8cff);
  border: none;
  color: white;
}

.batch-actions .el-button:first-child:hover {
  background: linear-gradient(135deg, #3a8ee6, #5a7cff);
}

.el-table th /deep/ .el-table__cell {
  overflow: hidden;
  -webkit-user-select: none;
  -moz-user-select: none;
  user-select: none;
  background-color: transparent !important;
}

::v-deep .el-table .custom-selection-header .cell .el-checkbox__inner {
  display: none !important;
}

::v-deep .el-table .custom-selection-header .cell::before {
  content: attr(data-content);
  display: block;
  text-align: center;
  line-height: 32px;
  /* 设置合适的行高，确保文本完整显示 */
  color: black;
  margin-top: 0;
  /* 移除可能导致偏移的上边距 */
  height: 32px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
}

.custom-selection-header .cell {
  position: relative;
}

/* 已移除可能影响文本显示的空伪元素 */

::v-deep .el-table__body .el-checkbox__inner {
  display: inline-block !important;
  background: #e6edfa;
}

::v-deep .el-table thead th:not(:first-child) .cell {
  color: #303133 !important;
}

::v-deep .nav-panel .el-menu-item.is-active .menu-text {
  color: #fff !important;
}

::v-deep .data-table {
  &.el-table::before,
  &.el-table::after,
  &.el-table__inner-wrapper::before {
    display: none !important;
  }
}

::v-deep .data-table .el-table__header-wrapper {
  border-bottom: 1px solid rgb(224, 227, 237);
}

::v-deep .data-table .el-table__body td {
  border-bottom: 1px solid rgb(224, 227, 237) !important;
}

.el-button img {
  height: 1em;
  vertical-align: middle;
  padding-right: 2px;
  padding-bottom: 2px;
}

::v-deep .el-checkbox__inner {
  border-color: #cfcfcf !important;
  transition: all 0.2s ease-in-out;
}

::v-deep .el-checkbox__input.is-checked .el-checkbox__inner {
  background-color: #5f70f3;
  border-color: #5f70f3;
}

.voice-management-btn {
  background: #9db3ea;
  color: white;
  min-width: 68px;
  line-height: 14px;
  white-space: nowrap;
  transition: all 0.3s;
  border-radius: 10px;
}

.voice-management-btn:hover {
  background: #8aa2e0;
  /* 悬停时颜色加深 */
  transform: scale(1.05);
}

::v-deep .el-table .el-table-column--selection .cell {
  padding-left: 15px !important;
}

::v-deep .el-table .el-table__fixed-right .cell {
  padding-right: 15px !important;
}

.edit-btn,
.delete-btn {
  margin: 0 8px;
  color: #7079aa !important;
}

::v-deep .el-table .cell {
  padding-left: 10px;
  padding-right: 10px;
}

/* 分页器 */
.custom-pagination {
  display: flex;
  align-items: center;
  gap: 8px;

  /* 导航按钮样式 (首页、上一页、下一页) */
  .pagination-btn:first-child,
  .pagination-btn:nth-child(2),
  .pagination-btn:nth-child(3),
  .pagination-btn:nth-last-child(2) {
    min-width: 60px;
    height: 32px;
    padding: 0 12px;
    border-radius: 4px;
    border: 1px solid #e4e7ed;
    background: #dee7ff;
    color: #606266;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: #d7dce6;
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  /* 数字按钮样式 */
  .pagination-btn:not(:first-child):not(:nth-child(2)):not(:nth-child(3)):not(:nth-last-child(2)) {
    min-width: 28px;
    height: 32px;
    padding: 0;
    border-radius: 4px;
    border: 1px solid transparent;
    background: transparent;
    color: #606266;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(245, 247, 250, 0.3);
    }
  }

  .pagination-btn.active {
    background: #5f70f3 !important;
    color: #ffffff !important;
    border-color: #5f70f3 !important;

    &:hover {
      background: #6d7cf5 !important;
    }
  }

  .total-text {
    color: #909399;
    font-size: 14px;
    margin-left: 10px;
  }
}

.model-card {
  background: white;
  flex: 1;
  display: flex;
  flex-direction: column;
  border: none;
  box-shadow: none;
  overflow: hidden;
}

.model-card ::v-deep .el-card__body {
  padding: 0;
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow: hidden;
}

.data-table {
  --table-max-height: calc(100vh - 45vh);
  max-height: var(--table-max-height);
}

.data-table ::v-deep .el-table__body-wrapper {
  max-height: calc(var(--table-max-height) - 80px);
  overflow-y: auto;
}

::v-deep .el-loading-mask {
  background-color: rgba(255, 255, 255, 0.6) !important;
  backdrop-filter: blur(2px);
}

::v-deep .el-loading-spinner .circular {
  width: 28px;
  height: 28px;
}

::v-deep .el-loading-spinner .path {
  stroke: #6b8cff;
}

::v-deep .el-loading-text {
  color: #6b8cff !important;
  font-size: 14px;
  margin-top: 8px;
}
</style>
