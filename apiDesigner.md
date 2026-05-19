

## 基础信息

- **Base URL**：`/api`
- **认证方式**：JWT token，放在 `Authorization: Bearer <token>` 头中（登录/注册接口除外）。
- **统一响应格式**：

```json
{
  "code": 200,        // 业务状态码：200成功，4xxx客户端错误，5xxx服务端错误
  "message": "成功",
  "data": {}          // 具体数据
}
```

- **分页请求参数**（适用于列表接口）：
  - `page`：页码，从 1 开始，默认 1
  - `size`：每页条数，默认 10
  - `sort`：排序字段，可选，如 `createTime,desc`

- **分页响应结构**：
```json
{
  "list": [],//当前页面的数据
  "total": 100, //总记录页数
  "page": 1, //当前页码
  "size": 10, //每页大小（记录数）
  "totalPages": 10 //总页数
}
```

---

## 1. 认证与用户模块

### 1.1 用户注册
- **URL**：`/auth/register`
- **Method**：`POST`
- **公开**：是
- **Request Body**：
```json
{
  "username": "filmfan",
  "password": "123456",
  "email": "fan@example.com"
}
```
- **Response (200)**：
```json
{
  "code": 200,
  "message": "注册成功，等待管理员审核",
  "data": {
    "userId": 101,
    "status": "pending"
  }
}
```

### 1.2 用户登录
- **URL**：`/auth/login`
- **Method**：`POST`
- **公开**：是
- **Request Body**：
```json
{
  "username": "filmfan",
  "password": "123456"
}
```
- **Response (200)**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "id": 101,
      "username": "filmfan",
      "role": "user",
      "status": "approved"
    }
  }
}
```
- **特殊错误**：`status` 为 `pending` 时返回 403 并提示“等待审核”；`rejected` 时返回 403 提示“审核未通过”。

### 1.3 获取当前用户信息
- **URL**：`/user/profile`
- **Method**：`GET`
- **认证**：需要
- **Response (200)**：
```json
{
  "code": 200,
  "data": {
    "id": 101,
    "username": "filmfan",
    "email": "fan@example.com",
    "role": "user",
    "status": "approved",
    "createTime": "2025-01-01T10:00:00Z",
    "lastLoginTime": "2025-03-20T15:30:00Z"
  }
}
```

### 1.4 更新用户信息（自己）
- **URL**：`/user/profile`
- **Method**：`PUT`
- **认证**：需要
- **Request Body**（所有字段可选）：
```json
{
  "email": "newemail@example.com",
"oldPassword": "123456", 
"newPassword": "newpassword123" 
}
```
- **Response (200)**：
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

---

## 2. 管理员专用接口

### 2.1 获取待审核用户列表
- **URL**：`/admin/users/pending`
- **Method**：`GET`
- **权限**：`admin`
- **Query 参数**：`page`, `size`, `sort`
- **Response (200)**：
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 102,
        "username": "newbie",
        "email": "newbie@example.com",
        "createTime": "2025-03-19T08:00:00Z"
      }
    ],
    "total": 5,
    "page": 1,
    "size": 10,
    "totalPages": 1
  }
}
```

### 2.2 审核用户（通过/拒绝）
- **URL**：`/admin/users/{userId}/audit`
- **Method**：`PUT`
- **权限**：`admin`
- **Path Variable**：`userId` (整数)
- **Request Body**：
```json
{
  "action": "approve",   // "approve" 或 "reject"
  "reason": "信息真实"    // 可选，拒绝时建议提供
}
```
- **Response (200)**：
```json
{
  "code": 200,
  "message": "审核通过",
  "data": null
}
```

### 2.3 添加影片
- **URL**：`/admin/movies`
- **Method**：`POST`
- **权限**：`admin`
- **Request Body**：
```json
{
  "title": "肖申克的救赎",
  "description": "一场谋杀使银行家安迪蒙冤入狱...",
  "releaseDate": "1994-09-23",
  "coverUrl": "https://example.com/cover.jpg",
  "director": "弗兰克·德拉邦特",
  "cast": "蒂姆·罗宾斯, 摩根·弗里曼"
}
```
- **Response (201)**：
```json
{
  "code": 201,
  "message": "影片添加成功",
  "data": {
    "movieId": 201
  }
}
```

