<template>
  <div class="welcome">
    <HeaderBar />

    <div class="health-report-page" v-loading="loading" element-loading-text="正在生成报告...">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-left">
          <el-button
            type="text"
            icon="el-icon-arrow-left"
            @click="goBack"
            class="back-btn"
          >
            返回设备管理
          </el-button>
          <h1 class="page-title">心理健康报告</h1>
        </div>
        <div class="header-actions">
          <el-button type="primary" icon="el-icon-download" @click="exportPDF">导出PDF</el-button>
          <el-button type="default" icon="el-icon-share" @click="shareReport">分享</el-button>
        </div>
      </div>

      <!-- 设备信息卡片 -->
      <div class="device-info-card">
        <div class="device-details">
          <h2 class="device-name">{{ deviceInfo.deviceName || '设备' }}</h2>
          <p class="device-mac">MAC: {{ deviceInfo.macAddress }}</p>
          <p class="device-model">型号: {{ deviceInfo.model }}</p>
        </div>
        <div class="report-meta">
          <div class="meta-item">
            <span class="meta-label">分析周期</span>
            <span class="meta-value">最近30天</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">生成时间</span>
            <span class="meta-value">{{ formatDate(reportData?.generatedAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 报告内容 -->
      <div class="report-content" v-if="reportData">
        <!-- 概览卡片 -->
        <div class="overview-section">
          <h3 class="section-title">健康概览</h3>
          <div class="overview-cards">
            <div class="overview-card score-card">
              <div class="card-icon">
                <i class="el-icon-medal"></i>
              </div>
              <div class="card-content">
                <h4 class="card-title">综合评分</h4>
                <div class="score-value">{{ reportData.overallScore }}</div>
                <div class="score-label">{{ getScoreLabel(reportData.overallScore) }}</div>
              </div>
            </div>

            <div class="overview-card risk-card">
              <div class="card-icon" :class="getRiskIconClass(reportData.riskLevel)">
                <i :class="getRiskIcon(reportData.riskLevel)"></i>
              </div>
              <div class="card-content">
                <h4 class="card-title">风险等级</h4>
                <div class="risk-value" :class="getRiskClass(reportData.riskLevel)">
                  {{ getRiskText(reportData.riskLevel) }}
                </div>
                <div class="risk-desc">{{ getRiskDescription(reportData.riskLevel) }}</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 多维度分析 -->
        <div class="analysis-section">
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

        <!-- 趋势分析 -->
        <div class="trend-section" v-if="reportData.trendAnalysis">
          <h3 class="section-title">心理健康趋势分析</h3>
          <div class="trend-chart-container">
            <div ref="trendChart" class="trend-chart"></div>
          </div>
        </div>

        <!-- 风险分析 -->
        <div class="risk-analysis-section" v-if="reportData.riskReasons && reportData.riskReasons.length > 0">
          <h3 class="section-title">风险分析</h3>
          <div class="risk-info">
            <div class="risk-classify">
              <div class="classify-item">
                <span class="classify-label">一级分类：</span>
                <span class="classify-value">{{ reportData.firstClassify || '未分类' }}</span>
              </div>
              <div class="classify-item" v-if="reportData.secondClassify">
                <span class="classify-label">二级分类：</span>
                <span class="classify-value">{{ reportData.secondClassify }}</span>
              </div>
            </div>
            <div class="risk-reasons">
              <h4 class="reasons-title">风险原因</h4>
              <ul class="reasons-list">
                <li v-for="(reason, index) in reportData.riskReasons" :key="index" class="reason-item">
                  {{ reason }}
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 专业建议 -->
        <div class="recommendations-section" v-if="reportData.recommendations">
          <h3 class="section-title">个性化建议</h3>
          <div class="recommendations-grid">
            <div
              v-for="(recommendation, index) in reportData.recommendations"
              :key="index"
              class="recommendation-card"
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
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts';
import Api from '@/apis/api';
import HeaderBar from '@/components/HeaderBar.vue';

export default {
  name: 'HealthReport',
  components: {
    HeaderBar
  },
  data() {
    return {
      loading: false,
      deviceInfo: {},
      reportData: null,
      radarChart: null,
      trendChart: null
    };
  },
  mounted() {
    this.initDeviceInfo();
    this.generateReport();
  },
  beforeDestroy() {
    this.destroyCharts();
  },
  methods: {
    initDeviceInfo() {
      // 从路由参数获取设备信息
      const { deviceId, deviceName, macAddress, model, firmwareVersion } = this.$route.query;
      this.deviceInfo = {
        deviceId,
        deviceName: deviceName || '未命名设备',
        macAddress,
        model,
        firmwareVersion
      };
    },

    goBack() {
      this.$router.go(-1);
    },

    async generateReport() {
      if (!this.deviceInfo.deviceId) {
        this.$message.error('设备信息不完整');
        this.goBack();
        return;
      }

      this.loading = true;
      try {
        // 获取当前用户ID
        const userInfo = this.$store.getters.getUserInfo;
        const userId = userInfo.id || userInfo.userId || localStorage.getItem('userId') || '';
        if (!userId) {
          throw new Error('用户未登录或用户ID获取失败');
        }

        const response = await Api.device.generateHealthReport({
          day: 30, // 分析最近30天的数据
          userId: userId
        });

        if (response.data.code === 0) {
          // 根据接口文档格式处理响应数据
          const apiData = response.data.data;
          this.reportData = {
            // 基本信息
            id: apiData.id,
            userId: apiData.userId,
            startTime: apiData.startTime,
            endTime: apiData.endTime,
            generatedAt: apiData.createdAt,

            // 风险评估结果
            firstClassify: apiData.firstClassify,
            secondClassify: apiData.secondClassify,
            riskLevel: this.mapRiskLevel(apiData.firstClassify), // 映射风险等级
            overallScore: this.calculateOverallScore(apiData.firstClassify), // 计算综合评分

            // 风险原因和建议
            riskReasons: apiData.riskReason || [],
            recommendations: this.formatRecommendations(apiData.suggestion || []),

            // 模拟多维度数据（实际项目中可能需要从其他接口获取）
            dimensions: {
              emotionalState: 88,
              stressLevel: 72,
              socialInteraction: 90,
              sleepQuality: 78,
              anxietyLevel: 82
            },

            // 模拟趋势数据（实际项目中可能需要从其他接口获取）
            trendAnalysis: this.generateMockTrendData()
          };

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
          },
          {
            type: 'social',
            title: '加强社交互动',
            description: '主动参与社交活动，与朋友家人保持良好沟通，建立支持网络。',
            tags: ['社交', '沟通', '人际关系']
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
      if (!dateStr) return '';
      const date = new Date(dateStr);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    },

    exportPDF() {
      this.$message.info('PDF导出功能开发中...');
    },

    shareReport() {
      this.$message.info('分享功能开发中...');
    },

    // 映射风险等级
    mapRiskLevel(firstClassify) {
      if (!firstClassify) return 'low';
      const classify = firstClassify.toLowerCase();
      if (classify.includes('高') || classify.includes('严重') || classify.includes('high')) {
        return 'high';
      } else if (classify.includes('中') || classify.includes('moderate') || classify.includes('medium')) {
        return 'medium';
      } else {
        return 'low';
      }
    },

    // 计算综合评分
    calculateOverallScore(firstClassify) {
      if (!firstClassify) return 85;
      const classify = firstClassify.toLowerCase();
      if (classify.includes('高') || classify.includes('严重') || classify.includes('high')) {
        return Math.floor(Math.random() * 20) + 40; // 40-59
      } else if (classify.includes('中') || classify.includes('moderate') || classify.includes('medium')) {
        return Math.floor(Math.random() * 20) + 60; // 60-79
      } else {
        return Math.floor(Math.random() * 20) + 80; // 80-99
      }
    },

    // 格式化建议数据
    formatRecommendations(suggestions) {
      return suggestions.map((suggestion, index) => ({
        type: this.getRecommendationType(index),
        title: this.getRecommendationTitle(suggestion),
        description: suggestion,
        tags: this.getRecommendationTags(suggestion)
      }));
    },

    // 获取建议类型
    getRecommendationType(index) {
      const types = ['exercise', 'sleep', 'mindfulness', 'social', 'nutrition'];
      return types[index % types.length];
    },

    // 获取建议标题
    getRecommendationTitle(suggestion) {
      if (suggestion.includes('运动') || suggestion.includes('锻炼')) return '增加运动锻炼';
      if (suggestion.includes('睡眠') || suggestion.includes('休息')) return '改善睡眠质量';
      if (suggestion.includes('冥想') || suggestion.includes('放松')) return '心理放松训练';
      if (suggestion.includes('社交') || suggestion.includes('交流')) return '加强社交互动';
      if (suggestion.includes('饮食') || suggestion.includes('营养')) return '调整饮食结构';
      return '健康生活建议';
    },

    // 获取建议标签
    getRecommendationTags(suggestion) {
      const tags = [];
      if (suggestion.includes('运动') || suggestion.includes('锻炼')) tags.push('运动');
      if (suggestion.includes('睡眠') || suggestion.includes('休息')) tags.push('睡眠');
      if (suggestion.includes('冥想') || suggestion.includes('放松')) tags.push('放松');
      if (suggestion.includes('社交') || suggestion.includes('交流')) tags.push('社交');
      if (suggestion.includes('饮食') || suggestion.includes('营养')) tags.push('营养');
      if (suggestion.includes('心理') || suggestion.includes('情绪')) tags.push('心理健康');
      return tags.length > 0 ? tags : ['健康建议'];
    },

    // 生成模拟趋势数据
    generateMockTrendData() {
      const data = [];
      const today = new Date();
      for (let i = 5; i >= 0; i--) {
        const date = new Date(today);
        date.setDate(date.getDate() - i * 5);
        data.push({
          date: date.toISOString().split('T')[0],
          score: Math.floor(Math.random() * 20) + 70 // 70-89
        });
      }
      return data;
    }
  }
};
</script>

<style lang="scss" scoped>
.health-report-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 120px);
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #E6F7FF;

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .back-btn {
      font-size: 14px;
      color: #409EFF;

      &:hover {
        color: #66B1FF;
      }
    }

    .page-title {
      font-size: 28px;
      font-weight: 600;
      color: #333;
      margin: 0;
    }
  }

  .header-actions {
    display: flex;
    gap: 12px;
  }
}

/* 设备信息卡片 */
.device-info-card {
  background: linear-gradient(135deg, #4A90E2, #7ED321);
  color: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .device-details {
    .device-name {
      font-size: 24px;
      font-weight: 600;
      margin: 0 0 8px 0;
    }

    .device-mac, .device-model {
      font-size: 14px;
      opacity: 0.9;
      margin: 4px 0;
    }
  }

  .report-meta {
    text-align: right;

    .meta-item {
      margin-bottom: 8px;

      .meta-label {
        display: block;
        font-size: 12px;
        opacity: 0.8;
      }

      .meta-value {
        display: block;
        font-size: 14px;
        font-weight: 500;
      }
    }
  }
}

/* 报告内容区域 */
.report-content {
  .section-title {
    font-size: 20px;
    font-weight: 600;
    color: #333;
    margin: 0 0 20px 0;
    padding-bottom: 12px;
    border-bottom: 2px solid #E6F7FF;
  }
}

/* 概览区域 */
.overview-section {
  margin-bottom: 40px;

  .overview-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
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
      background: linear-gradient(135deg, #4A90E2, #7ED321);
      color: white;

      &.icon-low {
        background: linear-gradient(135deg, #67C23A, #85CE61);
      }

      &.icon-medium {
        background: linear-gradient(135deg, #E6A23C, #EEBE77);
      }

      &.icon-high {
        background: linear-gradient(135deg, #F56C6C, #F78989);
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

      .risk-desc {
        font-size: 12px;
        color: #999;
        margin-top: 4px;
      }
    }
  }
}

/* 分析区域 */
.analysis-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .analysis-content {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 40px;
  }

  .radar-chart-container {
    .radar-chart {
      width: 100%;
      height: 400px;
    }
  }

  .dimensions-list {
    .dimension-item {
      margin-bottom: 24px;

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
        height: 8px;
        background: #F0F0F0;
        border-radius: 4px;
        overflow: hidden;
        margin-bottom: 8px;

        .progress-bar {
          height: 100%;
          border-radius: 4px;
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
}

/* 趋势分析 */
.trend-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .trend-chart-container {
    .trend-chart {
      width: 100%;
      height: 400px;
    }
  }
}

/* 风险分析区域 */
.risk-analysis-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .risk-info {
    .risk-classify {
      margin-bottom: 24px;
      padding: 16px;
      background: #F8F9FA;
      border-radius: 8px;

      .classify-item {
        display: flex;
        align-items: center;
        margin-bottom: 8px;

        &:last-child {
          margin-bottom: 0;
        }

        .classify-label {
          font-weight: 500;
          color: #666;
          min-width: 80px;
        }

        .classify-value {
          color: #333;
          font-weight: 600;
        }
      }
    }

    .risk-reasons {
      .reasons-title {
        font-size: 16px;
        font-weight: 600;
        color: #333;
        margin: 0 0 16px 0;
      }

      .reasons-list {
        margin: 0;
        padding: 0;
        list-style: none;

        .reason-item {
          position: relative;
          padding: 12px 0 12px 24px;
          color: #666;
          line-height: 1.6;
          border-bottom: 1px solid #F0F0F0;

          &:last-child {
            border-bottom: none;
          }

          &::before {
            content: '•';
            position: absolute;
            left: 8px;
            color: #E6A23C;
            font-weight: bold;
          }
        }
      }
    }
  }
}

/* 建议区域 */
.recommendations-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .recommendations-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
  }

  .recommendation-card {
    background: #F8F9FA;
    border-radius: 12px;
    padding: 20px;
    display: flex;
    gap: 16px;
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-2px);
    }

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
  .health-report-page {
    padding: 10px;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;

    .header-left {
      flex-direction: column;
      gap: 8px;
    }

    .page-title {
      font-size: 24px;
    }
  }

  .device-info-card {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .analysis-content {
    grid-template-columns: 1fr !important;
    gap: 20px;
  }

  .radar-chart, .trend-chart {
    height: 300px !important;
  }

  .recommendations-grid {
    grid-template-columns: 1fr !important;
  }

  .recommendation-card {
    flex-direction: column;
    text-align: center;

    .recommendation-icon {
      align-self: center;
    }
  }
}
</style>
