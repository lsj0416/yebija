# backend

> 예비자 (Yebija) — Spring Boot API 서버

---

## 기술 스택

- Spring Boot 3.3.5 / Java 17 / Gradle
- Spring Security + JWT
- Spring Data JPA + MySQL 8
- Spring Data Redis
- Apache POI (PPT 병합)
- Jsoup (성경·찬송가 스크래핑)

---

## 시작하기

### 1. 환경변수 설정

```bash
cp .env.example .env
```

```
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=
JWT_SECRET=your-secret-key-must-be-at-least-32-characters
CORS_ORIGINS=http://localhost:5173
STORAGE_TYPE=local
FILE_UPLOAD_PATH=/tmp/yebija/uploads
OPENAI_API_KEY=        # Phase 2
```

### 2. DB 세팅

```bash
# MySQL에서 데이터베이스 생성
mysql -u root -p -e "CREATE DATABASE yebija CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

> 테이블은 `application-local.yml`의 `ddl-auto: update` 설정으로 앱 실행 시 JPA가 자동 생성합니다.
> 스키마 참고: [docs/schema.md](../docs/schema.md)

### 3. 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

---

## 패키지 구조

```
com.yebija
├── auth/          # JWT 인증·인가
├── church/        # 교회 계정·구성원
├── template/      # 예배 순서 템플릿
├── worship/       # 예배 인스턴스·항목
├── bible/         # 성경봉독 스크래핑
├── hymn/          # 찬송가 스크래핑
├── responsive/    # 교독문 JSON 번들
├── ppt/           # PPT 병합 (Apache POI)
├── ai/            # AI 추천 — Phase 2
├── file/          # 파일 업로드·스토리지
└── common/        # 예외처리·응답포맷·설정
```

> 상세 구조: [docs/backend-structure.md](../docs/backend-structure.md)

---

## 주요 API

```
POST /api/auth/signup              교회 계정 생성
POST /api/auth/login               로그인

GET  /api/templates                템플릿 목록
POST /api/templates                템플릿 생성

POST /api/worships                 예배 생성
PUT  /api/worships/{id}/items/{id} 항목 내용 입력
POST /api/worships/{id}/export     PPT 병합 다운로드

GET  /api/bible/verses             성경 구절 조회
GET  /api/hymns/{number}           찬송가 조회
GET  /api/responsive/{number}      교독문 조회
```

> Swagger UI (로컬): http://localhost:8080/swagger-ui/index.html
> 전체 명세: [docs/api.md](../docs/api.md)

---

## 브랜치 전략

```
main              # 배포 가능한 상태만
└── develop       # 백+프론트 통합
    └── feature/* # 기능 단위 개발 → PR → develop 머지
```

**브랜치 네이밍 예시**
```
feature/project-init
feature/auth
feature/bible-scraper
feature/template
feature/worship
feature/hymn-scraper
feature/ppt-merge
feature/file-upload
```