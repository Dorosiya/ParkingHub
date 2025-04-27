# ParkingHub 데이터베이스 설정 가이드

이 디렉토리에는 ParkingHub 애플리케이션에 필요한 데이터베이스 스키마와 기본 데이터를 설정하는 SQL 파일이 포함되어 있습니다.

## 파일 설명

- `01_schema.sql`: 기본 테이블 구조를 생성하는 SQL 파일입니다.
- `02_init_data.sql`: 기본 역할 및 관리자 계정을 생성하는 SQL 파일입니다.
- `03_reviews_table.sql`: 리뷰 테이블을 생성하는 SQL 파일입니다.
- `04_reset_procedure.sql`: 주차장 데이터 초기화 프로시저를 생성하는 SQL 파일입니다.

## 설정 방법

### MySQL 데이터베이스 생성 및 설정

1. MySQL 서버에 접속합니다:
   ```
   mysql -u root -p
   ```

2. 데이터베이스를 생성합니다:
   ```sql
   CREATE DATABASE parking_hub DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE parking_hub;
   ```

3. SQL 파일을 순서대로 실행합니다:
   ```
   source /path/to/01_schema.sql
   source /path/to/02_init_data.sql
   source /path/to/03_reviews_table.sql
   source /path/to/04_reset_procedure.sql
   ```

### 데이터 초기화

주차장 데이터가 손상되거나 초기화가 필요한 경우 다음 SQL 명령어를 사용할 수 있습니다:

```sql
CALL reset_parking_data();
```

## 관리자 계정 정보

- 사용자명: admin
- 이메일: admin@parkinghub.com
- 비밀번호: admin1234 (BCrypt로 암호화됨)
- 역할: ROLE_ADMIN 