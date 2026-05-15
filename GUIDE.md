# EBU6304_Group82 — TA Recruitment System 指导手册

## 项目概述

国际化学院 TA 招聘系统，支持三类角色（Admin / MO / TA）通过 Swing GUI 完成完整业务闭环：

```
MO 创建岗位 → Admin 审批 → TA 查看并申请 → MO 审核申请 → Admin 统计工作量
                    ↑                                   ↓
              面试安排 + 消息通知 ←──────────────────────┘
```

---

## 环境要求

- **JDK**：8 或更高
- **系统**：Windows / macOS / Linux
- **无需** Maven / Gradle / 外部数据库

---

## 快速开始

### 1. 克隆仓库

```bash
git clone https://github.com/lmc2290/EBU6304_Group82.git
cd EBU6304_Group82
git checkout data-flow-change
```

### 2. 编译

```bash
javac -encoding UTF-8 -d out -sourcepath . LoginPage/*.java AdminPage/*.java TAUI/*.java
```

### 3. 运行

```bash
java -cp out LoginPage.LoginMain
```

---

## 项目结构

```
EBU6304_Group82/
├── LoginPage/          # 登录 + MO 端所有页面（17 个 Java 文件）
│   ├── LoginMain.java          ← 入口 main()
│   ├── LoginUI.java            ← 登录界面
│   ├── LoginController.java    ← 身份认证 + 路由
│   ├── User.java               ← 用户实体（id / role / moId / taId）
│   ├── DashBoardUI.java        ← 抽象 Dashboard 基类
│   ├── MODashboardUI.java      ← MO 主页（6 个功能卡片）
│   ├── MOApplicantListUI.java  ← MO 查看/审核申请人
│   ├── MOJobVacancyUI.java     ← MO 创建岗位
│   ├── MOScheduleInterviewsUI.java ← MO 安排面试
│   ├── MOMessageTAUI.java      ← MO 发送消息
│   ├── MOStatisticsUI.java     ← MO 统计面板
│   ├── UnifiedDataStore.java   ← ★ 核心：统一 CSV 数据读写层
│   └── ...
├── AdminPage/          # Admin 端（8 个 Java 文件）
│   ├── AdminDashboardUI.java              ← Admin 主页（双标签）
│   ├── Admin_TAWorkLoadControl.java       ← TA 工作量统计
│   ├── Admin_TAWorkLoadControlUI.java     ← 工作量界面
│   ├── Admin_CourseApplicationControl.java ← 课程审批
│   └── Admin_CourseApplicationControlUI.java ← 审批界面
├── TAUI/               # TA 端（12 个 Java 文件）
│   ├── TAController.java       ← TA 核心控制器
│   ├── TADashboardUI.java      ← TA 主页（岗位卡片网格）
│   ├── TADataStore.java        ← TA 本地持久化（ta_profiles.txt）
│   ├── ApplicationDialog.java  ← 提交申请弹窗
│   ├── MyApplicationsDialog.java ← 我的申请列表
│   └── ...
├── data/               # CSV 数据文件（运行时读写）
│   ├── modules.csv       ← 岗位数据（moduleCode, moId, status）
│   ├── applicants.csv    ← 申请数据（applicationId, taId, status）
│   ├── interviews.csv    ← 面试安排
│   └── messages.csv      ← 消息记录
└── out/                # 编译输出（.class 文件）
```

---

## 三类角色对照

| 角色 | ID 范围 | 功能 |
|------|---------|------|
| **Admin** | `0` | 审批岗位（Approve/Reject）、查看 TA 工作量统计 |
| **MO**（Module Organiser） | `1` ~ `100` | 创建岗位、审核申请、安排面试、发送消息、查看统计 |
| **TA**（Teaching Assistant） | `> 100` | 浏览岗位、提交申请、撤回申请、管理个人档案 |

---

## 测试账号

### Admin
| 账号 ID | 密码 | 说明 |
|---------|------|------|
| `0` | 任意 | 系统管理员 |

### MO（通过 modules.csv 的 moId 绑定课程）
| 账号 ID | 密码 | 负责课程 | 说明 |
|---------|------|----------|------|
| `1` | 任意 | CS101 | moId=1 在 modules.csv 中负责 CS101 |
| `2` | 任意 | CS202 | moId=2 在 modules.csv 中负责 CS202 |
| `22` | 任意 | ECS999 | moId=22 在 modules.csv 中负责 ECS999 |

