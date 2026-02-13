# 阶段六：深度学习入门 — 练习手册

> 前置要求：完成阶段一~五，已有 Kaggle 竞赛经验
> 额外安装：`pip install torch torchvision`
> 预计耗时：持续学习

---

## 6.1 PyTorch 基础

### 知识点 38：Tensor 基本操作

**概念**：Tensor 就是 PyTorch 版的 NumPy 数组，但能在 GPU 上运算、能自动求导。

**Demo**：
```python
import torch
import numpy as np

# 创建 Tensor
a = torch.tensor([1.0, 2.0, 3.0])
b = torch.zeros(3, 4)
c = torch.ones(2, 3)
d = torch.randn(3, 4)        # 标准正态随机
e = torch.arange(0, 10, 2)   # [0, 2, 4, 6, 8]

# NumPy ↔ Tensor 互转
np_arr = np.array([1, 2, 3])
tensor_from_np = torch.from_numpy(np_arr)
np_from_tensor = a.numpy()

# 基本运算（和 NumPy 几乎一样）
x = torch.tensor([1.0, 2.0, 3.0])
y = torch.tensor([4.0, 5.0, 6.0])
print(x + y)                 # 逐元素加
print(x * y)                 # 逐元素乘
print(torch.dot(x, y))       # 点积
print(x.mean(), x.std())     # 统计

# 形状操作
m = torch.randn(2, 3)
print(m.shape)                # torch.Size([2, 3])
print(m.reshape(3, 2))       # reshape
print(m.T)                   # 转置

# 设备（CPU / GPU）
device = torch.device('mps' if torch.backends.mps.is_available() else 'cpu')  # Mac
print(f"使用设备: {device}")
x_gpu = x.to(device)
```

**练习 6.1.1** ⭐
```python
# TODO: 1. 创建一个 (4, 5) 的随机 Tensor
# TODO: 2. 计算每行的均值和每列的最大值
# TODO: 3. 将它 reshape 为 (2, 10)
# TODO: 4. 创建一个 NumPy 数组并转为 Tensor，再转回 NumPy
# TODO: 5. 检查你的电脑是否有 GPU/MPS 可用
```

<details>
<summary>点击查看答案</summary>

```python
# 1
t = torch.randn(4, 5)
print(t)

# 2
print("每行均值:", t.mean(dim=1))
print("每列最大:", t.max(dim=0).values)

# 3
print("reshape:", t.reshape(2, 10))

# 4
np_arr = np.array([[1, 2], [3, 4]])
tensor = torch.from_numpy(np_arr)
back_to_np = tensor.numpy()
print(f"NumPy→Tensor→NumPy: {back_to_np}")

# 5
print(f"MPS: {torch.backends.mps.is_available()}")
if hasattr(torch, 'cuda'):
    print(f"CUDA: {torch.cuda.is_available()}")
```
</details>

---

### 知识点 39：自动求导（Autograd）

**概念**：PyTorch 自动跟踪 Tensor 上的运算，调用 `.backward()` 自动计算梯度。这就是反向传播的实现方式。

**Demo**：
```python
# 手动验证：f(x) = x² + 3x → f'(x) = 2x + 3

x = torch.tensor(2.0, requires_grad=True)  # 告诉 PyTorch 需要跟踪梯度
y = x ** 2 + 3 * x   # y = 4 + 6 = 10
y.backward()          # 自动求导
print(f"x = {x.item()}")
print(f"y = {y.item()}")
print(f"dy/dx = {x.grad.item()}")  # 2*2 + 3 = 7 ✅

# 多变量
a = torch.tensor(1.0, requires_grad=True)
b = torch.tensor(2.0, requires_grad=True)
z = a**2 + a*b + b**2  # z = 1 + 2 + 4 = 7
z.backward()
print(f"dz/da = {a.grad.item()}")  # 2a + b = 2+2 = 4 ✅
print(f"dz/db = {b.grad.item()}")  # a + 2b = 1+4 = 5 ✅
```

