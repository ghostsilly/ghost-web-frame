# ghost-web-frame

全栈基础框架。后端 Spring Boot，前端 Vue 3，数据库 MySQL，鉴权 JWT。不使用 Docker。

## 环境

| 工具 | 版本 |
| --- | --- |
| JDK | 17 |
| Spring Boot | 3.5.16 |
| Maven | 使用 `ghost-frame-backend/mvnw`，不必单独安装 |
| Node.js | 建议 20 及以上（需能运行 Vite 8） |
| MySQL | 8.x |

## 目录

```text
ghost-frame-backend/     Spring Boot，接口前缀 /ghost，端口 10583
ghost-frame-frontend/    Vue 3 + Vite，开发端口 5173
```

后端包结构按**业务模块**划分，模块内再按层组织：

```text
com.ghost.frame
├── common/      框架级横切：Response、MyMetaObjectHandler、全局异常
├── config/      OpenApiConfig
├── security/    SecurityConfig、JwtAuthenticationFilter、JwtUtil
├── utils/       ApiResourceScannerUtil（只读扫描容器路由）
└── modules/
    ├── common/  跨模块共享：BaseController、BaseEntity、DeleteFlag
    ├── user/    用户与鉴权：controller / service / entity / mapper / dto / vo / enums
    └── api/     接口资源池：controller / service / entity / mapper / dto / vo / enums
```

新增业务时在 `modules/` 下新建模块子包，不要把类直接散落在 `modules/` 下。

## 准备数据库

创建库 `silly_ghost_frame`，字符集 `utf8mb4`。

表结构不由启动自动维护。在空库上按顺序手工执行 `ghost-frame-backend/data_table/schema/` 下的全量脚本；已有库只执行尚未跑过的 `data_table/migration/{年}/{月}/{日}/` 增量脚本。

当前涉及的表：

| 表 | 全量脚本 | 说明 |
| --- | --- | --- |
| `user` | `ghost-frame-backend/data_table/schema/user.sql` | 用户与鉴权 |
| `api_resource` | `ghost-frame-backend/data_table/schema/api_resource.sql` | 接口资源池，供后续权限配置选用 |

`data_table/` 是唯一的表结构来源（全量 `schema/` + 按日期分目录的增量 `migration/`），规范见 `ghost-frame-backend/data_table/README.md`。改表时同步全量脚本，并另写一份增量脚本供已有库执行。

## 后端

在 `ghost-frame-backend/src/main/resources/` 复制本地配置（该文件已忽略，不要提交）：

```powershell
copy application-local.yml.example application-local.yml
```

编辑 `application-local.yml`，填入 MySQL 账号、密码和 JWT 密钥。`application.yml` 会读取 `mysql.username`、`mysql.password`、`jwt.jwt-secret`。默认 profile 为 `local`，端口 `10583`。

access token 有效期 6 小时（`jwt.expiration-ms: 21600000`），refresh token 有效期 7 天（`jwt.refresh-expiration-ms: 604800000`）。两种票用 `tokenType` 区分，业务接口只接受 access token。

在 `ghost-frame-backend` 目录启动：

```powershell
.\mvnw.cmd spring-boot:run
```

接口文档：<http://localhost:10583/doc.html>

### 已有接口

鉴权（`modules/user`）：

- `POST /ghost/auth/register`：注册并登录，返回 `token` 与 `refreshToken`
- `POST /ghost/auth/login`：登录，返回 `token` 与 `refreshToken`
- `POST /ghost/auth/refresh`：用 `{ "refreshToken": "..." }` 换新的 access token，无需登录
- `GET /ghost/auth/me`：当前用户，请求头 `Authorization: Bearer <token>`
- `POST /ghost/auth/logout`：确认已登录后返回成功，**不吊销 JWT**——已签发的票在过期前仍然可用

接口资源（`modules/api`），需登录：

- `POST /ghost/api-resource/scan-diff`：扫描容器路由并与 `api_resource` 表比对，返回新增 / 修改 / 删除三类差异，**只读不写库**
- `POST /ghost/api-resource/apply`：提交人工勾选的差异，**唯一写入口**
- `GET /ghost/api-resource/list`：查询表内接口清单

扫描只收录 `/ghost` 前缀下的路由，`/v3/api-docs`、`/doc.html`、`/webjars`、`/error` 等框架端点自动排除。表里的「删除」只把 `status` 置为 0，不物理删除记录——后续权限表要按 `id` 引用这张表。`remark` 字段由人工维护，扫描同步不会覆盖它。

`UserRole` 只表示账号类型，不参与接口鉴权。公开接口以外的请求都要求已登录。接口级权限体系尚在建设中。

## 前端

在 `ghost-frame-frontend` 目录：

```powershell
npm install
npm run dev
```

浏览器打开 <http://localhost:5173>。Vite 把以 `/ghost` 开头的请求代理到 `http://localhost:10583`。

登录态放在 `localStorage`：access token 键名 `ghost-token`，refresh token 键名 `ghost-refresh-token`。access token 过期或接口返回 401 时，前端会先调 `/ghost/auth/refresh`，再重放原来的请求。未登录访问首页会跳到登录页。

前端目前覆盖登录、注册、首页三个页面；接口资源管理尚无界面，可直接调接口或用 knife4j 调试。

## 联调顺序

1. MySQL 已建库，并已手工执行 `data_table/schema/` 中的全量脚本
2. `application-local.yml` 已填写
3. 后端在 10583 启动
4. 前端在 5173 启动
5. 注册 → 登录 → 首页能看到用户信息 → 刷新页面后仍保持登录 → 登出

## 当前状态

阶段 0–6 已完成并验收（鉴权闭环、Token 刷新、测试、仓库补齐）。表结构改为只由 `data_table/` 手工维护，启动不再执行迁移。正在进行的是接口资源池与后续的权限体系。

## 开发约定

`AGENTS.md` 记录了完整的项目结构、包结构约定、数据库脚本规范、已知问题和下一步计划，改动代码前请先读它。要点：

- 后端代码由开发者手动维护
- 密钥、数据库密码不进仓库
- 改表时同步 `data_table/schema/` 全量脚本，并在 `data_table/migration/` 新增增量脚本后手工执行
