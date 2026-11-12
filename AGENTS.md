# ghost-web-frame

全栈基础框架项目。后端 Spring Boot，前端 Vue 3（Vite），数据库 MySQL，鉴权 JWT。不使用 Docker。

---

## 技术栈

| 层 | 选型 | 状态 |
| --- | --- | --- |
| 后端 | Java 17 + Spring Boot 3.5.16 + Maven | 已接入 Web / Validation / Security |
| 前端 | Vue 3 + Vite | 登录 / 注册 / 首页、axios、Pinia、路由守卫已落地 |
| 数据库 | MySQL + MyBatis-Plus 3.5.9 | 已接入，`user`、`api_resource` 两张表 |
| 鉴权 | JWT（jjwt 0.12.6）+ Spring Security | 已接入过滤器与登录态；接口只区分是否已登录 |
| 接口文档 | knife4j 4.5.0 + springdoc-openapi 2.7.0 | 已接入 |
| 工具库 | Hutool 5.8.47、Lombok | 已引入 |
| 容器化 | 无 | 按约定不加 Docker |

---

## 当前目录结构

```text
ghost-web-frame/
├── AGENTS.md
├── README.md
├── .gitattributes / .gitignore
├── ghost-frame-backend/
│   ├── pom.xml
│   ├── mvnw / mvnw.cmd / .mvn/（已入库）
│   ├── data_table/                        # 表结构脚本（规范见该目录 README.md）
│   │   ├── README.md
│   │   ├── schema/
│   │   │   ├── user.sql                   # user 表全量建表
│   │   │   └── api_resource.sql           # api_resource 表全量建表
│   │   └── migration/
│   │       └── 2026/11/06/user_add_remark.sql   # 首个增量脚本实例
│   └── src/
│       ├── main/java/com/ghost/frame/
│       │   ├── GhostFrameBackendApplication.java   # 无 @MapperScan，靠 @Mapper 自动扫描
│       │   ├── common/                    # 框架级横切能力
│       │   │   ├── Response.java
│       │   │   ├── MyMetaObjectHandler.java
│       │   │   └── throwable/
│       │   │       ├── GlobalExceptionHandler.java
│       │   │       ├── enums/ErrorCode.java
│       │   │       └── exception/BusinessException.java
│       │   ├── config/
│       │   │   └── OpenApiConfig.java     # knife4j / springdoc
│       │   ├── security/
│       │   │   ├── SecurityConfig.java    # 过滤链 + CORS + 401 统一体
│       │   │   ├── JwtAuthenticationFilter.java   # 只接受 access token
│       │   │   └── JwtUtil.java           # access / refresh，用 tokenType 区分
│       │   ├── utils/
│       │   │   └── ApiResourceScannerUtil.java    # 只读扫描容器路由，不持久化
│       │   └── modules/                   # 按业务模块分包
│       │       ├── common/                # 模块间共享
│       │       │   ├── controller/BaseController.java   # getUserId()
│       │       │   ├── entity/BaseEntity.java           # 公共字段 + deleted + remark
│       │       │   └── enums/DeleteFlag.java
│       │       ├── user/                  # 用户与鉴权
│       │       │   ├── controller/LoginRegisterController.java
│       │       │   ├── service/UserService.java (+ impl/UserServiceImpl.java)
│       │       │   ├── entity/User.java
│       │       │   ├── mapper/UserMapper.java           # @Mapper
│       │       │   ├── dto/{LoginRequestDTO, RegisterRequestDTO, RefreshRequestDTO}.java
│       │       │   ├── vo/LoginRequestVO.java
│       │       │   └── enums/{UserRole, UserStatus}.java
│       │       └── api/                   # 接口资源池（阶段 7 第一块）
│       │           ├── controller/ApiResourceController.java
│       │           ├── service/ApiResourceService.java (+ impl/ApiResourceServiceImpl.java)
│       │           ├── entity/ApiResource.java
│       │           ├── mapper/ApiResourceMapper.java     # @Mapper
│       │           ├── dto/ApiResourceApplyDTO.java
│       │           ├── vo/{ApiResourceDiffVO, ApiResourceDiffItemVO,
│       │           │       ApiResourceApplyResultVO, FieldChangeVO}.java
│       │           └── enums/{ApiResourceStatus, ApiResourceDiffType}.java
│       ├── main/resources/
│       │   ├── application.yml            # profile=local、api: /ghost、端口 10583；mapper-locations 仍用点号路径
│       │   ├── application-local.yml      # 本机私有（不入库）
│       │   ├── application-local.yml.example
│       │   ├── config/application-knife4j.yml
│       │   └── com/ghost/frame/modules/user/mapper/UserMapper.xml  # 已与接口同包
│       └── test/java/com/ghost/frame/
│           ├── GhostFrameBackendApplicationTest.java   # 注意文件名无 s
│           ├── ApiResourceScannerTest.java             # @SpringBootTest，需连库
│           └── modules/UserServiceImplTest.java        # 纯 Mockito，不连库
└── ghost-frame-frontend/
    ├── package.json / package-lock.json   # vue 3.5 / vite 8 / vue-router 4.6 / pinia 4.1 / axios 1.20
    ├── vite.config.js                     # 代理 /ghost → localhost:10583，port 5173
    ├── index.html
    ├── public/favicon.svg、public/icons.svg
    └── src/
        ├── main.js / App.vue / style.css
        ├── api/request.js                 # axios + Bearer + 解包；401 或过期时先刷新再重放
        ├── api/auth.js                    # login / register / getMe / logout
        ├── components/PasswordInput.vue
        ├── router/index.js                # / /login /register + beforeEach
        ├── stores/user.js                 # token 持久化与 login / register / fetchMe / logout
        └── views/HomeView.vue、views/login/{LoginView, RegisterView}.vue
```

