-- ParkingHub 초기 데이터
-- 기본 역할 및 관리자 계정 생성

-- 초기 역할 데이터 삽입
INSERT INTO ROLE (id, name, description) VALUES 
(1, 'ROLE_USER', '일반 사용자'),
(2, 'ROLE_ADMIN', '관리자'),
(3, 'ROLE_MANAGER', '매니저');

-- 관리자 계정 생성 (비밀번호: admin1234 - BCrypt 암호화 적용)
INSERT INTO USER (email, username, password, role_id) VALUES 
('admin@parkinghub.com', 'admin', '$2a$10$8YaV9.a.dL5jLdxLYjClJuoJ7vYWcZ.WYq7WH8q7eRkONO.KDJFK6', 2); 