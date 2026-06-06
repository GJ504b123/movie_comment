# 切片4：管理员模块（用户审批 + 影片管理 + 评论管理 + 日志查询）

- 日期：2026-06-06
- 分支：main（直接提交）
- 对应契约：apiDesigner.md 2.1–2.8
- 状态：完成

## 做了什么
实现了全部 8 个 Admin API，覆盖用户审批、影片增改删、影评管理（隐藏/显示联动评分冗余）、访问日志查询。

### 新增文件清单（9 个源文件 + 1 测试）

| 文件 | 说明 |
|------|------|
| `dto/AuditRequest.java` | 审核请求 `{action: "approve"/"reject", reason}` |
| `dto/AddMovieRequest.java` | 添加影片请求（title/description/releaseDate/coverUrl/director/cast） |
| `dto/UpdateMovieRequest.java` | 修改影片请求，全部字段可选 |
| `dto/HideReviewRequest.java` | 隐藏/显示评论 `{hidden: boolean}` |
| `dto/AdminReviewVO.java` | 管理端评论列表项（含 movieTitle、username、hidden 布尔） |
| `dto/AccessLogVO.java` | 日志列表项，`static from(AccessLog)` |
| `service/AdminService.java` | 接口：8 个方法 |
| `service/impl/AdminServiceImpl.java` | 实现：审批校验→400、影片软删除→`deleteById`（MyBatis-Plus @TableLogic）、影评隐藏→同事务重算评分冗余 |
| `controller/AdminController.java` | 8 个端点，`@RequestMapping("/api/admin")`，`requireAdmin()` 守卫（401/403） |
| `test/.../AdminTest.java` | 24 个测试：权限校验、待审列表、审批、影片 CRUD、影评管理、日志查询、软删除 |

### 8 个端点对照

| # | 契约 | 方法 | 路径 | 关键逻辑 |
|---|---|---|---|---|
| 2.1 | 待审核用户列表 | GET | `/api/admin/users/pending` | `status=pending`，分页 |
| 2.2 | 审核用户 | PUT | `/api/admin/users/{userId}/audit` | approve→approved；reject→rejected；已处理→400 |
| 2.3 | 添加影片 | POST | `/api/admin/movies` | averageScore=0, reviewCount=0 |
| 2.4 | 修改影片 | PUT | `/api/admin/movies/{movieId}` | 逐字段可选更新 |
| 2.5 | 删除影片 | DELETE | `/api/admin/movies/{movieId}` | 软删除（`@TableLogic`，调 `deleteById`） |
| 2.6 | 影评列表 | GET | `/api/admin/reviews` | 支持 movieId/userId/hidden 过滤 + 分页 + 批量查标题/用户名 |
| 2.7 | 隐藏/显示 | PUT | `/api/admin/reviews/{reviewId}/visibility` | 设置 hidden→刷新影片评分冗余 |
| 2.8 | 访问日志 | GET | `/api/admin/logs` | 支持 userId/action/startTime/endTime 过滤 |

## 关键决策与假设
- 软删除用 MyBatis-Plus `deleteById()` 触发 `@TableLogic`，不能手动 `updateById` 设 `deleted=1`
- `@DirtiesContext(AFTER_CLASS)` 解决 AdminTest 和 MovieReviewTest 共享 H2 导致 username 唯一约束冲突
- 评分冗余 `refreshMovieScore()` 复刻了 ReviewServiceImpl 的逻辑（自包含）

## 与架构/契约的偏差
- 无

## 测试（24 个）
全部通过（独立运行 24；全量 53）

## 下一步
- 切片5：AOP 访问日志切面
