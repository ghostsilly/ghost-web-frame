# ghost-web-frame

全栈基础框架项目。后端 Spring Boot，前端 Vue 3（Vite），数据库 MySQL，鉴权 JWT。不使用 Docker。

---

## 技术栈

| 层 | 选型 | 状态 |
| --- | --- | --- |
| 后端 | Java 17 + Spring Boot 3.5.16 + Maven | 已接入 Web / Validation / Security |
| 前端 | Vue 3 + Vite | 登录 / 注册 / 首页、axios、Pinia、路由守卫已落地 |
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
    ├── package.json / package-lock.json              # vue 3.5 / vite 8 / vue-router 4.6 / pinia 4.1 / axios 1.20
    ├── vite.config.js                                # 代理 /ghost → localhost:8080，port 5173
    ├── index.html
    ├── public/favicon.svg、public/icons.svg
    └── src/
        ├── main.js                                   # 已 use(pinia)、use(router)
        ├── App.vue                                   # <router-view />
        ├── style.css
        ├── api/request.js                            # axios + Bearer + 解包 Response + 401
        ├── api/auth.js                               # login / register / getMe
        ├── router/index.js                           # / /login /register + beforeEach
        ├── stores/user.js                            # token 持久化、login / register / fetchMe / logout
        ├── views/HomeView.vue                        # 展示当前用户，刷新时 fetchMe
        └── views/login/LoginView.vue、RegisterView.vue
