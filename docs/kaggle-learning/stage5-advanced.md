# 阶段五：进阶技能与正式参赛 — 练习手册

> 前置要求：完成阶段一~四，已完成 Titanic/House Prices 竞赛
> 额外安装：`pip install optuna`
> 预计耗时：2-4 个月

---

## 5.1 超参调优

### 知识点 35：Optuna 自动调参

**概念**：手动调参效率低，Optuna 用贝叶斯优化自动搜索最佳超参数组合。

**Demo**：
```python
import optuna
import lightgbm as lgb
from sklearn.datasets import load_breast_cancer
from sklearn.model_selection import StratifiedKFold, cross_val_score
import warnings
warnings.filterwarnings('ignore')

data = load_breast_cancer()
X, y = data.data, data.target

def objective(trial):
    """Optuna 会自动调用这个函数，每次传入不同的超参组合"""
    params = {
        'n_estimators': trial.suggest_int('n_estimators', 50, 500),
        'max_depth': trial.suggest_int('max_depth', 2, 10),
        'learning_rate': trial.suggest_float('learning_rate', 0.01, 0.3, log=True),
        'num_leaves': trial.suggest_int('num_leaves', 10, 100),
        'subsample': trial.suggest_float('subsample', 0.5, 1.0),
        'colsample_bytree': trial.suggest_float('colsample_bytree', 0.5, 1.0),
        'reg_alpha': trial.suggest_float('reg_alpha', 1e-8, 10.0, log=True),
        'reg_lambda': trial.suggest_float('reg_lambda', 1e-8, 10.0, log=True),
    }

    model = lgb.LGBMClassifier(**params, random_state=42, verbose=-1)
    skf = StratifiedKFold(n_splits=5, shuffle=True, random_state=42)
    scores = cross_val_score(model, X, y, cv=skf, scoring='accuracy')
    return scores.mean()

# 运行优化（50次试验）
study = optuna.create_study(direction='maximize')
study.optimize(objective, n_trials=50, show_progress_bar=True)

print(f"\n最佳分数: {study.best_value:.4f}")
print(f"最佳参数: {study.best_params}")

# 用最佳参数训练最终模型
best_model = lgb.LGBMClassifier(**study.best_params, random_state=42, verbose=-1)
```

**练习 5.1.1** ⭐⭐
```python
from sklearn.datasets import fetch_california_housing
import xgboost as xgb

housing = fetch_california_housing()
X, y = housing.data, housing.target

# TODO: 1. 用 Optuna 调优 XGBRegressor 的超参数
#    搜索空间：
#    - n_estimators: 100~1000
#    - max_depth: 3~10
#    - learning_rate: 0.01~0.3 (log scale)
#    - subsample: 0.5~1.0
#    - colsample_bytree: 0.5~1.0
# TODO: 2. 目标：最小化 5-Fold CV 的 RMSE（用 neg_root_mean_squared_error）
# TODO: 3. 运行 30 次试验
# TODO: 4. 打印最佳参数和最佳 RMSE
# TODO: 5. 对比默认参数 vs 调优后参数的 RMSE 差距
```

<details>
<summary>点击查看答案</summary>

```python
import optuna
from sklearn.model_selection import KFold, cross_val_score

housing = fetch_california_housing()
X, y = housing.data, housing.target

def objective(trial):
    params = {
        'n_estimators': trial.suggest_int('n_estimators', 100, 1000),
        'max_depth': trial.suggest_int('max_depth', 3, 10),
        'learning_rate': trial.suggest_float('learning_rate', 0.01, 0.3, log=True),
        'subsample': trial.suggest_float('subsample', 0.5, 1.0),
        'colsample_bytree': trial.suggest_float('colsample_bytree', 0.5, 1.0),
    }
    model = xgb.XGBRegressor(**params, random_state=42)
    kf = KFold(n_splits=5, shuffle=True, random_state=42)
    scores = cross_val_score(model, X, y, cv=kf, scoring='neg_root_mean_squared_error')
    return scores.mean()  # 负值，Optuna 会最大化（即最小化 RMSE）

study = optuna.create_study(direction='maximize')
study.optimize(objective, n_trials=30, show_progress_bar=True)

print(f"最佳 RMSE: {-study.best_value:.4f}")
print(f"最佳参数: {study.best_params}")

# 对比默认参数
default = xgb.XGBRegressor(random_state=42)
kf = KFold(n_splits=5, shuffle=True, random_state=42)
default_scores = cross_val_score(default, X, y, cv=kf, scoring='neg_root_mean_squared_error')
print(f"默认参数 RMSE: {-default_scores.mean():.4f}")
print(f"调优后 RMSE: {-study.best_value:.4f}")
print(f"提升: {(-default_scores.mean()) - (-study.best_value):.4f}")
```
</details>

