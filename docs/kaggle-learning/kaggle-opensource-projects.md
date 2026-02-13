# Kaggle 学习路线：推荐开源项目学习指南

> 配套文档：[kaggle-learning-guide.md](./kaggle-learning-guide.md)
> 目标：通过研读高质量开源项目，加速你从"会写代码"到"会做数据科学"的转变
> 原则：**每个项目都要动手跑通 → 改代码实验 → 写学习笔记**，不要只看不练

---

## 目录

- [一、项目分类总览](#一项目分类总览)
- [二、竞赛解决方案类（最核心）](#二竞赛解决方案类最核心)
- [三、EDA 与数据分析模板类](#三eda-与数据分析模板类)
- [四、特征工程与 Pipeline 类](#四特征工程与-pipeline-类)
- [五、AutoML 与调参框架类](#五automl-与调参框架类)
- [六、机器学习教学类](#六机器学习教学类)
- [七、深度学习实战类](#七深度学习实战类)
- [八、工具链与效率提升类](#八工具链与效率提升类)
- [九、开源项目学习方法论](#九开源项目学习方法论)

---

## 一、项目分类总览

| 类别 | 核心价值 | 对应学习阶段 | 优先级 |
|------|---------|-------------|--------|
| 竞赛解决方案 | 直接学 Top 选手怎么打比赛 | 阶段四~五 | ⭐⭐⭐⭐⭐ |
| EDA 模板 | 学会快速探索数据的标准流程 | 阶段二~三 | ⭐⭐⭐⭐ |
| 特征工程库 | 省时间 + 学习工程化思维 | 阶段三~五 | ⭐⭐⭐⭐ |
| AutoML 框架 | 自动化建模，快速出 baseline | 阶段四~五 | ⭐⭐⭐ |
| ML 教学项目 | 系统学理论 + 实践 | 阶段一~三 | ⭐⭐⭐⭐ |
| 深度学习实战 | 进入 CV/NLP 赛道 | 阶段六 | ⭐⭐⭐ |
| 工具链 | 提升效率，养成好习惯 | 全阶段 | ⭐⭐⭐ |

---

## 二、竞赛解决方案类（最核心）

> **这是对你目标最有价值的一类项目。** 直接看 Kaggle Top 选手的完整代码，理解他们是怎么拿到高分的。

### 2.1 🏆 Kaggle Past Solutions

- **GitHub**: https://github.com/faridrashidi/kaggle-solutions
- **Stars**: 5k+
- **内容**: 汇总了历年 Kaggle 竞赛的 Top 方案链接

**能学到什么**：

| 功能点 | 你能学到的 | 对应阶段 |
|--------|-----------|---------|
| 竞赛方案汇总索引 | 知道去哪里找每个竞赛的 Top 解法 | 阶段四 |
| 分类/回归/NLP/CV 全类型 | 不同类型竞赛的通用套路和差异 | 阶段四~六 |
| 冠军方案 write-up | 学习"方案设计思路"而不只是代码 | 阶段五 |

**怎么学**：
```
第1步：先看 Titanic 和 House Prices 的 Top 方案（你已做过这两个竞赛）
第2步：重点看方案的"方法论"部分（EDA 发现了什么、特征怎么构造的、为什么选这个模型）
第3步：选 1 个表格数据竞赛的 Top3 方案，完整复现代码
第4步：对比你自己的方案和 Top 方案的差距在哪里
```

---

### 2.2 🏆 Data Science Solutions (Titanic 经典教程)

- **GitHub**: https://github.com/minsuk-heo/kaggle-titanic
- **Stars**: 1k+
- **内容**: Titanic 竞赛从零到满分的完整过程

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 完整 EDA 流程 | 如何系统地探索数据、发现规律 |
| 特征工程实战 | Title 提取、家庭特征、分箱等经典技巧 |
| 多模型对比 | 从 LR 到 RF 到 XGBoost 的升级路径 |
| 模型融合 | 简单平均 → 加权平均 → Stacking |

**怎么学**：
```
第1步：Fork 到自己的 GitHub
第2步：在 Kaggle Notebook 中逐 cell 运行，每个 cell 加自己的注释
第3步：尝试修改特征工程部分，观察分数变化
第4步：把学到的技巧应用到 House Prices 竞赛中
```

---

### 2.3 🏆 Approaching (Almost) Any Machine Learning Problem

- **GitHub**: https://github.com/abhishekkrthakur/approachingalmost
- **作者**: Abhishek Thakur（Kaggle 世界排名第一，4x Grandmaster）
- **Stars**: 4k+
- **内容**: 配套书 + 代码，覆盖几乎所有 ML 问题的解题框架

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 二分类/多分类/回归框架 | 一套通用代码框架处理不同问题类型 |
| 交叉验证策略 | 各种 CV 策略的选择（分层、分组、时序） |
| 特征工程方法论 | 系统性的特征构造和选择方法 |
| 超参调优 | 从 GridSearch 到 Optuna 的进阶 |
| 模型部署 | 如何把模型变成 API（了解即可） |

**怎么学**：
```
第1步：先看书的目录（免费 PDF 预览），了解全局框架
第2步：从第 5 章（交叉验证）开始，这是最核心的章节
第3步：跑 src/ 目录下的代码模板，理解他的代码组织方式
第4步：把他的 train.py 框架改成你自己的竞赛模板
关键：学他的"思考方式"，而不只是代码
```

---

### 2.4 🏆 Kaggle Competition Solutions 汇编 (表格数据专精)

- **GitHub**: https://github.com/dimitreOliveira/kaggle-solutions
- **Stars**: 2k+
- **内容**: 分门别类整理的竞赛方案，侧重表格数据

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 表格数据竞赛全覆盖 | 表格数据的各种打法和常见套路 |
| 方案分类索引 | 按竞赛类型（分类/回归/时序）快速定位 |
| Top 方案共性总结 | 发现 Top 选手都在用的通用技巧 |

---

## 三、EDA 与数据分析模板类

> 学会快速做 EDA 是 Kaggle 的基本功。这些项目教你"标准动作"。

### 3.1 📊 ydata-profiling（原 pandas-profiling）

- **GitHub**: https://github.com/ydataai/ydata-profiling
- **Stars**: 12k+
- **安装**: `pip install ydata-profiling`

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 一行代码生成完整 EDA 报告 | 快速了解数据全貌，不用手写十几张图 |
| 变量分析（类型/分布/缺失/异常） | 标准 EDA 应该检查哪些维度 |
| 变量间相关性分析 | 自动检测相关性、共线性 |
| 交互式 HTML 报告 | 竞赛中快速产出 EDA 报告 |

**怎么学**：
```python
# 安装
pip install ydata-profiling

# 基本用法 — 在 Jupyter 中运行
import pandas as pd
from ydata_profiling import ProfileReport

df = pd.read_csv('titanic.csv')
profile = ProfileReport(df, title="Titanic EDA Report", explorative=True)
profile.to_notebook_iframe()  # 在 Notebook 中显示
# profile.to_file("report.html")  # 导出 HTML
```

```
第1步：对你做过的 Titanic 数据集生成报告，花 30 分钟仔细看每个分析维度
第2步：阅读源码中的 describe.py，理解它检查了哪些统计量
第3步：自己手写代码复现报告中的关键分析（不用库）
第4步：在未来每个竞赛中，先用它快速出 EDA 报告，再深入分析
```

---

### 3.2 📊 Sweetviz

- **GitHub**: https://github.com/fbdesignpro/sweetviz
- **Stars**: 3k+
- **安装**: `pip install sweetviz`

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 训练集 vs 测试集对比分析 | 检查训练集和测试集分布是否一致（竞赛关键！） |
| 目标变量关联分析 | 快速发现哪些特征和目标强相关 |
| 可视化对比报告 | 两份数据的差异一目了然 |

**怎么学**：
```python
import sweetviz as sv

# 对比训练集和测试集
train = pd.read_csv('train.csv')
test = pd.read_csv('test.csv')
report = sv.compare([train, "Train"], [test, "Test"], target_feat="Survived")
report.show_html("comparison.html")
```

```
关键学习点：在竞赛中，如果训练集和测试集分布差异大（distribution shift），
你的 CV 策略和特征工程都需要调整。Sweetviz 帮你一眼看出这个问题。
```

---

## 四、特征工程与 Pipeline 类

> 特征工程是 Kaggle 表格竞赛的核心竞争力。这些库帮你系统化这个过程。

### 4.1 🔧 Feature-engine

- **GitHub**: https://github.com/feature-engine/feature_engine
- **Stars**: 2k+
- **安装**: `pip install feature-engine`

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 缺失值处理（多种策略） | 均值/中位数/任意值/端点值/随机值填充 |
| 类别编码（6种+） | OrdinalEncoder, CountFrequencyEncoder, MeanEncoder, WoEEncoder |
| 离散化/分箱 | 等宽/等频/决策树分箱 |
| 变量变换 | Log, Reciprocal, Power, BoxCox, YeoJohnson |
| 异常值处理 | Winsorizer, OutlierTrimmer |
| 特征选择 | 方差过滤、相关性过滤、递归特征消除 |
| Pipeline 兼容 | 所有变换器都能放入 sklearn Pipeline |

**怎么学**：
```python
# 示例：用 Pipeline 组织特征工程
from feature_engine.imputation import MeanMedianImputer, CategoricalImputer
from feature_engine.encoding import CountFrequencyEncoder
from feature_engine.transformation import LogTransformer
from sklearn.pipeline import Pipeline
from sklearn.ensemble import RandomForestClassifier

pipe = Pipeline([
    ('num_imputer', MeanMedianImputer(imputation_method='median',
                                       variables=['age', 'fare'])),
    ('cat_imputer', CategoricalImputer(variables=['embarked'])),
    ('encoder', CountFrequencyEncoder(variables=['sex', 'embarked'])),
    ('log', LogTransformer(variables=['fare'])),
    ('model', RandomForestClassifier()),
])
pipe.fit(X_train, y_train)
score = pipe.score(X_test, y_test)
```

```
第1步：阅读文档中"Quick Start"部分，了解所有变换器类型
第2步：在 Titanic 数据上，用 Pipeline 组织完整的特征工程流水线
第3步：对比手写特征工程 vs 用 Feature-engine 的代码量和可维护性
第4步：阅读 MeanEncoder 的源码（feature_engine/encoding/mean_encoding.py）
      理解 Target Encoding 如何防止数据泄漏
关键：学习"Pipeline 化"的工程思维，这在正式竞赛中非常重要
```

---

### 4.2 🔧 category_encoders

- **GitHub**: https://github.com/scikit-learn-contrib/category_encoders
- **Stars**: 2k+
- **安装**: `pip install category_encoders`

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 15+ 种类别编码方式 | 远超 sklearn 内置的 LabelEncoder/OneHotEncoder |
| Target Encoder | Kaggle 竞赛最常用的编码方式之一 |
| Binary Encoder | 高基数类别特征的高效编码 |
| Leave-One-Out Encoder | 另一种防泄漏的 Target Encoding 变体 |
| Weight of Evidence Encoder | 信用评分领域常用，竞赛也好用 |

**怎么学**：
```python
import category_encoders as ce

# Target Encoding
encoder = ce.TargetEncoder(cols=['city', 'category'])
X_train_encoded = encoder.fit_transform(X_train, y_train)
X_test_encoded = encoder.transform(X_test)

# Binary Encoding（适合城市名等高基数特征）
bin_encoder = ce.BinaryEncoder(cols=['city'])
X_encoded = bin_encoder.fit_transform(X_train)
```

```
第1步：在 Titanic 上分别试 5 种编码方式，对比模型效果
第2步：对高基数特征（如城市、邮编）测试 Binary vs Target vs Frequency 编码
第3步：阅读 TargetEncoder 源码，理解正则化（smoothing）参数的作用
```

---

## 五、AutoML 与调参框架类

### 5.1 ⚡ FLAML (Fast Lightweight AutoML)

- **GitHub**: https://github.com/microsoft/FLAML
- **Stars**: 4k+
- **作者**: 微软研究院
- **安装**: `pip install flaml`

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 自动模型选择 | 自动从 LightGBM/XGBoost/RF/LR 等中选最优 |
| 自动超参调优 | 比 Optuna 更快的搜索算法（CFO） |
| 预算约束优化 | 在限定时间内找最佳模型（竞赛deadline前很实用） |
| 自定义搜索空间 | 可以指定只搜索特定模型和参数范围 |
| 零配置快速出分 | 几行代码出一个强 baseline |

**怎么学**：
```python
from flaml import AutoML

automl = AutoML()
automl.fit(
    X_train, y_train,
    task="classification",      # 或 "regression"
    time_budget=120,            # 最多跑 120 秒
    metric="accuracy",          # 优化指标
    estimator_list=["lgbm", "xgboost", "rf"],  # 候选模型
)

print(f"最佳模型: {automl.best_estimator}")
print(f"最佳分数: {automl.best_loss}")
print(f"最佳参数: {automl.best_config}")

# 预测
y_pred = automl.predict(X_test)
```

```
第1步：用 Titanic 数据，设 time_budget=60，看 FLAML 能跑出多少分
第2步：对比你手动调参的最佳分数 vs FLAML 自动出的分数
第3步：阅读 FLAML 选出的最佳参数，和你的参数有什么区别
第4步：在新竞赛中，先用 FLAML 跑 baseline（5分钟），再手动优化
关键：FLAML 不是替代你，而是帮你快速找到一个好的起点
```

---

### 5.2 ⚡ Optuna

- **GitHub**: https://github.com/optuna/optuna
- **Stars**: 11k+
- **安装**: `pip install optuna`
- **已在练习手册阶段五中详细讲解**

**额外学习点**（进阶）：

| 功能点 | 你能学到的 |
|--------|-----------|
| Optuna Dashboard | 可视化调参过程，理解参数重要性 |
| 多目标优化 | 同时优化精度和推理速度 |
| 剪枝（Pruning） | 提前终止没希望的 trial，节省时间 |
| Integration | 与 LightGBM/XGBoost 的原生集成 |

```python
# 进阶：带剪枝的 LightGBM 调参
import optuna.integration.lightgbm as lgb_optuna

# Optuna 内置的 LightGBM tuner，自动调参
dtrain = lgb.Dataset(X_train, label=y_train)
params = {"objective": "binary", "metric": "auc", "verbosity": -1}
tuner = lgb_optuna.LightGBMTunerCV(
    params, dtrain, num_boost_round=1000,
    folds=StratifiedKFold(n_splits=5), return_cvbooster=True
)
tuner.run()
print(f"Best AUC: {tuner.best_score}")
print(f"Best params: {tuner.best_params}")
```

---

## 六、机器学习教学类

> 这些项目兼顾理论讲解和代码实践，适合系统学习。

### 6.1 📚 mlcourse.ai

- **GitHub**: https://github.com/Yorko/mlcourse.ai
- **Stars**: 10k+
- **内容**: 开放的机器学习课程，含 Jupyter Notebook + Kaggle 竞赛作业

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 10 个主题的系统教程 | EDA、决策树、回归、聚类、梯度提升、时序等 |
| 每个主题配 Kaggle 作业 | 理论学完立刻在 Kaggle 上实战检验 |
| 真实竞赛数据 | 用真实竞赛数据而非玩具数据集 |
| 社区讨论 | 可以看其他学员的解法和讨论 |

**怎么学**：
```
第1步：从 Topic 1 开始，按顺序学习
第2步：每个 Topic 的 Notebook 都要在 Kaggle 上跑通
第3步：完成每个 Topic 末尾的 Assignment（提交到 Kaggle）
第4步：对比你的分数和课程给出的 baseline 分数
推荐节奏：每周 1 个 Topic，10 周完成全部
```

---

### 6.2 📚 d2l-ai/d2l-zh（动手学深度学习）

- **GitHub**: https://github.com/d2l-ai/d2l-zh
- **Stars**: 65k+
- **作者**: 李沐团队
- **在线阅读**: https://zh.d2l.ai
- **已在准备工作中推荐过（电子书）**

**额外学习路线**：

| 章节 | 对应 Kaggle 能力 |
|------|-----------------|
| 第2-4章（预备知识+线性网络+MLP） | 理解神经网络基础，对应阶段六基础 |
| 第6-7章（CNN） | 图像竞赛必备 |
| 第9-10章（RNN+注意力机制） | NLP 竞赛必备 |
| 第11章（优化算法） | 理解 SGD/Adam/学习率调度 |
| 第13章（计算机视觉） | 图像竞赛的数据增强、迁移学习 |

```
第1步：配合 B 站李沐的讲解视频一起看
第2步：每章的代码都在本地/Kaggle 跑一遍
第3步：重点学第 4 章（Kaggle 房价预测实战），这就是 Kaggle 竞赛
第4步：深度学习阶段需要时再看 CNN/RNN 章节
```

---

### 6.3 📚 Made-With-ML

- **GitHub**: https://github.com/GokuMohandas/Made-With-ML
- **Stars**: 40k+
- **内容**: 从 ML 基础到 MLOps 的完整课程

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| ML 基础（含代码） | NumPy/Pandas/sklearn 的教科书级示例 |
| 数据工程 | 数据清洗、划分、增强的最佳实践 |
| 模型开发 | 从实验到生产的完整流程 |
| MLOps | 模型版本管理、监控（进阶了解即可） |

**怎么学**：
```
重点看 "Foundations" 和 "ML Development" 两部分
MLOps 部分可以等你有竞赛经验后再看
```

---

### 6.4 📚 100-Days-Of-ML-Code

- **GitHub**: https://github.com/MLEveryday/100-Days-Of-ML-Code
- **Stars**: 25k+（中文版）
- **内容**: 100 天 ML 学习计划，每天一个知识点 + 代码

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 每日学习计划 | 帮你养成每天学习的习惯 |
| 图文解释算法 | 直观理解 SVM/KNN/决策树等算法 |
| 中文内容 | 全中文无障碍 |

```
适合在阶段一~三期间，每天花 20 分钟看一个知识点作为补充阅读
不需要严格按 100 天完成，挑和你当前阶段相关的看
```

---

## 七、深度学习实战类

### 7.1 🧠 pytorch-image-models (timm)

- **GitHub**: https://github.com/huggingface/pytorch-image-models
- **Stars**: 33k+
- **安装**: `pip install timm`

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 700+ 预训练模型 | ResNet/EfficientNet/ViT 等全部开箱即用 |
| 迁移学习实战 | 用预训练模型 finetune 自己的数据 |
| 数据增强 | Mixup/CutMix/RandAugment 等竞赛常用增强 |
| 训练技巧 | 学习率预热、余弦退火、EMA 等 |

**怎么学**：
```python
import timm

# 列出所有可用模型
print(timm.list_models('efficientnet*'))

# 加载预训练模型
model = timm.create_model('efficientnet_b0', pretrained=True, num_classes=10)

# 查看模型结构
print(model)
```

```
第1步：等你进入阶段六并学了 CNN 基础后再看
第2步：用 MNIST/FashionMNIST 试 timm 的预训练模型
第3步：参加一个 Kaggle 图像分类竞赛，用 timm 作为骨干网络
关键：图像竞赛 90% 的选手都在用 timm，这是必备工具
```

---

### 7.2 🧠 HuggingFace Transformers

- **GitHub**: https://github.com/huggingface/transformers
- **Stars**: 140k+
- **安装**: `pip install transformers`

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| BERT/GPT 等预训练模型 | NLP 竞赛的核心武器 |
| 文本分类/NER/QA | 各种 NLP 任务的标准解法 |
| 模型微调 | 在自己的数据上 finetune 大模型 |
| Tokenizer | 文本预处理的标准工具 |

**怎么学**：
```
等你进入阶段六并对 NLP 方向感兴趣时再学
HuggingFace 的官方教程本身就是最好的学习材料：
https://huggingface.co/learn/nlp-course/
```

---

## 八、工具链与效率提升类

### 8.1 🛠️ scikit-learn

- **GitHub**: https://github.com/scikit-learn/scikit-learn
- **Stars**: 61k+
- **你已经在用了，但值得深入阅读源码**

**进阶学习点**：

| 功能点 | 阅读什么源码 | 你能学到的 |
|--------|------------|-----------|
| Pipeline | `sklearn/pipeline.py` | 如何组织特征工程+模型训练的流水线 |
| ColumnTransformer | `sklearn/compose/` | 对不同列做不同处理 |
| cross_val_score | `sklearn/model_selection/` | 交叉验证的底层实现 |
| GridSearchCV | `sklearn/model_selection/_search.py` | 超参搜索的框架设计 |

```
你不需要读所有源码，只读你经常用的那几个函数
重点理解：为什么 fit_transform(X_train) 和 transform(X_test) 要分开？
→ 这是防止数据泄漏的核心设计
```

---

### 8.2 🛠️ Polars（Pandas 的高性能替代）

- **GitHub**: https://github.com/pola-rs/polars
- **Stars**: 32k+
- **安装**: `pip install polars`

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 比 Pandas 快 10-100 倍 | 大数据集处理不再等待 |
| 惰性求值 | 先构建计算图再执行，自动优化 |
| 并行计算 | 自动利用多核 CPU |
| 更严格的类型系统 | 减少类型错误 |

```python
import polars as pl

# 基本用法（和 Pandas 类似但更快）
df = pl.read_csv('large_data.csv')
result = (
    df.lazy()
    .filter(pl.col('age') > 30)
    .group_by('city')
    .agg(pl.col('salary').mean())
    .sort('salary', descending=True)
    .collect()
)
```

```
第1步：先把 Pandas 学扎实（阶段二），不要急着换
第2步：当你遇到大数据集（>1GB）处理太慢时，切换到 Polars
第3步：在竞赛中，先用 Pandas 开发，大数据集时用 Polars 加速
很多 Kaggle Top 选手已经在用 Polars，这是趋势
```

---

### 8.3 🛠️ LightAutoML

- **GitHub**: https://github.com/sb-ai-lab/LightAutoML
- **Stars**: 1k+
- **来自**: Sberbank AI Lab（俄罗斯顶尖 AI 实验室，多个 Kaggle 冠军团队）

**能学到什么**：

| 功能点 | 你能学到的 |
|--------|-----------|
| 为 Kaggle 设计的 AutoML | 专门针对竞赛场景优化 |
| 自动特征工程 | 日期解析、高基数编码、交互特征 |
| 自动 Blending | 自动融合多个模型 |
| 竞赛模式 | 有专门的竞赛 preset |

```python
from lightautoml.automl.presets.tabular_presets import TabularAutoML
from lightautoml.tasks import Task

automl = TabularAutoML(task=Task('binary'), timeout=300)
oof_pred = automl.fit_predict(train, roles={'target': 'Survived'})
test_pred = automl.predict(test)
```

---

## 九、开源项目学习方法论

> **重要：不要贪多！** 根据你当前的阶段，选对应的项目学习。

### 学习一个开源项目的标准步骤

```
┌─────────────────────────────────────────────────┐
│               开源项目学习五步法                   │
├─────────────────────────────────────────────────┤
│                                                   │
│  第1步：快速体验（30分钟）                          │
│  ├── Star + Fork 项目                             │
│  ├── 读 README 了解项目定位                        │
│  ├── 跑 Quick Start 示例                          │
│  └── 确认：这个项目对我有用吗？                     │
│                                                   │
│  第2步：深度使用（2-3天）                           │
│  ├── 在自己的数据上使用（Titanic/House Prices）     │
│  ├── 试遍核心 API                                  │
│  ├── 记录哪些功能好用、哪些不好用                    │
│  └── 对比：用它 vs 自己手写的效果差异                │
│                                                   │
│  第3步：阅读核心源码（3-5天）                       │
│  ├── 只读你用过的功能的源码                         │
│  ├── 理解关键设计决策（为什么这么做？）              │
│  ├── 学习代码组织方式                               │
│  └── 记录学到的技巧和模式                           │
│                                                   │
│  第4步：实战应用（1-2周）                           │
│  ├── 在一个新的 Kaggle 竞赛中实际使用               │
│  ├── 把项目中学到的技巧融入自己的代码                │
│  └── 和不用该项目的方案对比分数                     │
│                                                   │
│  第5步：沉淀总结                                    │
│  ├── 写一篇学习笔记（中文）                        │
│  ├── 提炼出可复用的代码模板                         │
│  └── 加入自己的"竞赛工具箱"                        │
│                                                   │
└─────────────────────────────────────────────────┘
```

### 各阶段推荐学习路线

```
阶段二~三（正在学基础）：
  必学 → ydata-profiling（EDA 自动化）
  必学 → 100-Days-Of-ML-Code（每日补充）
  选学 → mlcourse.ai（系统课程）

阶段四（Kaggle 入门）：
  必学 → Kaggle Past Solutions（看 Titanic/House Prices 的 Top 方案）
  必学 → minsuk-heo/kaggle-titanic（Titanic 满分教程）
  必学 → feature-engine + category_encoders（特征工程）
  选学 → Sweetviz（训练集 vs 测试集对比）

阶段五（进阶参赛）：
  必学 → approachingalmost（Kaggle #1 的方法论）
  必学 → Optuna（深度调参）
  必学 → FLAML（快速出 baseline）
  选学 → LightAutoML（竞赛 AutoML）
  选学 → Polars（大数据加速）

阶段六（深度学习）：
  必学 → d2l-zh（动手学深度学习）
  必学 → timm（图像方向）或 Transformers（NLP方向）
  选学 → Made-With-ML（MLOps 了解）
```

### 每周学习建议

```
周一~周五（工作日，每天 1-1.5 小时）：
  - 主线学习（跟阶段走）
  - 练习手册中的题目

周六（3-4 小时）：
  - 开源项目深度学习日
  - 选一个项目，跑代码 + 读源码

周日（2-3 小时）：
  - Kaggle 竞赛实战
  - 把本周学到的技巧用到竞赛中
  - 写学习笔记
```

---

## 附：项目快速索引表

| 项目 | GitHub Stars | 一句话总结 | 最佳学习时机 |
|------|-------------|-----------|-------------|
| kaggle-solutions | 5k+ | 历年竞赛 Top 方案索引 | 阶段四开始 |
| kaggle-titanic | 1k+ | Titanic 从零到满分 | 阶段四 |
| approachingalmost | 4k+ | Kaggle #1 的 ML 方法论 | 阶段五 |
| ydata-profiling | 12k+ | 一行代码 EDA 报告 | 阶段二 |
| sweetviz | 3k+ | 训练集 vs 测试集对比 | 阶段四 |
| feature-engine | 2k+ | 特征工程全家桶 | 阶段三 |
| category_encoders | 2k+ | 15+ 种类别编码 | 阶段三 |
| FLAML | 4k+ | 微软 AutoML，快速 baseline | 阶段四 |
| Optuna | 11k+ | 超参调优框架 | 阶段五 |
| mlcourse.ai | 10k+ | ML 课程 + Kaggle 作业 | 阶段二~三 |
| d2l-zh | 65k+ | 动手学深度学习（中文） | 阶段六 |
| 100-Days-Of-ML | 25k+ | 100天学ML（中文） | 阶段一~三 |
| timm | 33k+ | 700+ 图像预训练模型 | 阶段六 |
| transformers | 140k+ | NLP 预训练模型 | 阶段六 |
| Polars | 32k+ | 高性能 DataFrame | 阶段五 |
| LightAutoML | 1k+ | 竞赛专用 AutoML | 阶段五 |
| scikit-learn | 61k+ | ML 基础库（读源码） | 全阶段 |
| Made-With-ML | 40k+ | ML → MLOps 全流程 | 阶段五~六 |
