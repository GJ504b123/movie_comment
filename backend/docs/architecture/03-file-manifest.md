# 文件清单与调用关系（切片1完成时）

> 每新增文件在此登记。读完本文你能回答：**"这个文件干什么的、谁调它、它调谁"**。

---

## 项目根（backend/）

| 文件 | 职责 | 谁用它 |
|------|------|--------|
| `pom.xml` | Maven 依赖清单 + 构建配置。定了 Spring Boot 3.4.3、MyBatis-Plus 3.5.9、Flyway、jjwt、springdoc、H2、BCrypt | `./mvnw` 读取并解析依赖 |
| `mvnw` | Maven Wrapper 脚本。不需要预装 Maven，首次运行自动下载 Maven 3.9.9 | 开发者直接执行 |
| `.mvn/wrapper/maven-wrapper.jar` | Wrapper 的 Java 部分，负责下载/启动 Maven | `mvnw` 脚本调用 |
| `.mvn/wrapper/maven-wrapper.properties` | 指定 Maven 版本和下载地址 | `mvnw` 脚本读取 |
| `.env.example` | 环境变量占位模板。列了 DB_URL / DB_PASSWORD / JWT_SECRET / SERVER_PORT 四项 | 开发者复制为 `.env` 后填真值 |

## 配置层（src/main/resources/）

| 文件 | 职责 | 关键配置项 |
|------|------|----------|
| `application.yml` | Spring Boot 主配置 | `server.port`、`spring.datasource.*`、`flyway.enabled`、`mybatis-plus.*`、`springdoc.*` |
| `db/migration/V1__init_tables.sql` | Flyway 初始迁移，建 4 张表 | 见下文"数据库"小节 |

## 启动入口

| 文件 | 职责 |
|------|------|
| `MovieCommentApplication.java` | `@SpringBootApplication` 启动类，`main()` 一行 `SpringApplication.run(...)` |

---

## 分层代码（src/main/java/com/movie/comment/）

### common/ —— 公共基础设施（6 个文件）

**职责：** 统一响应格式、异常处理、枚举常量。被 Controller 和 Service 直接依赖。

| 文件 | 职责 | 谁调它 | 它调谁 |
|------|------|--------|--------|
| `Result.java` | 统一响应体 `{code, message, data}`，提供静态工厂：`Result.ok(data)` / `Result.created(msg,data)` / `Result.error(code,msg)` | **Controller** 调用它包装返回给前端；**GlobalExceptionHandler** 调用它返回错误体 | 无 |
| `PageResult.java` | 分页响应 `{list, total, page, size, totalPages}`，构造时自动算 `totalPages` | **Controller** 中列表接口调用它 | 无 |
| `BusinessException.java` | 业务异常，带 `int code` + `String message` | **Service** 层在业务不满足时 `throw new BusinessException(409, "不能重复评论")` | 继承 `RuntimeException` |
| `GlobalExceptionHandler.java` | `@RestControllerAdvice`，捕获所有异常并转成 `Result` | **Spring** 自动拦截 Controller 抛出的异常 | 调用 `Result.error()` |
| `Role.java` | 枚举 `USER("user")` / `ADMIN("admin")` | **Service** 做权限判断时引用 | 无 |
| `UserStatus.java` | 枚举 `PENDING` / `APPROVED` / `REJECTED` | **Service** 做状态校验时引用 | 无 |
| `ActionType.java` | 枚举：登录/注册/搜索/看详情/发评/改评/删评/看排行/点赞/审核用户/增影片/改影片/删影片/隐藏评论 | **AOP 切面**（切片5实现）写日志时用 | 无 |

**调用链条：**
```
Controller → Result.ok(data)              ← 正常返回
Service   → throw BusinessException(409)  ← 业务错误
              ↓
GlobalExceptionHandler                    ← 拦截
              ↓
         Result.error(409, msg)           ← 统一错误体返回前端
```

### entity/ —— 数据库实体（4 个文件）

**职责：** 和数据库表一一对应的 POJO。MyBatis-Plus 用它生成 SQL。

| 文件 | 对应表 | 特殊注解 | 注意事项 |
|------|--------|----------|----------|
| `User.java` | `user` | `@TableName("user")`，`@TableId(type=IdType.AUTO)` | `user` 是 SQL 保留字，H2 测试需 URL 加 `NON_KEYWORDS=USER` |
| `Movie.java` | `movie` | `@TableLogic` 软删除，`@TableField("\`cast\`")` 转义保留字 | `deleted` 字段由 MyBatis-Plus 自动在 WHERE 后加 `deleted=0` |
| `Review.java` | `review` | `@TableId(type=IdType.AUTO)` | `user_id` + `movie_id` 有唯一约束（一人一片一评） |
| `AccessLog.java` | `access_log` | `@TableId(type=IdType.AUTO)` | 纯插入，不修改不删除 |

**映射规则：** `map-underscore-to-camel-case: true`，自动把 `create_time` ↔ `createTime`。

### mapper/ —— 数据访问层（4 个文件）

**职责：** MyBatis-Plus 的数据访问接口。**空接口即可**——继承 `BaseMapper<T>` 自动获得全套 CRUD。

