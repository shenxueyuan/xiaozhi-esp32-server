# 阶段一：数学基础 — 练习手册

> 前置要求：已安装 Python 环境（见主文档"零、准备工作"）
> 工具：Jupyter Lab 或 Kaggle Notebook
> 预计耗时：2-3 周

---

## 1.1 线性代数

### 知识点 1：向量基础

**概念**：向量就是一组有序的数字。在数据科学中，一条数据（一个样本）就是一个向量。

**Demo**：
```python
import numpy as np

# 一个学生的数据：[身高cm, 体重kg, 年龄]
student = np.array([170, 65, 20])
print(f"这个学生向量有 {len(student)} 个维度")
print(f"身高: {student[0]}cm, 体重: {student[1]}kg, 年龄: {student[2]}")

# 向量加法：两个学生数据相加（对应元素相加）
student_a = np.array([170, 65, 20])
student_b = np.array([175, 70, 22])
print(f"向量加法: {student_a + student_b}")  # [345, 135, 42]

# 标量乘法：所有元素乘以一个数
print(f"标量乘法(x2): {student_a * 2}")  # [340, 130, 40]
```

**练习 1.1.1** ⭐
```python
# 练习：创建以下向量并完成运算
# a = [3, 5, 7]
# b = [1, 2, 3]

# TODO: 1. 计算 a + b
# TODO: 2. 计算 a - b
# TODO: 3. 计算 3 * a
# TODO: 4. 计算 a 和 b 的逐元素乘法（不是点积）
```

<details>
<summary>点击查看答案</summary>

```python
a = np.array([3, 5, 7])
b = np.array([1, 2, 3])

print("a + b =", a + b)       # [4, 7, 10]
print("a - b =", a - b)       # [2, 3, 4]
print("3 * a =", 3 * a)       # [9, 15, 21]
print("a * b =", a * b)       # [3, 10, 21] （逐元素乘法）
```
</details>

---

### 知识点 2：点积（Dot Product）

**概念**：两个向量对应元素相乘再求和。结果是一个数字（标量）。
神经网络的每一层本质上就是在做点积。

**Demo**：
```python
a = np.array([1, 2, 3])
b = np.array([4, 5, 6])

# 点积 = 1*4 + 2*5 + 3*6 = 4 + 10 + 18 = 32
dot_product = np.dot(a, b)
print(f"点积: {dot_product}")  # 32

# 等价写法
print(f"@ 运算符: {a @ b}")      # 32
print(f"手动计算: {sum(a * b)}")  # 32

# 点积的几何意义：衡量两个向量的"相似度"
# 方向相同 → 点积大（正数）
# 方向垂直 → 点积为 0
# 方向相反 → 点积小（负数）
v1 = np.array([1, 0])  # 指向右
v2 = np.array([0, 1])  # 指向上
v3 = np.array([-1, 0]) # 指向左
print(f"垂直向量的点积: {np.dot(v1, v2)}")   # 0
print(f"相反向量的点积: {np.dot(v1, v3)}")   # -1
```

**练习 1.1.2** ⭐
```python
# 练习：手动计算点积，然后用 NumPy 验证

x = np.array([2, 3, 4])
y = np.array([5, 6, 7])

# TODO: 1. 先在纸上/脑中算出 x·y 的结果（不运行代码）
# TODO: 2. 用 np.dot() 验证你的答案
# TODO: 3. 用 sum(x * y) 再验证一次
```

<details>
<summary>点击查看答案</summary>

```python
# 手动计算：2*5 + 3*6 + 4*7 = 10 + 18 + 28 = 56
print("np.dot:", np.dot(x, y))     # 56
print("手动:", sum(x * y))         # 56
```
</details>

**练习 1.1.3** ⭐⭐
```python
# 用点积计算"相似度"
# 场景：用户 A 对 5 部电影的评分，用户 B 对 5 部电影的评分
# 点积越大，说明两个用户口味越相似

user_a = np.array([5, 4, 1, 2, 5])  # 用户A的评分
user_b = np.array([4, 5, 1, 1, 4])  # 用户B的评分
user_c = np.array([1, 1, 5, 5, 1])  # 用户C的评分

# TODO: 1. 计算 A 和 B 的点积
# TODO: 2. 计算 A 和 C 的点积
# TODO: 3. 谁和 A 更相似？为什么？
```

<details>
<summary>点击查看答案</summary>

```python
sim_ab = np.dot(user_a, user_b)
sim_ac = np.dot(user_a, user_c)
print(f"A 和 B 的相似度: {sim_ab}")  # 5*4+4*5+1*1+2*1+5*4 = 20+20+1+2+20 = 63
print(f"A 和 C 的相似度: {sim_ac}")  # 5*1+4*1+1*5+2*5+5*1 = 5+4+5+10+5 = 29
print("A 和 B 更相似（点积更大），因为他们喜欢同样类型的电影")
```
</details>

