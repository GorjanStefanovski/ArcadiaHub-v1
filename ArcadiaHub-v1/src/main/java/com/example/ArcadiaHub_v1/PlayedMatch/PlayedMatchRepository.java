package com.example.ArcadiaHub_v1.PlayedMatch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayedMatchRepository extends JpaRepository<PlayedMatch,PlayedMatchId> {
    PlayedMatch findByMatchId(Long matchId);

    @Query("SELECT pm FROM PlayedMatch pm WHERE pm.playerOne.googleSub = :sub OR pm.playerTwo.googleSub = :sub")
    PlayedMatch findByPlayerSub(@Param("sub") String sub);
}
