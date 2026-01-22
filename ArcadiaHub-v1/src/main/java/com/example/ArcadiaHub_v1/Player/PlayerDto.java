package com.example.ArcadiaHub_v1.Player;

public record PlayerDto(String username,Integer matchesPlayed,Integer matchesLost,Integer matchesWon,Integer accountLevel,Long hoursPlayed,Integer accountXP,double winRate) {
}
