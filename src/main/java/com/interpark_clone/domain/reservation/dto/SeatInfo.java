package com.interpark_clone.domain.reservation.dto;

public record SeatInfo(
        Long seatId,
        Integer seatRow,
        Integer seatCol,
        boolean isAvailable
) {
    public static SeatInfo fromSeatSectionAndAvailability(
            SeatSectionInfo seat,
            boolean available
    ) {
        return new SeatInfo(
                seat.seatId(),
                seat.seatRow(),
                seat.seatCol(),
                available
        );
    }
}