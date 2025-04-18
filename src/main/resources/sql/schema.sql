-- ParkingHub 스키마 및 테이블 정의
-- MySQL 데이터베이스 기준

-- 데이터베이스 생성 (없는 경우)
CREATE DATABASE IF NOT EXISTS parking_hub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 데이터베이스 선택
USE parking_hub;

-- 기존 테이블 삭제 (역순으로 삭제)
DROP TABLE IF EXISTS FAVORITE_PARKING;
DROP TABLE IF EXISTS PARKING_REALTIME;
DROP TABLE IF EXISTS PARKING_OPERATION;
DROP TABLE IF EXISTS PARKING_INFO;
DROP TABLE IF EXISTS USER;
DROP TABLE IF EXISTS ROLE;

-- 역할 테이블
CREATE TABLE IF NOT EXISTS ROLE (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS USER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES ROLE(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주차장 기본 정보 테이블
CREATE TABLE IF NOT EXISTS PARKING_INFO (
    prk_center_id VARCHAR(30) PRIMARY KEY,    -- 주차장 관리 ID (외부 API에서 제공하는 ID)
    prk_plce_nm VARCHAR(255) NOT NULL,        -- 주차장 이름
    prk_plce_adres VARCHAR(255),              -- 주차장 주소
    prk_plce_entrc_la DOUBLE,                 -- 주차장 입구 위도
    prk_plce_entrc_lo DOUBLE,                 -- 주차장 입구 경도
    prk_cmprt_co INT DEFAULT 0,               -- 주차 구획 수 (총 주차 면수)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주차장 운영 정보 테이블
CREATE TABLE IF NOT EXISTS PARKING_OPERATION (
    prk_center_id VARCHAR(30) PRIMARY KEY,     -- 주차장 관리 ID
    opertn_bs_free_time INT DEFAULT 0,         -- 주차장 기본 무료 시간 (분 단위)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (prk_center_id) REFERENCES PARKING_INFO(prk_center_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주차장 실시간 정보 테이블
CREATE TABLE IF NOT EXISTS PARKING_REALTIME (
    prk_center_id VARCHAR(30) PRIMARY KEY,                -- 주차장 관리 ID
    pkfc_parking_lots_total INT DEFAULT 0,                -- 총 주차 면수
    pkfc_available_parking_lots_total INT DEFAULT 0,      -- 이용 가능 주차 면수
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,      -- 업데이트 시간
    FOREIGN KEY (prk_center_id) REFERENCES PARKING_INFO(prk_center_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 즐겨찾기 주차장 테이블
CREATE TABLE IF NOT EXISTS FAVORITE_PARKING (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,                            -- 사용자 ID
    prk_center_id VARCHAR(30) NOT NULL,                 -- 주차장 ID
    memo VARCHAR(500),                                  -- 메모
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_parking (user_id, prk_center_id),
    FOREIGN KEY (user_id) REFERENCES USER(id) ON DELETE CASCADE,
    FOREIGN KEY (prk_center_id) REFERENCES PARKING_INFO(prk_center_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 초기 역할 데이터 삽입
INSERT INTO ROLE (id, name, description) VALUES 
(1, 'ROLE_USER', '일반 사용자'),
(2, 'ROLE_ADMIN', '관리자'),
(3, 'ROLE_MANAGER', '매니저');

-- 관리자 계정 생성 (비밀번호: admin1234 - BCrypt 암호화 적용)
INSERT INTO USER (email, username, password, role_id) VALUES 
('admin@parkinghub.com', 'admin', '$2a$10$8YaV9.a.dL5jLdxLYjClJuoJ7vYWcZ.WYq7WH8q7eRkONO.KDJFK6', 2); 