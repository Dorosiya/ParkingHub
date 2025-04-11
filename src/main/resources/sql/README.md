# ParkingHub 데이터베이스 설정 가이드

이 디렉토리에는 ParkingHub 애플리케이션에 필요한 데이터베이스 스키마와 샘플 데이터를 설정하는 SQL 파일이 포함되어 있습니다.

## 파일 설명

- `schema.sql`: 데이터베이스와 테이블 구조를 생성하는 SQL 문이 포함되어 있습니다.
- `sample-data.sql`: 개발 및 테스트를 위한 샘플 데이터를 추가하는 SQL 문이 포함되어 있습니다.

## 설정 방법

### MySQL 데이터베이스 생성 및 설정

1. MySQL 서버에 접속합니다:
   ```
   mysql -u root -p
   ```

2. 스키마를 생성합니다:
   ```
   source /path/to/schema.sql
   ```

3. (선택사항) 개발 및 테스트를 위한 샘플 데이터를 추가합니다:
   ```
   source /path/to/sample-data.sql
   ```

### 애플리케이션 설정

`application.properties` 파일에서 데이터베이스 연결 정보가 올바르게 설정되어 있는지 확인하세요:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/parking_hub?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

## 기본 계정 정보

스키마 파일은 기본적으로 다음 계정을 생성합니다:

1. 관리자 계정:
   - 사용자명: admin
   - 이메일: admin@parkinghub.com
   - 비밀번호: admin1234
   - 역할: ROLE_ADMIN

2. 샘플 사용자 계정 (샘플 데이터 파일에서 생성):
   - 사용자명: user1, user2
   - 이메일: user1@example.com, user2@example.com
   - 비밀번호: user1234
   - 역할: ROLE_USER

3. 매니저 계정 (샘플 데이터 파일에서 생성):
   - 사용자명: manager
   - 이메일: manager@example.com
   - 비밀번호: user1234
   - 역할: ROLE_MANAGER

## 참고사항

- 비밀번호는 모두 BCrypt로 암호화되어 있습니다.
- 실제 운영 환경에서는 보안을 위해 기본 계정의 비밀번호를 변경하고, 샘플 데이터는 제거하는 것이 좋습니다. 