-- 주차장 기본 정보 테이블
CREATE TABLE IF NOT EXISTS parking_info (
    id VARCHAR(50) PRIMARY KEY COMMENT '주차장 ID',
    parking_name VARCHAR(100) NOT NULL COMMENT '주차장 이름',
    rdnmadr VARCHAR(200) COMMENT '도로명 주소',
    latitude DOUBLE DEFAULT 0 COMMENT '위도',
    longitude DOUBLE DEFAULT 0 COMMENT '경도',
    parking_lots_count INT DEFAULT 0 COMMENT '주차 면수',
    sync_date DATETIME COMMENT '동기화 일시'
) COMMENT '주차장 기본 정보';

-- 주차장 운영 정보 테이블
CREATE TABLE IF NOT EXISTS parking_operation (
    id VARCHAR(50) PRIMARY KEY COMMENT '주차장 ID',
    free_time INT DEFAULT 0 COMMENT '무료 주차 시간(분)',
    sync_date DATETIME COMMENT '동기화 일시',
    FOREIGN KEY (id) REFERENCES parking_info(id)
) COMMENT '주차장 운영 정보';

-- 주차장 실시간 정보 테이블
CREATE TABLE IF NOT EXISTS parking_realtime (
    id VARCHAR(50) PRIMARY KEY COMMENT '주차장 ID',
    pkfc_parking_lots_total INT DEFAULT 0 COMMENT '전체 주차 면수',
    pkfc_available_parking_lots_total INT DEFAULT 0 COMMENT '가용 주차 면수',
    updated_at DATETIME COMMENT '정보 갱신 시간',
    sync_date DATETIME COMMENT '동기화 일시',
    FOREIGN KEY (id) REFERENCES parking_info(id)
) COMMENT '주차장 실시간 정보'; 