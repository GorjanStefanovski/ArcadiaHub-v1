package com.example.ArcadiaHub_v1.GameState;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GameLoop {

    private final MatchRegistry matchRegistry; // inject this instead of raw Map
    private final SimpMessagingTemplate messagingTemplate;

    private final double gravity = 0.7;

    public GameLoop(MatchRegistry matchRegistry, SimpMessagingTemplate messagingTemplate) {
        this.matchRegistry = matchRegistry;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 16)
    public void tick() {
        for (MatchState match : matchRegistry.getActiveMatches().values()) {
            updatePlayer(match.getPlayer1());
            updatePlayer(match.getPlayer2());

            // Broadcast full match state to clients
            messagingTemplate.convertAndSend(
                    "/topic/match/" + match.getId() + "/state",
                    match
            );
        }
    }

    private void updatePlayer(PlayerState player) {
        player.setX(player.getX() + player.getVx());
        player.setY(player.getY() + player.getVy());
        player.setVy(player.getVy() + gravity);

        if (player.getY() > 0) {
            player.setY(0);
            player.setVy(0);
        }
    }
}

