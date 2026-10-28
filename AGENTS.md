# ghost-web-frame

全栈基础框架项目。后端 Spring Boot，前端 Vue 3（Vite），数据库 MySQL，鉴权 JWT。不使用 Docker。

---

## 技术栈

| 层 | 选型 | 状态 |
| --- | --- | --- |
| 后端 | Java 17 + Spring Boot 3.5.16 + Maven | 已接入 Web / Validation / Security |
| 前端 | Vue 3 + Vite | 登录 / 注册 / 首页、axios、Pinia、路由守卫已落地 |
| 数据库 | MySQL + MyBatis-Plus 3.5.9 | 已接入，实体与 Mapper 已写，建表 SQL 已落地 |
| 鉴权 | JWT（jjwt 0.12.6）+ Spring Security | 已接入过滤器与登录态；接口只区分是否已登录 |
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
│       │   │   │   ├── MyMetaObjectHandler.java      # 自动填充 create/update 字段
│       │   │   │   └── throwable/
│       │   │   │       ├── GlobalExceptionHandler.java
│       │   │   │       ├── enums/ErrorCode.java
│       │   │   │       └── exception/BusinessException.java
│       │   │   ├── config/
│       │   │   │   └── OpenApiConfig.java            # knife4j / springdoc
│       │   │   ├── security/
│       │   │   │   ├── SecurityConfig.java           # 过滤链 + CORS
│       │   │   │   ├── JwtAuthenticationFilter.java  # 只接受 access token
│       │   │   │   └── JwtUtil.java                  # access / refresh，用 tokenType 区分
│       │   │   └── modules/                          # 按层分包（见下方说明）
│       │   │       ├── controller/
│       │   │       │   ├── BaseController.java       # getUserId() 取当前登录用户
│       │   │       │   └── LoginRegisterController.java  # /ghost/auth/login、/register、/me、/logout、/refresh
│       │   │       ├── service/ (+ impl/)            # UserService / UserServiceImpl
│       │   │       ├── entity/                      # User、BaseEntity
│       │   │       ├── mapper/                       # UserMapper
│       │   │       ├── dto/                          # LoginRequestDTO、RegisterRequestDTO、RefreshRequestDTO
│       │   │       ├── vo/                           # LoginRequestVO（含 token、refreshToken）
│       │   │       └── enums/                      # UserRole、UserStatus、DeleteFlag
│       │   └── resources/
│       │       ├── application.yml                 # 默认配置，profile=local，含 api: /ghost、Flyway baseline
│       │       ├── application-local.yml            # 本机私有配置（不入库）
│       │       ├── application-local.yml.example
│       │       ├── config/application-knife4j.yml
│       │       ├── db/migration/V1__create_user.sql  # 与 schema/user.sql 等价；文件名仍是单下划线
│       │       └── com/ghost/frame/modules/mapper/UserMapper.xml   # 已配 mapper-locations
│       └── test/java/com/ghost/frame/
│           ├── GhostFrameBackendApplicationTests.java
│           └── modules/UserServiceImplTest.java    # mock Mapper，覆盖注册重复与登录成败
└── ghost-frame-frontend/
    ├── package.json / package-lock.json              # vue 3.5 / vite 8 / vue-router 4.6 / pinia 4.1 / axios 1.20
    ├── vite.config.js                                # 代理 /ghost → localhost:8080，port 5173
    ├── index.html
    ├── public/favicon.svg、public/icons.svg
    └── src/
        ├── main.js                                   # 已 use(pinia)、use(router)
        ├── App.vue                                   # <router-view />
        ├── style.css
        ├── api/request.js                            # axios + Bearer + 解包 Response；access 过期或 401 时刷新
        ├── api/auth.js                               # login / register / getMe / logout
        ├── components/PasswordInput.vue              # 密码框与眼睛图标显隐
        ├── router/index.js                           # / /login /register + beforeEach
        ├── stores/user.js                            # token 持久化、login / register / fetchMe / logout
        ├── views/HomeView.vue                        # 展示当前用户；无用户信息或 access 过期时 fetchMe
        └── views/login/LoginView.vue、RegisterView.vue
