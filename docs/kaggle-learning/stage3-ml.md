# 阶段三：机器学习 — 练习手册

> 前置要求：完成阶段一、二，熟练使用 Pandas 和可视化
> 额外安装：`pip install xgboost lightgbm catboost category_encoders`
> 预计耗时：4-6 周

---

## 3.1 第一个 ML 模型：从头到尾

### 知识点 25：Scikit-learn 基本流程

**概念**：所有 Scikit-learn 模型都遵循 `fit → predict → evaluate` 三步流程。

**Demo**：
```python
import numpy as np
import pandas as pd
from sklearn.datasets import load_iris
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report

# 1. 加载数据
iris = load_iris()
X, y = iris.data, iris.target
print(f"特征形状: {X.shape}, 标签形状: {y.shape}")
print(f"类别: {iris.target_names}")

# 2. 划分训练集/测试集
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y
)
print(f"训练集: {X_train.shape}, 测试集: {X_test.shape}")

# 3. 训练模型
model = LogisticRegression(max_iter=200)
model.fit(X_train, y_train)

# 4. 预测
y_pred = model.predict(X_test)

# 5. 评估
print(f"准确率: {accuracy_score(y_test, y_pred):.4f}")
print(classification_report(y_test, y_pred, target_names=iris.target_names))
```

**练习 3.1.1** ⭐
```python
from sklearn.datasets import load_wine

# Wine 数据集：用化学成分预测葡萄酒类别（3类）
wine = load_wine()

# TODO: 1. 查看数据集信息（特征名、类别名、样本数）
# TODO: 2. 转为 DataFrame 方便查看
# TODO: 3. 划分 80% 训练 / 20% 测试（stratify=y）
# TODO: 4. 用 LogisticRegression 训练
# TODO: 5. 在测试集上预测并输出准确率
# TODO: 6. 打印 classification_report
```

<details>
<summary>点击查看答案</summary>

```python
from sklearn.datasets import load_wine
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score, classification_report

wine = load_wine()
print(f"特征: {wine.feature_names}")
print(f"类别: {wine.target_names}")
print(f"样本数: {wine.data.shape}")

df = pd.DataFrame(wine.data, columns=wine.feature_names)
df['target'] = wine.target
print(df.head())

X_train, X_test, y_train, y_test = train_test_split(
    wine.data, wine.target, test_size=0.2, random_state=42, stratify=wine.target
)

model = LogisticRegression(max_iter=5000)
model.fit(X_train, y_train)
y_pred = model.predict(X_test)

print(f"准确率: {accuracy_score(y_test, y_pred):.4f}")
print(classification_report(y_test, y_pred, target_names=wine.target_names))
```
</details>

---

### 知识点 26：分类 vs 回归

**Demo**：
```python
from sklearn.datasets import make_classification, make_regression
from sklearn.linear_model import LogisticRegression, LinearRegression
from sklearn.metrics import accuracy_score, mean_squared_error
import matplotlib.pyplot as plt

fig, axes = plt.subplots(1, 2, figsize=(14, 5))

# === 分类问题 ===
X_cls, y_cls = make_classification(n_samples=200, n_features=2,
                                    n_redundant=0, random_state=42)
axes[0].scatter(X_cls[:, 0], X_cls[:, 1], c=y_cls, cmap='coolwarm', s=20, alpha=0.7)
axes[0].set_title('分类问题：预测类别（0或1）')

# === 回归问题 ===
X_reg, y_reg = make_regression(n_samples=200, n_features=1, noise=20, random_state=42)
model_reg = LinearRegression().fit(X_reg, y_reg)
axes[1].scatter(X_reg, y_reg, s=20, alpha=0.7)
axes[1].plot(sorted(X_reg), model_reg.predict(sorted(X_reg)), 'r-', linewidth=2)
axes[1].set_title('回归问题：预测连续值')

plt.tight_layout()
plt.show()

print("分类 → 预测离散类别（是/否、猫/狗、A/B/C）→ 用准确率/F1/AUC 评估")
print("回归 → 预测连续数值（房价、温度、销量）→ 用 RMSE/MAE/R² 评估")
```

