package com.interpark_clone.domain.reservation.repository;

import com.interpark_clone.domain.reservation.entity.EventType;
import com.interpark_clone.domain.reservation.entity.SeatHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface SeatHoldRepository extends JpaRepository<SeatHold, Long> {

    @Query(
            value = """
                SELECT sh.seat.id
                FROM SeatHold sh
                WHERE sh.eventType = :eventType
                AND sh.scheduleId = :eventRefId
                AND sh.seat.section.id IN :sectionIds
                AND (
                      sh.status = com.interpark_clone.domain.reservation.entity.SeatHoldStatus.CONFIRMED
                      OR (
                            sh.status = com.interpark_clone.domain.reservation.entity.SeatHoldStatus.HOLDING
                            AND sh.expiresAt > :now
                      )          
                )
                ORDER BY sh.seat.section.id ASC, sh.seat.seatRow ASC, sh.seat.seatCol ASC
          """
    )
    Set<Long> findUnavailableSeats(
            @Param("eventType") EventType eventType,
            @Param("eventRefId") Long eventRefId,
            @Param("sectionIds") List<Long> sectionIds,
            @Param("now") LocalDateTime now
    );

    @Query(
            value = """
                SELECT sh.seat.id
                FROM SeatHold sh
                WHERE sh.eventType = :eventType
                AND sh.scheduleId = :eventRefId
                AND sh.seat.id IN :seatIds
                AND (
                      sh.status = com.interpark_clone.domain.reservation.entity.SeatHoldStatus.CONFIRMED
                      OR (
                            sh.status = com.interpark_clone.domain.reservation.entity.SeatHoldStatus.HOLDING
                            AND sh.expiresAt > :now
                      )
                )
          """
    )
    Set<Long> findActiveSeatHoldSeatIds(
            @Param("eventType") EventType eventType,
            @Param("eventRefId") Long eventRefId,
            @Param("seatIds") List<Long> seatIds,
            @Param("now") LocalDateTime now
    );
}
