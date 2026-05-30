# 技术栈与编码规范

## 技术栈（已定）
| 项 | 选型 |
|---|---|
| JDK | **Java 17** |
| 框架 | **Spring Boot 3.x（最新稳定版）** |
| 构建 | **Maven** |
| 持久层 | **MyBatis-Plus**（含分页插件） |
| 数据库 | **MySQL 8** |
| 迁移 | **Flyway**（所有表结构变更走迁移脚本，不手改库） |
| 鉴权 | **JWT**（前后端分离，无 Session） |
| 接口文档 | **springdoc-openapi（Swagger UI）** —— Spring Boot 3 用 springdoc，**不是**老的 springfox |
| 密码 | **BCrypt** |
| 测试 | JUnit 5 + Spring Boot Test（关键路径） |

> 注意：Spring Boot 3 + Java 17 必须用 `jakarta.*` 包（非 `javax.*`）；Swagger 用 `springdoc-openapi-starter-webmvc-ui`。

## 项目结构（去哪做什么）
在 `backend/` 下建议：
```
backend/
├─ pom.xml
├─ .env.example
├─ src/main/java/<basePackage>/
│  ├─ MovieCommentApplication.java
│  ├─ common/        响应包装 Result、统一异常、分页 DTO、常量、枚举(action/role/status)
│  ├─ config/        Web / MyBatisPlus / Swagger / 跨域 等配置
│  ├─ security/      JWT 生成与解析、JWT 过滤器、当前用户上下文、权限校验
│  ├─ aspect/        访问日志 AOP 切面
│  ├─ controller/    REST 入口：参数校验 + 调 service + 包装返回（不写业务）
│  ├─ service/(impl) 业务逻辑（**事务在此层**）
│  ├─ mapper/        MyBatis-Plus Mapper 接口
│  ├─ entity/        与表对应的实体
│  └─ dto/           请求/响应对象（dto/vo），**勿把 entity 直接暴露给前端**
├─ src/main/resources/
│  ├─ application.yml          通用配置（敏感项用 ${VAR}）
│  ├─ application-local.yml    本地机密（**不进库**）
│  └─ db/migration/            Flyway 脚本 V1__*.sql、V2__*.sql ……
└─ src/test/java/...           关键路径测试
```
`<basePackage>` 建议 `com.movie.comment`（或与课程要求一致）。**定后写进报告，别再随意变**。

## 接口实现约定（对齐契约）
- 所有接口前缀 **`/api`**（见 `apiDesigner.md` Base URL）。
- 统一返回 `Result{code,message,data}`；分页返回 `{list,total,page,size,totalPages}`。
- 业务码遵循契约：200 成功、201 创建、401 未认证、403 未授权/未审核、409 冲突、4xxx/5xxx。
- 登录/注册公开，其余需 `Authorization: Bearer <token>`。
- 时间用 ISO 8601。

## 关键依赖（pom 清单，版本由 DeepSeek 取最新稳定）
- `spring-boot-starter-web`、`spring-boot-starter-validation`
- `mybatis-plus-spring-boot3-starter`（**Spring Boot 3 专用 starter**）
- `mysql-connector-j`
- `flyway-core` + `flyway-mysql`
- JWT 库（如 `io.jsonwebtoken:jjwt-api/impl/jackson`）
- `spring-boot-starter-aop`
- `springdoc-openapi-starter-webmvc-ui`
- `spring-security-crypto`（BCrypt 加密；若不引完整 Spring Security 可单用此模块）
- `spring-boot-starter-test`（test）
> **是否引入完整 Spring Security**（用其过滤链管 JWT/权限）vs **自写轻量 JWT 过滤器** —— 属**重大决策**，定前在报告里说明利弊并征求用户意见。

## 编码规范：阿里巴巴《Java 开发手册》
**命名与接口契约严格遵循阿里巴巴 Java 规范**，要点：
- 类名 UpperCamelCase；方法/变量 lowerCamelCase；常量全大写下划线；包名全小写。
- 分层命名规范：`UserController` / `UserService` / `UserServiceImpl` / `UserMapper`。
- **DTO/VO 与 Entity 分离**，禁止用 entity 直接接收前端入参或作为返回体。
- 数据库字段 snake_case，实体驼峰，由 MyBatis-Plus 映射。
- 严禁魔法值，枚举/常量集中管理（如 `role`、`status`、`action`）。
- 方法单一职责；Controller 不写业务，Service 管事务。
- 日志用 SLF4J，**禁止 `System.out`**。

**Why:** 统一技术栈与结构，DeepSeek 才知道"代码放哪、怎么命名"，前端也能稳定对接。
**How to apply:** 建工程时按此结构与 pom；每写一个类先想它属于哪层、命名是否合规。