**练习 3.1.2** ⭐
```python
from sklearn.datasets import fetch_california_housing
from sklearn.linear_model import LinearRegression
from sklearn.metrics import mean_squared_error, r2_score

# California Housing：预测房价（回归问题）
housing = fetch_california_housing()

# TODO: 1. 查看数据信息
# TODO: 2. 划分训练集/测试集
# TODO: 3. 用 LinearRegression 训练
# TODO: 4. 计算 RMSE 和 R²
# TODO: 5. 这个问题是分类还是回归？为什么？
```

<details>
<summary>点击查看答案</summary>

```python
housing = fetch_california_housing()
print(f"特征: {housing.feature_names}")
print(f"样本数: {housing.data.shape}")
print(f"目标变量描述: {housing.DESCR[:200]}")

X_train, X_test, y_train, y_test = train_test_split(
    housing.data, housing.target, test_size=0.2, random_state=42
)

model = LinearRegression()
model.fit(X_train, y_train)
y_pred = model.predict(X_test)

rmse = np.sqrt(mean_squared_error(y_test, y_pred))
r2 = r2_score(y_test, y_pred)
print(f"RMSE: {rmse:.4f}")
print(f"R²: {r2:.4f}")
print("这是回归问题，因为目标变量（房价）是连续值")
```
</details>

---

## 3.2 核心算法详解

### 知识点 27：决策树

**Demo**：
```python
from sklearn.tree import DecisionTreeClassifier, plot_tree
from sklearn.datasets import load_iris
from sklearn.model_selection import train_test_split

iris = load_iris()
X_train, X_test, y_train, y_test = train_test_split(
    iris.data, iris.target, test_size=0.2, random_state=42
)

# 训练决策树
dt = DecisionTreeClassifier(max_depth=3, random_state=42)
dt.fit(X_train, y_train)
print(f"训练准确率: {dt.score(X_train, y_train):.4f}")
print(f"测试准确率: {dt.score(X_test, y_test):.4f}")

# 可视化决策树（最大亮点！）
plt.figure(figsize=(16, 8))
plot_tree(dt, feature_names=iris.feature_names,
          class_names=iris.target_names, filled=True, rounded=True)
plt.title('决策树可视化')
plt.show()

# 特征重要性
importance = pd.Series(dt.feature_importances_, index=iris.feature_names)
importance.sort_values().plot(kind='barh')
plt.title('特征重要性')
plt.show()
```

**练习 3.2.1** ⭐⭐
```python
from sklearn.tree import DecisionTreeClassifier

# 用 Wine 数据集
wine = load_wine()
X_train, X_test, y_train, y_test = train_test_split(
    wine.data, wine.target, test_size=0.2, random_state=42
)

# TODO: 1. 分别训练 max_depth=2, 5, 10, None 的决策树
# TODO: 2. 记录每棵树的训练准确率和测试准确率
# TODO: 3. 画折线图对比（x=max_depth, y=accuracy，两条线）
# TODO: 4. 哪个深度的测试准确率最高？
# TODO: 5. 当 max_depth=None 时，训练 vs 测试准确率差距大吗？这说明什么？
```

<details>
<summary>点击查看答案</summary>

```python
depths = [2, 5, 10, None]
train_scores = []
test_scores = []

for d in depths:
    dt = DecisionTreeClassifier(max_depth=d, random_state=42)
    dt.fit(X_train, y_train)
    train_scores.append(dt.score(X_train, y_train))
    test_scores.append(dt.score(X_test, y_test))

labels = ['2', '5', '10', 'None']
plt.figure(figsize=(8, 5))
plt.plot(labels, train_scores, 'o-', label='训练')
plt.plot(labels, test_scores, 's-', label='测试')
plt.xlabel('max_depth')
plt.ylabel('Accuracy')
plt.legend()
plt.title('决策树深度 vs 准确率')
plt.grid(True, alpha=0.3)
plt.show()

for d, tr, te in zip(labels, train_scores, test_scores):
    print(f"depth={d}: 训练={tr:.4f}, 测试={te:.4f}, 差距={tr-te:.4f}")

print("\nmax_depth=None时训练准确率=1.0但测试较低 → 过拟合！")
print("需要限制深度来防止过拟合")
```
</details>

