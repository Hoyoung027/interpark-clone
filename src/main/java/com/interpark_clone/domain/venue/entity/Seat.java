package com.interpark_clone.domain.venue.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_seat_section_row_col",
                        columnNames = {"section_id", "seat_row", "seat_col"}
                )
        }
)
@Getter
@NoArgsConstructor
public class Seat {

    public static final int MAX_ROW = 100;
    public static final int MAX_COL = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "seat_row", nullable = false)
    private Integer seatRow;

    @Column(name = "seat_col", nullable = false)
    private Integer seatCol;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false, length = 20)
    private SeatType seatType;

    @Builder
    private Seat(Section section, Integer seatRow, Integer seatCol, SeatType seatType) {
        this.section = section;
        this.seatRow = seatRow;
        this.seatCol = seatCol;
        this.seatType = seatType;
    }
}
