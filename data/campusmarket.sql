CREATE
    DATABASE market_system_db;

USE market_system_db;


# 公司表
DROP TABLE IF EXISTS `tbl_firm`;
CREATE TABLE `tbl_firm`
(
    `id`      INT(20)      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name`    VARCHAR(100) NOT NULL,
    `address` VARCHAR(100) NOT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='公司信息表';

TRUNCATE TABLE tbl_firm;

SELECT *
FROM tbl_firm;

INSERT INTO `tbl_firm`(`id`, `name`, `address`)
VALUES (1, '中国联通集团', '北京市大兴区亦庄文化园'),
       (2, '中国联通湖北省分公司', '湖北省武汉市江汉区'),
       (3, '中国联通陕西省分公司', '陕西省西安市雁塔区'),
       (4, '中国联通武汉市分公司', '湖北省武汉市汉口火车站'),
       (5, '中国联通黄石市分公司', '湖北省黄石市黄石港区');


#角色表
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `id`          INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `code`        VARCHAR(50) DEFAULT NULL,
    `name`        VARCHAR(50) DEFAULT NULL,
    `data_access` VARCHAR(50) DEFAULT NULL
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='角色信息表';

SELECT *
FROM `sys_role`;

INSERT INTO `sys_role`
VALUES (1, 'ADMINISTRATOR', '系统管理员', 'GROUP'),
       (2, 'PROVINCIAL_ADMIN', '省份管理员', 'PROVINCIAL'),
       (3, 'CITY_ADMIN', '地市管理员', 'CITY'),
       (4, 'CAMPUS_MANAGER', '校园经理', 'CAMPUS');


# 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `id`          BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `role_id`     BIGINT(20)  NOT NULL,
    `login_name`  VARCHAR(30) NOT NULL COMMENT 'oa账号',
    `user_name`   VARCHAR(30) NOT NULL COMMENT '用户昵称',
    `telephone`   VARCHAR(11)  DEFAULT NULL COMMENT '手机号码',
    `password`    VARCHAR(50)  DEFAULT NULL COMMENT '密码',
    `salt`        VARCHAR(20)  DEFAULT NULL COMMENT '盐加密',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `province`    VARCHAR(50)  DEFAULT NULL COMMENT '省份',
    `city`        VARCHAR(50)  DEFAULT NULL COMMENT '城市',
    `create_by`   VARCHAR(64)  DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='用户信息表';


ALTER TABLE `sys_user`
    ADD COLUMN `campus_name` VARCHAR(50) DEFAULT NULL COMMENT '校园经理所负责的校园名称';
INSERT INTO `sys_user`
VALUES (1, 1, 'system', 'system', NULL, 'system', NULL, 0, NULL, NULL, NULL, '2021-01-14 01:53:55', NULL, NULL,
        '系统账户发送系统消息');
INSERT INTO `sys_user`
VALUES (2, 1, 'admin', 'admin', NULL, 'admin', NULL, 0, NULL, NULL, NULL, '2021-01-14 01:53:55', NULL, NULL, '管理员账户');

SELECT *
FROM `sys_user`;


# 营销活动表
DROP TABLE IF EXISTS `tbl_activities`;
CREATE TABLE `tbl_activities`
(
    `id`          BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '营销活动ID',
    `name`        VARCHAR(50)  DEFAULT NULL COMMENT '活动名称',
    `content`     VARCHAR(500) DEFAULT NULL COMMENT '活动内容',
    `target_nums` INT(20)      DEFAULT NULL COMMENT '放号目标',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '0-未接受，1-已接收，2-删除，3-过期未完成，4-已完成',
    `city`        VARCHAR(50)  DEFAULT NULL COMMENT '地市',
    `begin_time`  DATETIME     DEFAULT NULL COMMENT '开始时间',
    `end_time`    DATETIME     DEFAULT NULL COMMENT '结束时间',
    `create_by`   VARCHAR(64)  DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_by`   VARCHAR(64)  DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `accept_name` VARCHAR(50)  DEFAULT NULL COMMENT '活动接受者',
    `accept_time` DATETIME     DEFAULT NULL COMMENT '接受时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE = INNODB
  DEFAULT CHARSET = utf8 COMMENT ='营销活动表';



DROP
    DATABASE market_system_db;
