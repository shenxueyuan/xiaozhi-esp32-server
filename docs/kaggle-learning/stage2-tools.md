# 阶段二：Python 数据科学工具链 — 练习手册

> 前置要求：完成阶段一，已安装 numpy/pandas/matplotlib/seaborn
> 工具：Jupyter Lab 或 Kaggle Notebook
> 预计耗时：3-4 周

---

## 2.1 NumPy 进阶

### 知识点 13：数组创建与形状操作

**Demo**：
```python
import numpy as np

# 各种创建方式
a = np.zeros((3, 4))          # 3x4 全零
b = np.ones((2, 5))           # 2x5 全一
c = np.eye(3)                 # 3x3 单位矩阵
d = np.arange(0, 20, 2)       # [0,2,4,...,18]
e = np.linspace(0, 1, 5)      # [0, 0.25, 0.5, 0.75, 1]
f = np.random.randn(3, 4)     # 3x4 标准正态随机

# 形状操作
arr = np.arange(12)            # [0,1,2,...,11]
print(arr.reshape(3, 4))       # 变成 3x4
print(arr.reshape(2, -1))      # -1 表示自动计算 → 2x6
print(arr.reshape(3, 4).flatten())  # 拉回一维
```

**练习 2.1.1** ⭐
```python
# TODO: 1. 创建一个 5x5 的单位矩阵（对角线为1，其余为0）
# TODO: 2. 创建一个包含 1 到 100 的数组，然后 reshape 为 10x10
# TODO: 3. 创建一个 3x3 矩阵，值为 1-9，然后转置
# TODO: 4. 把一个 (2,3,4) 的三维数组 flatten 为一维，长度是多少？
```

<details>
<summary>点击查看答案</summary>

```python
# 1
print(np.eye(5))

# 2
arr = np.arange(1, 101).reshape(10, 10)
print(arr)

# 3
m = np.arange(1, 10).reshape(3, 3)
print("原始:\n", m)
print("转置:\n", m.T)

# 4
arr3d = np.random.randn(2, 3, 4)
flat = arr3d.flatten()
print(f"长度: {len(flat)}")  # 2*3*4 = 24
```
</details>

---

### 知识点 14：索引、切片与条件筛选

**Demo**：
```python
arr = np.array([[10, 20, 30, 40],
                [50, 60, 70, 80],
                [90, 100, 110, 120]])

# 基本索引
print(arr[1, 2])        # 70（第2行第3列）
print(arr[0])            # [10,20,30,40]（第1行）
print(arr[:, 1])         # [20,60,100]（第2列）
print(arr[0:2, 1:3])     # [[20,30],[60,70]]（子矩阵）

# 条件筛选（非常重要！）
print(arr[arr > 50])     # 所有大于50的元素
print(arr[arr % 30 == 0])  # 所有能被30整除的元素

# 条件赋值
arr_copy = arr.copy()
arr_copy[arr_copy > 80] = 0  # 大于80的都设为0
print(arr_copy)
```

**练习 2.1.2** ⭐⭐
```python
np.random.seed(42)
scores = np.random.randint(0, 100, size=(5, 4))  # 5个学生，4门课
# 列分别代表：数学、语文、英语、物理

print("成绩表:\n", scores)

# TODO: 1. 获取第3个学生的所有成绩
# TODO: 2. 获取所有学生的英语成绩（第3列）
# TODO: 3. 找出所有不及格的成绩（<60）
# TODO: 4. 统计不及格成绩的个数
# TODO: 5. 把所有不及格的成绩改为 60（补到及格线）
# TODO: 6. 找出数学成绩最高的学生是第几个（用 argmax）
```

<details>
<summary>点击查看答案</summary>

```python
np.random.seed(42)
scores = np.random.randint(0, 100, size=(5, 4))
print("成绩表:\n", scores)

# 1
print("学生3:", scores[2])

# 2
print("英语成绩:", scores[:, 2])

# 3
print("不及格:", scores[scores < 60])

# 4
print("不及格个数:", np.sum(scores < 60))

# 5
scores_fixed = scores.copy()
scores_fixed[scores_fixed < 60] = 60
print("补分后:\n", scores_fixed)

# 6
print("数学最高:", scores[:, 0].argmax(), "号学生")
```
</details>

---

### 知识点 15：广播机制（Broadcasting）

