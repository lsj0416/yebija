# 예비자 (Yebija) — API 명세

> Base URL: `http://localhost:8080` (로컬) / `https://xxx.railway.app` (운영)
> 모든 응답은 `ApiResponse<T>` 래퍼로 감싸짐
> 인증이 필요한 API는 `Authorization: Bearer {accessToken}` 헤더 필요

---

## 공통 응답 포맷

```json
// 성공
{ "success": true, "data": { ... } }

// 실패
{ "success": false, "error": { "code": "ERROR_CODE", "message": "설명" } }
```

## 에러 코드

| 코드 | 상황 |
|---|---|
| `CHURCH_NOT_FOUND` | 교회 계정 없음 |
| `INVALID_CREDENTIALS` | 이메일/비밀번호 불일치 |
| `TOKEN_EXPIRED` | JWT 만료 |
| `BIBLE_FETCH_FAILED` | 성경 스크래핑 실패 |
| `HYMN_FETCH_FAILED` | 찬송가 스크래핑 실패 |
| `TEMPLATE_NOT_FOUND` | 템플릿 없음 |
| `WORSHIP_NOT_FOUND` | 예배 없음 |
| `PPT_MERGE_FAILED` | PPT 병합 실패 |
| `INSUFFICIENT_CREDIT` | 크레딧 부족 (Phase 2) |
| `FILE_TOO_LARGE` | 파일 크기 초과 (50MB) |

---

## 1. 인증 `/api/auth`

### 교회 계정 생성
```
POST /api/auth/signup

Request:
{
  "churchName": "새벽이슬교회",
  "denomination": "PRESBYTERIAN",
  "adminEmail": "admin@church.com",
  "password": "password123"
}

Response: 201
{
  "churchId": 1,
  "churchName": "새벽이슬교회",
  "adminEmail": "admin@church.com"
}
```

### 로그인
```
POST /api/auth/login

Request:
{
  "email": "admin@church.com",
  "password": "password123"
}

Response: 200
{
  "accessToken": "eyJ...",
  "refreshToken": "eyJ...",
  "churchId": 1,
  "churchName": "새벽이슬교회"
}
```

### 토큰 갱신
```
POST /api/auth/refresh

Request:
{
  "refreshToken": "eyJ..."
}

Response: 200
{
  "accessToken": "eyJ..."
}
```

---

## 2. 예배 순서 템플릿 `/api/templates` 🔒

### 템플릿 목록 조회
```
GET /api/templates

Response: 200
[
  {
    "id": 1,
    "name": "주일예배",
    "isDefault": true,
    "items": [
      { "id": 1, "type": "HYMN", "seq": 1, "label": "찬양1", "defaultMode": "FILE" },
      { "id": 2, "type": "RESPONSIVE_READING", "seq": 2, "label": "교독문", "defaultMode": "AUTO" },
      { "id": 3, "type": "PRAYER", "seq": 3, "label": "대표기도", "defaultMode": "AUTO" },
      { "id": 4, "type": "BIBLE", "seq": 4, "label": "성경봉독", "defaultMode": "AUTO" },
      { "id": 5, "type": "SERMON", "seq": 5, "label": "설교", "defaultMode": "AUTO" }
    ]
  }
]
```

### 템플릿 생성
```
POST /api/templates

Request:
{
  "name": "수요예배",
  "isDefault": false,
  "items": [
    { "type": "HYMN", "seq": 1, "label": "찬양", "defaultMode": "FILE" },
    { "type": "BIBLE", "seq": 2, "label": "성경봉독", "defaultMode": "AUTO" },
    { "type": "SERMON", "seq": 3, "label": "설교", "defaultMode": "AUTO" }
  ]
}

Response: 201
{ "id": 2, "name": "수요예배", ... }
```

### 템플릿 수정
```
PUT /api/templates/{templateId}
→ Request/Response 구조 생성과 동일
```

### 템플릿 삭제
```
DELETE /api/templates/{templateId}
Response: 204
```

---

## 3. 예배 `/api/worships` 🔒

### 예배 목록 조회
```
GET /api/worships?year=2025&month=3

Response: 200
[
  {
    "id": 1,
    "worshipDate": "2025-03-30",
    "title": "부활절 예배",
    "status": "DRAFT",
    "templateName": "주일예배"
  }
]
```