---

### 知识点 3：矩阵基础

**概念**：矩阵就是二维数组。在数据科学中，整个数据集就是一个矩阵（行=样本，列=特征）。

**Demo**：
```python
# 3个学生的成绩表：数学、语文、英语
grades = np.array([
    [90, 85, 92],   # 学生1
    [78, 88, 95],   # 学生2
    [85, 90, 80],   # 学生3
])
print(f"矩阵形状: {grades.shape}")  # (3, 3) → 3行3列

# 访问元素
print(f"学生2的语文成绩: {grades[1, 1]}")  # 88（第2行第2列，从0开始）
print(f"学生1的所有成绩: {grades[0, :]}")   # [90, 85, 92]
print(f"所有学生的数学成绩: {grades[:, 0]}")  # [90, 78, 85]

# 转置：行列互换
print(f"转置后形状: {grades.T.shape}")  # (3, 3)
print(f"转置后:\n{grades.T}")
# 转置后每一行变成了一个科目的所有学生成绩

# 矩阵统计
print(f"每个学生的平均分: {grades.mean(axis=1)}")  # 沿列求均值 → 每行一个值
print(f"每科的平均分: {grades.mean(axis=0)}")       # 沿行求均值 → 每列一个值
```

**练习 1.1.4** ⭐
```python
# 练习：创建并操作矩阵
# 4个城市的天气数据：温度、湿度、风速
weather = np.array([
    [28, 65, 12],   # 北京
    [32, 80, 8],    # 上海
    [35, 70, 5],    # 广州
    [22, 55, 15],   # 哈尔滨
])

# TODO: 1. 输出矩阵的形状
# TODO: 2. 获取上海的所有天气数据
# TODO: 3. 获取所有城市的温度
# TODO: 4. 计算每个城市的平均天气指标
# TODO: 5. 计算每个指标的最大值
# TODO: 6. 哪个城市温度最高？（提示：用 argmax）
```

<details>
<summary>点击查看答案</summary>

```python
print("形状:", weather.shape)                    # (4, 3)
print("上海:", weather[1, :])                     # [32, 80, 8]
print("所有温度:", weather[:, 0])                 # [28, 32, 35, 22]
print("每城平均:", weather.mean(axis=1))          # [35., 40., 36.67, 30.67]
print("每指标最大:", weather.max(axis=0))          # [35, 80, 15]
print("最高温城市索引:", weather[:, 0].argmax())    # 2（广州）
```
</details>

---

### 知识点 4：矩阵乘法

**概念**：矩阵 A(m×n) × 矩阵 B(n×p) = 矩阵 C(m×p)。内维度必须匹配。

**Demo**：
```python
# 矩阵乘法：模拟神经网络的一层计算
# 输入：2个样本，每个有3个特征
X = np.array([
    [1, 2, 3],    # 样本1
    [4, 5, 6],    # 样本2
])  # 形状 (2, 3)

# 权重矩阵：3个输入特征 → 2个输出
W = np.array([
    [0.1, 0.4],
    [0.2, 0.5],
    [0.3, 0.6],
])  # 形状 (3, 2)

# 矩阵乘法：(2,3) × (3,2) = (2,2)
output = X @ W
print(f"输入形状: {X.shape}")    # (2, 3)
print(f"权重形状: {W.shape}")    # (3, 2)
print(f"输出形状: {output.shape}")  # (2, 2)
print(f"输出:\n{output}")

# 手动验证第一个元素：1*0.1 + 2*0.2 + 3*0.3 = 0.1+0.4+0.9 = 1.4
print(f"手动验证 [0,0]: {1*0.1 + 2*0.2 + 3*0.3}")  # 1.4
```

**练习 1.1.5** ⭐⭐
```python
A = np.array([[1, 2], [3, 4]])
B = np.array([[5, 6], [7, 8]])

# TODO: 1. 手动计算 A @ B 的结果（写在注释里），然后用代码验证
# TODO: 2. 计算 B @ A，结果和 A @ B 一样吗？（矩阵乘法不满足交换律）
# TODO: 3. 如果 C 的形状是 (2,3)，D 的形状是 (4,2)，C @ D 能算吗？为什么？
```

<details>
<summary>点击查看答案</summary>