---

## 5.2 模型融合（Ensemble）

### 知识点 36：加权平均

**Demo**：
```python
from sklearn.datasets import load_breast_cancer
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
import lightgbm as lgb
import xgboost as xgb
from sklearn.ensemble import RandomForestClassifier

data = load_breast_cancer()
X_train, X_test, y_train, y_test = train_test_split(
    data.data, data.target, test_size=0.2, random_state=42
)

# 训练 3 个模型
m1 = lgb.LGBMClassifier(100, random_state=42, verbose=-1).fit(X_train, y_train)
m2 = xgb.XGBClassifier(100, random_state=42, eval_metric='logloss').fit(X_train, y_train)
m3 = RandomForestClassifier(200, random_state=42).fit(X_train, y_train)

# 获取概率预测
p1 = m1.predict_proba(X_test)[:, 1]
p2 = m2.predict_proba(X_test)[:, 1]
p3 = m3.predict_proba(X_test)[:, 1]

# 单模型准确率
for name, p in [('LGB', p1), ('XGB', p2), ('RF', p3)]:
    print(f"{name}: {accuracy_score(y_test, (p > 0.5).astype(int)):.4f}")

# 简单平均
avg_pred = (p1 + p2 + p3) / 3
print(f"简单平均: {accuracy_score(y_test, (avg_pred > 0.5).astype(int)):.4f}")

# 加权平均（给表现好的模型更大权重）
weighted = 0.4 * p1 + 0.35 * p2 + 0.25 * p3
print(f"加权平均: {accuracy_score(y_test, (weighted > 0.5).astype(int)):.4f}")
```

**练习 5.2.1** ⭐⭐
```python
from sklearn.datasets import fetch_california_housing
from sklearn.metrics import mean_squared_error

housing = fetch_california_housing()
X_train, X_test, y_train, y_test = train_test_split(
    housing.data, housing.target, test_size=0.2, random_state=42
)

# TODO: 1. 训练 3 个回归模型：Ridge, XGBRegressor, LGBMRegressor
# TODO: 2. 分别计算每个模型的 RMSE
# TODO: 3. 计算简单平均的 RMSE
# TODO: 4. 尝试不同权重组合（如 0.5/0.3/0.2），找到最优权重
# TODO: 5. 融合是否比最好的单模型更好？
```

<details>
<summary>点击查看答案</summary>

```python
from sklearn.linear_model import Ridge

m1 = Ridge(alpha=1.0).fit(X_train, y_train)
m2 = xgb.XGBRegressor(200, random_state=42).fit(X_train, y_train)
m3 = lgb.LGBMRegressor(200, random_state=42, verbose=-1).fit(X_train, y_train)

p1 = m1.predict(X_test)
p2 = m2.predict(X_test)
p3 = m3.predict(X_test)

for name, p in [('Ridge', p1), ('XGB', p2), ('LGB', p3)]:
    rmse = np.sqrt(mean_squared_error(y_test, p))
    print(f"{name}: RMSE = {rmse:.4f}")

# 简单平均
avg = (p1 + p2 + p3) / 3
print(f"简单平均: RMSE = {np.sqrt(mean_squared_error(y_test, avg)):.4f}")

# 网格搜索最优权重
best_rmse = 999
best_w = None
for w1 in np.arange(0.1, 0.8, 0.1):
    for w2 in np.arange(0.1, 0.9 - w1, 0.1):
        w3 = 1 - w1 - w2
        if w3 < 0.05: continue
        pred = w1*p1 + w2*p2 + w3*p3
        rmse = np.sqrt(mean_squared_error(y_test, pred))
        if rmse < best_rmse:
            best_rmse = rmse
            best_w = (w1, w2, w3)

print(f"\n最优权重: Ridge={best_w[0]:.1f}, XGB={best_w[1]:.1f}, LGB={best_w[2]:.1f}")
print(f"最优融合 RMSE: {best_rmse:.4f}")
```
</details>