**概念**：NumPy 自动扩展不同形状数组使其可以运算。这是 NumPy 最"魔法"的特性。

**Demo**：
```python
# 场景：给每个学生的每科成绩加上不同的附加分
scores = np.array([[80, 70, 90],    # 学生1
                   [60, 85, 75],    # 学生2
                   [95, 80, 65]])   # 学生3
# 数学加5分，语文加10分，英语加3分
bonus = np.array([5, 10, 3])

# Broadcasting: (3,3) + (3,) → NumPy 自动把 bonus 扩展为 (3,3)
result = scores + bonus
print(result)
# [[85, 80, 93],
#  [65, 95, 78],
#  [100, 90, 68]]

# 场景2：标准化（每列减去该列均值，除以标准差）
mean = scores.mean(axis=0)   # 每列均值 (3,)
std = scores.std(axis=0)     # 每列标准差 (3,)
normalized = (scores - mean) / std  # (3,3) 操作 (3,) → 自动广播
print(f"标准化后:\n{normalized}")
```

**练习 2.1.3** ⭐⭐
```python
# 练习：用广播实现常见数据处理操作

# 商品价格矩阵：4个商品 x 3个地区
prices = np.array([[100, 110, 95],
                   [200, 220, 190],
                   [50,  55,  48],
                   [300, 330, 280]])

# TODO: 1. 每个地区的物价统一上涨 10%（乘以 1.1）
# TODO: 2. 不同地区有不同税率 [0.08, 0.10, 0.06]，计算含税价格
# TODO: 3. 计算每个商品在 3 个地区的均价（每行的均值）
# TODO: 4. 计算每个商品相对于其均价的偏差（每个价格减去该商品的均价）
#    提示：均价 shape 是 (4,)，需要 reshape 为 (4,1) 才能广播
```

<details>
<summary>点击查看答案</summary>

```python
# 1
print("涨价10%:\n", prices * 1.1)

# 2
tax_rates = np.array([0.08, 0.10, 0.06])
print("含税价:\n", prices * (1 + tax_rates))

# 3
avg_prices = prices.mean(axis=1)
print("每商品均价:", avg_prices)

# 4
deviation = prices - avg_prices.reshape(-1, 1)  # (4,3) - (4,1) → 广播
print("价格偏差:\n", deviation)
```
</details>

---

## 2.2 Pandas 核心（最重要！）

### 知识点 16：DataFrame 创建与基本探索

**Demo**：
```python
import pandas as pd

# 从字典创建
df = pd.DataFrame({
    'name': ['张三', '李四', '王五', '赵六', '钱七'],
    'age': [25, 30, 28, 35, 22],
    'city': ['北京', '上海', '北京', '广州', '上海'],
    'salary': [15000, 25000, 18000, 30000, 12000]
})

print(df.head())        # 前5行
print(df.shape)         # (5, 4)
print(df.dtypes)        # 每列类型
print(df.describe())    # 数值列统计
print(df.info())        # 全面信息
```

**练习 2.2.1** ⭐
```python
# 创建一个 DataFrame，包含以下信息（至少8行数据）：
# 列：学生姓名(name)、年级(grade: 1-3)、数学成绩(math)、语文成绩(chinese)、性别(gender: M/F)

# TODO: 1. 创建 DataFrame
# TODO: 2. 查看前 3 行
# TODO: 3. 查看形状、数据类型
# TODO: 4. 查看数值列的统计摘要
# TODO: 5. 查看每列有多少唯一值
```

<details>
<summary>点击查看答案</summary>

```python
df = pd.DataFrame({
    'name': ['小明', '小红', '小刚', '小丽', '小华', '小芳', '小强', '小美'],
    'grade': [1, 2, 1, 3, 2, 3, 1, 2],
    'math': [85, 92, 78, 95, 88, 76, 90, 82],
    'chinese': [90, 88, 82, 80, 95, 92, 75, 88],
    'gender': ['M', 'F', 'M', 'F', 'M', 'F', 'M', 'F']
})

print(df.head(3))
print(f"形状: {df.shape}")
print(f"类型:\n{df.dtypes}")
print(df.describe())
print(f"唯一值:\n{df.nunique()}")
```
</details>

---

### 知识点 17：数据选择与过滤