```python
# 1. 手动计算 A @ B:
# [1*5+2*7, 1*6+2*8] = [19, 22]
# [3*5+4*7, 3*6+4*8] = [43, 50]
print("A @ B =\n", A @ B)   # [[19, 22], [43, 50]]

# 2. B @ A ≠ A @ B
print("B @ A =\n", B @ A)   # [[23, 34], [31, 46]]
print("相同吗?", np.array_equal(A @ B, B @ A))  # False

# 3. C(2,3) @ D(4,2) 不能算！
# C 的列数(3) ≠ D 的行数(4)，内维度不匹配
# 但 D(4,2) @ C(2,3) = (4,3) 是可以的
```
</details>

---

### 知识点 5：范数（向量的"长度"）

**概念**：范数衡量向量的大小。L1 范数 = 各元素绝对值之和，L2 范数 = 各元素平方和再开根。
机器学习中的"正则化"就是用范数来约束模型权重不要太大。

**Demo**：
```python
v = np.array([3, 4])

# L2 范数（欧几里得距离）
l2 = np.linalg.norm(v)          # sqrt(3² + 4²) = sqrt(25) = 5
print(f"L2 范数: {l2}")          # 5.0

# L1 范数（曼哈顿距离）
l1 = np.linalg.norm(v, ord=1)   # |3| + |4| = 7
print(f"L1 范数: {l1}")          # 7.0

# 单位向量（长度为1的向量）
unit_v = v / l2
print(f"单位向量: {unit_v}")                  # [0.6, 0.8]
print(f"单位向量的长度: {np.linalg.norm(unit_v)}")  # 1.0
```

**练习 1.1.6** ⭐
```python
w = np.array([1, -2, 3, -4])

# TODO: 1. 计算 w 的 L1 范数（各元素绝对值之和）
# TODO: 2. 计算 w 的 L2 范数
# TODO: 3. 将 w 转换为单位向量（除以 L2 范数）
# TODO: 4. 验证单位向量的 L2 范数是否为 1
```

<details>
<summary>点击查看答案</summary>

```python
l1 = np.linalg.norm(w, ord=1)    # |1|+|-2|+|3|+|-4| = 10
l2 = np.linalg.norm(w)           # sqrt(1+4+9+16) = sqrt(30) ≈ 5.477
unit_w = w / l2
print(f"L1: {l1}")               # 10.0
print(f"L2: {l2:.4f}")           # 5.4772
print(f"单位向量: {unit_w}")
print(f"验证: {np.linalg.norm(unit_w):.4f}")  # 1.0000
```
</details>

---

## 1.2 概率与统计

### 知识点 6：均值、中位数、众数

**概念**：
- 均值（mean）：所有值的平均
- 中位数（median）：排序后中间的值（对异常值更鲁棒）
- 众数（mode）：出现最多的值

**Demo**：
```python
import numpy as np
from scipy import stats

# 一组人的月薪（万元）
salaries = np.array([0.8, 1.0, 1.2, 1.5, 2.0, 2.5, 3.0, 50.0])  # 注意最后一个是CEO

print(f"均值: {salaries.mean():.2f} 万")      # 7.75 万（被 50 拉高了！）
print(f"中位数: {np.median(salaries):.2f} 万")  # 1.75 万（更能代表大多数人）

# 这就是为什么新闻说"平均工资"时你总觉得被平均了——应该看中位数
```

**练习 1.2.1** ⭐
```python
# 某班级一次考试成绩
scores = np.array([45, 62, 70, 72, 75, 78, 80, 82, 85, 88, 90, 95, 98])

# TODO: 1. 计算均值
# TODO: 2. 计算中位数
# TODO: 3. 如果加入一个 0 分的同学，均值和中位数分别变成多少？
# TODO: 4. 哪个指标受异常值影响更大？
```

<details>
<summary>点击查看答案</summary>

```python
print(f"均值: {scores.mean():.2f}")        # 78.46
print(f"中位数: {np.median(scores):.2f}")   # 80.0

scores_with_zero = np.append(scores, 0)
print(f"加0分后均值: {scores_with_zero.mean():.2f}")      # 72.86（下降明显）
print(f"加0分后中位数: {np.median(scores_with_zero):.2f}") # 76.5（变化很小）
print("结论：均值受异常值影响大，中位数更鲁棒")
```
</details>

---

### 知识点 7：方差与标准差

**概念**：
- 方差（variance）：每个值与均值的差的平方的平均 → 衡量数据分散程度
- 标准差（std）：方差开根号 → 单位与原数据一致，更直观

