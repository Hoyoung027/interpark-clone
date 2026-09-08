package com.interpark_clone.domain.venue.service;

import com.interpark_clone.domain.venue.dto.response.StadiumResponse;
import com.interpark_clone.domain.venue.entity.Section;
import com.interpark_clone.domain.venue.entity.VenueType;
import com.interpark_clone.domain.venue.repository.SectionRepository;
import com.interpark_clone.domain.venue.repository.VenueRepository;
import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepository venueRepository;
    private final SectionRepository sectionRepository;

    public StadiumResponse getStadium(Long venueId) {

        // 경기장 정보 조회
        return venueRepository.findByIdAndType(venueId, VenueType.STADIUM)
                .map(venue -> {
                    List<Section> sections = sectionRepository.findByVenueId(venueId);
                    return StadiumResponse.from(venue, sections);
                })
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.VENUE_NOT_FOUND));

    }
}