**Demo**：
```python
df = pd.DataFrame({
    'name': ['张三', '李四', '王五', '赵六', '钱七', '孙八'],
    'age': [25, 30, 28, 35, 22, 40],
    'city': ['北京', '上海', '北京', '广州', '上海', '北京'],
    'salary': [15000, 25000, 18000, 30000, 12000, 35000]
})

# 选择列
print(df['name'])                    # 单列 → Series
print(df[['name', 'salary']])        # 多列 → DataFrame

# 条件过滤
print(df[df['age'] > 28])           # 年龄大于28
print(df[df['city'] == '北京'])      # 北京的人
print(df[(df['age'] > 25) & (df['salary'] > 20000)])  # 组合条件

# loc（标签） vs iloc（位置）
print(df.loc[0:2, 'name':'city'])   # 标签索引（包含右端点！）
print(df.iloc[0:2, 0:3])            # 位置索引（不包含右端点）
```

**练习 2.2.2** ⭐⭐
```python
# 用 Scikit-learn 内置的鸢尾花数据集
from sklearn.datasets import load_iris

iris = load_iris()
df = pd.DataFrame(iris.data, columns=iris.feature_names)
df['species'] = iris.target  # 0=setosa, 1=versicolor, 2=virginica

# TODO: 1. 只选择 'sepal length (cm)' 和 'petal length (cm)' 两列
# TODO: 2. 筛选出 species == 0 的所有行
# TODO: 3. 筛选出花萼长度 > 6.0 且花瓣长度 > 5.0 的行
# TODO: 4. 用 iloc 获取第 50-55 行
# TODO: 5. 每个物种各有多少条数据？（提示：value_counts）
```

<details>
<summary>点击查看答案</summary>

```python
from sklearn.datasets import load_iris

iris = load_iris()
df = pd.DataFrame(iris.data, columns=iris.feature_names)
df['species'] = iris.target

# 1
print(df[['sepal length (cm)', 'petal length (cm)']].head())

# 2
print(df[df['species'] == 0].head())

# 3
mask = (df['sepal length (cm)'] > 6.0) & (df['petal length (cm)'] > 5.0)
print(df[mask])

# 4
print(df.iloc[50:56])

# 5
print(df['species'].value_counts())  # 每种各50条
```
</details>

---

### 知识点 18：缺失值处理

**概念**：真实数据几乎总有缺失值（NaN）。处理方式：删除、填充（均值/中位数/众数/特定值）。

**Demo**：
```python
df = pd.DataFrame({
    'name': ['A', 'B', 'C', 'D', 'E'],
    'age': [25, None, 30, None, 28],
    'salary': [5000, 8000, None, 12000, 7000],
    'city': ['北京', '上海', '北京', None, '广州']
})

# 检查缺失值
print(df.isnull().sum())       # 每列缺失数量
print(df.isnull().mean() * 100)  # 缺失百分比

# 删除含缺失值的行
print(df.dropna())             # 删任意列有NaN的行
print(df.dropna(subset=['age']))  # 只看 age 列

# 填充
df['age'].fillna(df['age'].median(), inplace=True)   # 数值列用中位数
df['city'].fillna('未知', inplace=True)              # 类别列用固定值
df['salary'].fillna(df['salary'].mean(), inplace=True)  # 用均值
print(df)
```

**练习 2.2.3** ⭐⭐
```python
np.random.seed(42)
n = 100
df = pd.DataFrame({
    'height': np.where(np.random.rand(n) > 0.1, np.random.normal(170, 8, n), np.nan),
    'weight': np.where(np.random.rand(n) > 0.2, np.random.normal(65, 10, n), np.nan),
    'blood_type': np.where(np.random.rand(n) > 0.15,
                           np.random.choice(['A', 'B', 'O', 'AB'], n), None),
    'score': np.where(np.random.rand(n) > 0.05, np.random.randint(60, 100, n), np.nan)
})

# TODO: 1. 查看每列的缺失数量和缺失百分比
# TODO: 2. height 用中位数填充
# TODO: 3. weight 用均值填充
# TODO: 4. blood_type 用众数（出现最多的值）填充
# TODO: 5. score 缺失较少，直接删除含缺失的行
# TODO: 6. 最终确认没有缺失值
```

<details>
<summary>点击查看答案</summary>

