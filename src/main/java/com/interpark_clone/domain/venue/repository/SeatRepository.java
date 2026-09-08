package com.interpark_clone.domain.venue.repository;

import com.interpark_clone.domain.reservation.dto.SeatSectionInfo;
import com.interpark_clone.domain.venue.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query(
            value = """
                SELECT new com.interpark_clone.domain.reservation.dto.SeatSectionInfo(
                    se.id,
                    se.name,
                    se.grade,
                    se.price,
                    se.seatCount,
                    s.id,
                    s.seatRow,
                    s.seatCol
                )
                FROM Seat s
                JOIN s.section se
                WHERE s.section.id IN :sectionIds
                ORDER BY se.id ASC, s.seatRow ASC, s.seatCol ASC        
            """
    )
    List<SeatSectionInfo> findSeatSectionInfos(
            @Param("sectionIds") List<Long> sectionIds
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT s
            FROM Seat s
            JOIN FETCH s.section se
            JOIN FETCH se.venue v
            WHERE s.id IN :seatIds
            ORDER BY s.id ASC
            """)
    List<Seat> findAllByIdInWithLock(@Param("seatIds") List<Long> seatIds);

}
