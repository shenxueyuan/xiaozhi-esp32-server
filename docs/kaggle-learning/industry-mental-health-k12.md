# 行业专题：青少年心理健康 × K12 教育 — AI 应用全景

> 围绕你的发展方向，梳理两个领域中**可用 AI/ML 解决的行业问题**
> 每个问题：行业背景 → 开源项目 → 数据集 → 技术路线 → 涉及知识点

---

## 目录

**第一部分：青少年心理健康（M1-M10）**
- [M1: 抑郁/焦虑文本检测](#m1-社交媒体抑郁焦虑文本检测) | [M2: 自杀风险预警](#m2-自杀自伤风险文本预警)
- [M3: 情绪日记分析](#m3-情绪日记智能分析) | [M4: 语音情绪识别](#m4-语音情绪识别)
- [M5: 表情情绪识别](#m5-面部表情情绪识别) | [M6: 多模态情绪分析](#m6-多模态情绪分析)
- [M7: 心理对话机器人](#m7-心理健康对话机器人) | [M8: 校园霸凌检测](#m8-校园霸凌文本检测)
- [M9: 睡眠与心理关联](#m9-睡眠质量与心理健康关联分析) | [M10: 智能心理测评](#m10-心理测评智能化)

**第二部分：K12 教育（E1-E10）**
- [E1: 试卷 OCR](#e1-试卷作业-ocr-识别) | [E2: 数学公式识别](#e2-手写数学公式识别)
- [E3: 作文自动评分](#e3-作文自动评分) | [E4: 答题卡批改](#e4-选择题答题卡自动批改)
- [E5: 知识追踪](#e5-知识追踪与学情分析) | [E6: 智能出题](#e6-智能出题与自动组卷)
- [E7: 课堂专注度](#e7-课堂专注度检测) | [E8: 个性化学习推荐](#e8-个性化学习路径推荐)
- [E9: 试卷区域检测](#e9-试卷区域检测与矫正) | [E10: 错题本生成](#e10-错题本自动生成)

**交叉领域**
- [X1: 学生心理状态预测](#x1-学生心理状态早期预测) | [X2: 网络行为风险监测](#x2-青少年网络行为风险监测)

---

# 第一部分：青少年心理健康

> 中国青少年抑郁检出率约 24.6%（2022中科院）。AI 可辅助**筛查、监测、干预**。

---

## M1: 社交媒体抑郁/焦虑文本检测

**行业问题**：青少年在社交媒体的发言可能隐含抑郁/焦虑倾向，心理老师无法逐条监控。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| depression-detection | [rafalposwiata/depression-detection-lt-edi-2022](https://github.com/rafalposwiata/depression-detection-lt-edi-2022) | ACL2022 竞赛冠军 |
| Mental Health NLP | [indranil143/Mental-Health-Sentiment-Analysis-using-Deep-Learning](https://github.com/indranil143/Mental-Health-Sentiment-Analysis-using-Deep-Learning) | RoBERTa 微调 |
| 数据集汇总 | [bucuram/depression-datasets-nlp](https://github.com/bucuram/depression-datasets-nlp) | NLP抑郁检测数据集大全 |
| Kaggle 数据集 | [Mental Health Corpus](https://www.kaggle.com/datasets/reihanenamdari/mental-health-corpus) | 标注心理健康文本 |
| Reddit 抑郁帖 | [Depression Reddit](https://www.kaggle.com/datasets/infamouscoder/depression-reddit-cleaned) | Reddit 数据 |

**技术路线**：
```
方案A（入门）：jieba分词 → TF-IDF + 情感词典 → XGBoost 分类
  涉及：阶段二（Pandas）+ 阶段三（分类）+ NLP基础

方案B（进阶）：bert-base-chinese 微调 → 多分类（正常/轻度/中度/重度）
  涉及：阶段六（Transformer微调）
  关键：阈值偏向高 Recall（宁可误报不可漏报）
```

---

## M2: 自杀/自伤风险文本预警

**行业问题**：最紧急的心理健康场景，需从文本识别自杀/自伤意图并即时干预。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| suicidal-text-detection | [gohjiayi/suicidal-text-detection](https://github.com/gohjiayi/suicidal-text-detection) | 自杀意图检测+聊天机器人 |
| Suicide Watch Dataset | [Kaggle Suicide Watch](https://www.kaggle.com/datasets/nikhileswarkomati/suicide-watch) | Reddit 数据集 |

**技术路线**：
```
三层防线架构：
├── 第1层：关键词匹配（"不想活""跳楼""遗书"）→ 立即标记
├── 第2层：BERT 二分类（阈值极低，>0.3 就标记）
├── 第3层：心理老师人工复核 → 触发干预
⚠️ Recall 必须 >95%，漏掉一个后果不堪设想
⚠️ 伦理要求：数据脱敏、隐私保护、AI只辅助不替代专业判断
```

---

## M3: 情绪日记智能分析

**行业问题**：心理老师让学生写情绪日记，面对几百份无法逐一细看。需要自动分析情绪趋势、标出异常。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| GoEmotions | [google-research/goemotions](https://github.com/google-research/goemotions) | 27种细粒度情绪分类 |
| SoulChat 数据 | [scutcyr/SoulChat](https://github.com/scutcyr/SoulChat) | 中文心理情感对话 |

**技术路线**：
```
├── BERT 情绪分类：高兴/平静/焦虑/悲伤/愤怒/恐惧
├── 每日情绪打分（-1到+1）→ 按天绘制情绪曲线
├── 异常检测：连续3天 < -0.5 → 孤立森林 / 移动平均标准差 → 预警
├── 班级热力图：全班情绪分布可视化
涉及：NLP分类 + 时序异常检测 + 可视化
```

---

## M4: 语音情绪识别

**行业问题**：心理咨询中，语速变慢、声调低沉、停顿增多都是抑郁信号。AI 可从语音提取这些特征。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| SpeechBrain | [speechbrain/speechbrain](https://github.com/speechbrain/speechbrain) ⭐9k+ | 语音处理全家桶 |
| Multimodal Emotion | [maelfabien/Multimodal-Emotion-Recognition](https://github.com/maelfabien/Multimodal-Emotion-Recognition) | 多模态情绪识别 |
| librosa | [librosa/librosa](https://github.com/librosa/librosa) ⭐7k+ | 音频分析库 |
| opensmile | [audeering/opensmile](https://github.com/audeering/opensmile) ⭐1k+ | 语音特征提取 |
| RAVDESS | [Kaggle RAVDESS](https://www.kaggle.com/datasets/uwrfkaggler/ravdess-emotional-speech-audio) | 情绪语音数据集 |

**技术路线**：
```
方案A：librosa 提取 MFCC/基频/能量/语速 → SVM/XGBoost（60-70%）
方案B：语音 → Mel频谱图（当成图片）→ CNN 分类（70-80%）
方案C：SpeechBrain 预训练模型微调
涉及：阶段三（分类）或 阶段六（CNN）+ 音频处理
```

---

## M5: 面部表情情绪识别

**行业问题**：通过摄像头实时识别面部表情，辅助心理评估和课堂观察。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| DeepFace | [serengil/deepface](https://github.com/serengil/deepface) ⭐15k+ | 人脸分析全家桶 |
| FER | [justinshenk/fer](https://github.com/justinshenk/fer) ⭐1k+ | 表情识别库 |
| MediaPipe | [google/mediapipe](https://github.com/google-ai-edge/mediapipe) ⭐28k+ | 人脸/手部/姿态检测 |
| FER2013 | [Kaggle FER2013](https://www.kaggle.com/datasets/msambare/fer2013) | 35k张表情图片 |

**技术路线**：
```
方案A（开箱即用）：DeepFace.analyze(img, actions=['emotion']) 一行搞定
方案B（自训练）：FER2013 + EfficientNet/ResNet 迁移学习 → 7类分类
方案C（实时）：MediaPipe人脸检测 → 裁剪 → CNN分类 → 时间窗口情绪统计
涉及：阶段六（CNN + 图像分类 + 迁移学习）
```

---

## M6: 多模态情绪分析

**行业问题**：单一模态准确率有限，结合文字+语音+表情的多模态分析显著提升效果。

| 开源项目 | 链接 | 说明 |
|---------|------|------|
| Multimodal-Emotion | [maelfabien/Multimodal-Emotion-Recognition](https://github.com/maelfabien/Multimodal-Emotion-Recognition) | 文本+语音+视频 |
| MELD | [declare-lab/MELD](https://github.com/declare-lab/MELD) ⭐700+ | 多模态情绪对话数据集 |
| CMU-MultimodalSDK | [A2Zadeh/CMU-MultimodalSDK](https://github.com/A2Zadeh/CMU-MultimodalSDK) | 多模态分析SDK |

**技术路线**：
```
文本→BERT→768维 | 语音→CNN→256维 | 人脸→ResNet→512维
  └─→ 融合策略：早期拼接 / Attention加权 / 晚期投票
涉及：阶段六全部 + 多模态融合（进阶方向）
```

---

## M7: 心理健康对话机器人

**行业问题**：心理咨询师严重不足（中国每10万人仅3-4名）。AI 可 24 小时提供初步情绪支持。

| 开源项目 | 链接 | 说明 |
|---------|------|------|
| **SoulChat 灵心** | [scutcyr/SoulChat](https://github.com/scutcyr/SoulChat) ⭐2k+ | 中文心理健康大模型 |
| **MeChat 心理猫** | [qiuhuachuan/smile](https://github.com/qiuhuachuan/smile) ⭐1k+ | 中文心理支持对话 |
| CPsyCoun | [CAS-SIAT-XinHai/CPsyCoun](https://github.com/CAS-SIAT-XinHai/CPsyCoun) | 中科院心理咨询对话数据 |
| ChatGLM3 | [THUDM/ChatGLM3](https://github.com/THUDM/ChatGLM3) ⭐13k+ | 基座模型 |

**技术路线**：
```
方案A（快速）：ChatGLM API + 心理咨询 system prompt → 1周出demo
方案B（专用）：ChatGLM3-6B + SoulChat数据 → LoRA微调（8GB显存可跑）
⚠️ 安全机制必须有：危机关键词→引导12355热线，不给诊断，提醒就医
```

---

## M8: 校园霸凌文本检测

**行业问题**：网络霸凌是青少年心理问题重要诱因，需自动检测班级群聊/论坛中的霸凌行为。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| Toxic Comment | [Kaggle Toxic Comment](https://www.kaggle.com/c/jigsaw-toxic-comment-classification-challenge) | 有毒评论分类竞赛（经典） |
| HateXplain | [hate-alert/HateXplain](https://github.com/hate-alert/HateXplain) ⭐300+ | 可解释仇恨言论检测 |
| Cyberbullying | [Kaggle Cyberbullying](https://www.kaggle.com/datasets/andrewmvd/cyberbullying-classification) | 网络霸凌分类数据集 |

**技术路线**：
```
BERT 多标签分类（一条消息可能同时是嘲讽+人身攻击）
上下文建模：拼接前后3条消息作为输入
难点：隐晦讽刺（"你真是个好人呢"→嘲讽）
涉及：阶段六（BERT微调 + 多标签分类）
```

---

## M9: 睡眠质量与心理健康关联分析

**行业问题**：睡眠是心理健康重要指标，通过手环/手机数据分析睡眠模式可早期发现问题。

| 数据集 | 链接 | 说明 |
|-------|------|------|
| Sleep Health | [Kaggle Sleep Health](https://www.kaggle.com/datasets/uom190346a/sleep-health-and-lifestyle-dataset) | 睡眠+生活方式 |
| MESA Sleep | [sleepdata.org](https://sleepdata.org/) | 大规模睡眠研究数据 |

**技术路线**：
```
特征：睡眠时长/入睡时间标准差/深夜手机使用/运动量/周末作息差异
模型：XGBoost综合评分 + 孤立森林异常检测 + LSTM趋势预测
涉及：阶段三（特征工程/异常检测）+ 时序分析
```

---

## M10: 心理测评智能化

**行业问题**：传统心理问卷题量大、学生敷衍填写。自适应测评可用更少的题获得更准结果。

| 开源项目 | 链接 | 说明 |
|---------|------|------|
| catsim | [douglasrizzo/catsim](https://github.com/douglasrizzo/catsim) ⭐200+ | 计算机自适应测试 |
| py-irt | [nd-ball/py-irt](https://github.com/nd-ball/py-irt) | 项目反应理论 |

**技术路线**：
```
项目反应理论（IRT）：每题有"难度""区分度"参数
根据前面回答动态选最优下一题 → 只需原始30-50%题量
涉及：概率统计（阶段一）+ 优化算法
```

---

# 第二部分：K12 教育

> "双减"后学校主阵地作用增强，AI辅助教学需求爆发。核心场景：教、学、练、测、评。

---

## E1: 试卷/作业 OCR 识别

**行业问题**：纸质作业数字化是所有智能批改、错题整理、学情分析的基础。

| 开源项目 | 链接 | 说明 |
|---------|------|------|
| **PaddleOCR** | [PaddlePaddle/PaddleOCR](https://github.com/PaddlePaddle/PaddleOCR) ⭐45k+ | 最强中文OCR |
| EasyOCR | [JaidedAI/EasyOCR](https://github.com/JaidedAI/EasyOCR) ⭐25k+ | 轻量OCR |
| Tesseract | [tesseract-ocr/tesseract](https://github.com/tesseract-ocr/tesseract) ⭐63k+ | 经典OCR |
| DocTR | [mindee/doctr](https://github.com/mindee/doctr) ⭐4k+ | 文档识别一体化 |
| RapidOCR | [RapidAI/RapidOCR](https://github.com/RapidAI/RapidOCR) ⭐4k+ | PaddleOCR轻量部署 |

**技术路线**：
```
Pipeline：图片预处理 → 文本检测(DBNet) → 文本识别(CRNN+CTC) → 结构化输出
印刷体：PaddleOCR开箱即用，95%+准确率
手写体：需微调，准确率依赖数据量
涉及：阶段六（CNN + 序列模型）
```

```python
from paddleocr import PaddleOCR
ocr = PaddleOCR(use_angle_cls=True, lang='ch')
result = ocr.ocr('exam_paper.jpg', cls=True)
for line in result[0]:
    print(f"[{line[1][1]:.2f}] {line[1][0]}")
```

---

## E2: 手写数学公式识别

**行业问题**：数学试卷中的手写公式（分数、根号、积分）普通OCR无法识别。

| 开源项目 | 链接 | 说明 |
|---------|------|------|
| **LaTeX-OCR** | [lukas-blecher/LaTeX-OCR](https://github.com/lukas-blecher/LaTeX-OCR) ⭐4k+ | 图片→LaTeX |
| **Pix2Text** | [breezedeus/Pix2Text](https://github.com/breezedeus/Pix2Text) ⭐2k+ | 中文+公式混合识别 |
| HME100K | [TAL-EE/HME100K](https://github.com/TAL-EE/HME100K) | 10万张手写数学表达式 |

**技术路线**：
```
架构：CNN编码器 + Transformer解码器 → 自回归生成LaTeX token
推荐：直接用 Pix2Text (pip install pix2text)，可在此基础上微调
涉及：阶段六（Image-to-Sequence）
```

---

## E3: 作文自动评分

**行业问题**：语文老师批改一篇作文5-10分钟，一个班50篇需4-8小时。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| L2C-rater | [iris2hu/L2C-rater](https://github.com/iris2hu/L2C-rater) | 中文作文评分 |
| Kaggle ASAP | [Kaggle AES](https://www.kaggle.com/c/asap-aes) | 英文作文评分竞赛 |
| Kaggle AES 2.0 | [Kaggle AES2](https://www.kaggle.com/competitions/learning-agency-lab-automated-essay-scoring-2) | 2024最新竞赛 |

**技术路线**：
```
方案A：手工特征（字数/词汇丰富度/复杂句占比/错别字率）→ XGBoost回归
方案B：BERT + 回归头 → 多任务同时预测总分+各维度
方案C：LLM + 评分rubric prompt → 给出详细修改建议
评估指标：QWK（Quadratic Weighted Kappa）
涉及：阶段三（特征工程+回归）或 阶段六（BERT）
```

---

## E4: 选择题答题卡自动批改

**行业问题**：纸质答题卡（涂卡）自动识别。

| 开源项目 | 链接 | 说明 |
|---------|------|------|
| **OMRChecker** | [Udayraj123/OMRChecker](https://github.com/Udayraj123/OMRChecker) ⭐1k+ | 通用答题卡阅卷 |
| MCQ Grading | [SihabSahariar/Automated-MCQ-OCR-Based-Grading-System](https://github.com/SihabSahariar/Automated-MCQ-OCR-Based-Grading-System) | 选择题批改 |

**技术路线**：
```
纯 OpenCV 方案（无需深度学习！）：
灰度化 → 二值化 → 轮廓检测 → 透视校正 → 定位选项区域 → 像素统计判断涂写
这是很好的 CV 入门项目，涉及：OpenCV 图像处理基础
```

---

## E5: 知识追踪与学情分析

**行业问题**："对了70%"太粗糙，需要精确到每个知识点的掌握程度。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| **pyKT** | [pykt-toolkit/pykt-toolkit](https://github.com/pykt-toolkit/pykt-toolkit) ⭐400+ | 知识追踪算法库 |
| awesome-ai4education | [GeminiLight/awesome-ai-llm4education](https://github.com/GeminiLight/awesome-ai-llm4education) | AI教育论文集 |
| EdNet | [riiid/ednet](https://github.com/riiid/ednet) ⭐300+ | 1.3亿条做题记录 |
| ASSISTments | [ASSISTments Data](https://sites.google.com/site/assistmentsdata/) | 真实学生做题数据 |

**技术路线**：
```
输入：学生做题序列 [(题目,知识点,对/错), ...]
输出：每个知识点的掌握概率

经典模型：
├── BKT（贝叶斯知识追踪）→ 入门
├── DKT（深度知识追踪，LSTM）→ 进阶
├── AKT（注意力知识追踪）→ SOTA
├── 用 pyKT 库可快速跑通所有模型
涉及：阶段三（分类/序列）+ 阶段六（LSTM/Transformer）
```

---

## E6: 智能出题与自动组卷

**行业问题**：老师出一套试卷需要数小时，要兼顾知识点覆盖、难度梯度、题型比例。

| 开源项目 | 链接 | 说明 |
|---------|------|------|
| question_generator | [AMontgomerie/question_generator](https://github.com/AMontgomerie/question_generator) | NLP自动出题 |
| quiz.ai | [geekquad/quiz.ai](https://github.com/geekquad/quiz.ai) | PDF→自动生成题目 |

**技术路线**：
```
方案A（规则+优化）：题库 + 知识点标签 → 整数规划自动组卷
  约束：知识点覆盖率、难度分布、总分
  工具：scipy.optimize / PuLP

方案B（LLM生成）：
  给LLM一段知识点文本 → 生成选择题/填空题/简答题
  用 few-shot prompt 控制题目格式和难度
  需人工审核质量
涉及：优化算法 + NLP/LLM
```

---

## E7: 课堂专注度检测

**行业问题**：老师无法实时关注每个学生的课堂状态，需要AI辅助识别走神、打瞌睡、玩手机等行为。

| 开源项目/数据集 | 链接 | 说明 |
|----------------|------|------|
| attention-monitor | [yptheangel/attention-monitor](https://github.com/yptheangel/attention-monitor) | 课堂注意力监控 |
| SCB-dataset | [Whiffe/SCB-dataset](https://github.com/Whiffe/SCB-dataset) | 学生课堂行为数据集 |
| Engagement Recognition | [omidmnezami/Engagement-Recognition](https://github.com/omidmnezami/Engagement-Recognition) | 学生参与度识别 |
| **mmpose** | [open-mmlab/mmpose](https://github.com/open-mmlab/mmpose) ⭐6k+ | 姿态估计工具箱 |
| **YOLOv8** | [ultralytics/ultralytics](https://github.com/ultralytics/ultralytics) ⭐35k+ | 目标检测 |

**技术路线**：
```
Pipeline：
├── YOLOv8 检测人体/人脸
├── mmpose 估计头部姿态（低头→可能在玩手机/睡觉）
├── 表情识别（闭眼→打瞌睡）
├── 行为分类：专注/走神/睡觉/玩手机/讲话
├── 按时间窗口统计 → 输出专注度曲线
涉及：阶段六（目标检测 + 姿态估计 + 分类）
⚠️ 伦理：需告知学生，不可偷拍，数据不可滥用
```

---

## E8: 个性化学习路径推荐

**行业问题**：每个学生薄弱点不同，需要个性化的学习资源和练习推荐。

| 开源项目 | 链接 | 说明 |
|---------|------|------|
| **OATutor** | [CAHLR/OATutor](https://github.com/CAHLR/OATutor) ⭐ | 开源自适应辅导系统 |
| AI4ED | [nikbearbrown/AI4ED](https://github.com/nikbearbrown/AI4ED) | AI教育项目集 |
| LeetNode | [zhermin/LeetNode](https://github.com/zhermin/LeetNode) | 自适应学习软件 |

**技术路线**：
```
├── 知识图谱：构建学科知识点依赖关系（加法→乘法→除法）
├── 学生画像：基于知识追踪（E5）得到每个知识点掌握度
├── 推荐算法：
│   ├── 基于规则：掌握度 < 60% 的知识点优先推荐
│   ├── 协同过滤：相似学生做过的有效练习
│   └── 强化学习：动态调整推荐策略（进阶）
涉及：推荐系统 + 知识图谱 + 阶段三/五
```

---

## E9: 试卷区域检测与矫正

**行业问题**：教育App用户拍照上传试卷，需要自动定位并裁剪试卷内容区域。

**（已在 [case-based-learning.md 案例7](./case-based-learning.md) 中详细讲解）**

| 开源项目 | 链接 |
|---------|------|
| DocTR | [mindee/doctr](https://github.com/mindee/doctr) ⭐4k+ |
| LabelMe | [labelmeai/labelme](https://github.com/labelmeai/labelme) ⭐13k+ |
| SmartDoc | [jchazalon/smartdoc15-ch1-dataset](https://github.com/jchazalon/smartdoc15-ch1-dataset) |
| timm | [huggingface/pytorch-image-models](https://github.com/huggingface/pytorch-image-models) ⭐33k+ |

---

## E10: 错题本自动生成

**行业问题**：学生手动整理错题本费时费力，需要自动从试卷照片中提取错题并归类。

**技术路线**：
```
这是一个多模型串联的综合项目：
├── Step1：试卷区域检测（E9）→ 裁剪试卷
├── Step2：版面分析（检测每道题的区域）→ 用 YOLOv8 或 PaddleOCR 版面分析
├── Step3：OCR 识别题目文字（E1）
├── Step4：识别批改标记（✓/✗/分数）→ 图像分类
├── Step5：错题提取 → 按知识点归类
├── Step6：生成错题本 PDF
涉及：CV（检测+分类+OCR）+ NLP（知识点匹配）= 综合实战项目
```

---

# 交叉领域

## X1: 学生心理状态早期预测

**行业问题**：不依赖问卷，从学业数据+行为数据中发现心理异常的早期信号。

**数据特征**：
```
学业数据：成绩突然下滑、作业完成率降低、缺勤增多
行为数据：图书馆使用减少、食堂消费减少（不吃饭）、晚归增多
社交数据：社团活动减少、朋友圈发布减少
```

**技术路线**：
```
├── 多源数据融合：学业系统 + 一卡通 + 门禁 + 网络行为
├── 特征工程：构建变化趋势特征（本周vs上周、本月vs上月）
├── 模型：XGBoost 分类 → 输出风险等级
├── SHAP 可解释：告诉老师"为什么系统认为这个学生有风险"
├── 关键：纵向对比（和自己比），而非横向对比（和别人比）
涉及：阶段三（特征工程+分类）+ 阶段四（SHAP可解释性）
```

---

## X2: 青少年网络行为风险监测

**行业问题**：监测学生上网行为中的风险信号（搜索自杀方法、浏览不良内容、深夜游戏成瘾）。

**技术路线**：
```
├── URL/关键词分类：风险网站识别（文本分类）
├── 时间模式分析：深夜使用时长异常检测
├── 搜索意图分析：NLP 理解搜索查询的真实意图
├── 隐私平衡：只检测风险信号，不监控具体内容
⚠️ 这个领域争议大，必须在"保护"和"隐私"之间找平衡
```

---

# 综合推荐：按你的发展路径排序

## 第一梯队（立即可做，和学习计划同步）

| 项目 | 类型 | 难度 | 为什么先做 |
|------|------|------|-----------|
| E4 答题卡批改 | CV | ⭐ | 纯OpenCV，不需要DL，练手CV基础 |
| M1 抑郁文本检测 | NLP | ⭐⭐ | 经典NLP分类，数据集现成 |
| E1 PaddleOCR | CV | ⭐⭐ | 开箱即用，快速出成果 |
| M3 情绪日记分析 | NLP+可视化 | ⭐⭐ | 结合NLP和时序分析 |

## 第二梯队（学完阶段六后）

| 项目 | 类型 | 难度 | 为什么做 |
|------|------|------|---------|
| E9 试卷区域检测 | CV | ⭐⭐⭐ | 你最想做的项目 |
| M5 表情情绪识别 | CV | ⭐⭐⭐ | CNN实战 + DeepFace |
| E2 数学公式识别 | CV | ⭐⭐⭐ | 教育核心能力 |
| M8 霸凌检测 | NLP | ⭐⭐⭐ | BERT多标签分类 |
| E5 知识追踪 | 序列模型 | ⭐⭐⭐ | 教育AI核心技术 |

## 第三梯队（进阶方向选择）

| 项目 | 类型 | 难度 | 说明 |
|------|------|------|------|
| M7 心理对话机器人 | LLM | ⭐⭐⭐⭐ | LLM微调方向 |
| E3 作文评分 | NLP | ⭐⭐⭐⭐ | Kaggle竞赛方向 |
| E10 错题本生成 | 综合 | ⭐⭐⭐⭐ | 多模型串联项目 |
| M4 语音情绪 | 音频 | ⭐⭐⭐⭐ | 多模态方向 |
| E7 课堂专注度 | CV | ⭐⭐⭐⭐ | 目标检测+姿态估计 |

---

> 💡 **建议**：从第一梯队挑 1-2 个和当前学习阶段匹配的项目开始。
> 不要贪多，做透一个项目比浅尝十个项目有价值得多。
