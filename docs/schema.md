# 예비자 (Yebija) — DB 스키마

> MySQL 8.0+ 기준
> DBML 파일: `yebija_schema.dbml`

---

## 테이블 목록

| 테이블 | 역할 |
|---|---|
| `church` | 교회 계정 (서비스 최상위 단위) |
| `church_member` | 교회 구성원 (담당자 여러 명 지원) |
| `worship_template` | 예배 순서 템플릿 |
| `template_item` | 템플릿의 순서 항목 (구조만 정의) |
| `worship` | 예배 인스턴스 (매주 생성) |
| `worship_item` | 예배 순서 항목 — **핵심 테이블** |
| `ai_suggestion` | AI 추천 결과 |
| `credit` | AI 크레딧 관리 |
| `uploaded_file` | 첨부 파일 메타데이터 |

---

## 테이블 상세

### church

교회 계정. 서비스의 최상위 단위.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| name | VARCHAR(100) | NOT NULL | 교회명 |
| denomination | VARCHAR(50) | NOT NULL, DEFAULT 'PRESBYTERIAN' | 교단 |
| admin_email | VARCHAR(200) | NOT NULL, UNIQUE | 대표 관리자 이메일 |
| password_hash | VARCHAR(255) | NOT NULL | |
| created_at | DATETIME | NOT NULL, DEFAULT NOW() | |
| updated_at | DATETIME | NOT NULL, DEFAULT NOW() | |

**denomination 값:** `PRESBYTERIAN` / `METHODIST` / `BAPTIST` / 추후 확장

---

### church_member

교회 구성원. 한 교회에 여러 명 등록 가능.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| church_id | BIGINT | NOT NULL, FK → church.id | |
| email | VARCHAR(200) | NOT NULL, UNIQUE | |
| password_hash | VARCHAR(255) | NOT NULL | |
| name | VARCHAR(50) | NOT NULL | |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'MEMBER' | ADMIN / MEMBER |
| created_at | DATETIME | NOT NULL | |

---

### worship_template

예배 순서 템플릿. 교회별로 여러 개 저장 가능.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| church_id | BIGINT | NOT NULL, FK → church.id | |
| name | VARCHAR(100) | NOT NULL | 예: 주일예배 / 수요예배 |
| description | VARCHAR(255) | | |
| is_default | BOOLEAN | NOT NULL, DEFAULT FALSE | 기본 템플릿 여부 |
| created_at | DATETIME | NOT NULL | |
| updated_at | DATETIME | NOT NULL | |

---

### template_item

템플릿의 순서 항목. **내용은 비어있고 구조만 정의**.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| template_id | BIGINT | NOT NULL, FK → worship_template.id | |
| type | VARCHAR(30) | NOT NULL | 항목 유형 |
| seq | INT | NOT NULL | 순서 (1부터) |
| label | VARCHAR(100) | | 표시 이름 (예: 찬양1, 대표기도) |
| default_mode | VARCHAR(10) | NOT NULL, DEFAULT 'AUTO' | AUTO / FILE |

**type 값:** `HYMN` / `BIBLE` / `RESPONSIVE_READING` / `PRAYER` / `SERMON` / `CUSTOM`

---

### worship

예배 인스턴스. 매주 생성되는 단위.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| church_id | BIGINT | NOT NULL, FK → church.id | |
| template_id | BIGINT | FK → worship_template.id | NULL 허용 (템플릿 삭제 시 기록 보존) |
| worship_date | DATE | NOT NULL | 예배 날짜 |
| title | VARCHAR(200) | | 예배 제목 (선택) |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'DRAFT' | DRAFT / COMPLETED |
| created_by | BIGINT | FK → church_member.id | |
| created_at | DATETIME | NOT NULL | |
| updated_at | DATETIME | NOT NULL | |

---

### worship_item ⭐ 핵심 테이블

예배의 각 순서 항목. `mode`로 자동 생성/파일 첨부를 구분.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| worship_id | BIGINT | NOT NULL, FK → worship.id | |
| type | VARCHAR(30) | NOT NULL | 항목 유형 |
| seq | INT | NOT NULL | 예배 내 순서 |
| label | VARCHAR(100) | | 표시 이름 |
| mode | VARCHAR(10) | NOT NULL, DEFAULT 'AUTO' | AUTO / FILE |
| content | JSON | | AUTO 모드 시 입력 내용 |
| file_storage_key | VARCHAR(500) | | FILE 모드 시 스토리지 경로 |
| created_at | DATETIME | NOT NULL | |
| updated_at | DATETIME | NOT NULL | |

