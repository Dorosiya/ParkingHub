# ParkingHub - 주차장 정보 관리 시스템

이 프로젝트는 사용자들이 주차장 정보를 조회하고 즐겨찾기로 등록/관리할 수 있는 웹 애플리케이션입니다.

## 기능 개요

- 주차장 검색 및 조회
- 지도 기반 주차장 위치 확인
- 사용자 계정 관리
- 즐겨찾기 주차장 등록 및 관리
- 주차장 상세 정보 제공

## 기술 스택

- Spring Boot
- MyBatis
- MySQL
- JWT (인증)
- JSP (뷰)
- JavaScript/CSS (프론트엔드)
- Bootstrap (UI 프레임워크)

## 주요 기능 상세

### 즐겨찾기(마이페이지) 기능

즐겨찾기 기능은 사용자가 자주 이용하는 주차장을 즐겨찾기에 등록하고 관리할 수 있는 기능입니다. 사용자는 마이페이지에서 즐겨찾기한 주차장 목록을 확인하고, 메모를 추가/수정하거나 즐겨찾기를 삭제할 수 있습니다.

#### 구현된 기능

1. 사용자의 즐겨찾기 주차장 목록 조회
2. 즐겨찾기 추가 (주차장 ID 기준)
3. 즐겨찾기 추가/제거 토글 기능
4. 즐겨찾기 메모 업데이트
5. 즐겨찾기 삭제
6. 마이페이지 UI (즐겨찾기 관리 페이지)

#### 데이터베이스 스키마

즐겨찾기 기능을 위한 데이터베이스 테이블 구조는 다음과 같습니다:

```sql
CREATE TABLE IF NOT EXISTS FAVORITE_PARKING (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,                            -- 사용자 ID
    prk_center_id VARCHAR(30) NOT NULL,                 -- 주차장 ID
    memo VARCHAR(500),                                  -- 메모
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_parking (user_id, prk_center_id),
    FOREIGN KEY (user_id) REFERENCES USER(id) ON DELETE CASCADE,
    FOREIGN KEY (prk_center_id) REFERENCES PARKING_INFO(prk_center_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### API 엔드포인트

**즐겨찾기 목록 조회**
```
GET /api/favorites
```

**즐겨찾기 추가**
```
POST /api/favorites
```

**즐겨찾기 토글**
```
POST /api/favorites/toggle/{prkCenterId}
```

**즐겨찾기 상태 확인**
```
GET /api/favorites/check/{prkCenterId}
```

**즐겨찾기 메모 업데이트**
```
PATCH /api/favorites/{id}
```

**즐겨찾기 삭제**
```
DELETE /api/favorites/{id}
```

## 설정 및 시작하기

### 필수 조건

- Java 11 이상
- Maven
- MySQL 8.0

### 데이터베이스 설정

1. MySQL에 접속하여 데이터베이스를 생성합니다.
2. `src/main/resources/sql/schema.sql`을 실행하여 테이블을 생성합니다.
3. 테스트 데이터를 추가하려면 `src/main/resources/sql/testdata.sql`을 실행합니다.

### 애플리케이션 설정

1. `application.properties` 파일에서 데이터베이스 연결 정보를 설정합니다:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/parking_hub?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### 애플리케이션 실행

1. 프로젝트를 빌드하고 실행합니다:
   ```bash
   ./mvnw spring-boot:run
   ```
2. 웹 브라우저에서 `http://localhost:8080`에 접속합니다.

### 기본 계정

- 사용자 계정:
  - 이메일: user@parkinghub.com
  - 비밀번호: user1234
- 관리자 계정:
  - 이메일: admin@parkinghub.com
  - 비밀번호: admin1234

## 참고 문서

- [Spring Boot 문서](https://docs.spring.io/spring-boot/3.3.10/reference)
- [MyBatis 문서](https://mybatis.org/mybatis-3/ko/)
- [Spring Security 문서](https://docs.spring.io/spring-security/reference)

## 문제해결

- **데이터베이스 연결 문제**: `application.properties` 파일의 데이터베이스 연결 정보를 확인하세요.
- **인증 오류**: JWT 토큰이 유효한지 확인하고, 필요한 경우 다시 로그인하여 새 토큰을 발급받으세요.
- **즐겨찾기 추가 실패**: 주차장 ID가 `PARKING_INFO` 테이블에 존재하는지 확인하세요.
- **즐겨찾기 조회 결과 없음**: 사용자 ID에 연결된 즐겨찾기가 있는지 확인하세요. 