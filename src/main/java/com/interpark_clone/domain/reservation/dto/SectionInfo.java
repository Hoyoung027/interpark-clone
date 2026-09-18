package com.interpark_clone.domain.reservation.dto;

import com.interpark_clone.domain.venue.entity.SectionGrade;

import java.util.List;
import java.util.Set;

public record SectionInfo(
        Long sectionId,
        String sectionName,
        SectionGrade sectionGrade,
        Integer price,
        Integer seatCount,
        Integer availableSeatCount,
        List<SeatInfo> seats
) {
    public static SectionInfo fromSeatSectionsAndAvailability(
            List<SeatSectionInfo> sectionSeats,
            Set<Long> unavailableSeatIds
    ) {
        SeatSectionInfo first = sectionSeats.get(0);

        List<SeatInfo> seats = sectionSeats.stream()
                .map(seat -> SeatInfo.fromSeatSectionAndAvailability(
                        seat,
                        !unavailableSeatIds.contains(seat.seatId())
                ))
                .toList();

        int availableSeatCount = (int) seats.stream()
                .filter(SeatInfo::isAvailable)
                .count();

        return new SectionInfo(
                first.sectionId(),
                first.sectionName(),
                first.sectionGrade(),
                first.price(),
                first.seatCount(),
                availableSeatCount,
                seats
        );
    }
}
