CREATE TABLE IF NOT EXISTS `user_stat` (
	`stat_code`	INT(11)	NOT NULL AUTO_INCREMENT COMMENT '능력 코드',
	`stat_name`	VARCHAR(20)	NOT NULL COMMENT '능력명',
	PRIMARY KEY (`stat_code`)
)COMMENT '회원 능력';

INSERT INTO user_stat (stat_code, stat_name) VALUES
(1, '슛'),
(2, '패스'),
(3, '드리블'),
(4, '체력'),
(5, '스피드'),
(6, '피지컬');

CREATE TABLE IF NOT EXISTS `user_style` (
	`style_code` INT(11) NOT NULL AUTO_INCREMENT COMMENT '스타일 코드',
	`style_name` VARCHAR(20) NOT NULL COMMENT '스타일명',
	PRIMARY KEY (`style_code`)
)COMMENT '회원 스타일';

INSERT INTO user_style (style_code, style_name) VALUES
(1, '공격'),
(2, '밸런스'),
(3, '수비');

CREATE TABLE IF NOT EXISTS `user_authority` (
	`auth_code`	VARCHAR(10)	NOT NULL COMMENT '권한 코드',
	`auth_name`	VARCHAR(20)	NOT NULL COMMENT '권한명',
	PRIMARY KEY (`auth_code`)
)COMMENT '회원 권한';

INSERT INTO user_authority (auth_code, auth_name) VALUES
('A1', 'ADMIN'),
('A2', 'MANAGER'),
('A3', 'USER');

CREATE TABLE IF NOT EXISTS `user` (
	`user_no` INT(11) NOT NULL AUTO_INCREMENT COMMENT '회원 번호',
	`user_name` VARCHAR(20)	NOT NULL COMMENT '회원 성명',
	`user_birth` DATE NOT NULL COMMENT '회원 생년월일',
	`user_phone` VARCHAR(15) NOT NULL COMMENT '회원 전화번호',
	`user_post_code` VARCHAR(10) NOT NULL COMMENT '우편번호',
	`user_addr`	VARCHAR(100) NOT NULL COMMENT '주소',
	`user_detail_addr` VARCHAR(100)	NOT NULL COMMENT '상세주소',
	`ori_pic_name` VARCHAR(255) NULL COMMENT '원본 프로필 사진명',
	`new_pic_name` VARCHAR(255) NULL COMMENT '저장 프로필 사진명',
	`recent_login` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '최근 로그인 시간',
	`user_reg_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
	`user_exit_status` VARCHAR(1) NOT NULL DEFAULT 'N' COMMENT '탈퇴여부',
	`user_exit_date` TIMESTAMP NULL COMMENT '탈퇴일시',
	`user_fail_status` VARCHAR(1) NOT NULL DEFAULT 'N' COMMENT '로그인 잠금 여부',
	`user_fail_cnt`	INT(2) NOT NULL DEFAULT 0 COMMENT '로그인 실패 횟수',
	`auth_code`	VARCHAR(10)	NOT NULL COMMENT '권한 코드',
	`user_code`	VARCHAR(10)	NOT NULL COMMENT '회원 코드',
	`user_gender` VARCHAR(1) NOT NULL COMMENT '성별',
	`user_comment` VARCHAR(255)	NULL COMMENT '소개',
	`style_code` INT(11) NULL COMMENT '스타일 코드',
	`stat_code`	INT(11) NULL COMMENT '능력 코드',
	PRIMARY KEY (`user_no`),
	KEY `user_auth_code` (`auth_code`),
	KEY `user_style_code` (`style_code`),
	KEY `user_stat_code` (`stat_code`),
	CONSTRAINT `user_auth_code` FOREIGN KEY (`auth_code`) REFERENCES `user_authority` (`auth_code`) ON DELETE NO ACTION ON UPDATE CASCADE,
	CONSTRAINT `user_style_code` FOREIGN KEY (`style_code`) REFERENCES `user_style` (`style_code`) ON DELETE NO ACTION ON UPDATE CASCADE,
	CONSTRAINT `user_stat_code` FOREIGN KEY (`stat_code`) REFERENCES `user_stat` (`stat_code`) ON DELETE NO ACTION ON UPDATE CASCADE
)COMMENT '회원';

CREATE TABLE IF NOT EXISTS `local_account` (
	`local_no` INT(11) NOT NULL AUTO_INCREMENT COMMENT '로컬 계정 번호',
	`user_no` INT(11) NOT NULL COMMENT '회원 번호',
	`user_id` VARCHAR(255) NOT NULL COMMENT '회원 아이디',
	`user_pwd` VARCHAR(255)	NOT NULL COMMENT '회원 비밀번호',
	PRIMARY KEY (`local_no`),
	KEY `local_user_no`(`user_no`),
	CONSTRAINT `local_user_no` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`) ON DELETE NO ACTION ON UPDATE CASCADE
)COMMENT '로컬계정';

