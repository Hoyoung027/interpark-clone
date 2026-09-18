# Reservation API Draft

예약/결제 기능 개발을 위한 임시 설계 문서입니다.

현재 목표는 전시처럼 좌석이 없는 수량형 예약과, 콘서트/야구처럼 좌석 선점이 필요한 좌석형 예약을 분리하되 예약/결제 상태 흐름은 공통으로 유지하는 것입니다.

---

## 1. 예약 타입

### 1.1 좌석형 예약

대상:

- 콘서트: `EventType.CONCERT`, `eventRefId = concertScheduleId`
- 야구: `EventType.BASEBALL`, `eventRefId = baseballGameId`

특징:

- 사용자가 좌석을 선택한다.
- 선택한 좌석은 `SeatHold`로 5분간 임시 점유된다.
- 결제 성공 시 예약과 좌석 점유가 확정된다.
- 결제 실패, 취소, 만료 시 좌석 점유가 해제된다.

### 1.2 수량형 예약

대상:

- 전시: `EventType.EXHIBITION`, `eventRefId = exhibitionId`

특징:

- 좌석 선택이 없다.
- 사용자가 티켓 수량만 선택한다.
- 결제 승인 시점에 재고 row를 lock으로 조회하고 잔여 수량을 차감한다.
- 결제 실패 시 별도 좌석 해제는 없다.
- 결제 성공 후 취소/환불 시 차감 수량을 복구한다.

---

## 2. 상태 전이

### 2.1 ReservationStatus

| 상태 | 의미 | 다음 상태 |
|---|---|---|
| `PENDING` | 예약 생성 후 결제 대기 | `CONFIRMED`, `CANCELED`, `EXPIRED` |
| `CONFIRMED` | 결제 완료 후 예약 확정 | `CANCELED` |
| `CANCELED` | 사용자 취소 또는 결제 취소 | - |
| `EXPIRED` | 결제 시간 초과 | - |

### 2.2 SeatHoldStatus

| 상태 | 의미 | 다음 상태 |
|---|---|---|
| `HOLDING` | 5분간 임시 점유 중 | `CONFIRMED`, `RELEASED`, `EXPIRED` |
| `CONFIRMED` | 결제 성공으로 좌석 확정 | - |
| `RELEASED` | 결제 실패/취소로 좌석 해제 | - |
| `EXPIRED` | 선점 시간 만료 | - |

### 2.3 PaymentStatus

| 상태 | 의미 | 다음 상태 |
|---|---|---|
| `READY` | 결제 요청 생성 | `PAID`, `FAILED`, `CANCELLED` |
| `PAID` | 결제 승인 완료 | `CANCELLED` |
| `FAILED` | 결제 실패 | - |
| `CANCELLED` | 결제 취소/환불 | - |

---

## 3. 남은 엔티티 보강 필요 사항

### 3.1 Exhibition 재고 변경 메서드

전시에는 `totalQuantity`, `remainingQuantity`가 추가되어 있으므로, 서비스에서 필드를 직접 변경하지 않도록 재고 변경 메서드를 추가합니다.

- `decreaseRemainingQuantity(int quantity)`
- `increaseRemainingQuantity(int quantity)`

동시성 제어:

- `ExhibitionRepository.findByIdForUpdate(exhibitionId)` 형태로 `@Lock(PESSIMISTIC_WRITE)` 사용
- 결제 승인 트랜잭션 안에서 잔여 수량 검증 후 차감

### 3.2 ReservationSeat 중복 확정 방지

현재 `ReservationSeat`는 `(reservation_id, seat_id)`만 unique입니다.

좌석형 예약에서는 같은 이벤트 회차의 같은 좌석이 중복 확정되면 안 됩니다.

보강 방향:

- `ReservationSeat`에 `eventType`, `eventRefId`를 중복 저장하고 `(event_type, event_ref_id, seat_id)` unique 추가
- 또는 `SeatHold(CONFIRMED)`를 좌석 확정 원장으로 사용

초기 구현 추천:

- `SeatHold`를 점유 원장으로 사용하고, `HOLDING`, `CONFIRMED` 상태를 기준으로 중복 점유를 막는다.
- `SeatHold` 엔티티의 JPA unique 제약은 제거되어 있으므로, PostgreSQL partial unique index를 별도 DB migration으로 적용한다.

### 3.3 Payment 식별자

