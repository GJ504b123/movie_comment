# 切片3：影片与评论模块（列表搜索 + 详情 + 发评改评删评 + 排行榜 + 评分冗余维护）

- 日期：2026-06-01
- 分支：feat/movie-review
- 对应契约：apiDesigner.md 3.1–3.6
- 状态：完成

## 做了什么
实现了影片与评论的全部 6 个接口，以及评分冗余的事务维护机制。

### 新增文件清单（15 个源文件 + 1 测试）

| 文件 | 说明 |
|------|------|
| `dto/MovieVO.java` | 影片列表项（id/title/coverUrl/averageScore/reviewCount/releaseDate），静态 `from(Movie)` 转换 |
| `dto/MovieDetailVO.java` | 影片详情（所有字段），`from(Movie)` 转换 |
| `dto/ReviewVO.java` | 评论展示（含 username + canEdit），`from(Review, username, canEdit)` |
| `dto/CreateReviewRequest.java` | 发表评论请求，rating `@Min(1) @Max(10) @NotNull` |
| `dto/UpdateReviewRequest.java` | 修改评论请求，rating 和 comment 均可选 |
| `dto/RankingItemVO.java` | 排行榜项（rank/movieId/title/coverUrl/averageScore/reviewCount），`from(Movie, rank)` |
| `service/MovieService.java` | 接口：listMovies / getMovieDetail |
| `service/impl/MovieServiceImpl.java` | 实现：模糊搜索（title/director/cast LIKE）+ 手动分页排序；详情含评论分页 + 批量查用户名 + canEdit 判断 |
| `service/ReviewService.java` | 接口：createReview / updateReview / deleteReview |
| `service/impl/ReviewServiceImpl.java` | 实现：唯一约束校验 → 409、权限校验 → 403、评分冗余同事务刷新 |
| `controller/MovieController.java` | `GET /api/movies`（keyword/page/size/sort）、`GET /api/movies/{id}`（reviewPage/reviewSize） |
| `controller/ReviewController.java` | `POST /api/movies/{id}/reviews`、`PUT /api/reviews/{id}`、`DELETE /api/reviews/{id}`，均需登录 |
| `controller/RankingController.java` | `GET /api/rankings`（limit/timeRange），按 averageScore 降序 |
| `test/.../MovieReviewTest.java` | 12 个测试覆盖全流程 |

### 修改文件

| 文件 | 改动 |
|------|------|
| `config/MyBatisPlusConfig.java` | 无改动（保持原样） |

## 怎么做的

### 影片列表搜索（3.1）
```
GET /api/movies?keyword=星际&page=1&size=10&sort=rating
```
- 用 `LambdaQueryWrapper.or()` 对 title/director/cast 三字段 LIKE
- 排序：`rating` → averageScore DESC，`releaseDate` → releaseDate DESC，默认 id DESC
- 手动分页：先 `selectCount`（不带排序），再 `selectList` + `LIMIT n OFFSET m`
- **为什么手动分页**：MyBatis-Plus 3.5.9 的 `PaginationInnerInterceptor` 类不存在于 classpath，且 H2 严格模式下 `selectCount` + ORDER BY 会报错。改用两次独立 wrapper 的 `selectCount`+`selectList` 方案，兼容 MySQL 和 H2

### 影片详情（3.2）
```
GET /api/movies/{movieId}?reviewPage=1&reviewSize=5
```
- 返回 `{movie: MovieDetailVO, reviews: PageResult<ReviewVO>}`
- 评论只取 `hidden=0`，按 `createTime` 倒序
- `canEdit` = 当前登录用户 id == review.userId（游客为 false）
- 批量查用户名：收集所有评论的 userId → `selectBatchIds` → 构建 Map

### 发表评论（3.3）
```
POST /api/movies/{movieId}/reviews
```
- 校验：用户必须 approved（否则 403）、影片存在且未删除（否则 404）
- 唯一约束：`selectCount(userId, movieId) > 0` → 409
- 插入后**同事务**调用 `refreshMovieScore()`
- 只有非隐藏评论计入评分统计

### 修改评论（3.4）
```
PUT /api/reviews/{reviewId}
```
- 校验：评论存在 → 404，非本人 → 403
- rating 或 comment 至少改一项
- rating 变更时才刷新评分（仅 comment 变更不影响分数）

### 删除评论（3.5）
```
DELETE /api/reviews/{reviewId}
```
- 物理删除该条评论（契约要求）
- **同事务**刷新影片 average_score / review_count

### 排行榜（3.6）
```
GET /api/rankings?limit=10&timeRange=all
```
- 按 `averageScore DESC`，LIMIT 由参数控制（最大 50）
- 返回带 `rank` 序号的对象列表
- timeRange 参数预留，当前仅支持 `all`

### 评分冗余维护
```java
private void refreshMovieScore(Long movieId) {
    // 查该影片所有非隐藏评论
    // count=0 → averageScore=0, reviewCount=0
    // count>0 → avg = stream.mapToInt(::getRating).average()
    // setScale(1, HALF_UP)
    // updateById(movie)
}
```

## 关键决策与假设
- **手动分页**（不用 MyBatis-Plus `selectPage`）：因为 `PaginationInnerInterceptor` 在 3.5.9 classpath 中不存在，且 H2 严格模式对 `COUNT(*) + ORDER BY` 报错
- **评分只统计非隐藏评论**（`hidden=0`）：隐藏评论不应影响排行榜
- **排行榜 timeRange**：参数已定义但只实现 `all`，`week/month` 筛选留到后续（按架构 doc 约定）
- **likeCount**：实体和 DTO 都包含字段，但无点赞接口（架构 doc "契约留白"）。后续切片实现

## 与架构/契约的偏差
- 无

## 测试（12 个用例）

| # | 测试 | 验证内容 | 结果 |
|---|------|---------|------|
| 1 | `setup` | 创建 approved 用户 + 登录 + 2 部影片 | ✅ |
| 2 | `listAllMovies` | 影片列表返回 3 部（含 InfraTest 的 1 部） | ✅ |
| 3 | `searchMovies` | keyword="星际" 搜到 1 部 | ✅ |
| 4 | `searchNoMatch` | keyword="不存在" 搜到 0 部 | ✅ |
| 5 | `getMovieDetail` | 详情含 movie + 空 reviews | ✅ |
| 6 | `getMovieNotFound` | 不存在的影片 → 404 | ✅ |
| 7 | `postReview` | 发表评论 → 201 + reviewId + 评分刷新为 8.0 | ✅ |
| 8 | `duplicateReview` | 重复评论 → 409 | ✅ |
| 9 | `postReviewUnauthenticated` | 无 token → 401 | ✅ |
| 10 | `updateReview` | 修改评分 → 200 + 评分刷新为 9.0 | ✅ |
| 11 | `deleteReview` | 删除评论 → 200 + 评分归零 | ✅ |
| 12 | `rankings` | 排行榜返回影片列表，rank=1 | ✅ |

**全部 20 个测试通过（含原 8 个）：Tests run: 20, Failures: 0, Errors: 0, Skipped: 0**

## 遗留 / 待批准
- 无

## 下一步
- 切片4：管理员模块（待审用户列表、审核、影片增改删、影评管理、日志查询）