| 文件 | 继承 | 自动提供的方法 |
|------|------|-------------|
| `UserMapper.java` | `BaseMapper<User>` | `insert / deleteById / updateById / selectById / selectList / selectPage` 等 |
| `MovieMapper.java` | `BaseMapper<Movie>` | 同上，且自动在 WHERE 后拼 `deleted=0`（`@TableLogic` 效果） |
| `ReviewMapper.java` | `BaseMapper<Review>` | 同上 |
| `AccessLogMapper.java` | `BaseMapper<AccessLog>` | 同上 |

**调用方向：** `Service` → `Mapper` → MyBatis-Plus 生成 SQL → JDBC 执行。

### config/ —— 配置类（3 个文件）

**职责：** 注册 Bean、启用跨域、连接各种组件的"胶水"。

| 文件 | 注册了什么 Bean | 为什么需要 |
|------|---------------|----------|
| `MyBatisPlusConfig.java` | `MybatisPlusInterceptor` | 启用分页插件。不加这个 `selectPage()` 不工作。注：3.5.9 中 `PaginationInnerInterceptor` 已移除，`MybatisPlusInterceptor` 自身就处理分页 |
| `SecurityConfig.java` | `BCryptPasswordEncoder`（实现 `PasswordEncoder`） | 注册/登录时对密码做 BCrypt 加密和比对 |
| `WebConfig.java` | `WebMvcConfigurer` 配置 CORS | 允许前端跨域调用 `/api/**`，否则浏览器拦截 |

---

## 数据库（Flyway V1__init_tables.sql）

### 四张表一张图

```
user ──1:N──▶ review ◀──N:1── movie
 │                                  │
 └──1:N──▶ access_log              （无直接关系）
```

### 表结构速查

**user 表**
| 列 | 类型 | 约束 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | |
| username | VARCHAR(50) UNIQUE NOT NULL | 登录名 |
| password | VARCHAR(255) NOT NULL | BCrypt 密文 |
| email | VARCHAR(100) | |
| role | VARCHAR(10) DEFAULT 'user' | user / admin |
| status | VARCHAR(10) DEFAULT 'pending' | pending / approved / rejected |
| create_time | DATETIME | 注册时间 |
| last_login_time | DATETIME | 最后登录 |

**movie 表**
| 列 | 类型 | 约束 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | |
| title | VARCHAR(200) NOT NULL | |
| description | TEXT | |
| director | VARCHAR(100) | |
| cast | VARCHAR(500) | ⚠️ SQL 保留字，用反引号转义 |
| release_date | DATE | |
| cover_url | VARCHAR(500) | |
| average_score | DECIMAL(3,1) DEFAULT 0.0 | 冗余字段，评论变更时维护 |
| review_count | INT DEFAULT 0 | 冗余字段，评论变更时维护 |
| deleted | TINYINT(1) DEFAULT 0 | 软删除标记，MyBatis-Plus `@TableLogic` 管理 |
| create_time | DATETIME | |

**review 表**
| 列 | 类型 | 约束 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | |
| movie_id | BIGINT NOT NULL | 外键→movie |
| user_id | BIGINT NOT NULL | 外键→user |
| rating | INT NOT NULL | 1-10 |
| comment | TEXT | |
| like_count | INT DEFAULT 0 | |
| hidden | TINYINT(1) DEFAULT 0 | 管理员隐藏 |
| create_time | DATETIME | |
| **唯一索引** | `uk_user_movie (user_id, movie_id)` | 一人一片一评 |
| 普通索引 | `idx_review_movie_id` / `idx_review_user_id` | 按影片/用户查评论 |

**access_log 表**
| 列 | 类型 | 约束 |
|----|------|------|
| id | BIGINT PK AUTO_INCREMENT | |
| user_id | BIGINT | 游客可空 |
| username | VARCHAR(50) | 冗余，方便查询 |
| action | VARCHAR(30) NOT NULL | 动作枚举 |
| target_id | BIGINT | |
| ip | VARCHAR(50) | |
| user_agent | VARCHAR(500) | |
| create_time | DATETIME | |
| 普通索引 | `idx_log_user_id` / `idx_log_action` / `idx_log_create_time` | |

---

## 测试层（src/test/）

| 文件 | 职责 |
|------|------|
| `resources/application.yml` | 测试用 H2 内存库配置。URL 加 `MODE=MySQL;NON_KEYWORDS=USER` 兼容 MySQL 语法、避免 `user` 保留字冲突 |
| `java/.../InfrastructureTest.java` | 3 个测试：`contextLoads`（Spring 启动）、`userCrud`（BCrypt + 持久化）、`movieCrud`（软删除过滤） |

---

## 关键依赖调用链（跨层总览）

```
HTTP 请求
  ↓
WebConfig (CORS 放行)
  ↓
Controller（参数校验 + 调 Service）
  ↓
Service（业务逻辑 + @Transactional 事务）
  ↓
Mapper（MyBatis-Plus BaseMapper）
  ↓
实体 Entity（↔ 数据库表）
  ↓
MySQL（生产） / H2（测试）

异常路径：
  Service throw BusinessException
    → GlobalExceptionHandler 拦截
    → Result.error(code, msg)
    → JSON 返回前端

鉴权路径（切片2实现）：
  JWT 过滤器 → 解析 token → 注入当前用户 → Controller 获取
```
