package com.interpark_clone.domain.catalog.controller;

import com.interpark_clone.domain.catalog.dto.request.OpeningRequest;
import com.interpark_clone.domain.catalog.dto.response.OpeningResponse;
import com.interpark_clone.domain.catalog.service.OpeningService;
import com.interpark_clone.global.code.SuccessCode;
import com.interpark_clone.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Opening", description = "오픈 예정 API")
@Validated
@RestController
@RequiredArgsConstructor
public class OpeningController {

    private final OpeningService openingService;

    @Operation(
            summary = "오픈 예정 목록 조회",
            description = "콘서트 회차 또는 전시 중 예매 오픈 예정 항목을 기간/지역으로 필터링하고 오픈일 임박순, 최근 등록순, 조회수순으로 정렬해 페이지 단위로 조회합니다. 기본 조회 대상은 콘서트이며, 기본 기간은 내일부터 7일간입니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/openings/upcoming")
    public ResponseEntity<Response<List<OpeningResponse>>> getUpcomingOpenings(
            @Valid @ModelAttribute OpeningRequest request
    ) {
        Page<OpeningResponse> openings = openingService.getUpcomingOpenings(request);
        Response<List<OpeningResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                openings,
                "오픈 예정 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
