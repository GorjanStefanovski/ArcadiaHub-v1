package com.example.ArcadiaHub_v1.MatchParticipant;

import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.Match.Match;
import com.example.ArcadiaHub_v1.Player.Player;
import jakarta.persistence.*;

@Entity
@IdClass(MatchParticipantId.class)
public class MatchParticipant {

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player playerId;

    @Id
    @ManyToOne
    @JoinColumn(name = "class_id")
    private FightingClass classId;

    @Id
    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match matchId;
    private int accountXpEarned;
    private int classXpEarned;
    private boolean isWinner;

    public MatchParticipant(){
    }

    public MatchParticipant(Player p,FightingClass fc,Match m){
        this.playerId=p;
        this.classId=fc;
        this.matchId=m;
        isWinner=false;
        accountXpEarned=500;
        classXpEarned=500;
    }

    public void setClassId(FightingClass classId) {
        this.classId = classId;
    }

    public void setPlayerId(Player playerId) {
        this.playerId = playerId;
    }

    public void setMatchId(Match matchId) {
        this.matchId = matchId;
    }

    public void setAccountXpEarned(int accountXpEarned) {
        this.accountXpEarned = accountXpEarned;
    }

    public void setClassXpEarned(int classXpEarned) {
        this.classXpEarned = classXpEarned;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public Player getPlayerId() {
        return playerId;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public int getClassXpEarned() {
        if(isWinner){
            return 1000;
        }
        return 500;
    }

    public int getAccountXpEarned() {
        if(isWinner){
            return 1000;
        }
        return 500;
    }

    public FightingClass getClassId() {
        return classId;
    }

    public Match getMatchId() {
        return matchId;
    }
}
