# 案例驱动学习：10 个实战项目从入门到进阶

> 理念：**每个案例解决一个真实问题，在实战中学习知识点，而不是先学知识再找场景**
> 顺序：按难度递增排列，建议从案例 1 开始，逐步推进
> 配套：[完整学习指南](./kaggle-learning-guide.md) | [练习手册](./README.md) | [开源项目](./kaggle-opensource-projects.md)

---

## 案例总览

| # | 案例 | 解决什么问题 | 类型 | 核心知识点 | 难度 |
|---|------|-------------|------|-----------|------|
| 1 | [电影评分预测](#案例-1电影评分预测--推荐系统入门) | 预测用户对电影的评分 | 表格回归 | Pandas/EDA/基础建模 | ⭐ |
| 2 | [信用卡欺诈检测](#案例-2信用卡欺诈检测--不平衡分类) | 从交易流水中识别欺诈 | 表格分类 | 不平衡处理/评估指标/特征工程 | ⭐⭐ |
| 3 | [二手房价预测](#案例-3二手房价预测--特征工程实战) | 根据房屋属性预测价格 | 表格回归 | 特征工程/XGBoost/调参 | ⭐⭐ |
| 4 | [电商用户流失预测](#案例-4电商用户流失预测--业务分析) | 预测哪些用户即将流失 | 表格分类 | 业务理解/模型可解释性/SHAP | ⭐⭐ |
| 5 | [垃圾短信分类](#案例-5垃圾短信分类--nlp-入门) | 自动识别垃圾短信 | NLP 分类 | 文本处理/TF-IDF/朴素贝叶斯 | ⭐⭐ |
| 6 | [手写数字识别](#案例-6手写数字识别--cnn-入门) | 识别手写数字图片 | 图像分类 | CNN/PyTorch/卷积 | ⭐⭐ |
| 7 | [试卷区域检测](#案例-7试卷区域检测--目标定位) | 从照片中定位试卷内容区域 | 关键点回归 | 数据标注/迁移学习/数据增强 | ⭐⭐⭐ |
| 8 | [商品评论情感分析](#案例-8商品评论情感分析--bert-微调) | 判断评论是好评还是差评 | NLP 分类 | Transformer/BERT/微调 | ⭐⭐⭐ |
| 9 | [表格竞赛通杀方案](#案例-9kaggle-表格竞赛通杀方案--竞赛实战) | Kaggle 表格竞赛 Top 方案 | 竞赛 | 模型融合/Stacking/实验管理 | ⭐⭐⭐⭐ |
| 10 | [车牌识别系统](#案例-10车牌识别系统--端到端-cv-项目) | 检测并识别车牌号 | 目标检测+OCR | YOLO/CRNN/端到端 Pipeline | ⭐⭐⭐⭐ |

---

## 案例 1：电影评分预测 — 推荐系统入门

### 解决什么问题

> 电影网站有用户的历史评分数据，需要预测用户对未看过的电影会打几分，用于个性化推荐。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **数据集** | [MovieLens 100K](https://grouplens.org/datasets/movielens/100k/) — 10万条评分数据 |
| **开源项目** | [surprise](https://github.com/NicolasHug/Surprise) — Python 推荐系统库（⭐ 6k+） |
| **Kaggle** | [Movie Recommendation](https://www.kaggle.com/datasets/grouplens/movielens-20m-dataset) |

### 涉及知识点

```
阶段二: Pandas 数据处理、EDA、数据可视化
阶段三: 回归模型（线性回归 → 随机森林 → SVD 矩阵分解）
新知识: 推荐系统基础概念（协同过滤）
```

### 技术路线

```
第1步：数据探索（2天）
├── 加载 MovieLens 数据集（users.csv, movies.csv, ratings.csv）
├── 统计：有多少用户？多少电影？评分分布？
├── 画图：评分分布直方图、最热门电影 Top20、最活跃用户 Top20
└── 发现：数据是稀疏的（大部分用户只评了很少的电影）

第2步：基础建模（2天）
├── 基线模型：预测所有电影的均分（RMSE baseline）
├── 方法1：用户平均分 + 电影平均分的组合
├── 方法2：用 sklearn 做回归（用户ID + 电影ID + 类型等特征）
└── 对比各方法的 RMSE

第3步：推荐算法（3天）
├── 安装 surprise 库
├── 用 SVD（矩阵分解）算法训练
├── 5折交叉验证评估
├── 调参：n_factors, n_epochs, lr_all, reg_all
└── 对比 SVD vs KNN vs baseline 的效果

第4步：输出成果
├── 给指定用户推荐 Top10 电影
├── 写一份分析报告
└── 学习笔记：推荐系统的核心思想
```

### 核心代码

```python
import pandas as pd
from surprise import Dataset, Reader, SVD
from surprise.model_selection import cross_validate

# 加载数据
ratings = pd.read_csv('ratings.csv')
print(f"评分数据: {len(ratings)} 条")
print(f"用户数: {ratings.userId.nunique()}, 电影数: {ratings.movieId.nunique()}")

# EDA
ratings.rating.hist(bins=10)
plt.title('评分分布')
plt.show()

# 用 Surprise 训练 SVD
reader = Reader(rating_scale=(0.5, 5))
data = Dataset.load_from_df(ratings[['userId','movieId','rating']], reader)

model = SVD(n_factors=100, n_epochs=20, lr_all=0.005, reg_all=0.02)
results = cross_validate(model, data, measures=['RMSE','MAE'], cv=5, verbose=True)
print(f"平均 RMSE: {results['test_rmse'].mean():.4f}")
```

---

## 案例 2：信用卡欺诈检测 — 不平衡分类

### 解决什么问题

> 银行每天处理百万笔信用卡交易，其中欺诈交易不到 0.2%。如何从海量正常交易中找出那极少数的欺诈？

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **数据集** | [Kaggle Credit Card Fraud](https://www.kaggle.com/datasets/mlg-ulb/creditcardfraud) — 28万条交易，⭐ Kaggle 最热门数据集之一 |
| **开源方案** | [fraud-detection-handbook](https://github.com/Fraud-Detection-Handbook/fraud-detection-handbook) — 欺诈检测教科书（⭐ 1k+） |
| **imbalanced-learn** | [imblearn](https://github.com/scikit-learn-contrib/imbalanced-learn) — 不平衡数据处理库（⭐ 7k+） |

### 涉及知识点

```
阶段二: Pandas + 可视化（数据极度偏斜的 EDA）
阶段三: 分类模型、交叉验证
核心新知识:
  - 不平衡数据处理（SMOTE 过采样、欠采样、类别权重）
  - 评估指标选择（准确率在这里毫无意义！要看 Precision/Recall/F1/AUC-PR）
  - 阈值调优（不同阈值对 Precision 和 Recall 的 trade-off）
```

### 技术路线

```
第1步：数据探索（1天）
├── 加载数据，发现 Class=1（欺诈）只有 492 条（0.17%）
├── 可视化：正常 vs 欺诈的金额分布差异
├── 可视化：PCA 特征的分布（数据已做过 PCA 脱敏）
├── 关键发现：如果模型预测"全部正常"，准确率 99.83%！
└── 所以准确率(accuracy)在这个场景下毫无意义

第2步：基线模型（2天）
├── 直接用 LogisticRegression 训练
├── 用 classification_report 看 Precision/Recall/F1
├── 画 ROC 曲线和 Precision-Recall 曲线
├── 对比 AUC-ROC vs AUC-PR（PR 曲线更适合不平衡数据）
└── 基线结果：欺诈类 Recall 可能只有 60%（漏掉 40% 的欺诈！）

第3步：处理不平衡（3天）
├── 方法1：class_weight='balanced'（调整类别权重）
├── 方法2：SMOTE 过采样（生成合成欺诈样本）
├── 方法3：随机欠采样（减少正常样本）
├── 方法4：组合策略（SMOTE + Tomek Links）
├── 对比各方法的 Recall 和 Precision
└── 关键学习：Precision 和 Recall 不可兼得，要根据业务需求选阈值

第4步：进阶优化（2天）
├── 用 XGBoost + scale_pos_weight 参数
├── 用 Optuna 调参
├── 模型融合（LR + RF + XGBoost 投票）
├── 阈值调优：画 Precision-Recall 曲线选最佳阈值
└── 最终成果：Recall > 90%（找到 90%+ 的欺诈），Precision > 80%
```

### 核心代码

```python
import pandas as pd
import numpy as np
from sklearn.model_selection import StratifiedKFold
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, precision_recall_curve, auc
from imblearn.over_sampling import SMOTE
import matplotlib.pyplot as plt

df = pd.read_csv('creditcard.csv')
print(f"欺诈比例: {df.Class.mean():.4%}")  # 0.17%

X = df.drop('Class', axis=1)
y = df['Class']

# 方法1: 基线
lr = LogisticRegression(max_iter=1000)
lr.fit(X_train, y_train)
print(classification_report(y_test, lr.predict(X_test)))

# 方法2: SMOTE 过采样
smote = SMOTE(random_state=42)
X_res, y_res = smote.fit_resample(X_train, y_train)
print(f"过采样后: 正常={sum(y_res==0)}, 欺诈={sum(y_res==1)}")

lr_smote = LogisticRegression(max_iter=1000)
lr_smote.fit(X_res, y_res)
print(classification_report(y_test, lr_smote.predict(X_test)))

# Precision-Recall 曲线
probs = lr_smote.predict_proba(X_test)[:, 1]
precision, recall, thresholds = precision_recall_curve(y_test, probs)
pr_auc = auc(recall, precision)

plt.plot(recall, precision, label=f'AUC-PR={pr_auc:.3f}')
plt.xlabel('Recall')
plt.ylabel('Precision')
plt.title('Precision-Recall 曲线')
plt.legend()
plt.show()
```

---

## 案例 3：二手房价预测 — 特征工程实战

### 解决什么问题

> 房产中介需要一个自动估价系统，输入房屋的面积、楼层、装修、学区等属性，输出预估价格。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **数据集** | [Kaggle House Prices](https://www.kaggle.com/c/house-prices-advanced-feature-engineering) — 经典 Kaggle 入门竞赛 |
| **冠军方案** | [Top Solutions 汇总](https://www.kaggle.com/c/house-prices-advanced-feature-engineering/discussion) |
| **教程 Notebook** | [Comprehensive data exploration](https://www.kaggle.com/pmarcelino/comprehensive-data-exploration-with-python) — ⭐4k+ 票 |

### 涉及知识点

```
阶段二: EDA（缺失值分析、分布偏斜、多重共线性）
阶段三: 回归模型 + 特征工程（核心）
阶段五: 模型融合 + 调参
核心新知识:
  - 目标变量 log 变换（房价右偏分布）
  - 特征交互（面积 × 房间数 = 每间面积）
  - 缺失值有业务含义（"无车库"不是缺失，是"没有车库"）
  - 正则化回归（Ridge/Lasso/ElasticNet）
```

### 技术路线

```
第1步：EDA 深度探索（2天）
├── 80 个特征！先理解每个特征的业务含义
├── 目标变量 SalePrice：右偏 → log 变换 → 近似正态
├── 缺失值分析：PoolQC 缺 99%（大部分房没游泳池，这是有意义的缺失）
├── 相关性分析：OverallQual 和 GrLivArea 与房价最强相关
├── 异常值：GrLivArea > 4000 的两个点明显异常
└── 画散点图、箱线图、热力图

第2步：特征工程（3天）⭐ 这是本案例的核心
├── 缺失值处理：分类型（填 "None"）和数值型（填 0 或中位数）
├── 类别编码：有序类别（Ex>Gd>TA>Fa>Po）→ OrdinalEncoder
├── 特征构造：
│   ├── TotalSF = 1stFlrSF + 2ndFlrSF + TotalBsmtSF（总面积）
│   ├── TotalBath = FullBath + 0.5*HalfBath + ...（总浴室数）
│   ├── HouseAge = YrSold - YearBuilt（房龄）
│   ├── RemodAge = YrSold - YearRemodAdd（装修年龄）
│   └── IsNew = (YearBuilt == YrSold)（是否新房）
├── 偏斜特征：对偏斜度 > 0.75 的数值特征做 Box-Cox 变换
└── 特征选择：去掉相关性 > 0.9 的冗余特征

第3步：建模对比（2天）
├── 基线：LinearRegression（RMSE ~0.18）
├── 正则化：Ridge / Lasso / ElasticNet
├── 树模型：RandomForest / XGBoost / LightGBM
├── 5折 CV 对比各模型
└── 发现：Lasso 自动做了特征选择（部分系数为 0）

第4步：融合冲分（2天）
├── 加权平均：0.3*Lasso + 0.3*Ridge + 0.4*XGBoost
├── Stacking：用 Lasso/Ridge/XGBoost 作为 base，LR 作为 meta
├── 提交 Kaggle 看排名
└── 目标：RMSE < 0.12（Top 20%）
```

---

## 案例 4：电商用户流失预测 — 业务分析

### 解决什么问题

> 电商平台发现老用户不断流失，需要提前识别"即将流失"的用户，主动发优惠券挽回。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **数据集** | [Kaggle Telco Customer Churn](https://www.kaggle.com/datasets/blastchar/telco-customer-churn) — 7k 用户数据 |
| **SHAP 库** | [shap](https://github.com/shap/shap) — 模型可解释性（⭐ 23k+） |
| **教程** | [Customer Churn Prediction](https://www.kaggle.com/code/bandiatindra/telecom-churn-prediction) |

### 涉及知识点

```
阶段二: EDA（业务视角分析）
阶段三: 分类模型 + 特征工程
核心新知识:
  - 业务理解驱动特征工程（不是纯技术，要理解业务）
  - 模型可解释性（SHAP 值：为什么模型认为这个用户会流失？）
  - ROI 分析（挽回一个用户值多少钱？发券成本是多少？）
```

### 技术路线

```
第1步：业务理解 + EDA（2天）
├── 流失率是多少？26.5%
├── 哪些特征和流失强相关？
│   ├── 合同类型：按月付费用户流失率远高于年付用户
│   ├── 任期：新用户（<6个月）流失率最高
│   ├── 月费：高月费用户更容易流失
│   └── 服务：没有技术支持的用户更容易流失
└── 画各维度的流失率对比柱状图

第2步：特征工程 + 建模（2天）
├── 特征构造：
│   ├── 月均消费 = TotalCharges / tenure
│   ├── 服务使用数量 = 各服务的 Yes 计数
│   └── 合同到期天数
├── XGBoost + 5折 StratifiedKFold
└── AUC > 0.83

第3步：模型可解释性（2天）⭐ 本案例最大亮点
├── SHAP 全局解释：哪些特征最重要？
├── SHAP 局部解释：这个用户为什么被预测为"将流失"？
├── SHAP dependence plot：tenure 和流失概率的关系
└── 输出：给运营团队的分析报告

第4步：业务落地模拟
├── 设定阈值：概率 > 0.7 → 高风险用户
├── 计算：如果给高风险用户发 20 元券，挽回率 30%
├── ROI：每挽回一个用户年均消费 800 元 → 划算！
└── 学到：模型不是终点，业务价值才是
```

### 核心代码（SHAP 可解释性）

```python
import shap
import xgboost as xgb

model = xgb.XGBClassifier(n_estimators=200, max_depth=5, learning_rate=0.1)
model.fit(X_train, y_train)

# SHAP 解释
explainer = shap.TreeExplainer(model)
shap_values = explainer.shap_values(X_test)

# 全局特征重要性
shap.summary_plot(shap_values, X_test)

# 单个用户的解释（为什么预测这个用户会流失？）
shap.force_plot(explainer.expected_value, shap_values[0], X_test.iloc[0])

# 特征依赖图
shap.dependence_plot('tenure', shap_values, X_test)
```

---

## 案例 5：垃圾短信分类 — NLP 入门

### 解决什么问题

> 手机收到的短信，自动判断是正常短信还是垃圾广告/诈骗短信。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **数据集** | [SMS Spam Collection](https://www.kaggle.com/datasets/uciml/sms-spam-collection-dataset) — 5.5k 条短信 |
| **中文数据集** | [Chinese Spam Messages](https://github.com/hrwhisper/SpamMessage) — 中文垃圾短信 |
| **jieba** | [jieba 分词](https://github.com/fxsjy/jieba) — 中文分词库（⭐ 33k+） |

### 涉及知识点

```
阶段二: 文本数据的 Pandas 处理
阶段三: 分类模型
核心新知识:
  - 文本预处理（分词、去停用词、词干化）
  - 文本向量化（CountVectorizer → TF-IDF → Word2Vec）
  - 朴素贝叶斯（文本分类经典算法）
  - 中文分词（jieba）
```

### 技术路线

```
第1步：文本 EDA（1天）
├── 垃圾短信 vs 正常短信的长度分布
├── 词云图：垃圾短信的高频词（"免费""中奖""点击"）
├── 正常短信的高频词（"好的""在吗""明天"）
└── 发现：垃圾短信平均更长、感叹号更多

第2步：文本向量化（2天）
├── 方法1: CountVectorizer（词袋模型）→ 简单计数
├── 方法2: TF-IDF（词频-逆文档频率）→ 更好！
├── 方法3: Word2Vec 词向量 → 语义相似性
├── 对比各方法生成的特征矩阵
└── 学习：为什么 TF-IDF 比纯词频更好？

第3步：建模对比（2天）
├── 朴素贝叶斯（NLP 经典 baseline，效果常常出奇地好）
├── LogisticRegression + TF-IDF
├── 随机森林 + TF-IDF
├── 对比 F1 分数
└── 结果：朴素贝叶斯 + TF-IDF 就能达到 F1 > 0.97

第4步：中文版本（2天）
├── 换成中文垃圾短信数据集
├── 用 jieba 分词
├── 中文停用词处理
└── 对比中英文处理流程的差异
```

### 核心代码

```python
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report

# 加载数据
df = pd.read_csv('spam.csv', encoding='latin-1')
df = df[['v1','v2']].rename(columns={'v1':'label','v2':'text'})
df['label'] = (df.label == 'spam').astype(int)

X_train, X_test, y_train, y_test = train_test_split(
    df.text, df.label, test_size=0.2, random_state=42
)

# TF-IDF 向量化
tfidf = TfidfVectorizer(max_features=5000, stop_words='english')
X_train_vec = tfidf.fit_transform(X_train)
X_test_vec = tfidf.transform(X_test)

# 朴素贝叶斯
nb = MultinomialNB()
nb.fit(X_train_vec, y_train)
print(classification_report(y_test, nb.predict(X_test_vec)))

# 看模型学到了什么：垃圾短信的"信号词"
feature_names = tfidf.get_feature_names_out()
spam_scores = nb.feature_log_prob_[1] - nb.feature_log_prob_[0]
top_spam_words = pd.Series(spam_scores, index=feature_names).nlargest(20)
print("垃圾短信信号词:")
print(top_spam_words)
```

---

## 案例 6：手写数字识别 — CNN 入门

### 解决什么问题

> 邮局需要自动识别信封上手写的邮政编码，或银行需要识别支票上手写的金额。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **数据集** | [MNIST](http://yann.lecun.com/exdb/mnist/) — 7万张手写数字图片，深度学习的"Hello World" |
| **Kaggle** | [Digit Recognizer](https://www.kaggle.com/c/digit-recognizer) — Kaggle 入门竞赛 |
| **PyTorch 官方教程** | [Training a Classifier](https://pytorch.org/tutorials/beginner/blitz/cifar10_tutorial.html) |

### 涉及知识点

```
阶段六: PyTorch 基础 + CNN
核心新知识:
  - 图片是什么（28×28 的像素矩阵）
  - 卷积层如何提取特征（边缘 → 纹理 → 形状）
  - 池化层的作用（降维 + 平移不变性）
  - Softmax + CrossEntropyLoss（多分类）
  - GPU 加速训练
```

### 技术路线

```
第1步：理解图片数据（1天）
├── 加载 MNIST，打印一张图片的 NumPy 数组
├── 可视化 10 个类别各 5 张样本
├── 理解：图片就是 0-255 的数字矩阵
└── 归一化到 [0,1]

第2步：从简单模型开始（2天）
├── 全连接网络：Flatten(784) → Linear(256) → Linear(10)
├── 训练 10 epoch，测试准确率 ~97%
├── 画混淆矩阵：哪些数字容易互相混淆？（4 和 9，3 和 8）
└── 思考：全连接网络为什么不够好？（不考虑空间结构）

第3步：CNN 模型（3天）⭐ 核心
├── Conv2d(1,32,3) → ReLU → MaxPool
├── Conv2d(32,64,3) → ReLU → MaxPool
├── Flatten → Linear(128) → Linear(10)
├── 准确率提升到 99%+
├── 可视化第一层卷积核（学到了什么？边缘检测！）
└── 可视化特征图（中间层的输出）

第4步：提交 Kaggle（1天）
├── 在 Digit Recognizer 竞赛上提交
├── 加数据增强（旋转、平移）冲 Top 10%
└── 尝试更深的网络（ResNet）
```

### 核心代码

```python
import torch
import torch.nn as nn
from torchvision import datasets, transforms
from torch.utils.data import DataLoader

# 数据
transform = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize((0.1307,), (0.3081,))
])
train_data = datasets.MNIST('./data', train=True, download=True, transform=transform)
test_data = datasets.MNIST('./data', train=False, transform=transform)

train_loader = DataLoader(train_data, batch_size=64, shuffle=True)
test_loader = DataLoader(test_data, batch_size=1000)

# CNN
class CNN(nn.Module):
    def __init__(self):
        super().__init__()
        self.conv = nn.Sequential(
            nn.Conv2d(1, 32, 3, padding=1), nn.ReLU(), nn.MaxPool2d(2),
            nn.Conv2d(32, 64, 3, padding=1), nn.ReLU(), nn.MaxPool2d(2),
        )
        self.fc = nn.Sequential(
            nn.Flatten(), nn.Linear(64*7*7, 128), nn.ReLU(),
            nn.Dropout(0.3), nn.Linear(128, 10)
        )
    def forward(self, x):
        return self.fc(self.conv(x))

model = CNN()
optimizer = torch.optim.Adam(model.parameters(), lr=1e-3)
criterion = nn.CrossEntropyLoss()

# 训练
for epoch in range(10):
    model.train()
    for images, labels in train_loader:
        optimizer.zero_grad()
        loss = criterion(model(images), labels)
        loss.backward()
        optimizer.step()

    # 测试
    model.eval()
    correct = sum((model(img).argmax(1) == lab).sum().item()
                  for img, lab in test_loader)
    print(f"Epoch {epoch+1}: acc={correct/len(test_data):.4f}")
```

---

## 案例 7：试卷区域检测 — 目标定位

### 解决什么问题

> 教育 App 需要用户拍照上传试卷，自动识别并裁剪出试卷内容区域，去除桌面背景。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **类似项目** | [DocTR](https://github.com/mindee/doctr) — 文档识别（⭐ 4k+） |
| **数据标注** | [LabelMe](https://github.com/labelmeai/labelme) — 标注工具（⭐ 13k+） |
| **SmartDoc** | [SmartDoc 2015](https://github.com/jchazalon/smartdoc15-ch1-dataset) — 文档定位数据集 |
| **albumentations** | [albumentations](https://github.com/albumentations-team/albumentations) — 数据增强（⭐ 14k+） |
| **timm** | [timm](https://github.com/huggingface/pytorch-image-models) — 预训练模型（⭐ 33k+） |

### 涉及知识点

```
阶段六: PyTorch + CNN + 迁移学习
核心新知识:
  - 数据标注流程（LabelMe 标注关键点）
  - 迁移学习（用预训练 MobileNet 作为骨干）
  - 关键点回归（输出坐标而不是类别）
  - 数据增强对小数据集的重要性（albumentations）
  - 透视变换校正（OpenCV warpPerspective）
```

### 技术路线

（已在上一条消息中详细给出，这里做整合）

```
第1周：数据收集 + 标注
├── 手机拍 300+ 张试卷照片（不同角度/光线/背景/桌面颜色）
├── 用 LabelMe 标注 4 个角点（左上/右上/右下/左下）
├── 导出为 JSON 格式
├── 写 Dataset 类 + 数据增强（albumentations）
└── 可视化确认标注和增强效果

第2周：模型训练
├── 骨干: MobileNetV3-Small（轻量，1.5M 参数）
├── 头部: Linear(576→256→64→8) + Sigmoid
├── 损失: SmoothL1Loss（对异常值鲁棒）
├── 优化: AdamW + CosineAnnealingLR
├── 训练 50-100 epoch
└── 验证集上看角点偏差（像素误差 < 10px 为合格）

第3周：优化 + 后处理
├── 数据增强调优（透视变换是关键增强）
├── 更换骨干（EfficientNet-B0 试试）
├── 后处理：用预测的 4 点做透视校正（warpPerspective）
├── 导出 ONNX 模型
└── 写推理脚本：输入一张照片 → 输出裁剪后的试卷正视图
```

---

## 案例 8：商品评论情感分析 — BERT 微调

### 解决什么问题

> 电商平台每天产生百万条商品评论，需要自动分析用户情感（好评/中性/差评），用于商品质量监控。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **中文数据集** | [ChnSentiCorp](https://huggingface.co/datasets/lansinuote/ChnSentiCorp) — 中文情感分析 |
| **中文 BERT** | [bert-base-chinese](https://huggingface.co/bert-base-chinese) — 中文预训练模型 |
| **HuggingFace** | [transformers](https://github.com/huggingface/transformers) — ⭐ 140k+ |
| **教程** | [HF NLP Course](https://huggingface.co/learn/nlp-course/) — 官方 NLP 课程 |

### 涉及知识点

```
阶段六: 深度学习进阶
核心新知识:
  - Transformer 架构（Attention 机制的直觉理解）
  - 预训练 + 微调范式（不用从头训练！）
  - Tokenizer（文本如何变成数字）
  - HuggingFace 生态（模型、数据集、训练工具一站式）
  - 学习率 warmup + 线性衰减
```

### 技术路线

```
第1步：传统方法 baseline（2天）
├── 用 jieba 分词 + TF-IDF + 逻辑回归
├── F1 约 0.88
└── 作为对比基线

第2步：BERT 微调（3天）⭐
├── 加载 bert-base-chinese 预训练模型
├── 加分类头: BERT → Dropout → Linear(768→2)
├── 训练 3 epoch（BERT 微调不需要太多 epoch）
├── F1 跳到 0.95+
└── 对比：BERT vs TF-IDF 提升了 7 个点！

第3步：理解 BERT（2天）
├── 可视化 Attention 权重（BertViz 库）
├── 看模型关注了哪些词？（"差""烂""好用""推荐"）
├── 对比不同预训练模型：BERT vs RoBERTa vs ERNIE
└── 学习：为什么预训练 + 微调如此强大？

第4步：部署测试
├── 写一个推理函数：输入一条评论 → 输出情感
├── 测试一些边界情况（反讽、双重否定）
└── 体验模型的局限性
```

### 核心代码

```python
from transformers import BertTokenizer, BertForSequenceClassification
from transformers import Trainer, TrainingArguments
from datasets import load_dataset

# 加载数据和模型
dataset = load_dataset('lansinuote/ChnSentiCorp')
tokenizer = BertTokenizer.from_pretrained('bert-base-chinese')
model = BertForSequenceClassification.from_pretrained('bert-base-chinese', num_labels=2)

# Tokenize
def tokenize(examples):
    return tokenizer(examples['text'], truncation=True, padding='max_length', max_length=128)

tokenized = dataset.map(tokenize, batched=True)

# 训练
args = TrainingArguments(
    output_dir='./results',
    num_train_epochs=3,
    per_device_train_batch_size=16,
    per_device_eval_batch_size=64,
    warmup_steps=500,
    weight_decay=0.01,
    evaluation_strategy='epoch',
    logging_dir='./logs',
)

trainer = Trainer(model=model, args=args,
                  train_dataset=tokenized['train'],
                  eval_dataset=tokenized['validation'])
trainer.train()

# 推理
from transformers import pipeline
classifier = pipeline('sentiment-analysis', model=model, tokenizer=tokenizer)
print(classifier("这个手机拍照非常清晰，电池也很耐用"))
print(classifier("质量太差了，用了一周就坏了"))
```

---

## 案例 9：Kaggle 表格竞赛通杀方案 — 竞赛实战

### 解决什么问题

> 你想在 Kaggle 表格竞赛中稳定拿到银牌/金牌，需要一套可复用的"通杀"竞赛框架。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **框架参考** | [approachingalmost](https://github.com/abhishekkrthakur/approachingalmost) — Kaggle #1（⭐ 4k+） |
| **FLAML** | [FLAML](https://github.com/microsoft/FLAML) — 微软 AutoML（⭐ 4k+） |
| **Optuna** | [optuna](https://github.com/optuna/optuna) — 超参调优（⭐ 11k+） |
| **竞赛** | 随便选一个当前进行中的 Kaggle 表格竞赛 |

### 涉及知识点

```
阶段四+五 全部知识的整合:
  - 完整 EDA Pipeline
  - 系统性特征工程
  - 多模型训练（LightGBM + XGBoost + CatBoost）
  - Optuna 超参搜索
  - 模型融合（加权平均 + Stacking）
  - 对抗验证（Adversarial Validation）
  - 伪标签（Pseudo Labeling）
```

### 竞赛通杀模板

```
Day 1-2: 理解比赛
├── 读比赛描述 3 遍（理解评价指标！）
├── 下载数据，完整 EDA
├── 用 ydata-profiling 生成 EDA 报告
├── 用 Sweetviz 对比 train vs test 分布
└── 读 Discussion 区前 10 个帖子

Day 3-5: Baseline + 特征工程 V1
├── FLAML 跑 2 分钟出快速 baseline
├── 手动 LightGBM + 5折 CV baseline
├── 基础特征工程（编码、缺失值、简单交互特征）
├── 第一次提交 → 看 LB 分数 vs CV 分数是否一致
└── 如果差异大 → 检查数据泄漏或分布偏移

Day 6-10: 特征工程深挖
├── Target Encoding（带 CV 防泄漏）
├── 频率编码、统计编码（groupby 聚合）
├── 交互特征（特征两两组合）
├── 时间特征（如果有日期）
├── 特征选择：permutation importance 去噪声特征
└── 每次加特征都要看 CV 分数变化

Day 11-15: 调参 + 多模型
├── Optuna 调 LightGBM（200 trials）
├── Optuna 调 XGBoost
├── Optuna 调 CatBoost
├── 记录每个模型的最佳 CV 分数
└── 实验日志记录（模型、参数、分数、备注）

Day 16-20: 融合 + 冲分
├── 加权平均：根据 CV 分数分配权重
├── Rank 平均：把概率转成排名再平均（更稳健）
├── Stacking：用 3 个模型的 OOF 预测作为新特征
├── 可选：伪标签（用高置信度的测试集预测加入训练）
├── 多次提交，选 CV 和 LB 都好的方案
└── 目标：银牌区（Top 5%）
```

---

## 案例 10：车牌识别系统 — 端到端 CV 项目

### 解决什么问题

> 停车场需要自动识别进出车辆的车牌号，实现无人值守收费。这是一个两阶段任务：先检测车牌位置，再识别车牌文字。

### 开源项目 & 数据集

| 资源 | 链接 |
|------|------|
| **完整项目** | [HyperLPR](https://github.com/szad670401/HyperLPR) — 中文车牌识别（⭐ 6k+） |
| **YOLO** | [ultralytics/yolov5](https://github.com/ultralytics/yolov5) — 目标检测（⭐ 52k+） |
| **PaddleOCR** | [PaddleOCR](https://github.com/PaddlePaddle/PaddleOCR) — OCR 工具包（⭐ 45k+） |
| **CCPD 数据集** | [CCPD](https://github.com/detectRecog/CCPD) — 25万张中国车牌（⭐ 3k+） |

### 涉及知识点

```
阶段六进阶:
  - 目标检测（YOLO 系列）
  - OCR（光学字符识别）
  - 多阶段 Pipeline（检测 → 裁剪 → 识别）
  - 数据集格式（COCO、VOC、YOLO 格式互转）
  - 模型串联和推理优化
```

### 技术路线

```
阶段 A：先跑通开源项目（3天）
├── clone HyperLPR，跑通 demo
├── 用自己拍的车牌照片测试
├── 理解整体 Pipeline：输入照片 → 车牌检测 → 车牌矫正 → 字符识别
└── 阅读核心代码，理解每个模块

阶段 B：自己训练检测模型（1周）
├── 用 CCPD 数据集 + YOLOv5 训练车牌检测
├── YOLO 格式标注：每张图标注车牌的 bounding box
├── 训练 100 epoch
├── mAP@0.5 > 0.95（车牌检测相对简单）
└── 学到：目标检测的完整流程（标注→训练→评估→推理）

阶段 C：字符识别（1周）
├── 方案1：用 PaddleOCR 直接识别（省事）
├── 方案2：自己训练 CRNN（学习更多）
├── 中国车牌格式：京A·12345（1个汉字+1个字母+5个字母数字）
└── 端到端准确率 > 95%

阶段 D：组装完整系统
├── Pipeline：YOLO检测 → 裁剪 → 透视校正 → OCR识别
├── 写一个完整推理脚本
├── 可选：加 Flask 做 Web API
└── 最终体验：上传一张照片 → 返回车牌号
```

---

## 学习路线图：10 个案例的推荐顺序

```
                    ┌──────────────────────────────────────┐
                    │         案例学习推荐路径               │
                    └──────────────────────────────────────┘

阶段二~三（基础期）              阶段四~五（竞赛期）          阶段六（深度学习期）
 约 6-8 周                       约 2-3 个月                  约 2-3 个月
 ┌─────────────┐               ┌──────────────┐           ┌──────────────┐
 │ 案例1 电影评分 │ ──────────→ │ 案例3 房价预测  │ ────────→ │ 案例6 MNIST   │
 │ Pandas+EDA   │              │ 特征工程深度   │           │ CNN 入门      │
 └─────────────┘               └──────────────┘           └──────────────┘
        │                             │                          │
        ▼                             ▼                          ▼
 ┌─────────────┐               ┌──────────────┐           ┌──────────────┐
 │ 案例2 欺诈检测│              │ 案例4 用户流失  │           │ 案例7 试卷检测 │
 │ 不平衡+评估  │              │ SHAP 可解释性  │           │ 迁移学习+标注  │
 └─────────────┘               └──────────────┘           └──────────────┘
        │                             │                          │
        ▼                             ▼                          ▼
 ┌─────────────┐               ┌──────────────┐           ┌──────────────┐
 │ 案例5 垃圾短信│              │ 案例9 竞赛通杀  │           │ 案例8 BERT    │
 │ NLP 入门     │              │ 融合+调参实战   │           │ 微调+NLP      │
 └─────────────┘               └──────────────┘           └──────────────┘
                                                                 │
                                                                 ▼
                                                          ┌──────────────┐
                                                          │ 案例10 车牌识别│
                                                          │ 端到端 CV     │
                                                          └──────────────┘
```

### 每个案例的预计耗时

| 案例 | 预计耗时 | 产出物 |
|------|---------|--------|
| 案例 1 电影评分 | 1 周 | 推荐系统 demo + 分析报告 |
| 案例 2 欺诈检测 | 1.5 周 | 不平衡分类方案 + PR 曲线分析 |
| 案例 3 房价预测 | 2 周 | Kaggle 提交 + 特征工程笔记 |
| 案例 4 用户流失 | 1.5 周 | SHAP 分析报告 + 业务建议 |
| 案例 5 垃圾短信 | 1 周 | NLP Pipeline + 中文分词实践 |
| 案例 6 MNIST | 1 周 | CNN 模型 + Kaggle 提交 |
| 案例 7 试卷检测 | 3 周 | 完整检测模型 + 透视校正 |
| 案例 8 BERT | 2 周 | 微调模型 + 情感分析 API |
| 案例 9 竞赛通杀 | 3 周 | 竞赛 Top 方案 + 可复用模板 |
| 案例 10 车牌识别 | 3 周 | 端到端识别系统 |

**总计约 5-6 个月**，和原学习计划（6-12个月）节奏一致，但每一步都在做真实项目。