CREATE TABLE IF NOT EXISTS `user_mod_history` (
	`mod_no` INT(11) NOT NULL AUTO_INCREMENT COMMENT '수정 이력 번호',
	`user_no` INT(11) NOT NULL COMMENT '회원 번호',
	`mod_user_name` VARCHAR(20)	NOT NULL COMMENT '회원 성명',
	`mod_user_birth` DATE NOT NULL COMMENT '회원 생년월일',
	`mod_user_phone` VARCHAR(15) NOT NULL COMMENT '회원 전화번호',
	`mod_user_post_code` VARCHAR(10) NOT NULL COMMENT '우편번호',
	`mod_user_addr`	VARCHAR(100) NOT NULL COMMENT '주소',
	`mod_user_detail_addr` VARCHAR(100)	NOT NULL COMMENT '상세주소',
	`mod_ori_pic_name` VARCHAR(255) NULL COMMENT '원본 프로필 사진명',
	`mod_new_pic_name` VARCHAR(255) NULL COMMENT '저장 프로필 사진명',
	`mod_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '수정일자',
	`mod_user_exit_status` VARCHAR(1) NOT NULL DEFAULT 'N' COMMENT '탈퇴여부',
	`mod_user_exit_date` TIMESTAMP NULL COMMENT '탈퇴일시',
	`mod_user_fail_status` VARCHAR(1) NOT NULL DEFAULT 'N' COMMENT '로그인 잠금 여부',
	`mod_user_fail_cnt`	INT(2) NOT NULL DEFAULT 0 COMMENT '로그인 실패 횟수',
	`mod_auth_code`	VARCHAR(10)	NOT NULL COMMENT '권한 코드',
	`mod_user_code`	VARCHAR(10)	NOT NULL COMMENT '회원 코드',
	`mod_user_gender` VARCHAR(1) NOT NULL COMMENT '성별',
	`mod_user_comment` VARCHAR(255)	NULL COMMENT '소개',
	`mod_stat_code` INT(11) NULL COMMENT '스타일 코드',
	`mod_style_code`	INT(11) NULL COMMENT '능력 코드',
	PRIMARY KEY (`mod_no`),
	KEY `mod_user_no` (`user_no`),
	KEY `mod_user_auth_code` (`mod_auth_code`),
	KEY `mod_user_stat_code` (`mod_stat_code`),
	KEY `mod_user_style_code` (`mod_style_code`),
	CONSTRAINT `mod_user_no` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`) ON DELETE NO ACTION ON UPDATE CASCADE,
	CONSTRAINT `mod_user_auth_code` FOREIGN KEY (`mod_auth_code`) REFERENCES `user_authority` (`auth_code`) ON DELETE NO ACTION ON UPDATE CASCADE,
	CONSTRAINT `mod_user_style_code` FOREIGN KEY (`mod_stat_code`) REFERENCES `user_style` (`style_code`) ON DELETE NO ACTION ON UPDATE CASCADE,
	CONSTRAINT `mod_user_stat_code` FOREIGN KEY (`mod_style_code`) REFERENCES `user_stat` (`stat_code`) ON DELETE NO ACTION ON UPDATE CASCADE
)COMMENT '회원 정보 수정 이력';

CREATE TABLE IF NOT EXISTS social_account (
    social_no INT(11) AUTO_INCREMENT COMMENT '소셜 계정 번호',
    user_no INT(11) NOT NULL COMMENT '회원 번호',
    provider VARCHAR(20) NOT NULL COMMENT '인증 제공자',
    provider_id VARCHAR(100) NOT NULL COMMENT '인증키',
    PRIMARY KEY (`social_no`),
    UNIQUE KEY uk_provider_user (provider, provider_id),
    CONSTRAINT fk_social_user FOREIGN KEY (user_no) REFERENCES user(user_no)
)COMMENT '소셜 계정';