```python
np.random.seed(42)
n = 100
df = pd.DataFrame({
    'height': np.where(np.random.rand(n) > 0.1, np.random.normal(170, 8, n), np.nan),
    'weight': np.where(np.random.rand(n) > 0.2, np.random.normal(65, 10, n), np.nan),
    'blood_type': np.where(np.random.rand(n) > 0.15,
                           np.random.choice(['A', 'B', 'O', 'AB'], n), None),
    'score': np.where(np.random.rand(n) > 0.05, np.random.randint(60, 100, n), np.nan)
})

# 1
print("缺失数量:\n", df.isnull().sum())
print("缺失百分比:\n", (df.isnull().mean() * 100).round(1))

# 2
df['height'].fillna(df['height'].median(), inplace=True)

# 3
df['weight'].fillna(df['weight'].mean(), inplace=True)

# 4
mode_blood = df['blood_type'].mode()[0]
df['blood_type'].fillna(mode_blood, inplace=True)

# 5
df.dropna(subset=['score'], inplace=True)

# 6
print("处理后缺失:\n", df.isnull().sum())
print(f"剩余行数: {len(df)}")
```
</details>

---

### 知识点 19：分组聚合（GroupBy）

**概念**：类似 SQL 的 GROUP BY，按某列分组后做聚合统计。数据分析的核心操作。

**Demo**：
```python
df = pd.DataFrame({
    'city': ['北京','上海','北京','广州','上海','广州','北京','上海'],
    'department': ['技术','技术','销售','技术','销售','销售','技术','技术'],
    'salary': [20000, 25000, 15000, 18000, 22000, 16000, 28000, 30000],
    'age': [28, 32, 25, 30, 27, 35, 40, 29]
})

# 基本分组
print(df.groupby('city')['salary'].mean())         # 每个城市的平均薪资
print(df.groupby('city')['salary'].agg(['mean', 'median', 'std']))  # 多个聚合

# 多列分组
print(df.groupby(['city', 'department'])['salary'].mean())

# 自定义聚合
print(df.groupby('city').agg({
    'salary': ['mean', 'max', 'min'],
    'age': ['mean', 'count']
}))
```

**练习 2.2.4** ⭐⭐
```python
np.random.seed(42)
n = 200
df = pd.DataFrame({
    'student_id': range(1, n+1),
    'grade': np.random.choice([1, 2, 3], n),
    'gender': np.random.choice(['M', 'F'], n),
    'math': np.random.randint(40, 100, n),
    'chinese': np.random.randint(50, 100, n),
    'english': np.random.randint(30, 100, n),
})

# TODO: 1. 按年级分组，计算每科的平均成绩
# TODO: 2. 按性别分组，计算数学的 [均值, 中位数, 最高分, 最低分]
# TODO: 3. 按 [年级, 性别] 分组，计算总人数和数学平均分
# TODO: 4. 找出数学平均分最高的年级
# TODO: 5. 新增列 'total'（三科总分），然后按年级求总分的均值和标准差
```

<details>
<summary>点击查看答案</summary>

```python
np.random.seed(42)
n = 200
df = pd.DataFrame({
    'student_id': range(1, n+1),
    'grade': np.random.choice([1, 2, 3], n),
    'gender': np.random.choice(['M', 'F'], n),
    'math': np.random.randint(40, 100, n),
    'chinese': np.random.randint(50, 100, n),
    'english': np.random.randint(30, 100, n),
})

# 1
print(df.groupby('grade')[['math', 'chinese', 'english']].mean())

# 2
print(df.groupby('gender')['math'].agg(['mean', 'median', 'max', 'min']))

# 3
print(df.groupby(['grade', 'gender']).agg(
    count=('student_id', 'count'),
    math_avg=('math', 'mean')
))

# 4
grade_math = df.groupby('grade')['math'].mean()
print(f"数学最高年级: {grade_math.idxmax()}, 均分: {grade_math.max():.1f}")

# 5
df['total'] = df['math'] + df['chinese'] + df['english']
print(df.groupby('grade')['total'].agg(['mean', 'std']).round(1))
```
</details>

---

### 知识点 20：数据合并（merge / concat）

