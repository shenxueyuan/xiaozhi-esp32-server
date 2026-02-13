# 阶段四：Kaggle 实战入门 — 练习手册

> 前置要求：完成阶段一~三，能独立完成 EDA → 建模 → 评估
> 平台：Kaggle Notebook（推荐）或本地 Jupyter
> 预计耗时：4-6 周

---

## 4.1 Titanic 完整实战（二分类）

> 竞赛地址：https://www.kaggle.com/c/titanic
> 目标：预测乘客是否幸存（survived: 0/1）

### 练习 4.1.1：数据探索 ⭐

```python
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns

# 在 Kaggle Notebook 中路径为：
# train = pd.read_csv('/kaggle/input/titanic/train.csv')
# test = pd.read_csv('/kaggle/input/titanic/test.csv')

# 本地练习可用 seaborn 内置数据集替代：
train = sns.load_dataset('titanic').dropna(subset=['survived'])

# TODO: 1. 查看 train 的形状、列名、数据类型
# TODO: 2. 查看缺失值情况
# TODO: 3. 查看目标变量 survived 的分布
# TODO: 4. 画出以下图表：
#    a. 性别 vs 生存率 柱状图
#    b. 船舱等级(pclass) vs 生存率 柱状图
#    c. 年龄分布直方图（按生存着色）
#    d. 票价(fare) vs 生存 箱线图
# TODO: 5. 写出 3 个你从 EDA 中发现的规律
```

<details>
<summary>点击查看答案</summary>

```python
train = sns.load_dataset('titanic')
print(f"形状: {train.shape}")
print(f"列: {train.columns.tolist()}")
print(train.dtypes)
print(f"\n缺失值:\n{train.isnull().sum()}")
print(f"\n生存分布:\n{train['survived'].value_counts(normalize=True)}")

fig, axes = plt.subplots(2, 2, figsize=(14, 10))

# a
train.groupby('sex')['survived'].mean().plot(kind='bar', ax=axes[0,0])
axes[0,0].set_title('性别 vs 生存率')
axes[0,0].set_ylabel('生存率')

# b
train.groupby('pclass')['survived'].mean().plot(kind='bar', ax=axes[0,1])
axes[0,1].set_title('船舱等级 vs 生存率')

# c
for s in [0, 1]:
    axes[1,0].hist(train[train['survived']==s]['age'].dropna(),
                   bins=30, alpha=0.5, label=f'survived={s}')
axes[1,0].set_title('年龄分布')
axes[1,0].legend()

# d
sns.boxplot(x='survived', y='fare', data=train, ax=axes[1,1])
axes[1,1].set_title('票价 vs 生存')

plt.tight_layout()
plt.show()

print("\n发现：")
print("1. 女性生存率约74%，男性约19%")
print("2. 一等舱生存率最高(~63%)，三等舱最低(~24%)")
print("3. 幸存者的平均票价更高")
```
</details>

---

### 练习 4.1.2：特征工程 ⭐⭐

```python
# 基于 Titanic 数据做特征工程

train = sns.load_dataset('titanic')

# TODO: 1. 填充缺失值
#    - age: 用中位数填充
#    - embarked(embark_town): 用众数填充
#    - deck: 缺失太多，可以删除或填'Unknown'

# TODO: 2. 从 name 中提取 Title（Mr/Mrs/Miss/Master 等）
#    提示：Titanic 原始数据中 name 格式为 "Last, Title. First"
#    seaborn 版本没有 name，可跳过此步

# TODO: 3. 构造新特征
#    - family_size = sibsp + parch + 1（家庭人数）
#    - is_alone = 1 if family_size == 1 else 0
#    - fare_per_person = fare / family_size
#    - age_group: 分箱（0-12儿童，13-18青少年，19-60成人，60+老人）

# TODO: 4. 编码类别特征
#    - sex: 男=0, 女=1
#    - embarked/embark_town: One-Hot 编码
#    - pclass: 保持数值

# TODO: 5. 选择最终特征列表，输出处理后的 DataFrame
```

<details>
<summary>点击查看答案</summary>

