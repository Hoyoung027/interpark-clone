package com.interpark_clone.domain.catalog.controller;

import com.interpark_clone.domain.catalog.dto.request.SearchRequest;
import com.interpark_clone.domain.catalog.dto.response.SearchResponse;
import com.interpark_clone.domain.catalog.service.SearchService;
import com.interpark_clone.global.code.SuccessCode;
import com.interpark_clone.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Search", description = "통합 검색 API")
@Validated
@RestController
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @Operation(summary = "통합 검색", description = "키워드(제목/공연장명)로 콘서트와 전시를 동시에 검색하고, 각각 독립적으로 페이징된 결과로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/search")
    public ResponseEntity<Response<SearchResponse>> search(
            @Valid @ModelAttribute SearchRequest request
    ) {
        SearchResponse result = searchService.search(request);
        Response<SearchResponse> response = Response.success(
                SuccessCode.GET_SUCCESS,
                result,
                "통합 검색 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