**Demo**：
```python
# 两张表
students = pd.DataFrame({
    'student_id': [1, 2, 3, 4],
    'name': ['张三', '李四', '王五', '赵六']
})

scores = pd.DataFrame({
    'student_id': [1, 2, 3, 5],
    'math': [90, 85, 78, 92],
    'chinese': [88, 92, 80, 85]
})

# inner join：只保留两边都有的
print(pd.merge(students, scores, on='student_id', how='inner'))

# left join：保留左表所有行
print(pd.merge(students, scores, on='student_id', how='left'))

# concat：上下拼接
part1 = pd.DataFrame({'A': [1,2], 'B': [3,4]})
part2 = pd.DataFrame({'A': [5,6], 'B': [7,8]})
print(pd.concat([part1, part2], ignore_index=True))
```

**练习 2.2.5** ⭐⭐
```python
# 三张表
orders = pd.DataFrame({
    'order_id': [101, 102, 103, 104, 105],
    'customer_id': [1, 2, 1, 3, 4],
    'amount': [200, 350, 150, 500, 100]
})

customers = pd.DataFrame({
    'customer_id': [1, 2, 3],
    'name': ['Alice', 'Bob', 'Charlie'],
    'city': ['北京', '上海', '广州']
})

products = pd.DataFrame({
    'order_id': [101, 102, 103, 104, 105],
    'product': ['手机', '电脑', '耳机', '平板', '充电器']
})

# TODO: 1. 将 orders 和 customers 用 customer_id 做 left join
# TODO: 2. 结果中有没有 NaN？哪个 customer 没有匹配到？
# TODO: 3. 继续将结果和 products 用 order_id 合并
# TODO: 4. 最终表应该有：order_id, customer_id, amount, name, city, product
# TODO: 5. 按 city 分组，计算每个城市的总消费额
```

<details>
<summary>点击查看答案</summary>

```python
# 1
result = pd.merge(orders, customers, on='customer_id', how='left')
print(result)

# 2 customer_id=4 没有匹配到，name和city为NaN
print("缺失:\n", result.isnull().sum())

# 3
result = pd.merge(result, products, on='order_id', how='left')
print(result)

# 4
print(result.columns.tolist())

# 5
print(result.groupby('city')['amount'].sum())
# 注意 customer_id=4 的城市是 NaN，会被分到 NaN 组
```
</details>

---

### 知识点 21：apply 与 lambda

**概念**：对 DataFrame 的行或列应用自定义函数，最灵活的数据处理方式。

**Demo**：
```python
df = pd.DataFrame({
    'name': ['张三', '李四', '王五'],
    'math': [85, 92, 78],
    'chinese': [90, 88, 82]
})

# lambda 快速创建函数
df['math_grade'] = df['math'].apply(lambda x: '优' if x >= 90 else '良' if x >= 80 else '中')

# 对每行操作
df['total'] = df.apply(lambda row: row['math'] + row['chinese'], axis=1)

# 更复杂的函数
def classify_student(row):
    avg = (row['math'] + row['chinese']) / 2
    if avg >= 90: return 'A'
    elif avg >= 80: return 'B'
    else: return 'C'

df['level'] = df.apply(classify_student, axis=1)
print(df)
```

**练习 2.2.6** ⭐⭐
```python
df = pd.DataFrame({
    'product': ['iPhone 15', 'MacBook Pro', 'AirPods', 'iPad', 'Apple Watch'],
    'price': [7999, 14999, 1399, 3499, 2999],
    'stock': [100, 30, 500, 200, 150],
    'category': ['手机', '电脑', '配件', '平板', '手表']
})

# TODO: 1. 新增列 'price_level'：>=10000 为"高端"，>=3000 为"中端"，其余"入门"
# TODO: 2. 新增列 'total_value'：price * stock（库存总价值）
# TODO: 3. 新增列 'discount_price'：电脑类打8折，配件打9折，其余不打折
#    提示：用 apply + 自定义函数
# TODO: 4. 按 price_level 分组，统计每组的商品数量和平均价格
```

<details>
<summary>点击查看答案</summary>

```python
# 1
df['price_level'] = df['price'].apply(
    lambda x: '高端' if x >= 10000 else '中端' if x >= 3000 else '入门'
)

# 2
df['total_value'] = df['price'] * df['stock']

# 3
def calc_discount(row):
    if row['category'] == '电脑':
        return row['price'] * 0.8
    elif row['category'] == '配件':
        return row['price'] * 0.9
    else:
        return row['price']

df['discount_price'] = df.apply(calc_discount, axis=1)
print(df)

# 4
print(df.groupby('price_level').agg(
    count=('product', 'count'),
    avg_price=('price', 'mean')
))
```
</details>

