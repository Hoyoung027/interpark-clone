package com.interpark_clone.domain.sport.repository;

import com.interpark_clone.domain.sport.entity.BaseballGame;
import com.interpark_clone.domain.sport.entity.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BaseballGameRepository extends JpaRepository<BaseballGame, Long> {

    @Query(
            value = """
                    select game
                    from BaseballGame game
                    join fetch game.homeClub
                    join fetch game.awayClub
                    join fetch game.venue
                    where game.gameDate >= :fromAt
                      and game.gameDate < :toAt
                    order by game.gameDate asc
                    """,
            countQuery = """
                    select count(game.id)
                    from BaseballGame game
                    where game.gameDate >= :fromAt
                      and game.gameDate < :toAt
                    """
    )
    Page<BaseballGame> findWeeklyGames(
            @Param("fromAt") LocalDateTime fromAt,
            @Param("toAt") LocalDateTime toAt,
            Pageable pageable
    );

    @Query(
            value = """
                    select game
                    from BaseballGame game
                    join fetch game.homeClub
                    join fetch game.awayClub
                    join fetch game.venue
                    where (game.homeClub.id = :clubId or game.awayClub.id = :clubId)
                      and game.status in :statuses
                    order by game.gameDate asc
                    """,
            countQuery = """
                    select count(game.id)
                    from BaseballGame game
                    where (game.homeClub.id = :clubId or game.awayClub.id = :clubId)
                      and game.status in :statuses
                    """
    )
    Page<BaseballGame> findGamesByClub(
            @Param("clubId") Long clubId,
            @Param("statuses") List<GameStatus> statuses,
            Pageable pageable
    );
}
