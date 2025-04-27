-- 리뷰 테이블 생성
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prk_center_id VARCHAR(50) NOT NULL COMMENT '주차장 ID',
    user_id VARCHAR(50) NOT NULL COMMENT '사용자 ID',
    username VARCHAR(100) NOT NULL COMMENT '사용자 이름',
    rating INT NOT NULL COMMENT '평점 (1-5)',
    content TEXT NOT NULL COMMENT '리뷰 내용',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '작성 일시',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 일시',
    
    INDEX idx_prk_center_id (prk_center_id),
    INDEX idx_user_id (user_id),
    UNIQUE KEY uk_user_parking (user_id, prk_center_id) COMMENT '한 사용자가 한 주차장에 한 개의 리뷰만 작성 가능'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='주차장 리뷰 정보'; 