CREATE TABLE IF NOT EXISTS `user_relation` (
	`relation_no`	INT(11)	AUTO_INCREMENT,
	`from_user_no`	INT(11)	NOT NULL COMMENT '요청자',
	`to_user_no`	INT(11)	NOT NULL COMMENT '요청대상',
	`relation_status`	VARCHAR(255) NOT NULL DEFAULT 'WAIT',
	`relation_req_date`	TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '친구 신청 일자',
	`relation_prc_date`	TIMESTAMP NULL COMMENT '처리 일자',
	PRIMARY KEY (`relation_no`),
	UNIQUE KEY uniq_user_pair (from_user_no, to_user_no),
	KEY `req_from_user_no` (`from_user_no`),
	KEY `req_to_user_no` (`to_user_no`),
	CONSTRAINT `req_from_user_no` FOREIGN KEY (`from_user_no`) REFERENCES `user` (`user_no`) ON DELETE NO ACTION ON UPDATE CASCADE,
	CONSTRAINT `req_to_user_no` FOREIGN KEY (`to_user_no`) REFERENCES `user` (`user_no`) ON DELETE NO ACTION ON UPDATE CASCADE
);

-- 클럽 테이블 (tbl_club)
CREATE TABLE tbl_club (
club_id INT(11) NOT NULL AUTO_INCREMENT,
club_name VARCHAR(50) NOT NULL,
logo_path VARCHAR(255) DEFAULT NULL,
club_level ENUM('브론즈','실버','골드','플래티넘','다이아') NOT NULL DEFAULT '브론즈',
ranking INT(11) DEFAULT NULL,
win_count INT(11) DEFAULT NULL,
draw_count INT(11) DEFAULT NULL,
loss_count INT(11) DEFAULT NULL,
description TEXT DEFAULT NULL,
created_at DATETIME DEFAULT CURRENT_TIMESTAMP(),
team_code VARCHAR(40) DEFAULT NULL,
user_no INT(11) NOT NULL,
PRIMARY KEY (club_id),
UNIQUE KEY uq_club_name (club_name),
UNIQUE KEY uq_team_code (team_code),
KEY fk_club_user (user_no),
CONSTRAINT fk_club_user FOREIGN KEY (user_no) REFERENCES user (user_no)
);


-- 모집 게시판 테이블 (tbl_recruit_board)
CREATE TABLE tbl_recruit_board (
bno INT(11) NOT NULL AUTO_INCREMENT,
club_id INT(11) NOT NULL,
user_no INT(11) NOT NULL,
title VARCHAR(100) NOT NULL,
content TEXT NOT NULL,
reg_date DATETIME DEFAULT CURRENT_TIMESTAMP(),
modified_date DATETIME DEFAULT CURRENT_TIMESTAMP() ON UPDATE CURRENT_TIMESTAMP(),
view_count INT(11) DEFAULT 0,
is_closed TINYINT(1) DEFAULT 0,
PRIMARY KEY (bno),
KEY idx_club_id (club_id),
KEY idx_user_no (user_no),
CONSTRAINT fk_recruit_club FOREIGN KEY (club_id) REFERENCES tbl_club (club_id) ON DELETE CASCADE,
CONSTRAINT fk_recruit_user FOREIGN KEY (user_no) REFERENCES user (user_no) ON DELETE CASCADE
);


--클럽 지원 테이블 (tbl_club_apply)
CREATE TABLE tbl_club_apply (
apply_id INT(11) NOT NULL AUTO_INCREMENT,
bno INT(11) NOT NULL,
appli_user_no INT(11) DEFAULT NULL,
apply_date DATETIME DEFAULT CURRENT_TIMESTAMP(),
status ENUM('pending','approved','rejected','canceled','withdrawn','kicked') DEFAULT NULL,
club_id INT(11) NOT NULL,
PRIMARY KEY (apply_id),
UNIQUE KEY uniq_bno_applicant (bno, appli_user_no),
KEY idx_user_no (appli_user_no),
KEY fk_club_apply_club (club_id),
CONSTRAINT fk_club_apply_bno FOREIGN KEY (bno) REFERENCES tbl_recruit_board (bno) ON DELETE CASCADE,
CONSTRAINT fk_club_apply_club FOREIGN KEY (club_id) REFERENCES tbl_club (club_id) ON DELETE CASCADE,
CONSTRAINT fk_club_apply_user FOREIGN KEY (appli_user_no) REFERENCES user (user_no) ON DELETE CASCADE
);


