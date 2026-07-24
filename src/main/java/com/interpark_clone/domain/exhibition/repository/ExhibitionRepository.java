package com.interpark_clone.domain.exhibition.repository;

import com.interpark_clone.domain.exhibition.dto.response.ExhibitionRankingItemResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse;
import com.interpark_clone.domain.exhibition.entity.Exhibition;
import com.interpark_clone.domain.exhibition.entity.ExhibitionGenre;
import com.interpark_clone.domain.exhibition.entity.ExhibitionStatus;
import com.interpark_clone.domain.venue.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    @Query(
            value = """
                    select new com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse(
                        e.id,
                        e.title,
                        e.posterUrl,
                        e.genre,
                        v.name,
                        e.startDate,
                        e.endDate,
                        e.saleType,
                        e.ageRating
                    )
                    from Exhibition e
                    join e.venue v
                    left join Reservation r
                        on r.eventRefId = e.id
                       and r.eventType = com.interpark_clone.domain.reservation.entity.EventType.EXHIBITION
                       and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                       and r.createdAt >= :rankingStartAt
                       and r.createdAt < :rankingEndAt
                    where (:genre is null or e.genre = :genre)
                      and (:region is null or v.city = :region)
                    group by e.id, e.title, e.posterUrl, e.genre, v.name, e.startDate, e.endDate, e.saleType, e.ageRating, e.createdAt
                    order by count(r.id) desc, e.createdAt desc
                    """,
            countQuery = """
                    select count(e.id)
                    from Exhibition e
                    join e.venue v
                    where (:genre is null or e.genre = :genre)
                      and (:region is null or v.city = :region)
                    """
    )
    Page<ExhibitionResponse> findExhibitionsOrderByRanking(
            ExhibitionGenre genre,
            City region,
            LocalDateTime rankingStartAt,
            LocalDateTime rankingEndAt,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse(
                        e.id,
                        e.title,
                        e.posterUrl,
                        e.genre,
                        v.name,
                        e.startDate,
                        e.endDate,
                        e.saleType,
                        e.ageRating
                    )
                    from Exhibition e
                    join e.venue v
                    where (:genre is null or e.genre = :genre)
                      and (:region is null or v.city = :region)
                    order by e.endDate asc, e.createdAt desc
                    """,
            countQuery = """
                    select count(e.id)
                    from Exhibition e
                    join e.venue v
                    where (:genre is null or e.genre = :genre)
                      and (:region is null or v.city = :region)
                    """
    )
    Page<ExhibitionResponse> findExhibitionsOrderByClosingSoon(
            ExhibitionGenre genre,
            City region,
            Pageable pageable
    );

    @Query(
            value = """
                    select new com.interpark_clone.domain.exhibition.dto.response.ExhibitionRankingItemResponse(
                        e.id,
                        e.title,
                        e.posterUrl,
                        e.genre,
                        v.name,
                        e.startDate,
                        e.endDate,
                        e.saleType,
                        e.ageRating
                    )
                    from Reservation r
                    join Exhibition e on e.id = r.eventRefId
                    join e.venue v
                    where r.eventType = com.interpark_clone.domain.reservation.entity.EventType.EXHIBITION
                      and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                      and r.createdAt >= :rankingStartAt
                      and r.createdAt < :rankingEndAt
                      and (:genre is null or e.genre = :genre)
                    group by e.id, e.title, e.posterUrl, e.genre, v.name, e.startDate, e.endDate, e.saleType, e.ageRating, e.createdAt
                    order by count(r.id) desc, e.createdAt desc
                    """,
            countQuery = """
                    select count(distinct e.id)
                    from Reservation r
                    join Exhibition e on e.id = r.eventRefId
                    where r.eventType = com.interpark_clone.domain.reservation.entity.EventType.EXHIBITION
                      and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                      and r.createdAt >= :rankingStartAt
                      and r.createdAt < :rankingEndAt
                      and (:genre is null or e.genre = :genre)
                    """
    )
    Page<ExhibitionRankingItemResponse> findDailyRankingItems(
            ExhibitionGenre genre,
            LocalDateTime rankingStartAt,
            LocalDateTime rankingEndAt,
            Pageable pageable
    );

    @Query("""
            select e
            from Exhibition e
            join fetch e.venue
            where e.openAt >= :fromAt
              and e.openAt <= :toAt
              and e.status = :status
              and (:region is null or e.venue.city = :region)
            order by e.openAt asc
            """)
    List<Exhibition> findUpcomingExhibitions(
            LocalDateTime fromAt,
            LocalDateTime toAt,
            ExhibitionStatus status,
            City region
    );

    @Query(
            value = """
                    select new com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse(
                        e.id,
                        e.title,
                        e.posterUrl,
                        e.genre,
                        v.name,
                        e.startDate,
                        e.endDate,
                        e.saleType,
                        e.ageRating
                    )
                    from Exhibition e
                    join e.venue v
                    left join Reservation r
                        on r.eventRefId = e.id
                       and r.eventType = com.interpark_clone.domain.reservation.entity.EventType.EXHIBITION
                       and r.status = com.interpark_clone.domain.reservation.entity.ReservationStatus.CONFIRMED
                    where (
                        lower(e.title) like lower(concat('%', :keyword, '%'))
                        or lower(v.name) like lower(concat('%', :keyword, '%'))
                    )
                      and e.status in :statuses
                      and (:region is null or v.city = :region)
                    group by e.id, e.title, e.posterUrl, e.genre, v.name, e.startDate, e.endDate, e.saleType, e.ageRating, e.viewCount, e.createdAt
                    order by
                        case when :sort = 'ranking' then e.viewCount end desc,
                        case when :sort = 'reservationCount' then count(r.id) end desc,
                        case when :sort = 'closingSoon' then e.endDate end asc,
                        case when :sort = 'latest' then e.createdAt end desc,
                        e.createdAt desc
                    """,
            countQuery = """
                    select count(e)
                    from Exhibition e
                    join e.venue v
                    where (
                        lower(e.title) like lower(concat('%', :keyword, '%'))
                        or lower(v.name) like lower(concat('%', :keyword, '%'))
                    )
                      and e.status in :statuses
                      and (:region is null or v.city = :region)
                    """
    )
    Page<ExhibitionResponse> searchExhibitions(
            String keyword,
            List<ExhibitionStatus> statuses,
            City region,
            String sort,
            Pageable pageable
    );
}