---

### 知识点 37：Stacking

**Demo**：
```python
from sklearn.ensemble import StackingClassifier
from sklearn.linear_model import LogisticRegression

# Stacking：用多个基模型的预测作为新特征，训练一个元模型
stacking = StackingClassifier(
    estimators=[
        ('lgb', lgb.LGBMClassifier(100, random_state=42, verbose=-1)),
        ('xgb', xgb.XGBClassifier(100, random_state=42, eval_metric='logloss')),
        ('rf', RandomForestClassifier(200, random_state=42)),
    ],
    final_estimator=LogisticRegression(),
    cv=5  # 内部用 5-Fold 生成 OOF 预测
)

data = load_breast_cancer()
X_train, X_test, y_train, y_test = train_test_split(
    data.data, data.target, test_size=0.2, random_state=42
)

stacking.fit(X_train, y_train)
print(f"Stacking 准确率: {stacking.score(X_test, y_test):.4f}")
```

**练习 5.2.2** ⭐⭐⭐
```python
from sklearn.ensemble import StackingRegressor
from sklearn.linear_model import Ridge

# TODO: 1. 对 California Housing 构建 Stacking 回归模型
#    base: Ridge, XGBRegressor, LGBMRegressor
#    meta: Ridge
# TODO: 2. 计算 Stacking 的 RMSE
# TODO: 3. 对比 Stacking vs 简单平均 vs 单模型，谁最好？
# TODO: 4. 尝试用 LightGBM 作为 meta learner 代替 Ridge，效果有变化吗？
```

<details>
<summary>点击查看答案</summary>

```python
from sklearn.ensemble import StackingRegressor

housing = fetch_california_housing()
X_train, X_test, y_train, y_test = train_test_split(
    housing.data, housing.target, test_size=0.2, random_state=42
)

# 1-2
stacking = StackingRegressor(
    estimators=[
        ('ridge', Ridge(alpha=1.0)),
        ('xgb', xgb.XGBRegressor(200, random_state=42)),
        ('lgb', lgb.LGBMRegressor(200, random_state=42, verbose=-1)),
    ],
    final_estimator=Ridge(),
    cv=5
)
stacking.fit(X_train, y_train)
rmse_stack = np.sqrt(mean_squared_error(y_test, stacking.predict(X_test)))
print(f"Stacking (Ridge meta): RMSE = {rmse_stack:.4f}")

# 4 LightGBM 作为 meta
stacking_lgb = StackingRegressor(
    estimators=[
        ('ridge', Ridge(alpha=1.0)),
        ('xgb', xgb.XGBRegressor(200, random_state=42)),
        ('lgb', lgb.LGBMRegressor(200, random_state=42, verbose=-1)),
    ],
    final_estimator=lgb.LGBMRegressor(50, random_state=42, verbose=-1),
    cv=5
)
stacking_lgb.fit(X_train, y_train)
rmse_stack_lgb = np.sqrt(mean_squared_error(y_test, stacking_lgb.predict(X_test)))
print(f"Stacking (LGB meta): RMSE = {rmse_stack_lgb:.4f}")
```
</details>

---

## 5.3 高级特征工程

### 练习 5.3.1：Target Encoding（防泄漏版）⭐⭐⭐

```python
from sklearn.model_selection import KFold

np.random.seed(42)
n = 1000
df = pd.DataFrame({
    'city': np.random.choice(['A','B','C','D','E','F','G','H'], n),
    'category': np.random.choice(['x','y','z'], n),
    'value': np.random.randn(n),
    'target': np.random.randint(0, 2, n),
})

# TODO: 1. 实现安全的 Target Encoding（在 K-Fold 内计算，防止数据泄漏）
#    思路：
#    - 对训练数据，用其他 fold 的目标均值编码当前 fold
#    - 对测试数据，用全部训练数据的目标均值编码

def safe_target_encode(train_df, col, target, n_folds=5):
    """安全的 Target Encoding"""
    result = pd.Series(index=train_df.index, dtype=float)
    kf = KFold(n_splits=n_folds, shuffle=True, random_state=42)

    for tr_idx, val_idx in kf.split(train_df):
        # TODO: 用 tr_idx 部分的数据计算每个类别的目标均值
        # TODO: 用这个均值映射 val_idx 部分的数据
        pass

    return result

# TODO: 2. 对 'city' 和 'category' 分别做安全 Target Encoding
# TODO: 3. 与普通 Target Encoding（直接用全部数据）对比，值是否不同？
```