--클럽 멤버 테이블(tbl_club_member)
CREATE TABLE tbl_club_member (
club_id INT(11) NOT NULL,
user_no INT(11) NOT NULL,
POM INT(11) DEFAULT 0,
match_count INT(11) DEFAULT 0,
role ENUM('LEADER','MEMBER') DEFAULT 'MEMBER',
joined_at DATETIME DEFAULT CURRENT_TIMESTAMP(),
left_at DATETIME DEFAULT NULL,
PRIMARY KEY (club_id, user_no),
KEY fk_club_member_user (user_no),
CONSTRAINT fk_club_member_club FOREIGN KEY (club_id) REFERENCES tbl_club (club_id),
CONSTRAINT fk_club_member_user FOREIGN KEY (user_no) REFERENCES user (user_no)
);



-- 클럽 상세 정보 테이블 (tbl_club_info)
CREATE TABLE tbl_club_info (
club_id INT(11) NOT NULL,
gender ENUM('남성','여성','혼성') NOT NULL,
age_group ENUM('10~20','20~30','30~40','40~50') NOT NULL,
active_days VARCHAR(20) NOT NULL,
active_times VARCHAR(50) NOT NULL,
PRIMARY KEY (club_id),
CONSTRAINT tbl_club_info_ibfk_1 FOREIGN KEY (club_id) REFERENCES tbl_club (club_id)
);


-- 📌 알림 테이블 생성 DDL
CREATE TABLE `tbl_alarm` (
  `alarm_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '알림 PK',
  `receiver_id` INT(11) NOT NULL COMMENT '알림 수신자(user_no, FK)',
  `sender_id` INT(11) DEFAULT NULL COMMENT '알림 발신자(user_no, FK)',
  `type` VARCHAR(30) NOT NULL COMMENT '알림 타입',
  `message` TEXT NOT NULL COMMENT '알림 내용',
  `url` VARCHAR(255) DEFAULT NULL COMMENT '클릭 이동 경로',
  `read_yn` CHAR(1) NOT NULL DEFAULT 'N' COMMENT '읽음 여부',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
  PRIMARY KEY (`alarm_id`),
  KEY `alarm_receiver_id` (`receiver_id`),
  KEY `alarm_sender_id` (`sender_id`),
  CONSTRAINT `alarm_receiver_id` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`user_no`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `alarm_sender_id` FOREIGN KEY (`sender_id`) REFERENCES `user` (`user_no`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
) ENGINE=InnoDB
  AUTO_INCREMENT=17
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_uca1400_ai_ci
  COMMENT='알림';

CREATE TABLE `stadium` (
  `SVCID` varchar(30) NOT NULL,
  `SVCNM` varchar(50) DEFAULT NULL,
  `PLACENM` varchar(100) DEFAULT NULL,
  `X` varchar(100) DEFAULT NULL,
  `Y` varchar(100) DEFAULT NULL,
  `IMG_PATH` varchar(255) DEFAULT NULL,
  `TELNO` varchar(100) DEFAULT NULL,
  `V_MIN` varchar(100) DEFAULT NULL,
  `V_MAX` varchar(100) DEFAULT NULL,
  `NOTICE` text,
  `SUBPLACENM` varchar(255) DEFAULT NULL,
  `ORGNM` varchar(100) DEFAULT NULL,
  `SVCENDTELNO` varchar(100) DEFAULT NULL,
  `AREANM` varchar(100) DEFAULT NULL,
  `ADRES` varchar(255) DEFAULT NULL,
  `DTLCONT` longtext,
  `state` varchar(10) DEFAULT NULL,
  `price` int NOT NULL DEFAULT '100000',
  PRIMARY KEY (`SVCID`)
);

CREATE TABLE `stadium_patch` (
  `SVCID` varchar(30) NOT NULL,
  `SVCNM` varchar(50) DEFAULT NULL,
  `PLACENM` varchar(100) DEFAULT NULL,
  `X` varchar(100) DEFAULT NULL,
  `Y` varchar(100) DEFAULT NULL,
  `IMG_PATH` varchar(255) DEFAULT NULL,
  `TELNO` varchar(100) DEFAULT NULL,
  `V_MIN` varchar(100) DEFAULT NULL,
  `V_MAX` varchar(100) DEFAULT NULL,
  `NOTICE` text,
  `SUBPLACENM` varchar(255) DEFAULT NULL,
  `ORGNM` varchar(100) DEFAULT NULL,
  `SVCENDTELNO` varchar(100) DEFAULT NULL,
  `AREANM` varchar(100) DEFAULT NULL,
  `ADRES` varchar(255) DEFAULT NULL,
  `DTLCONT` longtext,
  PRIMARY KEY (`SVCID`)
);

CREATE TABLE `stadium_comment` (
  `comment_no` int NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `regist_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_date` datetime DEFAULT NULL,
  `status` varchar(10) NOT NULL DEFAULT 'true',
  `svcid` varchar(30) DEFAULT NULL,
  `userid` varchar(100) NOT NULL,
  `rating` decimal(2,1) NOT NULL,
  PRIMARY KEY (`comment_no`),
  KEY `fk_comment_stadium` (`svcid`),
  CONSTRAINT `fk_comment_stadium` FOREIGN KEY (`svcid`) REFERENCES `stadium` (`SVCID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_stadium_comment_to_stadium` FOREIGN KEY (`svcid`) REFERENCES `stadium` (`SVCID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `chk_rating_range` CHECK (((`rating` >= 0) and (`rating` <= 5)))
);

