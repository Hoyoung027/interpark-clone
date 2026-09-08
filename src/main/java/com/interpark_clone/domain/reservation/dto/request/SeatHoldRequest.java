package com.interpark_clone.domain.reservation.dto.request;

import com.interpark_clone.domain.payment.entity.PaymentMethod;
import com.interpark_clone.domain.reservation.entity.EventType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public record SeatHoldRequest(
        @NotNull(message = "eventType은 필수입니다.")
        EventType eventType,
        @NotNull(message = "eventRefId는 필수입니다.")
        @Positive(message = "eventRefId는 양수여야 합니다.")
        Long eventRefId,
        @ArraySchema(schema = @Schema(description = "점유할 좌석 ID", example = "1"))
        @Size(max = 10, message = "seatIds는 최대 10개까지 요청할 수 있습니다.")
        @NotEmpty(message = "seatIds는 필수입니다.")
        List<@NotNull(message = "seatIds는 null일 수 없습니다.")
             @Positive(message = "seatId는 양수여야 합니다.") Long> seatIds,
        @NotNull(message = "paymentMethod는 필수입니다.")
        PaymentMethod paymentMethod
) {
    private static final Set<EventType> ALLOWED_EVENTS = EnumSet.of(
            EventType.CONCERT,
            EventType.BASEBALL
    );

    public SeatHoldRequest {
        if (seatIds != null) {
            seatIds = seatIds.stream()
                    .distinct()
                    .toList();
        }
    }

    @AssertTrue(message = "좌석형 이벤트만 점유할 수 있습니다.")
    @Schema(hidden = true)
    public boolean isSeatBasedEvent() {
        return eventType == null || ALLOWED_EVENTS.contains(eventType);
    }
}