**Demo**：
```python
# 两个班级的考试成绩
class_a = np.array([70, 72, 68, 74, 71])  # 成绩很集中
class_b = np.array([50, 90, 65, 85, 60])  # 成绩很分散

print(f"A班 均值={class_a.mean():.1f}, 标准差={class_a.std():.2f}")
# 均值=71.0, 标准差=2.00 → 成绩集中，标准差小
print(f"B班 均值={class_b.mean():.1f}, 标准差={class_b.std():.2f}")
# 均值=70.0, 标准差=15.17 → 成绩分散，标准差大

# 两个班均值差不多，但标准差差很多 → 标准差揭示了"分布的形状"
```

**练习 1.2.2** ⭐
```python
# 两家公司员工月薪（万元）
company_x = np.array([2.0, 2.2, 2.5, 2.3, 2.1, 2.4, 2.6, 2.2])
company_y = np.array([0.8, 1.0, 1.5, 2.0, 2.5, 3.0, 4.0, 5.0])

# TODO: 1. 分别计算两家公司的均值和标准差
# TODO: 2. 哪家公司薪资更公平（差距小）？
# TODO: 3. 如果你是求职者，你更愿意去哪家？为什么？
```

<details>
<summary>点击查看答案</summary>

```python
print(f"X公司: 均值={company_x.mean():.2f}万, 标准差={company_x.std():.3f}万")
# 均值=2.29万, 标准差=0.187万
print(f"Y公司: 均值={company_y.mean():.2f}万, 标准差={company_y.std():.3f}万")
# 均值=2.47万, 标准差=1.340万

print("X公司更公平（标准差更小，薪资差距小）")
print("Y公司均值略高但差距大，可能意味着大部分人拿低薪，少数人拿高薪")
```
</details>

---

### 知识点 8：正态分布

**概念**：钟形曲线，数据集中在均值附近。自然界很多现象近似正态分布（身高、考试成绩等）。
68-95-99.7 规则：68% 数据在 ±1σ 内，95% 在 ±2σ 内，99.7% 在 ±3σ 内。

**Demo**：
```python
import matplotlib.pyplot as plt

# 生成正态分布数据
np.random.seed(42)
heights = np.random.normal(loc=170, scale=8, size=10000)
# loc=均值, scale=标准差, size=样本数

fig, axes = plt.subplots(1, 2, figsize=(14, 5))

# 直方图
axes[0].hist(heights, bins=50, edgecolor='black', alpha=0.7)
axes[0].axvline(170, color='red', linestyle='--', label=f'均值={170}')
axes[0].axvline(170-8, color='orange', linestyle=':', label=f'-1σ={162}')
axes[0].axvline(170+8, color='orange', linestyle=':', label=f'+1σ={178}')
axes[0].set_title('身高分布（正态分布）')
axes[0].set_xlabel('身高 (cm)')
axes[0].legend()

# 验证 68-95-99.7 规则
within_1std = np.sum((heights > 162) & (heights < 178)) / len(heights) * 100
within_2std = np.sum((heights > 154) & (heights < 186)) / len(heights) * 100
axes[1].bar(['±1σ', '±2σ', '±3σ'],
            [within_1std, within_2std,
             np.sum((heights > 146) & (heights < 194)) / len(heights) * 100])
axes[1].set_title('68-95-99.7 规则验证')
axes[1].set_ylabel('百分比 (%)')

plt.tight_layout()
plt.show()
```

**练习 1.2.3** ⭐⭐
```python
np.random.seed(42)

# TODO: 1. 生成 5000 个均值为 75、标准差为 10 的正态分布随机数（模拟考试成绩）
# TODO: 2. 计算并打印均值和标准差，验证是否接近 75 和 10
# TODO: 3. 计算有多少百分比的成绩在 60-90 之间（即均值±1.5倍标准差）
# TODO: 4. 画直方图，用 plt.axvline 标注均值位置
# TODO: 5. 不及格（<60分）的学生大约有多少百分比？
```

<details>
<summary>点击查看答案</summary>

```python
np.random.seed(42)
scores = np.random.normal(loc=75, scale=10, size=5000)

print(f"均值: {scores.mean():.2f}")  # ≈75
print(f"标准差: {scores.std():.2f}") # ≈10

pct_60_90 = np.sum((scores >= 60) & (scores <= 90)) / len(scores) * 100
print(f"60-90分的比例: {pct_60_90:.1f}%")  # ≈86.6%

pct_fail = np.sum(scores < 60) / len(scores) * 100
print(f"不及格比例: {pct_fail:.1f}%")  # ≈6.7%

plt.figure(figsize=(10, 5))
plt.hist(scores, bins=50, edgecolor='black', alpha=0.7)
plt.axvline(75, color='red', linestyle='--', label='均值=75')
plt.axvline(60, color='orange', linestyle=':', label='60分线')
plt.title('考试成绩分布')
plt.xlabel('分数')
plt.legend()
plt.show()
```
</details>