CREATE TABLE `stadium_api_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `svcid` varchar(30) DEFAULT NULL,
  `code` varchar(50) DEFAULT NULL,
  `message` text,
  `started_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);

CREATE TABLE `reservation_slot` (
  `slot_id` bigint NOT NULL AUTO_INCREMENT,
  `svcid` varchar(50) NOT NULL,
  `slot_date` date NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  PRIMARY KEY (`slot_id`),
  KEY `fk_reservation_stadium` (`svcid`),
  CONSTRAINT `fk_reservation_stadium` FOREIGN KEY (`svcid`) REFERENCES `stadium` (`SVCID`) ON DELETE CASCADE
);

-- kosa_db.board definition


CREATE TABLE `board` (
  `board_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_no` int(11) NOT NULL,
  `user_name` varchar(20) NOT NULL,
  `board_title` varchar(100) NOT NULL,
  `board_content` mediumtext NOT NULL,
  `board_category` varchar(10) NOT NULL,
  `board_created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `board_modified_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  `board_status` varchar(10) NOT NULL DEFAULT 'active',
  `board_viewcount` int(11) NOT NULL DEFAULT 0,
  `board_likecount` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`board_id`),
  KEY `fk_board_user` (`user_no`),
  CONSTRAINT `fk_board_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`)
);

-- kosa_db.board_file definition

CREATE TABLE `board_file` (
  `file_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `board_id` bigint(20) NOT NULL,
  `file_original_name` varchar(255) NOT NULL,
  `file_saved_name` varchar(255) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `file_size` bigint(20) NOT NULL,
  `file_type` varchar(100) NOT NULL,
  `file_created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `file_status` varchar(50) NOT NULL DEFAULT 'active',
  PRIMARY KEY (`file_id`),
  KEY `board_id` (`board_id`),
  CONSTRAINT `board_file_ibfk_1` FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`)
);

-- kosa_db.board_like definition

CREATE TABLE `board_like` (
  `board_id` bigint(20) NOT NULL,
  `user_no` int(11) NOT NULL,
  PRIMARY KEY (`board_id`,`user_no`),
  KEY `fk_board_like_user` (`user_no`),
  CONSTRAINT `board_like_ibfk_1` FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`),
  CONSTRAINT `fk_board_like_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`)
);

-- kosa_db.location definition

CREATE TABLE `location` (
  `weather_location` varchar(50) NOT NULL,
  `weather_location_x` varchar(50) DEFAULT NULL,
  `weather_location_y` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`weather_location`)
);

