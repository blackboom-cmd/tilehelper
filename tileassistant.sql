/*
 Navicat Premium Data Transfer

 Source Server         : TileAssistant
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : tileassistant

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 22/05/2026 11:05:03
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单编号（业务唯一）',
  `customer_id` int(11) NOT NULL COMMENT '客户ID（仅记录，不做关联约束）',
  `customer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户名称（冗余字段）',
  `total_amount` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '待处理' COMMENT '订单状态（待处理/已付款/已发货/已完成/已取消）',
  `delivery_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '送货地址',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '预计送货时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tile_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '瓦片名称',
  `tile_quantity` int(11) NOT NULL DEFAULT 0 COMMENT '总张数',
  `package_count` int(11) NOT NULL DEFAULT 0 COMMENT '整包数量',
  `extra_pieces` int(11) NOT NULL DEFAULT 0 COMMENT '零散张数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, 'ORD20260412001', 1, '张师傅', 1210.00, '待发货', '霍邱县城关镇工地A区', '2026-04-15 09:00:00', '优先安排早上送', '2026-04-12 17:16:57', '众和直角连锁琉璃瓦', 37, 5, 2);
INSERT INTO `order` VALUES (2, 'ORD20260412002', 2, '李老板', 850.00, '待处理', '霍邱县工业园B区', '2026-04-16 14:00:00', '需要帮忙搬到仓库', '2026-04-12 17:16:57', '兔旺达单波罗曼瓦', 25, 2, 5);
INSERT INTO `order` VALUES (17, 'ORD-20260519210919', 43926, '陈老板', 350.00, '待发货', '火车站', '2026-05-19 21:09:00', '', '2026-05-19 21:09:20', '众和直角连锁琉璃瓦', 100, 14, 2);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `failed_attempts` int(11) NULL DEFAULT 0 COMMENT '连续登录失败次数',
  `locked_until` datetime NULL DEFAULT NULL COMMENT '锁定截止时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'tilehome', 'TileHelper@2026', 0, NULL);

-- ----------------------------
-- Table structure for tile
-- ----------------------------
DROP TABLE IF EXISTS `tile`;
CREATE TABLE `tile`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tile_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '瓦片名称（展示用）',
  `model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '款式/型号',
  `specification` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '规格',
  `color` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '颜色',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `unit_quantity` int(11) NOT NULL COMMENT '每包数量',
  `stock_quantity` int(11) NOT NULL DEFAULT 0 COMMENT '库存数量',
  `accessories` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '配件说明',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片路径',
  `location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '存放位置',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '瓦片信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tile
-- ----------------------------
INSERT INTO `tile` VALUES (1, '众和直角连锁琉璃瓦', 'ZH-LX-300x400', '约300×400mm', '红棕色', 3.50, 7, 0, '配件：脊瓦、封边瓦、防水垫层、固定挂钩/水泥砂浆铺设', '/images/e35e2269e284121712e274acb01b7f0c.jpg', '家中库存/待分类');
INSERT INTO `tile` VALUES (2, '兔旺达单波罗曼瓦', 'TWD-RM-280x420', '约280×420mm', '黑色', 6.00, 6, 0, '配件：脊瓦、边瓦、固定螺钉、防水垫层、抗风卡扣', '/images/bed638f09b02f8fa56f7fd656c8190df.jpg', '家中库存/待分类');

-- ----------------------------
-- Table structure for transport
-- ----------------------------
DROP TABLE IF EXISTS `transport`;
CREATE TABLE `transport`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` int(11) NOT NULL COMMENT '订单ID（仅记录，不做外键）',
  `customer_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户名称（方便直接查看）',
  `truck_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '车辆名称/类型（如：小货车/大货车）',
  `total_pieces` int(11) NOT NULL DEFAULT 0 COMMENT '总张数（本次运输瓦片总数量）',
  `package_count` int(11) NOT NULL DEFAULT 0 COMMENT '整包数量（完整包装数量）',
  `extra_pieces` int(11) NOT NULL DEFAULT 0 COMMENT '零散张数（不满一包的部分）',
  `note` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '补充说明（如：补5张 / 协商处理）',
  `delivery_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '送货地址（快照）',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '计划送货时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '待出发' COMMENT '运输状态（待出发/运输中/已送达/取消）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tile_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '瓦片名称',
  `tile_id` int(11) NOT NULL COMMENT '瓦片ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '运输记录表（出车/送货单）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of transport
-- ----------------------------
INSERT INTO `transport` VALUES (1, 1, '张师傅', '小货车', 37, 3, 7, '零头客户同意补5张', '霍邱县城关镇工地A区', '2026-04-15 09:00:00', '待发货', '2026-04-12 17:17:24', '众和直角连锁琉璃瓦', 1);
INSERT INTO `transport` VALUES (2, 2, '李老板', '大货车', 25, 2, 5, '零散部分现场清点', '霍邱县工业园B区', '2026-04-16 14:00:00', '待出发', '2026-04-12 17:17:24', '兔旺达单波罗曼瓦', 2);
INSERT INTO `transport` VALUES (13, 17, '陈老板', NULL, 100, 14, 2, '', '火车站', '2026-05-19 21:09:00', '待发货', '2026-05-19 21:09:20', '众和直角连锁琉璃瓦', 1);

-- ----------------------------
-- Table structure for truck
-- ----------------------------
DROP TABLE IF EXISTS `truck`;
CREATE TABLE `truck`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '货车名称',
  `capacity` decimal(10, 2) NOT NULL COMMENT '可装载体积（单位：立方米）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of truck
-- ----------------------------
INSERT INTO `truck` VALUES (1, '1号货车', 12.50);

SET FOREIGN_KEY_CHECKS = 1;