서버 내부 결제 흐름과 PG webhook 멱등성을 위해 별도 주문 식별자가 필요합니다.

추천 필드:

- `orderId`: 서버가 생성한 결제 주문 ID, unique
- `approvedAt`: 결제 승인 시각
- `failedReason`: 실패 사유, nullable

### 3.4 상태 변경 메서드

상태 변경은 서비스에서 필드를 직접 바꾸지 않고 엔티티 메서드로 처리합니다.

추천 메서드:

- `Reservation.confirm()`
- `Reservation.cancel()`
- `Reservation.expire()`
- `SeatHold.confirm()`
- `SeatHold.release()`
- `SeatHold.expire()`
- `Payment.markPaid(String pgTransactionId)`
- `Payment.markFailed(String failedReason)`
- `Payment.cancel()`

---

## 4. 좌석형 User Journey

### 4.1 좌석 상태 조회

사용자는 콘서트 회차 또는 야구 경기의 좌석 배치도를 조회합니다.

서버는 좌석별 선택 가능 여부를 `isAvailable`로 반환합니다.

- `true`: 선택 가능. 유효한 점유나 확정 내역이 없는 좌석
- `false`: 선택 불가. `CONFIRMED`이거나 `HOLDING`이면서 `expiresAt > 현재 시각`인 점유가 있는 좌석

본인 점유 여부나 선택 불가 사유를 별도 상태로 구분하지 않습니다. 만료 시각이 지난
`HOLDING`은 DB 상태 정리 전이라도 선택 가능한 것으로 판단합니다.

### 4.2 좌석 선점 및 예약 생성

사용자가 좌석을 선택하고 선점 API를 호출합니다.

서버 처리:

1. 회원 인증 확인
2. 이벤트 회차 존재 확인
3. 선택 좌석들이 해당 venue에 속하는지 검증
4. 좌석들을 lock으로 조회
5. 유효한 `SeatHold(HOLDING, CONFIRMED)` 존재 여부 확인
6. `Reservation(PENDING)` 생성
7. `SeatHold(HOLDING, expiresAt = now + 5분)` 생성
8. `Payment(READY)` 생성
9. 예약 ID, 결제 ID, 만료 시각 반환

### 4.3 결제 승인

후속 결제 PR에서 구현합니다. 현재 좌석 점유 PR의 범위에는 포함하지 않습니다.

PG 결제 성공 후 confirm API 또는 webhook에서 처리합니다.

서버 처리:

1. payment/orderId 조회
2. `Payment.READY` 상태 검증
3. `Reservation.PENDING` 상태 검증
4. `SeatHold.HOLDING` 상태 및 만료 여부 검증
5. `Payment.PAID` 변경
6. `Reservation.CONFIRMED` 변경
7. `SeatHold.CONFIRMED` 변경

### 4.4 결제 실패/만료

결제 실패에 따른 상태 변경은 후속 결제 PR에서 구현합니다. 주기적인 만료 상태 정리는
`Todo.md`의 스케줄러 작업으로 관리하며, 현재 좌석 점유 PR의 범위에는 포함하지 않습니다.

결제 실패 또는 5분 만료 시 처리합니다.

서버 처리:

1. `Payment.FAILED` 변경
2. `Reservation.CANCELED` 또는 `EXPIRED` 변경
3. `SeatHold.RELEASED` 또는 `EXPIRED` 변경

---

## 5. 전시 User Journey

### 5.1 예약 요청

사용자는 전시 상세에서 티켓 수량을 선택합니다.

서버 처리:

1. 회원 인증 확인
2. 전시 존재 및 판매 상태 확인
3. 수량 검증
4. `Reservation(PENDING)` 생성
5. `Payment(READY)` 생성
6. 결제 요청 정보 반환

이 시점에는 재고를 차감하지 않습니다.

### 5.2 결제 승인

PG 결제 성공 후 confirm API 또는 webhook에서 처리합니다.

서버 처리:

1. payment/orderId 조회
2. `Payment.READY` 상태 검증
3. `Reservation.PENDING` 상태 검증
4. `Exhibition`을 `PESSIMISTIC_WRITE` lock으로 조회
5. 잔여 수량 검증
6. 잔여 수량 차감
7. `Payment.PAID` 변경
8. `Reservation.CONFIRMED` 변경

### 5.3 결제 취소/환불