```python
train = sns.load_dataset('titanic')
df = train.copy()

# 1 缺失值
df['age'].fillna(df['age'].median(), inplace=True)
df['embark_town'].fillna(df['embark_town'].mode()[0], inplace=True)
df['deck'].fillna('Unknown', inplace=True)

# 3 新特征
df['family_size'] = df['sibsp'] + df['parch'] + 1
df['is_alone'] = (df['family_size'] == 1).astype(int)
df['fare_per_person'] = df['fare'] / df['family_size']
df['age_group'] = pd.cut(df['age'], bins=[0, 12, 18, 60, 100],
                          labels=['child', 'teen', 'adult', 'senior'])

# 4 编码
df['sex_encoded'] = (df['sex'] == 'female').astype(int)
df = pd.get_dummies(df, columns=['embark_town', 'age_group'], drop_first=True)

# 5 选特征
features = ['pclass', 'sex_encoded', 'age', 'fare', 'family_size',
            'is_alone', 'fare_per_person'] + \
           [c for c in df.columns if c.startswith('embark_town_') or c.startswith('age_group_')]

X = df[features]
y = df['survived']
print(f"特征数: {X.shape[1]}")
print(f"特征列: {features}")
print(X.head())
```
</details>

---

### 练习 4.1.3：建模与提交 ⭐⭐

```python
from sklearn.model_selection import StratifiedKFold, cross_val_score
from sklearn.ensemble import RandomForestClassifier
import lightgbm as lgb

# 接上一步的 X, y

# TODO: 1. 用 StratifiedKFold(5) 评估 RandomForest 和 LightGBM
# TODO: 2. 比较两个模型的 CV 分数
# TODO: 3. 选最佳模型，用全部训练数据重新训练
# TODO: 4. 输出特征重要性 Top 10
# TODO: 5. 如果在 Kaggle 上：对 test 数据做同样的特征工程，预测并生成 submission.csv
```

<details>
<summary>点击查看答案</summary>

```python
skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)

rf = RandomForestClassifier(n_estimators=200, max_depth=6, random_state=42)
lgbm = lgb.LGBMClassifier(n_estimators=200, max_depth=6, random_state=42, verbose=-1)

rf_scores = cross_val_score(rf, X, y, cv=skf, scoring='accuracy')
lgb_scores = cross_val_score(lgbm, X, y, cv=skf, scoring='accuracy')

print(f"RandomForest: {rf_scores.mean():.4f} ± {rf_scores.std():.4f}")
print(f"LightGBM: {lgb_scores.mean():.4f} ± {lgb_scores.std():.4f}")

# 选最佳模型训练
best_model = lgbm if lgb_scores.mean() > rf_scores.mean() else rf
best_model.fit(X, y)

# 特征重要性
importance = pd.Series(best_model.feature_importances_, index=features)
importance.nlargest(10).plot(kind='barh')
plt.title('Top 10 特征重要性')
plt.show()

# Kaggle 提交（示意代码）
# test_processed = feature_engineering(test)  # 同样的特征工程
# predictions = best_model.predict(test_processed[features])
# submission = pd.DataFrame({'PassengerId': test['PassengerId'], 'Survived': predictions})
# submission.to_csv('submission.csv', index=False)
```
</details>

---

## 4.2 House Prices 完整实战（回归）

> 竞赛地址：https://www.kaggle.com/c/house-prices-advanced-regression-techniques
> 目标：预测房屋销售价格（SalePrice，连续值）

### 练习 4.2.1：回归问题完整流程 ⭐⭐⭐

```python
# 这个练习使用 California Housing 数据集模拟（无需 Kaggle 账号）
from sklearn.datasets import fetch_california_housing
from sklearn.model_selection import KFold, cross_val_score
from sklearn.preprocessing import StandardScaler
from sklearn.linear_model import Ridge, Lasso
from sklearn.ensemble import RandomForestRegressor
import lightgbm as lgb
import xgboost as xgb

housing = fetch_california_housing()
X = pd.DataFrame(housing.data, columns=housing.feature_names)
y = housing.target

# TODO: 完整流程

# 第1步：EDA
# 1. 查看数据形状、统计摘要
# 2. 画目标变量(y)的分布 → 是否需要 log 变换？
# 3. 画特征与目标的相关性热力图
# 4. 找出与目标相关性最强的 3 个特征

# 第2步：特征工程
# 5. 对目标做 log 变换（np.log1p），使其更正态
# 6. 构造新特征：rooms_per_household = AveRooms / AveOccup
# 7. 构造新特征：bedrooms_ratio = AveBedrms / AveRooms
# 8. StandardScaler 标准化

# 第3步：建模
# 9. 用 5-Fold CV 评估以下模型（scoring='neg_root_mean_squared_error'）：
#    - Ridge
#    - Lasso
#    - RandomForest
#    - XGBoost
#    - LightGBM
# 10. 对比所有模型的 RMSE

# 第4步：结果分析
# 11. 最佳模型的特征重要性 Top 8
# 12. 画 真实值 vs 预测值 的散点图（理想情况应在对角线上）
```

