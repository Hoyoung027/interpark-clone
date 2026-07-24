package com.interpark_clone.domain.venue.controller;

import com.interpark_clone.domain.venue.dto.response.StadiumResponse;
import com.interpark_clone.domain.venue.service.VenueService;
import com.interpark_clone.global.code.SuccessCode;
import com.interpark_clone.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Venue", description = "경기장 API")
@Validated
@RestController
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @Operation(summary = "경기장 상세 조회", description = "경기장 ID로 경기장 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 경기장")
    })
    @GetMapping("/api/v1/venues/{venueId}")
    public ResponseEntity<Response<StadiumResponse>> getStadium(
            @PathVariable Long venueId
    ) {
        StadiumResponse stadium = venueService.getStadium(venueId);
        Response<StadiumResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                stadium,
                "경기장 상세 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