INSERT INTO location (weather_location, weather_location_x, weather_location_y) VALUES
('강남구', '127.047059839521', '37.5179681611717'),
('강동구', '127.12379233466', '37.530160973856'),
('강북구', '127.025449504014', '37.6391856183931'),
('강서구', '126.849532173376', '37.5509655144007'),
('관악구', '126.951501244173', '37.4782106746327'),
('광진구', '127.087753081994', '37.5362819442695'),
('구로구', '126.888292375229', '37.4955054632154'),
('금천구', '126.896036850324', '37.4570656519531'),
('노원구', '127.056268317802', '37.6545228397157'),
('도봉구', '127.047131400119', '37.6687161285201'),
('동대문구', '127.039896580148', '37.5743917161622'),
('동작구', '126.939942092863', '37.51252777344'),
('마포구', '126.901615668932', '37.5663128370109'),
('서대문구', '126.936759175119', '37.5791546257808'),
('서초구', '127.032683002019', '37.4836248649455'),
('성동구', '127.036964999975', '37.5634225092469'),
('성북구', '127.016690019544', '37.5894551333062'),
('송파구', '127.105859984389', '37.514477182474'),
('양천구', '126.866542541936', '37.5170753784215'),
('영등포구', '126.896367130558', '37.525963157053'),
('용산구', '126.990478820837', '37.5324522944579'),
('은평구', '126.928822870137', '37.6024574203071'),
('종로구', '126.978988255925', '37.5735051436739'),
('중구', '126.998009728978', '37.5641201543296'),
('중랑구', '127.09272484193', '37.6065635856848');

-- kosa_db.`match` definition

CREATE TABLE `match` (
  `match_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `match_title` varchar(100) NOT NULL,
  `match_created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `match_modified_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `user_no` int(11) NOT NULL,
  `manager_no` int(11) NOT NULL,
  `match_date` datetime NOT NULL,
  `SVCID` varchar(30) NOT NULL,
  `match_closed` varchar(50) NOT NULL DEFAULT 'active',
  `gender_condition` varchar(50) NOT NULL,
  `match_description` text DEFAULT NULL,
  `match_status` varchar(50) NOT NULL DEFAULT 'waiting',
  `match_board_status` varchar(50) NOT NULL DEFAULT 'active',
  `match_code` varchar(50) NOT NULL,
  `reservation_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`match_id`),
  KEY `fk_match_user` (`user_no`),
  KEY `fk_match_manager` (`manager_no`),
  KEY `fk_match_stadium` (`SVCID`),
  KEY `fk_match_reservation` (`reservation_id`),
  CONSTRAINT `fk_match_manager` FOREIGN KEY (`manager_no`) REFERENCES `user` (`user_no`),
  CONSTRAINT `fk_match_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`reservation_id`),
  CONSTRAINT `fk_match_stadium` FOREIGN KEY (`SVCID`) REFERENCES `stadium` (`SVCID`),
  CONSTRAINT `fk_match_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`)
);

-- kosa_db.match_log definition

CREATE TABLE `match_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `match_id` bigint(20) NOT NULL,
  `log_type` varchar(50) NOT NULL,
  `club_id` int(11) DEFAULT NULL,
  `user_no` int(11) DEFAULT NULL,
  `log_created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `log_modified_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  `log_memo` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`log_id`),
  KEY `fk_log_match` (`match_id`),
  KEY `fk_log_club` (`club_id`),
  KEY `fk_log_user` (`user_no`),
  CONSTRAINT `fk_log_club` FOREIGN KEY (`club_id`) REFERENCES `tbl_club` (`club_id`),
  CONSTRAINT `fk_log_match` FOREIGN KEY (`match_id`) REFERENCES `match` (`match_id`),
  CONSTRAINT `fk_log_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`)
);

-- kosa_db.match_participant definition

