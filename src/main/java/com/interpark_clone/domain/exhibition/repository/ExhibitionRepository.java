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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            value = """
                update Exhibition e
                set e.viewCount = e.viewCount + 1
                where e.id = :exhibitionId
            """
    )
    int increaseViewCount(@Param("exhibitionId") Long exhibitionId);


    @Query("""
            select e
            from Exhibition e
            join fetch e.venue
            where e.id = :exhibitionId
            """)
    Optional<Exhibition> findByIdWithVenue(@Param("exhibitionId") Long exhibitionId);

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
                    order by coalesce(sum(r.ticketQuantity), 0) desc, e.createdAt desc
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
            @Param("genre") ExhibitionGenre genre,
            @Param("region") City region,
            @Param("rankingStartAt") LocalDateTime rankingStartAt,
            @Param("rankingEndAt") LocalDateTime rankingEndAt,
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
            @Param("genre") ExhibitionGenre genre,
            @Param("region") City region,
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
                    order by coalesce(sum(r.ticketQuantity), 0) desc, e.createdAt desc
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
            @Param("genre") ExhibitionGenre genre,
            @Param("rankingStartAt") LocalDateTime rankingStartAt,
            @Param("rankingEndAt") LocalDateTime rankingEndAt,
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
            order by
                case when :sort = 'OPEN_AT' then e.openAt end asc,
                case when :sort = 'LATEST' then e.createdAt end desc,
                case when :sort = 'VIEW' then e.viewCount end desc,
                e.createdAt desc
            """)
    Page<Exhibition> findUpcomingExhibitions(
            @Param("fromAt") LocalDateTime fromAt,
            @Param("toAt") LocalDateTime toAt,
            @Param("status") ExhibitionStatus status,
            @Param("region") City region,
            @Param("sort") String sort,
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
                        case when :sort = 'VIEW' then e.viewCount end desc,
                        case when :sort = 'RESERVATION' then coalesce(sum(r.ticketQuantity), 0) end desc,
                        case when :sort = 'CLOSING_SOON' then e.endDate end asc,
                        case when :sort = 'LATEST' then e.createdAt end desc,
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
            @Param("keyword") String keyword,
            @Param("statuses") List<ExhibitionStatus> statuses,
            @Param("region") City region,
            @Param("sort") String sort,
            Pageable pageable
    );
}
