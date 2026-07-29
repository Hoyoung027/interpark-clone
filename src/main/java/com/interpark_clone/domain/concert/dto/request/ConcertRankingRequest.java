package com.interpark_clone.domain.concert.dto.request;

import com.interpark_clone.domain.concert.entity.ConcertGenre;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ParameterObject
public record ConcertRankingRequest(
        @Schema(description = "랭킹 집계 기준일입니다. 생략하면 오늘 날짜로 조회합니다.", type = "string", format = "date", example = "2026-08-01")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate rankingDate,
        @Parameter(
                description = "콘서트 장르 필터입니다. 장르는 랭킹 집계 범위를 제한하는 필터이며, 순위는 확정 예매 좌석 수 기준입니다.",
                schema = @Schema(type = "string", allowableValues = {
                        "BALLAD", "ROCK_METAL", "RAP_HIPHOP", "JAZZ_SOUL", "DINNER_SHOW", "FOLK_TROT",
                        "OVERSEAS", "FESTIVAL", "FAN_MEETING", "INDIE", "TALK_LECTURE"
                })
        )
        ConcertGenre genre,
        @Schema(description = "페이지 번호입니다. 0부터 시작합니다.", defaultValue = "0", minimum = "0", example = "0")
        @Min(value = 0, message = "page는 0 이상이어야 합니다.")
        Integer page,
        @Schema(description = "페이지 크기입니다.", defaultValue = "10", minimum = "1", maximum = "100", example = "10")
        @Min(value = 1, message = "size는 1 이상이어야 합니다.")
        @Max(value = 100, message = "size는 100 이하여야 합니다.")
        Integer size
) {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;

    public ConcertRankingRequest {
        rankingDate = rankingDate == null ? LocalDate.now() : rankingDate;
        page = page == null ? DEFAULT_PAGE : page;
        size = size == null ? DEFAULT_SIZE : size;
    }
}
