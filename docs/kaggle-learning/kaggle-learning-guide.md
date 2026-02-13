# 从零到 Kaggle Expert：数据科学完整学习指南

> 面向：有扎实编程基础、零数据科学经验的软件工程师
> 目标：系统掌握数据科学核心技能，在 Kaggle 平台获得 Expert 及以上排名
> 预计周期：6-12 个月（每天 1-2 小时）

---

## 目录

- [零、准备工作（开始学习前必读）](#零准备工作开始学习前必读) — 电脑配置、账号、软件、电子书
- [一、知识图谱总览](#一知识图谱总览)
- [二、学习路线（6 个阶段）](#二学习路线6-个阶段)
- [三、阶段一：数学基础速补](#三阶段一数学基础速补2-3-周) → 📝 [练习手册](./stage1-math.md)
- [四、阶段二：Python 数据科学工具链](#四阶段二python-数据科学工具链3-4-周) → 📝 [练习手册](./stage2-tools.md)
- [五、阶段三：机器学习核心理论](#五阶段三机器学习核心理论4-6-周) → 📝 [练习手册](./stage3-ml.md)
- [六、阶段四：Kaggle 实战入门](#六阶段四kaggle-实战入门4-6-周) → 📝 [练习手册](./stage4-kaggle.md)
- [七、阶段五：进阶技能与正式参赛](#七阶段五进阶技能与正式参赛2-4-个月) → 📝 [练习手册](./stage5-advanced.md)
- [八、阶段六：深度学习与专精方向](#八阶段六深度学习与专精方向持续) → 📝 [练习手册](./stage6-deeplearning.md)
- [九、工具与环境配置](#九工具与环境配置)
- [十、英文应对策略](#十英文应对策略)
- [十一、推荐资源清单（中文优先）](#十一推荐资源清单中文优先)
- [十二、学习进度追踪表](#十二学习进度追踪表)

> 📝 **练习手册总目录**：[README.md](./README.md) — 每个知识点都有 Demo + 练习 + 答案
> 🔗 **开源项目学习指南**：[kaggle-opensource-projects.md](./kaggle-opensource-projects.md) — 18 个精选项目 + 功能点 + 学习方法
> 🚀 **案例驱动学习**：[case-based-learning.md](./case-based-learning.md) — 10 个实战案例 + 真实行业问题 + 完整技术方案
> 🏥 **行业专题**：[industry-mental-health-k12.md](./industry-mental-health-k12.md) — 青少年心理健康 × K12 教育 AI 应用全景（22个项目）

---

## 零、准备工作（开始学习前必读）

> 在开始学习之前，把环境和资源全部准备好，避免学到一半被环境问题卡住。

### 0.1 电脑配置要求

```
最低配置（阶段一~四够用）：
  CPU：任意（M1/M2 Mac、Intel i5 以上均可）
  内存：8GB（能跑 Jupyter + 浏览器就行）
  硬盘：20GB 可用空间（装 Python 环境 + 数据集）
  GPU：不需要！前 4 个阶段完全不需要 GPU
  网络：能访问 Kaggle（需要科学上网工具）

推荐配置（阶段五~六更舒适）：
  内存：16GB+（大数据集处理更流畅）
  GPU：仍然不是必须的 —— 用 Kaggle 免费 GPU 即可
  硬盘：50GB+（存放更多数据集和模型）

结论：你现在的 Mac 完全够用。
      深度学习阶段需要 GPU 时，用 Kaggle Notebook 免费 GPU 即可。
      不需要花钱买显卡或云服务器。
```

### 0.2 需要注册的账号

| 账号 | 地址 | 何时需要 | 说明 |
|------|------|---------|------|
| **Kaggle** | kaggle.com | 阶段二开始 | 核心平台，注册后可用免费 GPU Notebook，下载数据集 |
| **GitHub** | github.com | 立刻 | 你应该已有，用来管理代码和学习笔记 |
| **Google 账号** | — | 阶段二 | Kaggle 可用 Google 登录；Colab 需要 Google 账号 |
| 和鲸社区 | kesci.com | 阶段四 | 国内版 Kaggle，中文赛题，可选 |
| 天池 | tianchi.aliyun.com | 阶段四 | 阿里云竞赛平台，可选 |
| Coursera | coursera.org | 阶段三 | 吴恩达课程，可旁听免费（不要证书就不花钱） |

> ⚠️ **科学上网**：Kaggle、Google Colab、YouTube/Coursera 都需要科学上网。
> 如果没有，用国内替代：和鲸社区（替代 Kaggle）、B站（替代 YouTube）。

### 0.3 需要安装的软件

```bash
# ===== 必装（阶段一就需要）=====

# 1. Miniconda（Python 环境管理器）
#    下载地址：https://docs.conda.io/en/latest/miniconda.html
#    Mac 用户选 "Miniconda3 macOS Apple M1 64-bit pkg"（M芯片）
#    或 "Miniconda3 macOS Intel x86 64-bit pkg"（Intel芯片）

# 2. 安装后，在终端执行：
conda create -n kaggle python=3.10 -y
conda activate kaggle

# 3. 安装核心库（一条命令全装好）
pip install numpy pandas matplotlib seaborn scikit-learn jupyterlab

# 4. 启动 Jupyter（你的数据科学 IDE）
jupyter lab
# 会自动打开浏览器，在里面写代码

# ===== 阶段三需要 =====
pip install xgboost lightgbm catboost
pip install optuna
pip install category_encoders

# ===== 阶段六需要（深度学习）=====
pip install torch torchvision
# Mac M 芯片会自动使用 MPS 加速，不需要 CUDA

# ===== 可选但推荐 =====
# VS Code（你应该已有）+ Jupyter 插件
# 浏览器安装"沉浸式翻译"插件
```

### 0.4 验证环境是否安装成功

安装完后，打开 Jupyter Lab，新建一个 Notebook，运行以下代码：

```python
# 复制粘贴到 Jupyter 的第一个 cell，Shift+Enter 运行
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier

print("✅ NumPy 版本:", np.__version__)
print("✅ Pandas 版本:", pd.__version__)
print("✅ Scikit-learn 已安装")
print("✅ Matplotlib 已安装")
print("✅ Seaborn 已安装")
print("\n🎉 环境配置完成！可以开始学习了！")
```

如果全部输出 ✅，说明环境没问题。

### 0.5 推荐电子书（可下载）

以下书籍均有合法免费电子版或官方在线阅读版：

| 书名 | 免费获取方式 | 阶段 | 说明 |
|------|-------------|------|------|
| 《动手学深度学习》 | 官方免费：[d2l.ai/d2l-zh.pdf](https://zh.d2l.ai/) | 阶段六 | 李沐团队，中文原生，PDF/HTML 均可下载 |
| 《Python Data Science Handbook》 | 官方免费：[jakevdp.github.io](https://jakevdp.github.io/PythonDataScienceHandbook/) | 阶段二 | NumPy/Pandas/Matplotlib/Scikit-learn 全覆盖，在线阅读 |
| 《Think Stats》（统计思维） | 官方免费：[greenteapress.com](https://greenteapress.com/thinkstats2/html/index.html) | 阶段一 | 用 Python 学统计，有中文翻译版搜索"统计思维" |
| 《Scikit-Learn 官方文档》 | 官方免费：[scikit-learn.org/stable/user_guide](https://scikit-learn.org/stable/user_guide.html) | 阶段三 | 每个算法都有详细文档+代码示例，当参考手册用 |
| 《Interpretable ML Book》 | 官方免费：[christophm.github.io](https://christophm.github.io/interpretable-ml-book/) | 阶段五 | 可解释机器学习，理解模型为什么这样预测 |
| 《Kaggle Book》 | 需购买/搜索 | 阶段四 | Kaggle Grandmaster 写的竞赛实战指南 |

**需购买但强烈推荐（有中文版）**：

| 中文书名 | 获取方式 | 阶段 | 说明 |
|---------|---------|------|------|
| 《利用Python进行数据分析》第3版 | 微信读书/京东/当当 | 阶段二 | Pandas 作者写的，权威实用 |
| 《机器学习》（西瓜书） | 微信读书有电子版 | 阶段三 | 周志华著，理论经典 |
| 《统计学习方法》第2版 | 微信读书/京东 | 阶段三 | 李航著，算法推导清晰 |
| 《机器学习实战》（Hands-On ML 中文版） | 微信读书/京东 | 阶段三-五 | 实战圣经 |
| 《百面机器学习》 | 微信读书/京东 | 查漏补缺 | 面试+复习神器 |

> 💡 **提示**：微信读书 App 上很多技术书可以免费阅读（用阅读时长兑换），是获取中文电子书的好渠道。

### 0.6 学习用的数据集（提前下载）

| 数据集 | 来源 | 大小 | 用于 |
|-------|------|------|------|
| Titanic | kaggle.com/c/titanic | 60KB | 阶段二-四，入门必做 |
| House Prices | kaggle.com/c/house-prices-advanced-regression-techniques | 200KB | 阶段四，回归练习 |
| Iris（鸢尾花） | Scikit-learn 内置，无需下载 | 极小 | 阶段三，分类入门 |
| Boston Housing | Scikit-learn 内置 | 极小 | 阶段三，回归入门 |
| MNIST（手写数字） | Kaggle / torchvision 内置 | 50MB | 阶段六，深度学习入门 |

> 在 Kaggle Notebook 中直接使用数据集最方便，无需下载。本地练习时用 Scikit-learn 内置数据集即可。

---

## 一、知识图谱总览

数据科学是一个交叉学科，以下是你需要掌握的完整知识版图：

```
数据科学知识图谱
│
├── 1. 数学基础 ──────────────────────────────────
│   ├── 线性代数（向量、矩阵运算、特征值）
│   ├── 概率与统计（分布、假设检验、贝叶斯）
│   └── 微积分（导数、梯度、链式法则）          ← 理解模型原理的地基
│
├── 2. 编程工具链 ────────────────────────────────
│   ├── Python 基础                              ← 你已有，快速过
│   ├── NumPy（数值计算）
│   ├── Pandas（数据处理，最核心！）
│   ├── Matplotlib / Seaborn（可视化）
│   └── Jupyter Notebook（交互式开发环境）
│
├── 3. 数据处理 ──────────────────────────────────
│   ├── 数据清洗（缺失值、异常值、重复值）
│   ├── 特征工程（编码、缩放、构造、选择）       ← Kaggle 制胜关键！
│   ├── 探索性数据分析（EDA）
│   └── 数据可视化与洞察
│
├── 4. 机器学习 ──────────────────────────────────
│   ├── 监督学习
│   │   ├── 分类：逻辑回归、决策树、随机森林、SVM、KNN
│   │   ├── 回归：线性回归、Ridge/Lasso、树模型回归
│   │   └── 集成方法：Bagging、Boosting（XGBoost/LightGBM/CatBoost）
│   ├── 无监督学习
│   │   ├── 聚类：K-Means、DBSCAN、层次聚类
│   │   └── 降维：PCA、t-SNE、UMAP
│   └── 模型评估
│       ├── 交叉验证（K-Fold、Stratified K-Fold）
│       ├── 评估指标（AUC、F1、RMSE、MAE、Log Loss）
│       └── 过拟合 vs 欠拟合
│
├── 5. 深度学习（进阶） ─────────────────────────
│   ├── 神经网络基础（前馈网络、反向传播）
│   ├── CNN（卷积神经网络，图像方向）
│   ├── RNN / LSTM / Transformer（序列/NLP方向）
│   ├── 框架：PyTorch（主流）/ TensorFlow
│   └── 预训练模型与迁移学习
│
├── 6. Kaggle 竞赛技能 ──────────────────────────
│   ├── EDA 与数据理解
│   ├── 高级特征工程
│   ├── 模型调参（Optuna / GridSearch）
│   ├── 模型融合（Stacking / Blending / 加权平均）
│   ├── 后处理技巧
│   └── 避免 Leaderboard 过拟合
│
└── 7. 工程实践 ──────────────────────────────────
    ├── Git 版本管理（你已有）
    ├── 实验追踪（Weights & Biases / MLflow）
    ├── 代码复用与 Pipeline 构建
    └── GPU 使用（Kaggle Notebooks / Colab）
```

---

## 二、学习路线（6 个阶段）

```
时间线（月）
  1        2        3        4        5        6        7+
  |--------|--------|--------|--------|--------|--------|------→
  [阶段1]  [阶段2]  [---阶段3---]  [---阶段4---]  [---阶段5---]  [阶段6...]
  数学速补  工具链    机器学习核心    Kaggle入门     正式参赛拿牌    深度学习
                                    实战            ↑首枚铜牌      专精方向
```

每个阶段的验收标准都很明确，确保你不会"学了很多但什么都做不了"。

---

## 三、阶段一：数学基础速补（2-3 周）

> 目标：不是要你变数学家，而是能**看懂公式、理解模型在干什么**

### 3.1 线性代数（4-5 天）

你需要掌握的概念（不需要证明，需要直觉理解）：

| 概念 | 你需要理解到什么程度 | 为什么需要 |
|------|---------------------|-----------|
| 向量 | 一组有序数字，可以表示数据点的特征 | 每条数据就是一个向量 |
| 矩阵 | 多个向量排列成表，理解矩阵乘法 | 数据集就是矩阵，模型权重也是矩阵 |
| 转置 | 行列互换 | 数据处理中频繁出现 |
| 点积 | 两个向量对应元素相乘再求和 | 神经网络每一层都在做点积 |
| 矩阵乘法 | 理解维度匹配规则和计算过程 | 模型计算的核心操作 |
| 特征值/特征向量 | 矩阵变换中不改变方向的向量 | PCA 降维的数学基础 |
| 范数 | 向量的"长度"，L1/L2 范数 | 正则化的理论基础 |

**学习资源**：
- [3Blue1Brown - 线性代数的本质](https://www.youtube.com/playlist?list=PLZHQObOWTQDPD3MizzM2xVFitgF8hE_ab)（B站有搬运，中文字幕，共 16 集，每集约 15 分钟）
- 看完视频后，用 NumPy 手动实现每个运算，加深理解

**动手练习**：
```python
import numpy as np

# 1. 创建向量和矩阵
v = np.array([1, 2, 3])
M = np.array([[1, 2], [3, 4], [5, 6]])

# 2. 点积
dot_product = np.dot(v, v)  # 理解：结果是什么？为什么？

# 3. 矩阵乘法
A = np.array([[1, 2], [3, 4]])
B = np.array([[5, 6], [7, 8]])
C = A @ B  # 手算验证结果

# 4. 转置
print(M.T)  # 观察维度变化

# 5. 特征值分解
eigenvalues, eigenvectors = np.linalg.eig(A)
```

### 3.2 概率与统计（5-7 天）

| 概念 | 直觉理解 | 为什么需要 |
|------|---------|-----------|
| 均值/中位数/众数 | 数据的"中心"在哪 | EDA 基础 |
| 方差/标准差 | 数据的"分散程度" | 特征缩放、异常检测 |
| 概率分布 | 数据长什么"形状" | 理解数据特性 |
| 正态分布 | 钟形曲线，自然界最常见 | 很多模型假设数据正态分布 |
| 条件概率 | 在已知条件下的概率 | 朴素贝叶斯分类器 |
| 贝叶斯定理 | P(A\|B) = P(B\|A)·P(A) / P(B) | 概率模型的理论基础 |
| 相关系数 | 两个变量的线性关系强度 | 特征选择 |
| 假设检验 | 判断结果是否"偶然的" | 验证模型改进是否显著 |

**学习资源**：
- [StatQuest with Josh Starmer](https://www.youtube.com/c/joshstarmer)（YouTube，讲得极其清楚，每集 5-15 分钟）
- 重点看：Probability、Distributions、Hypothesis Testing 系列

**动手练习**：
```python
import numpy as np
import matplotlib.pyplot as plt

# 1. 生成不同分布的数据并可视化
normal_data = np.random.normal(loc=0, scale=1, size=10000)    # 正态分布
uniform_data = np.random.uniform(low=0, high=1, size=10000)   # 均匀分布

fig, axes = plt.subplots(1, 2, figsize=(12, 4))
axes[0].hist(normal_data, bins=50, title='Normal')
axes[1].hist(uniform_data, bins=50, title='Uniform')

# 2. 计算统计量
print(f"均值: {normal_data.mean():.4f}")
print(f"标准差: {normal_data.std():.4f}")
print(f"中位数: {np.median(normal_data):.4f}")

# 3. 相关系数
x = np.random.normal(0, 1, 100)
y = 2 * x + np.random.normal(0, 0.5, 100)  # y 和 x 有强相关
print(f"相关系数: {np.corrcoef(x, y)[0, 1]:.4f}")
```

### 3.3 微积分（3-4 天）

| 概念 | 直觉理解 | 为什么需要 |
|------|---------|-----------|
| 导数 | 函数在某点的变化率/斜率 | 梯度下降的基础 |
| 偏导数 | 多变量函数对其中一个变量的导数 | 模型有多个参数时需要 |
| 梯度 | 所有偏导数组成的向量，指向函数增长最快的方向 | 梯度下降靠它优化模型 |
| 链式法则 | 复合函数求导：(f∘g)' = f'·g' | 反向传播（深度学习核心）|
| 损失函数 | 衡量模型预测与真实值的差距 | 训练模型就是最小化损失 |
| 梯度下降 | 沿梯度反方向一步步走，找损失函数最小值 | 几乎所有模型的训练方式 |

**学习资源**：
- [3Blue1Brown - 微积分的本质](https://www.youtube.com/playlist?list=PLZHQObOWTQDMsr9K-rj53DwVRMYO3t5Yr)（前 7 集即可）
- [3Blue1Brown - 神经网络](https://www.youtube.com/playlist?list=PLZHQObOWTQDNU6R1_67000Dx_ZCJB-3pi)（4 集，直观理解梯度下降在神经网络中的应用）

**动手练习**：
```python
import numpy as np
import matplotlib.pyplot as plt

# 手动实现梯度下降，找 f(x) = (x-3)^2 的最小值
def f(x):
    return (x - 3) ** 2

def gradient(x):
    return 2 * (x - 3)  # f'(x) = 2(x-3)

x = 0.0  # 初始位置
learning_rate = 0.1
history = [x]

for i in range(20):
    x = x - learning_rate * gradient(x)  # 梯度下降公式
    history.append(x)
    print(f"Step {i+1}: x = {x:.4f}, f(x) = {f(x):.6f}")

# 可视化下降过程
xs = np.linspace(-1, 7, 100)
plt.plot(xs, f(xs))
plt.scatter(history, [f(h) for h in history], c='red', zorder=5)
plt.title("Gradient Descent Visualization")
plt.show()
# 观察：x 是否逐步接近 3？
```

### ✅ 阶段一验收标准

- [ ] 能用 NumPy 进行向量/矩阵运算
- [ ] 能解释正态分布、标准差、相关系数的含义
- [ ] 能用代码实现简单的梯度下降
- [ ] 能看懂含有 Σ（求和）、∂（偏导）符号的基础公式

---

## 四、阶段二：Python 数据科学工具链（3-4 周）

> 目标：熟练使用数据处理和可视化工具，这是你日后 80% 时间在用的东西

### 4.1 Jupyter Notebook（第 1 天）

Jupyter 是数据科学的"IDE"，交互式编程，边写边看结果。

**关键操作**：
- 安装：`pip install jupyterlab`
- 启动：`jupyter lab`
- 快捷键：`Shift+Enter`（运行当前 cell）、`A`/`B`（上方/下方插入 cell）、`M`（切换 Markdown）
- 或者直接用 **Kaggle Notebooks**（在线，免费 GPU，零配置）

### 4.2 NumPy（3-4 天）

NumPy 是一切数据科学库的底层。

**必须掌握的操作**：

```python
import numpy as np

# === 创建 ===
a = np.array([1, 2, 3])              # 从列表创建
b = np.zeros((3, 4))                  # 全零矩阵
c = np.ones((2, 3))                   # 全一矩阵
d = np.random.randn(3, 4)             # 标准正态随机矩阵
e = np.arange(0, 10, 2)               # [0, 2, 4, 6, 8]
f = np.linspace(0, 1, 5)              # [0, 0.25, 0.5, 0.75, 1]

# === 形状操作 ===
print(d.shape)                         # (3, 4) — 查看维度
d_reshaped = d.reshape(4, 3)           # 改变形状
d_flat = d.flatten()                   # 拉平为一维

# === 索引与切片 ===
arr = np.array([[1,2,3],[4,5,6],[7,8,9]])
print(arr[0, :])                       # 第一行: [1, 2, 3]
print(arr[:, 1])                       # 第二列: [2, 5, 8]
print(arr[arr > 5])                    # 条件筛选: [6, 7, 8, 9]

# === 数学运算 ===
x = np.array([1, 2, 3])
y = np.array([4, 5, 6])
print(x + y)                           # 逐元素加: [5, 7, 9]
print(x * y)                           # 逐元素乘: [4, 10, 18]
print(np.dot(x, y))                    # 点积: 32
print(x @ y)                           # 点积（等价写法）

# === 聚合 ===
print(arr.sum())                       # 总和
print(arr.mean(axis=0))                # 每列均值
print(arr.std(axis=1))                 # 每行标准差
print(arr.max(), arr.argmax())         # 最大值，最大值的索引

# === 广播（Broadcasting）===
# NumPy 自动对齐不同形状的数组进行运算
matrix = np.ones((3, 4))
row_vector = np.array([1, 2, 3, 4])
result = matrix + row_vector            # (3,4) + (4,) → 自动扩展为 (3,4)
```

### 4.3 Pandas（7-10 天）⭐ 最重要

Pandas 是你处理表格数据的核心武器。**花最多时间在这里**。

**4.3.1 核心数据结构**：

```python
import pandas as pd

# Series: 一维带标签数组
s = pd.Series([10, 20, 30], index=['a', 'b', 'c'])

# DataFrame: 二维表格（最常用）
df = pd.DataFrame({
    'name': ['Alice', 'Bob', 'Charlie'],
    'age': [25, 30, 35],
    'salary': [50000, 60000, 70000]
})
```

**4.3.2 数据读写**：

```python
# 读取
df = pd.read_csv('data.csv')               # CSV 文件
df = pd.read_excel('data.xlsx')             # Excel
df = pd.read_parquet('data.parquet')        # Parquet（大文件更快）

# 写出
df.to_csv('output.csv', index=False)
```

**4.3.3 数据探索（EDA 基础）**：

```python
df.head(10)           # 前 10 行
df.tail(5)            # 后 5 行
df.shape              # (行数, 列数)
df.dtypes             # 每列的数据类型
df.info()             # 列信息概览（类型、非空数量）
df.describe()         # 数值列的统计摘要（均值、标准差、分位数等）
df.isnull().sum()     # 每列缺失值数量
df.nunique()          # 每列唯一值数量
df['col'].value_counts()  # 某列的值分布
```

**4.3.4 数据选择与过滤**：

```python
# 选择列
df['age']                      # 单列（返回 Series）
df[['name', 'age']]            # 多列（返回 DataFrame）

# 条件过滤
df[df['age'] > 28]             # age > 28 的行
df[(df['age'] > 25) & (df['salary'] < 70000)]  # 组合条件（用 & |）

# loc（标签索引）和 iloc（位置索引）
df.loc[0:2, 'name':'age']     # 按标签范围选择
df.iloc[0:2, 0:2]             # 按位置范围选择
```

**4.3.5 数据处理（高频操作）**：

```python
# --- 缺失值处理 ---
df.dropna()                            # 删除含缺失值的行
df.fillna(0)                           # 用 0 填充
df['age'].fillna(df['age'].median())   # 用中位数填充（常用）

# --- 类型转换 ---
df['age'] = df['age'].astype(int)

# --- 新建列 ---
df['age_group'] = df['age'].apply(lambda x: 'young' if x < 30 else 'senior')
df['salary_k'] = df['salary'] / 1000

# --- 排序 ---
df.sort_values('salary', ascending=False)

# --- 去重 ---
df.drop_duplicates()
df.drop_duplicates(subset=['name'])

# --- 重命名列 ---
df.rename(columns={'name': 'full_name'}, inplace=True)

# --- 删除列 ---
df.drop(columns=['salary_k'], inplace=True)
```

**4.3.6 分组聚合（GroupBy）**：

```python
# 类似 SQL 的 GROUP BY
df.groupby('age_group')['salary'].mean()          # 每组平均薪资
df.groupby('age_group').agg({
    'salary': ['mean', 'median', 'std'],
    'age': 'count'
})
```

**4.3.7 表合并**：

```python
# merge（类似 SQL JOIN）
df1 = pd.DataFrame({'id': [1,2,3], 'name': ['A','B','C']})
df2 = pd.DataFrame({'id': [2,3,4], 'score': [80,90,70]})

pd.merge(df1, df2, on='id', how='inner')  # 内连接
pd.merge(df1, df2, on='id', how='left')   # 左连接
pd.merge(df1, df2, on='id', how='outer')  # 外连接

# concat（上下拼接）
pd.concat([df1_part1, df1_part2], axis=0)  # 纵向拼接
pd.concat([df_a, df_b], axis=1)            # 横向拼接
```

### 4.4 Matplotlib + Seaborn（4-5 天）

可视化是理解数据的眼睛。

```python
import matplotlib.pyplot as plt
import seaborn as sns

# --- Matplotlib 基础 ---

# 折线图
plt.figure(figsize=(10, 6))
plt.plot(x, y, label='trend')
plt.xlabel('X axis')
plt.ylabel('Y axis')
plt.title('My Plot')
plt.legend()
plt.show()

# 多子图
fig, axes = plt.subplots(1, 3, figsize=(15, 5))
axes[0].hist(data, bins=30)
axes[1].scatter(x, y)
axes[2].bar(categories, values)

# --- Seaborn（更美观、更适合统计可视化）---

# 分布图
sns.histplot(df['age'], kde=True)          # 直方图 + 密度曲线

# 箱线图（查看异常值）
sns.boxplot(x='category', y='value', data=df)

# 散点图（查看两变量关系）
sns.scatterplot(x='age', y='salary', hue='gender', data=df)

# 热力图（查看相关性矩阵，非常常用！）
correlation = df.select_dtypes(include='number').corr()
sns.heatmap(correlation, annot=True, cmap='coolwarm', center=0)

# 计数图
sns.countplot(x='category', data=df)

# 成对关系图（一次看所有变量间关系）
sns.pairplot(df, hue='target')
```

**必背的可视化选择指南**：

| 想看什么 | 用什么图 |
|---------|---------|
| 单变量分布 | `histplot` / `kdeplot` |
| 分类变量计数 | `countplot` / `bar` |
| 两个数值变量关系 | `scatterplot` |
| 分组比较 | `boxplot` / `violinplot` |
| 相关性 | `heatmap` |
| 异常值检测 | `boxplot` |
| 时间趋势 | `lineplot` |

### ✅ 阶段二验收标准

- [ ] 能用 Pandas 读取 CSV，完成数据清洗、缺失值处理、分组聚合
- [ ] 能对任意数据集做基本的 EDA（5 分钟内产出关键统计量和可视化）
- [ ] 能画出热力图、分布图、散点图、箱线图
- [ ] **实战验证**：下载 Kaggle 上的 [Titanic 数据集](https://www.kaggle.com/c/titanic)，完成 EDA 分析

---

## 五、阶段三：机器学习核心理论（4-6 周）

> 目标：理解主流 ML 算法的原理、适用场景、优缺点，能用 Scikit-learn 实现

### 5.1 机器学习核心概念（第 1 周）

**5.1.1 什么是机器学习？**

```
传统编程: 输入 + 规则 → 输出
机器学习: 输入 + 输出 → 规则（模型从数据中学习规则）
```

**5.1.2 三大类型**：

| 类型 | 定义 | 例子 |
|------|------|------|
| 监督学习 | 有标签（正确答案），学习输入→输出的映射 | 垃圾邮件分类、房价预测 |
| 无监督学习 | 无标签，发现数据中的结构/模式 | 客户分群、异常检测 |
| 强化学习 | 通过奖惩反馈学习决策 | 游戏AI、机器人（Kaggle 少用）|

**5.1.3 核心术语**：

| 术语 | 含义 | 类比 |
|------|------|------|
| 特征（Feature） | 输入变量 | 考试的题目 |
| 标签（Label/Target） | 要预测的输出 | 考试的答案 |
| 训练集（Training Set） | 用来训练模型的数据 | 平时的练习题 |
| 验证集（Validation Set） | 调参时评估模型的数据 | 模拟考试 |
| 测试集（Test Set） | 最终评估的数据（模型没见过的） | 正式考试 |
| 过拟合（Overfitting） | 训练集上表现好，新数据上差 | 死记硬背，不会灵活运用 |
| 欠拟合（Underfitting） | 训练集上就表现不好 | 没好好学习 |
| 超参数（Hyperparameter） | 人为设定的模型配置 | 学习时间、学习方法的选择 |

**5.1.4 机器学习工作流程**：

```
1. 定义问题 → 这是分类还是回归？评估指标是什么？
2. 收集数据 → Kaggle 提供好的数据集
3. EDA → 理解数据的分布、缺失值、异常值
4. 特征工程 → 清洗、编码、构造新特征
5. 选择模型 → 从简单开始（逻辑回归/决策树）
6. 训练模型 → model.fit(X_train, y_train)
7. 评估模型 → 用验证集评估，交叉验证
8. 调参优化 → 调整超参数提升性能
9. 模型融合 → 多个模型的预测结合
10. 提交预测 → 在测试集上生成预测结果
```

### 5.2 分类算法（第 2 周）

**5.2.1 逻辑回归（Logistic Regression）**

> 尽管名字叫"回归"，它是一个**分类**算法

- **原理**：线性组合 + Sigmoid 函数将输出压缩到 [0, 1] 作为概率
- **公式**：P(y=1|x) = 1 / (1 + e^(-(w·x + b)))
- **优点**：简单、可解释、训练快、不容易过拟合
- **缺点**：只能处理线性可分问题
- **适用**：二分类问题的 baseline

```python
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report

# 准备数据
X_train, X_val, y_train, y_val = train_test_split(X, y, test_size=0.2, random_state=42)

# 训练
model = LogisticRegression(max_iter=1000)
model.fit(X_train, y_train)

# 预测与评估
y_pred = model.predict(X_val)
print(classification_report(y_val, y_pred))
```

**5.2.2 决策树（Decision Tree）**

- **原理**：通过一系列 if-else 规则将数据分成越来越纯的子集
- **分裂标准**：信息增益（Entropy）或 基尼系数（Gini）
- **优点**：直观可解释、无需特征缩放、能处理非线性
- **缺点**：容易过拟合（树太深）
- **关键超参数**：`max_depth`、`min_samples_split`、`min_samples_leaf`

```python
from sklearn.tree import DecisionTreeClassifier
from sklearn import tree

model = DecisionTreeClassifier(max_depth=5, random_state=42)
model.fit(X_train, y_train)

# 可视化决策树
plt.figure(figsize=(20, 10))
tree.plot_tree(model, feature_names=feature_names, filled=True, rounded=True)
plt.show()
```

**5.2.3 随机森林（Random Forest）**

- **原理**：训练多棵决策树，每棵用不同的数据子集和特征子集，最终投票
- **为什么有效**：多棵"弱"树的集合比单棵"强"树更稳定（"三个臭皮匠"）
- **优点**：不容易过拟合、能评估特征重要性、无需太多调参
- **缺点**：训练慢、不可解释
- **关键超参数**：`n_estimators`（树的数量）、`max_depth`、`max_features`

```python
from sklearn.ensemble import RandomForestClassifier

model = RandomForestClassifier(n_estimators=200, max_depth=10, random_state=42)
model.fit(X_train, y_train)

# 特征重要性（非常有用！）
importances = pd.Series(model.feature_importances_, index=feature_names)
importances.sort_values(ascending=True).plot(kind='barh')
```

**5.2.4 XGBoost / LightGBM / CatBoost（Boosting 三大件）** ⭐⭐⭐

> 这是 Kaggle 表格竞赛的绝对王者，必须精通

- **原理**：多棵树**顺序**训练，每棵新树专注于修正前面树的错误
- **与随机森林的区别**：随机森林是并行（Bagging），Boosting 是串行

| 库 | 特点 | 何时用 |
|----|------|-------|
| XGBoost | 最经典，社区最大 | 通用首选 |
| LightGBM | 更快，内存更省，直方图优化 | 大数据集首选 |
| CatBoost | 原生支持类别特征，不用手动编码 | 类别特征多时首选 |

```python
import xgboost as xgb
from sklearn.model_selection import cross_val_score

model = xgb.XGBClassifier(
    n_estimators=500,         # 树的数量
    max_depth=6,              # 每棵树最大深度
    learning_rate=0.1,        # 学习率（步长）
    subsample=0.8,            # 每棵树用 80% 的数据
    colsample_bytree=0.8,     # 每棵树用 80% 的特征
    reg_alpha=0.1,            # L1 正则化
    reg_lambda=1.0,           # L2 正则化
    random_state=42,
    eval_metric='logloss'
)

# 交叉验证评估
scores = cross_val_score(model, X, y, cv=5, scoring='accuracy')
print(f"CV Accuracy: {scores.mean():.4f} ± {scores.std():.4f}")

# LightGBM
import lightgbm as lgb

model_lgb = lgb.LGBMClassifier(
    n_estimators=500,
    max_depth=6,
    learning_rate=0.1,
    num_leaves=31,            # LightGBM 特有参数
    random_state=42
)
```

### 5.3 回归算法（第 3 周前半）

| 算法 | 原理 | 关键点 |
|------|------|-------|
| 线性回归 | y = w·x + b，最小化 MSE | 最简单的 baseline |
| Ridge (L2) | 线性回归 + L2 正则化（惩罚大系数） | 防过拟合，系数变小但不为零 |
| Lasso (L1) | 线性回归 + L1 正则化 | 自动特征选择，部分系数变为零 |
| ElasticNet | L1 + L2 混合 | 综合两者优点 |
| 树模型回归 | XGBoost/LightGBM 的回归版本 | 大多数情况下的最优选择 |

```python
from sklearn.linear_model import LinearRegression, Ridge, Lasso
from sklearn.metrics import mean_squared_error, mean_absolute_error
import numpy as np

# 训练
model = Ridge(alpha=1.0)
model.fit(X_train, y_train)

# 评估
y_pred = model.predict(X_val)
rmse = np.sqrt(mean_squared_error(y_val, y_pred))
mae = mean_absolute_error(y_val, y_pred)
print(f"RMSE: {rmse:.4f}, MAE: {mae:.4f}")
```

### 5.4 模型评估与验证（第 3 周后半）⭐⭐⭐

> 这部分至关重要！在 Kaggle 上，正确的验证策略决定你能否拿牌

**5.4.1 分类评估指标**：

| 指标 | 公式/含义 | 何时用 |
|------|---------|-------|
| Accuracy | 正确预测数 / 总数 | 类别平衡时 |
| Precision | TP / (TP + FP)，"预测为正的有多少是对的" | 关注误报成本时（如垃圾邮件） |
| Recall | TP / (TP + FN)，"真正为正的有多少被找到了" | 关注漏报成本时（如疾病诊断） |
| F1 Score | 2 × (P × R) / (P + R)，P 和 R 的调和平均 | P 和 R 都重要时 |
| AUC-ROC | ROC 曲线下面积 | 二分类最常用，不受阈值影响 |
| Log Loss | 交叉熵损失 | 关注概率预测质量时 |

**5.4.2 回归评估指标**：

| 指标 | 含义 | 注意 |
|------|------|------|
| MSE | 均方误差，对大误差惩罚更重 | 对异常值敏感 |
| RMSE | MSE 开根号，单位和目标一致 | 最常用 |
| MAE | 平均绝对误差 | 对异常值更鲁棒 |
| R² | 模型解释了多少方差，1 为完美 | 可以为负（比均值预测还差） |

**5.4.3 交叉验证**：

```
为什么需要交叉验证？
- 单次 train/val 划分，结果受划分方式影响大
- 交叉验证多次划分取平均，更稳定可靠

K-Fold 交叉验证（以 5-Fold 为例）：
Fold 1: [VAL][TRAIN][TRAIN][TRAIN][TRAIN] → Score 1
Fold 2: [TRAIN][VAL][TRAIN][TRAIN][TRAIN] → Score 2
Fold 3: [TRAIN][TRAIN][VAL][TRAIN][TRAIN] → Score 3
Fold 4: [TRAIN][TRAIN][TRAIN][VAL][TRAIN] → Score 4
Fold 5: [TRAIN][TRAIN][TRAIN][TRAIN][VAL] → Score 5
最终得分 = 5 个 Score 的平均值
```

```python
from sklearn.model_selection import (
    KFold, StratifiedKFold, cross_val_score
)

# 基本 K-Fold
kf = KFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(model, X, y, cv=kf, scoring='accuracy')

# 分层 K-Fold（分类问题必用，保持每折中类别比例一致）
skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores = cross_val_score(model, X, y, cv=skf, scoring='roc_auc')

print(f"CV Score: {scores.mean():.4f} ± {scores.std():.4f}")
```

### 5.5 特征工程（第 4 周）⭐⭐⭐⭐⭐

> **Kaggle 常言：特征工程决定上限，模型选择决定下限**

**5.5.1 类别特征编码**：

| 方法 | 说明 | 适用 |
|------|------|------|
| Label Encoding | 类别→整数（A=0, B=1, C=2） | 有序类别，或树模型 |
| One-Hot Encoding | 每个类别变成一列（0/1） | 无序类别，线性模型 |
| Target Encoding | 类别→该类别下目标变量的均值 | 高基数类别（城市名等） |
| Frequency Encoding | 类别→出现频率 | 简单有效 |

```python
from sklearn.preprocessing import LabelEncoder, OneHotEncoder
import category_encoders as ce

# Label Encoding
le = LabelEncoder()
df['color_encoded'] = le.fit_transform(df['color'])

# One-Hot Encoding
df_encoded = pd.get_dummies(df, columns=['color'], drop_first=True)

# Target Encoding（需要在交叉验证内做，防止数据泄漏！）
te = ce.TargetEncoder(cols=['city'])
df['city_encoded'] = te.fit_transform(df['city'], df['target'])
```

**5.5.2 数值特征处理**：

```python
from sklearn.preprocessing import StandardScaler, MinMaxScaler

# 标准化（均值为0，标准差为1）— 线性模型、SVM、KNN 需要
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X_train)

# 归一化（缩放到 [0, 1]）
scaler = MinMaxScaler()
X_normalized = scaler.fit_transform(X_train)

# 对数变换（处理右偏分布）
df['log_price'] = np.log1p(df['price'])  # log1p = log(1+x)，避免 log(0)

# 分箱
df['age_bin'] = pd.cut(df['age'], bins=[0, 18, 35, 60, 100],
                        labels=['youth', 'adult', 'middle', 'senior'])
```

**5.5.3 特征构造（创造力的核心）**：

```python
# 日期特征
df['year'] = df['date'].dt.year
df['month'] = df['date'].dt.month
df['dayofweek'] = df['date'].dt.dayofweek
df['is_weekend'] = df['dayofweek'].isin([5, 6]).astype(int)

# 交互特征
df['area'] = df['length'] * df['width']
df['price_per_sqft'] = df['price'] / df['area']
df['age_salary_ratio'] = df['age'] / df['salary']

# 聚合特征（按组统计）
group_stats = df.groupby('category')['price'].agg(['mean', 'std', 'max', 'min'])
group_stats.columns = ['cat_price_mean', 'cat_price_std', 'cat_price_max', 'cat_price_min']
df = df.merge(group_stats, on='category', how='left')

# 文本特征
df['text_length'] = df['text'].str.len()
df['word_count'] = df['text'].str.split().str.len()
```

**5.5.4 特征选择**：

```python
# 方法1：基于模型的特征重要性
model = lgb.LGBMClassifier()
model.fit(X, y)
importance = pd.Series(model.feature_importances_, index=X.columns)
top_features = importance.nlargest(20).index.tolist()

# 方法2：相关性过滤（删除高相关特征，减少冗余）
corr_matrix = X.corr().abs()
upper = corr_matrix.where(np.triu(np.ones(corr_matrix.shape), k=1).astype(bool))
to_drop = [col for col in upper.columns if any(upper[col] > 0.95)]

# 方法3：方差过滤（删除方差极低的特征）
from sklearn.feature_selection import VarianceThreshold
selector = VarianceThreshold(threshold=0.01)
X_selected = selector.fit_transform(X)
```

### ✅ 阶段三验收标准

- [ ] 能解释逻辑回归、决策树、随机森林、XGBoost 的原理和区别
- [ ] 能用 Scikit-learn 完成完整的训练→评估→交叉验证流程
- [ ] 能根据问题类型选择正确的评估指标
- [ ] 能做特征编码、缩放、构造、选择
- [ ] **实战验证**：在 Titanic 竞赛中提交一个完整方案，排名进入前 25%

---

## 六、阶段四：Kaggle 实战入门（4-6 周）

> 目标：通过经典竞赛熟悉完整竞赛流程，建立自己的代码模板

### 6.1 竞赛实战路线

按顺序完成以下 3 个经典入门赛：

**第 1 个：[Titanic](https://www.kaggle.com/c/titanic)（二分类）** — 2 周

```
你要预测：乘客是否幸存
数据特点：小数据集（891 条），特征简单
学习重点：
  ├── 完整 EDA 流程
  ├── 缺失值处理（Age、Embarked、Cabin）
  ├── 特征工程（Title 提取、Family Size、IsAlone）
  ├── 类别编码（Sex、Embarked）
  ├── 模型训练（从逻辑回归到 XGBoost）
  └── 提交流程
```

步骤：
1. 先自己做一版最简单的（只用 Sex + Pclass）
2. 看 Top Notebooks（搜索 "Titanic EDA"），学习别人的特征工程
3. 逐步加特征、换模型，观察分数变化
4. 目标：准确率 > 0.79（前 15%）

**第 2 个：[House Prices](https://www.kaggle.com/c/house-prices-advanced-regression-techniques)（回归）** — 2 周

```
你要预测：房屋售价
数据特点：79 个特征，包含大量类别特征和缺失值
学习重点：
  ├── 回归问题的处理流程
  ├── 大量缺失值的策略
  ├── 类别特征编码（高基数处理）
  ├── 目标变量变换（log 变换使分布更正态）
  ├── Ridge/Lasso 与树模型对比
  └── 模型融合初体验
```

**第 3 个：[Spaceship Titanic](https://www.kaggle.com/competitions/spaceship-titanic)（二分类，更现代）** — 2 周

```
你要预测：乘客是否被传送到另一个维度
数据特点：较新的竞赛，社区活跃
学习重点：
  ├── 更复杂的特征工程
  ├── 模型调参（Optuna）
  └── 模型融合实战
```

### 6.2 竞赛代码模板

以下是一个可复用的 Kaggle 竞赛框架：

```python
# ============================================================
# Kaggle Competition Template
# ============================================================
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from sklearn.model_selection import StratifiedKFold
from sklearn.metrics import accuracy_score
import lightgbm as lgb
import warnings
warnings.filterwarnings('ignore')

# === 1. 加载数据 ===
train = pd.read_csv('/kaggle/input/competition-name/train.csv')
test = pd.read_csv('/kaggle/input/competition-name/test.csv')
submission = pd.read_csv('/kaggle/input/competition-name/sample_submission.csv')

print(f"Train shape: {train.shape}, Test shape: {test.shape}")

# === 2. EDA（快速版）===
print(train.head())
print(train.describe())
print(train.isnull().sum())
print(train['target'].value_counts(normalize=True))

# === 3. 特征工程 ===
def feature_engineering(df):
    """对 train 和 test 做相同处理"""
    df = df.copy()
    # ... 你的特征工程代码 ...
    return df

train = feature_engineering(train)
test = feature_engineering(test)

# === 4. 准备建模数据 ===
TARGET = 'target'
FEATURES = [col for col in train.columns if col not in [TARGET, 'id']]

X = train[FEATURES]
y = train[TARGET]
X_test = test[FEATURES]

# === 5. 交叉验证训练 ===
N_FOLDS = 5
oof_preds = np.zeros(len(X))       # Out-of-Fold 预测
test_preds = np.zeros(len(X_test))
scores = []

skf = StratifiedKFold(n_splits=N_FOLDS, shuffle=True, random_state=42)

for fold, (train_idx, val_idx) in enumerate(skf.split(X, y)):
    print(f"\n--- Fold {fold + 1}/{N_FOLDS} ---")

    X_tr, X_val = X.iloc[train_idx], X.iloc[val_idx]
    y_tr, y_val = y.iloc[train_idx], y.iloc[val_idx]

    model = lgb.LGBMClassifier(
        n_estimators=1000,
        max_depth=6,
        learning_rate=0.05,
        num_leaves=31,
        subsample=0.8,
        colsample_bytree=0.8,
        random_state=42,
        verbose=-1
    )

    model.fit(
        X_tr, y_tr,
        eval_set=[(X_val, y_val)],
        callbacks=[lgb.early_stopping(50), lgb.log_evaluation(100)]
    )

    oof_preds[val_idx] = model.predict(X_val)
    test_preds += model.predict_proba(X_test)[:, 1] / N_FOLDS

    score = accuracy_score(y_val, oof_preds[val_idx])
    scores.append(score)
    print(f"Fold {fold + 1} Score: {score:.4f}")

print(f"\nOverall CV Score: {np.mean(scores):.4f} ± {np.std(scores):.4f}")

# === 6. 生成提交文件 ===
submission[TARGET] = (test_preds > 0.5).astype(int)
submission.to_csv('submission.csv', index=False)
print(f"\nSubmission shape: {submission.shape}")
print(submission[TARGET].value_counts())
```

### 6.3 高效学习 Notebook 的方法

在 Kaggle 上看别人的高分 Notebook 是提升最快的方式：

1. **搜索策略**：进入竞赛页 → Code 标签 → 按 "Most Votes" 排序
2. **阅读顺序**：先看 EDA 类 Notebook → 再看完整 Pipeline → 最后看 Top Solution
3. **如何读**：
   - 第一遍：通读，理解整体思路
   - 第二遍：重点看特征工程部分（这是差异化的核心）
   - 第三遍：Fork 到自己账号，修改参数/特征，观察分数变化
4. **做笔记**：每个竞赛记录"学到的新技巧"

### ✅ 阶段四验收标准

- [ ] 完成 3 个入门竞赛，每个至少提交 3 个不同版本
- [ ] 有自己的可复用竞赛代码模板
- [ ] 能独立完成 EDA → 特征工程 → 建模 → 提交 全流程
- [ ] 至少阅读过 10 个高质量 Notebook 并记录笔记

---

## 七、阶段五：进阶技能与正式参赛（2-4 个月）

> 目标：参加活跃竞赛，拿到首枚铜牌，冲击 Expert

### 7.1 超参调优

```python
import optuna

def objective(trial):
    params = {
        'n_estimators': trial.suggest_int('n_estimators', 100, 2000),
        'max_depth': trial.suggest_int('max_depth', 3, 12),
        'learning_rate': trial.suggest_float('learning_rate', 0.01, 0.3, log=True),
        'num_leaves': trial.suggest_int('num_leaves', 15, 127),
        'subsample': trial.suggest_float('subsample', 0.5, 1.0),
        'colsample_bytree': trial.suggest_float('colsample_bytree', 0.5, 1.0),
        'reg_alpha': trial.suggest_float('reg_alpha', 1e-8, 10.0, log=True),
        'reg_lambda': trial.suggest_float('reg_lambda', 1e-8, 10.0, log=True),
        'min_child_samples': trial.suggest_int('min_child_samples', 5, 100),
    }

    model = lgb.LGBMClassifier(**params, random_state=42, verbose=-1)
    scores = cross_val_score(model, X, y, cv=skf, scoring='roc_auc')
    return scores.mean()

study = optuna.create_study(direction='maximize')
study.optimize(objective, n_trials=100, show_progress_bar=True)

print(f"Best Score: {study.best_value:.4f}")
print(f"Best Params: {study.best_params}")
```

### 7.2 模型融合（Ensemble）

> 融合是从银牌到金牌的关键跨越

**7.2.1 加权平均（最简单）**：

```python
# 三个模型的预测概率
pred_lgb = model_lgb.predict_proba(X_test)[:, 1]
pred_xgb = model_xgb.predict_proba(X_test)[:, 1]
pred_cat = model_cat.predict_proba(X_test)[:, 1]

# 加权平均
final_pred = 0.4 * pred_lgb + 0.35 * pred_xgb + 0.25 * pred_cat
```

**7.2.2 Stacking（更强大）**：

```
原理：
第一层：多个不同模型各自做交叉验证预测
第二层：用第一层的预测结果作为特征，训练一个元模型

Level 0: 原始特征 X
         ↓
Level 1: [LightGBM预测, XGBoost预测, CatBoost预测, Ridge预测]
         ↓
Level 2: LogisticRegression(Level 1 的输出) → 最终预测
```

```python
from sklearn.ensemble import StackingClassifier

estimators = [
    ('lgb', lgb.LGBMClassifier(n_estimators=500, verbose=-1)),
    ('xgb', xgb.XGBClassifier(n_estimators=500, verbosity=0)),
    ('rf', RandomForestClassifier(n_estimators=200)),
]

stacking_model = StackingClassifier(
    estimators=estimators,
    final_estimator=LogisticRegression(),
    cv=5
)
stacking_model.fit(X_train, y_train)
```

### 7.3 高级特征工程

```python
# === Target Encoding（安全版，防泄漏）===
from sklearn.model_selection import KFold

def safe_target_encoding(train_df, test_df, col, target, n_folds=5):
    """在交叉验证内做 Target Encoding"""
    train_df[f'{col}_te'] = 0
    kf = KFold(n_splits=n_folds, shuffle=True, random_state=42)

    for tr_idx, val_idx in kf.split(train_df):
        mean_target = train_df.iloc[tr_idx].groupby(col)[target].mean()
        train_df.loc[val_idx, f'{col}_te'] = train_df.iloc[val_idx][col].map(mean_target)

    global_mean = train_df.groupby(col)[target].mean()
    test_df[f'{col}_te'] = test_df[col].map(global_mean)
    return train_df, test_df

# === 频率编码 ===
freq = train['category'].value_counts(normalize=True)
train['category_freq'] = train['category'].map(freq)
test['category_freq'] = test['category'].map(freq)

# === 交叉统计特征 ===
for col1, col2 in [('feature_a', 'feature_b'), ('feature_c', 'feature_d')]:
    train[f'{col1}_{col2}_sum'] = train[col1] + train[col2]
    train[f'{col1}_{col2}_diff'] = train[col1] - train[col2]
    train[f'{col1}_{col2}_product'] = train[col1] * train[col2]
    train[f'{col1}_{col2}_ratio'] = train[col1] / (train[col2] + 1e-8)
```

### 7.4 竞赛策略

```
时间分配（以一个 2 月期竞赛为例）：
Week 1:    读题 + EDA + 理解数据 + 简单 baseline 提交
Week 2-3:  深度特征工程 + 尝试不同模型
Week 4-5:  超参调优 + 特征筛选
Week 6-7:  模型融合 + 后处理
Week 8:    最终选择提交 + 检查数据泄漏

关键原则：
1. 一定要有可靠的 CV（本地验证）策略，不要盲目信 LB
2. 先用简单模型建 baseline，再逐步加复杂度
3. 每次只改一个变量，记录实验结果
4. Discussion 区是金矿，其他选手会分享数据洞察
5. 最后 2 天不要大改，风险太高
```

### ✅ 阶段五验收标准

- [ ] 能使用 Optuna 进行超参调优
- [ ] 能实现加权平均和 Stacking 融合
- [ ] 参加至少 1 个活跃竞赛并完整参与至结束
- [ ] 获得首枚铜牌（前 40%）
- [ ] 达到 Kaggle **Expert** 排名（需 2 枚铜牌）

---

## 八、阶段六：深度学习与专精方向（持续）

> 到了这个阶段，你已经是一个合格的数据科学家了。接下来选择方向深耕。

### 8.1 深度学习基础（2-4 周）

| 概念 | 含义 | 优先级 |
|------|------|-------|
| 神经网络 | 多层感知机（MLP），万能函数逼近器 | ⭐⭐⭐⭐⭐ |
| 反向传播 | 链式法则计算梯度，更新权重 | ⭐⭐⭐⭐⭐ |
| 激活函数 | ReLU、Sigmoid、Tanh、GELU | ⭐⭐⭐⭐ |
| 损失函数 | CrossEntropy、MSE、BCE | ⭐⭐⭐⭐ |
| 优化器 | SGD、Adam、AdamW | ⭐⭐⭐⭐ |
| Batch Normalization | 加速训练、稳定梯度 | ⭐⭐⭐ |
| Dropout | 随机丢弃神经元，防过拟合 | ⭐⭐⭐ |
| 学习率调度 | CosineAnnealing、Warmup | ⭐⭐⭐ |

**框架选择：PyTorch**（Kaggle 社区主流）

```python
import torch
import torch.nn as nn

class SimpleNet(nn.Module):
    def __init__(self, input_dim, hidden_dim, output_dim):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(input_dim, hidden_dim),
            nn.BatchNorm1d(hidden_dim),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(hidden_dim, hidden_dim // 2),
            nn.BatchNorm1d(hidden_dim // 2),
            nn.ReLU(),
            nn.Dropout(0.2),
            nn.Linear(hidden_dim // 2, output_dim),
        )

    def forward(self, x):
        return self.net(x)

# 训练循环
model = SimpleNet(input_dim=50, hidden_dim=128, output_dim=1)
optimizer = torch.optim.AdamW(model.parameters(), lr=1e-3, weight_decay=1e-5)
criterion = nn.BCEWithLogitsLoss()

for epoch in range(10):
    model.train()
    for batch_X, batch_y in train_loader:
        optimizer.zero_grad()
        output = model(batch_X).squeeze()
        loss = criterion(output, batch_y.float())
        loss.backward()
        optimizer.step()
```

### 8.2 方向选择

| 方向 | 关键技术 | 典型 Kaggle 竞赛 |
|------|---------|-----------------|
| **表格数据**（推荐起步） | XGBoost/LightGBM、特征工程 | 大部分 Featured 竞赛 |
| **计算机视觉（CV）** | CNN、ResNet、EfficientNet、数据增强 | 图像分类、目标检测、分割 |
| **自然语言处理（NLP）** | Transformer、BERT、LLM微调 | 文本分类、NER、QA |
| **时间序列** | ARIMA、Prophet、LSTM、时序特征 | 销售预测、能源预测 |

### 8.3 从 Expert 到 Master 的关键能力

1. **创新特征工程** — 别人想不到的特征构造
2. **多模型融合** — 5+ 个差异化模型的 Stacking
3. **领域知识** — 理解竞赛问题的业务背景
4. **代码效率** — 处理大规模数据的工程能力
5. **论文复现** — 快速实现最新论文中的方法

---

## 九、工具与环境配置

### 9.1 本地环境

```bash
# 1. 安装 Miniconda（Python 环境管理）
# 下载：https://docs.conda.io/en/latest/miniconda.html

# 2. 创建数据科学环境
conda create -n ds python=3.10
conda activate ds

# 3. 安装核心库
pip install numpy pandas matplotlib seaborn scikit-learn
pip install xgboost lightgbm catboost
pip install optuna
pip install jupyterlab

# 4. 深度学习（阶段六再装）
pip install torch torchvision  # CPU 版本
# GPU 版本参考 https://pytorch.org/get-started/locally/
```

### 9.2 Kaggle Notebook（推荐起步阶段使用）

- 免费 GPU：T4（每周 30 小时）
- 免费 TPU：v3-8（每周 20 小时）
- 预装所有常用库
- 可以直接访问竞赛数据
- 地址：https://www.kaggle.com/code

### 9.3 Google Colab（备选）

- 免费 GPU（有使用限制）
- 与 Google Drive 集成
- 地址：https://colab.research.google.com

---

## 十、英文应对策略

> 实话说：Kaggle 是英文平台，竞赛题目、Notebook、Discussion 全是英文。
> 但别担心，数据科学的英文是"技术英文"，词汇量有限且重复率高，很快就能适应。

### 10.1 必须认清的现实

```
中文资源能覆盖的阶段：
  阶段一（数学）   → 100% 中文资源可覆盖 ✅
  阶段二（工具链） → 90% 中文资源可覆盖 ✅
  阶段三（ML理论） → 80% 中文资源可覆盖 ✅
  阶段四（Kaggle）  → 50% 需要看英文 Notebook ⚠️
  阶段五（参赛）   → 80% 需要英文（题目/Discussion/方案）⚠️
  阶段六（深度学习）→ 70% 需要英文（论文/前沿技术）⚠️

结论：前 3 个阶段中文足够，之后英文是避不开的，但到那时你已经有技术基础了，
      看英文技术文章会比你想象的容易得多（因为核心词汇就那几十个）。
```

### 10.2 高效工具（不需要英文好也能用）

| 工具 | 用途 | 推荐理由 |
|------|------|---------|
| **沉浸式翻译**（浏览器插件） | 网页双语对照翻译 | 看 Kaggle Notebook 时原文+翻译并排显示，学英文同时学技术 |
| **DeepL 翻译** | 精准翻译 | 比 Google 翻译在技术文本上准确得多 |
| **ChatGPT / Claude** | 解释英文技术内容 | 把不懂的段落粘贴过去问"用中文解释这段代码/文章" |
| **欧路词典** | 随时查单词 | 技术词汇查多了自然就记住了 |
| **Kaggle 中文社区（知乎/CSDN）** | 竞赛中文解读 | 很多热门竞赛都有中文选手写的参赛心得 |

### 10.3 数据科学核心英文词汇表（约 80 个，记住就够日常用了）

```
--- 数据相关 ---
feature = 特征          label/target = 标签/目标     sample = 样本
training set = 训练集    validation set = 验证集      test set = 测试集
missing value = 缺失值   outlier = 异常值             categorical = 类别型
numerical = 数值型       distribution = 分布          correlation = 相关性

--- 模型相关 ---
classification = 分类    regression = 回归            clustering = 聚类
supervised = 监督        unsupervised = 无监督        prediction = 预测
accuracy = 准确率        precision = 精确率           recall = 召回率
loss = 损失              gradient = 梯度              weight = 权重
bias = 偏差              epoch = 训练轮次             batch = 批次
learning rate = 学习率   overfitting = 过拟合         underfitting = 欠拟合
regularization = 正则化  cross-validation = 交叉验证

--- 操作相关 ---
fit / train = 训练       predict = 预测               evaluate = 评估
transform = 变换         encode = 编码                scale = 缩放
split = 划分             merge = 合并                 submit = 提交
tune = 调参              ensemble = 融合/集成         stack = 堆叠

--- Kaggle 相关 ---
competition = 竞赛       notebook = 代码笔记本        discussion = 讨论区
submission = 提交文件     leaderboard (LB) = 排行榜    score = 分数
public LB = 公开排行榜   private LB = 最终排行榜      shake up = 排名剧变
medal = 奖牌             kernel = Notebook 的旧称     dataset = 数据集
```

**建议**：打印这个词汇表放在手边，看英文内容时对照查看，1-2 周就能记熟。

### 10.4 实际操作建议

1. **初期**：用中文资源打基础，不用碰英文
2. **阶段三开始**：安装沉浸式翻译插件，尝试看双语对照的 Kaggle Notebook
3. **阶段四开始**：每天看 1 篇英文 Notebook（借助翻译工具），逐渐适应
4. **遇到不懂的**：直接复制给 AI（ChatGPT/Claude）用中文解释，效率极高
5. **不要追求完美理解**：能看懂代码 + 大意即可，不需要逐字翻译

---

## 十一、推荐资源清单（中文优先）

### 11.1 视频课程

#### 数学基础（阶段一）

| 资源 | 平台 | 语言 | 说明 | 优先级 |
|------|------|------|------|-------|
| 3Blue1Brown 线性代数的本质 | B站搜索 | 中文字幕 | 最好的线代直觉课，B站有官方中文版 | ⭐⭐⭐⭐⭐ |
| 3Blue1Brown 微积分的本质 | B站搜索 | 中文字幕 | 同上，微积分部分 | ⭐⭐⭐⭐ |
| 可汗学院 - 统计与概率 | B站搬运 / khanacademy.org | 中文字幕 | 从零讲起，非常友好 | ⭐⭐⭐⭐ |
| 李永乐老师 - 概率统计 | B站 | 中文原生 | 考研名师，讲得通俗易懂 | ⭐⭐⭐ |

#### Python 数据科学（阶段二）

| 资源 | 平台 | 语言 | 说明 | 优先级 |
|------|------|------|------|-------|
| 莫烦 Python - NumPy & Pandas 教程 | B站 / mofanpy.com | 中文原生 | 短视频系列，每集 5-10 分钟，适合快速上手 | ⭐⭐⭐⭐⭐ |
| Kaggle Learn 微课程 | kaggle.com/learn | 英文（简单） | Pandas/可视化/ML入门，交互式练习 | ⭐⭐⭐⭐⭐ |
| 《利用 Python 进行数据分析》配套视频 | B站搜索 | 中文 | 跟着书学 Pandas | ⭐⭐⭐⭐ |
| 黑马程序员 - Python 数据分析 | B站 | 中文原生 | 体系化教程，适合从头跟 | ⭐⭐⭐ |

#### 机器学习（阶段三）

| 资源 | 平台 | 语言 | 说明 | 优先级 |
|------|------|------|------|-------|
| 吴恩达机器学习（2022新版） | Coursera / B站搬运 | 中文字幕 | 最经典ML课程，新版用Python | ⭐⭐⭐⭐⭐ |
| 李宏毅机器学习（台大） | B站 / YouTube | 中文原生 | 华人ML教育天花板，讲得深入又有趣 | ⭐⭐⭐⭐⭐ |
| StatQuest 机器学习系列 | B站搬运（有字幕） | 英文+中字 | 每个算法拆开讲，极其清楚 | ⭐⭐⭐⭐⭐ |
| 白板推导系列 - shuhuai008 | B站 | 中文原生 | 从数学角度推导每个算法，适合想深入理解的 | ⭐⭐⭐⭐ |
| 跟李沐学 AI | B站 | 中文原生 | Amazon 科学家，论文精读+实践 | ⭐⭐⭐⭐ |

#### 深度学习（阶段六）

| 资源 | 平台 | 语言 | 说明 | 优先级 |
|------|------|------|------|-------|
| 李宏毅深度学习（台大） | B站 | 中文原生 | 深度学习讲得最好的中文课程 | ⭐⭐⭐⭐⭐ |
| 动手学深度学习（d2l.ai） | B站 / d2l.ai | 中文原生 | 李沐团队，有配套中文教材和视频 | ⭐⭐⭐⭐⭐ |
| PyTorch 官方教程 | pytorch.org/tutorials | 英文（有社区中文翻译） | 学 PyTorch 的权威资料 | ⭐⭐⭐⭐ |
| fast.ai 实用深度学习 | course.fast.ai / B站搬运 | 英文+中字 | 实践导向，top-down 教学法 | ⭐⭐⭐ |

### 11.2 书籍（均有中文版）

| 中文书名 | 英文原名 | 适合阶段 | 说明 |
|---------|---------|---------|------|
| 《利用 Python 进行数据分析》（第3版） | Python for Data Analysis | 阶段二 | Pandas 作者 Wes McKinney 写的，中文翻译质量好 |
| 《机器学习》（西瓜书） | — | 阶段三 | 周志华著，国内 ML 经典教材，理论扎实 |
| 《统计学习方法》 | — | 阶段三 | 李航著，算法推导清晰，面试必备 |
| 《Scikit-Learn 与 TensorFlow 机器学习实用指南》 | Hands-On ML | 阶段三-五 | 实战圣经，中文翻译版名为《机器学习实战》 |
| 《动手学深度学习》 | Dive into Deep Learning | 阶段六 | 李沐团队，中文原生，免费在线阅读 d2l.ai |
| 《百面机器学习》 | — | 面试/查漏 | 以问答形式覆盖 ML 核心知识点 |

### 11.3 中文社区与博客

| 平台 | 内容 | 如何用 |
|------|------|-------|
| **知乎** | 搜索 "Kaggle 入门" "XGBoost 原理" 等 | 很多高质量的算法解析和竞赛心得 |
| **CSDN / 掘金** | 技术教程和代码实战 | 搜索具体问题（如 "Pandas groupby 用法"） |
| **微信公众号** | 机器之心、量子位、DataWhale、AI有道 | 关注行业动态和教程推送 |
| **DataWhale 开源社区** | 组队学习 + 开源教程 | 有系统化的 ML 学习路线和免费教程，适合自学 |
| **和鲸社区（Kesci）** | 国内版 Kaggle | 中文竞赛平台，题目和讨论全中文，适合初期练手 |
| **天池（阿里云）** | 国内数据竞赛平台 | 有中文赛题，部分有奖金，经验可迁移到 Kaggle |
| **B站** | 搜索任何技术关键词 | 几乎所有 ML/DL 知识点都有中文讲解视频 |

### 11.4 推荐的中文学习路径组合

```
最佳中文学习路径（按阶段）：

阶段一（数学）：
  → 3Blue1Brown B站中文版（线代+微积分）
  → 可汗学院概率统计（B站搬运）

阶段二（工具链）：
  → 莫烦 Python 教程（NumPy + Pandas + Matplotlib）
  → Kaggle Learn 微课程（英文简单，配合沉浸式翻译）

阶段三（机器学习）：
  → 吴恩达 ML 新版（B站中文字幕）—— 入门首选
  → 李宏毅 ML（B站）—— 想深入理解原理时看
  → 《机器学习实战》书 —— 动手实践
  → StatQuest（B站中字）—— 某个算法没理解时查看

阶段四（Kaggle 实战）：
  → 知乎/CSDN 搜索 "Titanic Kaggle 教程"
  → 和鲸社区 / 天池 做中文赛题练手
  → 开始看 Kaggle Notebook（用沉浸式翻译插件）

阶段五（正式参赛）：
  → 此时英文阅读能力已经够用
  → 知乎搜索竞赛名称，看中文选手的参赛总结

阶段六（深度学习）：
  → 李宏毅深度学习（B站）
  → 动手学深度学习（d2l.ai 中文版）
  → 跟李沐学 AI 论文精读（B站）
```

### 11.5 Kaggle 社区资源

- [Kaggle Winner Solutions](https://www.kaggle.com/sudalairajkumar/winning-solutions-of-kaggle-competitions) — 历年竞赛冠军方案合集（英文，但代码为主，配合翻译工具可读）
- [Abhishek Thakur 的 YouTube](https://www.youtube.com/c/AbhishekThakurAbhi) — Kaggle Grandmaster 的实战教程（英文，B站有搬运+字幕）
- 每个竞赛结束后的 Discussion 中的 Solution 帖子 — 最有价值的学习材料（英文，用沉浸式翻译阅读）
- 知乎搜索 **"Kaggle [竞赛名] 方案"** — 经常有中国选手写的中文参赛总结

---

## 十二、学习进度追踪表

复制以下表格，在学习过程中打勾记录进度：

### 阶段一：数学基础
- [ ] 线性代数视频看完 + NumPy 实践
- [ ] 概率统计核心概念理解 + 代码练习
- [ ] 梯度下降手动实现并可视化

### 阶段二：工具链
- [ ] Jupyter Notebook 熟练使用
- [ ] NumPy 核心操作熟练
- [ ] Pandas 数据处理熟练（最重要）
- [ ] Matplotlib + Seaborn 常用图表
- [ ] 用 Titanic 数据做完整 EDA

### 阶段三：机器学习
- [ ] 理解分类 vs 回归，监督 vs 无监督
- [ ] 掌握逻辑回归、决策树、随机森林
- [ ] 精通 XGBoost / LightGBM
- [ ] 掌握交叉验证和评估指标
- [ ] 能做完整的特征工程

### 阶段四：Kaggle 入门
- [ ] Titanic 竞赛完成（目标 > 0.79）
- [ ] House Prices 竞赛完成
- [ ] Spaceship Titanic 竞赛完成
- [ ] 建立个人竞赛代码模板
- [ ] 阅读 10+ 高质量 Notebook

### 阶段五：正式参赛
- [ ] 掌握 Optuna 超参调优
- [ ] 掌握模型融合（平均 + Stacking）
- [ ] 参加首个活跃竞赛
- [ ] 获得第 1 枚铜牌 🥉
- [ ] 获得第 2 枚铜牌 → Expert 🎖️

### 阶段六：深度学习
- [ ] PyTorch 基础
- [ ] 完成 1 个 CV/NLP 竞赛
- [ ] 首枚银牌 🥈

---

> **最后的忠告**：
> 1. **不要试图"准备好了再开始"** — 学到阶段二就可以开始看 Kaggle 竞赛了
> 2. **看 Notebook 是最高效的学习方式** — 比看书/看课效率高 10 倍
> 3. **坚持 > 天赋** — Kaggle Grandmaster 没有一个是短期速成的
> 4. **享受过程** — 数据科学的魅力在于用数据讲故事、解决真实问题
