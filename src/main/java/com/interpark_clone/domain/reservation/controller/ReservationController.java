package com.interpark_clone.domain.reservation.controller;

import com.interpark_clone.domain.reservation.dto.request.SeatHoldRequest;
import com.interpark_clone.domain.reservation.dto.request.SeatAvailabilityRequest;
import com.interpark_clone.domain.reservation.dto.response.SeatHoldResponse;
import com.interpark_clone.domain.reservation.dto.response.SeatAvailabilityResponse;
import com.interpark_clone.domain.reservation.service.ReservationService;
import com.interpark_clone.global.code.SuccessCode;
import com.interpark_clone.global.response.Response;
import com.interpark_clone.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reservation", description = "예약 API")
@Validated
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "좌석 목록 조회", description = "좌석 목록과 상태를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좌석 목록 조회 성공"),
    })
    @GetMapping("/api/v1/reservations/seat-availability")
    public ResponseEntity<Response<SeatAvailabilityResponse>> getUnavailableSeats(
            @Valid @ModelAttribute SeatAvailabilityRequest request
    ) {
        SeatAvailabilityResponse availabilityResponse = reservationService.getSeatAvailability(request);
        Response<SeatAvailabilityResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                availabilityResponse,
                "좌석 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "좌석 점유", description = "좌석 점유를 시작하는 API입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "좌석 점유 성공"),
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/api/v1/reservations/seat-holds")
    public ResponseEntity<Response<SeatHoldResponse>> createSeatHold(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody SeatHoldRequest request
    ) {
        SeatHoldResponse seatHoldResponse = reservationService.createSeatHold(userDetails.getMemberKey(), request);
        Response<SeatHoldResponse> response = Response.success(
                SuccessCode.INSERT_SUCCESS,
                seatHoldResponse,
                "좌석 점유 API"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



}
