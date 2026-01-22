package com.example.ArcadiaHub_v1.GameState;

import com.example.ArcadiaHub_v1.Match.Match;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Controller;

/*
@Controller
public class GameController {

    private final Map<String, Match> matches; // Your active matches map
    private final int playerSpeed = 5;
    private final int jumpVelocity = 20;

    public GameController() {
        matches = new HashMap<>();
    }

    @MessageMapping("/match/{matchId}/move")
    public void handleMove(@DestinationVariable String matchId, MoveMessage msg) {
        Match match = matches.get(matchId);
        if(match == null) return;

        PlayerState player = msg.playerId == 1 ? match.player1 : match.player2;

        switch(msg.key) {
            case "ArrowLeft":
            case "a":
                player.vx = msg.state.equals("DOWN") ? -playerSpeed : 0;
                break;
            case "ArrowRight":
            case "d":
                player.vx = msg.state.equals("DOWN") ? playerSpeed : 0;
                break;
            case "w":
            case "ArrowUp":
                if(msg.state.equals("DOWN")) player.vy = -jumpVelocity;
                break;
            case " ":
            case "ArrowDown":
                if(msg.state.equals("DOWN")) player.attack();
                break;
        }
    }
}
 */