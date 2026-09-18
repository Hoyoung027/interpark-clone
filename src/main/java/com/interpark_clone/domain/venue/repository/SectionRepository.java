package com.interpark_clone.domain.venue.repository;

import com.interpark_clone.domain.venue.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query("""
            select s
            from Section s
            where s.venue.id = :venueId
            order by s.id asc
            """)
    List<Section> findByVenueId(@Param("venueId") Long venueId);
}