<details>
<summary>点击查看答案</summary>

```python
housing = fetch_california_housing()
X = pd.DataFrame(housing.data, columns=housing.feature_names)
y = housing.target

# 1-4 EDA
print(X.describe())

fig, axes = plt.subplots(1, 2, figsize=(14, 5))
axes[0].hist(y, bins=50, edgecolor='black')
axes[0].set_title('房价分布（原始）')
axes[1].hist(np.log1p(y), bins=50, edgecolor='black')
axes[1].set_title('房价分布（log变换后）')
plt.show()

corr = X.copy()
corr['target'] = y
plt.figure(figsize=(10, 8))
sns.heatmap(corr.corr(), annot=True, cmap='coolwarm', center=0, fmt='.2f')
plt.title('相关性')
plt.show()

print("与目标相关性最强:", corr.corr()['target'].abs().nlargest(4)[1:])

# 5-8 特征工程
y_log = np.log1p(y)
X['rooms_per_household'] = X['AveRooms'] / (X['AveOccup'] + 1e-8)
X['bedrooms_ratio'] = X['AveBedrms'] / (X['AveRooms'] + 1e-8)

scaler = StandardScaler()
X_scaled = pd.DataFrame(scaler.fit_transform(X), columns=X.columns)

# 9-10 建模
kf = KFold(n_splits=5, shuffle=True, random_state=42)
models = {
    'Ridge': Ridge(alpha=1.0),
    'Lasso': Lasso(alpha=0.001),
    'RandomForest': RandomForestRegressor(200, max_depth=10, random_state=42),
    'XGBoost': xgb.XGBRegressor(200, max_depth=6, learning_rate=0.1, random_state=42),
    'LightGBM': lgb.LGBMRegressor(200, max_depth=6, learning_rate=0.1,
                                    random_state=42, verbose=-1),
}

results = {}
for name, model in models.items():
    scores = cross_val_score(model, X_scaled, y_log, cv=kf,
                              scoring='neg_root_mean_squared_error')
    rmse = -scores.mean()
    results[name] = rmse
    print(f"{name}: RMSE = {rmse:.4f}")

# 对比图
plt.figure(figsize=(10, 5))
plt.bar(results.keys(), results.values())
plt.ylabel('RMSE (log scale)')
plt.title('模型对比')
plt.show()

# 11 特征重要性
best_name = min(results, key=results.get)
print(f"\n最佳模型: {best_name}")
best_model = models[best_name]
best_model.fit(X_scaled, y_log)

if hasattr(best_model, 'feature_importances_'):
    imp = pd.Series(best_model.feature_importances_, index=X.columns)
    imp.nlargest(8).plot(kind='barh')
    plt.title(f'{best_name} Top 8 特征')
    plt.show()

# 12 预测 vs 真实
y_pred_log = best_model.predict(X_scaled)
plt.figure(figsize=(8, 8))
plt.scatter(y_log, y_pred_log, alpha=0.3, s=5)
plt.plot([y_log.min(), y_log.max()], [y_log.min(), y_log.max()], 'r--')
plt.xlabel('真实值 (log)')
plt.ylabel('预测值 (log)')
plt.title('预测 vs 真实')
plt.show()
```
</details>

---

## 4.3 竞赛提交实操清单

完成 Titanic 和 House Prices 后，用以下 checklist 检查你的能力：

- [ ] 能在 Kaggle Notebook 中读取竞赛数据
- [ ] 能独立完成 EDA（5 分钟出图，10 分钟出洞察）
- [ ] 能处理缺失值（数值用中位数，类别用众数）
- [ ] 能构造 3+ 个新特征
- [ ] 能对类别特征编码
- [ ] 能用交叉验证对比多个模型
- [ ] 能生成 submission.csv 并提交
- [ ] 看过 5+ 个高票 Notebook 并学到新技巧
