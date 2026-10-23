# ghost-web-frame

全栈基础框架项目。后端 Spring Boot，前端 Vue 3（Vite），数据库 MySQL，鉴权 JWT。不使用 Docker。

---

## 技术栈

| 层 | 选型 | 状态 |
| --- | --- | --- |
| 后端 | Java 17 + Spring Boot 3.5.16 + Maven | 已接入 Web / Validation / Security |
| 前端 | Vue 3 + Vite | 已初始化（Vite 空壳，deps 已装，src 待建） |
| 数据库 | MySQL + MyBatis-Plus 3.5.9 | 已接入，实体与 Mapper 已写，建表 SQL 已落地 |
| 鉴权 | JWT（jjwt 0.12.6）+ Spring Security | 已接入过滤器与配置 |
| 接口文档 | knife4j 4.5.0 + springdoc-openapi 2.7.0 | 已接入 |
| 工具库 | Hutool 5.8.47、Lombok | 已引入 |
| 容器化 | 无 | 按约定不加 Docker |

---

## 当前目录结构

```text
ghost-web-frame/
├── AGENTS.md
├── .gitattributes / .gitignore
├── ghost-frame-backend/
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd / .mvn/（尚未 git add）
│   ├── data_table/                    # 表结构脚本（见下方规范）
│   │   ├── README.md
│   │   └── schema/user.sql            # user 表全量建表 SQL
│   └── src/
│       ├── main/
│       │   ├── java/com/ghost/frame/
│       │   │   ├── GhostFrameBackendApplication.java
│       │   │   ├── common/
│       │   │   │   ├── Response.java                 # 统一响应体
│       │   │   │   ├── GlobalExceptionHandler.java
│       │   │   │   ├── MyMetaObjectHandler.java      # 自动填充 create/update 字段
│       │   │   │   ├── enums/ErrorCode.java
│       │   │   │   └── exception/BusinessException.java
│       │   │   ├── config/
│       │   │   │   └── OpenApiConfig.java            # knife4j / springdoc
│       │   │   ├── security/
│       │   │   │   ├── SecurityConfig.java           # 过滤链 + CORS
│       │   │   │   ├── JwtAuthenticationFilter.java
│       │   │   │   └── JwtUtil.java
│       │   │   └── modules/                          # 按层分包（见下方说明）
│       │   │       ├── controller/
│       │   │       │   ├── BaseController.java       # getUserId() 取当前登录用户
│       │   │       │   └── LoginRegisterController.java  # /ghost/auth/login、/register、/me
│       │   │       ├── service/ (+ impl/)            # UserService / UserServiceImpl
│       │   │       ├── entity/                      # User、BaseEntity
│       │   │       ├── mapper/                       # UserMapper
│       │   │       ├── dto/                          # LoginRequestDTO、RegisterRequestDTO
│       │   │       ├── vo/                           # LoginRequestVO
│       │   │       └── enums/                      # UserRole、UserStatus、DeleteFlag
│       │   └── resources/
│       │       ├── application.yml                 # 默认配置，profile=local，含 api: /ghost
│       │       ├── application-local.yml            # 本机私有配置（不入库）
│       │       ├── application-local.yml.example
│       │       ├── config/application-knife4j.yml
│       │       └── com/ghost/frame/modules/mapper/UserMapper.xml   # 已配 mapper-locations
│       └── test/java/com/ghost/frame/GhostFrameBackendApplicationTests.java
└── ghost-frame-frontend/
    ├── package.json / package-lock.json              # vue-router / pinia / axios 已装
    ├── vite.config.js                                # 代理 /ghost → localhost:8080
    ├── index.html
    └── src/                                          # 仍是 Vite 默认模板（App.vue / HelloWorld.vue）
```

**尚未创建：** 根目录 `README.md`、前端业务代码（`src/api`、`router`、`views`、`stores`、`utils`）。

---

## 包结构约定（实际落地）

早期计划按业务模块分包（`modules/health/`、`modules/auth/`、`modules/user/`），实际改为**按层分包**：

