# 密钥与安全规范

## 原则
- **任何机密**（数据库密码、JWT 密钥、第三方 key）**绝不硬编码**进代码，**绝不提交进仓库**。
- 机密只放在**不进库**的位置：`backend/.env` 或 `backend/src/main/resources/application-local.yml`（被 `.gitignore` 忽略）。
- 代码通过环境变量 / Spring 外部化配置读取（如 `${JWT_SECRET}`、`${DB_PASSWORD}`）。

## 必须提供的占位文件（进库）
- `backend/.env.example`：列出需要哪些变量、给占位值（不含真值）。
- `application.yml` 用 `${VAR:默认值}` 引用，敏感项不写真值。

示例 `.env.example`：
```
DB_URL=jdbc:mysql://localhost:3306/movie_comment?useSSL=false&serverTimezone=Asia/Shanghai
DB_USERNAME=root
DB_PASSWORD=改成你的密码
JWT_SECRET=用一段足够长的随机串替换
JWT_EXPIRE_MINUTES=120
```

## .gitignore 必须包含
`.env`、`application-local.yml`/`*-local.yml`、`target/`、IDE 与 OS 文件。

## 红线
- 把任何**真实**密钥写进会进库的文件 = **危险操作**（停、报、等）。
- 提交前检查 `git diff`，确认没有机密混入。

**Why:** 密钥一旦进了 git 历史，等于公开泄露，且很难彻底清除。
**How to apply:** 配置前先想"这值是机密吗"——是就走 `.env` + 占位；提交前再扫一眼 diff。
