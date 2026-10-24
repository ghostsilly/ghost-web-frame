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
ghost-frame-backend/     Spring Boot，接口前缀 /ghost
ghost-frame-frontend/    Vue 3 + Vite，开发端口 5173
```

## 准备数据库

创建库 `silly_ghost_frame`，字符集 `utf8mb4`，然后执行：

`ghost-frame-backend/data_table/schema/user.sql`

## 后端

在 `ghost-frame-backend/src/main/resources/` 复制本地配置（该文件已忽略，不要提交）：

```powershell
copy application-local.yml.example application-local.yml
```

编辑 `application-local.yml`，填入 MySQL 账号和 JWT 密钥。`application.yml` 会读取 `mysql.username`、`mysql.password`、`jwt.jwt-secret`。JWT 有效期 12 小时。默认 profile 为 `local`，端口 `8080`。

在 `ghost-frame-backend` 目录启动：

```powershell
.\mvnw.cmd spring-boot:run
```

接口文档：<http://localhost:8080/doc.html>

已有接口：

- `POST /ghost/auth/register`
- `POST /ghost/auth/login`
- `GET /ghost/auth/me`（请求头 `Authorization: Bearer <token>`）

## 前端

在 `ghost-frame-frontend` 目录：

```powershell
npm install
npm run dev
```

浏览器打开 <http://localhost:5173>。Vite 把以 `/ghost` 开头的请求代理到 `http://localhost:8080`。

登录态放在浏览器 `localStorage`，键名为 `ghost-token`。未登录访问首页会跳到登录页。

## 联调顺序

1. MySQL 已建库并执行 `user.sql`
2. `application-local.yml` 已填写
3. 后端在 8080 启动
4. 前端在 5173 启动
5. 注册 → 登录 → 首页能看到用户信息 → 刷新后仍保持登录 → 登出
