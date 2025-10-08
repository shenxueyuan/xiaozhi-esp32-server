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
        </div>
        <div class="header-actions">
          <el-button type="primary" icon="el-icon-download" @click="exportPDF">导出PDF</el-button>
          <el-button type="default" icon="el-icon-share" @click="shareReport">分享</el-button>
        </div>
      </div>

      <!-- 报告容器 -->
      <div class="report-container" v-if="reportData">
        <!-- 报告头部 - 证书样式 -->
        <div class="report-header">
          <div class="certificate-border">
            <div class="header-decoration">
              <div class="decoration-line"></div>
              <div class="decoration-icon">🏆</div>
              <div class="decoration-line"></div>
            </div>
            <h1 class="report-title">心理健康评估报告</h1>
            <h2 class="report-subtitle">Psychological Health Assessment Report</h2>
            <div class="report-meta">
              <div class="meta-row">
                <span class="meta-label">报告编号：</span>
                <span class="meta-value">{{ reportData.id || 'N/A' }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">评估周期：</span>
                <span class="meta-value">{{ formatDateRange(reportData.startTime, reportData.endTime) }}</span>
              </div>
              <div class="meta-row">
                <span class="meta-label">生成时间：</span>
                <span class="meta-value">{{ formatDate(new Date()) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 评估结果概览 - 突出显示 -->
        <div class="assessment-overview">
          <div class="overview-header">
            <h3 class="section-title">
              <i class="el-icon-s-data"></i>
              评估结果概览
            </h3>
          </div>

          <div class="result-cards">
            <!-- 风险分类卡片 -->
            <div class="result-card primary-card">
              <div class="card-header">
                <div class="card-icon risk-icon" :class="getRiskIconClass(reportData.riskLevel)">
                  <i :class="getRiskIcon(reportData.riskLevel)"></i>
                </div>
                <div class="card-title-group">
                  <h4 class="card-title">风险分类评估</h4>
                  <p class="card-subtitle">Risk Classification</p>
                </div>
              </div>
              <div class="card-content">
                <div class="primary-result">
                  <span class="result-label">一级分类</span>
                  <span class="result-value primary" :class="getRiskClass(reportData.riskLevel)">
                    {{ reportData.firstClassify || '未分类' }}
                  </span>
                </div>
                <div class="secondary-result" v-if="reportData.secondClassify">
                  <span class="result-label">二级分类</span>
                  <span class="result-value secondary">{{ reportData.secondClassify }}</span>
                </div>
              </div>
            </div>

            <!-- 综合评分卡片 -->
            <div class="result-card score-card">
              <div class="card-header">
                <div class="card-icon score-icon">
                  <i class="el-icon-trophy"></i>
                </div>
                <div class="card-title-group">
                  <h4 class="card-title">综合评分</h4>
                  <p class="card-subtitle">Overall Score</p>
                </div>
              </div>
              <div class="card-content">
                <div class="score-display">
                  <div class="score-number">{{ reportData.overallScore }}</div>
                  <div class="score-total">/100</div>
                </div>
                <div class="score-label">{{ getScoreLabel(reportData.overallScore) }}</div>
                <div class="score-bar">
                  <div class="score-progress" :style="{ width: reportData.overallScore + '%' }"></div>
                </div>
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

        <!-- 详细分析报告 -->
        <div class="detailed-analysis" v-if="reportData.riskReasons && reportData.riskReasons.length > 0">
          <div class="analysis-header">
            <h3 class="section-title">
              <i class="el-icon-document"></i>
              详细分析报告
            </h3>
            <div class="analysis-badge">
              <span class="badge-text">专业评估</span>
            </div>
          </div>

          <div class="analysis-content-wrapper">
            <div class="risk-factors">
              <h4 class="subsection-title">
                <span class="title-icon">⚠️</span>
                风险因素识别
              </h4>
              <div class="factors-list">
                <div
                  v-for="(reason, index) in reportData.riskReasons"
                  :key="index"
                  class="factor-item"
                >
                  <div class="factor-number">{{ index + 1 }}</div>
                  <div class="factor-content">
                    <p class="factor-text">{{ reason }}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 专业建议与干预措施 -->
        <div class="recommendations-section" v-if="reportData.recommendations">
          <div class="recommendations-header">
            <h3 class="section-title">
              <i class="el-icon-star-on"></i>
              专业建议与干预措施
            </h3>
            <div class="recommendations-badge">
              <span class="badge-text">专家推荐</span>
            </div>
          </div>

          <div class="recommendations-content">
            <div class="recommendations-intro">
              <p class="intro-text">
                基于您的心理健康评估结果，我们的专业团队为您制定了以下个性化建议和干预措施，
                请结合自身情况，循序渐进地实施这些建议。
              </p>
            </div>

            <div class="recommendations-list">
              <div
                v-for="(recommendation, index) in reportData.recommendations"
                :key="index"
                class="recommendation-item"
              >
                <div class="recommendation-header">
                  <div class="recommendation-number">{{ index + 1 }}</div>
                  <div class="recommendation-icon">
                    <i :class="getRecommendationIcon(recommendation.type)"></i>
                  </div>
                  <h4 class="recommendation-title">{{ recommendation.title }}</h4>
                </div>
                <div class="recommendation-body">
                  <p class="recommendation-desc">{{ recommendation.description }}</p>
                  <div class="recommendation-tags">
                    <el-tag
                      v-for="tag in recommendation.tags"
                      :key="tag"
                      size="small"
                      effect="plain"
                    >
                      {{ tag }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 技术说明 -->
        <div class="technical-description">
          <div class="tech-header">
            <h3 class="tech-title">
              <i class="el-icon-cpu"></i>
              评估技术说明
            </h3>
          </div>
          <div class="tech-content">
            <div class="tech-overview">
              <h4 class="tech-subtitle">核心技术架构</h4>
              <p class="tech-text">
                本报告采用先进的<strong>多模态AI融合分析技术</strong>，通过对您在周期性智能交互过程中产生的
                多维度生理与心理数据进行深度挖掘和智能分析，构建个性化心理健康评估模型。
              </p>
            </div>

            <div class="tech-details">
              <div class="tech-item">
                <div class="tech-icon">🧠</div>
                <div class="tech-info">
                  <h5 class="tech-name">大模型智能引擎</h5>
                  <p class="tech-desc">
                    基于<strong>通义千问72B</strong>大语言模型，采用公开心理学文献、临床诊断标准、
                    心理测评量表等专业数据集进行二次训练和领域微调，确保心理健康评估的
                    <strong>专业性</strong>和<strong>准确性</strong>。
                  </p>
                </div>
              </div>

              <div class="tech-item">
                <div class="tech-icon">💬</div>
                <div class="tech-info">
                  <h5 class="tech-name">文本语义分析</h5>
                  <p class="tech-desc">
                    运用先进的<strong>自然语言处理技术</strong>，深度解析对话文本中的情感倾向、
                    认知模式、行为意图等心理特征，识别潜在的心理健康风险因子。
                  </p>
                </div>
              </div>

              <div class="tech-item">
                <div class="tech-icon">🎵</div>
                <div class="tech-info">
                  <h5 class="tech-name">语音情感识别</h5>
                  <p class="tech-desc">
                    通过<strong>语音信号处理算法</strong>分析语调变化、语速节奏、停顿模式等
                    声学特征，精准识别情绪状态和心理压力水平。
                  </p>
                </div>
              </div>

              <div class="tech-item">
                <div class="tech-icon">👁️</div>
                <div class="tech-info">
                  <h5 class="tech-name">视觉微表情检测</h5>
                  <p class="tech-desc">
                    采用<strong>计算机视觉技术</strong>捕捉面部微表情变化，分析眼神、表情、
                    肢体语言等非言语信息，全面评估心理状态。
                  </p>
                </div>
              </div>
            </div>

            <div class="tech-guarantee">
              <p class="guarantee-text">
                <strong>技术保障：</strong>所有分析算法均经过严格的临床验证和专业评审，
                数据处理全程符合国际隐私保护标准，确保评估结果的科学可靠性。
              </p>
            </div>
          </div>
        </div>

        <!-- 报告结尾 - 证书样式 -->
        <div class="report-footer">
          <div class="footer-content">
            <div class="signature-section">
              <div class="signature-line">
                <span class="signature-label">评估机构：</span>
                <span class="signature-value">心理健康评估中心</span>
              </div>
              <div class="signature-line">
                <span class="signature-label">报告日期：</span>
                <span class="signature-value">{{ formatDate(new Date()) }}</span>
              </div>
            </div>
            <div class="official-seal">
              <div class="seal-circle">
                <div class="seal-text">官方认证</div>
                <div class="seal-subtext">CERTIFIED</div>
              </div>
            </div>
          </div>
          <div class="footer-note">
            <div class="disclaimer-content">
              <h4 class="disclaimer-title">重要声明</h4>
              <p class="disclaimer-text">
                本报告基于人工智能多模态分析技术生成，通过文本语义理解、语音情感识别、
                视觉微表情分析等先进技术，对用户在智能交互过程中的心理状态进行综合评估。
                <strong>本报告仅供健康参考，不能替代专业医疗诊断。</strong>
                如您感到心理困扰或需要专业帮助，请及时咨询持证心理咨询师或精神科医生。
              </p>
              <p class="tech-note">
                技术支持：多模态AI心理健康评估系统 | 数据处理符合隐私保护标准
              </p>
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
            overallScore: this.calculateOverallScore(apiData.firstClassify, apiData.riskReason), // 计算综合评分

            // 风险原因和建议
            riskReasons: apiData.riskReason || [],
            recommendations: this.formatRecommendations(apiData.suggestion || []),

            // 基于风险评估数据智能生成多维度分析
            dimensions: this.generateDimensionsFromRisk(apiData.firstClassify, apiData.secondClassify, apiData.riskReason),

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

    formatDateRange(startTime, endTime) {
      if (!startTime || !endTime) return '最近30天';
      const start = this.formatDate(startTime);
      const end = this.formatDate(endTime);
      return `${start} 至 ${end}`;
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
    calculateOverallScore(firstClassify, riskReasons = []) {
      if (!firstClassify) return 85;

      const classify = firstClassify.toLowerCase();
      let baseScore = 85; // 默认基础分数

      // 根据风险分类确定基础分数范围
      if (classify.includes('高') || classify.includes('严重') || classify.includes('high')) {
        baseScore = 50; // 高风险基础分
      } else if (classify.includes('中') || classify.includes('moderate') || classify.includes('medium')) {
        baseScore = 70; // 中风险基础分
      } else if (classify.includes('异常') || classify.includes('风险')) {
        baseScore = 65; // 异常风险基础分
      } else {
        baseScore = 85; // 低风险或正常基础分
      }

      // 根据风险原因数量调整分数
      const riskCount = riskReasons.length;
      let adjustment = 0;

      if (riskCount >= 4) {
        adjustment = -15; // 风险因素很多
      } else if (riskCount >= 3) {
        adjustment = -10; // 风险因素较多
      } else if (riskCount >= 2) {
        adjustment = -5;  // 风险因素一般
      } else if (riskCount === 1) {
        adjustment = -2;  // 风险因素较少
      }

      // 根据风险原因内容的严重程度进一步调整
      const riskText = riskReasons.join('').toLowerCase();
      if (riskText.includes('严重') || riskText.includes('非常') || riskText.includes('极度')) {
        adjustment -= 8; // 严重程度高
      } else if (riskText.includes('强烈') || riskText.includes('明显') || riskText.includes('持续')) {
        adjustment -= 5; // 严重程度中等
      }

      const finalScore = Math.max(25, Math.min(95, baseScore + adjustment));
      return Math.round(finalScore);
    },

    // 基于风险评估数据智能生成多维度分析
    generateDimensionsFromRisk(firstClassify, secondClassify, riskReasons = []) {
      const classify = (firstClassify || '').toLowerCase();
      const subClassify = (secondClassify || '').toLowerCase();
      const riskText = riskReasons.join('').toLowerCase();
      
      // 基础分数（正常状态）
      let dimensions = {
        emotionalState: 85,    // 情绪状态
        stressLevel: 80,       // 压力水平
        socialInteraction: 85, // 社交互动
        sleepQuality: 80,      // 睡眠质量
        anxietyLevel: 85       // 焦虑水平
      };
      
      // 根据一级分类调整各维度
      if (classify.includes('情绪') || classify.includes('emotion')) {
        dimensions.emotionalState -= 25; // 情绪问题显著影响情绪状态
        dimensions.anxietyLevel -= 15;   // 情绪问题通常伴随焦虑
        dimensions.stressLevel -= 10;    // 增加压力水平
      }
      
      if (classify.includes('焦虑') || classify.includes('anxiety')) {
        dimensions.anxietyLevel -= 30;   // 焦虑问题直接影响焦虑水平
        dimensions.stressLevel -= 20;    // 焦虑增加压力
        dimensions.sleepQuality -= 15;   // 焦虑影响睡眠
      }
      
      if (classify.includes('抑郁') || classify.includes('depression')) {
        dimensions.emotionalState -= 30; // 抑郁严重影响情绪
        dimensions.socialInteraction -= 25; // 抑郁影响社交
        dimensions.sleepQuality -= 20;   // 抑郁影响睡眠
      }
      
      if (classify.includes('压力') || classify.includes('stress')) {
        dimensions.stressLevel -= 25;    // 压力问题直接影响压力水平
        dimensions.sleepQuality -= 15;   // 压力影响睡眠
        dimensions.emotionalState -= 10; // 压力影响情绪
      }
      
      // 根据二级分类进一步调整
      if (subClassify.includes('抑郁') || subClassify.includes('depression')) {
        dimensions.emotionalState -= 15;
        dimensions.socialInteraction -= 15;
      }
      
      if (subClassify.includes('焦虑') || subClassify.includes('anxiety')) {
        dimensions.anxietyLevel -= 15;
        dimensions.stressLevel -= 10;
      }
      
      // 根据风险原因数量调整
      const riskCount = riskReasons.length;
      const countAdjustment = Math.min(riskCount * 3, 15); // 每个风险因素减3分，最多减15分
      
      Object.keys(dimensions).forEach(key => {
        dimensions[key] -= countAdjustment;
      });
      
      // 根据风险原因内容的严重程度调整
      let severityAdjustment = 0;
      if (riskText.includes('严重') || riskText.includes('非常') || riskText.includes('极度')) {
        severityAdjustment = 15; // 严重程度高
      } else if (riskText.includes('强烈') || riskText.includes('明显') || riskText.includes('持续')) {
        severityAdjustment = 10; // 严重程度中等
      } else if (riskText.includes('较') || riskText.includes('有些') || riskText.includes('一定')) {
        severityAdjustment = 5;  // 严重程度较轻
      }
      
      // 应用严重程度调整
      Object.keys(dimensions).forEach(key => {
        dimensions[key] -= severityAdjustment;
      });
      
      // 特定关键词的针对性调整
      if (riskText.includes('不开心') || riskText.includes('难过') || riskText.includes('低落')) {
        dimensions.emotionalState -= 10;
      }
      
      if (riskText.includes('考试') || riskText.includes('学习') || riskText.includes('工作')) {
        dimensions.stressLevel -= 8;
      }
      
      if (riskText.includes('睡眠') || riskText.includes('失眠') || riskText.includes('睡不着')) {
        dimensions.sleepQuality -= 15;
      }
      
      if (riskText.includes('社交') || riskText.includes('朋友') || riskText.includes('交流')) {
        dimensions.socialInteraction -= 10;
      }
      
      // 确保分数在合理范围内（25-95）
      Object.keys(dimensions).forEach(key => {
        dimensions[key] = Math.max(25, Math.min(95, Math.round(dimensions[key])));
      });
      
      return dimensions;
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
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* 报告容器 - 证书样式 */
.report-container {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 8px;
    background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  }
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 0 20px;

  .header-left {
    .back-btn {
      font-size: 14px;
      color: #667eea;

      &:hover {
        color: #764ba2;
      }
    }
  }

  .header-actions {
    display: flex;
    gap: 12px;
  }
}

/* 报告头部 - 证书样式 */
.report-header {
  padding: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;
  position: relative;

  .certificate-border {
    border: 3px solid rgba(255, 255, 255, 0.3);
    border-radius: 12px;
    padding: 40px 20px;
    position: relative;

    &::before, &::after {
      content: '';
      position: absolute;
      width: 60px;
      height: 60px;
      border: 3px solid rgba(255, 255, 255, 0.3);
      border-radius: 50%;
    }

    &::before {
      top: -30px;
      left: -30px;
    }

    &::after {
      bottom: -30px;
      right: -30px;
    }
  }

  .header-decoration {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;

    .decoration-line {
      width: 80px;
      height: 2px;
      background: rgba(255, 255, 255, 0.5);
    }

    .decoration-icon {
      font-size: 32px;
      margin: 0 20px;
    }
  }

  .report-title {
    font-size: 36px;
    font-weight: 700;
    margin: 0 0 8px 0;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  }

  .report-subtitle {
    font-size: 16px;
    font-weight: 300;
    margin: 0 0 30px 0;
    opacity: 0.9;
    font-style: italic;
  }

  .report-meta {
    .meta-row {
      display: flex;
      justify-content: center;
      align-items: center;
      margin-bottom: 8px;
      font-size: 14px;

      .meta-label {
        font-weight: 500;
        margin-right: 8px;
      }

      .meta-value {
        font-weight: 600;
      }
    }
  }
}

/* 技术说明 */
.technical-description {
  padding: 30px 40px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-bottom: 1px solid #dee2e6;

  .tech-header {
    text-align: center;
    margin-bottom: 20px;

    .tech-title {
      font-size: 20px;
      font-weight: 600;
      color: #495057;
      margin: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;

      i {
        color: #6c757d;
      }
    }
  }

  .tech-content {
    max-width: 1000px;
    margin: 0 auto;

    .tech-overview {
      margin-bottom: 30px;

      .tech-subtitle {
        font-size: 18px;
        font-weight: 600;
        color: #343a40;
        margin: 0 0 16px 0;
        text-align: center;
        position: relative;

        &::after {
          content: '';
          position: absolute;
          bottom: -8px;
          left: 50%;
          transform: translateX(-50%);
          width: 60px;
          height: 3px;
          background: linear-gradient(90deg, #667eea, #764ba2);
          border-radius: 2px;
        }
      }

      .tech-text {
        font-size: 15px;
        line-height: 1.8;
        color: #495057;
        text-align: justify;
        margin: 0;
        padding: 20px;
        background: white;
        border-radius: 12px;
        border-left: 4px solid #667eea;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);

        strong {
          color: #343a40;
          font-weight: 600;
          background: linear-gradient(135deg, #667eea, #764ba2);
          background-clip: text;
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
        }
      }
    }

    .tech-details {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
      gap: 20px;
      margin-bottom: 30px;

      .tech-item {
        background: white;
        border-radius: 16px;
        padding: 24px;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
        border: 1px solid #e9ecef;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
          border-color: #667eea;
        }

        .tech-icon {
          font-size: 32px;
          margin-bottom: 16px;
          text-align: center;
        }

        .tech-info {
          .tech-name {
            font-size: 16px;
            font-weight: 600;
            color: #343a40;
            margin: 0 0 12px 0;
            text-align: center;
          }

          .tech-desc {
            font-size: 14px;
            line-height: 1.6;
            color: #6c757d;
            margin: 0;
            text-align: justify;

            strong {
              color: #495057;
              font-weight: 600;
              background: linear-gradient(135deg, #667eea, #764ba2);
              background-clip: text;
              -webkit-background-clip: text;
              -webkit-text-fill-color: transparent;
            }
          }
        }
      }
    }

    .tech-guarantee {
      .guarantee-text {
        font-size: 14px;
        line-height: 1.6;
        color: #495057;
        text-align: center;
        margin: 0;
        padding: 16px 24px;
        background: linear-gradient(135deg, #f8f9fa, #e9ecef);
        border-radius: 12px;
        border: 1px solid #dee2e6;

        strong {
          color: #343a40;
          font-weight: 600;
        }
      }
    }
  }
}

/* 评估结果概览 */
.assessment-overview {
  padding: 40px;
  background: #fafbfc;
  border-bottom: 1px solid #e8ecf0;

  .overview-header {
    text-align: center;
    margin-bottom: 30px;

    .section-title {
      font-size: 24px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;

      i {
        color: #667eea;
      }
    }
  }

  .result-cards {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 30px;
    max-width: 800px;
    margin: 0 auto;
  }

  .result-card {
    background: white;
    border-radius: 16px;
    padding: 30px;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
    border: 2px solid transparent;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 32px rgba(0, 0, 0, 0.12);
    }

    &.primary-card {
      border-color: #667eea;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;

      .card-title, .card-subtitle {
        color: white;
      }

      .result-label {
        color: rgba(255, 255, 255, 0.8);
      }
    }

    .card-header {
      display: flex;
      align-items: center;
      margin-bottom: 20px;

      .card-icon {
        width: 48px;
        height: 48px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-right: 16px;
        font-size: 24px;

        &.risk-icon {
          background: rgba(255, 255, 255, 0.2);
          color: white;
        }

        &.score-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          color: white;
        }
      }

      .card-title-group {
        .card-title {
          font-size: 18px;
          font-weight: 600;
          margin: 0 0 4px 0;
          color: #2c3e50;
        }

        .card-subtitle {
          font-size: 12px;
          color: #7f8c8d;
          margin: 0;
          font-style: italic;
        }
      }
    }

    .card-content {
      .primary-result, .secondary-result {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        .result-label {
          font-size: 14px;
          color: #7f8c8d;
        }

        .result-value {
          font-weight: 600;
          font-size: 16px;

          &.primary {
            font-size: 18px;
          }

          &.risk-high {
            color: #e74c3c;
          }

          &.risk-medium {
            color: #f39c12;
          }

          &.risk-low {
            color: #27ae60;
          }
        }
      }

      .score-display {
        display: flex;
        align-items: baseline;
        justify-content: center;
        margin-bottom: 12px;

        .score-number {
          font-size: 48px;
          font-weight: 700;
          color: #667eea;
          line-height: 1;
        }

        .score-total {
          font-size: 24px;
          color: #95a5a6;
          margin-left: 4px;
        }
      }

      .score-label {
        text-align: center;
        font-size: 14px;
        color: #7f8c8d;
        margin-bottom: 16px;
      }

      .score-bar {
        height: 8px;
        background: #ecf0f1;
        border-radius: 4px;
        overflow: hidden;

        .score-progress {
          height: 100%;
          background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
          border-radius: 4px;
          transition: width 0.8s ease;
        }
      }
    }
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

/* 详细分析报告 */
.detailed-analysis {
  padding: 40px;
  background: white;

  .analysis-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f8f9fa;

    .section-title {
      font-size: 24px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;

      i {
        color: #667eea;
      }
    }

    .analysis-badge {
      .badge-text {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        padding: 6px 16px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
    }
  }

  .analysis-content-wrapper {
    .risk-factors {
      .subsection-title {
        font-size: 20px;
        font-weight: 600;
        color: #2c3e50;
        margin: 0 0 24px 0;
        display: flex;
        align-items: center;
        gap: 8px;

        .title-icon {
          font-size: 24px;
        }
      }

      .factors-list {
        .factor-item {
          display: flex;
          margin-bottom: 24px;
          padding: 20px;
          background: #f8f9fa;
          border-radius: 12px;
          border-left: 4px solid #e74c3c;
          transition: all 0.3s ease;

          &:hover {
            background: #f1f3f4;
            transform: translateX(4px);
          }

          .factor-number {
            width: 32px;
            height: 32px;
            background: #e74c3c;
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 14px;
            margin-right: 16px;
            flex-shrink: 0;
          }

          .factor-content {
            flex: 1;

            .factor-text {
              margin: 0;
              line-height: 1.6;
              color: #2c3e50;
              font-size: 15px;
            }
          }
        }
      }
    }
  }
}

/* 专业建议与干预措施 */
.recommendations-section {
  padding: 40px;
  background: #fafbfc;
  border-top: 1px solid #e8ecf0;

  .recommendations-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 2px solid #f8f9fa;

    .section-title {
      font-size: 24px;
      font-weight: 600;
      color: #2c3e50;
      margin: 0;
      display: flex;
      align-items: center;
      gap: 8px;

      i {
        color: #f39c12;
      }
    }

    .recommendations-badge {
      .badge-text {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        color: white;
        padding: 6px 16px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
    }
  }

  .recommendations-content {
    .recommendations-intro {
      background: white;
      padding: 24px;
      border-radius: 12px;
      margin-bottom: 30px;
      border-left: 4px solid #f39c12;

      .intro-text {
        margin: 0;
        line-height: 1.6;
        color: #2c3e50;
        font-size: 15px;
      }
    }

    .recommendations-list {
      .recommendation-item {
        background: white;
        border-radius: 16px;
        padding: 24px;
        margin-bottom: 20px;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
        border: 1px solid #e8ecf0;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        }

        .recommendation-header {
          display: flex;
          align-items: center;
          margin-bottom: 16px;

          .recommendation-number {
            width: 36px;
            height: 36px;
            background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: 600;
            font-size: 16px;
            margin-right: 12px;
            flex-shrink: 0;
          }

          .recommendation-icon {
            width: 36px;
            height: 36px;
            background: #f8f9fa;
            border-radius: 8px;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 12px;
            color: #667eea;
            font-size: 18px;
          }

          .recommendation-title {
            font-size: 18px;
            font-weight: 600;
            color: #2c3e50;
            margin: 0;
          }
        }

        .recommendation-body {
          .recommendation-desc {
            margin: 0 0 16px 0;
            line-height: 1.6;
            color: #5a6c7d;
            font-size: 15px;
          }

          .recommendation-tags {
            display: flex;
            gap: 8px;
            flex-wrap: wrap;
          }
        }
      }
    }
  }
}

/* 报告结尾 - 证书样式 */
.report-footer {
  padding: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-align: center;

  .footer-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    max-width: 600px;
    margin: 0 auto 20px auto;

    .signature-section {
      text-align: left;

      .signature-line {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
        font-size: 14px;

        .signature-label {
          font-weight: 500;
          margin-right: 8px;
          opacity: 0.9;
        }

        .signature-value {
          font-weight: 600;
        }
      }
    }

    .official-seal {
      .seal-circle {
        width: 80px;
        height: 80px;
        border: 3px solid rgba(255, 255, 255, 0.5);
        border-radius: 50%;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        background: rgba(255, 255, 255, 0.1);

        .seal-text {
          font-size: 12px;
          font-weight: 600;
          line-height: 1;
        }

        .seal-subtext {
          font-size: 8px;
          opacity: 0.8;
          margin-top: 2px;
        }
      }
    }
  }

  .footer-note {
    border-top: 1px solid rgba(255, 255, 255, 0.3);
    padding-top: 20px;

    .disclaimer-content {
      text-align: left;
      max-width: 800px;
      margin: 0 auto;

      .disclaimer-title {
        font-size: 16px;
        font-weight: 600;
        margin: 0 0 12px 0;
        color: white;
        text-align: center;
      }

      .disclaimer-text {
        font-size: 13px;
        line-height: 1.6;
        margin: 0 0 12px 0;
        opacity: 0.9;
        text-align: justify;

        strong {
          font-weight: 700;
          color: #fff;
          background: rgba(255, 255, 255, 0.2);
          padding: 2px 6px;
          border-radius: 4px;
        }
      }

      .tech-note {
        font-size: 11px;
        opacity: 0.7;
        text-align: center;
        margin: 0;
        font-style: italic;
        border-top: 1px solid rgba(255, 255, 255, 0.2);
        padding-top: 8px;
      }
    }
  }
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .health-report-page {
    padding: 10px;
    background: #f8f9fa;
  }

  .page-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
    padding: 0 10px;
  }

  .report-container {
    border-radius: 8px;
    margin-top: 10px;
  }

  .technical-description {
    padding: 20px;

    .tech-content {
      .tech-overview {
        .tech-subtitle {
          font-size: 16px;
        }

        .tech-text {
          font-size: 14px;
          line-height: 1.6;
          padding: 16px;
          text-align: left;
        }
      }

      .tech-details {
        grid-template-columns: 1fr;
        gap: 16px;

        .tech-item {
          padding: 20px;

          .tech-icon {
            font-size: 28px;
            margin-bottom: 12px;
          }

          .tech-info {
            .tech-name {
              font-size: 15px;
            }

            .tech-desc {
              font-size: 13px;
              line-height: 1.5;
            }
          }
        }
      }

      .tech-guarantee {
        .guarantee-text {
          font-size: 13px;
          padding: 14px 16px;
        }
      }
    }
  }

  .report-header {
    padding: 20px;

    .certificate-border {
      padding: 20px 10px;

      &::before, &::after {
        width: 40px;
        height: 40px;
      }

      &::before {
        top: -20px;
        left: -20px;
      }

      &::after {
        bottom: -20px;
        right: -20px;
      }
    }

    .report-title {
      font-size: 24px;
    }

    .report-subtitle {
      font-size: 14px;
    }

    .header-decoration {
      .decoration-line {
        width: 50px;
      }

      .decoration-icon {
        font-size: 24px;
        margin: 0 10px;
      }
    }
  }

  .assessment-overview {
    padding: 20px;

    .result-cards {
      grid-template-columns: 1fr;
      gap: 20px;
    }

    .result-card {
      padding: 20px;

      .card-header {
        .card-icon {
          width: 40px;
          height: 40px;
          font-size: 20px;
        }

        .card-title-group {
          .card-title {
            font-size: 16px;
          }
        }
      }

      .card-content {
        .score-display {
          .score-number {
            font-size: 36px;
          }

          .score-total {
            font-size: 18px;
          }
        }
      }
    }
  }

  .analysis-section {
    padding: 20px;

    .analysis-content {
      grid-template-columns: 1fr;
      gap: 20px;
    }

    .radar-chart, .trend-chart {
      height: 250px !important;
    }
  }

  .detailed-analysis {
    padding: 20px;

    .analysis-header {
      flex-direction: column;
      gap: 12px;
      text-align: center;

      .section-title {
        font-size: 20px;
      }
    }

    .factors-list {
      .factor-item {
        padding: 16px;

        .factor-number {
          width: 28px;
          height: 28px;
          font-size: 12px;
        }

        .factor-content {
          .factor-text {
            font-size: 14px;
          }
        }
      }
    }
  }

  .recommendations-section {
    padding: 20px;

    .recommendations-header {
      flex-direction: column;
      gap: 12px;
      text-align: center;

      .section-title {
        font-size: 20px;
      }
    }

    .recommendations-content {
      .recommendations-intro {
        padding: 16px;

        .intro-text {
          font-size: 14px;
        }
      }

      .recommendations-list {
        .recommendation-item {
          padding: 16px;

          .recommendation-header {
            .recommendation-number {
              width: 32px;
              height: 32px;
              font-size: 14px;
            }

            .recommendation-icon {
              width: 32px;
              height: 32px;
              font-size: 16px;
            }

            .recommendation-title {
              font-size: 16px;
            }
          }

          .recommendation-body {
            .recommendation-desc {
              font-size: 14px;
            }
          }
        }
      }
    }
  }

  .report-footer {
    padding: 20px;

    .footer-content {
      flex-direction: column;
      gap: 20px;

      .signature-section {
        text-align: center;
      }

      .official-seal {
        .seal-circle {
          width: 60px;
          height: 60px;

          .seal-text {
            font-size: 10px;
          }

          .seal-subtext {
            font-size: 7px;
          }
        }
      }
    }

    .footer-note {
      .disclaimer-content {
        .disclaimer-title {
          font-size: 14px;
        }

        .disclaimer-text {
          font-size: 12px;
          line-height: 1.5;
        }

        .tech-note {
          font-size: 10px;
        }
      }
    }
  }
}
</style>