---

### 知识点 28：随机森林

**Demo**：
```python
from sklearn.ensemble import RandomForestClassifier

rf = RandomForestClassifier(n_estimators=100, max_depth=5, random_state=42)
rf.fit(X_train, y_train)
print(f"训练: {rf.score(X_train, y_train):.4f}")
print(f"测试: {rf.score(X_test, y_test):.4f}")

# 特征重要性（比单棵树更可靠）
importance = pd.Series(rf.feature_importances_, index=wine.feature_names)
importance.sort_values(ascending=True).plot(kind='barh', figsize=(8, 6))
plt.title('随机森林特征重要性')
plt.show()
```

**练习 3.2.2** ⭐⭐
```python
# 对比实验：单棵决策树 vs 随机森林

from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import cross_val_score

wine = load_wine()
X, y = wine.data, wine.target

# TODO: 1. 用 5-Fold 交叉验证评估 DecisionTreeClassifier（默认参数）
# TODO: 2. 用 5-Fold 交叉验证评估 RandomForestClassifier（100棵树）
# TODO: 3. 打印两者的 CV 均值和标准差
# TODO: 4. 哪个更好？为什么随机森林通常比单棵树好？
# TODO: 5. 尝试不同的 n_estimators（10, 50, 100, 200, 500），画出 CV 分数曲线
```

<details>
<summary>点击查看答案</summary>

```python
from sklearn.model_selection import cross_val_score

X, y = wine.data, wine.target

# 1-2
dt_scores = cross_val_score(DecisionTreeClassifier(random_state=42), X, y, cv=5)
rf_scores = cross_val_score(RandomForestClassifier(100, random_state=42), X, y, cv=5)

# 3
print(f"决策树 CV: {dt_scores.mean():.4f} ± {dt_scores.std():.4f}")
print(f"随机森林 CV: {rf_scores.mean():.4f} ± {rf_scores.std():.4f}")

# 4
print("随机森林更好。因为多棵树投票减少了单棵树的过拟合风险（集成学习的优势）")

# 5
n_list = [10, 50, 100, 200, 500]
scores_list = []
for n in n_list:
    s = cross_val_score(RandomForestClassifier(n, random_state=42), X, y, cv=5)
    scores_list.append(s.mean())
    print(f"n={n}: {s.mean():.4f}")

plt.plot(n_list, scores_list, 'o-')
plt.xlabel('n_estimators')
plt.ylabel('CV Accuracy')
plt.title('树的数量 vs 准确率')
plt.grid(True, alpha=0.3)
plt.show()
```
</details>

---

### 知识点 29：XGBoost / LightGBM ⭐⭐⭐

**Demo**：
```python
import xgboost as xgb
import lightgbm as lgb
from sklearn.model_selection import cross_val_score

wine = load_wine()
X, y = wine.data, wine.target

# XGBoost
xgb_model = xgb.XGBClassifier(n_estimators=100, max_depth=4, learning_rate=0.1,
                                random_state=42, eval_metric='mlogloss')
xgb_scores = cross_val_score(xgb_model, X, y, cv=5, scoring='accuracy')
print(f"XGBoost CV: {xgb_scores.mean():.4f} ± {xgb_scores.std():.4f}")

# LightGBM
lgb_model = lgb.LGBMClassifier(n_estimators=100, max_depth=4, learning_rate=0.1,
                                 random_state=42, verbose=-1)
lgb_scores = cross_val_score(lgb_model, X, y, cv=5, scoring='accuracy')
print(f"LightGBM CV: {lgb_scores.mean():.4f} ± {lgb_scores.std():.4f}")
```

**练习 3.2.3** ⭐⭐
```python
from sklearn.datasets import fetch_california_housing
from sklearn.metrics import mean_squared_error
import xgboost as xgb
import lightgbm as lgb

housing = fetch_california_housing()
X_train, X_test, y_train, y_test = train_test_split(
    housing.data, housing.target, test_size=0.2, random_state=42
)

# TODO: 1. 用 LinearRegression 训练并计算 RMSE（作为 baseline）
# TODO: 2. 用 xgb.XGBRegressor 训练并计算 RMSE
# TODO: 3. 用 lgb.LGBMRegressor 训练并计算 RMSE
# TODO: 4. 对比三个模型的 RMSE，哪个最好？
# TODO: 5. 输出 XGBoost 的特征重要性 top 5
```

