# backend

> Spring Boot 3.2 / Java 17 / Gradle
> 패키지: `com.yebija`

---

## 패키지 구조 요약

```
com.yebija
├── auth/         # JWT 인증·인가
├── church/       # 교회 계정·구성원
├── template/     # 예배 순서 템플릿
├── worship/      # 예배 인스턴스·항목
├── bible/        # 성경봉독 스크래핑
├── hymn/         # 찬송가 스크래핑
├── responsive/   # 교독문 JSON 번들
├── ppt/          # PPT 병합 (Apache POI)
├── ai/           # AI 추천 — Phase 2
├── file/         # 파일 업로드·스토리지
└── common/       # 예외처리·응답포맷·설정
```

> 상세 구조: `../docs/backend-structure.md`

## 주요 컨벤션

**응답 포맷** — 모든 API는 `ApiResponse<T>` 래퍼 사용
```java
{ "success": true, "data": { ... } }
{ "success": false, "error": { "code": "BIBLE_NOT_FOUND", "message": "..." } }
```

**예외 처리** — `YebijaException(ErrorCode)` 던지면 `GlobalExceptionHandler`가 처리

**캐시 키 패턴**
```
bible::{book}::{chapter}          # TTL 24h
hymn::{hymnNumber}                # TTL 24h
responsive::{number}              # TTL 7d
```

**엔티티 공통** — `BaseEntity` 상속 (`createdAt`, `updatedAt` JPA Auditing)

**인증** — JWT Access Token (30분) / Refresh Token (7일)
현재 로그인 교회: `SecurityUtil.getCurrentChurchId()`

## 주요 의존성

```groovy
implementation 'org.jsoup:jsoup:1.17.2'              // 스크래핑
implementation 'org.apache.poi:poi-ooxml:5.2.5'      // PPT 병합
implementation 'io.jsonwebtoken:jjwt-api:0.12.5'     // JWT
implementation 'org.springframework.boot:spring-boot-starter-webflux'  // OpenAI 호출
```

## 환경변수 목록

```
DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD
REDIS_HOST, REDIS_PORT
JWT_SECRET
CORS_ORIGINS
STORAGE_TYPE (local / s3)
FILE_UPLOAD_PATH
OPENAI_API_KEY        # Phase 2
```

## 갓피플 스크래핑 핵심 정보

**성경 URL 패턴**
```
https://bible.godpeople.com/?bible=GAE&bid={bookIndex}&chap={chapter}
```

**실제 HTML 셀렉터 (개발자도구 확인 완료)**
- 절 번호: `td.bidx_listTd_yak` → `ownText()`
- 본문: `td.bidx_listTd_phrase span.line_pen_ > span.line_pen_` → `ownText()`
- 장 전체를 1회 요청 후 절 범위 필터링 (요청 최소화)

---

## 현재 상태

> ⚠️ 세션 종료 시 이 섹션을 업데이트할 것

### 완료된 작업
- [x] Gradle 프로젝트 뼈대 (Spring Boot 3.5.0 / Gradle 8.14.4)
- [x] `common/` 패키지 (ApiResponse, ErrorCode, GlobalExceptionHandler, BaseEntity, JpaConfig, RedisConfig, CorsConfig)
- [x] `church/` 도메인 (Church, ChurchMember 엔티티 + Repository)
- [x] `auth/` 패키지 (JwtProvider, JwtFilter, SecurityConfig, 회원가입·로그인 API)
- [x] `bible/` 패키지 (BibleBook enum 66권, HolybibleScraper, BibleService Redis 캐시, BibleController)
- [x] `template/` 패키지 (ItemType/ItemMode enum, WorshipTemplate/TemplateItem 엔티티, CRUD API)

### 진행 중
- (없음)

### 다음 할 일 (순서대로)
1. **Step 6** — `worship/` 패키지 (예배 인스턴스·항목 CRUD)
2. **Step 7** — `file/` 패키지 (파일 업로드)
3. **Step 8** — `ppt/` 패키지 (Apache POI 병합)
4. **Step 9** — 배포 (Railway)

### MVP 범위 확정 (ADR-010)
- `hymn/`, `responsive/` 패키지 MVP 제외
- `HYMN`, `RESPONSIVE_READING` ItemType → FILE 모드 전용 (AUTO 없음)

### 현재 브랜치
```
main
└── develop
    └── feature/project-init  ← 현재 작업 브랜치
```

---

## 세션 시작 체크리스트

```
1. git status 확인
2. 현재 작업 브랜치 확인
3. "다음 할 일" 확인 후 목표 선언
```