access token 键名 `ghost-token`，refresh token 键名 `ghost-refresh-token`，都在 `localStorage`。

---

## 包结构约定（2026-11-09 起）

**已从「按层分包」重构为「按业务模块分包」。** 早期在 `modules/` 下直接放 `controller/`、`service/`、`entity/` 等层目录，现改为先按业务模块分，模块内再按层分：

```text
com.ghost.frame
├── common/      # 框架级横切：Response、MyMetaObjectHandler、全局异常
│   └── throwable/{GlobalExceptionHandler, enums/ErrorCode, exception/BusinessException}
├── config/      # OpenApiConfig（后续放 WebMvc、Jackson）
├── security/    # SecurityConfig、JwtAuthenticationFilter、JwtUtil
├── utils/       # 与 Spring 容器协作的工具组件
└── modules/
    ├── common/  # 跨模块共享的基类与枚举
    │   ├── controller/BaseController    # getUserId() 取当前登录用户
    │   ├── entity/BaseEntity            # createdDate/By、updatedDate/By、deleted、remark
    │   └── enums/DeleteFlag
    ├── user/    # {controller, service(+impl), entity, mapper, dto, vo, enums}
    └── api/     # {controller, service(+impl), entity, mapper, dto, vo, enums}
```

新增业务时**在 `modules/` 下新建模块子包**，模块内按层组织，不要把类直接散落在 `modules/` 下。

**注意有两个 `common` 包，职责不同，不要混用：**

| 包 | 放什么 |
| --- | --- |
| `com.ghost.frame.common` | 框架级：统一响应体、异常体系、自动填充 |
| `com.ghost.frame.modules.common` | 业务级：实体基类、控制器基类、业务枚举 |

API 统一前缀：`/ghost/**`，由 `${api}` 配置注入。

---

## Mapper 扫描方式（2026-11-09 起）

启动类**不再有 `@MapperScan`**，改为依赖 MyBatis-Plus 自动配置的 `AutoConfiguredMapperScannerRegistrar`，从 `com.ghost.frame` 递归扫描**带 `@Mapper` 注解的接口**。

- 每个 Mapper 接口**必须标 `@Mapper`**，否则不会被注册成 bean
- 不要再加回不带 `annotationClass` 的 `@MapperScan("com.ghost.frame.modules")`——那会把 `UserService`、`ApiResourceService` 这类业务接口也注册成 `MapperFactoryBean`，导致注入崩溃
- 若确实要用 `@MapperScan`，必须写成 `@MapperScan(basePackages = "com.ghost.frame.modules", annotationClass = Mapper.class)`

