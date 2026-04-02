# 예비자 (Yebija)

> 예배 준비 올인원 플랫폼 — 성경 구절 기반 설교 준비 + 예배 PPT 자동 생성

---

## 프로젝트 개요

| 항목 | 값 |
|---|---|
| 서비스명 | 예비자 (Yebija) |
| 패키지명 | `com.yebija` |
| 타겟 | 예배 PPT를 준비하는 목사, 전도사, 예배 간사 |
| 핵심 기능 | 예배 순서 템플릿 + 항목별 슬라이드 자동 생성/파일 첨부 + PPT 병합 |

## 레포 구조

```
yebija/
├── CLAUDE.md                  ← 지금 이 파일
├── docs/
│   ├── plan-v0.3.md           ← 서비스 기획안
│   ├── schema.md              ← DB 스키마 상세
│   ├── schema.dbml            ← dbdiagram.io ERD
│   ├── backend-structure.md   ← Spring Boot 패키지 구조
│   ├── api.md                 ← API 명세 (작성 예정)
│   └── decisions.md           ← 기술 결정 로그
├── backend/            ← Spring Boot (com.yebija)
│   └── CLAUDE.md
└── frontend/           ← React + Vite
    └── CLAUDE.md
```

## 기술 스택

| 레이어 | 기술 |
|---|---|
| 프론트엔드 | React 19 + Vite |
| 백엔드 | Spring Boot 3.3.5 / Java 17 / Gradle |
| DB | MySQL 8 + Redis |
| PPT 병합 | Apache POI |
| AI (Phase 2) | OpenAI GPT-4o |
| 배포 | Vercel (프론트) + Railway (백엔드) |

## 핵심 도메인 개념

**예배 순서 템플릿** — 교회별로 저장하는 순서 구조 (내용 없음, 구조만)

**WorshipItem.mode**
- `AUTO` — content JSON으로 슬라이드 자동 생성
- `FILE` — 첨부된 .pptx 파일을 그대로 Apache POI로 병합

**ItemType**: `HYMN` / `BIBLE` / `RESPONSIVE_READING` / `PRAYER` / `SERMON` / `CUSTOM`

---

## 현재 상태

> ⚠️ 세션 종료 시 이 섹션을 업데이트할 것

### 완료된 작업
- [x] 서비스 기획 확정 (docs/plan-v0.3.md)
- [x] DB 스키마 설계 (docs/schema.md, schema.dbml)
- [x] Spring Boot 패키지 구조 설계 (docs/backend-structure.md)
- [x] `common/` 패키지 (ApiResponse, ErrorCode, GlobalExceptionHandler 등)
- [x] `church/` + `auth/` 패키지 (JWT 인증, 회원가입·로그인 API)
- [x] `bible/` 패키지 (holybible.or.kr 스크래퍼, Redis 캐시, REST API)

### 진행 중
- (없음)

### 다음 할 일
1. `template/` 패키지 — 예배 순서 템플릿 CRUD
2. `worship/` 패키지 — 예배 인스턴스·항목 CRUD
3. `file/` 패키지 — 파일 업로드 (찬송가·교독문은 FILE 모드 전용)
4. `ppt/` 패키지 — Apache POI 병합
5. 배포 (Railway)

### MVP 범위 확정 (ADR-010)
- **성경봉독**: holybible.or.kr 스크래핑 → AUTO 슬라이드 생성 ✅
- **찬송가·교독문**: FILE 첨부 전용 (AUTO 생성 없음)
- `hymn/`, `responsive/` 패키지 MVP 제외

---

## 브랜치 전략

```
main              # 배포 가능한 상태만
└── develop       # 백+프론트 통합
    └── feature/* # 기능 단위 개발 → PR → develop 머지
```

- `main` 직접 푸시 금지 — 항상 PR
- 브랜치 네이밍: `feature/auth`, `feature/bible-scraper`, `feature/ppt-merge` 등
- 머지 후 브랜치 삭제

## 커밋 컨벤션

```
feat:     새 기능
fix:      버그 수정
docs:     문서 작업
refactor: 리팩토링
test:     테스트
chore:    빌드·설정 변경
```

## 참고 문서 빠른 링크

- 전체 기획: `docs/plan-v0.3.md`
- DB 스키마: `docs/schema.md`
- 백엔드 구조: `docs/backend-structure.md`
- 기술 결정 로그: `docs/decisions.md`
