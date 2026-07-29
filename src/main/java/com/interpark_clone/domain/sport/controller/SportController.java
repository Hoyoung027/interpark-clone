package com.interpark_clone.domain.sport.controller;

import com.interpark_clone.domain.sport.dto.request.ClubGamesRequest;
import com.interpark_clone.domain.sport.dto.request.SportPageRequest;
import com.interpark_clone.domain.sport.dto.response.ClubSportGameResponse;
import com.interpark_clone.domain.sport.dto.response.SportClubResponse;
import com.interpark_clone.domain.sport.dto.response.WeeklySportGameResponse;
import com.interpark_clone.domain.sport.service.SportService;
import com.interpark_clone.global.code.SuccessCode;
import com.interpark_clone.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@Tag(name = "Sport", description = "스포츠 API")
@Validated
@RestController
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @Operation(summary = "예약 가능 구단 목록 조회", description = "예매 가능한 경기가 있는 구단 목록을 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/sports/clubs/available")
    public ResponseEntity<Response<List<SportClubResponse>>> getAvailableClubs(
            @Valid @ModelAttribute SportPageRequest request
    ) {
        Page<SportClubResponse> clubs = sportService.getAvailableClubs(request);
        Response<List<SportClubResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                clubs,
                "예약 가능 구단 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "이번 주 경기 일정 조회", description = "이번 주 월요일부터 일요일까지의 경기 일정을 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/sports/games/weekly")
    public ResponseEntity<Response<List<WeeklySportGameResponse>>> getWeeklyGames(
            @Valid @ModelAttribute SportPageRequest request
    ) {
        Page<WeeklySportGameResponse> games = sportService.getWeeklyGames(request);
        Response<List<WeeklySportGameResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                games,
                "이번 주 경기 일정 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "특정 구단 경기 목록 조회", description = "특정 구단의 예매 가능 경기 또는 오픈 예정 포함 경기 목록을 페이지 단위로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/sports/clubs/{clubId}/games")
    public ResponseEntity<Response<List<ClubSportGameResponse>>> getClubGames(
            @Parameter(description = "조회할 야구 구단 ID", required = true, example = "1")
            @Positive @PathVariable Long clubId,
            @Valid @ModelAttribute ClubGamesRequest request
    ) {
        Page<ClubSportGameResponse> games = sportService.getClubGames(clubId, request);
        Response<List<ClubSportGameResponse>> response = Response.success(
                SuccessCode.GET_SUCCESS,
                games,
                "특정 구단 경기 목록 조회 API"
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