```text
com.ghost.frame
├── common/          # Response、GlobalExceptionHandler、BusinessException、ErrorCode、MyMetaObjectHandler
│   ├── enums/       # ErrorCode
│   └── exception/   # BusinessException
├── config/         # OpenApiConfig（后续放 WebMvc、Jackson）
├── security/       # SecurityFilterChain（含 CORS）、JwtAuthenticationFilter、JwtUtil
└── modules/
    ├── controller/  # 所有 REST 控制器（BaseController 提供 getUserId()）
    ├── service/     # 业务接口，impl/ 放实现
    ├── entity/      # 数据库实体（含 BaseEntity 公共字段）
    ├── mapper/      # MyBatis-Plus Mapper 接口
    ├── dto/         # 请求 DTO
    ├── vo/          # 响应 VO
    └── enums/       # 业务枚举
```

原则不变：横切能力进 `common` / `config` / `security`，业务进 `modules`。新增业务时继续按层在 `modules/` 下加包，不要回退到按业务拆子包。

API 统一前缀：`/ghost/**`（前后端一致，由 `${api}` 配置注入）。

---

## 后端现状

### 已完成

- 统一响应体 `Response<T>` + 全局异常 `GlobalExceptionHandler` + `BusinessException` + `ErrorCode`
- MySQL 数据源 + MyBatis-Plus（含逻辑删除 `deleted`、下划线转驼峰、`id-type: auto`）
- `User` / `BaseEntity` 实体、`UserMapper`、`MyMetaObjectHandler` 自动填充
- Spring Security 过滤链 + `JwtUtil` + `JwtAuthenticationFilter`（Bearer Token 解析）
- `POST /ghost/auth/register`、`POST /ghost/auth/login`（BCrypt 密码、返回 JWT）
- `GET /ghost/auth/me`（当前登录用户，经 `BaseController.getUserId()` 从 `SecurityContextHolder` 取 id）
- CORS 已配置（`SecurityConfig.corsConfigurationSource` 放行 `http://localhost:5173`）
- knife4j 接口文档（已强制覆盖 springdoc 到 2.7.0 兼容 Spring Framework 6.2.x）
- 建表 SQL 已落地：`data_table/schema/user.sql`
- `application-local.yml` 已落地，`application.yml` 通过 `${mysql.username}` / `${mysql.password}` / `${jwt.jwt-secret}` 注入敏感值

### 尚未实现

- 单元测试 / 集成测试（只有默认上下文加载测试）
- 登出、Token 刷新、权限注解（`@PreAuthorize`）
- 前端业务页面（登录/注册/首页、路由守卫、axios 拦截器）

---

## 前端现状（ghost-frame-frontend）

Vite 已初始化，`package.json` 已含 `vue-router` / `pinia` / `axios`。`vite.config.js` 已配代理 `/ghost` → `http://localhost:8080`。

`src` 目前仍是 Vite 默认模板（`App.vue` / `main.js` / `components/HelloWorld.vue`），**尚未创建** `src/api`、`src/router`、`src/views`、`src/stores`、`src/utils`，也无登录/注册页面。

---

## 开发阶段与进度

| 阶段 | 内容 | 状态 |
| --- | --- | --- |
| 0 | 仓库结构、根 `.gitignore`、git 仓库 | 已完成（mvnw 等尚未入库） |
| 1 | 后端空壳：统一响应、异常 | 已完成（早期 `/api/health` 已随前缀调整移除） |
| 2 | MySQL + MyBatis-Plus + `user` 表 | 已完成（含建表 SQL `data_table/schema/user.sql`） |
| 3 | JWT 鉴权：注册 / 登录 / `/me` | 已完成（含 CORS 放行 `localhost:5173`） |
| 4 | 前端 Vite 壳 + axios 代理 | 部分完成（Vite + 依赖 + 代理已就绪，`src` 仍默认模板） |
| 5 | 前端登录页 + 路由守卫 + 联调 | 未开始 |
| 6 | 权限扩展、日志、测试、Flyway 等 | 未开始 |

---

## 已知问题与约束（改动前必读）