**练习 6.1.2** ⭐⭐
```python
# TODO: 1. 用 PyTorch 自动求导验证：
#    f(x) = x³ 在 x=3 处的导数 = 27（3x² = 3*9 = 27）
# TODO: 2. f(x, y) = 2x²y + y³ 在 (x=1, y=2) 处：
#    ∂f/∂x = 4xy = 8
#    ∂f/∂y = 2x² + 3y² = 2+12 = 14
# TODO: 3. 用 PyTorch 实现梯度下降找 f(x) = (x-5)² 的最小值
#    提示：手动循环，每步 x = x - lr * x.grad，记得 x.grad.zero_()
```

<details>
<summary>点击查看答案</summary>

```python
# 1
x = torch.tensor(3.0, requires_grad=True)
f = x ** 3
f.backward()
print(f"f'(3) = {x.grad.item()}")  # 27

# 2
x = torch.tensor(1.0, requires_grad=True)
y = torch.tensor(2.0, requires_grad=True)
f = 2 * x**2 * y + y**3
f.backward()
print(f"∂f/∂x = {x.grad.item()}")  # 8
print(f"∂f/∂y = {y.grad.item()}")  # 14

# 3
x = torch.tensor(0.0, requires_grad=True)
lr = 0.1
for step in range(50):
    f = (x - 5) ** 2
    f.backward()
    with torch.no_grad():
        x -= lr * x.grad
    x.grad.zero_()
    if step % 10 == 0:
        print(f"Step {step}: x={x.item():.4f}, f(x)={f.item():.6f}")

print(f"最终 x = {x.item():.4f}（应接近 5）")
```
</details>

---

### 知识点 40：构建神经网络

**Demo**：
```python
import torch
import torch.nn as nn
from torch.utils.data import DataLoader, TensorDataset
from sklearn.datasets import load_breast_cancer
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

# 准备数据
data = load_breast_cancer()
X_train, X_test, y_train, y_test = train_test_split(
    data.data, data.target, test_size=0.2, random_state=42
)

scaler = StandardScaler()
X_train = scaler.fit_transform(X_train)
X_test = scaler.transform(X_test)

# 转为 Tensor
X_train_t = torch.FloatTensor(X_train)
y_train_t = torch.FloatTensor(y_train)
X_test_t = torch.FloatTensor(X_test)
y_test_t = torch.FloatTensor(y_test)

# DataLoader
train_ds = TensorDataset(X_train_t, y_train_t)
train_loader = DataLoader(train_ds, batch_size=32, shuffle=True)

# 定义网络
class BinaryClassifier(nn.Module):
    def __init__(self, input_dim):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(input_dim, 64),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(64, 32),
            nn.ReLU(),
            nn.Dropout(0.2),
            nn.Linear(32, 1),
        )

    def forward(self, x):
        return self.net(x).squeeze()

model = BinaryClassifier(input_dim=30)
optimizer = torch.optim.Adam(model.parameters(), lr=1e-3)
criterion = nn.BCEWithLogitsLoss()

# 训练
for epoch in range(50):
    model.train()
    total_loss = 0
    for batch_X, batch_y in train_loader:
        optimizer.zero_grad()
        output = model(batch_X)
        loss = criterion(output, batch_y)
        loss.backward()
        optimizer.step()
        total_loss += loss.item()

    if (epoch + 1) % 10 == 0:
        model.eval()
        with torch.no_grad():
            test_output = model(X_test_t)
            test_pred = (torch.sigmoid(test_output) > 0.5).float()
            accuracy = (test_pred == y_test_t).float().mean()
        print(f"Epoch {epoch+1}: loss={total_loss/len(train_loader):.4f}, "
              f"test_acc={accuracy:.4f}")
```