**背景**：重构后 Mapper 移到了 `modules.user.mapper` 与 `modules.api.mapper`，而启动类仍写着旧路径 `@MapperScan("com.ghost.frame.modules.mapper")`，扫描落空且会屏蔽 `@Mapper` 自动扫描，启动报 `No qualifying bean of type ApiResourceMapper`。已按上述方式修复。

---

## 后端现状

### 已完成

**框架与鉴权**

- 统一响应体 `Response<T>` + `GlobalExceptionHandler` + `BusinessException` + `ErrorCode`
- Spring Security 过滤链 + `JwtUtil` + `JwtAuthenticationFilter`（Bearer Token 解析）
- `POST /ghost/auth/register`、`POST /ghost/auth/login`（BCrypt，返回 access + refresh token）
- `POST /ghost/auth/refresh`（匿名可访问；只接受 `tokenType=refresh`，换新的 access token）
- `JwtAuthenticationFilter` 只把 `tokenType=access` 的票写入登录态
- `GET /ghost/auth/me`（经 `BaseController.getUserId()` 取 id，再走 `UserMapper.getUserInformation`）
- `POST /ghost/auth/logout`（只确认登录态，**不吊销 token**）
- CORS 放行 `http://localhost:5173`；未登录走 `authenticationEntryPoint` 返回 401 统一体
- knife4j 接口文档（springdoc 强制覆盖到 2.7.0 以兼容 Spring Framework 6.2.x）

**数据层**

- MySQL + MyBatis-Plus（逻辑删除 `deleted`、下划线转驼峰、`id-type: auto`）
- `BaseEntity` 承载公共字段：`createdDate` / `createdBy` / `updatedDate` / `updatedBy` / `deleted` / `remark`。`deleted` 与 `remark` 已从 `User` 上移到此基类
- `MyMetaObjectHandler` 自动填充创建/更新的时间与操作人
- 表结构只由 `data_table/` 手工维护（全量 `schema/`、增量 `migration/`）。不使用 Flyway，启动不自动建表或改表
- `UserMapper.xml` 已放到 `resources/com/ghost/frame/modules/user/mapper/`，与 Mapper 接口同包

**接口资源池（阶段 7 第一块，2026-11-09）**

三段式流程，**扫描只读、入库需人工确认**：

| 接口 | 作用 |
| --- | --- |
| `POST /ghost/api-resource/scan-diff` | 扫描容器路由并与表比对，返回新增/修改/删除三类差异，**不写库** |
| `POST /ghost/api-resource/apply` | 提交人工勾选的差异，**唯一写入口** |
| `GET /ghost/api-resource/list` | 查询当前表内接口清单 |

- `ApiResourceScannerUtil` 通过 `applicationContext.getBean("requestMappingHandlerMapping", ...)` 按名取 bean 读路由。必须按名取：springdoc 也注册了 `springDocHandlerMapping`，按类型注入会 `NoUniqueBeanDefinitionException`
- 只收录 `${api}` 前缀下的路由，`/v3/api-docs`、`/doc.html`、`/webjars`、`/error` 等框架端点自动排除
- `module` 取控制器类上的 `@Tag.name`（`@Tag` 是 `@Repeatable`，必须用 `getAnnotationsByType`，用 `getAnnotation` 在多个标注时会返回 null）；`summary` 取方法上的 `@Operation.summary`
- 唯一键为 `path + "|" + method`，与 `uk_ar_path_method` 一致
- 差异比对只覆盖 `module` / `summary` / `handler` / `status` 四个字段；**`remark` 是人工维护的，不参与比对也不被覆盖**（`updateById` 走 `NOT_NULL` 策略，patch 对象不 set 该字段即保留原值）
- 「删除」只置 `status=0`（`REMOVED`），**不置 `deleted`、不物理删除**——后续权限表要按 `id` 引用这张表，真删会造成孤儿引用，且管理员再也看不到失效的权限点
- `apply` 对每条重新查库校验（新增查重、更新查 id 存在性），冲突计入 `skipped` 而非抛异常，避免扫描到应用之间的并发脏写
- 存的是**路径 pattern**（如 `/ghost/user/{id}`），不是字面路径。后续权限校验必须用 `PathPatternParser` 匹配，不能用字符串相等

