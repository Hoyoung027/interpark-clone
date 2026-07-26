package com.interpark_clone.domain.concert.repository;

import com.interpark_clone.domain.concert.entity.ConcertSchedule;
import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.SortType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {

    @Query("""
            select cs
            from ConcertSchedule cs
            join fetch cs.concert
            join fetch cs.venue
            where cs.concert.id = :concertId
            order by cs.startDate asc
            """)
    List<ConcertSchedule> findByConcertIdWithVenue(@Param("concertId") Long concertId);

    @Query("""
            select cs
            from ConcertSchedule cs
            join fetch cs.concert
            join fetch cs.venue
            where cs.openAt >= :fromAt
              and cs.openAt <= :toAt
              and cs.status = :status
              and (:region is null or cs.venue.city = :region)
            order by
              case when :sort = com.interpark_clone.global.enums.SortType.OPEN_AT then cs.openAt end asc,
              case when :sort = com.interpark_clone.global.enums.SortType.LATEST then cs.concert.createdAt end desc,
              case when :sort = com.interpark_clone.global.enums.SortType.VIEW then cs.concert.viewCount end desc,
              cs.createdAt desc
            """)
    Page<ConcertSchedule> findUpcomingSchedules(
            @Param("fromAt") LocalDateTime fromAt,
            @Param("toAt") LocalDateTime toAt,
            @Param("status") ConcertStatus status,
            @Param("region") City region,
            @Param("sort") SortType sort,
            Pageable pageable
    );
}