확정된 전시 예약이 취소되면 수량을 복구합니다.

서버 처리:

1. reservation/payment 조회
2. `Reservation.CONFIRMED` 상태 검증
3. `Payment.PAID` 상태 검증
4. `Exhibition`을 `PESSIMISTIC_WRITE` lock으로 조회
5. 잔여 수량 복구
6. `Payment.CANCELLED` 변경
7. `Reservation.CANCELED` 변경

---

## 6. 필요한 API 초안

### 6.1 좌석 목록 및 선택 가능 여부 조회

요청한 구역의 전체 좌석을 반환하며, 각 좌석의 `isAvailable`로 선택 가능 여부를 표시합니다.

```http
GET /api/v1/reservations/seat-availability
```

Query Parameters:

| 이름 | 타입 | 필수 | 설명 |
|---|---:|---:|---|
| `eventType` | `EventType` | Y | `CONCERT`, `BASEBALL` |
| `eventRefId` | `Long` | Y | 콘서트 회차 ID 또는 야구 경기 ID |
| `sectionIds` | `List<Long>` | Y | 조회할 구역 ID 목록, 중복 제거 후 1~5개 |

Response Payload:

```json
{
  "eventType": "CONCERT",
  "eventRefId": 10,
  "sections": [
    {
      "sectionId": 1,
      "sectionName": "A구역",
      "sectionGrade": "VIP",
      "price": 150000,
      "seatCount": 2,
      "availableSeatCount": 1,
      "seats": [
        {
          "seatId": 1,
          "seatRow": 1,
          "seatCol": 1,
          "isAvailable": true
        },
        {
          "seatId": 2,
          "seatRow": 1,
          "seatCol": 2,
          "isAvailable": false
        }
      ]
    }
  ]
}
```

### 6.2 좌석형 예약 생성

```http
POST /api/v1/reservations/seat-holds
```

Request Body:

```json
{
  "eventType": "CONCERT",
  "eventRefId": 10,
  "seatIds": [1, 2],
  "paymentMethod": "CARD"
}
```

Response Payload:

```json
{
  "reservationId": 1,
  "paymentId": 1,
  "orderId": "ORD-20260731-000001",
  "status": "PENDING",
  "totalPrice": 300000,
  "ticketQuantity": 2,
  "expiresAt": "2026-07-31T12:05:00"
}
```

### 6.3 전시 예약 생성

```http
POST /api/v1/reservations/exhibitions
```

Request Body:

```json
{
  "exhibitionId": 20,
  "ticketQuantity": 3,
  "paymentMethod": "CARD"
}
```

Response Payload:

```json
{
  "reservationId": 2,
  "paymentId": 2,
  "orderId": "ORD-20260731-000002",
  "status": "PENDING",
  "totalPrice": 45000,
  "ticketQuantity": 3
}
```

### 6.4 결제 승인

```http
POST /api/v1/payments/{paymentId}/confirm
```

Request Body:

```json
{
  "orderId": "ORD-20260731-000001",
  "pgTransactionId": "pg_tx_123456",
  "amount": 300000
}
```

Response Payload:

```json
{
  "reservationId": 1,
  "paymentId": 1,
  "reservationStatus": "CONFIRMED",
  "paymentStatus": "PAID"
}
```

### 6.5 결제 실패 처리

```http
POST /api/v1/payments/{paymentId}/fail
```

Request Body:

```json
{
  "orderId": "ORD-20260731-000001",
  "failedReason": "카드 승인 실패"
}
```

Response Payload:

```json
{
  "reservationId": 1,
  "paymentId": 1,
  "reservationStatus": "CANCELED",
  "paymentStatus": "FAILED"
}
```

### 6.6 예약 취소

```http
POST /api/v1/reservations/{reservationId}/cancel
```

Response Payload:

```json
{
  "reservationId": 1,
  "reservationStatus": "CANCELED",
  "paymentStatus": "CANCELLED"
}
```

### 6.7 내 예약 목록 조회

```http
GET /api/v1/reservations/me
```

Query Parameters:

| 이름 | 타입 | 필수 | 기본값 | 설명 |
|---|---:|---:|---:|---|
| `status` | `ReservationStatus` | N | - | 예약 상태 필터 |
| `page` | `int` | N | `0` | 페이지 번호 |
| `size` | `int` | N | `20` | 페이지 크기 |

### 6.8 예약 상세 조회

