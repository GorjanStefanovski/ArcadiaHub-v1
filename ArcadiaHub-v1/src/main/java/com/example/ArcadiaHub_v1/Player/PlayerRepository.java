package com.example.ArcadiaHub_v1.Player;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player,Long> {
    Player findByGoogleSub(String googleSub);
    Player findByUsername(String username);
    // To
    List<Player> findTop5ByOrderByMatchesWonDesc();
    List<Player> findTop10ByOrderByMatchesWonDesc();
    List<Player> findTop5ByOrderByMatchesWonAsc();
    List<Player> findTop10ByOrderByMatchesWonAsc();
    List<Player> findTop5ByOrderByMatchesLostDesc();
    List<Player> findTop10ByOrderByMatchesLostDesc();
    List<Player> findTop5ByOrderByMatchesLostAsc();
    List<Player> findTop10ByOrderByMatchesLostAsc();
    //List<Player> findTop10ByWinRateAsc();
}
