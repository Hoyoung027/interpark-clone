package com.interpark_clone.domain.sport.service;

import com.interpark_clone.domain.sport.entity.GameStatus;
import com.interpark_clone.domain.sport.repository.BaseballClubRepository;
import com.interpark_clone.domain.sport.repository.BaseballGameRepository;
import com.interpark_clone.domain.sport.dto.request.ClubGamesRequest;
import com.interpark_clone.domain.sport.dto.request.SportPageRequest;
import com.interpark_clone.domain.sport.dto.response.ClubSportGameResponse;
import com.interpark_clone.domain.sport.dto.response.SportClubResponse;
import com.interpark_clone.domain.sport.dto.response.WeeklySportGameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SportService {

    private final BaseballClubRepository baseballClubRepository;
    private final BaseballGameRepository baseballGameRepository;

    public Page<SportClubResponse> getAvailableClubs(SportPageRequest request) {

        // 예약 가능 구단 조회
        Pageable pageable = PageRequest.of(request.page(), request.size());

        return baseballClubRepository.findAvailableClubs(GameStatus.OPEN, pageable)
                .map(SportClubResponse::from);
    }

    public Page<WeeklySportGameResponse> getWeeklyGames(SportPageRequest request) {

        // 이번 주 날짜 범위 생성
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime fromAt = monday.atStartOfDay();
        LocalDateTime toAt = monday.plusWeeks(1).atStartOfDay();

        // 이번 주 경기 일정 조회
        Pageable pageable = PageRequest.of(request.page(), request.size());

        return baseballGameRepository.findWeeklyGames(fromAt, toAt, pageable)
                .map(WeeklySportGameResponse::from);
    }

    public Page<ClubSportGameResponse> getClubGames(Long clubId, ClubGamesRequest request) {

        // 조회 대상 경기 상태 생성
        List<GameStatus> statuses = request.includeUpcoming()
                ? List.of(GameStatus.OPEN, GameStatus.SCHEDULED)
                : List.of(GameStatus.OPEN);

        // 특정 구단 경기 목록 조회
        Pageable pageable = PageRequest.of(request.page(), request.size());

        return baseballGameRepository.findGamesByClub(clubId, statuses, pageable)
                .map(ClubSportGameResponse::from);
    }
}
