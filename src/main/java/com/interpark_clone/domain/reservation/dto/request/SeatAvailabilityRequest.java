package com.interpark_clone.domain.reservation.dto.request;

import com.interpark_clone.domain.reservation.entity.EventType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record SeatAvailabilityRequest(
        @NotNull(message = "eventType은 필수입니다.")
        EventType eventType,
        @NotNull(message = "eventRefId는 필수입니다.")
        @Positive(message = "eventRefId는 양수여야 합니다.")
        Long eventRefId,
        @ArraySchema(schema = @Schema(description = "조회할 구역 ID", example = "1"))
        @Size(max = 5, message = "sectionIds는 최대 5개까지 조회할 수 있습니다.")
        @NotEmpty(message = "sectionIds는 필수입니다.")
        List<@NotNull(message = "sectionIds는 null일 수 없습니다.")
             @Positive(message = "sectionId는 양수여야 합니다.") Long> sectionIds
) {
    private static final Set<EventType> ALLOWED_EVENTS = EnumSet.of(
            EventType.CONCERT,
            EventType.BASEBALL
    );

    public SeatAvailabilityRequest {
        // 중복 section ID 통일
        if (sectionIds != null) {
            sectionIds = sectionIds.stream()
                    .distinct()
                    .toList();
        }
    }

    @AssertTrue(message = "좌석형 이벤트만 조회할 수 있습니다.")
    @Schema(hidden = true)
    public boolean isSeatBasedEvent() {
        return eventType == null || ALLOWED_EVENTS.contains(eventType);
    }
}
