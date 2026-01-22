package com.example.ArcadiaHub_v1.PlayedMatch;


import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.Match.Match;
import com.example.ArcadiaHub_v1.Player.Player;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@IdClass(PlayedMatchId.class)
@Table(name = "played_matches")
public class PlayedMatch {
    @Id
    @Column(name = "match_id")
    private Long matchId;

    @Id
    @Column(name = "player_one_id")
    private Long playerOneId;

    @Id
    @Column(name = "player_two_id")
    private Long playerTwoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", insertable = false, updatable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_one_id", insertable = false, updatable = false)
    private Player playerOne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_two_id", insertable = false, updatable = false)
    private Player playerTwo;

    private Long fcIdPlayerOne;
    private Long fcIdPlayerTwo;
    private Long winnerId;
    private Long loserId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public PlayedMatch(Match match, Player one, Player two, FightingClass classOne, FightingClass classTwo){
        this.match = match;
        this.matchId = match.getId();
        this.playerOne = one;
        this.playerOneId = playerOne.getId();
        this.playerTwo = two;
        this.playerTwoId = playerTwo.getId();
        this.fcIdPlayerOne = classOne.getFcId();
        this.fcIdPlayerTwo = classTwo.getFcId();
        this.startTime = LocalDateTime.now();
    }

    public PlayedMatch() {
    }

    public Match getMatch() {
        return match;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Long getFcIdPlayerOne() {
        return fcIdPlayerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public Long getFcIdPlayerTwo() {
        return fcIdPlayerTwo;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public Long getLoserId() {
        return loserId;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public void setLoserId(Long loserId) {
        this.loserId = loserId;
    }

    public void setFcIdPlayerTwo(Long fcIdPlayerTwo) {
        this.fcIdPlayerTwo = fcIdPlayerTwo;
    }

    public void setFcIdPlayerOne(Long fcIdPlayerOne) {
        this.fcIdPlayerOne = fcIdPlayerOne;
    }

    public void setPlayerOne(Player playerOne) {
        this.playerOne = playerOne;
    }

    public void setPlayerTwo(Player playerTwo) {
        this.playerTwo = playerTwo;
    }
}