**练习 6.1.3** ⭐⭐
```python
# TODO: 修改上面的 Demo，完成以下任务：
# 1. 把隐藏层改为 [128, 64, 32] 三层
# 2. 加上 BatchNorm1d
# 3. 记录每个 epoch 的训练损失和测试准确率
# 4. 画损失曲线和准确率曲线（2个子图）
# 5. 尝试不同的学习率（1e-2, 1e-3, 1e-4），观察训练曲线的差异
```

<details>
<summary>点击查看答案</summary>

```python
class ImprovedClassifier(nn.Module):
    def __init__(self, input_dim):
        super().__init__()
        self.net = nn.Sequential(
            nn.Linear(input_dim, 128),
            nn.BatchNorm1d(128),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(128, 64),
            nn.BatchNorm1d(64),
            nn.ReLU(),
            nn.Dropout(0.2),
            nn.Linear(64, 32),
            nn.BatchNorm1d(32),
            nn.ReLU(),
            nn.Linear(32, 1),
        )

    def forward(self, x):
        return self.net(x).squeeze()

import matplotlib.pyplot as plt

fig, axes = plt.subplots(1, 2, figsize=(14, 5))

for lr in [1e-2, 1e-3, 1e-4]:
    model = ImprovedClassifier(30)
    optimizer = torch.optim.Adam(model.parameters(), lr=lr)
    criterion = nn.BCEWithLogitsLoss()

    losses, accs = [], []
    for epoch in range(100):
        model.train()
        epoch_loss = 0
        for bx, by in train_loader:
            optimizer.zero_grad()
            out = model(bx)
            loss = criterion(out, by)
            loss.backward()
            optimizer.step()
            epoch_loss += loss.item()
        losses.append(epoch_loss / len(train_loader))

        model.eval()
        with torch.no_grad():
            pred = (torch.sigmoid(model(X_test_t)) > 0.5).float()
            accs.append((pred == y_test_t).float().mean().item())

    axes[0].plot(losses, label=f'lr={lr}')
    axes[1].plot(accs, label=f'lr={lr}')

axes[0].set_title('训练损失')
axes[0].set_xlabel('Epoch')
axes[0].legend()
axes[1].set_title('测试准确率')
axes[1].set_xlabel('Epoch')
axes[1].legend()
plt.tight_layout()
plt.show()
```
</details>

---

## 6.2 图像分类入门（CNN）

### 知识点 41：用 CNN 分类 MNIST

**Demo + 练习 6.2.1** ⭐⭐⭐
```python
import torch
import torch.nn as nn
from torchvision import datasets, transforms
from torch.utils.data import DataLoader

# 数据准备（MNIST: 28x28 灰度手写数字图片）
transform = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize((0.1307,), (0.3081,))
])

train_data = datasets.MNIST(root='./data', train=True, download=True, transform=transform)
test_data = datasets.MNIST(root='./data', train=False, transform=transform)

train_loader = DataLoader(train_data, batch_size=64, shuffle=True)
test_loader = DataLoader(test_data, batch_size=1000)

# TODO: 1. 定义 CNN 模型：
#    Conv2d(1, 32, 3) → ReLU → MaxPool2d(2)
#    Conv2d(32, 64, 3) → ReLU → MaxPool2d(2)
#    Flatten → Linear(64*5*5, 128) → ReLU → Dropout(0.3)
#    Linear(128, 10)

class CNN(nn.Module):
    def __init__(self):
        super().__init__()
        # TODO: 补全网络结构
        pass

    def forward(self, x):
        # TODO: 补全前向传播
        pass

# TODO: 2. 训练 10 个 epoch，每个 epoch 打印训练损失和测试准确率
# TODO: 3. 画损失曲线和准确率曲线
# TODO: 4. 最终测试准确率应该能到 99%+
# TODO: 5. 从测试集中取 10 张图，展示图片和模型预测结果
```

<details>
<summary>点击查看答案</summary>