**content JSON 구조 (type별):**

```json
// HYMN
{ "hymnNumber": 304, "title": "그 크신 하나님의 사랑", "verses": [1, 2, 4] }

// BIBLE
{ "book": "요한복음", "chapter": 3, "verseStart": 16, "verseEnd": 16 }

// RESPONSIVE_READING
{ "number": 14, "title": "감사" }

// PRAYER
{ "role": "대표기도", "leader": "홍길동 장로" }

// SERMON
{ "title": "하나님의 사랑", "bibleRef": "요한복음 3:16", "preacher": "홍길동 목사" }

// CUSTOM
{ "text": "자유 형식 텍스트" }
```

---

### ai_suggestion

AI 추천 결과 저장. 크레딧 차감 추적 및 결과 재활용용.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| worship_item_id | BIGINT | NOT NULL, FK → worship_item.id | 연결된 성경봉독 항목 |
| church_id | BIGINT | NOT NULL, FK → church.id | 크레딧 차감 추적용 |
| bible_ref | VARCHAR(100) | NOT NULL | 예: 요한복음 3:16 |
| result | JSON | NOT NULL | Claude API 응답 파싱 결과 |
| tokens_used | INT | NOT NULL, DEFAULT 0 | 사용된 토큰 수 |
| model | VARCHAR(50) | | 사용된 AI 모델명 |
| created_at | DATETIME | NOT NULL | |

**result JSON 구조:**

```json
{
  "theme": "하나님의 사랑과 구원",
  "outline": {
    "intro": "죄인인 우리를 향한 하나님의 시선",
    "points": ["사랑의 크기", "사랑의 방식", "사랑의 목적"],
    "conclusion": "이 사랑에 어떻게 응답할 것인가"
  },
  "subtitles": ["값없이 주신 사랑", "독생자의 의미", "멸망에서 영생으로"],
  "hymnNumbers": [304, 405, 191, 280, 93],
  "relatedVerses": ["롬 5:8", "요일 4:9-10", "엡 2:8-9"]
}
```

---

### credit

AI 기능 크레딧 관리. 교회당 1개 레코드.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| church_id | BIGINT | NOT NULL, UNIQUE, FK → church.id | |
| plan_type | VARCHAR(20) | NOT NULL, DEFAULT 'FREE' | FREE / TICKET / SUBSCRIPTION |
| remaining | INT | NOT NULL, DEFAULT 10 | 남은 크레딧 |
| total_used | INT | NOT NULL, DEFAULT 0 | 누적 사용량 |
| expires_at | DATETIME | | 구독 만료일 |
| updated_at | DATETIME | NOT NULL | |

---

### uploaded_file

첨부 파일 메타데이터. 실제 파일은 스토리지에 저장.

| 컬럼 | 타입 | 제약 | 설명 |
|---|---|---|---|
| id | BIGINT | PK, AUTO_INCREMENT | |
| church_id | BIGINT | NOT NULL, FK → church.id | |
| worship_item_id | BIGINT | FK → worship_item.id | NULL 허용 (미사용 파일) |
| original_name | VARCHAR(255) | NOT NULL | 원본 파일명 |
| storage_key | VARCHAR(500) | NOT NULL, UNIQUE | 스토리지 경로 |
| file_size | BIGINT | NOT NULL | bytes |
| mime_type | VARCHAR(100) | | |
| created_at | DATETIME | NOT NULL | |

---

## 연관관계 요약

```
church
  ├── church_member (1:N)
  ├── worship_template (1:N)
  │     └── template_item (1:N)
  ├── worship (1:N)
  │     └── worship_item (1:N)
  │           ├── ai_suggestion (1:0..1)
  │           └── uploaded_file (1:0..1)
  └── credit (1:1)
```

---

## 설계 원칙

1. **유연한 확장** — `worship_item`의 `type` + `content` JSON 구조로 새 순서 타입 추가 시 스키마 변경 불필요
2. **기록 보존** — 템플릿 삭제 시 `worship.template_id = NULL` (ON DELETE SET NULL)로 예배 기록 보존
3. **캐시 전략** — 성경·찬송가 스크래핑 결과는 Redis에 24시간 캐싱, DB 미저장
4. **크레딧 분리** — AI 사용량은 `ai_suggestion`(결과 저장) + `credit`(잔여량 관리) 두 테이블로 분리 추적

---

*예비자 (Yebija) DB 스키마 문서 v0.1*
