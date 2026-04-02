# frontend

> 예비자 (Yebija) — React 클라이언트

---

## 기술 스택

- React 19 + Vite
- PptxGenJS (자동 생성 슬라이드)
- Axios (API 호출)

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

## 슬라이드 생성 방식

| mode | 처리 방식 |
|---|---|
| `AUTO` | PptxGenJS로 브라우저에서 직접 생성 |
| `FILE` | 업로드 파일을 백엔드로 전송 → Apache POI 병합 |

최종 PPT 출력은 항상 `POST /api/worships/{id}/export` 를 통해 백엔드에서 병합 후 다운로드.

---

## 브랜치 전략

```
main              # 배포 가능한 상태만
└── develop       # 백+프론트 통합
    └── feature/* # 기능 단위 개발 → PR → develop 머지
```
