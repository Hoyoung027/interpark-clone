package com.interpark_clone.domain.reservation.entity;

import com.interpark_clone.domain.venue.entity.Seat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "reservation_seat",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_reservation_seat", columnNames = {"reservation_id", "seat_id"})
        }
)
@Getter
@NoArgsConstructor
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    private ReservationSeat(Reservation reservation, Seat seat) {
        this.reservation = reservation;
        this.seat = seat;
    }

    static ReservationSeat of(Reservation reservation, Seat seat) {
        return new ReservationSeat(reservation, seat);
    }
}