<details>
<summary>点击查看答案</summary>

```python
from sklearn.linear_model import LinearRegression

# 1 Baseline
lr = LinearRegression().fit(X_train, y_train)
rmse_lr = np.sqrt(mean_squared_error(y_test, lr.predict(X_test)))
print(f"LinearRegression RMSE: {rmse_lr:.4f}")

# 2 XGBoost
xgb_model = xgb.XGBRegressor(n_estimators=200, max_depth=6, learning_rate=0.1,
                               random_state=42)
xgb_model.fit(X_train, y_train)
rmse_xgb = np.sqrt(mean_squared_error(y_test, xgb_model.predict(X_test)))
print(f"XGBoost RMSE: {rmse_xgb:.4f}")

# 3 LightGBM
lgb_model = lgb.LGBMRegressor(n_estimators=200, max_depth=6, learning_rate=0.1,
                                random_state=42, verbose=-1)
lgb_model.fit(X_train, y_train)
rmse_lgb = np.sqrt(mean_squared_error(y_test, lgb_model.predict(X_test)))
print(f"LightGBM RMSE: {rmse_lgb:.4f}")

# 4
print(f"\n最佳模型: {'XGBoost' if rmse_xgb < rmse_lgb else 'LightGBM'}")

# 5
importance = pd.Series(xgb_model.feature_importances_,
                        index=housing.feature_names)
print("\nTop 5 特征:")
print(importance.nlargest(5))
```
</details>

---

## 3.3 模型评估

### 知识点 30：交叉验证

**Demo**：
```python
from sklearn.model_selection import KFold, StratifiedKFold, cross_val_score

wine = load_wine()
X, y = wine.data, wine.target
model = lgb.LGBMClassifier(n_estimators=100, random_state=42, verbose=-1)

# 普通 K-Fold
kf = KFold(n_splits=5, shuffle=True, random_state=42)
scores_kf = cross_val_score(model, X, y, cv=kf, scoring='accuracy')
print(f"KFold: {scores_kf.mean():.4f} ± {scores_kf.std():.4f}")

# 分层 K-Fold（分类问题推荐，保持每折中类别比例一致）
skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
scores_skf = cross_val_score(model, X, y, cv=skf, scoring='accuracy')
print(f"StratifiedKFold: {scores_skf.mean():.4f} ± {scores_skf.std():.4f}")

# 查看每折分数
print(f"每折分数: {scores_skf}")
```

**练习 3.3.1** ⭐⭐
```python
from sklearn.model_selection import StratifiedKFold, cross_val_score
from sklearn.linear_model import LogisticRegression
from sklearn.ensemble import RandomForestClassifier

# 用 Wine 数据集，对比 4 个模型的 5-Fold CV 分数
wine = load_wine()
X, y = wine.data, wine.target
skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)

models = {
    'LogisticRegression': LogisticRegression(max_iter=5000),
    'DecisionTree': DecisionTreeClassifier(random_state=42),
    'RandomForest': RandomForestClassifier(100, random_state=42),
    'LightGBM': lgb.LGBMClassifier(100, random_state=42, verbose=-1),
}

# TODO: 1. 对每个模型做 5-Fold CV
# TODO: 2. 记录均值和标准差
# TODO: 3. 画柱状图对比（带误差棒）
# TODO: 4. 哪个模型最好？哪个最不稳定（标准差最大）？
```

<details>
<summary>点击查看答案</summary>

