/*
Navicat MySQL Data Transfer

Source Server         : 120.26.217.116
Source Server Version : 50634
Source Host           : 120.26.217.116:3306
Source Database       : texas

Target Server Type    : MYSQL
Target Server Version : 50634
File Encoding         : 65001

Date: 2024-11-21 10:44:00
*/

SET FOREIGN_KEY_CHECKS=0;
use texas;
-- ----------------------------
-- Table structure for game_log
-- ----------------------------
DROP TABLE IF EXISTS `game_log`;
CREATE TABLE `game_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `roomType` varchar(50) DEFAULT NULL COMMENT '房间信息（普通场）',
  `roomLevel` varchar(50) DEFAULT NULL COMMENT '房间信息（级别）',
  `dealer` text COMMENT '庄家',
  `smallBet` text COMMENT '小盲',
  `bigBet` text COMMENT '大盲',
  `roundInfo` text COMMENT '第一圈（底牌圈）',
  `betpoolInfo` text COMMENT '分奖池信息',
  `startTime` datetime DEFAULT NULL COMMENT '游戏开始时间',
  `endTime` datetime DEFAULT NULL COMMENT '游戏结算时间',
  `countBetpool` int(11) DEFAULT NULL COMMENT '底池总金额',
  `cut` int(11) DEFAULT NULL COMMENT '抽成',
  `communityCards` text COMMENT ' 公共牌',
  `playersInitInfo` longtext COMMENT '房间中玩家的初始信息',
  `playersFinalInfo` longtext COMMENT '房间中玩家的最终信息 ',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='游戏日志';

-- ----------------------------
-- Table structure for player
-- ----------------------------
DROP TABLE IF EXISTS `player`;
CREATE TABLE `player` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(200) NOT NULL COMMENT '用户名',
  `userpwd` varchar(100) NOT NULL COMMENT '用户密码',
  `nickname` varchar(50) DEFAULT '' COMMENT '用户昵称',
  `email` varchar(30) DEFAULT '' COMMENT '邮箱',
  `phone` varchar(20) DEFAULT '' COMMENT '手机',
  `chips` bigint(20) DEFAULT '1000' COMMENT '筹码数',
  `pic` int(11) DEFAULT '0' COMMENT '头像编号',
  `regdate` datetime DEFAULT NULL COMMENT '注册时间',
  `state` tinyint(2) DEFAULT '1' COMMENT '状态1正常，2冻结',
  `isrobot` tinyint(2) DEFAULT '0' COMMENT '是否是机器人',
  `type` varchar(15) DEFAULT 'normal',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2494 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for player_chips_log
-- ----------------------------
DROP TABLE IF EXISTS `player_chips_log`;
CREATE TABLE `player_chips_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL COMMENT '用户名',
  `chips` bigint(20) DEFAULT '1000000' COMMENT '筹码数',
  `state` int(2) DEFAULT '1',
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for system_log
-- ----------------------------
DROP TABLE IF EXISTS `system_log`;
CREATE TABLE `system_log` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` int(11) DEFAULT NULL COMMENT '用户编号',
  `type` varchar(50) DEFAULT NULL COMMENT '类型',
  `operation` varchar(200) DEFAULT NULL COMMENT '操作',
  `content` text COMMENT '内容',
  `datetime` datetime DEFAULT NULL COMMENT '时间',
  `machine` varchar(100) DEFAULT NULL COMMENT '机器码',
  `clienttype` varchar(20) DEFAULT NULL COMMENT '客户端类型',
  `token` varchar(100) DEFAULT NULL COMMENT '记号',
  `appversion` varchar(15) DEFAULT NULL COMMENT 'app版本',
  `ip` varchar(30) DEFAULT NULL COMMENT 'IP',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83862 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for transaction_log
-- ----------------------------
DROP TABLE IF EXISTS `transaction_log`;
CREATE TABLE `transaction_log` (
  `transactionHash` varchar(200) NOT NULL,
  `status` tinyint(2) NOT NULL DEFAULT '1',
  `from` varchar(200) NOT NULL,
  `time` datetime NOT NULL,
  `value` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`transactionHash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Procedure structure for add_chips
-- ----------------------------
DROP PROCEDURE IF EXISTS `add_chips`;
CREATE DEFINER=`root`@`%` PROCEDURE `add_chips`(IN input_username VARCHAR(2000))
BEGIN
    DECLARE current_chips INT DEFAULT 0;

    -- 获取用户当前的筹码数
    SELECT p.chips INTO current_chips
    FROM player p
    WHERE p.username = input_username;

    -- 插入日志记录
    INSERT INTO player_chips_log (username, chips)
    VALUES (input_username, current_chips);

    -- 更新用户的筹码数
    UPDATE player p
    SET p.chips = current_chips + 100
    WHERE p.username = input_username;
END;