**测试**

- `UserServiceImplTest`：`@ExtendWith(MockitoExtension.class)`，mock `UserMapper` / `PasswordEncoder` / `JwtUtil`，覆盖重复用户名注册、密码错误、登录成功签发 token。**不连库**
- `ApiResourceScannerTest`：`@SpringBootTest`，会启动完整上下文，**需要可用的 MySQL**
- `GhostFrameBackendApplicationTest`：上下文加载测试（文件名无 `s`）

### 尚未实现

- 权限模型本体：`permission`（权限点）、`permission_role`（权限角色）、用户与权限角色的关联表，以及接口级校验过滤器
- 前端接口资源管理页面（三个 `/ghost/api-resource/**` 接口目前无 UI）
- **服务端 token 吊销**：登出不使已签发的票失效，两张票在各自过期前仍可用
- 前端工程化：无 ESLint / Prettier、无 UI 组件库、无前端单元测试

---

## 前端现状（ghost-frame-frontend）

**2026-11-09 无变化**，仍是阶段 5 验收通过的形态。

- 依赖：`vue ^3.5.44`、`vue-router ^4.6.4`、`pinia ^4.1.0`、`axios ^1.20.0`；devDeps `vite ^8.3.4`、`@vitejs/plugin-vue ^6.0.10`。scripts 只有 `dev` / `build` / `preview`
- `vite.config.js` 代理 `/ghost` → `http://localhost:10583`，`host: true`、`port: 5173`
- Vite 默认模板已清理（`HelloWorld.vue`、`assets/{hero.png, vite.svg, vue.svg}`）

| 文件 | 当前状态 |
| --- | --- |
| `src/main.js` | `createApp(App).use(pinia).use(router).mount('#app')`，Pinia 在 Router 之前 |
| `src/App.vue` | 模板为 `<router-view />` |
| `src/api/request.js` | `baseURL: '/ghost'`；带 `Authorization: Bearer`；业务码 200 解包 `data`。本地 access token 的 `exp` 已过、或接口返回 401，且存在 `ghost-refresh-token` 时，先调 `POST /auth/refresh` 再重放原请求。`skipAuthRedirect` 只禁止跳登录页 |
| `src/api/auth.js` | `login` / `register` / `getMe` / `logout` |
| `src/stores/user.js` | 两张票写入 `localStorage`；`login`、`register` 写入用户信息；`fetchMe` 合并 `/me`；`logout` 先调接口，无论成败都清本地 |
| `src/router/index.js` | `/`、`/login`、`/register`；`beforeEach`：无 token 去登录并带 `redirect`；已登录访问登录/注册回首页 |
| `src/components/PasswordInput.vue` | `toggleVisible` 在明文和圆点间切换，按钮为眼睛图标 |
| `src/views/login/LoginView.vue` | 密码框用 `PasswordInput`；用户名框回车聚焦密码框；校验与登录失败显示在对应输入框下方；成功后按 `redirect` 回跳 |
| `src/views/login/RegisterView.vue` | 字段顺序：昵称、用户名、密码、确认密码。两个密码框用 `PasswordInput`；校验与 `RegisterRequestDTO` 对齐；成功后自动登录 |
| `src/views/HomeView.vue` | 展示昵称、用户名、角色；无 `userInfo` 或 access token 已过期时调 `fetchMe`，并按 `exp` 定时再拉一次以触发刷新；登出后回 `/login` |

前端 README 仍是 Vite 默认模板内容，未定制。

---

## 开发阶段与进度