### 2.4 修改影片信息
- **URL**：`/admin/movies/{movieId}`
- **Method**：`PUT`
- **权限**：`admin`
- **Path Variable**：`movieId`
- **Request Body**：同添加，所有字段可选
- **Response (200)**：
```json
{
  "code": 200,
  "message": "更新成功"
}
```

### 2.5 删除影片
- **URL**：`/admin/movies/{movieId}`
- **Method**：`DELETE`
- **权限**：`admin`
- **Response (200)**：
```json
{
  "code": 200,
  "message": "删除成功",
"delete":true
}
```
给 movie 表加 deleted 字段，删除时置为 true，评论仍然保留但前端不展示。更好，因为管理员可能误删

### 2.6 获取所有影评（管理视图）
- **URL**：`/admin/reviews`
- **Method**：`GET`
- **权限**：`admin`
- **Query 参数**：`movieId`（可选）、`userId`（可选）、`hidden`（可选布尔，过滤被隐藏的评论）、`page`、`size`
- **Response (200)**：
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 301,
        "movieTitle": "肖申克的救赎",
        "username": "filmfan",
        "rating": 9,
        "comment": "经典之作",
        "likeCount": 15,
        "hidden": false,
        "createTime": "2025-03-18T12:00:00Z"
      }
    ],
    "total": 50,
    "page": 1,
    "size": 10
  }
}
```

### 2.7 隐藏/显示评论
- **URL**：`/admin/reviews/{reviewId}/visibility`
- **Method**：`PUT`
- **权限**：`admin`
- **Path Variable**：`reviewId`
- **Request Body**：
```json
{
  "hidden": true   // true=隐藏, false=恢复显示
}
```
- **Response (200)**：
```json
{
  "code": 200,
  "message": "操作成功"
}
```

### 2.8 查询访问日志
- **URL**：`/admin/logs`
- **Method**：`GET`
- **权限**：`admin`
- **Query 参数**：
  - `userId`（可选）
  - `action`（可选，如 `search_movie`、`post_review`）
  - `startTime` / `endTime`（ISO 8601 格式）
  - `page`, `size`
- **Response (200)**：
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 401,
        "userId": 101,
        "username": "filmfan",
        "action": "search_movie",
        "targetId": null,
        "ip": "192.168.1.1",
        "userAgent": "Mozilla/5.0 ...",
        "createTime": "2025-03-20T16:20:00Z"
      }
    ],
    "total": 200,
    "page": 1,
    "size": 20
  }
}
```

---

## 3. 影片与评论模块（普通用户 + 游客）

### 3.1 查询影片列表（支持模糊搜索）
- **URL**：`/movies`
- **Method**：`GET`
- **认证**：可选（若已登录，后端可记录日志）
- **Query 参数**：
  - `keyword`（字符串，模糊匹配标题或导演/演员）
  - `page`, `size`
  - `sort`：可选值 `rating`（按评分）、`releaseDate`（按上映时间）
 **影片的平均评分和评论数由后端在评论变更时实时维护，保证排行榜数据一致性**
- **Response (200)**：
```json
{
  "code": 200,
  "data": {
    "list": [
      {
        "id": 201,
        "title": "肖申克的救赎",
        "coverUrl": "https://...",
        "averageScore": 9.2,
        "reviewCount": 128,
        "releaseDate": "1994-09-23"
      }
    ],
    "total": 30,
    "page": 1,
    "size": 10,
    "totalPages": 3
  }
}
```

