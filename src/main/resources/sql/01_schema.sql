-- ParkingHub 기본 스키마 정의
-- 테이블 구조 생성 스크립트

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
    prk_center_id VARCHAR(30) PRIMARY KEY,    -- 주차장 관리 ID (외부 API ID)
    prk_plce_nm VARCHAR(255) NOT NULL,        -- 주차장 이름
    prk_plce_adres VARCHAR(255),              -- 주차장 주소
    prk_plce_entrc_la DOUBLE,                 -- 주차장 입구 위도
    prk_plce_entrc_lo DOUBLE,                 -- 주차장 입구 경도
    prk_cmprt_co INT DEFAULT 0,               -- 총 주차 구획 수
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주차장 운영 기본 정보 테이블
CREATE TABLE IF NOT EXISTS PARKING_OPERATION (
    prk_center_id VARCHAR(30) PRIMARY KEY,     -- 주차장 관리 ID
    opertn_bs_free_time INT DEFAULT 0,         -- 기본 무료 시간 (분 단위)
    parking_chrge_bs_time VARCHAR(50),         -- 기본 주차 시간
    parking_chrge_bs_chrg VARCHAR(50),         -- 기본 주차 요금
    parking_chrge_adit_unit_time VARCHAR(50),  -- 추가 단위 시간
    parking_chrge_adit_unit_chrge VARCHAR(50), -- 추가 단위 요금
    parking_chrge_one_day_chrge VARCHAR(50),   -- 1일 주차 요금
    parking_chrge_mon_unit_chrge VARCHAR(50),  -- 월 정기권 요금
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (prk_center_id) REFERENCES PARKING_INFO(prk_center_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주차장 요일별 운영시간 테이블
CREATE TABLE IF NOT EXISTS PARKING_OPERATION_TIME (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prk_center_id VARCHAR(30) NOT NULL,         -- 주차장 관리 ID
    day_of_week ENUM('Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Holiday') NOT NULL, -- 요일
    opertn_start_time VARCHAR(6),               -- 운영 시작시간 (ex: '080000')
    opertn_end_time VARCHAR(6),                 -- 운영 종료시간 (ex: '200000')
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (prk_center_id) REFERENCES PARKING_INFO(prk_center_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 주차장 실시간 정보 테이블
CREATE TABLE IF NOT EXISTS PARKING_REALTIME (
    prk_center_id VARCHAR(30) PRIMARY KEY,                -- 주차장 관리 ID
    pkfc_parking_lots_total INT DEFAULT 0,                -- 총 주차 면수
    pkfc_available_parking_lots_total INT DEFAULT 0,      -- 이용 가능 주차 면수
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (prk_center_id) REFERENCES PARKING_INFO(prk_center_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 즐겨찾기 주차장 테이블
CREATE TABLE IF NOT EXISTS FAVORITE_PARKING (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,                            -- 사용자 ID
    prk_center_id VARCHAR(30) NOT NULL,                 -- 주차장 ID
    memo VARCHAR(500),                                  -- 즐겨찾기 메모
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_parking (user_id, prk_center_id),
    FOREIGN KEY (user_id) REFERENCES USER(id) ON DELETE CASCADE,
    FOREIGN KEY (prk_center_id) REFERENCES PARKING_INFO(prk_center_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
