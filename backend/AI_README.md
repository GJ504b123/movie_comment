# 后端工作总入口（DeepSeek 必读）

> 你是《在线影评系统》的**后端**开发执行者。本文件是你的导航。**每次开工先读本文件，再读铁律。**

## 一句话背景
- 仓库是**多人共享**：根目录 `movie-review/` 等是前端队友(Vue)的，**你只动 `backend/`**。
- 你做 Spring Boot + MySQL 后端，给前端提供 REST API。
- **接口契约 = 仓库根 `apiDesigner.md`（权威、只读）**。

## 最高优先级：先读铁律
👉 **`backend/docs/governance/00-core-rules.md`（工作铁律总纲）** —— 七条安全铁律，必须遵守。

## 文档地图
- 治理规则 `backend/docs/governance/`
  - `00-core-rules.md` 工作铁律总纲（最高优先级）
  - `01-danger-and-blocking.md` 危险操作清单 + 阻塞协议
  - `02-git-and-commit.md` 分支 / 提交规范 / 审批流程
  - `03-module-report.md` 每切片报告的模板与位置
  - `04-secrets-and-security.md` 密钥与安全
- 架构 `backend/docs/architecture/`
  - `00-system-overview.md` 系统大图（大致做什么）
  - `01-techstack-and-conventions.md` 技术栈 + 目录结构 + 阿里规范
  - `02-data-model.md` 数据模型概要
- 进度 `backend/docs/progress/`
  - `_index.md` 进度索引（开工先看，别重复劳动）
  - `pending-approval.md` 待批准事项（危险操作/重大决策记这里）
- 契约：仓库根 `apiDesigner.md`（实现以它为准）

## 五条最容易踩的红线（详见铁律）
1. 只动 `backend/`，**碰不到前端**。
2. **不自己 commit / merge / push** —— commit 要用户批准，merge/push 用户来做。
3. 删除 / 删库删表 / 覆盖大段内容 / git 破坏命令 = 危险，**先停后报**。
4. 影片删除是**软删除**（`deleted=true`），别物理删业务数据。
5. 每个切片**写报告**，讲清"怎么做的"。

## 开工固定动作
读：本文件 → `governance/00` → 其余 governance → architecture → 根 `apiDesigner.md` → `progress/_index.md`，然后开干。
