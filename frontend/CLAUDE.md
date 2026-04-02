# frontend

> React 19 + Vite
> 패키지명: frontend

---

## 프로젝트 구조 요약

```
src/
├── api/           # 백엔드 API 호출 (axios)
├── components/    # 공통 컴포넌트
├── hooks/         # 커스텀 훅
├── pages/         # 라우트별 페이지
├── stores/        # 전역 상태 (Zustand 예정)
├── utils/         # 유틸 함수
│   └── pptGenerator.js   # PptxGenJS (자동 생성 슬라이드)
└── data/
    └── books.js           # 66권 메타데이터
```

## 환경변수

```
VITE_API_BASE_URL=http://localhost:8080   # 로컬
VITE_API_BASE_URL=https://xxx.railway.app # 운영
```

## 주요 컨벤션

**API 호출** — `src/api/` 에서 함수로 분리, 컴포넌트에서 직접 fetch 금지

**슬라이드 생성 방식 구분**
- `AUTO` 항목: PptxGenJS로 브라우저에서 직접 생성
- `FILE` 항목: 사용자가 업로드한 파일을 백엔드로 전송 → POI 병합 후 다운로드
- 최종 병합은 항상 백엔드(POST `/api/worships/{id}/export`)

**WorshipItem type별 입력 컴포넌트**
```
HYMN              → HymnInput.jsx      (찬송가 번호 + AUTO/FILE 토글)
BIBLE             → BibleInput.jsx     (책/장/절 선택)
RESPONSIVE_READING→ ResponsiveInput.jsx(교독문 번호)
PRAYER            → PrayerInput.jsx    (역할 + 담당자 이름)
SERMON            → SermonInput.jsx    (제목 + 본문)
```

---

## 브랜치 전략

```
main              # 배포 가능한 상태만 (Vercel 자동 배포)
└── develop       # 백+프론트 통합
    └── feature/* # 기능 단위 개발 → PR → develop 머지
```

**규칙**
- `main` 직접 푸시 금지 — 항상 PR
- PR 대상은 항상 `develop` (main으로 직접 PR 금지)
- `develop` → `main` PR은 배포 준비 완료 시점에만

**브랜치 네이밍**
```
feature/frontend-init        # Step 1 — 프로젝트 초기화 (완료)
feature/auth                 # Step 2 — 로그인·회원가입 페이지
feature/frontend-template    # Step 3 — 템플릿 관리 페이지 (backend feature/template 사용됨)
feature/frontend-worship     # Step 4 — 예배 생성 페이지 (backend feature/worship 사용됨)
feature/ppt-export           # Step 5 — PPT 생성·다운로드 플로우
feature/ai-recommend         # Step 6 — AI 추천 UI (Phase 2)
```

**커밋 컨벤션**
```
feat:     새 기능
fix:      버그 수정
docs:     문서
refactor: 리팩토링
style:    스타일(포맷, CSS)
chore:    빌드·설정 변경
```

---

## 현재 상태

> ⚠️ 세션 종료 시 이 섹션을 업데이트할 것

### 완료된 작업
- [x] 성경봉독 프로토타입 (BibleForm, SlidePreview, pptGenerator)
- [x] 66권 메타데이터 (books.js)
- [x] 백엔드 API 연동 (bibleApi.js)
- [x] 슬라이드 스타일 옵션 (배경색·글자색·폰트 크기)
- [x] **Step 1** — 프로젝트 초기화 (Vite, react-router-dom, axios, zustand 설정)
- [x] **Step 2** — 로그인·회원가입 페이지 (authApi, authStore, LoginPage, RegisterPage, PrivateRoute)
- [x] **Step 3** — 템플릿 관리 페이지 (templateApi, TemplatePage, TemplateFormPage, itemMeta)
- [x] **Step 4** — 예배 생성 페이지 (worshipApi, bibleApi, books.js, WorshipPage, WorshipDetailPage, 항목별 InputComponent)
- [x] **Step 5** — PPT 생성·다운로드 플로우 (fileApi, FileAttachment, exportPpt → blob 다운로드)

### 진행 중
- (없음)

### 다음 할 일 (순서대로)
1. **Step 6** — AI 추천 UI (Phase 2)
3. **Step 3** — 템플릿 관리 페이지 (순서 구성 UI)
4. **Step 4** — 예배 생성 페이지 (항목별 입력 + AUTO/FILE 토글)
5. **Step 5** — PPT 생성·다운로드 플로우
6. **Step 6** — AI 추천 UI (Phase 2)

### 현재 브랜치
```
main
└── develop
    └── feature/ppt-export  ← 현재 작업 브랜치
```

---

## 세션 시작 체크리스트

```
1. git status 확인
2. 현재 작업 브랜치 확인
3. "다음 할 일" 확인 후 목표 선언
```
