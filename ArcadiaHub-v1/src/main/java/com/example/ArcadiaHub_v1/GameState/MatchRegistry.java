package com.example.ArcadiaHub_v1.GameState;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchRegistry {

    private final Map<Long, MatchState> activeMatches = new ConcurrentHashMap<>();

    public Map<Long, MatchState> getActiveMatches() {
        return activeMatches;
    }

    public MatchState getOrCreateMatch(Long matchId) {
        return activeMatches.computeIfAbsent(matchId, MatchState::new);
    }

    public void removeMatch(Long matchId) {
        activeMatches.remove(matchId);
    }
}

