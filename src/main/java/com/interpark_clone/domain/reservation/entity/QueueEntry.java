package com.interpark_clone.domain.reservation.entity;

import com.interpark_clone.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "queue_entry",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_queue_entry_schedule_member",
                        columnNames = {"schedule_type", "schedule_id", "member_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QueueEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queue_entry_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_type", nullable = false)
    private ScheduleType scheduleType;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private Integer rank;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QueueStatus status;

    @Builder
    public QueueEntry(Member member, ScheduleType scheduleType, Long scheduleId, Integer rank, QueueStatus status) {
        this.member = member;
        this.scheduleType = scheduleType;
        this.scheduleId = scheduleId;
        this.rank = rank;
        this.status = QueueStatus.WAITING;
    }
}