```python
results = {}
for name, model in models.items():
    scores = cross_val_score(model, X, y, cv=skf, scoring='accuracy')
    results[name] = (scores.mean(), scores.std())
    print(f"{name}: {scores.mean():.4f} ± {scores.std():.4f}")

names = list(results.keys())
means = [r[0] for r in results.values()]
stds = [r[1] for r in results.values()]

plt.figure(figsize=(10, 5))
plt.bar(names, means, yerr=stds, capsize=5, alpha=0.8)
plt.ylabel('CV Accuracy')
plt.title('模型对比')
plt.ylim(0.8, 1.05)
plt.grid(True, alpha=0.3, axis='y')
plt.show()
```
</details>

---

### 知识点 31：评估指标详解

**Demo**：
```python
from sklearn.metrics import (accuracy_score, precision_score, recall_score,
                              f1_score, roc_auc_score, confusion_matrix,
                              ConfusionMatrixDisplay)
from sklearn.datasets import make_classification

# 创建不平衡二分类数据（90% 类0, 10% 类1）
X, y = make_classification(n_samples=1000, weights=[0.9, 0.1], random_state=42)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

model = lgb.LGBMClassifier(random_state=42, verbose=-1)
model.fit(X_train, y_train)
y_pred = model.predict(X_test)
y_prob = model.predict_proba(X_test)[:, 1]

# 各种指标
print(f"Accuracy:  {accuracy_score(y_test, y_pred):.4f}")
print(f"Precision: {precision_score(y_test, y_pred):.4f}")
print(f"Recall:    {recall_score(y_test, y_pred):.4f}")
print(f"F1:        {f1_score(y_test, y_pred):.4f}")
print(f"AUC:       {roc_auc_score(y_test, y_prob):.4f}")

# 混淆矩阵
cm = confusion_matrix(y_test, y_pred)
ConfusionMatrixDisplay(cm, display_labels=['正常', '异常']).plot()
plt.title('混淆矩阵')
plt.show()
```

**练习 3.3.2** ⭐⭐
```python
# 场景：疾病诊断（正类=有病，负类=健康）
# 问题：用哪个指标评估更合适？

np.random.seed(42)
y_true = np.array([0]*180 + [1]*20)  # 200人中20人有病
y_pred_a = np.array([0]*200)          # 模型A：全部预测健康
y_pred_b = np.array([0]*170 + [1]*30) # 模型B：预测30人有病（含15个真有病的）

# TODO: 1. 分别计算模型 A 和 B 的 Accuracy, Precision, Recall, F1
# TODO: 2. 模型A的准确率是多少？看起来很高？但它有什么问题？
# TODO: 3. 在疾病诊断场景，应该重点看哪个指标？为什么？
# TODO: 4. 模型B虽然准确率低一些，但为什么在这个场景下更好？
```

<details>
<summary>点击查看答案</summary>

```python
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score

for name, y_pred in [('模型A(全预测健康)', y_pred_a), ('模型B', y_pred_b)]:
    print(f"\n{name}:")
    print(f"  Accuracy:  {accuracy_score(y_true, y_pred):.4f}")
    print(f"  Precision: {precision_score(y_true, y_pred, zero_division=0):.4f}")
    print(f"  Recall:    {recall_score(y_true, y_pred, zero_division=0):.4f}")
    print(f"  F1:        {f1_score(y_true, y_pred, zero_division=0):.4f}")

print("\n分析：")
print("模型A准确率90%看起来很高，但Recall=0 → 一个病人都没找到！")
print("疾病诊断应重点看 Recall（召回率）→ 尽量不漏诊")
print("模型B Recall=0.75 → 找到了75%的病人，虽然有些误报，但漏诊少")
print("在医疗场景：漏诊（假阴性）比误报（假阳性）的代价更高")
```
</details>

---

## 3.4 特征工程实战

### 知识点 32：类别特征编码

**Demo**：
```python
import pandas as pd
from sklearn.preprocessing import LabelEncoder

df = pd.DataFrame({
    'color': ['red', 'blue', 'green', 'red', 'blue'],
    'size': ['S', 'M', 'L', 'XL', 'M'],
    'price': [100, 200, 150, 120, 180]
})

# Label Encoding（适合有序类别或树模型）
le = LabelEncoder()
df['color_le'] = le.fit_transform(df['color'])

# One-Hot Encoding（适合无序类别 + 线性模型）
df_onehot = pd.get_dummies(df, columns=['color'], drop_first=True)

# 频率编码
freq = df['color'].value_counts(normalize=True)
df['color_freq'] = df['color'].map(freq)

print("Label Encoding:\n", df[['color', 'color_le']])
print("\nOne-Hot:\n", df_onehot)
print("\n频率编码:\n", df[['color', 'color_freq']])
```