```

**已删除（Vite 默认模板）：** `src/components/HelloWorld.vue`、`src/assets/{hero.png, vite.svg, vue.svg}`。

根目录已有 `README.md`。access token 键名 `ghost-token`，refresh token 键名 `ghost-refresh-token`，都在 `localStorage`。不需要单独的 `src/utils/`。

---

## 包结构约定（实际落地）

早期计划按业务模块分包（`modules/health/`、`modules/auth/`、`modules/user/`），实际改为**按层分包**：

```text
com.ghost.frame
├── common/          # Response、MyMetaObjectHandler、全局异常
│   └── throwable/   # GlobalExceptionHandler
│       ├── enums/      # ErrorCode
│       └── exception/  # BusinessException
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
- `POST /ghost/auth/register`、`POST /ghost/auth/login`（BCrypt 密码，返回 access token 与 refresh token）
- `POST /ghost/auth/refresh`（匿名可访问；只接受 `tokenType=refresh` 的票，换新的 access token，原 refresh token 原样返回）
- `JwtAuthenticationFilter` 只把 `tokenType=access` 的票写入登录态
- `GET /ghost/auth/me`（当前登录用户，经 `BaseController.getUserId()` 从 `SecurityContextHolder` 取 id）
- `POST /ghost/auth/logout`（需已登录；JWT 无状态，接口只确认登录并返回成功，不吊销 token）
- CORS 已配置（`SecurityConfig.corsConfigurationSource` 放行 `http://localhost:5173`）
- knife4j 接口文档（已强制覆盖 springdoc 到 2.7.0 兼容 Spring Framework 6.2.x）
- 建表 SQL 已落地：`data_table/schema/user.sql`
- 登录成功 / 失败业务日志在 `UserServiceImpl.login`（不打印密码和 token）
- `UserServiceImplTest`：mock `UserMapper` / `PasswordEncoder` / `JwtUtil`，覆盖重复用户名注册、密码错误、登录成功签发 token
- Flyway：`flyway-core`、`flyway-mysql`；`spring.flyway.baseline-on-migrate: true`、`baseline-version: 1`。本机已有库的 `flyway_schema_history` 为 `BASELINE` 版本 1，未重跑建表
- `application-local.yml` 已落地，`application.yml` 通过 `${mysql.username}` / `${mysql.password}` / `${jwt.jwt-secret}` 注入敏感值
- 未登录访问受保护接口走 `SecurityConfig.authenticationEntryPoint`，返回 401 统一体

### 尚未实现

- 接口权限角色，以及可动态修改的接口权限归属。`UserRole` 只区分账号类型，不参与接口鉴权
- Flyway 迁移文件名仍是 `V1__create_user.sql`（单下划线）。版本脚本必须是 `V1__create_user.sql`，否则空库启动不会执行这份建表 SQL

---

## 前端现状（ghost-frame-frontend）

**已就绪（可用）：**

- `package.json` 依赖齐全：`vue ^3.5.44`、`vue-router ^4.6.4`、`pinia ^4.1.0`、`axios ^1.20.0`；devDeps `vite ^8.3.4`、`@vitejs/plugin-vue ^6.0.10`
- `vite.config.js` 代理已配：`/ghost` → `http://localhost:8080`，`host: true`、`port: 5173`
- 目录骨架已建：`src/api`、`src/router`、`src/stores`、`src/views`（含 `views/login/`）
- Vite 默认模板已清理：删除 `components/HelloWorld.vue`、`assets/{hero.png, vite.svg, vue.svg}`

**已实现：**

