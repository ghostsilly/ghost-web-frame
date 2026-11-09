-- ----------------------------
-- Table structure for api_resource
-- ----------------------------
CREATE TABLE `api_resource` (
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `path`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接口路径 pattern，与 Spring 注册一致，如 /ghost/auth/login',
    `method`        varchar(10)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'HTTP 方法：GET/POST/PUT/DELETE/PATCH，空串表示不限方法',
    `module`        varchar(64)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '所属模块，取自控制器类上的 @Tag.name',
    `summary`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接口说明，取自方法上的 @Operation.summary',
    `handler`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理方法，格式为 全限定类名#方法名',
    `status`        tinyint      NOT NULL DEFAULT 1 COMMENT '状态：1-代码中存在，0-代码中已移除（保留历史权限配置）',
    `remark`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注，人工维护，扫描同步不覆盖',
    `created_date`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `created_by`    bigint       NULL DEFAULT NULL COMMENT '创建人 ID',
    `updated_date`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updated_by`    bigint       NULL DEFAULT NULL COMMENT '更新人 ID',
    `deleted`       tinyint      NOT NULL DEFAULT 0 COMMENT '删除状态：0-未删，1-已删',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT = '接口资源表（人工触发扫描后确认入库，权限配置的可选接口池）'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Indexes for api_resource
-- ----------------------------
CREATE UNIQUE INDEX `uk_ar_path_method` ON `api_resource` (`path` ASC, `method` ASC) USING BTREE;
CREATE INDEX        `idx_ar_module`     ON `api_resource` (`module` ASC) USING BTREE;
CREATE INDEX        `idx_ar_status`     ON `api_resource` (`status` ASC) USING BTREE;
CREATE INDEX        `idx_ar_deleted`    ON `api_resource` (`deleted` ASC) USING BTREE;