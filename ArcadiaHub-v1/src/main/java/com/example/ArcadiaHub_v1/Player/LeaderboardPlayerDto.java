package com.example.ArcadiaHub_v1.Player;

public record LeaderboardPlayerDto(String playerName,Integer matchesPlayed,Integer matchesWon,Integer matchesLost,double winRate) {
}
