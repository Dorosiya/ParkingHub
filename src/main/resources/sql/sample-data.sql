-- ParkingHub 샘플 데이터
-- 테스트 및 개발용

USE parking_hub;

-- 샘플 주차장 정보 추가
INSERT INTO PARKING_INFO (prk_center_id, prk_plce_nm, prk_plce_adres, prk_plce_entrc_la, prk_plce_entrc_lo, prk_cmprt_co) VALUES 
('P00001', '강남역 공영주차장', '서울특별시 강남구 강남대로 396', 37.4979, 127.0276, 200),
('P00002', '센트럴시티 주차장', '서울특별시 서초구 신반포로 176', 37.5045, 127.0047, 350),
('P00003', '종로 공영주차장', '서울특별시 종로구 종로17길 45', 37.5712, 126.9817, 120),
('P00004', '홍대입구역 공영주차장', '서울특별시 마포구 양화로 지하 160', 37.5571, 126.9242, 180),
('P00005', '잠실 롯데월드 주차장', '서울특별시 송파구 올림픽로 240', 37.5113, 127.0980, 500);

-- 샘플 주차장 운영 정보 추가
INSERT INTO PARKING_OPERATION (prk_center_id, opertn_bs_free_time) VALUES 
('P00001', 30),
('P00002', 15),
('P00003', 20),
('P00004', 10),
('P00005', 60);

-- 샘플 주차장 실시간 정보 추가
INSERT INTO PARKING_REALTIME (prk_center_id, pkfc_parking_lots_total, pkfc_available_parking_lots_total, updated_at) VALUES 
('P00001', 200, 45, NOW()),
('P00002', 350, 120, NOW()),
('P00003', 120, 35, NOW()),
('P00004', 180, 60, NOW()),
('P00005', 500, 230, NOW());

-- 샘플 사용자 추가 (비밀번호: user1234 - BCrypt 암호화 적용)
INSERT INTO USER (email, username, password, phone_number, role_id) VALUES 
('user1@example.com', 'user1', '$2a$10$8YaV9.a.dL5jLdxLYjClJuoJ7vYWcZ.WYq7WH8q7eRkONO.KDJFK6', '010-1234-5678', 1),
('user2@example.com', 'user2', '$2a$10$8YaV9.a.dL5jLdxLYjClJuoJ7vYWcZ.WYq7WH8q7eRkONO.KDJFK6', '010-2345-6789', 1),
('manager@example.com', 'manager', '$2a$10$8YaV9.a.dL5jLdxLYjClJuoJ7vYWcZ.WYq7WH8q7eRkONO.KDJFK6', '010-3456-7890', 3); 