# 업데이트된 스마트 주차 가이드 데이터베이스 테이블 명세서

## 1. 사용자(USER)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| id | int | 사용자 ID | PK | AUTO_INCREMENT |
| email | varchar(100) | 이메일 | | NOT NULL, UNIQUE |
| nickname | varchar(50) | 닉네임 | | NOT NULL |
| password | varchar(100) | 비밀번호 | | NOT NULL |
| created_at | datetime | 생성일시 | | DEFAULT CURRENT_TIMESTAMP |

## 2. 주차장 정보(PARKING_INFO)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| prk_center_id | varchar(50) | 주차장 관리 ID | PK | NOT NULL |
| prk_plce_nm | varchar(100) | 주차장명 | | NOT NULL |
| prk_plce_adres | varchar(200) | 주차장 도로명 주소 | | NOT NULL |
| prk_plce_entrc_la | decimal(10,6) | 위도 | | NOT NULL |
| prk_plce_entrc_lo | decimal(10,6) | 경도 | | NOT NULL |
| prk_cmprt_co | int | 총 주차 구획 수 | | NOT NULL |

## 3. 주차장 운영정보(PARKING_OPERATION)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| prk_center_id | varchar(50) | 주차장 관리 ID | PK, FK | NOT NULL, REFERENCES PARKING_INFO |
| opertn_bs_free_time | int | 기본 회차(기본무료)시간 | | |

## 4. 주차장 운영시간(PARKING_OPERATION_HOURS)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| id | int | ID | PK | AUTO_INCREMENT |
| prk_center_id | varchar(50) | 주차장 관리 ID | FK | NOT NULL, REFERENCES PARKING_INFO |
| day_of_week | varchar(3) | 요일 | | NOT NULL, CHECK IN ('SUN',...) |
| start_time | time | 운영 시작 시간 | | NOT NULL |
| end_time | time | 운영 종료 시간 | | NOT NULL |

## 5. 주차장 요금정보(PARKING_PRICE_INFO)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| prk_center_id | varchar(50) | 주차장 관리 ID | PK, FK | NOT NULL, REFERENCES PARKING_INFO |
| parking_chrge_bs_time | int | 기본시간 | | |
| parking_chrge_bs_chrge | int | 기본요금 | | |
| parking_chrge_adit_unit_time | int | 추가단위시간 | | |
| parking_chrge_adit_unit_chrge | int | 추가단위요금 | | |
| parking_chrge_one_day_chrge | int | 1일 요금 | | |
| parking_chrge_mon_unit_chrge | int | 월 정액 | | |

## 6. 주차장 실시간 정보(PARKING_REALTIME)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| prk_center_id | varchar(50) | 주차장 관리 ID | PK, FK | NOT NULL, REFERENCES PARKING_INFO |
| pkfc_ParkingLots_total | int | 총 주차 구획 수 | | NOT NULL |
| pkfc_Available_ParkingLots_total | int | 주차 가능 구획 수 | | NOT NULL |
| updated_at | datetime | 정보 갱신 시간 | | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |

## 7. 즐겨찾기 주차장(FAVORITE_PARKING)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| id | int | 즐겨찾기 ID | PK | AUTO_INCREMENT |
| user_id | int | 사용자 ID | FK | NOT NULL, REFERENCES USER |
| parking_code | varchar(50) | 주차장 코드 | FK | NOT NULL, REFERENCES PARKING_INFO |
| created_at | datetime | 생성일시 | | DEFAULT CURRENT_TIMESTAMP |

## 8. 리뷰(REVIEW)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| id | int | 리뷰 ID | PK | AUTO_INCREMENT |
| user_id | int | 사용자 ID | FK | NOT NULL, REFERENCES USER |
| parking_code | varchar(50) | 주차장 코드 | FK | NOT NULL, REFERENCES PARKING_INFO |
| rating | decimal(2,1) | 별점 (0-5) | | NOT NULL, CHECK (rating BETWEEN 0 AND 5) |
| comment | varchar(1000) | 리뷰 내용 | | |
| created_at | datetime | 생성일시 | | DEFAULT CURRENT_TIMESTAMP |