---

### 知识点 9：相关系数

**概念**：衡量两个变量之间线性关系的强度，范围 [-1, 1]。
+1 = 完全正相关，0 = 无线性关系，-1 = 完全负相关。
在特征工程中用来发现哪些特征和目标变量有关。

**Demo**：
```python
np.random.seed(42)

# 模拟：身高 vs 体重（正相关）
height = np.random.normal(170, 8, 200)
weight = 0.8 * height - 70 + np.random.normal(0, 5, 200)

# 模拟：温度 vs 冰淇淋销量（正相关），温度 vs 热饮销量（负相关）
temp = np.random.uniform(5, 35, 200)
ice_cream = 2 * temp + np.random.normal(0, 5, 200)
hot_drink = -1.5 * temp + 80 + np.random.normal(0, 5, 200)

print(f"身高-体重 相关系数: {np.corrcoef(height, weight)[0,1]:.3f}")  # ≈0.85+
print(f"温度-冰淇淋 相关系数: {np.corrcoef(temp, ice_cream)[0,1]:.3f}")  # ≈0.97+
print(f"温度-热饮 相关系数: {np.corrcoef(temp, hot_drink)[0,1]:.3f}")   # ≈-0.97

fig, axes = plt.subplots(1, 3, figsize=(15, 4))
axes[0].scatter(height, weight, alpha=0.5, s=10)
axes[0].set_title(f'身高vs体重 (r={np.corrcoef(height,weight)[0,1]:.2f})')
axes[1].scatter(temp, ice_cream, alpha=0.5, s=10, color='orange')
axes[1].set_title(f'温度vs冰淇淋 (r={np.corrcoef(temp,ice_cream)[0,1]:.2f})')
axes[2].scatter(temp, hot_drink, alpha=0.5, s=10, color='red')
axes[2].set_title(f'温度vs热饮 (r={np.corrcoef(temp,hot_drink)[0,1]:.2f})')
plt.tight_layout()
plt.show()
```

**练习 1.2.4** ⭐⭐
```python
np.random.seed(42)

# 模拟学生数据
study_hours = np.random.uniform(1, 10, 100)                          # 学习时间
exam_score = 8 * study_hours + 20 + np.random.normal(0, 5, 100)      # 考试成绩
game_hours = 12 - study_hours + np.random.normal(0, 1, 100)          # 打游戏时间
sleep_hours = np.random.uniform(5, 9, 100)                           # 睡眠时间（无关）

# TODO: 1. 计算 study_hours 与 exam_score 的相关系数
# TODO: 2. 计算 game_hours 与 exam_score 的相关系数
# TODO: 3. 计算 sleep_hours 与 exam_score 的相关系数
# TODO: 4. 哪个因素和成绩关系最强？哪个最弱？
# TODO: 5. 画 3 个散点图对比
```

<details>
<summary>点击查看答案</summary>

```python
r_study = np.corrcoef(study_hours, exam_score)[0,1]
r_game = np.corrcoef(game_hours, exam_score)[0,1]
r_sleep = np.corrcoef(sleep_hours, exam_score)[0,1]

print(f"学习时间-成绩: r={r_study:.3f}")  # 强正相关 ≈0.95
print(f"打游戏-成绩: r={r_game:.3f}")     # 强负相关 ≈-0.93
print(f"睡眠-成绩: r={r_sleep:.3f}")      # 接近0（无关）

fig, axes = plt.subplots(1, 3, figsize=(15, 4))
axes[0].scatter(study_hours, exam_score, s=10)
axes[0].set_title(f'学习 vs 成绩 (r={r_study:.2f})')
axes[1].scatter(game_hours, exam_score, s=10, color='red')
axes[1].set_title(f'游戏 vs 成绩 (r={r_game:.2f})')
axes[2].scatter(sleep_hours, exam_score, s=10, color='gray')
axes[2].set_title(f'睡眠 vs 成绩 (r={r_sleep:.2f})')
plt.tight_layout()
plt.show()
print("学习时间与成绩关系最强（正相关），睡眠与成绩几乎无关")
```
</details>

---

## 1.3 微积分

### 知识点 10：导数与梯度

**概念**：
- 导数 = 函数在某点的变化率（斜率）
- 梯度 = 多变量函数的所有偏导数组成的向量
- 机器学习中：梯度告诉我们"应该往哪个方向调整参数来减小损失"