---

## 2.3 Matplotlib + Seaborn 可视化

### 知识点 22：基础图表

**Demo**：
```python
import matplotlib.pyplot as plt
import seaborn as sns
plt.rcParams['font.sans-serif'] = ['Arial Unicode MS']  # Mac 中文支持
plt.rcParams['axes.unicode_minus'] = False

# 四种基础图表
fig, axes = plt.subplots(2, 2, figsize=(12, 10))

# 折线图
x = np.arange(1, 13)
y = [12, 15, 13, 18, 22, 28, 32, 30, 25, 20, 15, 13]
axes[0,0].plot(x, y, marker='o', color='steelblue')
axes[0,0].set_title('月平均气温')
axes[0,0].set_xlabel('月份')
axes[0,0].set_ylabel('温度(°C)')

# 柱状图
categories = ['Python', 'Java', 'JS', 'C++', 'Go']
values = [35, 25, 20, 12, 8]
axes[0,1].bar(categories, values, color='coral')
axes[0,1].set_title('编程语言流行度')

# 散点图
np.random.seed(42)
x = np.random.randn(100)
y = 2*x + np.random.randn(100)*0.5
axes[1,0].scatter(x, y, alpha=0.6, s=30)
axes[1,0].set_title('散点图')

# 直方图
data = np.random.normal(100, 15, 1000)
axes[1,1].hist(data, bins=30, edgecolor='black', alpha=0.7)
axes[1,1].set_title('成绩分布')

plt.tight_layout()
plt.show()
```

**练习 2.3.1** ⭐
```python
# TODO: 画一个 2x2 的子图，展示以下内容：
# 子图1（折线图）：y = sin(x)，x 从 0 到 2π，标注标题和坐标轴
# 子图2（柱状图）：5个城市的人口（自编数据）
# 子图3（散点图）：100个随机点，x 和 y 都是正态分布
# 子图4（直方图）：1000个均匀分布的随机数
```

<details>
<summary>点击查看答案</summary>

```python
fig, axes = plt.subplots(2, 2, figsize=(12, 10))

# 子图1
x = np.linspace(0, 2*np.pi, 100)
axes[0,0].plot(x, np.sin(x), color='blue')
axes[0,0].set_title('y = sin(x)')
axes[0,0].set_xlabel('x')
axes[0,0].set_ylabel('y')

# 子图2
cities = ['北京', '上海', '广州', '深圳', '成都']
population = [2189, 2487, 1868, 1756, 2093]
axes[0,1].bar(cities, population, color='teal')
axes[0,1].set_title('城市人口（万）')

# 子图3
np.random.seed(42)
axes[1,0].scatter(np.random.randn(100), np.random.randn(100), alpha=0.5)
axes[1,0].set_title('随机散点')

# 子图4
axes[1,1].hist(np.random.uniform(0, 1, 1000), bins=30, edgecolor='black')
axes[1,1].set_title('均匀分布')

plt.tight_layout()
plt.show()
```
</details>

---

### 知识点 23：Seaborn 统计可视化

**Demo**：
```python
# 用 Seaborn 的内置数据集
tips = sns.load_dataset('tips')
print(tips.head())

fig, axes = plt.subplots(2, 3, figsize=(18, 10))

# 分布图
sns.histplot(tips['total_bill'], kde=True, ax=axes[0,0])
axes[0,0].set_title('账单分布')

# 箱线图
sns.boxplot(x='day', y='total_bill', data=tips, ax=axes[0,1])
axes[0,1].set_title('每天的账单分布')

# 散点图+颜色
sns.scatterplot(x='total_bill', y='tip', hue='sex', data=tips, ax=axes[0,2])
axes[0,2].set_title('账单vs小费（按性别）')

# 计数图
sns.countplot(x='day', hue='sex', data=tips, ax=axes[1,0])
axes[1,0].set_title('每天的就餐人数')

# 热力图
corr = tips.select_dtypes(include='number').corr()
sns.heatmap(corr, annot=True, cmap='coolwarm', ax=axes[1,1])
axes[1,1].set_title('相关性热力图')

# 小提琴图
sns.violinplot(x='day', y='total_bill', hue='sex', data=tips, split=True, ax=axes[1,2])
axes[1,2].set_title('账单分布（小提琴图）')

plt.tight_layout()
plt.show()
```

