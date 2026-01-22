package com.example.ArcadiaHub_v1.TestingController;


import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class TestSocketController {

    @MessageMapping("/match/{matchId}/test")
    @SendTo("/topic/match/{matchId}")
    public String test(@DestinationVariable Long matchId, String message){
        return message;
    }
}
