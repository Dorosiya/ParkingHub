# ParkingHub 데이터베이스 마이그레이션 가이드

이 디렉토리는 ParkingHub 애플리케이션의 데이터베이스 스키마 마이그레이션 파일을 포함하고 있습니다.

## 마이그레이션 파일 목록

### V1__Create_initial_schema.sql
- 기본 테이블 구조 생성
  - ROLE: 사용자 역할 테이블
  - USER: 사용자 정보 테이블
  - PARKING_INFO: 주차장 기본 정보 테이블
  - PARKING_OPERATION: 주차장 운영 정보 테이블
  - PARKING_REALTIME: 주차장 실시간 정보 테이블
  - FAVORITE_PARKING: 즐겨찾기 주차장 테이블

### V2__Insert_initial_data.sql
- 기본 역할 데이터 및 관리자 계정 생성

### V3__Create_reviews_table.sql
- 주차장 리뷰 테이블 구조 생성

### V4__Create_reset_data_procedure.sql
- 주차장 데이터 초기화를 위한 저장 프로시저 생성

## 마이그레이션 과정

1. 애플리케이션 첫 실행 시 위 마이그레이션이 순차적으로 적용됩니다.
2. 이후 새로운 마이그레이션이 추가되면 다음 실행 시 추가된 마이그레이션만 적용됩니다.

## 데이터 초기화

주차장 데이터가 손상되거나 초기화가 필요한 경우 다음 SQL 명령어를 사용할 수 있습니다:

```sql
CALL reset_parking_data();
```

## 주의사항

- V4에서 생성된 `reset_parking_data()` 프로시저는 주차장 관련 테이블만 초기화합니다.
- 사용자 데이터와 역할 테이블은 초기화되지 않습니다.
- 실제 운영 환경에서는 신중하게 사용해야 합니다.

## 관리자 계정 정보

- 사용자명: admin
- 이메일: admin@parkinghub.com
- 비밀번호: admin1234 (bcrypt로 암호화됨)
- 역할: ROLE_ADMIN 