```python
class CNN(nn.Module):
    def __init__(self):
        super().__init__()
        self.conv = nn.Sequential(
            nn.Conv2d(1, 32, 3, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2),
            nn.Conv2d(32, 64, 3, padding=1),
            nn.ReLU(),
            nn.MaxPool2d(2),
        )
        self.fc = nn.Sequential(
            nn.Flatten(),
            nn.Linear(64 * 7 * 7, 128),
            nn.ReLU(),
            nn.Dropout(0.3),
            nn.Linear(128, 10),
        )

    def forward(self, x):
        x = self.conv(x)
        x = self.fc(x)
        return x

device = torch.device('mps' if torch.backends.mps.is_available() else 'cpu')
model = CNN().to(device)
optimizer = torch.optim.Adam(model.parameters(), lr=1e-3)
criterion = nn.CrossEntropyLoss()

losses, accs = [], []
for epoch in range(10):
    model.train()
    total_loss = 0
    for images, labels in train_loader:
        images, labels = images.to(device), labels.to(device)
        optimizer.zero_grad()
        output = model(images)
        loss = criterion(output, labels)
        loss.backward()
        optimizer.step()
        total_loss += loss.item()
    losses.append(total_loss / len(train_loader))

    model.eval()
    correct = 0
    total = 0
    with torch.no_grad():
        for images, labels in test_loader:
            images, labels = images.to(device), labels.to(device)
            output = model(images)
            pred = output.argmax(dim=1)
            correct += (pred == labels).sum().item()
            total += labels.size(0)
    acc = correct / total
    accs.append(acc)
    print(f"Epoch {epoch+1}: loss={losses[-1]:.4f}, test_acc={acc:.4f}")

# 曲线
fig, axes = plt.subplots(1, 2, figsize=(12, 4))
axes[0].plot(losses)
axes[0].set_title('训练损失')
axes[1].plot(accs)
axes[1].set_title('测试准确率')
plt.show()

# 展示预测
model.eval()
images, labels = next(iter(test_loader))
with torch.no_grad():
    preds = model(images.to(device)).argmax(dim=1).cpu()

fig, axes = plt.subplots(2, 5, figsize=(15, 6))
for i, ax in enumerate(axes.flat):
    ax.imshow(images[i].squeeze(), cmap='gray')
    color = 'green' if preds[i] == labels[i] else 'red'
    ax.set_title(f'真:{labels[i]} 预:{preds[i]}', color=color)
    ax.axis('off')
plt.tight_layout()
plt.show()
```
</details>

---

## 6.3 下一步方向

完成上述练习后，你可以根据兴趣选择深入方向：

### CV（计算机视觉）
- 学习 ResNet / EfficientNet 等经典架构
- 练习：参加 Kaggle 图像分类竞赛
- 关键技能：数据增强、迁移学习、预训练模型微调

### NLP（自然语言处理）
- 学习 Transformer / BERT / GPT 架构
- 练习：参加 Kaggle 文本分类竞赛
- 关键技能：Tokenizer、HuggingFace Transformers 库、微调

### 表格数据 + 深度学习
- 学习 TabNet、神经网络用于表格数据
- 与 XGBoost/LightGBM 融合
- 这是最实用的组合

---

## 阶段六综合测试 ⭐⭐⭐

```python
# 挑战：用 PyTorch 从零实现一个完整的分类项目

# 数据集：Fashion-MNIST（10类服装图片，和 MNIST 同格式但更难）
# torchvision.datasets.FashionMNIST

# 完成以下任务：
# 1. 加载数据，展示每类的样本图片
# 2. 设计 CNN 网络（至少 3 个卷积层）
# 3. 使用 Adam 优化器 + CrossEntropyLoss
# 4. 训练 20 个 epoch，记录损失和准确率
# 5. 画学习曲线
# 6. 输出测试集的 classification_report（10 个类别）
# 7. 画混淆矩阵
# 8. 找出模型最容易混淆的两个类别是什么
# 9. 目标：测试准确率 > 90%
```