**练习 2.3.2** ⭐⭐
```python
# 用鸢尾花数据集做一组完整的 EDA 可视化
from sklearn.datasets import load_iris

iris = load_iris()
df = pd.DataFrame(iris.data, columns=['花萼长', '花萼宽', '花瓣长', '花瓣宽'])
df['品种'] = [iris.target_names[t] for t in iris.target]

# TODO: 1. 画 4 个特征的分布直方图（2x2 子图），按品种用不同颜色
# TODO: 2. 画相关性热力图（4个数值特征之间的相关系数）
# TODO: 3. 画 花瓣长 vs 花瓣宽 的散点图，按品种着色
# TODO: 4. 画每个品种的花萼长箱线图
# TODO: 5. 从图中你能得出什么结论？（写在 print 中）
```

<details>
<summary>点击查看答案</summary>

```python
from sklearn.datasets import load_iris

iris = load_iris()
df = pd.DataFrame(iris.data, columns=['花萼长', '花萼宽', '花瓣长', '花瓣宽'])
df['品种'] = [iris.target_names[t] for t in iris.target]

# 1 分布直方图
fig, axes = plt.subplots(2, 2, figsize=(12, 10))
for i, col in enumerate(['花萼长', '花萼宽', '花瓣长', '花瓣宽']):
    ax = axes[i//2, i%2]
    for species in df['品种'].unique():
        ax.hist(df[df['品种']==species][col], bins=15, alpha=0.5, label=species)
    ax.set_title(col)
    ax.legend()
plt.tight_layout()
plt.show()

# 2 相关性热力图
plt.figure(figsize=(8, 6))
corr = df[['花萼长', '花萼宽', '花瓣长', '花瓣宽']].corr()
sns.heatmap(corr, annot=True, cmap='coolwarm', center=0)
plt.title('特征相关性')
plt.show()

# 3 散点图
plt.figure(figsize=(8, 6))
for species in df['品种'].unique():
    subset = df[df['品种']==species]
    plt.scatter(subset['花瓣长'], subset['花瓣宽'], label=species, alpha=0.7)
plt.xlabel('花瓣长')
plt.ylabel('花瓣宽')
plt.legend()
plt.title('花瓣长 vs 花瓣宽')
plt.show()

# 4 箱线图
plt.figure(figsize=(8, 5))
sns.boxplot(x='品种', y='花萼长', data=df)
plt.title('各品种花萼长度')
plt.show()

# 5
print("结论：")
print("1. 花瓣长和花瓣宽高度正相关(r≈0.96)")
print("2. setosa 品种的花瓣明显更小，很容易区分")
print("3. versicolor 和 virginica 有部分重叠，但 virginica 整体更大")
print("4. 花瓣特征比花萼特征更适合用来区分品种")
```
</details>

---

### 知识点 24：完整 EDA 流程

**概念**：EDA（Exploratory Data Analysis）是每个数据项目/竞赛的第一步。

**Demo + 练习 2.3.3** ⭐⭐⭐ (综合大练习)
```python
# 这是一个完整的 EDA 模板练习
# 用 Seaborn 内置的 Titanic 数据集（和 Kaggle Titanic 类似）

titanic = sns.load_dataset('titanic')

# TODO: 按顺序完成以下 EDA 步骤：

# === 第1步：数据概览 ===
# TODO: 打印前5行、形状、数据类型、统计摘要

# === 第2步：缺失值分析 ===
# TODO: 查看每列缺失数量和百分比
# TODO: 画缺失值柱状图

# === 第3步：目标变量分析 ===
# TODO: 查看 survived 的分布（0=遇难 1=幸存）
# TODO: 画计数图

# === 第4步：数值特征分析 ===
# TODO: 画 age 和 fare 的分布直方图
# TODO: 画年龄 vs 生存的箱线图

# === 第5步：类别特征分析 ===
# TODO: 画 sex vs survived 的计数图
# TODO: 画 pclass vs survived 的计数图
# TODO: 计算不同性别的生存率

# === 第6步：相关性 ===
# TODO: 画数值特征的相关性热力图

# === 第7步：总结发现 ===
# TODO: 用 print 写出 3 个你从 EDA 中发现的规律
```

