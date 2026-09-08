package com.interpark_clone.domain.payment.repository;

import com.interpark_clone.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