**Demo**：
```python
# 可视化导数：f(x) = x² 的导数是 f'(x) = 2x
x = np.linspace(-5, 5, 100)
y = x ** 2
dy = 2 * x  # 导数

fig, axes = plt.subplots(1, 2, figsize=(12, 5))

axes[0].plot(x, y, label='f(x) = x²')
axes[0].set_title('函数')
axes[0].legend()
axes[0].grid(True)

axes[1].plot(x, dy, color='red', label="f'(x) = 2x")
axes[1].axhline(y=0, color='black', linestyle='-', linewidth=0.5)
axes[1].set_title('导数（斜率）')
axes[1].legend()
axes[1].grid(True)

plt.tight_layout()
plt.show()
# 观察：x<0 时导数为负（函数在下降），x=0 时导数为0（最低点），x>0 时导数为正（函数在上升）
```

**练习 1.3.1** ⭐
```python
# 练习：理解几个常见函数的导数
# 不需要数学推导，用数值方法近似计算导数
# 导数 ≈ (f(x+h) - f(x)) / h（h 很小时）

def numerical_derivative(f, x, h=1e-7):
    """数值近似计算导数"""
    return (f(x + h) - f(x)) / h

# TODO: 1. 定义 f(x) = x³，在 x=2 处计算导数（正确答案应该是 12）
# TODO: 2. 定义 f(x) = 3x² + 2x + 1，在 x=1 处计算导数（正确答案是 8）
# TODO: 3. 定义 f(x) = 1/x，在 x=3 处计算导数（正确答案是 -1/9 ≈ -0.111）
```

<details>
<summary>点击查看答案</summary>

```python
def numerical_derivative(f, x, h=1e-7):
    return (f(x + h) - f(x)) / h

# 1. f(x) = x³ → f'(x) = 3x² → f'(2) = 12
print(numerical_derivative(lambda x: x**3, 2))          # ≈12.0

# 2. f(x) = 3x²+2x+1 → f'(x) = 6x+2 → f'(1) = 8
print(numerical_derivative(lambda x: 3*x**2 + 2*x + 1, 1))  # ≈8.0

# 3. f(x) = 1/x → f'(x) = -1/x² → f'(3) = -1/9
print(numerical_derivative(lambda x: 1/x, 3))           # ≈-0.1111
```
</details>

---

### 知识点 11：梯度下降

**概念**：机器学习训练的核心算法。
1. 计算损失函数的梯度（方向）
2. 沿梯度反方向更新参数（下山）
3. 重复直到损失不再减小

**Demo**：
```python
# 完整的梯度下降可视化
def f(x):
    """损失函数：(x-3)²+1"""
    return (x - 3) ** 2 + 1

def gradient(x):
    """f 的导数：2(x-3)"""
    return 2 * (x - 3)

# 梯度下降
x = -2.0               # 随机初始位置
learning_rate = 0.1     # 学习率（步长）
history = [(x, f(x))]

for step in range(30):
    grad = gradient(x)
    x = x - learning_rate * grad  # 核心公式！
    history.append((x, f(x)))

# 可视化
xs = np.linspace(-4, 8, 200)
plt.figure(figsize=(10, 6))
plt.plot(xs, f(xs), 'b-', label='损失函数 f(x)=(x-3)²+1')

# 画出梯度下降路径
hx = [h[0] for h in history]
hy = [h[1] for h in history]
plt.scatter(hx, hy, c=range(len(hx)), cmap='Reds', zorder=5, s=30)
plt.plot(hx[:10], hy[:10], 'r--', alpha=0.5, label='下降路径')

plt.xlabel('x（参数值）')
plt.ylabel('f(x)（损失值）')
plt.title(f'梯度下降过程（最终 x={x:.4f}，最优解 x=3）')
plt.legend()
plt.grid(True, alpha=0.3)
plt.show()

print(f"最终位置: x = {x:.6f}（真实最优解: x = 3）")
print(f"最终损失: f(x) = {f(x):.6f}（真实最小损失: 1）")
```

**练习 1.3.2** ⭐⭐
```python
# 练习：用梯度下降找 f(x) = x⁴ - 3x³ + 2 的最小值

# TODO: 1. 定义函数 f(x) = x⁴ - 3x³ + 2
# TODO: 2. 定义其导数 f'(x) = 4x³ - 9x²
# TODO: 3. 从 x=4.0 开始，学习率 0.01，跑 200 步梯度下降
# TODO: 4. 记录每步的 x 和 f(x)
# TODO: 5. 画出函数曲线和下降路径
# TODO: 6. 最终 x 收敛到多少？f(x) 是多少？
```

<details>
<summary>点击查看答案</summary>

