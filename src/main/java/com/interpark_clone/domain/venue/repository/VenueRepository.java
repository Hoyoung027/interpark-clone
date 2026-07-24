package com.interpark_clone.domain.venue.repository;

import com.interpark_clone.domain.venue.entity.Venue;
import com.interpark_clone.domain.venue.entity.VenueType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    java.util.Optional<Venue> findByIdAndType(Long id, VenueType type);
}
