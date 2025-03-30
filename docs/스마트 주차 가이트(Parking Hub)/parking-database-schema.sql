-- 주차장 시설 정보 테이블 (API 캐싱용)
CREATE TABLE parking_facility (
    prk_center_id VARCHAR(30) PRIMARY KEY,              -- 주차장 관리 ID (API에서 제공)
    prk_plce_nm VARCHAR(100) NOT NULL,                  -- 주차장명
    prk_plce_adres VARCHAR(200) NOT NULL,               -- 주차장 도로명 주소
    prk_plce_entrc_la DOUBLE NOT NULL,                  -- 위도
    prk_plce_entrc_lo DOUBLE NOT NULL,                  -- 경도
    prk_cmprt_co INT NOT NULL,                          -- 주차장의 총 주차 구획 수
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    -- 마지막 업데이트 시간
    SPATIAL INDEX(prk_plce_entrc_la, prk_plce_entrc_lo) -- 공간 인덱스
);

-- 주차장 운영 정보 테이블 (API 캐싱용)
CREATE TABLE parking_operation (
    prk_center_id VARCHAR(30) PRIMARY KEY,                 -- 주차장 관리 ID
    sun_start_time VARCHAR(6),                            -- 일요일 운영 시작 시간
    sun_end_time VARCHAR(6),                              -- 일요일 운영 종료 시간
    mon_start_time VARCHAR(6),                            -- 월요일 운영 시작 시간
    mon_end_time VARCHAR(6),                              -- 월요일 운영 종료 시간
    tue_start_time VARCHAR(6),                            -- 화요일 운영 시작 시간
    tue_end_time VARCHAR(6),                              -- 화요일 운영 종료 시간
    wed_start_time VARCHAR(6),                            -- 수요일 운영 시작 시간
    wed_end_time VARCHAR(6),                              -- 수요일 운영 종료 시간
    thu_start_time VARCHAR(6),                            -- 목요일 운영 시작 시간
    thu_end_time VARCHAR(6),                              -- 목요일 운영 종료 시간
    fri_start_time VARCHAR(6),                            -- 금요일 운영 시작 시간
    fri_end_time VARCHAR(6),                              -- 금요일 운영 종료 시간
    sat_start_time VARCHAR(6),                            -- 토요일 운영 시작 시간
    sat_end_time VARCHAR(6),                              -- 토요일 운영 종료 시간
    hol_start_time VARCHAR(6),                            -- 공휴일 운영 시작 시간
    hol_end_time VARCHAR(6),                              -- 공휴일 운영 종료 시간
    opertn_bs_free_time INT,                              -- 주차장 기본 회차(기본무료)시간
    parking_chrge_bs_time INT,                            -- 기본 시간
    parking_chrge_bs_chrge INT,                           -- 기본 요금
    parking_chrge_adit_unit_time INT,                     -- 추가 단위 시간
    parking_chrge_adit_unit_chrge INT,                    -- 추가 단위 요금
    parking_chrge_one_day_chrge INT,                      -- 1일 요금(전일)
    parking_chrge_mon_unit_chrge INT,                     -- 월정액
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,      -- 마지막 업데이트 시간
    FOREIGN KEY (prk_center_id) REFERENCES parking_facility(prk_center_id) ON DELETE CASCADE
);

-- 주차장 실시간 정보 캐시 테이블 (API 실시간 정보 캐싱용)
CREATE TABLE parking_realtime_cache (
    prk_center_id VARCHAR(30) PRIMARY KEY,              -- 주차장 관리 ID
    pkfc_parking_lots_total INT,                        -- 주차장의 총 주차 구획 수
    pkfc_available_parking_lots_total INT,              -- 주차장의 총 주차가능 구획 수
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,    -- 마지막 업데이트 시간
    FOREIGN KEY (prk_center_id) REFERENCES parking_facility(prk_center_id) ON DELETE CASCADE
);

-- 사용자 테이블 (기존 유지)
-- user 테이블 그대로 유지 (id, email, nickname, created_at)

-- 즐겨찾기 테이블 (기존 유지, 외래키 추가)
CREATE TABLE favorite_parking (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50),  -- 비회원은 IP 기반
    parking_code VARCHAR(30) NOT NULL, -- prk_center_id와 연결
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parking_code) REFERENCES parking_facility(prk_center_id) ON DELETE CASCADE
);

-- 리뷰 테이블 (기존 유지, 외래키 추가)
CREATE TABLE review (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50), -- 비회원은 IP 기반
    parking_code VARCHAR(30) NOT NULL, -- prk_center_id와 연결
    rating DECIMAL(2,1) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parking_code) REFERENCES parking_facility(prk_center_id) ON DELETE CASCADE
);

-- 사용자 방문 기록 테이블 (기존 유지, 외래키 추가)
CREATE TABLE user_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50), -- 비회원은 IP 기반
    parking_code VARCHAR(30) NOT NULL, -- prk_center_id와 연결
    visited_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (parking_code) REFERENCES parking_facility(prk_center_id) ON DELETE CASCADE
);

-- 알림 설정 테이블 (기존 유지, 외래키 추가)
CREATE TABLE alert_setting (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50), -- 비회원은 IP 기반
    parking_code VARCHAR(30) NOT NULL, -- prk_center_id와 연결
    day_of_week VARCHAR(20), -- 요일 정보 (Mon,Tue,Wed...)
    hour VARCHAR(2), -- 시간 (00-23)
    min_free_slots INT, -- 최소 빈 주차 공간
    FOREIGN KEY (parking_code) REFERENCES parking_facility(prk_center_id) ON DELETE CASCADE
);

-- 게시글 테이블 (기존 유지)
-- post 테이블 그대로 유지 (id, user_id, title, content, views, created_at)

-- 댓글 테이블 (기존 유지)
-- comment 테이블 그대로 유지 (id, post_id, user_id, comment, created_at)

-- 관리자 테이블 (기존 유지)
-- admin 테이블 그대로 유지 (admin_id, password, created_at)
