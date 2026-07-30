## 향후 개선 사항

### 쿼리 정확성
- [ ] `ConcertRepository`: 클립 목록 조회 시 본문 쿼리는 `ConcertSchedule`을 inner join하지만 `countQuery`는 조인이 빠져 있어 페이지 수가 실제 결과보다 많게 계산됨
- [ ] `ExhibitionRepository.findUpcomingExhibitions`: `join fetch e.venue`에 별칭이 없어 `e.venue.city` 재참조 시 불필요한 추가 조인 발생 가능

### 성능/인덱스
- [ ] 트래픽 증가 시 주요 목록/랭킹/검색 쿼리에 대해 `EXPLAIN ANALYZE` 기반 복합 인덱스 도입 검토 — 예약 집계, 오픈 예정 조회, 장르/지역 필터, 스포츠 경기 조회를 우선 확인
- [ ] 콘서트/전시 키워드 검색이 선행 와일드카드 LIKE(`%keyword%`)라 일반 B-tree 인덱스를 못 탐 — PostgreSQL trigram 인덱스, 전문검색, 또는 검색 엔진 도입 검토
- [ ] 랭킹순 정렬은 실시간 집계 비용이 커질 수 있음 — 트래픽 증가 시 Redis 캐싱 또는 랭킹 집계 테이블/배치 갱신 검토
- [ ] 조회수 증가는 DB 원자 update로 유실은 막지만 인기 상세 페이지에서 write hot spot이 될 수 있음 — 트래픽 증가 시 Redis 카운터 + 배치 반영 검토
---
