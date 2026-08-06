# ghost-web-frame

全栈基础框架项目。后端 Spring Boot，前端 Vue 3（Vite），数据库 MySQL，鉴权 JWT。不使用 Docker。

---

## 技术栈

| 层 | 选型 | 状态 |
| --- | --- | --- |
| 后端 | Java 17 + Spring Boot 3.5.16 + Maven | 已初始化 |
| 前端 | Vue 3 + Vite | 未初始化（空目录） |
| 数据库 | MySQL + MyBatis-Plus | 已接入依赖，待写 Mapper |
| 鉴权 | JWT + Spring Security | 未接入 |
| 容器化 | 无 | 按约定不加 Docker |

---

## 当前目录结构

```text
ghost-web-frame/
├── AGENTS.md                 # 本文件：项目上下文与开发计划
├── ghost-frame-backend/      # Spring Boot 后端
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd
│   └── src/
│       ├── main/
│       │   ├── java/com/ghost/frame/
│       │   │   ├── GhostFrameBackendApplication.java   # 启动类（已有）
│       │   │   ├── common/                             # 空包：统一响应、异常、常量
│       │   │   ├── config/                             # 空包：全局配置
│       │   │   ├── modules/                            # 空包：业务模块（health、auth、user…）
│       │   │   └── security/                           # 空包：JWT、Security 配置
│       │   └── resources/
│       │       └── application.yml                     # 端口 8080、应用名（已有）
│       └── test/
│           └── java/com/ghost/frame/
│               └── GhostFrameBackendApplicationTests.java
└── ghost-frame-frontend/     # 空目录，待 Vite 初始化
```

**尚未创建：** 根目录 `README.md`。

---

## 后端现状（ghost-frame-backend）

### 已有

- Spring Initializr 生成的 Maven 工程
- 依赖：`spring-boot-starter-web`、`spring-boot-starter-validation`、Lombok
- 启动类：`com.ghost.frame.GhostFrameBackendApplication`
- 分层包目录已建好（`common` / `config` / `modules` / `security`），但**尚无业务代码**
- `application.yml`：仅 `spring.application.name` 与 `server.port: 8080`
- 默认上下文加载测试

### 尚未实现

- 统一响应体 `{ code, message, data }`
- 全局异常处理
- `GET /api/health` 健康检查
- MySQL 数据源与 MyBatis-Plus
- `User` 实体与 `UserMapper`
- Spring Security + JWT
- 注册 / 登录 / `/api/auth/me` 接口
- CORS 配置

---

## 前端现状（ghost-frame-frontend）

目录已创建，**尚未执行** `npm create vite`。无 `package.json`、无源码。

---

## 包与分层约定

```text
com.ghost.frame
├── common/          # ApiResponse、GlobalExceptionHandler、错误码、工具类
├── config/          # WebMvc、CORS、Jackson 等配置类
├── security/        # SecurityFilterChain、JwtFilter、JwtUtil
└── modules/
    ├── health/      # HealthController
    ├── auth/        # AuthController、AuthService、DTO
    └── user/        # User 实体、UserMapper
```

原则：**横切能力进 `common` / `config` / `security`，业务进 `modules`，启动类只负责引导。**

API 统一前缀建议：`/api/**`。

---

## 开发阶段与进度

| 阶段 | 内容 | 状态 |
| --- | --- | --- |
| 0 | 仓库结构、根 `.gitignore`、git 仓库 | 已完成（待首次 commit） |
| 1 | 后端空壳：统一响应、异常、`/api/health` | **进行中**（骨架与包已建，核心代码未写） |
| 2 | MySQL + MyBatis-Plus + `User` 表 | 进行中（依赖已加，待建表与 Mapper） |
| 3 | JWT 鉴权：注册 / 登录 / `/me` | 未开始 |
| 4 | 前端 Vite 空壳 + axios 代理 | 未开始 |
| 5 | 前端登录页 + 路由守卫 + 联调 | 未开始 |
| 6 | 权限扩展、日志、测试、Flyway 等 | 未开始 |

---

## 下一步（建议顺序）

### 1. 补全根目录（可选）

- 添加 `README.md`（JDK 17、Maven、Node、MySQL 版本与启动步骤）
- 复制 `application-local.yml.example` 为 `application-local.yml` 并填入本机 MySQL 账号
- 执行首次 `git commit`

### 2. 完成后端阶段 1

在 `ghost-frame-backend` 中依次实现：

1. `common/ApiResponse.java` — 统一响应封装
2. `common/GlobalExceptionHandler.java` — 全局异常与 Validation 错误处理
3. `modules/health/HealthController.java` — `GET /api/health`

**验收：** `./mvnw spring-boot:run` 启动成功，访问 `http://localhost:8080/api/health` 有正常 JSON 响应。

### 3. 接入 MySQL（阶段 2）

1. 本机创建库（建议名：`ghost_frame`）
2. `pom.xml` 添加 `mybatis-plus-spring-boot3-starter` + MySQL 驱动
3. `application.yml` 配置数据源与 `mybatis-plus`
4. `modules/user/` 下建 `User` 实体与 `UserMapper`（继承 `BaseMapper<User>`）
5. 手动建表或后续加 Flyway migration（MyBatis-Plus 不会像 JPA 那样自动建表）

### 4. JWT 鉴权（阶段 3）

1. 引入 `spring-boot-starter-security` + jjwt
2. `POST /api/auth/register`、`POST /api/auth/login`、`GET /api/auth/me`
3. 密码 BCrypt 加密；JWT 过滤器解析 `Authorization: Bearer <token>`
4. 放行 `/api/auth/**`、`/api/health`；CORS 允许 `http://localhost:5173`

### 5. 初始化前端（阶段 4–5）

```bash
cd ghost-frame-frontend
npm create vite@latest . -- --template vue
npm install vue-router pinia axios
```

- 目录：`src/api`、`src/views`、`src/router`、`src/stores`、`src/utils`
- Vite 代理：`/api` → `http://localhost:8080`
- axios 自动带 token；401 跳转登录

**验收：** 注册 → 登录 → 首页 → 刷新保持登录 → 登出。

---

## 本机依赖

| 工具 | 用途 |
| --- | --- |
| JDK 17 | 运行 Spring Boot |
| Maven（或使用项目内 `mvnw`） | 构建后端 |
| Node.js + npm | 前端 Vite |
| MySQL | 用户数据持久化 |
| IntelliJ IDEA / VS Code | 开发 |

---

## 给 AI Agent 的协作约定

- 用户倾向**手动开发**；默认只提供步骤与示例，不批量生成代码，除非明确要求。
- 修改代码时**不改动无关文件**；保持现有包名 `com.ghost.frame` 与目录约定。
- 密钥、数据库密码不进仓库；本地配置用 `application-local.yml` 或环境变量。
- 问答与请求权限用中文；代码注释保持简洁，仅解释非显而易见的逻辑。
- 当前最优先任务：**后端阶段 1**（统一响应 + 异常处理 + health 接口）。

---

## 参考：联调拓扑

```text
浏览器 (localhost:5173)
    ↓ Vite 代理 /api
Spring Boot (localhost:8080)
    ↓ JWT 过滤器
业务模块 (auth / user / health)
    ↓ MyBatis-Plus
MySQL (本机)
```
