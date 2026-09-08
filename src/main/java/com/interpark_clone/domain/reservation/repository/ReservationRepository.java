package com.interpark_clone.domain.reservation.repository;

import com.interpark_clone.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
