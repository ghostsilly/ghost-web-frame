# 数据库脚本约定

本目录存放 `ghost-frame-backend` 的 MySQL 表结构脚本。新表、改表前必读本文件。

## 目录组织

```text
data_table/
├── README.md          # 本文件
├── schema/            # 全量建表脚本（每张表一个文件）
│   └── user.sql
└── migration/         # 增量变更脚本（ALTER），按 年/月/日 分目录
    └── 2026/10/23/xxx.sql
```

- **`schema/` 下的 `*.sql`**：全量建表脚本，新环境 / 重建库时一键执行。
- **`migration/` 下的脚本**：已有环境的增量升级，只允许 `ALTER` / 新建表，禁止 `DROP TABLE`。

## 全量建表脚本规范（每张表一个文件）

1. **一张表一个 `.sql` 文件**，文件名 = 表名（小写下划线），如 `user.sql`、`post_comment.sql`。
2. 文件内**必须分成两段**，用注释隔开：
   - 第一段 `CREATE TABLE`：只写字段定义 + 主键。
   - 第二段 `CREATE INDEX` / `CREATE UNIQUE INDEX`：所有二级索引单独写，不要内嵌在 `CREATE TABLE` 里。
3. **每个字段都必须有 `COMMENT '...'`**，表本身也要有 `COMMENT='...'`；缺注释不允许入库。
4. 表名、字段名一律**小写下划线**；字符串统一 `utf8mb4 / utf8mb4_unicode_ci`；引擎 `InnoDB`；`ROW_FORMAT = Dynamic`。
5. **公共字段必须包含**（与 `com.ghost.frame.modules.entity.BaseEntity` 对齐）：

   | 字段 | 类型 | 说明 |
   | --- | --- | --- |
   | `id` | bigint, AUTO_INCREMENT | 主键 |
   | `created_date` | datetime | 创建时间，默认 CURRENT_TIMESTAMP |
   | `created_by` | bigint, NULL | 创建人 ID |
   | `updated_date` | datetime | 更新时间，ON UPDATE CURRENT_TIMESTAMP |
   | `updated_by` | bigint, NULL | 更新人 ID |
   | `deleted` | tinyint, 默认 0 | 逻辑删除：0-未删，1-已删 |

6. 索引命名：
   - 主键：在建表语句内 `PRIMARY KEY (id)`。
   - 唯一索引：`uk_{表缩写}_{字段}`，例如 `uk_user_username`。
   - 普通索引：`idx_{表缩写}_{字段}`，例如 `idx_user_status`。
7. 文件开头保留 `SET NAMES utf8mb4;`；**不要**写 `USE`、`DROP DATABASE`，库名由连接串指定。
8. 全量脚本允许带 `DROP TABLE IF EXISTS`（开发环境重建用）。

## 增量脚本规范（migration/）

1. **按创建日期分目录存放**：`migration/{年}/{月}/{日}/脚本.sql`，例如：

   ```text
   migration/
   └── 2026/
       └── 10/
           ├── 23/
           │   ├── add_user_phone.sql
           │   └── add_user_idx_email.sql
           └── 28/
               └── create_post_table.sql
   ```

   目录名 `年` 用 4 位数字，`月`、`日` 用 2 位数字（不补零也行，建议补零保持排序稳定）。同一天的多个脚本放在同一天目录下。
2. **文件名用描述性动词开头**，例如 `add_user_phone.sql`、`create_post_table.sql`、`alter_user_modify_password_length.sql`，不要带日期戳（日期已经由目录表达）。
3. **每个脚本文件开头必须有中英文双语注释块**，说明脚本作用、作者、创建时间：

   ```sql
   -- ============================================================
   -- 脚本作用：为 user 表新增 phone 字段（varchar(20)，可空）
   -- Description: Add phone column to user table
   -- Author: your-name
   -- Created: 2026-10-23
   -- ============================================================
   ```

4. 脚本内容只写 `ALTER TABLE ... ADD / MODIFY / CHANGE / ADD INDEX ...` 或 `CREATE TABLE ...`（新建表的增量脚本），**禁止 `DROP TABLE`、`DROP COLUMN`（除非明确数据已废弃）、`DELETE`、`UPDATE` 生产数据**。
5. 每次表结构变更：
   - 新环境要看到最新结构 → **同步修改 `schema/` 下对应的全量脚本**；
   - 老环境升级 → 在 `migration/{年}/{月}/{日}/` 下新增一个脚本。
6. 后续若引入 Flyway，由于 Flyway 默认不递归扫描多层日期目录，需要把脚本铺平到单个目录，或配置 `spring.flyway.locations` 按日期目录逐个列出；按日期分目录主要是为了人工按时间线查阅。

## 标准模板

```sql
SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for {表名}
-- ----------------------------
DROP TABLE IF EXISTS `{表名}`;
CREATE TABLE `{表名}` (
  `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  -- TODO: 业务字段，每个字段都要有 COMMENT
  `created_date`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by`    bigint       NULL DEFAULT NULL COMMENT '创建人 ID',
  `updated_date`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by`    bigint       NULL DEFAULT NULL COMMENT '更新人 ID',
  `deleted`       tinyint      NOT NULL DEFAULT 0 COMMENT '删除状态：0-未删，1-已删',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '{表注释}'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Indexes for {表名}
-- ----------------------------
CREATE UNIQUE INDEX `uk_{表缩写}_{字段}` ON `{表名}` (`{字段}` ASC) USING BTREE;
CREATE INDEX         `idx_{表缩写}_{字段}` ON `{表名}` (`{字段}` ASC) USING BTREE;
```
