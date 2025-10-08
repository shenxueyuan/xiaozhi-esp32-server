<template>
  <el-dialog
    :title="dialogTitle"
    :visible.sync="dialogVisible"
    :width="isMobile ? '100%' : '90%'"
    :before-close="handleClose"
    custom-class="health-report-dialog"
    :close-on-click-modal="false"
  >
    <div class="health-report-container" v-loading="loading" element-loading-text="正在生成报告...">
      <!-- 报告头部信息 -->
      <div class="report-header">
        <div class="device-info">
          <h2 class="device-name">{{ deviceInfo.deviceName || '设备' }}</h2>
          <p class="mac-address">MAC: {{ deviceInfo.macAddress }}</p>
        </div>
        <div class="report-actions">
          <el-button type="primary" icon="el-icon-download" @click="exportPDF">导出PDF</el-button>
          <el-button type="default" icon="el-icon-share" @click="shareReport">分享</el-button>
        </div>
      </div>

      <!-- 报告概览卡片 -->
      <div class="overview-cards" v-if="reportData">
        <div class="overview-card score-card">
          <div class="card-icon">
            <i class="el-icon-medal"></i>
          </div>
          <div class="card-content">
            <h3 class="card-title">综合评分</h3>
            <div class="score-value">{{ reportData.overallScore }}</div>
            <div class="score-label">{{ getScoreLabel(reportData.overallScore) }}</div>
          </div>
        </div>

        <div class="overview-card risk-card">
          <div class="card-icon" :class="getRiskIconClass(reportData.riskLevel)">
            <i :class="getRiskIcon(reportData.riskLevel)"></i>
          </div>
          <div class="card-content">
            <h3 class="card-title">风险等级</h3>
            <div class="risk-value" :class="getRiskClass(reportData.riskLevel)">
              {{ getRiskText(reportData.riskLevel) }}
            </div>
            <div class="risk-desc">{{ getRiskDescription(reportData.riskLevel) }}</div>
          </div>
        </div>

        <div class="overview-card time-card">
          <div class="card-icon">
            <i class="el-icon-time"></i>
          </div>
          <div class="card-content">
            <h3 class="card-title">分析周期</h3>
            <div class="time-value">最近30天</div>
            <div class="time-desc">{{ formatDate(reportData.generatedAt) }}</div>
          </div>
        </div>
      </div>

      <!-- 多维度分析区域 -->
      <div class="analysis-section" v-if="reportData">
        <h3 class="section-title">多维度心理健康分析</h3>
        <div class="analysis-content">
          <div class="radar-chart-container">
            <div ref="radarChart" class="radar-chart"></div>
          </div>
          <div class="dimensions-list">
            <div
              v-for="(dimension, key) in reportData.dimensions"
              :key="key"
              class="dimension-item"
            >
              <div class="dimension-header">
                <span class="dimension-name">{{ getDimensionName(key) }}</span>
                <span class="dimension-score">{{ dimension }}/100</span>
              </div>
              <div class="dimension-progress">
                <div
                  class="progress-bar"
                  :style="{ width: dimension + '%', backgroundColor: getDimensionColor(dimension) }"
                ></div>
              </div>
              <p class="dimension-desc">{{ getDimensionDescription(key, dimension) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 趋势分析图表 -->
      <div class="trend-section" v-if="reportData && reportData.trendAnalysis">
        <h3 class="section-title">心理健康趋势分析</h3>
        <div class="trend-chart-container">
          <div ref="trendChart" class="trend-chart"></div>
        </div>
      </div>

      <!-- 专业建议区域 -->
      <div class="recommendations-section" v-if="reportData && reportData.recommendations">
        <h3 class="section-title">个性化建议</h3>
        <div class="recommendations-list">
          <div
            v-for="(recommendation, index) in reportData.recommendations"
            :key="index"
            class="recommendation-item"
          >
            <div class="recommendation-icon">
              <i :class="getRecommendationIcon(recommendation.type)"></i>
            </div>
            <div class="recommendation-content">
              <h4 class="recommendation-title">{{ recommendation.title }}</h4>
              <p class="recommendation-desc">{{ recommendation.description }}</p>
              <div class="recommendation-tags">
                <el-tag
                  v-for="tag in recommendation.tags"
                  :key="tag"
                  size="mini"
                  type="info"
                >
                  {{ tag }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import * as echarts from 'echarts';
import Api from '@/apis/api';

export default {
  name: 'HealthReportDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    deviceInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      isMobile: false,
      reportData: null,
      radarChart: null,
      trendChart: null
    };
  },
  computed: {
    dialogTitle() {
      return `心理健康报告 - ${this.deviceInfo.deviceName || '设备'}`;
    }
  },
  watch: {
    visible(val) {
      this.dialogVisible = val;
      if (val) {
        this.generateReport();
      } else {
        this.destroyCharts();
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
    this.destroyCharts();
  },
  methods: {
    checkScreenSize() {
      this.isMobile = window.innerWidth <= 768;
    },

    handleClose() {
      this.dialogVisible = false;
    },

    async generateReport() {
      if (!this.deviceInfo.deviceId) {
        this.$message.error('设备信息不完整');
        return;
      }

      this.loading = true;
      try {
        const response = await Api.device.generateHealthReport({
          deviceId: this.deviceInfo.deviceId,
          macAddress: this.deviceInfo.macAddress,
          timeRange: '30d'
        });

        if (response.data.code === 200) {
          this.reportData = response.data.data.analysisResult;
          this.reportData.generatedAt = response.data.data.generatedAt;
          this.reportData.recommendations = response.data.data.recommendations;
          this.reportData.trendAnalysis = response.data.data.trendAnalysis;

          this.$nextTick(() => {
            this.initCharts();
          });
        } else {
          throw new Error(response.data.message || '生成报告失败');
        }
      } catch (error) {
        console.error('生成报告失败:', error);
        this.$message.error(error.message || '生成报告失败，请稍后重试');
        // 使用模拟数据进行演示
        this.loadMockData();
      } finally {
        this.loading = false;
      }
    },

    loadMockData() {
      // 模拟数据用于演示
      this.reportData = {
        overallScore: 85,
        riskLevel: 'low',
        dimensions: {
          emotionalState: 88,
          stressLevel: 72,
          socialInteraction: 90,
          sleepQuality: 78,
          anxietyLevel: 82
        },
        generatedAt: new Date().toISOString(),
        recommendations: [
          {
            type: 'exercise',
            title: '增加户外活动',
            description: '建议每天进行30分钟的户外运动，如散步、慢跑或骑行，有助于改善情绪状态。',
            tags: ['运动', '户外', '情绪调节']
          },
          {
            type: 'sleep',
            title: '优化睡眠质量',
            description: '保持规律的作息时间，建立良好的睡前习惯，避免睡前使用电子设备。',
            tags: ['睡眠', '作息', '健康习惯']
          },
          {
            type: 'mindfulness',
            title: '冥想放松练习',
            description: '每天进行10-15分钟的冥想或深呼吸练习，有助于减轻压力和焦虑。',
            tags: ['冥想', '放松', '压力管理']
          }
        ],
        trendAnalysis: [
          { date: '2024-09-01', score: 78 },
          { date: '2024-09-08', score: 82 },
          { date: '2024-09-15', score: 79 },
          { date: '2024-09-22', score: 85 },
          { date: '2024-09-29', score: 88 },
          { date: '2024-10-06', score: 85 }
        ]
      };

      this.$nextTick(() => {
        this.initCharts();
      });
    },

    initCharts() {
      this.initRadarChart();
      this.initTrendChart();
    },

    initRadarChart() {
      if (!this.$refs.radarChart) return;

      this.radarChart = echarts.init(this.$refs.radarChart);

      const option = {
        radar: {
          indicator: [
            { name: '情绪状态', max: 100 },
            { name: '压力水平', max: 100 },
            { name: '社交互动', max: 100 },
            { name: '睡眠质量', max: 100 },
            { name: '焦虑水平', max: 100 }
          ],
          radius: '70%',
          axisLine: {
            lineStyle: {
              color: '#E6F7FF'
            }
          },
          splitLine: {
            lineStyle: {
              color: '#E6F7FF'
            }
          },
          splitArea: {
            show: true,
            areaStyle: {
              color: ['rgba(64, 158, 255, 0.1)', 'rgba(64, 158, 255, 0.05)']
            }
          }
        },
        series: [{
          type: 'radar',
          data: [{
            value: [
              this.reportData.dimensions.emotionalState,
              this.reportData.dimensions.stressLevel,
              this.reportData.dimensions.socialInteraction,
              this.reportData.dimensions.sleepQuality,
              this.reportData.dimensions.anxietyLevel
            ],
            areaStyle: {
              color: 'rgba(64, 158, 255, 0.3)'
            },
            lineStyle: {
              color: '#409EFF',
              width: 2
            },
            itemStyle: {
              color: '#409EFF'
            }
          }]
        }]
      };

      this.radarChart.setOption(option);
    },

    initTrendChart() {
      if (!this.$refs.trendChart || !this.reportData.trendAnalysis) return;

      this.trendChart = echarts.init(this.$refs.trendChart);

      const dates = this.reportData.trendAnalysis.map(item => item.date);
      const scores = this.reportData.trendAnalysis.map(item => item.score);

      const option = {
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLine: {
            lineStyle: {
              color: '#E6F7FF'
            }
          }
        },
        yAxis: {
          type: 'value',
          min: 0,
          max: 100,
          axisLine: {
            lineStyle: {
              color: '#E6F7FF'
            }
          }
        },
        series: [{
          data: scores,
          type: 'line',
          smooth: true,
          lineStyle: {
            color: '#409EFF',
            width: 3
          },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ])
          },
          itemStyle: {
            color: '#409EFF'
          }
        }]
      };

      this.trendChart.setOption(option);
    },

    destroyCharts() {
      if (this.radarChart) {
        this.radarChart.dispose();
        this.radarChart = null;
      }
      if (this.trendChart) {
        this.trendChart.dispose();
        this.trendChart = null;
      }
    },

    // 工具方法
    getScoreLabel(score) {
      if (score >= 90) return '优秀';
      if (score >= 80) return '良好';
      if (score >= 70) return '一般';
      if (score >= 60) return '需关注';
      return '需改善';
    },

    getRiskText(level) {
      const riskMap = {
        low: '低风险',
        medium: '中风险',
        high: '高风险'
      };
      return riskMap[level] || '未知';
    },

    getRiskClass(level) {
      return `risk-${level}`;
    },

    getRiskIcon(level) {
      const iconMap = {
        low: 'el-icon-success',
        medium: 'el-icon-warning',
        high: 'el-icon-error'
      };
      return iconMap[level] || 'el-icon-info';
    },

    getRiskIconClass(level) {
      return `icon-${level}`;
    },

    getRiskDescription(level) {
      const descMap = {
        low: '心理状态良好',
        medium: '需适当关注',
        high: '建议寻求专业帮助'
      };
      return descMap[level] || '';
    },

    getDimensionName(key) {
      const nameMap = {
        emotionalState: '情绪状态',
        stressLevel: '压力水平',
        socialInteraction: '社交互动',
        sleepQuality: '睡眠质量',
        anxietyLevel: '焦虑水平'
      };
      return nameMap[key] || key;
    },

    getDimensionColor(score) {
      if (score >= 80) return '#67C23A';
      if (score >= 60) return '#E6A23C';
      return '#F56C6C';
    },

    getDimensionDescription(key, score) {
      // 根据维度和分数返回描述
      if (score >= 80) return '状态良好，继续保持';
      if (score >= 60) return '状态一般，可适当改善';
      return '需要重点关注和改善';
    },

    getRecommendationIcon(type) {
      const iconMap = {
        exercise: 'el-icon-bicycle',
        sleep: 'el-icon-moon-night',
        mindfulness: 'el-icon-sunny',
        social: 'el-icon-user-solid',
        nutrition: 'el-icon-food'
      };
      return iconMap[type] || 'el-icon-info';
    },

    formatDate(dateStr) {
      const date = new Date(dateStr);
      return `生成于 ${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    },

    exportPDF() {
      this.$message.info('PDF导出功能开发中...');
    },

    shareReport() {
      this.$message.info('分享功能开发中...');
    }
  }
};
</script>

<style lang="scss" scoped>
.health-report-container {
  padding: 0;
  min-height: 600px;
}

/* 报告头部 */
.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #4A90E2, #7ED321);
  color: white;
  border-radius: 12px;
  margin-bottom: 30px;

  .device-info {
    .device-name {
      font-size: 24px;
      font-weight: 600;
      margin: 0 0 8px 0;
    }

    .mac-address {
      font-size: 14px;
      opacity: 0.9;
      margin: 0;
    }
  }

  .report-actions {
    display: flex;
    gap: 12px;
  }
}

/* 概览卡片 */
.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.overview-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  gap: 16px;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-2px);
  }

  .card-icon {
    width: 48px;
    height: 48px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;

    &.icon-low {
      background: linear-gradient(135deg, #67C23A, #85CE61);
      color: white;
    }

    &.icon-medium {
      background: linear-gradient(135deg, #E6A23C, #EEBE77);
      color: white;
    }

    &.icon-high {
      background: linear-gradient(135deg, #F56C6C, #F78989);
      color: white;
    }
  }

  .card-content {
    flex: 1;

    .card-title {
      font-size: 14px;
      color: #666;
      margin: 0 0 8px 0;
    }

    .score-value {
      font-size: 32px;
      font-weight: 700;
      color: #4A90E2;
      line-height: 1;
    }

    .score-label {
      font-size: 12px;
      color: #999;
    }

    .risk-value {
      font-size: 18px;
      font-weight: 600;

      &.risk-low {
        color: #67C23A;
      }

      &.risk-medium {
        color: #E6A23C;
      }

      &.risk-high {
        color: #F56C6C;
      }
    }

    .risk-desc, .time-desc {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }

    .time-value {
      font-size: 16px;
      font-weight: 600;
      color: #333;
    }
  }
}

/* 分析区域 */
.analysis-section, .trend-section, .recommendations-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .section-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin: 0 0 20px 0;
    padding-bottom: 12px;
    border-bottom: 2px solid #E6F7FF;
  }
}

.analysis-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.radar-chart-container {
  .radar-chart {
    width: 100%;
    height: 300px;
  }
}

.dimensions-list {
  .dimension-item {
    margin-bottom: 20px;

    .dimension-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .dimension-name {
        font-weight: 500;
        color: #333;
      }

      .dimension-score {
        font-weight: 600;
        color: #4A90E2;
      }
    }

    .dimension-progress {
      height: 6px;
      background: #F0F0F0;
      border-radius: 3px;
      overflow: hidden;
      margin-bottom: 8px;

      .progress-bar {
        height: 100%;
        border-radius: 3px;
        transition: width 0.3s ease;
      }
    }

    .dimension-desc {
      font-size: 12px;
      color: #666;
      margin: 0;
    }
  }
}

/* 趋势图表 */
.trend-chart-container {
  .trend-chart {
    width: 100%;
    height: 300px;
  }
}

/* 建议区域 */
.recommendations-list {
  .recommendation-item {
    display: flex;
    gap: 16px;
    padding: 20px;
    background: #F8F9FA;
    border-radius: 8px;
    margin-bottom: 16px;

    .recommendation-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      background: linear-gradient(135deg, #4A90E2, #7ED321);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      flex-shrink: 0;
    }

    .recommendation-content {
      flex: 1;

      .recommendation-title {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin: 0 0 8px 0;
      }

      .recommendation-desc {
        font-size: 14px;
        color: #666;
        line-height: 1.5;
        margin: 0 0 12px 0;
      }

      .recommendation-tags {
        display: flex;
        gap: 8px;
        flex-wrap: wrap;
      }
    }
  }
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .health-report-dialog {
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

  .health-report-container {
    min-height: calc(100vh - 120px);
    padding: 0 10px;
  }

  .report-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;

    .report-actions {
      justify-content: center;
    }
  }

  .overview-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .analysis-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .radar-chart, .trend-chart {
    height: 250px !important;
  }

  .recommendation-item {
    flex-direction: column;
    text-align: center;

    .recommendation-icon {
      align-self: center;
    }
  }
}
</style>