**练习 3.4.1** ⭐⭐
```python
df = pd.DataFrame({
    'city': ['北京', '上海', '广州', '北京', '深圳', '上海', '广州', '北京',
             '深圳', '成都', '上海', '北京', '广州', '成都', '深圳'],
    'education': ['本科', '硕士', '博士', '本科', '硕士', '博士', '本科',
                  '硕士', '本科', '博士', '硕士', '本科', '硕士', '本科', '博士'],
    'salary': [15, 25, 35, 14, 22, 40, 12, 28, 20, 38, 26, 16, 24, 18, 32]
})

# TODO: 1. 对 city 做 Label Encoding
# TODO: 2. 对 city 做 One-Hot Encoding
# TODO: 3. 对 city 做频率编码
# TODO: 4. education 是有序的（本科<硕士<博士），手动做有序编码（本科=0,硕士=1,博士=2）
# TODO: 5. 计算 city 的 Target Encoding（每个城市的 salary 均值）
```

<details>
<summary>点击查看答案</summary>

```python
# 1
le = LabelEncoder()
df['city_le'] = le.fit_transform(df['city'])
print("Label:\n", df[['city', 'city_le']].drop_duplicates())

# 2
df_oh = pd.get_dummies(df, columns=['city'], drop_first=True)
print("\nOne-Hot:\n", df_oh.head())

# 3
freq = df['city'].value_counts(normalize=True)
df['city_freq'] = df['city'].map(freq)
print("\n频率:\n", df[['city', 'city_freq']].drop_duplicates())

# 4
edu_map = {'本科': 0, '硕士': 1, '博士': 2}
df['edu_ordinal'] = df['education'].map(edu_map)
print("\n有序编码:\n", df[['education', 'edu_ordinal']].drop_duplicates())

# 5
city_mean = df.groupby('city')['salary'].mean()
df['city_te'] = df['city'].map(city_mean)
print("\nTarget Encoding:\n", df[['city', 'city_te']].drop_duplicates())
```
</details>

---

### 知识点 33：数值特征处理

**练习 3.4.2** ⭐⭐
```python
from sklearn.preprocessing import StandardScaler, MinMaxScaler

np.random.seed(42)
df = pd.DataFrame({
    'age': np.random.randint(18, 70, 100),
    'income': np.random.exponential(50000, 100),   # 右偏分布
    'score': np.random.normal(75, 10, 100),
})

# TODO: 1. 画 income 的分布图 → 观察是否右偏
# TODO: 2. 对 income 做 log 变换 (np.log1p)，再画分布图 → 是否更正态了？
# TODO: 3. 用 StandardScaler 标准化所有列（均值0标准差1）
# TODO: 4. 用 MinMaxScaler 归一化所有列（缩放到 [0,1]）
# TODO: 5. 标准化前后，数据的均值和标准差分别是多少？
```

<details>
<summary>点击查看答案</summary>

```python
np.random.seed(42)
df = pd.DataFrame({
    'age': np.random.randint(18, 70, 100),
    'income': np.random.exponential(50000, 100),
    'score': np.random.normal(75, 10, 100),
})

# 1-2
fig, axes = plt.subplots(1, 2, figsize=(12, 4))
axes[0].hist(df['income'], bins=30, edgecolor='black')
axes[0].set_title('income 原始分布（右偏）')
axes[1].hist(np.log1p(df['income']), bins=30, edgecolor='black')
axes[1].set_title('income log变换后（更正态）')
plt.tight_layout()
plt.show()

# 3
scaler = StandardScaler()
df_std = pd.DataFrame(scaler.fit_transform(df), columns=df.columns)
print("标准化后:\n", df_std.describe().round(3))

# 4
minmax = MinMaxScaler()
df_mm = pd.DataFrame(minmax.fit_transform(df), columns=df.columns)
print("\n归一化后:\n", df_mm.describe().round(3))

# 5
print(f"\n标准化前 age 均值={df['age'].mean():.2f}, 标准差={df['age'].std():.2f}")
print(f"标准化后 age 均值={df_std['age'].mean():.4f}, 标准差={df_std['age'].std():.4f}")
```
</details>

