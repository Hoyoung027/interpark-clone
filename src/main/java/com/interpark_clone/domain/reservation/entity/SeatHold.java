package com.interpark_clone.domain.reservation.entity;

import com.interpark_clone.domain.member.entity.Member;
import com.interpark_clone.domain.venue.entity.Seat;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "seat_hold",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_hold_seat_schedule",
                        columnNames = {"seat_id", "schedule_type", "schedule_id"}
                )
        }
)
@Getter
@NoArgsConstructor
public class SeatHold {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false, length = 20)
    private ScheduleType scheduleType;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatHoldStatus status;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder
    private SeatHold(Seat seat, Member member, ScheduleType scheduleType, Long scheduleId) {
        this.seat = seat;
        this.member = member;
        this.scheduleType = scheduleType;
        this.scheduleId = scheduleId;
        this.status = SeatHoldStatus.HOLDING;
        this.expiresAt = LocalDateTime.now().plus(HOLD_DURATION);
    }
}
