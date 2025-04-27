-- ParkingHub 데이터 리셋 프로시저
-- V4: 주차장 데이터 초기화 프로시저 생성

DELIMITER //

CREATE PROCEDURE IF NOT EXISTS reset_parking_data()
BEGIN
    -- 외래키 제약조건 비활성화
    SET FOREIGN_KEY_CHECKS = 0;
    
    -- 주차장 관련 테이블 데이터 초기화
    TRUNCATE TABLE PARKING_REALTIME;
    TRUNCATE TABLE PARKING_OPERATION;
    TRUNCATE TABLE PARKING_INFO;
    
    -- 외래키 제약조건 활성화
    SET FOREIGN_KEY_CHECKS = 1;
    
    SELECT 'Parking data has been reset.' AS message;
END //

DELIMITER ;

-- 프로시저 사용법 주석
-- 데이터 리셋이 필요할 때 다음 명령어를 실행:
-- CALL reset_parking_data(); 