> MO 登录后"View Applicants"只显示 **自己创建的课程**（通过 modules.csv 的 moId 匹配）。

### TA
| 账号 ID | 密码 | 说明 |
|---------|------|------|
| `10086` | 任意 | Alice，已申请 CS101，状态 Approved |
| `10087` | 任意 | Bob，已申请 CS101，状态 Pending |
| `10088` | 任意 | Cathy，已申请 CS202，状态 Shortlisted |
| `10099` | 任意 | 新 TA，无申请记录 |
| 任意 >100 | 任意 | 新 TA，可用任意 ID 登录测试完整流程 |

---

## 数据文件格式

### modules.csv

| 列 | 字段 | 说明 |
|----|------|------|
| 0 | moduleCode | 课程代码，如 CS101 |
| 1 | moduleName | 课程名称 |
| 2 | moId | 负责该课程的 MO ID |
| 3 | requiredTas | 需要的 TA 数量 |
| 4 | status | Pending / Approved / Rejected |
| 5 | createdBy | 创建者 ID |
| 6 | createdAt | 创建时间 |
| 7 | approvedBy | 审批者 ID |
| 8 | approvedAt | 审批时间 |
| 9 | rejectReason | 拒绝原因 |

### applicants.csv

| 列 | 字段 | 说明 |
|----|------|------|
| 0 | applicationId | 申请 ID，格式 APP-{timestamp} |
| 1 | taId | TA 的登录 ID |
| 2 | taName | TA 姓名 |
| 3 | moduleCode | 申请的课程代码 |
| 4 | moduleName | 申请的课程名称 |
| 5 | cvFile | CV 文件名 |
| 6 | coverLetter | 求职信 |
| 7 | status | Pending / Shortlisted / Approved / Rejected / Withdrawn |
| 8 | submittedAt | 提交时间 |
| 9 | reviewedBy | 审核者 ID |
| 10 | reviewedAt | 审核时间 |

### interviews.csv

| 列 | 字段 | 说明 |
|----|------|------|
| 0 | interviewId | 面试 ID，格式 INT-{timestamp} |
| 1 | applicationId | 关联的申请 ID（来自 applicants.csv） |
| 2 | taId | TA ID |
| 3 | taName | TA 姓名 |
| 4 | moduleCode | 课程代码 |
| 5 | moduleName | 课程名称 |
| 6 | interviewTime | 面试时间 |
| 7 | location | 面试地点 |
| 8 | status | Scheduled |
| 9 | createdBy | 创建者（MO ID） |
| 10 | createdAt | 创建时间 |

### messages.csv

| 列 | 字段 | 说明 |
|----|------|------|
| 0 | messageId | 消息 ID，格式 MSG-{timestamp} |
| 1 | fromRole | 发送方角色（MO） |
| 2 | fromId | 发送方 ID |
| 3 | toTaId | 接收方 TA ID |
| 4 | toTaName | 接收方 TA 姓名 |
| 5 | moduleCode | 关联课程 |
| 6 | subject | 消息主题 |
| 7 | content | 消息内容 |
| 8 | sentAt | 发送时间 |

---

## 完整验收流程

### 流程 1：MO 创建岗位 → Admin 审批 → TA 查看

```
步骤 1.1 — MO 登录
  登录界面输入 ID=22, Password=任意
  点击 Login → 进入 MO Dashboard（显示 Module: Not Assigned）

步骤 1.2 — MO 创建岗位
  点击 "Create TA Vacancy" 卡片
  填写：
    Module Code:  ECS999
    Module Name:  Test Module
    Positions:    2
  点击 Save → 提示 "Job vacancy submitted successfully. Status: Pending"
  此时 data/modules.csv 中出现 ECS999 行，status=Pending, moId=22

步骤 1.3 — Admin 审批
  关闭 MO Dashboard，重新登录 ID=0
  点击 "Approve Requests" 按钮
  表格中出现 ECS999，状态显示为 Pending（橙色）
  点击 Approve 按钮 → 状态变为 Approved
  此时 data/modules.csv 中 ECS999 的 status 变为 Approved

步骤 1.4 — TA 查看
  关闭 Admin Dashboard，重新登录 ID=10099（新 TA）
  岗位卡片网格中应出现 "Test Module TA"
  不应该出现任何 Pending 或 Rejected 的岗位
  不应该出现 J01 Java Lab Assistant 或 J02 Python Tutor
```

### 流程 2：TA 申请 → MO 审核 → Admin 统计

