package com.example.ArcadiaHub_v1.PlayerClassProgression;

import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.Player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerProgressionRepository extends JpaRepository<PlayerClassProgression,PlayerClassProgressionId> {
    PlayerClassProgression findPlayerClassProgressionByFcAndP(FightingClass fc, Player p);
}
