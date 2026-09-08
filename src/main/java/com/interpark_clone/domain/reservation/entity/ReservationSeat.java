package com.interpark_clone.domain.reservation.entity;

import com.interpark_clone.domain.venue.entity.Seat;
import com.interpark_clone.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "reservation_seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reservation_seat",
                        columnNames = {
                                "evnt_type",
                                "event_ref_id",
                                "reservation_id",
                                "seat_id"
                        }
                )
        }
)
@Getter
@NoArgsConstructor
public class ReservationSeat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    @Column(name = "event_ref_id", nullable = false)
    private Long eventRefId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    private ReservationSeat(EventType eventType, Long eventRefId, Reservation reservation, Seat seat) {
        this.eventType = eventType;
        this.eventRefId = eventRefId;
        this.reservation = reservation;
        this.seat = seat;
    }

    static ReservationSeat of(Reservation reservation, Seat seat) {
        return new ReservationSeat(reservation.getEventType(), reservation.getEventRefId(), reservation, seat);
    }
}
