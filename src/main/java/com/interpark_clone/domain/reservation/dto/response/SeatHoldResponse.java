package com.interpark_clone.domain.reservation.dto.response;

import com.interpark_clone.domain.payment.entity.Payment;
import com.interpark_clone.domain.reservation.entity.Reservation;
import com.interpark_clone.domain.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;

public record SeatHoldResponse(
        Long reservationId,
        Long paymentId,
        ReservationStatus status,
        Integer totalPrice,
        Integer ticketQuantity,
        LocalDateTime expiresAt
) {
    public static SeatHoldResponse of(
            Reservation reservation,
            Payment payment,
            LocalDateTime expiresAt
    ) {
        return new SeatHoldResponse(
                reservation.getId(),
                payment.getId(),
                reservation.getStatus(),
                reservation.getTotalPrice(),
                reservation.getTicketQuantity(),
                expiresAt
        );
    }
}