## 9. 사용자 방문 기록(USER_HISTORY)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| id | int | 방문 기록 ID | PK | AUTO_INCREMENT |
| user_id | int | 사용자 ID | FK | NOT NULL, REFERENCES USER |
| parking_code | varchar(50) | 주차장 코드 | FK | NOT NULL, REFERENCES PARKING_INFO |
| visited_at | datetime | 방문 일시 | | NOT NULL, DEFAULT CURRENT_TIMESTAMP |

## 10. 알림 설정(ALERT_SETTING)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| id | int | 알림 설정 ID | PK | AUTO_INCREMENT |
| user_id | int | 사용자 ID | FK | NOT NULL, REFERENCES USER |
| parking_code | varchar(50) | 주차장 코드 | FK | NOT NULL, REFERENCES PARKING_INFO |
| day_of_week | varchar(3) | 요일 | | NOT NULL, CHECK IN ('SUN',...) |
| hour | int | 시간(24시간) | | NOT NULL, CHECK (hour BETWEEN 0 AND 23) |
| min_free_slots | int | 최소 가용 주차공간 | | NOT NULL, CHECK (min_free_slots > 0) |

## 11. 게시글(POST)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| id | int | 게시글 ID | PK | AUTO_INCREMENT |
| user_id | int | 사용자 ID | FK | NOT NULL, REFERENCES USER |
| title | varchar(200) | 제목 | | NOT NULL |
| content | text | 내용 | | NOT NULL |
| views | int | 조회수 | | NOT NULL, DEFAULT 0 |
| created_at | datetime | 생성일시 | | DEFAULT CURRENT_TIMESTAMP |

## 12. 댓글(COMMENT)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| id | int | 댓글 ID | PK | AUTO_INCREMENT |
| post_id | int | 게시글 ID | FK | NOT NULL, REFERENCES POST |
| user_id | int | 사용자 ID | FK | NOT NULL, REFERENCES USER |
| comment | varchar(1000) | 댓글 내용 | | NOT NULL |
| created_at | datetime | 생성일시 | | DEFAULT CURRENT_TIMESTAMP |

## 13. 관리자(ADMIN)
| 필드명 | 데이터 타입 | 설명 | 키 | 제약조건 |
|--------|------------|------|-----|---------|
| admin_id | int | 관리자 ID | PK | AUTO_INCREMENT |
| admin_email | varchar(100) | 관리자 이메일 | | NOT NULL, UNIQUE |
| password | varchar(100) | 비밀번호 | | NOT NULL |
| created_at | datetime | 생성일시 | | DEFAULT CURRENT_TIMESTAMP |

## 테이블 관계도

### 사용자(USER) 관계
- USER → FAVORITE_PARKING: 1:N (한 사용자가 여러 주차장을 즐겨찾기할 수 있음)
- USER → REVIEW: 1:N (한 사용자가 여러 리뷰를 작성할 수 있음)
- USER → USER_HISTORY: 1:N (한 사용자가 여러 방문 기록을 가질 수 있음)
- USER → ALERT_SETTING: 1:N (한 사용자가 여러 알림 설정을 할 수 있음)
- USER → POST: 1:N (한 사용자가 여러 게시글을 작성할 수 있음)
- USER → COMMENT: 1:N (한 사용자가 여러 댓글을 작성할 수 있음)

### 주차장 정보(PARKING_INFO) 관계
- PARKING_INFO → PARKING_OPERATION: 1:1 (하나의 주차장에 하나의 운영 정보)
- PARKING_INFO → PARKING_OPERATION_HOURS: 1:N (하나의 주차장에 여러 요일별 운영시간)
- PARKING_INFO → PARKING_PRICE_INFO: 1:1 (하나의 주차장에 하나의 요금 정보)
- PARKING_INFO → PARKING_REALTIME: 1:1 (하나의 주차장에 하나의 실시간 정보)
- PARKING_INFO → FAVORITE_PARKING: 1:N (하나의 주차장이 여러 사용자에 의해 즐겨찾기될 수 있음)
- PARKING_INFO → REVIEW: 1:N (하나의 주차장이 여러 리뷰를 받을 수 있음)
- PARKING_INFO → USER_HISTORY: 1:N (하나의 주차장이 여러 방문 기록을 가질 수 있음)
- PARKING_INFO → ALERT_SETTING: 1:N (하나의 주차장에 여러 알림 설정이 있을 수 있음)

### 게시글(POST) 관계
- POST → COMMENT: 1:N (하나의 게시글에 여러 댓글이 달릴 수 있음)
