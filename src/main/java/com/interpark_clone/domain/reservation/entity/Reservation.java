package com.interpark_clone.domain.reservation.entity;

import com.interpark_clone.domain.member.entity.Member;
import com.interpark_clone.domain.venue.entity.Seat;
import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.entity.BaseEntity;
import com.interpark_clone.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    @Column(name = "event_ref_id", nullable = false)
    private Long eventRefId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "ticket_quantity", nullable = false)
    private Integer ticketQuantity;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    @Builder
    private Reservation(Member member, EventType eventType, Long eventRefId,
                        List<Seat> seats, Integer totalPrice, Integer ticketQuantity) {
        this.member = member;
        this.eventType = eventType;
        this.eventRefId = eventRefId;
        this.status = ReservationStatus.PENDING;
        this.totalPrice = totalPrice;
        this.ticketQuantity = resolveTicketQuantity(seats, ticketQuantity);
        if (seats != null) {
            seats.forEach(seat -> this.reservationSeats.add(ReservationSeat.of(this, seat)));
        }
    }

    private int resolveTicketQuantity(List<Seat> seats, Integer ticketQuantity) {
        int resolvedQuantity;

        if (seats != null && !seats.isEmpty()) {
            resolvedQuantity = seats.size();
        } else if (ticketQuantity != null) {
            resolvedQuantity = ticketQuantity;
        } else {
            resolvedQuantity = 1;
        }

        if (resolvedQuantity <= 0) {
            throw new BusinessException(BusinessErrorCode.INVALID_RESERVATION_QUANTITY);
        }

        return 1;
    }
}
