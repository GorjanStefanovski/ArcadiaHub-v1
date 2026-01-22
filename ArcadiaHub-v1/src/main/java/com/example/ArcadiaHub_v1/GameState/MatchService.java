package com.example.ArcadiaHub_v1.GameState;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/*
@Service
public class MatchService {

    private final Map<Long, MatchState> gameStates = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    public MatchService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void onInput(InputMessage input) {
        Long matchId = input.matchId();
        MatchState state = gameStates.computeIfAbsent(matchId, k -> new MatchState(input.matchId()));

        PlayerState fighter = input.playerNumber() == 1 ? state.getPlayer1() : state.getPlayer2();

        // Apply input
        switch (input.key()) {
            case "a", "ArrowLeft" -> fighter.setLeftPressed(input.state().equals("DOWN"));
            case "d", "ArrowRight" -> fighter.setRightPressed(input.state().equals("DOWN"));
            case "w", "ArrowUp" -> { if ("DOWN".equals(input.state())) fighter.setJumpPressed(true); }
            case " ", "ArrowDown" -> { if ("DOWN".equals(input.state())) fighter.setAttackPressed(true); }
        }

        // Simulate one tick (gravity, movement, etc.)
        simulateFighter(fighter);

        // Broadcast updated state to both players
        messagingTemplate.convertAndSend("/topic/match/" + matchId + "/state", state);
    }

    private void simulateFighter(PlayerState f) {
        if (f.isLeftPressed()) f.setVx(-5);
        else if (f.isRightPressed()) f.setVx(5);
        else f.setVx(0);

        f.setX(f.getX() + f.getVx());
        f.setY(f.getY() + f.getVy());

        f.setVy(f.getVy() + 0.7);  // gravity

        if (f.getY() > 400) {  // ground level - adjust
            f.setY(400);
            f.setVy(0);
        }

        if (f.isJumpPressed()) {
            f.setVy(-15);
            f.setJumpPressed(false);
        }

        // Simple sprite logic (expand later)
        if (f.isAttackPressed()) {
            f.setSprite("attack1");
            f.setAttackPressed(false);
        } else if (f.getVy() < 0) f.setSprite("jump");
        else if (f.getVy() > 0) f.setSprite("fall");
        else if (Math.abs(f.getVx()) > 0) f.setSprite("run");
        else f.setSprite("idle");
    }

    private void broadcastState(Long matchId, MatchState state) {
        messagingTemplate.convertAndSend("/topic/match/" + matchId + "/state", state);
    }
}
 */