---

### 知识点 34：特征构造

**练习 3.4.3** ⭐⭐⭐ (综合)
```python
# 模拟一个电商数据集，练习特征构造
np.random.seed(42)
n = 500
df = pd.DataFrame({
    'user_id': np.random.randint(1, 101, n),
    'order_date': pd.date_range('2024-01-01', periods=n, freq='8H'),
    'product_price': np.random.choice([29, 99, 199, 499, 999], n),
    'quantity': np.random.randint(1, 6, n),
    'category': np.random.choice(['食品', '电子', '服装', '图书'], n),
    'payment_method': np.random.choice(['支付宝', '微信', '银行卡'], n),
})

# TODO: 完成以下特征构造：

# 日期特征
# 1. 提取 month, dayofweek, hour
# 2. 新增 is_weekend (周末=1, 工作日=0)

# 金额特征
# 3. total_amount = product_price * quantity
# 4. log_total = log(total_amount)

# 聚合特征（按用户统计）
# 5. user_order_count: 每个用户的总订单数
# 6. user_avg_amount: 每个用户的平均订单金额
# 7. user_max_amount: 每个用户的最大单笔金额

# 类别编码
# 8. category 做频率编码
# 9. payment_method 做 One-Hot 编码

# 最终输出处理后的 DataFrame 的 shape 和列名
```

<details>
<summary>点击查看答案</summary>

```python
np.random.seed(42)
n = 500
df = pd.DataFrame({
    'user_id': np.random.randint(1, 101, n),
    'order_date': pd.date_range('2024-01-01', periods=n, freq='8H'),
    'product_price': np.random.choice([29, 99, 199, 499, 999], n),
    'quantity': np.random.randint(1, 6, n),
    'category': np.random.choice(['食品', '电子', '服装', '图书'], n),
    'payment_method': np.random.choice(['支付宝', '微信', '银行卡'], n),
})

# 日期特征
df['month'] = df['order_date'].dt.month
df['dayofweek'] = df['order_date'].dt.dayofweek
df['hour'] = df['order_date'].dt.hour
df['is_weekend'] = df['dayofweek'].isin([5, 6]).astype(int)

# 金额特征
df['total_amount'] = df['product_price'] * df['quantity']
df['log_total'] = np.log1p(df['total_amount'])

# 聚合特征
user_stats = df.groupby('user_id')['total_amount'].agg(
    user_order_count='count',
    user_avg_amount='mean',
    user_max_amount='max'
).reset_index()
df = df.merge(user_stats, on='user_id', how='left')

# 类别编码
freq = df['category'].value_counts(normalize=True)
df['category_freq'] = df['category'].map(freq)

df = pd.get_dummies(df, columns=['payment_method'], drop_first=True)

print(f"最终形状: {df.shape}")
print(f"列名: {df.columns.tolist()}")
print(df.head())
```
</details>

---

## 阶段三综合测试 ⭐⭐⭐

```python
# 综合实战：用 Scikit-learn 内置数据集完成一个完整的 ML 项目

from sklearn.datasets import load_breast_cancer

# 乳腺癌数据集：预测良性(0)/恶性(1)（二分类）
data = load_breast_cancer()
X, y = data.data, data.target

# 完成以下全部步骤：
# 1. EDA：数据形状、类别分布、特征统计
# 2. 划分数据：80/20，stratify
# 3. 特征工程：StandardScaler 标准化
# 4. 训练 4 个模型：LogisticRegression, RandomForest, XGBoost, LightGBM
# 5. 用 5-Fold StratifiedKFold CV 评估每个模型
# 6. 选出最佳模型，输出 classification_report
# 7. 画混淆矩阵
# 8. 输出最佳模型的 Top 10 特征重要性
# 9. 讨论：这个场景中，Precision 和 Recall 哪个更重要？
```
