package com.interpark_clone.domain.concert.dto.response;

import com.interpark_clone.domain.concert.entity.Concert;
import com.interpark_clone.domain.concert.entity.ConcertGenre;
import com.interpark_clone.domain.concert.entity.ConcertSchedule;
import com.interpark_clone.domain.concert.entity.ConcertStatus;
import com.interpark_clone.domain.venue.entity.City;
import com.interpark_clone.global.enums.AgeRating;
import com.interpark_clone.global.enums.SaleType;

import java.time.LocalDateTime;
import java.util.List;

public record ConcertDetailResponse(
        Long concertId,
        String title,
        String posterUrl,
        String videoUrl,
        String description,
        ConcertGenre genre,
        ConcertStatus status,
        SaleType saleType,
        AgeRating ageRating,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<ScheduleResponse> schedules
) {
    public static ConcertDetailResponse of(Concert concert, List<ConcertSchedule> schedules) {
        LocalDateTime startDate = schedules.stream()
                .map(ConcertSchedule::getStartDate)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        LocalDateTime endDate = schedules.stream()
                .map(ConcertSchedule::getEndDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        return new ConcertDetailResponse(
                concert.getId(),
                concert.getTitle(),
                concert.getPosterUrl(),
                concert.getVideoUrl(),
                concert.getDescription(),
                concert.getGenre(),
                concert.getStatus(),
                concert.getSaleType(),
                concert.getAgeRating(),
                startDate,
                endDate,
                schedules.stream()
                        .map(ScheduleResponse::from)
                        .toList()
        );
    }

    public record ScheduleResponse(
            Long scheduleId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime openAt,
            ConcertStatus status,
            VenueResponse venue
    ) {
        private static ScheduleResponse from(ConcertSchedule schedule) {
            return new ScheduleResponse(
                    schedule.getId(),
                    schedule.getStartDate(),
                    schedule.getEndDate(),
                    schedule.getOpenAt(),
                    schedule.getStatus(),
                    VenueResponse.from(schedule)
            );
        }
    }

    public record VenueResponse(
            Long venueId,
            String name,
            City city,
            String address
    ) {
        private static VenueResponse from(ConcertSchedule schedule) {
            return new VenueResponse(
                    schedule.getVenue().getId(),
                    schedule.getVenue().getName(),
                    schedule.getVenue().getCity(),
                    schedule.getVenue().getAddress()
            );
        }
    }
}