1. **`${api}` 占位符必须解析成功**：`LoginRegisterController` 使用 `@RequestMapping("${api}/auth")`，**API 统一前缀为 `/ghost`**。`application.yml` 与 `application-local.yml` **都已定义** `api: /ghost`；若改动前缀，两处必须同步，否则启动报 `Could not resolve placeholder 'api'` 或前后端不一致。
2. **`UserMapper.xml` 扫描已配置**：文件在 `resources/com/ghost/frame/modules/mapper/` 下，`application.yml` 已加 `mybatis-plus.mapper-locations: classpath*:com.ghost.frame.modules.mapper/*.xml`，无需再改。
3. **数据库名实际为 `silly_ghost_frame`**（不是早期建议的 `ghost_frame`）。本机需手动建库 + 建 `user` 表（`data_table/schema/user.sql`），否则注册/登录必失败。
4. **JWT 有效期 12 小时**（`jwt.expiration-ms: 43200000`），Secret 从环境变量 `jwt.jwt-secret` 注入，不入库。
5. **未提交文件**：`.gitattributes`、`ghost-frame-backend/mvnw`、`mvnw.cmd`、`.mvn/` 处于 untracked，下次提交一并入库。

---

## 数据库脚本规范

所有表结构脚本放在 `ghost-frame-backend/data_table/`，详细规范见该目录下 `README.md`。关键约束：

- **一张表一个 `.sql` 文件**，文件名 = 表名；建表语句和建索引语句分两段写，不要把索引内嵌在 `CREATE TABLE` 里。
- **每个字段必须带 `COMMENT`**，表本身也要有 `COMMENT`；缺注释不允许入库。
- 公共字段必须包含 `id / created_date / created_by / updated_date / updated_by / deleted`，与 `BaseEntity` 对齐。
- 索引命名：唯一索引 `uk_{表缩写}_{字段}`，普通索引 `idx_{表缩写}_{字段}`。
- 全量建表脚本放 `data_table/schema/`（可带 `DROP TABLE IF EXISTS`）；增量变更放 `data_table/migration/{年}/{月}/{日}/` 下，按创建日期分目录，文件名用动词开头的描述性命名（如 `add_user_phone.sql`），文件开头必须有中英文双语注释块说明脚本作用，只允许 `ALTER` / 新建表，禁止 `DROP TABLE`。
- 改表时**全量脚本和 migration 都要同步改**，保证新环境初始化出来就是最新结构。

---

## 下一步（建议顺序）

### 1. 前端阶段 5：登录页 + 路由守卫 + 联调（当前最优先）

1. 建目录 `src/api`、`src/router`、`src/views`、`src/stores`、`src/utils`。
2. 登录页、注册页、首页。
3. 路由守卫 + pinia 存 token + axios 拦截器（自动带 token、401 跳登录）。
4. 全链路联调：注册 → 登录 → `/me` → 刷新保持登录 → 登出。

**验收：** 注册 → 登录 → 首页 → 刷新保持登录 → 登出。后端接口前缀 `/ghost`，Vite 代理 `/ghost` → `http://localhost:8080`。

### 2. 补全根目录

- 添加 `README.md`（JDK 17、Maven、Node、MySQL 版本与启动步骤）
- 把 `mvnw` / `mvnw.cmd` / `.mvn/` / `.gitattributes` 加入版本控制

### 3. 阶段 6（后续）

- 单元/集成测试（`UserService` 登录/注册）
- 登出、Token 刷新、`@PreAuthorize` 权限注解
- 日志、Flyway 迁移

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
- 修改代码时**不改动无关文件**；保持现有包名 `com.ghost.frame` 与按层分包结构。
- 密钥、数据库密码不进仓库；本地配置用 `application-local.yml` 或环境变量。
- 问答与请求权限用中文；代码注释保持简洁，仅解释非显而易见的逻辑。
- 当前最优先任务：**前端阶段 5**（登录页 + 路由守卫 + 联调），后端阶段 2/3 已收尾完成。

---

## 参考：联调拓扑

```text
浏览器 (localhost:5173)
    ↓ Vite 代理 /ghost
Spring Boot (localhost:8080)
    ↓ JWT 过滤器
modules (controller → service → mapper)
    ↓ MyBatis-Plus
MySQL (本机，库名 silly_ghost_frame)
```
