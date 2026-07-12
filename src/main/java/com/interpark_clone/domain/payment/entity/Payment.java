package com.interpark_clone.domain.payment.entity;

import com.interpark_clone.domain.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, length = 100)
    private String PGTransactionId;

    @Column(nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Builder
    public Payment(Reservation reservation, Integer amount, PaymentStatus status, PaymentMethod method) {
        this.reservation = reservation;
        this.amount = amount;
        this.status = PaymentStatus.READY;;
        this.method = method;
    }
}
