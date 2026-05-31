# 切片1：基础设施（Result + 全局异常 + Swagger + Flyway 建表 + 配置）

- 日期：2026-05-30
- 分支：待用户创建 feat/infra-base
- 对应契约：apiDesigner.md 全部（只建表，接口未实现）
- 状态：完成

## 做了什么
- 新建 Maven 项目骨架（pom.xml + mvnw）
- 新建全部目录结构：common / config / security / aspect / controller / service / mapper / entity / dto
- 写了 19 个源文件 + Flyway V1 迁移 + 测试配置

### 新增/修改文件清单
| 文件 | 说明 |
|------|------|
| `pom.xml` | Spring Boot 3.4.3 + MyBatis-Plus 3.5.9 + Flyway + jjwt 0.12.6 + springdoc 2.8.6 + H2 |
| `.env.example` | 环境变量占位模板 |
| `.mvn/wrapper/` | Maven Wrapper（mvnw），自动下载 Maven 3.9.9 |
| `src/main/java/.../MovieCommentApplication.java` | Spring Boot 启动类 |
| `src/main/java/.../common/Result.java` | 统一响应 `{code, message, data}` |
| `src/main/java/.../common/PageResult.java` | 分页响应 `{list, total, page, size, totalPages}` |
| `src/main/java/.../common/BusinessException.java` | 业务异常（带 code） |
| `src/main/java/.../common/GlobalExceptionHandler.java` | 全局异常 → Result |
| `src/main/java/.../common/Role.java` | 角色枚举 user/admin |
| `src/main/java/.../common/UserStatus.java` | 状态枚举 pending/approved/rejected |
| `src/main/java/.../common/ActionType.java` | 日志动作枚举（对齐 apiDesigner.md 第4节） |
| `src/main/java/.../entity/User.java` | user 表实体 |
| `src/main/java/.../entity/Movie.java` | movie 表实体（含 @TableLogic 软删除 + @TableField 处理 cast 保留字） |
| `src/main/java/.../entity/Review.java` | review 表实体 |
| `src/main/java/.../entity/AccessLog.java` | access_log 表实体 |
| `src/main/java/.../mapper/*.java` | 4 个 MyBatis-Plus Mapper |
| `src/main/java/.../config/MyBatisPlusConfig.java` | MyBatis-Plus 插件配置（自动分页） |
| `src/main/java/.../config/SecurityConfig.java` | BCrypt PasswordEncoder Bean |
| `src/main/java/.../config/WebConfig.java` | CORS 跨域配置 |
| `src/main/resources/application.yml` | 主配置（MySQL + Flyway + MyBatis-Plus + Swagger） |
| `src/main/resources/db/migration/V1__init_tables.sql` | 初始建表：user/movie/review/access_log |
| `src/test/resources/application.yml` | 测试配置（H2 内存库 MySQL 兼容模式） |
| `src/test/java/.../InfrastructureTest.java` | 3 个测试：context 加载、user CRUD、movie CRUD |

## 怎么做的
- **项目骨架**：Maven Wrapper（`mvnw`）方案，项目自带 Maven 下载脚本，无需预装 Maven
- **统一响应**：`Result<T>` 静态工厂方法，`code` 遵循契约 200/201/4xx/5xx
- **全局异常**：`@RestControllerAdvice` 捕获 `BusinessException` + `MethodArgumentNotValidException` + 兜底 Exception
- **Flyway 迁移**：V1 建 4 张表，索引名加前缀防冲突（`idx_review_movie_id`、`idx_log_user_id` 等）
- **SQL 保留字处理**：`user` 在 H2 URL 加 `NON_KEYWORDS=USER`，`cast` 用 `@TableField("`cast`")` 反引号
- **软删除**：MyBatis-Plus `@TableLogic` + `logic-delete-value: "1"` 控制
- **测试**：H2 内存库 MySQL 兼容模式 + Flyway 自动建表，3 个测试全部通过

## 关键决策与假设
- MyBatis-Plus 3.5.9 中 `PaginationInnerInterceptor` 已不存在，分页由 `MybatisPlusInterceptor` 自动处理
- 鉴权方案已定：**自写轻量 JWT 过滤器**（非 Spring Security 完整过滤链）
- 点赞 API 已定：**要做**（后续切片实现）

## 测试
- `contextLoads`：Spring 容器启动 + Flyway 迁移成功 ✅
- `userCrud`：插入用户 + BCrypt 密码验证 ✅
- `movieCrud`：插入影片 + 列表查询 + 软删除过滤 ✅
- 全部通过：`Tests run: 3, Failures: 0, Errors: 0`

## 与架构/契约的偏差
- 无

## 遗留 / 待批准
- 无

## 下一步
- 切片2：认证与用户模块（注册 + 登录 + JWT + 个人信息）
