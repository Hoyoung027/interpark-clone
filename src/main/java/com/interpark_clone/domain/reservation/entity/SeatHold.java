package com.interpark_clone.domain.reservation.entity;

import com.interpark_clone.domain.member.entity.Member;
import com.interpark_clone.domain.venue.entity.Seat;
import com.interpark_clone.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "seat_hold"
)
@Getter
@NoArgsConstructor
public class SeatHold extends BaseEntity {

    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_hold_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 20)
    private EventType eventType;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatHoldStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    private SeatHold(Seat seat, Member member, Reservation reservation,
                     EventType eventType, Long scheduleId, LocalDateTime expiresAt) {
        this.seat = seat;
        this.member = member;
        this.reservation = reservation;
        this.eventType = eventType;
        this.scheduleId = scheduleId;
        this.status = SeatHoldStatus.HOLDING;
        this.expiresAt = expiresAt;
    }

    public static LocalDateTime calculateExpiresAt(LocalDateTime now) {
        return now.plus(HOLD_DURATION);
    }

    public static SeatHold create(
            Seat seat,
            Member member,
            Reservation reservation,
            EventType eventType,
            Long scheduleId,
            LocalDateTime expiresAt
    ) {
        return SeatHold.builder()
                .seat(seat)
                .member(member)
                .reservation(reservation)
                .eventType(eventType)
                .scheduleId(scheduleId)
                .expiresAt(expiresAt)
                .build();
    }
}