```http
GET /api/v1/reservations/{reservationId}
```

---

## 7. 필요한 Repository 기능

### 7.1 ExhibitionRepository

- `findByIdForUpdate(Long exhibitionId)`
- 결제 승인/취소 시 `PESSIMISTIC_WRITE` lock으로 재고 변경

### 7.2 SeatRepository

- `findAllByIdInForUpdate(List<Long> seatIds)`
- 선택 좌석 lock 조회
- section, venue fetch join 필요

### 7.3 SeatHoldRepository

- 유효한 좌석 점유 조회
- 기존 만료/해제 hold 재사용 조회
- 예약별 hold 조회

예시 조건:

- `status = HOLDING and expiresAt > now`
- `status = CONFIRMED`

### 7.4 ReservationRepository

- 내 예약 목록 조회
- 예약 상세 조회
- 결제 confirm/cancel 시 reservation fetch join

### 7.5 PaymentRepository

- `findByOrderId`
- `findByIdWithReservation`
- PG webhook 멱등성 처리를 위한 unique key 조회

---

## 8. 에러 코드 초안

추가 후보:

| Code | HTTP | 메시지 |
|---|---:|---|
| `RESERVATION_NOT_FOUND` | 404 | 존재하지 않는 예약입니다. |
| `PAYMENT_NOT_FOUND` | 404 | 존재하지 않는 결제입니다. |
| `SEAT_NOT_FOUND` | 404 | 존재하지 않는 좌석입니다. |
| `SEAT_ALREADY_HELD` | 409 | 이미 선점된 좌석입니다. |
| `SEAT_ALREADY_RESERVED` | 409 | 이미 예약된 좌석입니다. |
| `SEAT_HOLD_EXPIRED` | 409 | 좌석 선점 시간이 만료되었습니다. |
| `INSUFFICIENT_TICKET_QUANTITY` | 409 | 잔여 티켓 수량이 부족합니다. |
| `INVALID_RESERVATION_STATUS` | 400 | 예약 상태가 올바르지 않습니다. |
| `INVALID_PAYMENT_STATUS` | 400 | 결제 상태가 올바르지 않습니다. |
| `PAYMENT_AMOUNT_MISMATCH` | 400 | 결제 금액이 일치하지 않습니다. |

---

## 9. 구현 순서 제안

1. 남은 엔티티 보강
   - 전시 재고 변경 메서드 추가
   - `Payment.orderId`, `approvedAt`, `failedReason` 추가
   - `Reservation`, `SeatHold`, `Payment` 상태 변경 메서드 추가
   - PostgreSQL partial unique index migration 추가

2. Repository 추가
   - `ReservationRepository`
   - `SeatRepository`
   - `SeatHoldRepository`
   - `PaymentRepository`
   - lock 조회 메서드 추가

3. 좌석 상태 조회 API
   - 좌석형 이벤트의 좌석 상태 계산

4. 좌석형 예약 생성 API
   - 좌석 lock
   - 유효 hold/확정 예약 검증
   - reservation/payment/seatHold 생성

5. 전시 예약 생성 API
   - reservation/payment 생성
   - 재고 차감은 아직 하지 않음

6. 결제 승인 API
   - 좌석형: hold 확인 후 확정
   - 전시: exhibition lock 후 재고 차감 및 확정

7. 결제 실패 API
   - 좌석형 hold release
   - 전시 reservation/payment 실패 처리

8. 예약 취소 API
   - 좌석형 hold release 또는 reservation seat 취소 처리
   - 전시 재고 복구

9. 만료 처리
   - `SeatHold.HOLDING` 중 `expiresAt < now`인 데이터 만료 처리
   - `Reservation.PENDING` 중 결제 제한 시간이 지난 데이터 만료 처리

---

## 10. 초기 구현 기준

초기 버전은 다음 범위로 제한합니다.

- PG 실제 연동은 제외하고 confirm/fail API로 결제 결과를 시뮬레이션한다.
- 전시 재고는 결제 승인 시점에만 차감한다.
- 좌석형 예약은 선점과 예약 생성을 하나의 API에서 처리한다.
- 좌석 점유 시간은 5분으로 고정한다.
- 동시성 제어는 우선 DB pessimistic lock으로 구현한다.
- Redis 대기열, 분산 락, 실제 webhook 멱등성은 후속 단계에서 확장한다.