| 阶段 | 内容 | 状态 |
| --- | --- | --- |
| 0 | 仓库结构、根 `.gitignore`、git 仓库 | 已完成 |
| 1 | 后端空壳：统一响应、异常 | 已完成 |
| 2 | MySQL + MyBatis-Plus + `user` 表 | 已完成 |
| 3 | JWT 鉴权：注册 / 登录 / `/me` | 已完成 |
| 4 | 前端 Vite 壳 + axios 代理 | 已完成 |
| 5 | 前端登录页 + 路由守卫 + 联调 | 已完成（2026-10-24 验收） |
| 6 | 登出与刷新 Token、测试、日志、仓库补齐 | 已完成（2026-11-06 验收）。Token 刷新链路、`mvn test` 实测通过。表结构后改为 `data_table/` 手工维护 |
| **7a** | **按业务模块重构分包 + 接口资源池** | **进行中**。分包重构、扫描/比对/应用三接口、启动类去掉 `@MapperScan`、`UserMapper.xml` 迁到 `modules/user/mapper/` 已完成。2026-11-12 已移除 Flyway，建表改为手工执行 `data_table/` |
| 7b | 权限模型：权限点、权限角色、用户关联 | 未开始 |
| 7c | 接口级校验过滤器 + `SecurityConfig` 放行收紧 | 未开始 |
| 7d | 前端权限：拉取权限集合、路由与菜单过滤 | 未开始 |

**仓库状态（2026-11-09）：** `master` 分支，最新提交仍是 `3587ce1`。工作区**有大量未提交改动**，尚未形成新提交：

- 按业务模块移动的 Java 多为 `RM`（index 已记为 rename，工作区相对 index 还有修改）
- `UserMapper.xml` 已 staged 迁到 `resources/com/ghost/frame/modules/user/mapper/`
- 已改：启动类（去掉 `@MapperScan`）、`application.yml`（`mapper-locations` 写成了点号通配 `classpath*:com.ghost.frame.modules.**.mapper/*.xml`）、`SecurityConfig.java`、`UserServiceImplTest.java`、`README.md`、`AGENTS.md`
- 尚未跟踪：`modules/api/`、`utils/`、`data_table/schema/api_resource.sql`、`data_table/migration/`、`ApiResourceScannerTest.java`

---

## 已知问题与约束（改动前必读）

### A. 重构遗留

1. **`mapper-locations` 已改为斜杠通配** `classpath*:com/ghost/frame/modules/**/mapper/*.xml`。`UserMapper.xml` 与接口同包。

2. **不再使用 Flyway。** 空库启动不会建表。新环境需手工执行 `data_table/schema/` 下的全量脚本（`user.sql`、`api_resource.sql`）。已有库若缺列或表，执行 `data_table/migration/` 中对应增量脚本。库里残留的 `flyway_schema_history` 可手工删除，应用不再读写它。

3. **`data_table/README.md` 里的 `BaseEntity` 包路径可能仍过期。** 实际类在 `com.ghost.frame.modules.common.entity.BaseEntity`。公共字段应含 `remark`。

### B. 既有约定与隐患

5. **`${api}` 占位符必须解析成功**：控制器用 `@RequestMapping("${api}/auth")`、`@RequestMapping("${api}/api-resource")`，**统一前缀 `/ghost`**。`application.yml` 与 `application-local.yml` 都定义了 `api: /ghost`，改前缀两处必须同步。
6. **`@RequestMapping` 不要在 `${api}` 前再加斜杠**：写成 `/${api}` 会拼出双斜杠。
7. **数据库名为 `silly_ghost_frame`**（不是早期建议的 `ghost_frame`）。建表与改表只通过 `data_table/` 手工执行。
8. **access token 有效期 6 小时**（`jwt.expiration-ms: 21600000`，`application.yml` 注释写作「半天」），**refresh token 7 天**（`jwt.refresh-expiration-ms: 604800000`）。Secret 从 `application-local.yml` 的 `jwt.jwt-secret` 注入，不入库。两种票用 claim `tokenType` 区分。
9. **后端端口 10583**（`server.port`），不是 8080。`vite.config.js` 代理 target 已是 `http://localhost:10583`，两处必须一起改。前端 dev 端口 5173。
10. **不要把 `application-local.yml` 提交进去**（`.gitignore` 第 36 行 `**/application-local.yml` 已覆盖，仓库只留 `.example`）。`.idea/` 也已被根 `.gitignore` 第 11 行忽略。
11. **`UserRole` 只区分账号类型**，不参与接口鉴权。现有接口只区分未登录和已登录。**不要用 `@PreAuthorize` 或 `hasRole`** 把 `USER` / `ADMIN` / `SUPER_ADMIN` 绑到接口上（全后端已无这类注解，别加回去）。权限归属由阶段 7 的独立权限角色承担。
12. **公开注册固定为 `UserRole.USER`**。`RegisterRequestDTO` 里不要加 `role`。
13. **登出不等于失效**：JWT 无状态，登出后已签发的 access token 在过期前仍能调通受保护接口。当前验收口径是「前端清掉本地 token、后续请求不再带 Authorization」。做服务端吊销前，不要在文档或注释里声称登出已使 token 失效。
14. **`api_resource` 表不要用 `deleted` 表达接口生命周期**。唯一索引 `uk_ar_path_method` 与逻辑删除会打架：某行被置 `deleted=1` 后仍占着唯一索引，下次扫到相同 path+method 想插入就撞索引。约定是生命周期一律用 `status`（1-存在 / 0-已移除），`deleted` 仅为对齐 `BaseEntity` 而存在，扫描与应用流程都不置它为 1。
15. **`SecurityConfig` 放行规则偏松且有残留**：用的是 `/*/auth/register`、`/*/auth/login`、`/*/auth/refresh` 单星通配，会放行**任意一级前缀**下的同名路径，与「统一前缀 `/ghost`」的约定不符；另外放行列表里有一条 `/test`，代码中并无对应端点，属调试残留。做阶段 7c 时一并收紧为 `${api}` 注入或显式 `/ghost/auth/**`，并删掉 `/test`。

