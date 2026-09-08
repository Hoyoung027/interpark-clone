package com.interpark_clone.domain.venue.dto.response;

import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.domain.venue.entity.Section;
import com.interpark_clone.domain.venue.entity.SectionGrade;
import com.interpark_clone.domain.venue.entity.Venue;

import java.util.List;

public record StadiumResponse(
        Long venueId,
        String name,
        City city,
        String address,
        String imageUrl,
        Integer totalSeatCount,
        List<SectionResponse> sections
) {
    public static StadiumResponse from(Venue venue, List<Section> sections) {
        return new StadiumResponse(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getAddress(),
                venue.getImageUrl(),
                venue.getTotalSeatCount(),
                sections.stream()
                        .map(SectionResponse::from)
                        .toList()
        );
    }

    public record SectionResponse(
            Long sectionId,
            String name,
            SectionGrade grade,
            Integer price,
            Integer seatCount
    ) {
        private static SectionResponse from(Section section) {
            return new SectionResponse(
                    section.getId(),
                    section.getName(),
                    section.getGrade(),
                    section.getPrice(),
                    section.getSeatCount()
            );
        }
    }
}
