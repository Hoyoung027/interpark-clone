package com.interpark_clone.domain.venue.repository;

import com.interpark_clone.domain.venue.entity.Venue;
import com.interpark_clone.domain.venue.entity.VenueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface VenueRepository extends JpaRepository<Venue, Long> {

    java.util.Optional<Venue> findByIdAndType(
            @Param("id") Long id,
            @Param("type") VenueType type
    );
}
