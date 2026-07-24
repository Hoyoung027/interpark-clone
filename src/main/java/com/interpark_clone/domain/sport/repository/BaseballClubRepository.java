package com.interpark_clone.domain.sport.repository;

import com.interpark_clone.domain.sport.entity.BaseballClub;
import com.interpark_clone.domain.sport.entity.GameStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BaseballClubRepository extends JpaRepository<BaseballClub, Long> {

    @Query(
            value = """
                    select distinct club
                    from BaseballClub club
                    where exists (
                        select 1
                        from BaseballGame game
                        where game.status = :status
                          and (game.homeClub = club or game.awayClub = club)
                    )
                    order by club.name asc
                    """,
            countQuery = """
                    select count(distinct club.id)
                    from BaseballClub club
                    where exists (
                        select 1
                        from BaseballGame game
                        where game.status = :status
                          and (game.homeClub = club or game.awayClub = club)
                    )
                    """
    )
    Page<BaseballClub> findAvailableClubs(GameStatus status, Pageable pageable);
}