```

**已删除（Vite 默认模板）：** `src/components/HelloWorld.vue`、`src/assets/{hero.png, vite.svg, vue.svg}`。

根目录已有 `README.md`。token 存在 `localStorage`（键 `ghost-token`），不需要单独的 `src/utils/`。

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
- 登出接口、Token 刷新、权限注解（`@PreAuthorize`）
- 日志、Flyway

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
| `src/api/request.js` | `baseURL: '/ghost'`；请求带 `Authorization: Bearer`；业务码 200 解包 `data`；401 清 `ghost-token` 并跳登录 |
| `src/api/auth.js` | `login` / `register` / `getMe` |
| `src/stores/user.js` | token 写入 `localStorage`；`login`、`register` 写入用户信息；`fetchMe` 合并 `/me`；`logout` 只清本地 |
| `src/router/index.js` | `/`、`/login`、`/register`；`beforeEach`：无 token 去登录并带 `redirect`；已登录访问登录/注册回首页 |
| `src/views/login/LoginView.vue` | 登录表单，成功后按 `redirect` 回跳 |
| `src/views/login/RegisterView.vue` | 注册表单，校验与 `RegisterRequestDTO` 对齐，成功后自动登录 |
| `src/views/HomeView.vue` | 展示昵称、用户名、角色；刷新且无 `userInfo` 时调用 `fetchMe`；登出后回 `/login` |

人工验收（注册 → 登录 → 首页 → 刷新仍显示用户 → 登出后再进首页被拦住）尚未在浏览器走完。

---

## 开发阶段与进度

| 阶段 | 内容 | 状态 |
| --- | --- | --- |
| 0 | 仓库结构、根 `.gitignore`、git 仓库 | 已完成（mvnw 等尚未入库） |
| 1 | 后端空壳：统一响应、异常 | 已完成（早期 `/api/health` 已随前缀调整移除） |
| 2 | MySQL + MyBatis-Plus + `user` 表 | 已完成（含建表 SQL `data_table/schema/user.sql`） |
| 3 | JWT 鉴权：注册 / 登录 / `/me` | 已完成（含 CORS 放行 `localhost:5173`） |
| 4 | 前端 Vite 壳 + axios 代理 | 已完成（依赖、代理、目录骨架均就绪） |
| 5 | 前端登录页 + 路由守卫 + 联调 | 代码已落地；全链路人工验收未完成 |
| 6 | 登出与刷新 Token、权限注解、测试、日志、Flyway | 未开始 |

---

## 已知问题与约束（改动前必读）

1. **`${api}` 占位符必须解析成功**：`LoginRegisterController` 使用 `@RequestMapping("${api}/auth")`，**API 统一前缀为 `/ghost`**。`application.yml` 与 `application-local.yml` **都已定义** `api: /ghost`；若改动前缀，两处必须同步，否则启动报 `Could not resolve placeholder 'api'` 或前后端不一致。
2. **`UserMapper.xml` 扫描已配置**：文件在 `resources/com/ghost/frame/modules/mapper/` 下，`application.yml` 已加 `mybatis-plus.mapper-locations: classpath*:com.ghost.frame.modules.mapper/*.xml`，无需再改。
3. **数据库名实际为 `silly_ghost_frame`**（不是早期建议的 `ghost_frame`）。本机需手动建库 + 建 `user` 表（`data_table/schema/user.sql`），否则注册/登录必失败。
4. **JWT 有效期 12 小时**（`jwt.expiration-ms: 43200000`），Secret 从环境变量 `jwt.jwt-secret` 注入，不入库。
5. **阶段 5 代码仍在工作区**（截至 2026-10-24）：前端业务文件已有实现，但尚未作为一次提交入库。`mvnw` / `mvnw.cmd` / `.mvn/` 以及根目录、后端的 `.gitattributes` 仍未 `git add`。不要把 `application-local.yml` 提交进去。
6. **`.idea/` 已被忽略**：根 `.gitignore` 第 11 行 `.idea/` 同时覆盖了 `ghost-frame-backend/.idea/`，`git ls-files` 无 IDE 配置污染，无需处理。
7. **前端可以打开页面**：`npm run dev` 默认端口 5173。未登录访问 `/` 会到 `/login?redirect=/`。后端需在 8080 启动，否则登录、注册、`fetchMe` 会失败。

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

### 1. 收尾阶段 5 验收

在 5173 与 8080 都已启动时走一遍：注册 → 登录进首页并能看到昵称 → 刷新后仍显示同一用户 → 登出后再打开 `/` 被拦到登录页。通过后再进入阶段 6。

### 2. 阶段 6：鉴权补齐与可重复构建

做到下面这些即算本阶段完成：

1. **登出**：`POST /ghost/auth/logout`。当前 JWT 无状态，接口只需校验已登录并返回成功；前端 `logout` 先调该接口，无论成败都清本地 token 并回 `/login`。
2. **刷新 Token**：登录响应增加 `refreshToken`（有效期长于 12 小时的 access token）。`POST /ghost/auth/refresh` 用 refresh token 换新的 access token。axios 在 access token 过期（401）时尝试刷新一次，失败再清 token 跳登录。refresh token 与 access token 一样不入库明文日志。
3. **权限注解**：开启方法安全。`/me` 要求已认证；预留一个仅 `ADMIN` 可访问的示例接口（或在现有用户接口上按 `UserRole` 限制），未授权返回统一 `Response`，前端不必做管理页。
4. **测试**：`UserService` 注册（重复用户名失败）、登录（密码错误失败、成功签发 token）有测试覆盖。可用测试库或 mock Mapper，不依赖本机 `application-local.yml` 里的密码。
5. **日志**：登录成功与失败打业务日志，不打印密码和完整 token。
6. **Flyway**：用 Flyway 执行 `data_table/schema/user.sql` 的等价迁移，新环境启动即可建表。全量脚本规范不变：之后改表仍要同步 `schema/` 与 `migration/`。

**验收：** 登录后等到 access token 失效仍能通过 refresh 继续调用 `/me`；登出后旧 access token 访问 `/me` 失败（若尚未做服务端吊销，则以前端清 token、再请求不再带 Authorization 为准，并在代码注释里写明 JWT 在过期前仍然有效）；非管理员访问管理员示例接口得到统一错误体；`mvn test` 通过。

### 3. 仓库补齐（可与阶段 6 并行）

- 根目录 `README.md` 已有启动说明
- 把 `mvnw` / `mvnw.cmd` / `.mvn/` / `.gitattributes` 加入版本控制

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
- 当前最优先任务：人工走完阶段 5 验收，然后做**阶段 6**（登出、刷新 Token、`@PreAuthorize`、`UserService` 测试、登录日志、Flyway）。阶段 6 的后端部分仍由用户手写。
- 进度基线核对时间：2026-10-24。阶段 5 的页面、store、拦截器和路由守卫已在工作区，尚未提交。

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
