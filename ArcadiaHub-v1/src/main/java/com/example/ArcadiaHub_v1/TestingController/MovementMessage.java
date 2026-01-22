package com.example.ArcadiaHub_v1.TestingController;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/*
@Controller
public class MovementMessage {

    @MessageMapping("/match/{matchId}/move")
    @SendTo("/topic/match/{matchId}/move")
    public MovementDto move(
            @DestinationVariable Long matchId,
            @Payload MovementDto message
    ) {
        return message;
    }
}
 */
