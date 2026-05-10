# EBU6304_Group82 功能测试指南
分支：feature/unified-data-business-flow
日期：2026-05-10

## 测试账号

| 角色 | 账号 | 密码 |
|------|------|------|
| Admin | 0 | 任意 |
| MO | 1-100 任意数字，例如 1 | 任意 |
| TA | 101+，例如 101 | 任意 |

---

## 完整业务流程测试

### 阶段1：MO 创建岗位
1. 启动程序，选择 MO 登录
2. 账号：1，密码：任意
3. Dashboard 点击 "Job Vacancy"
4. 点击 "Create New Position"
5. 填写信息：
   - Module Code: ECS999
   - Module Name: Enterprise Software
   - Required TAs: 2
6. 点击 Submit
7. 验证：data/modules.csv 中出现 Pending 记录

### 阶段2：Admin 审批岗位
1. 退出登录，选择 Admin 登录
2. 账号：0，密码：任意
3. Dashboard 点击 "Course Application"
4. 找到 ECS999，点击 "Approve"
5. 验证：data/modules.csv 中 ECS999 状态变为 Approved

### 阶段3：TA 申请岗位
1. 退出登录，选择 TA 登录
2. 账号：101，密码：任意
3. Dashboard 应能看到 ECS999（Approved 状态）
4. 点击 "Apply" 申请该岗位
5. 填写 Cover Letter
6. 点击 Submit
7. 验证：data/applicants.csv 中出现 Pending 申请记录

### 阶段4：MO 审核申请
1. 退出登录，选择 MO 登录
2. Dashboard 点击 "Applicant List"
3. 找到 TA 的申请，点击审核
4. 选择 "Approved"
5. 验证：data/applicants.csv 中申请状态变为 Approved

### 阶段5：查看统计数据
1. MO 登录后，点击 "Statistics"
2. 验证：能看到 ECS999 的统计数据
3. Admin 登录后，点击 "TA Workload"
4. 验证：能看到 TA 工作量统计

### 阶段6：安排面试
1. MO 登录后，点击 "Schedule Interview"
2. 选择申请人
3. 填写时间、地点
4. 点击 Submit
5. 验证：data/interviews.csv 中出现面试记录

### 阶段7：发送消息
1. MO 登录后，点击 "Message TA"
2. 选择 TA
3. 输入消息内容
4. 点击 Send
5. 验证：data/messages.csv 中出现消息记录

---

## 数据文件说明

| 文件路径 | 用途 | 操作权限 |
|----------|------|----------|
| data/modules.csv | 岗位数据 | MO写入、Admin更新 |
| data/applicants.csv | 申请数据 | TA写入、MO更新 |
| data/interviews.csv | 面试安排 | MO写入 |
| data/messages.csv | 消息记录 | MO写入 |

---

## 预期结果

### modules.csv 字段
moduleCode,moduleName,moId,requiredTas,status,createdBy,createdAt,approvedBy,approvedAt,rejectReason

### applicants.csv 字段
applicationId,taId,taName,moduleCode,moduleName,cvFile,coverLetter,status,submittedAt,reviewedBy,reviewedAt

### interviews.csv 字段
interviewId,taId,taName,moduleCode,interviewTime,location,status,createdBy,createdAt

### messages.csv 字段
messageId,senderId,senderRole,receiverId,receiverRole,content,sentAt

---

## 测试检查清单

- [ ] MO 能成功创建岗位
- [ ] Admin 能看到 Pending 岗位
- [ ] Admin 能审批岗位（Approve/Reject）
- [ ] TA 只能看到 Approved 岗位
- [ ] TA 申请后 applicants.csv 有记录
- [ ] MO 能看到申请人列表
- [ ] MO 能审核申请
- [ ] MO Statistics 能统计 Approved 申请
- [ ] Admin Workload 能统计 TA 工作量
- [ ] 面试安排写入 interviews.csv
- [ ] 消息发送写入 messages.csv
- [ ] 各页面 Back 按钮正常工作