<details>
<summary>点击查看答案</summary>

```python
def safe_target_encode(train_df, col, target, n_folds=5):
    result = pd.Series(index=train_df.index, dtype=float)
    kf = KFold(n_splits=n_folds, shuffle=True, random_state=42)

    for tr_idx, val_idx in kf.split(train_df):
        mean_target = train_df.iloc[tr_idx].groupby(col)[target].mean()
        result.iloc[val_idx] = train_df.iloc[val_idx][col].map(mean_target)

    # 用全局均值填充新类别的 NaN
    global_mean = train_df[target].mean()
    result.fillna(global_mean, inplace=True)
    return result

# 2
df['city_te'] = safe_target_encode(df, 'city', 'target')
df['category_te'] = safe_target_encode(df, 'category', 'target')

# 3 对比普通版
naive_te = df.groupby('city')['target'].mean()
df['city_naive_te'] = df['city'].map(naive_te)

print("安全版 vs 普通版（差异）:")
print((df['city_te'] - df['city_naive_te']).describe())
print("\n差异很小但不为零 → 安全版避免了数据泄漏")
```
</details>

---

## 5.4 竞赛实验管理

### 练习 5.4.1：实验记录模板 ⭐

```python
# TODO: 创建一个实验记录系统
# 每次实验记录：实验名、特征列表、模型名、超参数、CV分数、LB分数、备注

experiments = []

def log_experiment(name, features, model_name, params, cv_score, lb_score=None, note=""):
    experiments.append({
        'name': name,
        'n_features': len(features),
        'model': model_name,
        'params': str(params),
        'cv_score': cv_score,
        'lb_score': lb_score,
        'note': note,
        'timestamp': pd.Timestamp.now()
    })

# 示例
log_experiment(
    name="baseline_v1",
    features=['pclass', 'sex', 'age', 'fare'],
    model_name="LightGBM",
    params={'n_estimators': 100, 'max_depth': 5},
    cv_score=0.8234,
    lb_score=0.8100,
    note="最基础的特征集"
)

log_experiment(
    name="feature_eng_v1",
    features=['pclass', 'sex', 'age', 'fare', 'family_size', 'is_alone'],
    model_name="LightGBM",
    params={'n_estimators': 200, 'max_depth': 6},
    cv_score=0.8456,
    lb_score=0.8300,
    note="加了家庭特征"
)

# 查看所有实验
exp_df = pd.DataFrame(experiments)
print(exp_df[['name', 'model', 'cv_score', 'lb_score', 'note']])

# TODO: 1. 再添加 3 个实验记录（模拟不同尝试）
# TODO: 2. 按 cv_score 排序找出最佳实验
# TODO: 3. 画 cv_score 随实验推进的趋势图
```

---

## 阶段五综合测试 ⭐⭐⭐

```python
# 综合实战：参加一个完整的（模拟）竞赛

from sklearn.datasets import make_classification

# 生成一个有挑战性的分类数据集
X, y = make_classification(
    n_samples=5000, n_features=20, n_informative=10,
    n_redundant=5, n_clusters_per_class=3,
    weights=[0.7, 0.3], random_state=42
)
X = pd.DataFrame(X, columns=[f'feature_{i}' for i in range(20)])

# 按竞赛流程完成：
# 1. EDA：数据形状、类别分布、特征分布、相关性
# 2. 特征工程：构造交互特征、多项式特征、统计特征
# 3. 用 Optuna 调优 LightGBM（50 trials）
# 4. 训练 3 个不同模型（LGB/XGB/RF），用 5-Fold CV 评估
# 5. 用加权平均融合
# 6. 用 Stacking 融合
# 7. 对比：单模型 vs 加权平均 vs Stacking，用 AUC 评估
# 8. 输出最终最佳方案的 classification_report
# 9. 记录完整的实验日志
```
