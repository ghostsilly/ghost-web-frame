SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for user
-- ----------------------------
CREATE TABLE `user` (
  `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
  `nickname`      varchar(50)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `username`      varchar(50)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（BCrypt 哈希）',
  `role`          varchar(20)  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'USER' COMMENT '角色：USER-用户，ADMIN-管理员，SUPER_ADMIN-超级管理员',
  `status`        tinyint      NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-封禁',
  `created_date`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `created_by`    bigint       NULL DEFAULT NULL COMMENT '创建人 ID',
  `updated_date`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updated_by`    bigint       NULL DEFAULT NULL COMMENT '更新人 ID',
  `deleted`       tinyint      NOT NULL DEFAULT 0 COMMENT '删除状态：0-未删，1-已删',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Indexes for user
-- ----------------------------
CREATE UNIQUE INDEX `uk_user_username` ON `user` (`username` ASC) USING BTREE;
CREATE INDEX         `idx_user_status`  ON `user` (`status`   ASC) USING BTREE;
CREATE INDEX         `idx_user_deleted` ON `user` (`deleted`  ASC) USING BTREE;