### C. 代码风格

16. **`UserServiceImpl` 与 `ApiResourceServiceImpl` 存在重复依赖**：两者都 `extends ServiceImpl<XxxMapper, XxxEntity>`（父类已持有 `baseMapper`），又额外 `@Autowired` 注入了同一个 Mapper。功能正常，但可直接用父类的 `getBaseMapper()` 去掉冗余字段。
17. **`@RequiredArgsConstructor` 与 `@Value` 的配合**：`ApiResourceScannerUtil` 的 `apiPrefix` 字段刻意**不加 `final`**。加了它会变成构造器参数，而 Lombok 默认不把字段上的 `@Value` 复制到构造器参数，结果占位符不解析、启动报 `Could not resolve placeholder 'api'`。
18. **`@Qualifier` 在 `@RequiredArgsConstructor` 下默认失效**：Lombok 不会把它复制到构造器参数。若要用，需在 `ghost-frame-backend/` 根加 `lombok.config`：`lombok.copyableAnnotations += org.springframework.beans.factory.annotation.Qualifier`。目前项目未加该文件，按名取 bean 的写法不依赖它。

---

## 数据库脚本规范

详见 `ghost-frame-backend/data_table/README.md`。关键约束：

- **一张表一个 `.sql` 文件**，文件名 = 表名；建表与建索引分两段写，不要内嵌
- **每个字段必须带 `COMMENT`**，表本身也要有 `COMMENT`
- 公共字段对齐 `com.ghost.frame.modules.common.entity.BaseEntity`：`id` / `created_date` / `created_by` / `updated_date` / `updated_by` / `deleted` / `remark`
- 索引命名：唯一索引 `uk_{表缩写}_{字段}`，普通索引 `idx_{表缩写}_{字段}`
- 全量脚本放 `data_table/schema/`（可带 `DROP TABLE IF EXISTS`）；增量脚本放 `data_table/migration/{年}/{月}/{日}/`，动词开头命名，开头必须有中英文双语注释块，只允许 `ALTER` / 新建表，禁止 `DROP TABLE`
- **改表时同步全量脚本，并新增一份增量脚本**。两处都要人工执行到目标库，启动不会跑它们
- **不要再引入 Flyway，也不要恢复 `src/main/resources/db/migration/`**

### 当前两张表

| 表 | 全量脚本 | 增量 | 说明 |
| --- | --- | --- | --- |
| `user` | `schema/user.sql` | `migration/2026/11/06/user_add_remark.sql` | 已有库若缺 `remark`，执行该增量 |
| `api_resource` | `schema/api_resource.sql` | 无 | 空库直接执行全量脚本 |

---

## 下一步

### 1. 表结构

空库先手工执行 `data_table/schema/user.sql` 与 `data_table/schema/api_resource.sql`。启动不会建表。`data_table/README.md` 的 `BaseEntity` 路径与公共字段表仍可能过期，改文档时对齐 `modules.common.entity.BaseEntity`（含 `remark`）。