| 文件 | 当前状态 |
| --- | --- |
| `src/main.js` | `createApp(App).use(pinia).use(router).mount('#app')`，Pinia 在 Router 之前 |
| `src/App.vue` | 模板为 `<router-view />` |
| `src/api/request.js` | `baseURL: '/ghost'`；请求带 `Authorization: Bearer`；业务码 200 解包 `data`。本地 access token 的 `exp` 已过、或接口返回 401，且存在 `ghost-refresh-token` 时，先调 `POST /auth/refresh` 再重放原请求。`skipAuthRedirect` 只禁止跳登录页 |
| `src/api/auth.js` | `login` / `register` / `getMe` / `logout` |
| `src/stores/user.js` | access token 与 refresh token 写入 `localStorage`；`login`、`register` 写入用户信息；`fetchMe` 合并 `/me`；`logout` 先调登出接口，无论成败都清本地 |
| `src/router/index.js` | `/`、`/login`、`/register`；`beforeEach`：无 token 去登录并带 `redirect`；已登录访问登录/注册回首页 |
| `src/components/PasswordInput.vue` | 密码输入框。`toggleVisible` 在明文和圆点之间切换，按钮为眼睛图标 |
| `src/views/login/LoginView.vue` | 登录表单，密码框使用 `PasswordInput`。用户名框回车聚焦密码框。校验和登录失败显示在对应输入框下方。成功后按 `redirect` 回跳 |
| `src/views/login/RegisterView.vue` | 注册表单。字段顺序为昵称、用户名、密码、确认密码。昵称提示「请输入用户昵称」，用户名提示「请输入用户名」。两个密码框使用 `PasswordInput`。校验与 `RegisterRequestDTO` 对齐。成功后自动登录 |
| `src/views/HomeView.vue` | 展示昵称、用户名、角色。无 `userInfo` 或 access token 已过期时调用 `fetchMe`；并按 `exp` 定时再拉一次 `/me`，从而触发刷新。登出后回 `/login` |

阶段 5 人工验收已通过。登录和注册响应里的 `refreshToken` 会写入 `ghost-refresh-token`；没有这张票时前端不会发起刷新。

---

## 开发阶段与进度

| 阶段 | 内容 | 状态 |
| --- | --- | --- |
| 0 | 仓库结构、根 `.gitignore`、git 仓库 | 已完成（mvnw 等尚未入库） |
| 1 | 后端空壳：统一响应、异常 | 已完成（早期 `/api/health` 已随前缀调整移除） |
| 2 | MySQL + MyBatis-Plus + `user` 表 | 已完成（含建表 SQL `data_table/schema/user.sql`） |
| 3 | JWT 鉴权：注册 / 登录 / `/me` | 已完成（含 CORS 放行 `localhost:5173`） |
| 4 | 前端 Vite 壳 + axios 代理 | 已完成（依赖、代理、目录骨架均就绪） |
| 5 | 前端登录页 + 路由守卫 + 联调 | 已完成（2026-10-24 人工验收通过） |
| 6 | 登出与刷新 Token、测试、日志、Flyway | 已落地（2026-10-28）。登出、刷新 Token、登录日志、`UserServiceImplTest`、Flyway 依赖与 baseline 已有。本机 history 为 BASELINE。迁移文件名待改成双下划线。按账号角色做的方法级权限已移除 |

---

## 已知问题与约束（改动前必读）

1. **`${api}` 占位符必须解析成功**：`LoginRegisterController` 使用 `@RequestMapping("${api}/auth")`，**API 统一前缀为 `/ghost`**。`application.yml` 与 `application-local.yml` **都已定义** `api: /ghost`；若改动前缀，两处必须同步，否则启动报 `Could not resolve placeholder 'api'` 或前后端不一致。
2. **`UserMapper.xml` 扫描已配置**：文件在 `resources/com/ghost/frame/modules/mapper/` 下，`application.yml` 已加 `mybatis-plus.mapper-locations: classpath*:com.ghost.frame.modules.mapper/*.xml`，无需再改。
3. **数据库名实际为 `silly_ghost_frame`**（不是早期建议的 `ghost_frame`）。本机库已有 `user` 表，Flyway 只做了 baseline。空库要能自动建表，迁移文件名须为 `V1__create_user.sql`。全量脚本仍是 `data_table/schema/user.sql`。
4. **access token 有效期 12 小时**（`jwt.expiration-ms: 43200000`），**refresh token 有效期 7 天**（`jwt.refresh-expiration-ms: 604800000`）。Secret 从环境变量 `jwt.jwt-secret` 注入，不入库。两种票用 claim `tokenType` 区分。
5. **阶段 5 代码仍在工作区**（截至 2026-10-24）：前端业务文件已有实现，但尚未作为一次提交入库。`mvnw` / `mvnw.cmd` / `.mvn/` 以及根目录、后端的 `.gitattributes` 仍未 `git add`。不要把 `application-local.yml` 提交进去。
6. **`.idea/` 已被忽略**：根 `.gitignore` 第 11 行 `.idea/` 同时覆盖了 `ghost-frame-backend/.idea/`，`git ls-files` 无 IDE 配置污染，无需处理。
7. **前端可以打开页面**：`npm run dev` 默认端口 5173。未登录访问 `/` 会到 `/login?redirect=/`。后端需在 8080 启动，否则登录、注册、`fetchMe` 会失败。
8. **`UserRole` 只区分账号类型**，不参与接口鉴权。现有接口只区分未登录和已登录。不要再用 `@PreAuthorize` 或 `hasRole` 把 `USER` / `ADMIN` / `SUPER_ADMIN` 绑到接口上。
9. **接口权限以后单独做**：新增一套权限角色，用来决定能不能调用接口，并支持在系统里动态修改接口归属。账号角色和权限角色分开。
10. **`@RequestMapping` 不要在 `${api}` 前再加斜杠**：`${api}` 已是 `/ghost`，写成 `/ ${api}` 会拼出双斜杠。正确写法是 `@RequestMapping("${api}/auth")`。
11. **公开注册固定为 `UserRole.USER`**。`RegisterRequestDTO` 里不要加 `role`，避免调用方自己指定账号类型。

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

