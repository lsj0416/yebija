# frontend

> 예비자 (Yebija) — PPT export 베타용 React 클라이언트

---

## 기술 스택

- React 19 + Vite
- Axios (API 호출)
- Backend-driven PPT export 다운로드

---

## 시작하기

### 1. 환경변수 설정

```bash
# frontend/.env.local 파일 확인 또는 생성
```

```
VITE_API_BASE_URL=http://localhost:8080
```

### 2. 실행

```bash
npm install
npm run dev
```

---

## 프로젝트 구조

```
src/
├── api/           # 백엔드 API 호출
├── components/    # 공통 컴포넌트
├── hooks/         # 커스텀 훅
├── pages/         # 라우트별 페이지
├── stores/        # 전역 상태
├── utils/
│   └── pptGenerator.js   # PptxGenJS 슬라이드 생성
└── data/
    └── books.js           # 성경 66권 메타데이터
```

---

## 현재 베타 UX

- 예배 순서 템플릿을 기반으로 항목을 편집합니다.
- 찬양·교독문은 주로 기존 `.pptx` 파일을 첨부합니다.
- 성경봉독은 AUTO 슬라이드 생성으로 채웁니다.
- 최종 PPT 출력은 항상 `POST /api/worships/{id}/export` 를 통해 백엔드에서 병합 후 다운로드합니다.

다운로드 실패 시 백엔드의 JSON 에러 응답을 읽어 사용자 메시지로 표시합니다.

---

## 브랜치 전략

```
main              # 배포 가능한 상태만
└── develop       # 백+프론트 통합
    └── feature/* # 기능 단위 개발 → PR → develop 머지
```
