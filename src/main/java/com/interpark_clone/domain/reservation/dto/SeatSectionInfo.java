package com.interpark_clone.domain.reservation.dto;

import com.interpark_clone.domain.venue.entity.SectionGrade;

public record SeatSectionInfo(
        Long sectionId,
        String sectionName,
        SectionGrade sectionGrade,
        Integer price,
        Integer seatCount,
        Long seatId,
        Integer seatRow,
        Integer seatCol
) {
}