CREATE TABLE `match_participant` (
  `match_id` bigint(20) NOT NULL,
  `club_id` int(11) DEFAULT NULL,
  `user_no` int(11) NOT NULL,
  `user_role` varchar(50) NOT NULL DEFAULT 'member',
  `user_status` varchar(50) NOT NULL DEFAULT 'apply',
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `modified_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`match_id`,`user_no`),
  KEY `fk_participant_club` (`club_id`),
  KEY `fk_participant_user` (`user_no`),
  CONSTRAINT `fk_participant_club` FOREIGN KEY (`club_id`) REFERENCES `tbl_club` (`club_id`),
  CONSTRAINT `fk_participant_match` FOREIGN KEY (`match_id`) REFERENCES `match` (`match_id`),
  CONSTRAINT `fk_participant_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`)
);

-- kosa_db.reply definition

CREATE TABLE `reply` (
  `reply_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `board_id` bigint(20) NOT NULL,
  `parent_reply_id` bigint(20) DEFAULT NULL,
  `user_no` int(11) NOT NULL,
  `user_name` varchar(20) NOT NULL,
  `reply_content` text NOT NULL,
  `reply_created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `reply_modified_at` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  `reply_status` varchar(50) NOT NULL DEFAULT 'active',
  PRIMARY KEY (`reply_id`),
  KEY `board_id` (`board_id`),
  KEY `parent_reply_id` (`parent_reply_id`),
  KEY `fk_reply_user` (`user_no`),
  CONSTRAINT `fk_reply_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`),
  CONSTRAINT `reply_ibfk_1` FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`),
  CONSTRAINT `reply_ibfk_2` FOREIGN KEY (`parent_reply_id`) REFERENCES `reply` (`reply_id`)
);

-- kosa_db.weather definition

CREATE TABLE `weather` (
  `weather_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `weather_region` varchar(50) DEFAULT NULL,
  `weather_base_date` varchar(8) DEFAULT NULL,
  `weather_base_time` varchar(4) DEFAULT NULL,
  `weather_fcst_date` varchar(8) DEFAULT NULL,
  `weather_fcst_time` varchar(4) DEFAULT NULL,
  `weather_code` varchar(3) DEFAULT NULL,
  `weather_value` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`weather_id`),
  KEY `weather_location` (`weather_region`),
  CONSTRAINT `weather_ibfk_1` FOREIGN KEY (`weather_region`) REFERENCES `location` (`weather_location`)
);

CREATE TABLE `reservation` (
  `reservation_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `slot_id` bigint(20) NOT NULL,
  `reservation_type` enum('social','match') NOT NULL,
  `reserved_at` datetime DEFAULT current_timestamp(),
  `user_no` int(11) NOT NULL,
  `status` enum('reserved','cancelled','expired') DEFAULT 'reserved',
  `cancelled_at` datetime DEFAULT NULL,
  `price` int(11) NOT NULL,
  `board_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`reservation_id`),
  KEY `fk_reservation_user` (`user_no`),
  KEY `fk_slot` (`slot_id`),
  KEY `fk_reservation_board` (`board_id`),
  CONSTRAINT `fk_reservation_board` FOREIGN KEY (`board_id`) REFERENCES `board` (`board_id`),
  CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`) ON DELETE CASCADE,
  CONSTRAINT `fk_slot` FOREIGN KEY (`slot_id`) REFERENCES `reservation_slot` (`slot_id`)
); 

CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '고유 PK',
  `reservation_id` bigint NOT NULL COMMENT '예약ID FK',
  `amount` int NOT NULL COMMENT '가격',
  `status` enum('pending','paid','failed','refunded') DEFAULT 'pending' COMMENT '결제상태',
  `paid_at` datetime DEFAULT NULL COMMENT '결제 승인 시간',
  `method` varchar(50) DEFAULT NULL COMMENT '결제수단',
  `tid` varchar(50)  DEFAULT NULL COMMENT '카카오TID',
  `user_no` int NOT NULL COMMENT '결제 유저FK',
  `create_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '결제 시간',
  PRIMARY KEY (`id`),
  KEY `fk_payment_reservation` (`reservation_id`),
  KEY `fk_payment_user` (`user_no`),
  CONSTRAINT `fk_payment_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`reservation_id`),
  CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_no`) REFERENCES `user` (`user_no`) ON DELETE RESTRICT ON UPDATE CASCADE
);


DELIMITER $$

CREATE PROCEDURE `kosa_db`.`sync_stadium_state`()
BEGIN

    INSERT INTO stadium (
        SVCID, SVCNM, PLACENM, X, Y, IMG_PATH, TELNO,
        V_MIN, V_MAX, NOTICE, SUBPLACENM, ORGNM,
        SVCENDTELNO, AREANM, ADRES, DTLCONT, STATE, price
    )
    SELECT 
        sp.SVCID, sp.SVCNM, sp.PLACENM, sp.X, sp.Y, sp.IMG_PATH, sp.TELNO,
        sp.V_MIN, sp.V_MAX, sp.NOTICE, sp.SUBPLACENM, sp.ORGNM,
        sp.SVCENDTELNO, sp.AREANM, sp.ADRES, sp.DTLCONT, 'new', 100000
    FROM stadium_patch sp
    LEFT JOIN stadium s ON sp.SVCID = s.SVCID
    WHERE s.SVCID IS NULL;

    UPDATE stadium s
    LEFT JOIN stadium_patch sp ON s.SVCID = sp.SVCID
    SET s.STATE = 'delete'
    WHERE sp.SVCID IS NULL;

    UPDATE stadium s
    JOIN stadium_patch sp ON s.SVCID = sp.SVCID
    SET s.STATE = 'update'
    WHERE (
        IFNULL(s.SVCNM, '')       != IFNULL(sp.SVCNM, '') OR
        IFNULL(s.PLACENM, '')     != IFNULL(sp.PLACENM, '') OR
        IFNULL(s.X, 0)            != IFNULL(sp.X, 0) OR
        IFNULL(s.Y, 0)            != IFNULL(sp.Y, 0) OR
        IFNULL(s.IMG_PATH, '')    != IFNULL(sp.IMG_PATH, '') OR
        IFNULL(s.TELNO, '')       != IFNULL(sp.TELNO, '') OR
        IFNULL(s.V_MIN, '')       != IFNULL(sp.V_MIN, '') OR
        IFNULL(s.V_MAX, '')       != IFNULL(sp.V_MAX, '') OR
        IFNULL(s.NOTICE, '')      != IFNULL(sp.NOTICE, '') OR
        IFNULL(s.SUBPLACENM, '')  != IFNULL(sp.SUBPLACENM, '') OR
        IFNULL(s.ORGNM, '')       != IFNULL(sp.ORGNM, '') OR
        IFNULL(s.SVCENDTELNO, '') != IFNULL(sp.SVCENDTELNO, '') OR
        IFNULL(s.AREANM, '')      != IFNULL(sp.AREANM, '') OR
        IFNULL(s.ADRES, '')       != IFNULL(sp.ADRES, '') OR
        IFNULL(s.DTLCONT, '')     != IFNULL(sp.DTLCONT, '')
    );
    
    UPDATE stadium s
    JOIN stadium_patch sp ON s.SVCID = sp.SVCID
    SET s.STATE = 'same'
    WHERE s.STATE IS NULL;

    TRUNCATE TABLE stadium_patch;
    
    -- 슬롯 생성 프로시저 호출
CALL generate_reservation_slots();

END $$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE `kosa_db`.`generate_reservation_slots`()
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE v_svcid VARCHAR(50);
  DECLARE v_vmin TIME;
  DECLARE v_vmax TIME;

  DECLARE cur CURSOR FOR SELECT svcid, v_min, v_max FROM stadium;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
  
    -- 슬롯 중복 방지를 위해 오늘 이후 모든 슬롯 제거
  DELETE FROM reservation_slot WHERE slot_date >= CURDATE();
    

  SET @base_date = CURDATE();  -- 오늘 날짜 기준
  SET @days_ahead = 7;

  OPEN cur;

  read_loop: LOOP
    FETCH cur INTO v_svcid, v_vmin, v_vmax;
    IF done THEN
      LEAVE read_loop;
    END IF;

    SET @i = 0;
    day_loop: WHILE @i < @days_ahead DO
      SET @target_date = DATE_ADD(@base_date, INTERVAL @i DAY);
      SET @current_time = v_vmin;

      WHILE ADDTIME(@current_time, '02:00:00') <= v_vmax DO
        INSERT INTO reservation_slot (svcid, slot_date, start_time, end_time)
        VALUES (
          v_svcid,
          @target_date,
          @current_time,
          ADDTIME(@current_time, '02:00:00')
        );
        SET @current_time = ADDTIME(@current_time, '02:00:00');
      END WHILE;

      SET @i = @i + 1;
    END WHILE;

  END LOOP;
  
	CLOSE cur;
END $$
DELIMITER ;



