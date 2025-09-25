/*
 Navicat Premium Dump SQL

 Source Server         : 阿里云
 Source Server Type    : MySQL
 Source Server Version : 80041 (8.0.41)
 Source Host           : 123.57.221.79:3306
 Source Schema         : table_tennis_system

 Target Server Type    : MySQL
 Target Server Version : 80041 (8.0.41)
 File Encoding         : 65001

 Date: 25/09/2025 23:35:07
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for booking_record
-- ----------------------------
DROP TABLE IF EXISTS `booking_record`;
CREATE TABLE `booking_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `booking_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约单号',
  `student_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学员 username',
  `student_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学员姓名',
  `coach_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练 username',
  `coach_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练姓名',
  `campus_id` int NOT NULL COMMENT '校区编号',
  `course_id` bigint UNSIGNED NOT NULL COMMENT '对应课程ID（逻辑外键）',
  `date` date NOT NULL COMMENT '上课日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `duration` int NOT NULL COMMENT '时长（分钟）',
  `court_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '球台编号',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '状态：pending, confirmed, cancelled, completed',
  `amount` decimal(8, 2) NOT NULL COMMENT '课时费',
  `payment_status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'unpaid' COMMENT '支付状态：unpaid, paid, refunded',
  `cancel_initiator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '取消方 username',
  `cancel_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '取消原因',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `update_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_booking_no`(`booking_no` ASC) USING BTREE,
  INDEX `idx_student`(`student_username` ASC) USING BTREE,
  INDEX `idx_coach`(`coach_username` ASC) USING BTREE,
  INDEX `idx_date_status`(`date` ASC, `status` ASC) USING BTREE,
  INDEX `idx_campus_date`(`campus_id` ASC, `date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程预约记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of booking_record
-- ----------------------------
INSERT INTO `booking_record` VALUES (1, 'BOOK202510010001', 'user_zhangsan', '篮球学员张三', 'lcp', '篮球教练王五', 4, 1, '2025-10-01', '18:00:00', '19:00:00', 60, 'T01', 'completed', 200.00, 'paid', NULL, NULL, '2025-09-25 15:33:32.736', '2025-09-25 22:03:19.431');
INSERT INTO `booking_record` VALUES (2, 'BOOK202510010002', 'student_zhao', '赵同学', 'lcp', '张教练', 2, 2, '2025-10-01', '19:00:00', '20:00:00', 60, 'T02', 'completed', 200.00, 'paid', NULL, NULL, '2025-09-25 15:33:32.736', '2025-09-25 22:03:19.480');
INSERT INTO `booking_record` VALUES (3, 'BOOK202510020001', 'student_sun', '孙同学', 'lcp', '李教练', 3, 3, '2025-10-02', '17:00:00', '18:00:00', 60, 'T03', 'cancelled', 150.00, 'unpaid', NULL, '2', '2025-09-25 15:33:32.736', '2025-09-25 22:03:19.386');
INSERT INTO `booking_record` VALUES (4, '2222', 'user_zhangsan', '篮球学员张三', 'coach_wangwu', '篮球教练王五', 1, 1, '2025-10-01', '18:00:00', '19:00:00', 60, 'T01', 'cancelled', 120.00, 'refunded', '222', '22', '2025-09-25 17:44:10.265', '2025-09-25 17:44:37.790');
INSERT INTO `booking_record` VALUES (5, 'BK1758810309212359', 'lcp', 'lcp', 'lcp', '篮球教练王五', 1, 1, '2025-10-01', '18:00:00', '19:00:00', 60, 'T01', 'pending', 0.00, 'unpaid', NULL, NULL, '2025-09-25 22:25:10.281', '2025-09-25 22:25:10.281');
INSERT INTO `booking_record` VALUES (6, 'BK1758811034786919', 'lcp', 'lcp', 'lcp', '张教练', 2, 2, '2025-10-01', '19:00:00', '20:00:00', 60, 'T02', 'completed', 0.00, 'unpaid', NULL, NULL, '2025-09-25 22:37:15.850', '2025-09-25 23:05:37.259');
INSERT INTO `booking_record` VALUES (8, 'BK1758811282877487', 'lcp', 'lcp', 'coach_li', '李教练', 2, 3, '2025-10-02', '17:00:00', '18:00:00', 60, 'T03', 'pending', 0.00, 'unpaid', NULL, NULL, '2025-09-25 22:41:24.031', '2025-09-25 22:41:24.031');

-- ----------------------------
-- Table structure for campus
-- ----------------------------
DROP TABLE IF EXISTS `campus`;
CREATE TABLE `campus`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '校区编号（主键，如：ZX001, FX002）',
  `campus_no` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `campus_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '校区名称',
  `campus_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：C-中心校区，B-分校区',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `contact_person` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人姓名',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `admin_username` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '逻辑外键：校区管理员用户名',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '校区图片URL',
  `created_time` datetime NOT NULL DEFAULT (now()) COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 813522947 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '校区信息表（逻辑外键）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of campus
-- ----------------------------
INSERT INTO `campus` VALUES (1, NULL, '蓝星上海浦东校区', 'B', '上海市浦东新区世纪大道100号', '李娜', '13900002222', 'lina_sh@lanxing.com', 'admin_li', 'https://example.com/images/shanghai-pudong.jpg', '2025-09-02 14:30:00', '2025-09-10 09:15:00');
INSERT INTO `campus` VALUES (2, NULL, '蓝星广州天河校区', 'B', '广州市天河区体育东路88号', '王强', '13700003333', 'wangqiang_gz@lanxing.com', 'admin_wang', 'https://example.com/images/guangzhou-tianhe.jpg', '2025-09-03 09:20:00', NULL);
INSERT INTO `campus` VALUES (3, NULL, '蓝星成都武侯校区', 'B', '成都市武侯区人民南路四段20号', '陈芳', '13600004444', 'chenfang_cd@lanxing.com', 'admin_chen', 'https://example.com/images/chengdu-wuhou.jpg', '2025-09-04 16:45:00', NULL);
INSERT INTO `campus` VALUES (4, NULL, '蓝星乒乓球中心校区', 'C', '北京市海淀区中关村大街1号2', '张伟2', '13800001111', 'zhangwei@lanxing.com', 'admin_zhang', 'http://localhost:8080/upload/1758787696256.png', '2025-09-01 10:00:00', NULL);
INSERT INTO `campus` VALUES (5, 'ZX006', '测试', 'C', '分公司的方式给', 'lcp', '15865632011', '14614@qq.com', '222', 'http://localhost:8080/upload/1758787917960.png', '2025-09-25 16:11:59', '2025-09-25 16:12:25');
INSERT INTO `campus` VALUES (452812801, 'ZX006', 'fdsfd', 'B', 'dsfsfs', 'fdsdf', '15865696841', '113@qq.com', 'campus_admin_bj', 'http://localhost:8080/upload/1758787974179.png', '2025-09-25 16:12:55', '2025-09-25 16:19:52');

-- ----------------------------
-- Table structure for coach_detail
-- ----------------------------
DROP TABLE IF EXISTS `coach_detail`;
CREATE TABLE `coach_detail`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '逻辑外键：关联 user 表 username',
  `real_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `level` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练等级：初级、中级、高级',
  `hourly_rate` decimal(8, 2) NOT NULL COMMENT '每小时收费',
  `photo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练照片URL',
  `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '比赛成绩描述',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'approved' COMMENT '审核状态：pending, approved, rejected',
  `campus_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属校区编号（逻辑外键）',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `update_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_campus_id`(`campus_id` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '教练员详情表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of coach_detail
-- ----------------------------
INSERT INTO `coach_detail` VALUES (1, 'coach_wangwu', '张教练', '高级', 200.00, 'http://localhost:8080/upload/1758788979703.png', '全国青少年乒乓球锦标赛冠军', 'approved', '1', '2025-09-25 15:33:31.476', '2025-09-25 23:22:29.948');
INSERT INTO `coach_detail` VALUES (2, 'coach_li', '李教练', '中级', 150.00, 'https://example.com/coach/li.jpg', '省队退役，带队获市级比赛前三', 'approved', '2', '2025-09-25 15:33:31.476', '2025-09-25 21:54:25.723');
INSERT INTO `coach_detail` VALUES (3, 'coach_wang', '王教练', '初级', 80.00, 'https://example.com/coach/wang.jpg', '校队主力，教学经验丰富', 'approved', '3', '2025-09-25 15:33:31.476', '2025-09-25 21:54:25.802');
INSERT INTO `coach_detail` VALUES (4, 'coach_zhaoliu', 'fdfds', '初级', 40.00, 'http://localhost:8080/upload/1758789302736.png', '222', 'approved', '1', '2025-09-25 16:34:50.045', '2025-09-25 16:34:50.045');

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `coach_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练 username',
  `coach_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练姓名（冗余）',
  `campus_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '校区编号',
  `date` date NOT NULL COMMENT '课程日期',
  `start_time` time NOT NULL COMMENT '开始时间',
  `end_time` time NOT NULL COMMENT '结束时间',
  `duration` int NOT NULL COMMENT '时长（分钟）',
  `available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否可预约：1=是，0=否',
  `img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `court_number` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '球台编号',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `update_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach_date`(`coach_username` ASC, `date` ASC) USING BTREE,
  INDEX `idx_campus_date`(`campus_id` ASC, `date` ASC) USING BTREE,
  INDEX `idx_time_status`(`date` ASC, `start_time` ASC, `available` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '课程排班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'lcp', '篮球教练王五', '1', '2025-10-01', '18:00:00', '19:00:00', 60, 1, 'http://localhost:8080/upload/1758789998975.png', 'T01', '2025-09-25 15:33:32.161', '2025-09-25 21:52:26.356');
INSERT INTO `course` VALUES (2, 'lcp', '张教练', '2', '2025-10-01', '19:00:00', '20:00:00', 60, 1, NULL, 'T02', '2025-09-25 15:33:32.161', '2025-09-25 22:08:42.723');
INSERT INTO `course` VALUES (3, 'coach_li', '李教练', '2', '2025-10-02', '17:00:00', '18:00:00', 60, 1, NULL, 'T03', '2025-09-25 15:33:32.161', '2025-09-25 22:08:42.676');

-- ----------------------------
-- Table structure for evaluation_record
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_record`;
CREATE TABLE `evaluation_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `booking_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '关联预约单号',
  `student_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学员 username',
  `student_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '学员反馈：收获与教训',
  `student_rating` tinyint NULL DEFAULT NULL COMMENT '学员评分 1-5',
  `coach_username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练 username',
  `coach_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '教练反馈：表现与建议',
  `coach_rating` tinyint NULL DEFAULT NULL COMMENT '教练评分 1-5',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `update_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_booking_no`(`booking_no` ASC) USING BTREE,
  INDEX `idx_student`(`student_username` ASC) USING BTREE,
  INDEX `idx_coach`(`coach_username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '训练课评价记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of evaluation_record
-- ----------------------------
INSERT INTO `evaluation_record` VALUES (1, 'BOOK202510010001', 'lcp', '学会了正手攻球发力技巧，还需加强步伐移动。', 5, 'lcp', '动作规范，理解力强，建议多练习组合球。1', 5, '2025-09-25 15:33:33.832', '2025-09-25 23:06:05.335');
INSERT INTO `evaluation_record` VALUES (2, 'BOOK202510010002', 'lcp', '发球旋转掌握不好，下次重点练习。', 4, 'lcp', '训练认真，发球需加强摩擦感。', 2, '2025-09-25 15:33:33.832', '2025-09-25 23:06:05.291');
INSERT INTO `evaluation_record` VALUES (3, 'BOOK202510020001', 'student_sun', NULL, NULL, 'coach_wangwu', NULL, NULL, '2025-09-25 15:33:33.832', '2025-09-25 22:59:22.676');
INSERT INTO `evaluation_record` VALUES (5, 'BK1758811034786919', 'lcp', '2222222', 5, 'lcp', NULL, NULL, '2025-09-25 23:12:23.784', '2025-09-25 23:12:23.784');

-- ----------------------------
-- Table structure for student_coach
-- ----------------------------
DROP TABLE IF EXISTS `student_coach`;
CREATE TABLE `student_coach`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` bigint UNSIGNED NOT NULL COMMENT '学员 user.id',
  `user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '学员姓名',
  `coach_id` bigint UNSIGNED NOT NULL COMMENT '教练 user.id',
  `coach_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '教练姓名',
  `is_approved` tinyint(1) NOT NULL DEFAULT 0,
  `apply_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `create_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3),
  `update_time` datetime(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_coach`(`coach_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学员选择教练关系表（使用 id 关联）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_coach
-- ----------------------------
INSERT INTO `student_coach` VALUES (8, 8, 'lcp', 1, '张教练', 1, '2025-09-25 23:23:46.114', '2025-09-25 23:23:46.114', '2025-09-25 23:32:35.911');

-- ----------------------------
-- Table structure for tuition_record
-- ----------------------------
DROP TABLE IF EXISTS `tuition_record`;
CREATE TABLE `tuition_record`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL,
  `record_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '交易编号',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户 username',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户姓名',
  `amount` decimal(10, 2) NOT NULL COMMENT '金额（正数）',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式：wechat, alipay, offline',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_record_no`(`record_no` ASC) USING BTREE,
  INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '学费交易记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of tuition_record
-- ----------------------------
INSERT INTO `tuition_record` VALUES (1, NULL, 'TR202509250001', 'lcp', '刘同学', 1000.00, 'wechat', '微信充值', '2025-09-25 15:33:33.291');
INSERT INTO `tuition_record` VALUES (2, 8, 'TR202509250002', 'lcp', '刘同学', 200.00, 'wechat', '预约课程扣费', '2025-09-25 15:33:33.291');
INSERT INTO `tuition_record` VALUES (4, 8, '342432', 'user_lisi', '羽毛球学员李四', 9.00, 'alipay', '323223', '2025-09-25 17:07:53.912');
INSERT INTO `tuition_record` VALUES (5, NULL, 'TR1758813486330', 'lcp', 'lcp', 0.00, 'wechat', '预约课程支付 - BK1758811282877487', '2025-09-25 23:18:07.410');
INSERT INTO `tuition_record` VALUES (7, NULL, 'TR1758813623204', 'lcp', 'lcp', 0.00, 'wechat', '预约课程支付 - BK1758810309212359', '2025-09-25 23:20:24.362');
INSERT INTO `tuition_record` VALUES (8, NULL, 'TR1758813625429', 'lcp', 'lcp', 0.00, 'wechat', '预约课程支付 - BK1758810309212359', '2025-09-25 23:20:26.562');
INSERT INTO `tuition_record` VALUES (9, NULL, 'TR1758813627680', 'lcp', 'lcp', 0.00, 'wechat', '预约课程支付 - BK1758810309212359', '2025-09-25 23:20:28.794');
INSERT INTO `tuition_record` VALUES (10, NULL, 'BK1758810309212359', 'lcp', 'lcp', 0.00, 'wechat', '预约课程支付 - BK1758810309212359', '2025-09-25 23:22:43.669');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
  `sex` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称:superAdmin, campusAdmin, user, coach.',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` int NULL DEFAULT 0 COMMENT '使用状态 0.正常 1拉黑',
  `create_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '系统超级管理员', '13800138000', 'http://localhost:8080/upload/1758787082299.png', '男', 'lcp', '123456', 'superAdmin', 'superadmin@example.com', 0, '2025-09-25 15:23:16.000', '2025-09-25 15:50:54.000');
INSERT INTO `user` VALUES (2, '北京校区管理员', '13900139001', 'https://example.com/avatars/campusadmin_bj.jpg', '女', 'campus_admin_bj', '123456', 'campusAdmin', 'campus_bj@example.com', 0, '2025-09-25 15:23:16.021', '2025-09-25 15:23:16.021');
INSERT INTO `user` VALUES (3, '上海校区管理员', '13900139002', 'https://example.com/avatars/campusadmin_sh.jpg', '男', 'campus_admin_sh', '123456', 'campusAdmin', 'campus_sh@example.com', 0, '2025-09-25 15:23:16.021', '2025-09-25 15:23:16.021');
INSERT INTO `user` VALUES (4, '篮球学员张三', '13700137001', 'https://example.com/avatars/user_zhangsan.jpg', '男', 'user_zhangsan', '123456', 'user', 'zhangsan@example.com', 0, '2025-09-25 15:23:16.021', '2025-09-25 15:23:16.021');
INSERT INTO `user` VALUES (5, '羽毛球学员李四', '13700137002', 'https://example.com/avatars/user_lisi.jpg', '女', 'user_lisi', '123456', 'user', 'lisi@example.com', 0, '2025-09-25 15:23:16.021', '2025-09-25 15:23:16.021');
INSERT INTO `user` VALUES (6, '篮球教练王五', '13600136001', 'http://localhost:8080/upload/1758810553107.png', '男', 'coach_wangwu', '123456', 'coach', 'wangwu_coach@example.com', 0, '2025-09-25 15:23:16.021', '2025-09-25 22:29:14.972');
INSERT INTO `user` VALUES (7, '游泳教练赵六', '13600136002', 'https://example.com/avatars/coach_zhaoliu.jpg', '女', 'coach_zhaoliu', '123456', 'coach', 'zhaoliu_coach@example.com', 0, '2025-09-25 15:23:16.021', '2025-09-25 15:23:16.021');
INSERT INTO `user` VALUES (8, '测试拉黑用户孙七', '13500135001', 'https://example.com/avatars/user_sunqi.jpg', '男', 'lcp', '123456', 'user', 'sunqi@example.com', 0, '2025-09-25 15:23:16.021', '2025-09-25 18:14:36.141');

SET FOREIGN_KEY_CHECKS = 1;
