# 예비자 (Yebija)

> 예배 준비 올인원 플랫폼

성경 구절 하나로 설교 준비부터 예배 PPT 완성까지.
매주 반복되는 예배 준비 수고를 줄여주는 목회 보조 서비스입니다.

---

## 주요 기능

- **예배 순서 템플릿** — 교회별 예배 순서를 저장하고 매주 재사용
- **콘텐츠 자동 생성** — 성경봉독(개역개정), 찬송가 가사, 교독문을 번호/구절 입력만으로 자동 로드
- **파일 첨부 병합** — 기존에 쓰던 PPT 파일을 그대로 첨부해서 순서대로 병합
- **PPT 단일 출력** — 모든 슬라이드를 하나의 `.pptx` 파일로 다운로드
- **AI 설교 준비 보조** *(Phase 2)* — 성경 구절 기반 설교 개요·찬송가·연관구절 추천

---

## 기술 스택

| 레이어 | 기술 |
|---|---|
| 프론트엔드 | React 19 + Vite |
| 백엔드 | Spring Boot 3.3.5 / Java 17 |
| 데이터베이스 | MySQL 8 + Redis |
| PPT 병합 | Apache POI |
| AI | OpenAI GPT-4o *(Phase 2)* |
| 배포 | Vercel + Railway |

---

## 프로젝트 구조

```
yebija/
├── backend/    # Spring Boot API 서버
├── frontend/   # React 클라이언트
└── docs/              # 설계 문서
```

---

## 시작하기

### 사전 요구사항

- Java 17+
- Node.js 18+
- MySQL 8
- Redis

### 백엔드 실행

```bash
cd /path/to/yebija

# 환경변수 설정 (레포 루트)
cp .env.example .env
# .env 파일에 DB, Redis, JWT 설정 입력

# 실행
cd backend
./gradlew bootRun
```

### 프론트엔드 실행

```bash
cd frontend

# 환경변수 확인
# frontend/.env.local
# VITE_API_BASE_URL=http://localhost:8080

npm run dev
```

---

## 환경변수

### 백엔드 (`.env`)

```
DB_HOST=localhost
DB_PORT=3306
DB_USERNAME=root
DB_PASSWORD=
JWT_SECRET=
CORS_ORIGINS=http://localhost:5173
STORAGE_TYPE=local
FILE_UPLOAD_PATH=/tmp/yebija/uploads

# Phase 2
OPENAI_API_KEY=
```

### 프론트엔드 (`.env.local`)

```
VITE_API_BASE_URL=http://localhost:8080
```

---

## 문서

| 문서 | 내용 |
|---|---|
| [기획안](docs/plan-v0.3.md) | 서비스 개요, 기능 명세, 로드맵 |
| [DB 스키마](docs/schema.md) | 테이블 설계 및 관계 |
| [API 명세](docs/api.md) | 엔드포인트 목록 및 요청/응답 구조 |
| [백엔드 구조](docs/backend-structure.md) | Spring Boot 패키지 구조 및 개발 순서 |
| [기술 결정 로그](docs/decisions.md) | 주요 기술 선택 배경 및 이유 |

---

## 브랜치 전략

```
main              # 배포 가능한 상태만
└── develop       # 백+프론트 통합
    └── feature/* # 기능 단위 개발 → PR → develop 머지
```

**커밋 컨벤션**

```
feat:     새 기능
fix:      버그 수정
docs:     문서 작업
refactor: 리팩토링
test:     테스트
chore:    빌드·설정 변경
```

- **Phase 1** — MVP: 예배 순서 템플릿, PPT 자동 생성·병합, 교회 계정
- **Phase 2** — AI 기능: OpenAI 기반 설교 준비 보조, 크레딧 시스템
- **Phase 3** — 유료화: 월 구독 플랜, 교단 확장, 설교문 초안 생성

---

## 라이선스

MIT
