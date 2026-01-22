package com.example.ArcadiaHub_v1.GameState;


import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/*
@Controller
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }


    @MessageMapping("/move")
    public void handleMove(@Payload InputMessage input) {
        matchService.onInput(input);
    }


    @MessageMapping("/move")
    @SendTo("/topic/match/{matchId}/state")
    public Map<String, Object> handleMove(@DestinationVariable Long matchId, @Payload InputMessage input) {
        // For now just echo back something visible
        Map<String, Object> state = new HashMap<>();
        state.put("playerNumber", input.playerNumber());
        state.put("key", input.key());
        state.put("state", input.state());
        state.put("timestamp", System.currentTimeMillis());
        // Later: real positions, velocity, sprite, health...

        System.out.println("Broadcasting state for match " + matchId + ": " + state);

        return state;
    }
 */

import com.example.ArcadiaHub_v1.GameState.MatchRegistry;
import com.example.ArcadiaHub_v1.GameState.MatchState;
import com.example.ArcadiaHub_v1.GameState.MoveMessage;
import com.example.ArcadiaHub_v1.GameState.PlayerState;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
public class MatchController {

    private final MatchRegistry matchRegistry;

    public MatchController(MatchRegistry matchRegistry) {
        this.matchRegistry = matchRegistry;
    }

    @MessageMapping("/match/{matchId}/move")
    public void handleMove(@DestinationVariable Long matchId, MoveMessage msg) {
        MatchState match = matchRegistry.getOrCreateMatch(matchId);
        PlayerState player = msg.getPlayerId() == 1 ? match.getPlayer1() : match.getPlayer2();

        switch (msg.getKey()) {
            case "ArrowLeft":
            case "a":
                player.setVx(msg.getState().equals("DOWN") ? -match.getPlayerSpeed() : 0);
                break;
            case "ArrowRight":
            case "d":
                player.setVx(msg.getState().equals("DOWN") ? match.getPlayerSpeed() : 0);
                break;
            case "w":
            case "ArrowUp":
                if (msg.getState().equals("DOWN")) player.setVy(-match.getJumpVelocity());
                break;
            case " ":
            case "ArrowDown":
                if (msg.getState().equals("DOWN")) player.attack(); // implement attack logic in PlayerState
                break;
        }
    }
}

