package com.example.ArcadiaHub_v1.PlayerClassProgression;


import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.Player.Player;
import org.springframework.stereotype.Service;

@Service
public class PlayerProgressionService {

    private final PlayerProgressionRepository repo;

    public PlayerProgressionService(PlayerProgressionRepository repo) {
        this.repo = repo;
    }

    public void updateClassXp(Player p, FightingClass fc,int classXP){
        PlayerClassProgression current=repo.findPlayerClassProgressionByFcAndP(fc,p);
        int previousXP=current.getClassXP();
        int previousLevel=current.getClassLevel(previousXP);
        current.addClassXp(classXP);
        int newXP=current.getClassXP();
        int newLevel=current.getClassLevel(newXP);
        if(newLevel>previousLevel){
            current.updateSpeed(5.0);
            current.updateDamage(5.0);
            current.updateHealth(10.0);
        }
        repo.save(current);
    }
}
