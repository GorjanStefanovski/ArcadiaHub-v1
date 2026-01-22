package com.example.ArcadiaHub_v1.PlayedMatch;

import java.io.Serializable;
import java.util.Objects;

public class PlayedMatchId implements Serializable {

    private Long matchId;
    private Long playerOneId;
    private Long playerTwoId;

    public PlayedMatchId() {}
    public PlayedMatchId(Long matchId, Long playerOneId, Long playerTwoId) {
        this.matchId = matchId;
        this.playerOneId = playerOneId;
        this.playerTwoId = playerTwoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayedMatchId that = (PlayedMatchId) o;
        return Objects.equals(matchId, that.matchId) &&
                Objects.equals(playerOneId, that.playerOneId) &&
                Objects.equals(playerTwoId, that.playerTwoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchId, playerOneId, playerTwoId);
    }
}