### 1. 阶段 6：鉴权补齐与可重复构建

做到下面这些即算本阶段完成：

1. **登出**：已完成。`POST /ghost/auth/logout` 校验已登录并返回成功。前端先调该接口，无论成败都清本地 token 并回 `/login`。JWT 过期前仍然有效。
2. **刷新 Token**：已完成。登录和注册返回 `token` 与 `refreshToken`。`POST /ghost/auth/refresh` 放行，用 refresh token 换新的 access token。前端在 access token 过期或 401 时先刷新再重放。refresh token 不入库、不打进明文日志。尚未做服务端吊销。
3. **测试**：已完成。`UserServiceImplTest` 用 Mockito 覆盖重复用户名注册、密码错误、登录成功签发 token，不读 `application-local.yml`。
4. **日志**：已完成。`UserServiceImpl.login` 记成功与失败，不打印密码和完整 token。
5. **Flyway**：依赖与 `baseline-on-migrate` 已完成。本机已有库记为版本 1 的 `BASELINE`。`src/main/resources/db/migration/V1__create_user.sql` 还是单下划线，改成 `V1__create_user.sql` 后空库才会执行。之后改表仍要同步 `schema/` 与 `data_table/migration/`。

**验收：** 登录后等到 access token 失效仍能通过 refresh 继续调用 `/me`；登出后旧 access token 访问 `/me` 失败（若尚未做服务端吊销，则以前端清 token、再请求不再带 Authorization 为准，并在代码注释里写明 JWT 在过期前仍然有效）；`mvn test` 通过。

### 2. 仓库补齐（可与阶段 6 并行）

- 根目录 `README.md` 已有启动说明
- 把 `mvnw` / `mvnw.cmd` / `.mvn/` / `.gitattributes` 加入版本控制

### 3. 接口权限（后续，不在阶段 6）

`UserRole` 只表示账号类型。接口能不能访问，以后用另一套权限角色，并允许在系统里修改接口归属。在那套能力落地前，新接口沿用现在的规则：公开接口 `permitAll`，其余接口要求已登录。

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

- **后端由用户手动开发。** 对 `ghost-frame-backend/` 只给步骤、接口约定和示例片段，不直接改 Java、XML、SQL 或 `pom.xml`，除非用户在当次对话里明确要求改某个后端文件。
- **前端可以由 AI 写。** 可以改 `ghost-frame-frontend/`。每次交付生成或修改过的前端代码时，用中文说明每个改动文件的作用，以及关键函数、请求和路由各自做什么。
- 修改代码时**不改动无关文件**；保持现有包名 `com.ghost.frame` 与按层分包结构。
- 密钥、数据库密码不进仓库；本地配置用 `application-local.yml` 或环境变量。
- 问答与请求权限用中文；代码注释保持简洁，仅解释非显而易见的逻辑。
- 当前最优先任务：把 Flyway 脚本改名为 `V1__create_user.sql`。阶段 6 其余项已由用户写完。不要把 `UserRole` 写成接口权限。
- 进度基线核对时间：2026-10-28。阶段 5 已验收。阶段 6 的登出、刷新 Token、登录日志、`UserServiceImplTest`、Flyway baseline 已落地。本机 `flyway_schema_history` 为 `BASELINE` 版本 1。按账号角色做的方法级权限已移除。注册页字段顺序为昵称、用户名。登录和注册的密码框使用眼睛图标显隐。

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
