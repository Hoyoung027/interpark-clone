# API List

콘서트/전시 화면 구현에 필요한 API와 콘서트/전시 공통 오픈 예정/통합 검색 API 명세입니다.

## Common

API 기본 prefix는 `/api/v1`을 사용한다.

목록 API는 모두 pagination을 적용한다.

Enum 필드는 API 응답에서 enum code만 반환한다. 화면 표시 문구는 클라이언트에서 매핑한다.

### Response

```json
{
  "statusCode": 200,
  "isSuccess": true,
  "message": "조회 성공",
  "payload": {}
}
```

### Page Response

```json
{
  "statusCode": 200,
  "isSuccess": true,
  "message": "조회 성공",
  "pageInfo": {
    "page": 0,
    "size": 20,
    "hasNext": true,
    "totalElements": 100,
    "totalPages": 5
  },
  "payload": []
}
```

---

## Auth API

### 1. 회원가입

```http
POST /api/v1/auth/signup
```

#### Request Body

```json
{
  "email": "user@example.com",
  "password": "password123!"
}
```

#### Response Header

```http
Set-Cookie: accessToken=...
Set-Cookie: refreshToken=...
```

#### Response Payload

```json
{
  "memberKey": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Error

| HTTP | Code | 메시지 |
|---:|---|---|
| 400 | `VALIDATION_ERROR` | 입력값이 올바르지 않습니다. |
| 409 | `MEMBER_ALREADY_EXISTS` | 이미 가입된 회원입니다. |

---

### 2. 로그인

```http
POST /api/v1/auth/login
```

#### Request Body

```json
{
  "email": "user@example.com",
  "password": "password123!"
}
```

#### Response Header

```http
Set-Cookie: accessToken=...
Set-Cookie: refreshToken=...
```

#### Response Payload

```json
{
  "memberKey": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Error

| HTTP | Code | 메시지 |
|---:|---|---|
| 400 | `VALIDATION_ERROR` | 입력값이 올바르지 않습니다. |
| 401 | `LOGIN_FAILED` | 이메일 혹은 비밀번호가 올바르지 않습니다. |

---

### 3. 토큰 재발급

```http
POST /api/v1/auth/reissue
```

#### Request Cookie

```http
Cookie: refreshToken=...
```

#### Response Header

```http
Set-Cookie: accessToken=...
Set-Cookie: refreshToken=...
```

#### Response Payload

```json
{
  "memberKey": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Error

| HTTP | Code | 메시지 |
|---:|---|---|
| 401 | `TOKEN_EMPTY` | 토큰이 비어 있습니다. |
| 401 | `TOKEN_INVALID` | 유효하지 않은 토큰입니다. |
| 401 | `TOKEN_EXPIRED` | 토큰이 만료되었습니다. |

---

### 4. 로그아웃

```http
POST /api/v1/auth/logout
```

#### Request Cookie

```http
Cookie: refreshToken=...
```

#### Response Header

```http
Set-Cookie: accessToken=; Max-Age=0
Set-Cookie: refreshToken=; Max-Age=0
```

#### Response Payload

없음

---

## Concert API

### 1. 일간 랭킹 순 콘서트 목록 조회

```http
GET /api/v1/concerts/rankings/daily
```

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `rankingDate` | `LocalDate` | N | 오늘 | 랭킹 기준일 |
| `genre` | `ConcertGenre` | N | - | 장르 필터 |
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `10` | 페이지 크기 |

#### Ranking Rule

- 1차 구현은 `Reservation` 테이블의 확정 예매 데이터를 실시간 집계한다.
- 콘서트 예매의 `Reservation.eventRefId`는 `concertScheduleId`로 저장한다.
- `ConcertSchedule -> Concert` 관계를 통해 콘서트 단위로 집계한다.
- 기본 랭킹 기준은 확정 좌석 수 내림차순이다.
- `genre`가 있으면 해당 장르의 콘서트만 대상으로 랭킹을 계산한다.

#### Response Payload

```json
[
  {
    "rank": 1,
    "concertId": 1,
    "title": "ano LIVE in SEOUL, KOREA 2026",
    "posterUrl": "https://example.com/poster.jpg",
    "genre": "BALLAD",
    "venueName": "블루스퀘어 우리WON뱅킹홀",
    "startDate": "2026-10-17",
    "endDate": "2026-10-18",
    "saleType": "EXCLUSIVE",
    "ageRating": "ALL"
  }
]
```

---

### 2. 콘서트 클립 목록 조회

```http
GET /api/v1/concerts/clips
```

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `10` | 페이지 크기 |

#### Rule

- `Concert.videoUrl`이 있는 콘서트만 반환한다.
- 별도 필터링이나 정렬 파라미터는 받지 않는다.
- 정렬은 랭킹순으로 고정한다.

#### Response Payload

```json
[
  {
    "concertId": 10,
    "title": "Beat On Street Vol.2 - in Busan",
    "posterUrl": "https://example.com/poster.jpg",
    "videoUrl": "https://example.com/video.mp4"
  }
]
```

---

### 3. 전체 콘서트 목록 조회

```http
GET /api/v1/concerts
```

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `genre` | `ConcertGenre` | N | - | 장르 필터 |
| `region` | `City` | N | - | 지역 필터 |
| `sort` | `String` | N | `ranking` | 정렬: `ranking`, `closingSoon` |
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `20` | 페이지 크기 |

#### Sort Rule

- 기본 정렬은 랭킹순이다.
- `ranking`은 일간 랭킹 API와 동일하게 확정 좌석 수 기준으로 계산한다.
- `closingSoon`은 콘서트 종료일이 가까운 순으로 정렬한다.

#### Response Payload

```json
[
  {
    "concertId": 1,
    "title": "2026 지소쿠리클럽 단독 콘서트 : 와일드 오피스(Wild Office)",
    "posterUrl": "https://example.com/poster.jpg",
    "genre": "INDIE",
    "venueName": "NOL 씨어터",
    "startDate": "2026-08-20",
    "endDate": "2026-08-23",
    "saleType": "EXCLUSIVE",
    "ageRating": "AGE_12"
  }
]
```

---

### 4. 개별 콘서트 정보 조회

```http
GET /api/v1/concerts/{concertId}
```

#### Path Variables

| 이름 | 타입 | 설명 |
|---|---:|---|
| `concertId` | `Long` | 콘서트 ID |

#### Response Payload

```json
{
  "concertId": 1,
  "title": "2026 지소쿠리클럽 단독 콘서트 : 와일드 오피스(Wild Office)",
  "posterUrl": "https://example.com/poster.jpg",
  "videoUrl": "https://example.com/video.mp4",
  "description": "공연 소개",
  "genre": "INDIE",
  "status": "OPEN",
  "saleType": "EXCLUSIVE",
  "ageRating": "AGE_12",
  "startDate": "2026-08-20",
  "endDate": "2026-08-23",
  "schedules": [
    {
      "scheduleId": 10,
      "startDate": "2026-08-20T19:30:00",
      "endDate": "2026-08-20T21:30:00",
      "openAt": "2026-07-22T14:00:00",
      "status": "OPEN",
      "venue": {
        "venueId": 3,
        "name": "NOL 씨어터",
        "city": "SEOUL",
        "address": "서울특별시 ..."
      }
    }
  ]
}
```

#### Error

| HTTP | Code | 메시지 |
|---:|---|---|
| 404 | `CONCERT_NOT_FOUND` | 존재하지 않는 콘서트입니다. |

---

## Exhibition API

### 1. 일간 랭킹 순 전시 목록 조회

```http
GET /api/v1/exhibitions/rankings/daily
```

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `rankingDate` | `LocalDate` | N | 오늘 | 랭킹 기준일 |
| `genre` | `ExhibitionGenre` | N | - | 전시 장르 필터 |
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `10` | 페이지 크기 |

#### Ranking Rule

- 1차 구현은 `Reservation` 테이블의 확정 예매 데이터를 실시간 집계한다.
- 전시 예매의 `Reservation.eventRefId`는 `exhibitionId`로 저장한다.
- 기본 랭킹 기준은 확정 예매 건수 내림차순이다.
- `genre`가 있으면 해당 장르의 전시만 대상으로 랭킹을 계산한다.

#### Response Payload

```json
[
  {
    "rank": 1,
    "exhibitionId": 20,
    "title": "Example Exhibition",
    "posterUrl": "https://example.com/poster.jpg",
    "genre": "EXHIBITION",
    "venueName": "예술의전당",
    "startDate": "2026-08-01",
    "endDate": "2026-09-30",
    "saleType": "EARLY_BIRD",
    "ageRating": "ALL"
  }
]
```

---

### 2. 지역별 전시 목록 조회

```http
GET /api/v1/exhibitions/regions
```

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `region` | `City` | Y | - | 지역 필터 |
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `20` | 페이지 크기 |

#### Rule

- `region`에 해당하는 전시만 반환한다.
- 별도 정렬 파라미터는 받지 않는다.
- 정렬은 랭킹순으로 고정한다.
- 랭킹 기준은 오늘 확정 예매 건수 내림차순이다.

#### Response Payload

```json
[
  {
    "exhibitionId": 20,
    "title": "Example Exhibition",
    "posterUrl": "https://example.com/poster.jpg",
    "genre": "MUSEUM",
    "venueName": "예술의전당",
    "startDate": "2026-08-01",
    "endDate": "2026-09-30",
    "saleType": "DEFAULT",
    "ageRating": "ALL"
  }
]
```

---

### 3. 전체 전시 목록 조회

```http
GET /api/v1/exhibitions
```

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `genre` | `ExhibitionGenre` | N | - | 전시 장르 필터 |
| `region` | `City` | N | - | 지역 필터 |
| `sort` | `String` | N | `ranking` | 정렬: `ranking`, `closingSoon` |
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `20` | 페이지 크기 |

#### Sort Rule

- 기본 정렬은 랭킹순이다.
- `ranking`은 오늘 확정 예매 건수 기준으로 계산한다.
- `closingSoon`은 전시 종료일이 가까운 순으로 정렬한다.

#### Response Payload

```json
[
  {
    "exhibitionId": 20,
    "title": "Example Exhibition",
    "posterUrl": "https://example.com/poster.jpg",
    "genre": "EVENT_FESTIVAL",
    "venueName": "예술의전당",
    "startDate": "2026-08-01",
    "endDate": "2026-09-30",
    "saleType": "EARLY_BIRD",
    "ageRating": "ALL"
  }
]
```

---

## Opening API

### 1. 오픈 예정 공연/전시 목록 조회

```http
GET /api/v1/openings/upcoming
```

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `type` | `String` | N | `ALL` | `ALL`, `CONCERT`, `EXHIBITION` |
| `region` | `City` | N | - | 지역 필터 |
| `sort` | `String` | N | `openAt` | 정렬: `viewCount`, `openAt`, `latest` |
| `from` | `LocalDate` | N | 내일 | 예매 오픈 시작일 |
| `to` | `LocalDate` | N | `from + 6일` | 예매 오픈 종료일 |
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `20` | 페이지 크기 |

#### Rule

- 콘서트는 `ConcertSchedule.openAt` 기준으로 조회한다.
- 전시는 `Exhibition.openAt` 기준으로 조회한다.
- `type=ALL`이면 콘서트와 전시행사를 모두 조회한다.
- `type=CONCERT`이면 콘서트만, `type=EXHIBITION`이면 전시행사만 조회한다.
- `region`이 있으면 공연장/전시장 지역 기준으로 필터링한다.
- `openAt`은 곧 공개되는 순으로 정렬한다.
- `latest`는 등록순으로 정렬한다.
- `viewCount`는 조회수 높은 순으로 정렬한다.
- 날짜 단위로 조회한다. `from=2026-07-21`, `to=2026-07-27`이면 `2026-07-21T00:00:00` 이상, `2026-07-27T23:59:59.999999999` 이하의 오픈 예정 항목을 반환한다.
- `from`, `to`를 모두 생략하면 내일부터 7일간의 오픈 예정 항목을 반환한다.

#### Response Payload

```json
[
  {
    "type": "CONCERT",
    "contentId": 1,
    "scheduleId": 10,
    "title": "2026 Example Concert",
    "posterUrl": "https://example.com/poster.jpg",
    "venueName": "올림픽공원 KSPO DOME",
    "openAt": "2026-07-30T20:00:00",
    "startDate": "2026-08-15",
    "endDate": "2026-08-16",
    "saleType": "MEMBERSHIP_PRE_SALE",
    "ageRating": "ALL",
    "status": "UPCOMING"
  },
  {
    "type": "EXHIBITION",
    "contentId": 20,
    "title": "Example Exhibition",
    "posterUrl": "https://example.com/poster.jpg",
    "venueName": "예술의전당",
    "openAt": "2026-07-25T14:00:00",
    "startDate": "2026-08-01",
    "endDate": "2026-09-30",
    "saleType": "EARLY_BIRD",
    "ageRating": "ALL",
    "status": "UPCOMING"
  }
]
```

---

## Search API

### 6. 통합 검색

```http
GET /api/v1/search
```

#### Query Parameters

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `keyword` | `String` | Y | - | 검색 키워드 |
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `20` | 페이지 크기 |

#### Rule

- 콘서트는 `Concert.title` 또는 연결된 `Venue.name`을 대상으로, 전시는 `Exhibition.title` 또는 `Venue.name`을 대상으로 대소문자 구분 없이 부분 일치(LIKE) 검색한다.
- 콘서트와 전시를 동시에 검색하고, 결과는 `concerts`/`exhibitions`로 분리된 별도 리스트로 반환한다.
- `page`, `size`는 `concerts`, `exhibitions` 양쪽에 동일하게 적용되지만, 각 리스트는 서로 독립적으로 페이징된다(즉, 각자 자신의 `totalElements`/`totalPages`/`hasNext`를 가진다).
- 정렬 기준은 조회수(`viewCount`) 내림차순, 등록일(`createdAt`) 내림차순이다.

#### Response Payload

```json
{
  "concerts": {
    "pageInfo": {
      "page": 0,
      "size": 20,
      "hasNext": false,
      "totalElements": 1,
      "totalPages": 1
    },
    "items": [
      {
        "concertId": 1,
        "title": "2026 지소쿠리클럽 단독 콘서트 : 와일드 오피스(Wild Office)",
        "posterUrl": "https://example.com/poster.jpg",
        "genre": "INDIE",
        "venueName": "NOL 씨어터",
        "startDate": "2026-08-20T19:30:00",
        "endDate": "2026-08-23T21:30:00",
        "saleType": "EXCLUSIVE",
        "ageRating": "AGE_12"
      }
    ]
  },
  "exhibitions": {
    "pageInfo": {
      "page": 0,
      "size": 20,
      "hasNext": false,
      "totalElements": 1,
      "totalPages": 1
    },
    "items": [
      {
        "exhibitionId": 20,
        "title": "Example Exhibition",
        "posterUrl": "https://example.com/poster.jpg",
        "genre": "EXHIBITION",
        "venueName": "예술의전당",
        "startDate": "2026-08-01",
        "endDate": "2026-09-30",
        "saleType": "EARLY_BIRD",
        "ageRating": "ALL"
      }
    ]
  }
}
```

#### Error

| HTTP | Code | 메시지 |
|---:|---|---|
| 400 | `VALIDATION_ERROR` | 입력값이 올바르지 않습니다. |
