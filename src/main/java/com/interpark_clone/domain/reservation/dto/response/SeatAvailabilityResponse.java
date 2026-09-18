package com.interpark_clone.domain.reservation.dto.response;

import com.interpark_clone.domain.reservation.dto.SectionInfo;
import com.interpark_clone.domain.reservation.entity.EventType;

import java.util.List;

public record SeatAvailabilityResponse(
        EventType eventType,
        Long eventRefId,
        List<SectionInfo> sections
) {
}
