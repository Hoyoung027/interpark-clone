package com.interpark_clone.domain.concert.dto.response;

public record ConcertClipResponse(
        Long concertId,
        String title,
        String posterUrl,
        String videoUrl
) {
}
