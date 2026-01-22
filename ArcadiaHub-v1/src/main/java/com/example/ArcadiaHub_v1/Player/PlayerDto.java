package com.example.ArcadiaHub_v1.Player;

import com.example.ArcadiaHub_v1.Achievment.Achievement;

import java.util.List;

public record PlayerDto(String username, Integer matchesPlayed, Integer matchesLost, Integer matchesWon, Integer accountLevel, Long hoursPlayed, Integer accountXP, double winRate,
                        List<Achievement> achievements) {
}