```python
def f(x):
    return x**4 - 3*x**3 + 2

def grad(x):
    return 4*x**3 - 9*x**2

x = 4.0
lr = 0.01
history = [(x, f(x))]

for _ in range(200):
    x = x - lr * grad(x)
    history.append((x, f(x)))

xs = np.linspace(-1, 4.5, 200)
plt.figure(figsize=(10, 6))
plt.plot(xs, [f(xi) for xi in xs], 'b-', label='f(x)')
hx, hy = zip(*history)
plt.scatter(hx, hy, c=range(len(hx)), cmap='Reds', s=10, zorder=5)
plt.title(f'收敛到 x={x:.4f}, f(x)={f(x):.4f}')
plt.legend()
plt.grid(True, alpha=0.3)
plt.show()

print(f"x = {x:.4f}")    # ≈2.25
print(f"f(x) = {f(x):.4f}")
```
</details>

**练习 1.3.3** ⭐⭐⭐ (挑战)
```python
# 学习率的影响实验
# 用同一个函数 f(x)=(x-3)²+1，分别用以下学习率跑梯度下降：
# lr = 0.01（太小）、lr = 0.1（合适）、lr = 0.5（偏大）、lr = 1.01（太大，会发散）

# TODO: 1. 对 4 种学习率分别跑 50 步梯度下降（初始 x=-2）
# TODO: 2. 画 4 个子图，展示不同学习率下的收敛过程
# TODO: 3. 观察：学习率太小 → ?  太大 → ?  合适 → ?
```

<details>
<summary>点击查看答案</summary>

```python
def f(x): return (x - 3) ** 2 + 1
def grad(x): return 2 * (x - 3)

learning_rates = [0.01, 0.1, 0.5, 1.01]
fig, axes = plt.subplots(1, 4, figsize=(20, 4))
xs_plot = np.linspace(-10, 15, 200)

for i, lr in enumerate(learning_rates):
    x = -2.0
    hx, hy = [x], [f(x)]
    for _ in range(50):
        x = x - lr * grad(x)
        x = np.clip(x, -20, 20)  # 防止数值爆炸
        hx.append(x)
        hy.append(f(x))

    axes[i].plot(xs_plot, f(xs_plot), 'b-')
    axes[i].scatter(hx, hy, c='red', s=15, zorder=5)
    axes[i].set_title(f'lr={lr}\n最终x={hx[-1]:.2f}')
    axes[i].set_ylim(-5, 50)

plt.tight_layout()
plt.show()
# lr=0.01: 收敛太慢，50步还没到最优解
# lr=0.1:  完美收敛
# lr=0.5:  收敛但在最优解附近震荡
# lr=1.01: 发散！损失越来越大
```
</details>

---

### 知识点 12：损失函数

**概念**：衡量模型预测与真实值之间差距的函数。训练模型 = 最小化损失函数。

**Demo**：
```python
# 均方误差（MSE）：回归问题最常用的损失函数
# MSE = (1/n) * Σ(预测值 - 真实值)²

y_true = np.array([3.0, 5.0, 2.5, 7.0])
y_pred_good = np.array([2.8, 5.2, 2.3, 7.1])   # 好的预测
y_pred_bad = np.array([1.0, 8.0, 0.5, 4.0])     # 差的预测

mse_good = np.mean((y_pred_good - y_true) ** 2)
mse_bad = np.mean((y_pred_bad - y_true) ** 2)

print(f"好预测的 MSE: {mse_good:.4f}")  # 很小
print(f"差预测的 MSE: {mse_bad:.4f}")   # 很大

# 二元交叉熵（BCE）：分类问题最常用的损失函数
# BCE = -(1/n) * Σ[y*log(p) + (1-y)*log(1-p)]
def bce_loss(y_true, y_pred):
    epsilon = 1e-15  # 防止 log(0)
    y_pred = np.clip(y_pred, epsilon, 1 - epsilon)
    return -np.mean(y_true * np.log(y_pred) + (1 - y_true) * np.log(1 - y_pred))

y_true_cls = np.array([1, 0, 1, 1])
y_pred_cls_good = np.array([0.9, 0.1, 0.8, 0.95])  # 好的预测
y_pred_cls_bad = np.array([0.4, 0.6, 0.3, 0.5])     # 差的预测

print(f"好预测的 BCE: {bce_loss(y_true_cls, y_pred_cls_good):.4f}")  # 小
print(f"差预测的 BCE: {bce_loss(y_true_cls, y_pred_cls_bad):.4f}")   # 大
```

