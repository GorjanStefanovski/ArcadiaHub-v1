package com.example.ArcadiaHub_v1.MatchParticipant;


import java.io.Serializable;
import java.util.Objects;

public class MatchParticipantId implements Serializable {

    private Long playerId;
    private Long matchId;
    private Long classId;

    public MatchParticipantId(){
    }

    public MatchParticipantId(Long pId,Long mId,Long cId){
        this.playerId=pId;
        this.matchId=mId;
        this.classId=cId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchParticipantId)) return false;
        MatchParticipantId that = (MatchParticipantId) o;
        return Objects.equals(playerId, that.playerId)
                && Objects.equals(matchId, that.matchId)
                && Objects.equals(classId, that.classId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, matchId, classId);
    }
}
