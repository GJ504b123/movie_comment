# 数据模型概要

> 高层设计：给出表、关键字段、关系与约束；**完整 DDL 由 DeepSeek 写成 Flyway 迁移**。字段 snake_case，实体驼峰；各表带 `create_time`，需要的带 `update_time`。

## 表 1：`user`（用户）
| 字段 | 说明 |
|---|---|
| id | 主键 |
| username | 唯一，登录名 |
| password | **BCrypt 密文**，禁明文 |
| email | 邮箱 |
| role | `user` / `admin` |
| status | `pending` / `approved` / `rejected` |
| create_time / last_login_time | 时间 |
- 约束：`username` 唯一；注册默认 `role=user, status=pending`。

## 表 2：`movie`（影片）
| 字段 | 说明 |
|---|---|
| id | 主键 |
| title / description / director / cast | 影片信息 |
| release_date | 上映日期 |
| cover_url | 封面 |
| average_score | **冗余**：平均分，评论变更时维护 |
| review_count | **冗余**：评论数，评论变更时维护 |
| deleted | **软删除**标记，默认 false |
| create_time | 时间 |
- 搜索：`title`/`director`/`cast` 模糊匹配；列表/详情/排行**只取 `deleted=false`**。

## 表 3：`review`（影评/评分）
| 字段 | 说明 |
|---|---|
| id | 主键 |
| movie_id | 外键 → movie |
| user_id | 外键 → user |
| rating | 整数 1–10 |
| comment | 评论文本 |
| like_count | 点赞数，默认 0（**暂无点赞接口**，见总览"契约留白"） |
| hidden | 管理员隐藏标记，默认 false |
| create_time | 时间 |
- 约束：**唯一(`user_id`, `movie_id`)** —— 一人一片一评（重复 → 409）。
- 用户删自己的评论（契约 3.5）：**物理删该条即可**，删后必须**同事务内刷新该影片的 `average_score`/`review_count`**。这是正常业务功能，**非危险操作**。

## 表 4：`access_log`（访问日志）
| 字段 | 说明 |
|---|---|
| id | 主键 |
| user_id | 操作者（游客可空） |
| action | 动作枚举（见 `apiDesigner.md` 第 4 节：login/register/search_movie/…） |
| target_id | 目标对象 id（如 movieId），可空 |
| ip / user_agent | 来源 |
| create_time | 时间 |
- 由 AOP 切面自动写入；管理员按 userId/action/时间范围分页查询。

## 关系
- user 1—N review；movie 1—N review；user 1—N access_log。
- 排行榜直接读 `movie.average_score` 降序（已冗余，无需实时聚合）。

## 冗余维护规则（重要）
评论**新增 / 修改评分 / 删除**时，必须在**同一事务**内重算并更新该 `movie` 的 `average_score`（保留 1 位小数）与 `review_count`。这是排行榜与详情页一致性的关键。

**Why:** 把数据形状、约束、冗余维护讲清，DeepSeek 才能写出与契约一致、排行榜不出错的库。
**How to apply:** 按此写 Flyway 迁移与实体；评分相关的写操作记得在事务里刷新冗余字段。