### 2. 阶段 7b：权限模型

**先定模型再写代码**，三个决策点：

1. **权限粒度**：按接口路径，还是按功能点/权限码？路径直观但接口一改就失效；权限码多一层映射但更稳定
2. **归属关系**：`api_resource` ← `permission` ← `permission_role` ← `user_permission_role` 四层，各自表结构
3. **动态生效**：运行时改了接口归属，靠什么让服务立即感知——重启 / 缓存主动刷新 / 定时重载。这决定要不要引入缓存层

倾向**多对多**而非在 `api_resource` 上加 `permission_code` 单字段：AGENTS 要求「支持动态修改接口归属」，多对多下只是改关联行，单字段下会变成改接口自身属性，语义更别扭。

表建好后按 `data_table/` 规范写全量脚本和增量脚本，并手工执行。

### 3. 阶段 7c：接口级校验

- 在 `JwtAuthenticationFilter` 之后加过滤器，用 `PathPatternParser` 匹配 `api_resource.path`（存的是 pattern，不是字面路径）
- 收紧 `SecurityConfig` 放行规则，删掉 `/test`
- **不要**用 `@PreAuthorize` + `hasRole`

### 4. 阶段 7d：前端配合

- 接口资源管理页面：差异结果按新增/修改/删除三区展示，每区带全选框，**删除区默认不勾**（可能牵连已有权限配置）。修改区渲染「旧值 → 新值」对照。提交时拼成 `ApiResourceApplyDTO`；返回的 `skipped > 0` 说明数据已过期，提示重新扫描
- 登录后拉取权限集合，路由守卫与菜单按权限过滤

### 5. 可与上述并行的补齐项

- **服务端 token 吊销**：给 refresh token 加服务端存储或黑名单。当前鉴权闭环唯一的实质缺口
- **前端工程化**：ESLint + Prettier、UI 组件库选型、`request.js` 与 store 的单测、定制前端 README
- **通用能力**：分页封装、统一 CRUD 基类、操作日志、文件上传
- **CI**：`mvn test` + `npm run build` 自动校验
- **清理冗余**：问题 16 的重复 Mapper 注入

---

## 本机依赖

| 工具 | 用途 |
| --- | --- |
| JDK 17 | 运行 Spring Boot（本机路径 `S:\configuration\Java\jdk-17`） |
| Maven（或项目内 `mvnw`） | 构建后端（本机仓库 `S:\configuration\maven-repository`） |
| Node.js + npm | 前端 Vite |
| MySQL | 数据持久化 |
| IntelliJ IDEA 2025.3.3 | 开发 |

---

## 给 AI Agent 的协作约定

- **后端由用户手动开发。** 对 `ghost-frame-backend/` 只给步骤、接口约定和示例片段，不直接改 Java、XML、SQL 或 `pom.xml`，除非用户在当次对话里明确要求改某个后端文件
- **前端可以由 AI 写。** 可以改 `ghost-frame-frontend/`。每次交付前端代码时，用中文说明每个改动文件的作用，以及关键函数、请求和路由各自做什么
- 修改代码时**不改动无关文件**；保持包名 `com.ghost.frame` 与「按业务模块分包」结构
- 密钥、数据库密码不进仓库；本地配置用 `application-local.yml` 或环境变量
- 问答与请求权限用中文；代码注释保持简洁，仅解释非显而易见的逻辑
- **不要加回 Flyway。** 表结构只维护在 `data_table/`，由人工执行。当前优先进入阶段 7b 权限模型设计，第一步是定模型和表结构，不是直接写过滤器
- 进度基线：阶段 0–6 已验收；阶段 7a 进行中。2026-11-12 已移除 Flyway 依赖、`spring.flyway` 配置和 `db/migration` 脚本

---

## 参考：联调拓扑

```text
浏览器 (localhost:5173)
    ↓ Vite 代理 /ghost
Spring Boot (localhost:10583)
    ↓ SecurityFilterChain → JwtAuthenticationFilter
    ↓ (阶段 7c 将在此后插入接口级权限校验)
modules.{user, api} (controller → service → mapper)
    ↓ MyBatis-Plus
MySQL (本机，库名 silly_ghost_frame，表 user / api_resource)
```
