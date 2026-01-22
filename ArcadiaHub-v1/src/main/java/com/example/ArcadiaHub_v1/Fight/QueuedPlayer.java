package com.example.ArcadiaHub_v1.Fight;

import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.Player.Player;

public record QueuedPlayer(Player player, FightingClass fc) {
}
