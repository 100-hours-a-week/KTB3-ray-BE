# Node - Backend

> 사람과 사람을 잇는 커뮤니티 플랫폼의 백엔드 서버


https://github.com/user-attachments/assets/3f7edeb9-a638-41d8-af0c-0c13ba645c7f


## 기술 스택

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
- **Spring Security + JWT**
- **MySQL**
- **Swagger (SpringDoc OpenAPI)**

### 주요 라이브러리
- JWT 인증: `jjwt 0.12.3`
- 쿼리 로깅: `p6spy`
- 검증: `spring-boot-starter-validation`
- 암호화: `spring-security-crypto`

## 주요 기능

### 인증/인가
- JWT 기반 인증 시스템
- Spring Security를 활용한 보안 설정
- 회원가입 및 로그인

### 게시글
- 게시글 작성, 조회, 수정, 삭제 (CRUD)
- 이미지 업로드 지원
- 좋아요 기능

### 댓글
- 댓글 작성 및 관리

### 사용자
- 프로필 이미지 설정
- 닉네임 수정
- 비밀번호 수정

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/example/spring_practice/
│   │   ├── domain/              # 도메인 모델 및 엔티티
│   │   │   ├── comment/         # 댓글 도메인
│   │   │   ├── member/          # 회원 도메인
│   │   │   ├── post/            # 게시글 도메인
│   │   │   └── shared/          # 공유 도메인 (공통 엔티티 등)
│   │   ├── global/              # 전역 설정 및 공통 기능
│   │   │   ├── config/          # 설정 클래스 (Security, Swagger 등)
│   │   │   ├── response/        # 공통 응답 형식
│   │   │   └── security/        # 보안 관련 (JWT, Filter 등)
│   │   └── SpringPracticeApplication.java
│   └── resources/
│       ├── application.yml      # 애플리케이션 설정
│       └── spy.properties       # SQL 로깅 설정
└── test/                        # 테스트 코드
```

## 실행 방법

### 1. MySQL 설정

MySQL 데이터베이스를 생성합니다:

```sql
CREATE DATABASE ktb;
```

### 2. application.yml 설정

`src/main/resources/application.yml` 파일에서 다음 내용을 설정합니다:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ktb
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        show_sql: true

jwt:
  secret: your-secret-key-here-minimum-256-bits
  expiration: 86400000  # 24시간 (밀리초)
```

> `spy.properties`에서 SQL 로깅 관련 설정을 추가로 구성할 수 있습니다.

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

또는 IDE에서 직접 실행

### 4. API 문서 확인

애플리케이션 실행 후 Swagger UI에서 API 명세를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui/index.html
```

## 개발 환경

- IDE: IntelliJ IDEA (권장)
- Java: 17
- Gradle: Wrapper 사용
