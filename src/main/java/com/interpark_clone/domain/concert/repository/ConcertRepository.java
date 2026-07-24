package com.interpark_clone.domain.concert.repository;

import com.interpark_clone.domain.concert.dto.response.ConcertClipResponse;
import com.interpark_clone.domain.concert.dto.response.ConcertRankingItemResponse;
import com.interpark_clone.domain.concert.dto.response.ConcertResponse;
import com.interpark_clone.domain.concert.entity.Concert;
import com.interpark_clone.domain.concert.entity.ConcertGenre;
import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.venue.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {

    @Query(
            value = """
                    select new com.interpark_clone.domain.concert.dto.response.ConcertResponse(
                        c.id,
                        c.title,
                        c.posterUrl,
                        c.genre,
                        min(v.name),
                        min(cs.startDate),
                        max(cs.endDate),
                        c.saleType,
                        c.ageRating
                    )
                    from Concert c
                    join ConcertSchedule cs on cs.concert = c
                    join cs.venue v
                    left join Reservation r
                        on r.eventRefId = cs.id
                       and r.eventType = com.interpark_clone.domain.reservation.entity.EventType.CONCERT
                       and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                       and r.createdAt >= :rankingStartAt
                       and r.createdAt < :rankingEndAt
                    left join r.reservationSeats rs
                    where (:genre is null or c.genre = :genre)
                      and (:region is null or v.city = :region)
                    group by c.id, c.title, c.posterUrl, c.genre, c.saleType, c.ageRating, c.createdAt
                    order by count(rs.id) desc, c.createdAt desc
                    """,
            countQuery = """
                    select count(distinct c.id)
                    from Concert c
                    join ConcertSchedule cs on cs.concert = c
                    join cs.venue v
                    where (:genre is null or c.genre = :genre)
                      and (:region is null or v.city = :region)
                    """
    )
    Page<ConcertResponse> findConcertsOrderByRanking(
            ConcertGenre genre,
            City region,
            LocalDateTime rankingStartAt,
            LocalDateTime rankingEndAt,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.interpark_clone.domain.concert.dto.response.ConcertResponse(
                        c.id,
                        c.title,
                        c.posterUrl,
                        c.genre,
                        min(v.name),
                        min(cs.startDate),
                        max(cs.endDate),
                        c.saleType,
                        c.ageRating
                    )
                    from Concert c
                    join ConcertSchedule cs on cs.concert = c
                    join cs.venue v
                    where (:genre is null or c.genre = :genre)
                      and (:region is null or v.city = :region)
                    group by c.id, c.title, c.posterUrl, c.genre, c.saleType, c.ageRating, c.createdAt
                    order by max(cs.endDate) asc, c.createdAt desc
                    """,
            countQuery = """
                    select count(distinct c.id)
                    from Concert c
                    join ConcertSchedule cs on cs.concert = c
                    join cs.venue v
                    where (:genre is null or c.genre = :genre)
                      and (:region is null or v.city = :region)
                    """
    )
    Page<ConcertResponse> findConcertsOrderByClosingSoon(
            ConcertGenre genre,
            City region,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.interpark_clone.domain.concert.dto.response.ConcertClipResponse(
                        c.id,
                        c.title,
                        c.posterUrl,
                        c.videoUrl
                    )
                    from Concert c
                    join ConcertSchedule cs on cs.concert = c
                    left join Reservation r
                        on r.eventRefId = cs.id
                       and r.eventType = com.interpark_clone.domain.reservation.entity.EventType.CONCERT
                       and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                       and r.createdAt >= :rankingStartAt
                       and r.createdAt < :rankingEndAt
                    left join r.reservationSeats rs
                    where c.videoUrl is not null
                      and c.videoUrl <> ''
                    group by c.id, c.title, c.posterUrl, c.videoUrl, c.createdAt
                    order by count(rs.id) desc, c.createdAt desc
                    """,
            countQuery = """
                    select count(distinct c.id)
                    from Concert c
                    where c.videoUrl is not null
                      and c.videoUrl <> ''
                    """
    )
    Page<ConcertClipResponse> findConcertClipsOrderByRanking(
            LocalDateTime rankingStartAt,
            LocalDateTime rankingEndAt,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.interpark_clone.domain.concert.dto.response.ConcertRankingItemResponse(
                        c.id,
                        c.title,
                        c.posterUrl,
                        c.genre,
                        min(v.name),
                        min(cs.startDate),
                        max(cs.endDate),
                        c.saleType,
                        c.ageRating
                    )
                    from Reservation r
                    join r.reservationSeats rs
                    join ConcertSchedule cs on cs.id = r.eventRefId
                    join cs.concert c
                    join cs.venue v
                    where r.eventType = com.interpark_clone.domain.reservation.entity.EventType.CONCERT
                      and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                      and r.createdAt >= :rankingStartAt
                      and r.createdAt < :rankingEndAt
                      and (:genre is null or c.genre = :genre)
                    group by c.id, c.title, c.posterUrl, c.genre, c.saleType, c.ageRating, c.createdAt
                    order by count(rs.id) desc, c.createdAt desc
                    """,
            countQuery = """
                    select count(distinct c.id)
                    from Reservation r
                    join r.reservationSeats rs
                    join ConcertSchedule cs on cs.id = r.eventRefId
                    join cs.concert c
                    where r.eventType = com.interpark_clone.domain.reservation.entity.EventType.CONCERT
                      and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                      and r.createdAt >= :rankingStartAt
                      and r.createdAt < :rankingEndAt
                      and (:genre is null or c.genre = :genre)
                    """
    )
    Page<ConcertRankingItemResponse> findDailyRankingItems(
            ConcertGenre genre,
            LocalDateTime rankingStartAt,
            LocalDateTime rankingEndAt,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.interpark_clone.domain.concert.dto.response.ConcertResponse(
                        c.id,
                        c.title,
                        c.posterUrl,
                        c.genre,
                        min(v.name),
                        min(cs.startDate),
                        max(cs.endDate),
                        c.saleType,
                        c.ageRating
                    )
                    from Concert c
                    join ConcertSchedule cs on cs.concert = c
                    join cs.venue v
                    left join Reservation r
                        on r.eventRefId = cs.id
                       and r.eventType = com.interpark_clone.domain.reservation.entity.EventType.CONCERT
                       and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                    left join r.reservationSeats rs
                    where (
                        lower(c.title) like lower(concat('%', :keyword, '%'))
                        or lower(v.name) like lower(concat('%', :keyword, '%'))
                    )
                      and c.status in :statuses
                      and (:region is null or v.city = :region)
                    group by c.id, c.title, c.posterUrl, c.genre, c.saleType, c.ageRating, c.viewCount, c.createdAt
                    order by
                        case when :sort = 'ranking' then c.viewCount end desc,
                        case when :sort = 'reservationCount' then count(rs.id) end desc,
                        case when :sort = 'closingSoon' then max(cs.endDate) end asc,
                        case when :sort = 'latest' then c.createdAt end desc,
                        c.createdAt desc
                    """,
            countQuery = """
                    select count(distinct c.id)
                    from Concert c
                    join ConcertSchedule cs on cs.concert = c
                    join cs.venue v
                    where (
                        lower(c.title) like lower(concat('%', :keyword, '%'))
                        or lower(v.name) like lower(concat('%', :keyword, '%'))
                    )
                      and c.status in :statuses
                      and (:region is null or v.city = :region)
                    """
    )
    Page<ConcertResponse> searchConcerts(
            String keyword,
            List<ConcertStatus> statuses,
            City region,
            String sort,
            Pageable pageable
    );
}