**练习 1.3.4** ⭐⭐
```python
# 手动实现线性回归的梯度下降
# 目标：找到 y = wx + b 中的 w 和 b

np.random.seed(42)
# 生成数据：真实关系是 y = 2x + 3（w=2, b=3）
X = np.random.uniform(0, 10, 50)
y = 2 * X + 3 + np.random.normal(0, 1, 50)  # 加点噪声

# TODO: 1. 初始化 w=0, b=0
# TODO: 2. 定义预测函数 y_pred = w*X + b
# TODO: 3. 定义 MSE 损失函数
# TODO: 4. 计算梯度：dw = (2/n) * Σ(y_pred - y) * X, db = (2/n) * Σ(y_pred - y)
# TODO: 5. 学习率 0.01，跑 1000 步
# TODO: 6. 打印最终的 w 和 b（应该接近 2 和 3）
# TODO: 7. 画散点图 + 拟合直线
```

<details>
<summary>点击查看答案</summary>

```python
np.random.seed(42)
X = np.random.uniform(0, 10, 50)
y = 2 * X + 3 + np.random.normal(0, 1, 50)

w, b = 0.0, 0.0
lr = 0.01
n = len(X)
losses = []

for step in range(1000):
    y_pred = w * X + b
    loss = np.mean((y_pred - y) ** 2)
    losses.append(loss)

    dw = (2/n) * np.sum((y_pred - y) * X)
    db = (2/n) * np.sum(y_pred - y)

    w -= lr * dw
    b -= lr * db

    if step % 200 == 0:
        print(f"Step {step}: w={w:.4f}, b={b:.4f}, loss={loss:.4f}")

print(f"\n最终: w={w:.4f} (真实:2), b={b:.4f} (真实:3)")

fig, axes = plt.subplots(1, 2, figsize=(14, 5))
axes[0].scatter(X, y, s=20, label='数据')
axes[0].plot(sorted(X), [w*x+b for x in sorted(X)], 'r-', label=f'拟合: y={w:.2f}x+{b:.2f}')
axes[0].legend()
axes[0].set_title('线性回归拟合')

axes[1].plot(losses)
axes[1].set_title('损失函数下降曲线')
axes[1].set_xlabel('步数')
axes[1].set_ylabel('MSE Loss')
plt.tight_layout()
plt.show()
```
</details>

---

## 阶段一综合测试 ⭐⭐⭐

完成以下综合练习来检验你对数学基础的掌握：

```python
# 综合题：把所有知识串起来
# 场景：你有一个简单的数据集，用梯度下降做线性回归

np.random.seed(2024)

# 1. 生成 100 个数据点，真实关系 y = 3x₁ + (-2)x₂ + 5 + 噪声
#    提示：X 是 (100, 2) 的矩阵，y 是 (100,) 的向量

# 2. 计算 X 每列的均值、标准差

# 3. 计算两个特征与 y 的相关系数

# 4. 用矩阵运算实现预测：y_pred = X @ w + b
#    其中 w 是 (2,) 的权重向量，b 是标量

# 5. 实现梯度下降训练（用矩阵形式）：
#    dw = (2/n) * X.T @ (y_pred - y)
#    db = (2/n) * sum(y_pred - y)

# 6. 训练 2000 步，学习率 0.01，打印最终的 w 和 b
#    （应接近 [3, -2] 和 5）

# 7. 画损失曲线
```

<details>
<summary>点击查看答案</summary>

```python
np.random.seed(2024)

# 1. 生成数据
X = np.random.randn(100, 2)
y = 3 * X[:, 0] + (-2) * X[:, 1] + 5 + np.random.normal(0, 0.5, 100)

# 2. 统计
print(f"X[:,0] 均值={X[:,0].mean():.3f}, 标准差={X[:,0].std():.3f}")
print(f"X[:,1] 均值={X[:,1].mean():.3f}, 标准差={X[:,1].std():.3f}")

# 3. 相关系数
print(f"x1 与 y 的相关系数: {np.corrcoef(X[:,0], y)[0,1]:.3f}")
print(f"x2 与 y 的相关系数: {np.corrcoef(X[:,1], y)[0,1]:.3f}")

# 4-6. 梯度下降
w = np.zeros(2)
b = 0.0
lr = 0.01
n = len(y)
losses = []

for step in range(2000):
    y_pred = X @ w + b
    loss = np.mean((y_pred - y) ** 2)
    losses.append(loss)

    dw = (2/n) * (X.T @ (y_pred - y))
    db = (2/n) * np.sum(y_pred - y)

    w -= lr * dw
    b -= lr * db

print(f"w = {w} (真实: [3, -2])")
print(f"b = {b:.4f} (真实: 5)")

# 7. 损失曲线
plt.plot(losses)
plt.title('训练损失曲线')
plt.xlabel('步数')
plt.ylabel('MSE')
plt.grid(True, alpha=0.3)
plt.show()
```
</details>
