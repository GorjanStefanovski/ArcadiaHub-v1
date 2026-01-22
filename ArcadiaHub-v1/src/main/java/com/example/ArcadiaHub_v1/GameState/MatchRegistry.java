package com.example.ArcadiaHub_v1.GameState;

import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.PlayerClassProgression.PlayerClassProgression;
import com.example.ArcadiaHub_v1.PlayerClassProgression.PlayerProgressionRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchRegistry {

    private final Map<Long, MatchState> activeMatches = new ConcurrentHashMap<>();
    private final PlayerProgressionRepository playerProgressionRepository;

    public MatchRegistry(PlayerProgressionRepository playerProgressionRepository) {
        this.playerProgressionRepository = playerProgressionRepository;
    }

    public Map<Long, MatchState> getActiveMatches() {
        return activeMatches;
    }

    public MatchState getOrCreateMatch(Long matchId) {
        return activeMatches.computeIfAbsent(matchId, MatchState::new);
    }

    public void setupMatch(MatchState match, Player p1, FightingClass fc1, Player p2, FightingClass fc2) {
        PlayerClassProgression prog1 = playerProgressionRepository.findPlayerClassProgressionByFcAndP(fc1, p1);
        match.getPlayer1().setHealth((int) prog1.getHealth());
        match.getPlayer1().setDamage(prog1.getDamage());
        match.getPlayer1().setSpeed(prog1.getSpeed());
        match.getPlayer1().setClassId(fc1.getFcId());
        match.getPlayer1().setPlayerId(p1.getId());

        PlayerClassProgression prog2 = playerProgressionRepository.findPlayerClassProgressionByFcAndP(fc2, p2);
        match.getPlayer2().setHealth((int) prog2.getHealth());
        match.getPlayer2().setDamage(prog2.getDamage());
        match.getPlayer2().setSpeed(prog2.getSpeed());
        match.getPlayer2().setClassId(fc2.getFcId());
        match.getPlayer2().setPlayerId(p2.getId());

        if (fc1.getFcId() == 1) {
            match.getPlayer1().setAttackOffsetX(100);
            match.getPlayer1().setAttackWidth(160);
        } else {
            match.getPlayer1().setAttackOffsetX(-170);
            match.getPlayer1().setAttackWidth(170);
        }

        if (fc2.getFcId() == 1) {
            match.getPlayer2().setAttackOffsetX(100);
            match.getPlayer2().setAttackWidth(160);
        } else {
            match.getPlayer2().setAttackOffsetX(-170);
            match.getPlayer2().setAttackWidth(170);
        }
    }

    public void removeMatch(Long matchId) {
        activeMatches.remove(matchId);
    }
}