### 3.2 获取影片详情（含评论列表）
- **URL**：`/movies/{movieId}`
- **Method**：`GET`
- **认证**：可选
- **Path Variable**：`movieId`
- **Query 参数**：`reviewPage`（评论分页）、`reviewSize`（默认5）
- **Response (200)**：
```json
{
  "code": 200,
  "data": {
    "movie": {
      "id": 201,
      "title": "肖申克的救赎",
      "description": "...",
      "releaseDate": "1994-09-23",
      "coverUrl": "https://...",
      "director": "弗兰克·德拉邦特",
      "cast": "蒂姆·罗宾斯, 摩根·弗里曼",
      "averageScore": 9.2,
      "reviewCount": 128
    },
    "reviews": {
      "list": [
        {
          "id": 301,
          "userId": 101,
          "username": "filmfan",
          "rating": 10,
          "comment": "永远的神作",
          "likeCount": 32,
          "createTime": "2025-03-01T09:00:00Z",
          "canEdit": true   // 仅当当前登录用户为本人时返回 true
        }
      ],
      "total": 128,
      "page": 1,
      "size": 5,
      "totalPages": 26
    }
  }
}
```

### 3.3 发表评论/评分
- **URL**：`/movies/{movieId}/reviews`
- **Method**：`POST`
- **认证**：需要（用户状态必须为 `approved`）
- **Path Variable**：`movieId`
- **Request Body**：
```json
{
  "rating": 9,      // 整数 1-10
  "comment": "扣一分因为结尾有点仓促"
}
```
- **Response (201)**：
```json
{
  "code": 201,
  "message": "评论成功",
  "data": {
    "reviewId": 302
  }
}
```
- **业务约束**：一个用户对同一部电影只能有一条评论（若重复提交，后端返回 409）。

### 3.4 修改自己的评论
- **URL**：`/reviews/{reviewId}`
- **Method**：`PUT`
- **认证**：需要，且必须是评论作者本人
- **Request Body**：
```json
{
  "rating": 10,
  "comment": "重新思考后决定给满分"
}
```
- **Response (200)**：
```json
{
  "code": 200,
  "message": "修改成功"
}
```

### 3.5 删除自己的评论
- **URL**：`/reviews/{reviewId}`
- **Method**：`DELETE`
- **认证**：需要，且必须是评论作者本人
- **Response (200)**：
```json
{
  "code": 200,
  "message": "删除成功"
}
```

### 3.6 影片排行榜
- **URL**：`/rankings`
- **Method**：`GET`
- **认证**：可选
- **Query 参数**：
  - `limit`：返回前几名，默认 10，最大 50
  - `timeRange`：可选 `week` / `month` / `all`，默认 `all`（若实现时间筛选）
- **Response (200)**：
```json
{
  "code": 200,
  "data": [
    {
      "rank": 1,
      "movieId": 201,
      "title": "肖申克的救赎",
      "coverUrl": "https://...",
      "averageScore": 9.2,
      "reviewCount": 128
    },
    {
      "rank": 2,
      "movieId": 202,
      "title": "霸王别姬",
      "averageScore": 9.0,
      "reviewCount": 95
    }
  ]
}
```

---

## 4. 访问日志（前端无感知，后端自动记录）

- **前端不需要调用任何日志接口**，日志由后端自动记录（如通过 AOP 或拦截器）。
- 管理员通过 `GET /admin/logs` 查询。
- **记录的动作类型（action）建议枚举**：
  - `login`、`register`
  - `search_movie`（调用 GET /movies 带 keyword）
  - `view_movie_detail`（GET /movies/{id}）
  - `post_review`（POST /movies/{id}/reviews）
  - `update_review`、`delete_review`
  - `view_ranking`（GET /rankings）
  - 管理员操作：`audit_user`、`add_movie`、`edit_movie`、`delete_movie`、`hide_review`

---

## 5. 错误响应示例

```json
{
  "code": 401,
  "message": "未提供认证凭证",
  "data": null
}
```

```json
{
  "code": 403,
  "message": "用户尚未通过审核，无法执行此操作",
  "data": null
}
```

```json
{
  "code": 409,
  "message": "您已经评论过这部电影，不能重复评论",
  "data": null
}
```

---

## 总结

这份 API 设计覆盖了课程要求的所有功能点：
- 用户注册、管理员审核、登录
- 影片增删改查（管理员）和搜索（普通用户）
- 评论/评分、影片详情展示
- 排行榜
- 访问日志记录与查询