```
步骤 2.1 — TA 申请
  TA 登录（ID=10099）
  点击 "My Profile" 填写档案（姓名、技能等）
  点击 ECS999 岗位卡片，填写 Cover Letter
  点击 Confirm & Submit
  此时 data/applicants.csv 中出现新行，status=Pending，applicationId=APP-xxx

步骤 2.2 — MO 审核
  重新登录为 MO（ID=22，ECS999 的创建者）
  点击 "View Applicants"
  表格中出现 ECS999 的新申请
  点击 Update Status → 选择 Approved
  提示 "Status updated successfully"
  此时 data/applicants.csv 中该 applicationId 的 status 变为 Approved

步骤 2.3 — Admin 查看统计
  重新登录为 Admin（ID=0）
  点击 "Workload Dashboard"
  统计表格中应出现该 TA 的记录
  "Enrolled Courses" 列显示了被 Approved 的课程数 +1
```

### 流程 3：面试安排与消息

```
步骤 3.1 — 安排面试
  MO 登录（ID=22）
  点击 "Schedule Interviews"
  选择模块 ECS999
  看到 Pending 或 Shortlisted 申请人
  点击 "Schedule Interview"
  填写时间：2026-05-15 10:00，地点：Room 101
  点击 Schedule
  此时 data/interviews.csv 中出现面试记录
  字段包含正确的 applicationId（非 taId）

步骤 3.2 — 发送消息
  MO 登录
  点击 "Message TA"
  选择模块 ECS999
  看到 Approved/Shortlisted 的 TA 列表
  点击 Send Message → 输入消息 → 点击 Send
  此时 data/messages.csv 中出现消息记录
```

---

## 核心类说明

### UnifiedDataStore（LoginPage）

全局唯一的数据访问层，所有 CSV 读写必须通过此类：

```java
// 模块
UnifiedDataStore.addModule(moduleCode, moduleName, moId, requiredTas, createdBy);
UnifiedDataStore.getAllModules();
UnifiedDataStore.getApprovedModules();
UnifiedDataStore.getPendingModules();
UnifiedDataStore.getModulesByMoId(moId);
UnifiedDataStore.updateModuleStatus(moduleCode, newStatus, approvedBy, rejectReason);

// 申请
UnifiedDataStore.addApplicant(applicationId, taId, taName, moduleCode, moduleName, cvFile, coverLetter);
UnifiedDataStore.getAllApplicants();
UnifiedDataStore.getApplicantsByModule(moduleCode);
UnifiedDataStore.getApprovedApplicants();
UnifiedDataStore.getShortlistedApplicants();
UnifiedDataStore.getApplicationsByMoId(moId);
UnifiedDataStore.getApplicationsByTaId(taId);
UnifiedDataStore.updateApplicantStatus(applicationId, moduleCode, newStatus, reviewedBy);

// 面试
UnifiedDataStore.addInterview(applicationId, taId, taName, moduleCode, moduleName, interviewTime, location, createdBy);

// 消息
UnifiedDataStore.addMessage(fromRole, fromId, toTaId, toTaName, moduleCode, subject, content);
```

### TAController（TAUI）

TA 端核心控制器：
- `getAllJobs()` — 只从 modules.csv 读取 Approved 岗位，无 fallback mock
- `submitApplication()` — 写入 applicants.csv，统一使用 APP-xxx ID
- `withdrawApplication()` — 同步更新 applicants.csv → Withdrawn
- `getUserApplications()` — 从 applicants.csv 同步状态和 applicationId

---

## 常见问题排查

| 问题 | 原因 | 解决 |
|------|------|------|
| MO 登录后看不到申请 | MO 的 ID 与 modules.csv 的 moId 不匹配 | 确保用创建岗位的 MO ID 登录 |
| TA 看不到岗位 | modules.csv 中缺少 Approved 状态的模块 | Admin 登录审批对应模块 |
| 点击 Update Status 后 CSV 没更新 | applicationId 可能在代码中写错 | 检查 applicants.csv 第 0 列是否为 APP-xxx 格式 |
| 编译报错 | 文件编码问题或 classpath 不对 | 使用 `javac -encoding UTF-8` |
| CSV 格式错乱 | 包含逗号的字段未转义 | 系统已使用 `escapeCsv()` 处理 |
| 旧数据兼容 | 旧 applicants.csv header 列数不对 | 确保 header 有 11 列（以 applicationId 开头） |

---

## 最后更新

2026-05-11 — data-flow-change 分支，commit `87118b4`
