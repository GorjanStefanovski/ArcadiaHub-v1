package com.example.ArcadiaHub_v1.Achievment;

import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.Player.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final PlayerRepository playerRepository;

    public AchievementService(AchievementRepository achievementRepository, PlayerRepository playerRepository) {
        this.achievementRepository = achievementRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public void checkAndAssignAchievements(Player player) {
        List<Achievement> allAchievements = achievementRepository.findAll();
        for (Achievement achievement : allAchievements) {
            if (player.getAccountLevel() >= achievement.getRequiredLevel() &&
                    !player.getAchievements().contains(achievement)) {
                player.getAchievements().add(achievement);
            }
        }
        playerRepository.save(player);
    }
}