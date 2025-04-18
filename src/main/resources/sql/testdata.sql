-- 테스트 데이터 삽입을 위한 SQL 스크립트
-- 기존 데이터 삭제
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE FAVORITE_PARKING;
TRUNCATE TABLE PARKING_REALTIME;
TRUNCATE TABLE PARKING_OPERATION;
TRUNCATE TABLE PARKING_INFO;
TRUNCATE TABLE USER;
TRUNCATE TABLE ROLE;

SET FOREIGN_KEY_CHECKS = 1;

-- 역할 데이터
INSERT INTO ROLE (id, name, description) VALUES 
(1, 'ROLE_USER', '일반 사용자'),
(2, 'ROLE_ADMIN', '관리자'),
(3, 'ROLE_MANAGER', '매니저');

-- 사용자 데이터
-- 비밀번호: admin1234
INSERT INTO USER (email, username, password, phone_number, role_id) VALUES 
('admin@parkinghub.com', 'admin', '$2a$10$8YaV9.a.dL5jLdxLYjClJuoJ7vYWcZ.WYq7WH8q7eRkONO.KDJFK6', '010-1234-5678', 2);

-- 비밀번호: user1234
INSERT INTO USER (email, username, password, phone_number, role_id) VALUES 
('user@parkinghub.com', 'testuser', '$2a$10$UXfMD1YcEPf9XEexxFrRouREUGBpKEGSRGdg8qP01aGUN0zqVm0CC', '010-9876-5432', 1);

-- 주차장 기본 데이터 (10개)
INSERT INTO PARKING_INFO (prk_center_id, prk_plce_nm, prk_plce_adres, prk_plce_entrc_la, prk_plce_entrc_lo, prk_cmprt_co) VALUES 
('P001', '서울시청 지하주차장', '서울특별시 중구 세종대로 110', 37.5668, 126.9787, 350),
('P002', '남산공원 주차장', '서울특별시 중구 소파로 46', 37.5512, 126.9882, 200),
('P003', '강남역 공영주차장', '서울특별시 강남구 테헤란로 101', 37.4979, 127.0276, 250),
('P004', '코엑스 주차장', '서울특별시 강남구 봉은사로 524', 37.5115, 127.0586, 1500),
('P005', '올림픽공원 주차장', '서울특별시 송파구 올림픽로 424', 37.5202, 127.1214, 800),
('P006', '롯데월드 주차장', '서울특별시 송파구 올림픽로 240', 37.5115, 127.0980, 1200),
('P007', '여의도 공영주차장', '서울특별시 영등포구 여의동로 330', 37.5256, 126.9240, 300),
('P008', '한강공원 주차장', '서울특별시 마포구 마포나루길 467', 37.5352, 126.9429, 150),
('P009', '북한산 국립공원 주차장', '서울특별시 강북구 삼양로173길 504', 37.6613, 127.0049, 100),
('P010', '서울숲 주차장', '서울특별시 성동구 뚝섬로 273', 37.5446, 127.0379, 220);

-- 주차장 운영 정보
INSERT INTO PARKING_OPERATION (prk_center_id, opertn_bs_free_time) VALUES 
('P001', 30),   -- 30분 무료
('P002', 15),   -- 15분 무료
('P003', 0),    -- 무료시간 없음
('P004', 0),    -- 무료시간 없음
('P005', 60),   -- 1시간 무료
('P006', 0),    -- 무료시간 없음
('P007', 20),   -- 20분 무료
('P008', 120),  -- 2시간 무료
('P009', 180),  -- 3시간 무료
('P010', 60);   -- 1시간 무료

-- 주차장 실시간 정보
INSERT INTO PARKING_REALTIME (prk_center_id, pkfc_parking_lots_total, pkfc_available_parking_lots_total) VALUES 
('P001', 350, 120),   -- 350면 중 120면 이용 가능
('P002', 200, 80),    -- 200면 중 80면 이용 가능
('P003', 250, 30),    -- 250면 중 30면 이용 가능
('P004', 1500, 650),  -- 1500면 중 650면 이용 가능
('P005', 800, 500),   -- 800면 중 500면 이용 가능
('P006', 1200, 300),  -- 1200면 중 300면 이용 가능
('P007', 300, 150),   -- 300면 중 150면 이용 가능
('P008', 150, 100),   -- 150면 중 100면 이용 가능
('P009', 100, 70),    -- 100면 중 70면 이용 가능
('P010', 220, 140);   -- 220면 중 140면 이용 가능

-- 즐겨찾기 데이터
INSERT INTO FAVORITE_PARKING (user_id, prk_center_id, memo) VALUES 
(2, 'P001', '시청 근처로 갈 때 편리함'),
(2, 'P003', '강남역 회의 시 주차'),
(2, 'P004', '주말 쇼핑 시 이용'),
(2, 'P007', '여의도 방문 시 주차하기 좋음'),
(1, 'P005', '주말 가족 나들이 시 이용');

-- 샘플 데이터 생성 완료 메시지
SELECT 'Sample data inserted successfully!' AS message; 