### 예배 생성
```
POST /api/worships

Request:
{
  "templateId": 1,
  "worshipDate": "2025-03-30",
  "title": "부활절 예배"
}

Response: 201
{
  "id": 1,
  "worshipDate": "2025-03-30",
  "status": "DRAFT",
  "items": [
    { "id": 1, "type": "HYMN", "seq": 1, "label": "찬양1", "mode": "FILE", "content": null },
    { "id": 2, "type": "BIBLE", "seq": 2, "label": "성경봉독", "mode": "AUTO", "content": null },
    ...
  ]
}
```

### 예배 상세 조회
```
GET /api/worships/{worshipId}
→ 예배 생성 응답과 동일 구조 (content 포함)
```

### 항목 내용 수정
```
PUT /api/worships/{worshipId}/items/{itemId}

// AUTO 모드 — BIBLE 예시
Request:
{
  "mode": "AUTO",
  "content": {
    "book": "요한복음",
    "chapter": 3,
    "verseStart": 16,
    "verseEnd": 16
  }
}

// FILE 모드
Request:
{
  "mode": "FILE",
  "fileStorageKey": "uploads/church-1/worship-1/hymn.pptx"
}

Response: 200
{ "id": 2, "mode": "AUTO", "content": { ... } }
```

### PPT 생성 & 다운로드
```
POST /api/worships/{worshipId}/export

Response: 200
Content-Type: application/vnd.openxmlformats-officedocument.presentationml.presentation
Content-Disposition: attachment; filename="예비자_2025-03-30.pptx"
(바이너리 스트림)
```

---

## 4. 성경봉독 `/api/bible`

### 구절 조회
```
GET /api/bible/verses?book=요한복음&chapter=3&verseStart=16&verseEnd=16

Response: 200
{
  "book": "요한복음",
  "chapter": 3,
  "verseStart": 16,
  "verseEnd": 16,
  "verses": [
    { "verseNum": 16, "text": "하나님이 세상을 이처럼 사랑하사...", "ref": "요한복음 3:16" }
  ]
}
```

---

## 5. 찬송가 `/api/hymns`

### 찬송가 조회
```
GET /api/hymns/{hymnNumber}

Response: 200
{
  "hymnNumber": 304,
  "title": "그 크신 하나님의 사랑",
  "verses": [
    { "verseNum": 1, "lines": ["그 크신 하나님의 사랑", "말로 다 형용 못 하네", ...] },
    { "verseNum": 2, "lines": [...] }
  ]
}
```

---

## 6. 교독문 `/api/responsive`

### 교독문 조회
```
GET /api/responsive/{number}

Response: 200
{
  "number": 14,
  "title": "감사",
  "verses": [
    { "speaker": "LEADER", "text": "여호와께 감사하라 그는 선하시며..." },
    { "speaker": "CONGREGATION", "text": "그 인자하심이 영원함이로다" },
    ...
  ]
}
```

---

## 7. 파일 업로드 `/api/files` 🔒

### 파일 업로드
```
POST /api/files/upload
Content-Type: multipart/form-data

Form:
  file: (binary)
  worshipItemId: 1   (선택 — 나중에 연결할 항목 ID)

Response: 201
{
  "fileId": 1,
  "originalName": "찬양_주일.pptx",
  "storageKey": "uploads/church-1/worship-1/hymn.pptx",
  "fileSize": 2048000
}
```

---

## 8. AI 추천 `/api/ai` 🔒 Phase 2

### 설교 추천 요청
```
POST /api/ai/suggest

Request:
{
  "worshipItemId": 4,
  "bibleRef": "요한복음 3:16"
}

Response: 200
{
  "theme": "하나님의 무조건적 사랑과 구원",
  "outline": {
    "intro": "죄인인 우리를 향한 하나님의 시선",
    "points": ["사랑의 크기", "사랑의 방식", "사랑의 목적"],
    "conclusion": "이 사랑에 어떻게 응답할 것인가"
  },
  "subtitles": ["값없이 주신 사랑", "독생자의 의미", "멸망에서 영생으로"],
  "hymnNumbers": [304, 405, 191, 280, 93],
  "relatedVerses": ["롬 5:8", "요일 4:9-10", "엡 2:8-9"],
  "remainingCredits": 9
}
```

### 크레딧 조회
```
GET /api/ai/credits

Response: 200
{
  "planType": "FREE",
  "remaining": 9,
  "totalUsed": 1,
  "expiresAt": null
}
```

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|---|---|---|
| v0.1 | 2025-03 | 최초 작성 (간략형) |

> 구현하면서 Request/Response 상세 스펙 추가 예정
