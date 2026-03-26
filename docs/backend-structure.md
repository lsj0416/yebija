# 예비자 (Yebija) — Spring Boot 프로젝트 구조

> Spring Boot 3.2 / Java 17 / Gradle 기준
> 패키지: `com.yebija`

---

## 목차

1. [프로젝트 생성 설정](#1-프로젝트-생성-설정)
2. [패키지 구조](#2-패키지-구조)
3. [패키지별 역할](#3-패키지별-역할)
4. [주요 의존성](#4-주요-의존성)
5. [설정 파일](#5-설정-파일)
6. [개발 순서 (Phase 1 기준)](#6-개발-순서-phase-1-기준)

---

## 1. 프로젝트 생성 설정

[start.spring.io](https://start.spring.io) 설정값

| 항목 | 값 |
|---|---|
| Project | Gradle - Groovy |
| Language | Java |
| Spring Boot | 3.2.x |
| Group | com.yebija |
| Artifact | yebija-backend |
| Packaging | Jar |
| Java | 17 |

**초기 Dependencies (start.spring.io에서 선택)**

- Spring Web
- Spring Security
- Spring Data JPA
- Spring Data Redis
- Validation
- Lombok
- MySQL Driver

---

## 2. 패키지 구조

```
com.yebija
│
├── YebijaApplication.java
│
├── auth/                          # 인증·인가
│   ├── controller/
│   │   └── AuthController.java
│   ├── service/
│   │   └── AuthService.java
│   ├── dto/
│   │   ├── SignupRequest.java
│   │   ├── LoginRequest.java
│   │   └── TokenResponse.java
│   ├── jwt/
│   │   ├── JwtProvider.java
│   │   ├── JwtFilter.java
│   │   └── JwtProperties.java
│   └── security/
│       ├── SecurityConfig.java
│       ├── CustomUserDetails.java
│       └── CustomUserDetailsService.java
│
├── church/                        # 교회 계정
│   ├── controller/
│   │   └── ChurchController.java
│   ├── service/
│   │   └── ChurchService.java
│   ├── domain/
│   │   ├── Church.java
│   │   ├── ChurchMember.java
│   │   └── enums/
│   │       ├── Denomination.java
│   │       └── MemberRole.java
│   ├── dto/
│   │   └── ChurchResponse.java
│   └── repository/
│       ├── ChurchRepository.java
│       └── ChurchMemberRepository.java
│
├── template/                      # 예배 순서 템플릿
│   ├── controller/
│   │   └── TemplateController.java
│   ├── service/
│   │   └── TemplateService.java
│   ├── domain/
│   │   ├── WorkshipTemplate.java
│   │   ├── TemplateItem.java
│   │   └── enums/
│   │       ├── ItemType.java      # HYMN, BIBLE, RESPONSIVE_READING, PRAYER, SERMON, CUSTOM
│   │       └── ItemMode.java      # AUTO, FILE
│   ├── dto/
│   │   ├── TemplateCreateRequest.java
│   │   ├── TemplateResponse.java
│   │   └── TemplateItemDto.java
│   └── repository/
│       ├── WorshipTemplateRepository.java
│       └── TemplateItemRepository.java
│
├── worship/                       # 예배 인스턴스
│   ├── controller/
│   │   └── WorshipController.java
│   ├── service/
│   │   └── WorshipService.java
│   ├── domain/
│   │   ├── Worship.java
│   │   ├── WorshipItem.java
│   │   └── enums/
│   │       └── WorshipStatus.java  # DRAFT, COMPLETED
│   ├── dto/
│   │   ├── WorshipCreateRequest.java
│   │   ├── WorshipResponse.java
│   │   ├── WorshipItemUpdateRequest.java
│   │   └── content/               # type별 content DTO
│   │       ├── HymnContent.java
│   │       ├── BibleContent.java
│   │       ├── ResponsiveReadingContent.java
│   │       ├── PrayerContent.java
│   │       └── SermonContent.java
│   └── repository/
│       ├── WorshipRepository.java
│       └── WorshipItemRepository.java
│
├── bible/                         # 성경봉독
│   ├── controller/
│   │   └── BibleController.java
│   ├── service/
│   │   └── BibleService.java
│   ├── scraper/
│   │   └── GodpeopleBibleScraper.java
│   └── dto/
│       ├── BibleRequest.java
│       └── BibleResponse.java
│
├── hymn/                          # 찬송가
│   ├── controller/
│   │   └── HymnController.java
│   ├── service/
│   │   └── HymnService.java
│   ├── scraper/
│   │   └── GodpeopleHymnScraper.java
│   └── dto/
│       ├── HymnResponse.java
│       └── HymnVerse.java
│
├── responsive/                    # 교독문
│   ├── controller/
│   │   └── ResponsiveReadingController.java
│   ├── service/
│   │   └── ResponsiveReadingService.java
│   └── dto/
│       └── ResponsiveReadingResponse.java
│
├── ppt/                           # PPT 생성 & 병합
│   ├── controller/
│   │   └── PptController.java
│   ├── service/
│   │   └── PptMergeService.java
│   └── generator/
│       ├── SlideGenerator.java        # 인터페이스
│       ├── BibleSlideGenerator.java
│       ├── HymnSlideGenerator.java
│       ├── PrayerSlideGenerator.java
│       ├── SermonSlideGenerator.java
│       └── ResponsiveSlideGenerator.java
│
├── ai/                            # AI 추천 (Phase 2)
│   ├── controller/
│   │   └── AiController.java
│   ├── service/
│   │   ├── AiSuggestionService.java
│   │   └── CreditService.java
│   ├── client/
│   │   └── OpenAiApiClient.java
│   ├── domain/
│   │   ├── AiSuggestion.java
│   │   ├── Credit.java
│   │   └── enums/
│   │       └── PlanType.java      # FREE, TICKET, SUBSCRIPTION
│   ├── dto/
│   │   ├── AiSuggestRequest.java
│   │   ├── AiSuggestResponse.java
│   │   └── CreditResponse.java
│   └── repository/
│       ├── AiSuggestionRepository.java
│       └── CreditRepository.java
│
├── file/                          # 파일 업로드·스토리지
│   ├── controller/
│   │   └── FileController.java
│   ├── service/
│   │   └── FileStorageService.java
│   ├── domain/
│   │   └── UploadedFile.java
│   └── repository/
│       └── UploadedFileRepository.java
│
├── common/                        # 공통
│   ├── config/
│   │   ├── CorsConfig.java
│   │   ├── RedisConfig.java
│   │   └── JpaConfig.java
│   ├── exception/
│   │   ├── YebijaException.java       # 커스텀 베이스 예외
│   │   ├── ErrorCode.java             # 에러 코드 enum
│   │   ├── GlobalExceptionHandler.java
│   │   └── ErrorResponse.java
│   ├── response/
│   │   └── ApiResponse.java           # 공통 응답 래퍼
│   └── util/
│       └── SecurityUtil.java          # 현재 로그인 유저 추출
```

---

## 3. 패키지별 역할

### auth
JWT 기반 인증 처리. 회원가입·로그인·토큰 갱신.

```java
// JwtProvider 핵심 메서드
String generateAccessToken(Long churchId, String email, String role)
String generateRefreshToken(Long churchId)
Claims parseToken(String token)
```

### church
교회 계정 및 구성원 관리. 교단 설정.

### template
예배 순서 템플릿 CRUD. 항목 순서 변경 (seq 관리).

### worship
예배 인스턴스 생성·수정. 항목별 content 입력.

`WorshipItem.content`는 DB에 JSON으로 저장되고, type별 DTO로 역직렬화해서 사용.

```java
// content 역직렬화 예시
HymnContent content = objectMapper.convertValue(item.getContent(), HymnContent.class);
```

### bible / hymn / responsive
각각 갓피플 스크래핑 + Redis 캐싱 담당.

```java
// 캐시 키 패턴
"bible::{book}::{chapter}"          // 장 전체 캐싱
"hymn::{hymnNumber}"                 // 찬송가 번호별 캐싱
"responsive::{number}"               // 교독문 번호별 캐싱
```

### ppt
PPT 병합 핵심 로직. `SlideGenerator` 인터페이스로 type별 생성 전략 분리.

```java
// SlideGenerator 인터페이스
public interface SlideGenerator {
    ItemType getSupportedType();
    void addSlides(XMLSlideShow pptx, WorshipItem item);
}

// PptMergeService 흐름
List<WorshipItem> items = worship.getItems(); // seq 순서로 정렬
XMLSlideShow merged = new XMLSlideShow();
for (WorshipItem item : items) {
    if (item.getMode() == AUTO) {
        generators.get(item.getType()).addSlides(merged, item);
    } else { // FILE
        appendFile(merged, item.getFileStorageKey());
    }
}
return toByteArray(merged);
```

### ai *(Phase 2)*
OpenAI API 호출 + 크레딧 차감 트랜잭션 처리.

### file
멀티파트 파일 업로드 처리. Railway 볼륨 또는 S3 저장.

### common
전역 예외 처리, 공통 응답 포맷, CORS, Redis 설정.

---

## 4. 주요 의존성

```groovy
// build.gradle

dependencies {
    // Spring Boot
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // DB
    runtimeOnly 'com.mysql:mysql-connector-j'

    // JWT
    implementation 'io.jsonwebtoken:jjwt-api:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.12.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.12.5'

    // HTML 파싱 (스크래핑)
    implementation 'org.jsoup:jsoup:1.17.2'

    // PPT 병합
    implementation 'org.apache.poi:poi-ooxml:5.2.5'

    // HTTP 클라이언트 (OpenAI API 호출)
    implementation 'org.springframework.boot:spring-boot-starter-webflux'

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // 테스트
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
}
```

---

## 5. 설정 파일

### application.yml

```yaml
spring:
  application:
    name: yebija-backend

  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/yebija?characterEncoding=UTF-8&serverTimezone=Asia/Seoul
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: validate          # 운영: validate / 개발: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect
        format_sql: true
    show-sql: false

  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}

  cache:
    type: redis
    redis:
      time-to-live: 86400000      # 24시간 (ms)

  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 100MB

server:
  port: ${PORT:8080}

# JWT
jwt:
  secret: ${JWT_SECRET}
  access-expiration: 1800000      # 30분 (ms)
  refresh-expiration: 604800000   # 7일 (ms)

# CORS
cors:
  allowed-origins: ${CORS_ORIGINS:http://localhost:5173}

# 파일 스토리지
storage:
  type: ${STORAGE_TYPE:local}     # local / s3
  local:
    path: ${FILE_UPLOAD_PATH:/tmp/yebija/uploads}

# OpenAI GPT-4o (Phase 2)
openai:
  api-key: ${OPENAI_API_KEY:}
  model: gpt-4o
  max-tokens: 2000
```

### application-local.yml *(로컬 개발용)*

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  data:
    redis:
      host: localhost

jwt:
  secret: local-dev-secret-key-must-be-at-least-32-characters-long

cors:
  allowed-origins: http://localhost:5173,http://localhost:3000
```

---

## 6. 개발 순서 (Phase 1 기준)

### Step 1 — 기반 세팅
- [ ] Spring Boot 프로젝트 생성 (start.spring.io)
- [ ] `build.gradle` 의존성 추가
- [ ] `application.yml` 작성
- [ ] MySQL 스키마 생성 (`schema.sql` 실행)
- [ ] `common/` 패키지 — `ApiResponse`, `ErrorCode`, `GlobalExceptionHandler`

### Step 2 — 인증
- [ ] `Church`, `ChurchMember` 엔티티
- [ ] `JwtProvider`, `JwtFilter`
- [ ] `SecurityConfig` — 공개 URL 설정
- [ ] `AuthController` — POST `/api/auth/signup`, `/api/auth/login`

### Step 3 — 성경봉독 (기존 코드 이전)
- [ ] `GodpeopleBibleScraper` 이전 및 리팩토링
- [ ] `BibleService` — Redis 캐시 적용
- [ ] `BibleController` — GET `/api/bible/verses`

### Step 4 — 템플릿 & 예배
- [ ] `WorshipTemplate`, `TemplateItem` 엔티티
- [ ] `Worship`, `WorshipItem` 엔티티
- [ ] `ItemType`, `ItemMode` enum
- [ ] Template CRUD API
- [ ] Worship 생성·조회·수정 API

### Step 5 — 찬송가 & 교독문
- [ ] `GodpeopleHymnScraper`
- [ ] `HymnService` — Redis 캐시 적용
- [ ] `ResponsiveReadingService` — JSON 번들 로드
- [ ] 교독문 JSON 데이터 작성 (`resources/data/responsive-reading.json`)

### Step 6 — PPT 병합
- [ ] `SlideGenerator` 인터페이스
- [ ] 각 type별 `SlideGenerator` 구현체
- [ ] `PptMergeService` — Apache POI 병합 로직
- [ ] `PptController` — POST `/api/worships/{id}/export`

### Step 7 — 파일 업로드
- [ ] `FileStorageService` — 로컬 저장 (개발) / S3 (운영)
- [ ] `FileController` — POST `/api/files/upload`
- [ ] `UploadedFile` 엔티티

### Step 8 — 배포
- [ ] Railway MySQL + Redis 설정
- [ ] 환경변수 설정
- [ ] GitHub Actions CI/CD (선택)

---

*예비자 (Yebija) Spring Boot 구조 문서 v0.1*
