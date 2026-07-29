package com.interpark_clone.domain.concert.controller;

import com.interpark_clone.domain.concert.dto.response.ConcertClipResponse;
import com.interpark_clone.domain.concert.dto.request.ConcertClipRequest;
import com.interpark_clone.domain.concert.dto.response.ConcertDetailResponse;
import com.interpark_clone.domain.concert.dto.response.ConcertRankingResponse;
import com.interpark_clone.domain.concert.dto.request.ConcertRankingRequest;
import com.interpark_clone.domain.concert.dto.request.ConcertRequest;
import com.interpark_clone.domain.concert.dto.response.ConcertResponse;
import com.interpark_clone.domain.concert.service.ConcertService;
import com.interpark_clone.global.code.SuccessCode;
import com.interpark_clone.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Concert", description = "공연 API")
@Validated
@RestController
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;

    @Operation(summary = "콘서트 목록 조회", description = "장르/지역으로 필터링하고 오늘 확정 예매 수 기준 랭킹순 또는 공연 종료 임박순으로 콘서트 목록을 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/concerts")
    public ResponseEntity<Response<List<ConcertResponse>>> getConcerts(
            @Valid @ModelAttribute ConcertRequest request
    ) {
        Page<ConcertResponse> concerts = concertService.getConcertList(request);
        Response<List<ConcertResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                concerts,
                "콘서트 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "콘서트 클립 목록 조회", description = "영상 URL이 등록된 콘서트를 오늘 확정 예매 수 기준 랭킹순으로 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/concerts/clips")
    public ResponseEntity<Response<List<ConcertClipResponse>>> getConcertClips(
            @Valid @ModelAttribute ConcertClipRequest request
    ) {
        Page<ConcertClipResponse> clips = concertService.getConcertClips(request);
        Response<List<ConcertClipResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                clips,
                "콘서트 클립 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "콘서트 상세 조회", description = "콘서트 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 콘서트")
    })
    @GetMapping("/api/v1/concerts/{concertId}")
    public ResponseEntity<Response<ConcertDetailResponse>> getConcert(
            @Parameter(description = "조회할 콘서트 ID", required = true, example = "1")
            @Positive @PathVariable Long concertId
    ) {
        ConcertDetailResponse concert = concertService.getConcert(concertId);
        Response<ConcertDetailResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                concert,
                "콘서트 상세 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "일간 랭킹 콘서트 목록 조회", description = "특정 날짜(기본값: 오늘)의 확정 예매 수로 집계한 일간 랭킹을 페이지 단위로 조회합니다. 장르는 순위 산정 후 분류가 아니라 집계 대상 필터로 적용됩니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/concerts/rankings/daily")
    public ResponseEntity<Response<List<ConcertRankingResponse>>> getDailyRankings(
            @Valid @ModelAttribute ConcertRankingRequest request
    ) {
        Page<ConcertRankingResponse> rankings = concertService.getDailyRankings(request);
        Response<List<ConcertRankingResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                rankings,
                "일간 랭킹 콘서트 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
