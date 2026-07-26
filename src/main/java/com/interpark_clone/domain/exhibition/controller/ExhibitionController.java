package com.interpark_clone.domain.exhibition.controller;

import com.interpark_clone.domain.exhibition.dto.request.ExhibitionRankingRequest;
import com.interpark_clone.domain.exhibition.dto.request.ExhibitionRequest;
import com.interpark_clone.domain.exhibition.dto.request.RegionalExhibitionRequest;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionDetailResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionRankingResponse;
import com.interpark_clone.domain.exhibition.dto.response.ExhibitionResponse;
import com.interpark_clone.domain.exhibition.service.ExhibitionService;
import com.interpark_clone.global.code.SuccessCode;
import com.interpark_clone.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Exhibition", description = "전시/행사 API")
@Validated
@RestController
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @Operation(summary = "전시 상세 조회", description = "전시 ID로 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 전시")
    })
    @GetMapping("/api/v1/exhibitions/{exhibitionId}")
    public ResponseEntity<Response<ExhibitionDetailResponse>> getExhibition(
            @Positive @PathVariable Long exhibitionId
    ) {
        ExhibitionDetailResponse exhibition = exhibitionService.getExhibition(exhibitionId);
        Response<ExhibitionDetailResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                exhibition,
                "전시 상세 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "전시 목록 조회", description = "장르/지역으로 필터링하고 랭킹순 또는 종료 임박순으로 전시 목록을 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/exhibitions")
    public ResponseEntity<Response<List<ExhibitionResponse>>> getExhibitions(
            @Valid @ModelAttribute ExhibitionRequest request
    ) {
        Page<ExhibitionResponse> exhibitions = exhibitionService.getExhibitions(request);
        Response<List<ExhibitionResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                exhibitions,
                "전시 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "지역별 전시 목록 조회", description = "지역으로 필터링한 전시 목록을 랭킹순으로 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/exhibitions/regions")
    public ResponseEntity<Response<List<ExhibitionResponse>>> getRegionalExhibitions(
            @Valid @ModelAttribute RegionalExhibitionRequest request
    ) {
        Page<ExhibitionResponse> exhibitions = exhibitionService.getRegionalExhibitions(request);
        Response<List<ExhibitionResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                exhibitions,
                "지역별 전시 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "일간 랭킹 전시 목록 조회", description = "특정 날짜(기본값: 오늘) 기준 장르별 확정 예매 건수로 집계한 일간 랭킹을 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/exhibitions/rankings/daily")
    public ResponseEntity<Response<List<ExhibitionRankingResponse>>> getDailyRankings(
            @Valid @ModelAttribute ExhibitionRankingRequest request
    ) {
        Page<ExhibitionRankingResponse> rankings = exhibitionService.getDailyRankings(request);
        Response<List<ExhibitionRankingResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                rankings,
                "일간 랭킹 전시 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
