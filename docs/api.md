# 예비자 (Yebija) — API 명세

> Base URL: `http://localhost:8080` (로컬) / `https://xxx.railway.app` (운영)
> JSON 응답은 `ApiResponse<T>` 래퍼로 감싸짐
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
| `UNAUTHORIZED` | 인증 필요 |
| `FORBIDDEN` | 접근 권한 없음 |
| `INVALID_INPUT` | 요청 본문 또는 파라미터 오류 |
| `CHURCH_NOT_FOUND` | 교회 계정 없음 |
| `INVALID_PASSWORD` | 이메일/비밀번호 불일치 |
| `INVALID_TOKEN` | 유효하지 않은 JWT |
| `EXPIRED_TOKEN` | 만료된 JWT |
| `BIBLE_SCRAPING_FAILED` | 성경 스크래핑 실패 |
| `BIBLE_NOT_FOUND` | 성경 구절 없음 |
| `TEMPLATE_NOT_FOUND` | 템플릿 없음 |
| `WORSHIP_NOT_FOUND` | 예배 없음 |
| `WORSHIP_ITEM_NOT_FOUND` | 예배 항목 없음 |
| `ITEM_MODE_NOT_ALLOWED` | 해당 항목 유형에서 지원하지 않는 모드 |
| `PPT_MERGE_FAILED` | PPT 병합 실패 |
| `FILE_EMPTY` | 업로드 파일 없음 |
| `FILE_INVALID_TYPE` | pptx가 아닌 파일 업로드 |
| `FILE_UPLOAD_FAILED` | 파일 업로드 실패 |
| `FILE_NOT_FOUND` | 저장된 파일 없음 |
| `FILE_DELETE_FAILED` | 파일 삭제 실패 |

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
      { "id": 2, "type": "RESPONSIVE_READING", "seq": 2, "label": "교독문", "defaultMode": "FILE" },
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

// FILE 모드 전환
Request:
{
  "mode": "FILE"
}

Response: 200
{ "id": 2, "mode": "FILE", "content": null }
```

#### FILE 항목의 실제 업로드 흐름

1. `PUT /api/worships/{worshipId}/items/{itemId}` 로 `mode=FILE` 전환
2. `POST /api/files/worship-items/{itemId}` 로 `.pptx` 첨부

`fileStorageKey`는 클라이언트가 직접 쓰지 않는다.

### PPT 생성 & 다운로드
```
POST /api/worships/{worshipId}/export

Response: 200
Content-Type: application/vnd.openxmlformats-officedocument.presentationml.presentation
Content-Disposition: attachment; filename*=UTF-8''%EC%98%88%EB%B9%84%EC%9E%90_%EC%A3%BC%EC%9D%BC%EC%98%88%EB%B0%B0.pptx
(바이너리 스트림)
```

#### 동작 규약

- JSON 래퍼를 사용하지 않는 **예외 엔드포인트**다.
- 성공 시 `.pptx` 바이너리를 그대로 내려준다.
- 파일명은 `Content-Disposition` 헤더를 기준으로 클라이언트가 사용한다.
- 파일이 하나도 없어도 요청은 실패하지 않으며, 기본 디자인의 빈 PPT 한 장을 반환할 수 있다.

#### 실패 응답

`export` 요청이 실패하면 일반 JSON 에러 응답을 반환한다.

```json
{
  "success": false,
  "error": {
    "code": "PPT_MERGE_FAILED",
    "message": "PPT 생성에 실패했습니다."
  }
}
```

주요 실패 케이스:

- `404 WORSHIP_NOT_FOUND` — 다른 교회의 예배이거나 존재하지 않는 예배
- `404 FILE_NOT_FOUND` — FILE 모드 항목에 연결된 저장 파일이 없음
- `500 PPT_MERGE_FAILED` — 병합 중 예외 발생

#### 프론트엔드 표시 문구

- 기본 실패 문구: `PPT 생성 중 오류가 발생했습니다. 모든 항목의 파일이 올바른 .pptx 형식인지 확인해주세요.`
- 테마/마스터 충돌은 실패가 아니라 품질 저하 가능성으로 취급한다.


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

## 5. 파일 업로드 `/api/files` 🔒

### 파일 업로드
```
POST /api/files
Content-Type: multipart/form-data

Form:
  file: (binary)

Response: 201
{
  "id": 1,
  "originalName": "찬양_주일.pptx",
  "storageKey": "uploads/church-1/worship-1/hymn.pptx",
  "fileSize": 2048000,
  "mimeType": "application/vnd.openxmlformats-officedocument.presentationml.presentation"
}
```

### 예배 항목에 파일 첨부
```
POST /api/files/worship-items/{itemId}
Content-Type: multipart/form-data

Form:
  file: (binary)
```

### 파일 삭제
```
DELETE /api/files/{fileId}
Response: 204
```

> 찬송가와 교독문은 현재 MVP에서 별도 조회 API 없이 FILE 첨부 전용으로 운영합니다.

---

## 6. AI 추천 `/api/ai` 🔒 Phase 2

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
