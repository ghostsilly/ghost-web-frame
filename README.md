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

## 准备数据库

创建库 `silly_ghost_frame`，字符集 `utf8mb4`，然后执行：

`ghost-frame-backend/data_table/schema/user.sql`

Flyway 尚未接入，新环境需要手动执行这份脚本。

## 后端

在 `ghost-frame-backend/src/main/resources/` 复制本地配置（该文件已忽略，不要提交）：

```powershell
copy application-local.yml.example application-local.yml
```

编辑 `application-local.yml`，填入 MySQL 账号和 JWT 密钥。`application.yml` 会读取 `mysql.username`、`mysql.password`、`jwt.jwt-secret`。默认 profile 为 `local`，端口 `10583`。

access token 有效期 6 小时（`jwt.expiration-ms: 21600000`），refresh token 有效期 7 天（`jwt.refresh-expiration-ms: 604800000`）。两种票用 `tokenType` 区分，业务接口只接受 access token。

在 `ghost-frame-backend` 目录启动：

```powershell
.\mvnw.cmd spring-boot:run
```

接口文档：<http://localhost:10583/doc.html>

已有接口：

- `POST /ghost/auth/register`：注册并登录，返回 `token` 与 `refreshToken`
- `POST /ghost/auth/login`：登录，返回 `token` 与 `refreshToken`
- `POST /ghost/auth/refresh`：用 `{ "refreshToken": "..." }` 换新的 access token，无需登录
- `GET /ghost/auth/me`：当前用户，请求头 `Authorization: Bearer <token>`
- `POST /ghost/auth/logout`：确认已登录后返回成功，不吊销 JWT

`UserRole` 只表示账号类型，不参与接口鉴权。公开接口以外的请求都要求已登录。

## 前端

在 `ghost-frame-frontend` 目录：

```powershell
npm install
npm run dev
```

浏览器打开 <http://localhost:5173>。Vite 把以 `/ghost` 开头的请求代理到 `http://localhost:10583`。

登录态放在 `localStorage`：access token 键名 `ghost-token`，refresh token 键名 `ghost-refresh-token`。access token 过期或接口返回 401 时，前端会先调 `/ghost/auth/refresh`，再重放原来的请求。未登录访问首页会跳到登录页。

## 联调顺序

1. MySQL 已建库并执行 `user.sql`
2. `application-local.yml` 已填写
3. 后端在 10583 启动
4. 前端在 5173 启动
5. 注册 → 登录 → 首页能看到用户信息 → 刷新页面后仍保持登录 → 登出
