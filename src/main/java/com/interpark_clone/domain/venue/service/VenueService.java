package com.interpark_clone.domain.venue.service;

import com.interpark_clone.domain.venue.dto.response.StadiumResponse;
import com.interpark_clone.domain.venue.entity.VenueType;
import com.interpark_clone.domain.venue.repository.VenueRepository;
import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepository venueRepository;

    public StadiumResponse getStadium(Long venueId) {

        // 경기장 정보 조회
        return venueRepository.findByIdAndType(venueId, VenueType.STADIUM)
                .map(StadiumResponse::from)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.VENUE_NOT_FOUND));

    }
}