<details>
<summary>点击查看答案</summary>

```python
titanic = sns.load_dataset('titanic')

# 第1步
print(titanic.head())
print(f"形状: {titanic.shape}")
print(titanic.dtypes)
print(titanic.describe())

# 第2步
missing = titanic.isnull().sum()
missing_pct = (titanic.isnull().mean() * 100).round(1)
print(pd.DataFrame({'缺失数': missing, '缺失%': missing_pct}).sort_values('缺失%', ascending=False))

plt.figure(figsize=(10, 4))
missing[missing > 0].sort_values().plot(kind='barh')
plt.title('缺失值数量')
plt.show()

# 第3步
print(titanic['survived'].value_counts(normalize=True))
plt.figure(figsize=(6, 4))
sns.countplot(x='survived', data=titanic)
plt.title('生存分布 (0=遇难, 1=幸存)')
plt.show()

# 第4步
fig, axes = plt.subplots(1, 3, figsize=(16, 4))
sns.histplot(titanic['age'].dropna(), bins=30, kde=True, ax=axes[0])
axes[0].set_title('年龄分布')
sns.histplot(titanic['fare'], bins=30, kde=True, ax=axes[1])
axes[1].set_title('票价分布')
sns.boxplot(x='survived', y='age', data=titanic, ax=axes[2])
axes[2].set_title('年龄 vs 生存')
plt.tight_layout()
plt.show()

# 第5步
fig, axes = plt.subplots(1, 2, figsize=(12, 4))
sns.countplot(x='sex', hue='survived', data=titanic, ax=axes[0])
axes[0].set_title('性别 vs 生存')
sns.countplot(x='pclass', hue='survived', data=titanic, ax=axes[1])
axes[1].set_title('船舱等级 vs 生存')
plt.tight_layout()
plt.show()

print("各性别生存率:")
print(titanic.groupby('sex')['survived'].mean())

# 第6步
plt.figure(figsize=(8, 6))
corr = titanic.select_dtypes(include='number').corr()
sns.heatmap(corr, annot=True, cmap='coolwarm', center=0)
plt.title('相关性热力图')
plt.show()

# 第7步
print("\n=== EDA 发现 ===")
print("1. 女性生存率(~74%)远高于男性(~19%) → 性别是最强特征")
print("2. 一等舱生存率最高，三等舱最低 → 船舱等级很重要")
print("3. 年龄有约20%缺失值，需要填充处理")
```
</details>

---

## 阶段二综合测试 ⭐⭐⭐

```python
# 最终考核：用 Pandas 完成一个完整的数据分析项目
# 数据：自己构造一个"电商销售数据"

np.random.seed(2024)
n = 500
df = pd.DataFrame({
    'order_id': range(1, n+1),
    'date': pd.date_range('2024-01-01', periods=n, freq='D'),
    'product': np.random.choice(['手机', '电脑', '平板', '耳机', '手表'], n),
    'category': np.random.choice(['电子', '配件'], n, p=[0.7, 0.3]),
    'price': np.random.choice([999, 2999, 4999, 6999, 9999], n),
    'quantity': np.random.randint(1, 5, n),
    'customer_age': np.random.normal(30, 8, n).astype(int),
    'customer_gender': np.random.choice(['M', 'F'], n),
    'city': np.random.choice(['北京', '上海', '广州', '深圳', '成都'], n),
    'rating': np.where(np.random.rand(n) > 0.1,
                       np.random.choice([1,2,3,4,5], n, p=[0.05,0.1,0.2,0.35,0.3]),
                       np.nan)
})

# 完成以下全部任务：
# 1. 数据概览：shape, dtypes, head, describe
# 2. 缺失值处理：rating 用中位数填充
# 3. 新增列：total_amount = price * quantity
# 4. 新增列：month（从 date 中提取月份）
# 5. 按月统计：每月的总销售额、订单数
# 6. 按城市统计：每个城市的平均客单价
# 7. 按产品统计：每个产品的销量排名
# 8. 可视化（至少4张图）：
#    a. 月度销售额趋势（折线图）
#    b. 各城市销售额对比（柱状图）
#    c. 客户年龄分布（直方图）
#    d. 评分与价格的关系（散点图或箱线图）
# 9. 写出 3 个数据洞察
```
