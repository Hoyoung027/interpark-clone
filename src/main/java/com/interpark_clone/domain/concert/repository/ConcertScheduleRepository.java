package com.interpark_clone.domain.concert.repository;

import com.interpark_clone.domain.concert.entity.ConcertSchedule;
import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.venue.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    List<ConcertSchedule> findByConcertIdWithVenue(Long concertId);

    @Query("""
            select cs
            from ConcertSchedule cs
            join fetch cs.concert
            join fetch cs.venue
            where cs.openAt >= :fromAt
              and cs.openAt <= :toAt
              and cs.status = :status
              and (:region is null or cs.venue.city = :region)
            order by cs.openAt asc
            """)
    List<ConcertSchedule> findUpcomingSchedules(
            LocalDateTime fromAt,
            LocalDateTime toAt,
            ConcertStatus status,
            City region
    );
}
