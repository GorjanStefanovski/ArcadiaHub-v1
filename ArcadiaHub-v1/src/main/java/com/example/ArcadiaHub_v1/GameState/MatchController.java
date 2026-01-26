    package com.example.ArcadiaHub_v1.GameState;


    import com.example.ArcadiaHub_v1.Achievment.AchievementService;
    import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
    import com.example.ArcadiaHub_v1.FightingClass.FightingClassRepository;
    import com.example.ArcadiaHub_v1.Match.Match;
    import com.example.ArcadiaHub_v1.Match.MatchRepository;
    import com.example.ArcadiaHub_v1.MatchParticipant.MatchParticipant;
    import com.example.ArcadiaHub_v1.MatchParticipant.MatchParticipantRepository;
    import com.example.ArcadiaHub_v1.PlayedMatch.PlayedMatch;
    import com.example.ArcadiaHub_v1.PlayedMatch.PlayedMatchRepository;
    import com.example.ArcadiaHub_v1.Player.Player;
    import com.example.ArcadiaHub_v1.Player.PlayerRepository;
    import com.example.ArcadiaHub_v1.PlayerClassProgression.PlayerClassProgression;
    import com.example.ArcadiaHub_v1.PlayerClassProgression.PlayerProgressionRepository;
    import org.springframework.messaging.handler.annotation.DestinationVariable;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Controller;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.Map;


    import com.example.ArcadiaHub_v1.Fight.FightController; // Осигурај се дека овој импорт е точен
    import org.springframework.context.annotation.Lazy;

    import java.util.Map;

    @Controller
    public class MatchController {

        private final MatchRegistry matchRegistry;
        private final PlayedMatchRepository playedMatchRepository;
        private final PlayerRepository playerRepository;
        private final FightingClassRepository fightingClassRepository;
        private final PlayerProgressionRepository progressionRepository;
        private final SimpMessagingTemplate messagingTemplate;
        private final MatchParticipantRepository matchParticipantRepository;
        private final MatchRepository matchRepository;
        private final AchievementService achievementService;

        private final FightController fightController;

        public MatchController(MatchRegistry matchRegistry,
                               PlayedMatchRepository playedMatchRepository,
                               PlayerRepository playerRepository,
                               FightingClassRepository fightingClassRepository,
                               PlayerProgressionRepository progressionRepository,
                               SimpMessagingTemplate messagingTemplate,
                               MatchParticipantRepository matchParticipantRepository,
                               MatchRepository matchRepository, AchievementService achievementService,
                               @Lazy FightController fightController) {
            this.matchRegistry = matchRegistry;
            this.playedMatchRepository = playedMatchRepository;
            this.playerRepository = playerRepository;
            this.fightingClassRepository = fightingClassRepository;
            this.progressionRepository = progressionRepository;
            this.messagingTemplate = messagingTemplate;
            this.matchParticipantRepository = matchParticipantRepository;
            this.matchRepository = matchRepository;
            this.achievementService = achievementService;
            this.fightController = fightController;
        }

        @MessageMapping("/match/{matchId}/move")
        public void handleMove(@DestinationVariable Long matchId, MoveMessage msg) {
            MatchState match = matchRegistry.getOrCreateMatch(matchId);
            if (match == null || match.isEnded()) return;

            PlayerState player = msg.getPlayerId() == 1 ? match.getPlayer1() : match.getPlayer2();
            if (player.getHealth() <= 0) return;

            switch (msg.getKey()) {
                case "a": case "ArrowLeft":
                    player.setVx(msg.getState().equals("DOWN") ? -player.getSpeed() : 0);
                    break;
                case "d": case "ArrowRight":
                    player.setVx(msg.getState().equals("DOWN") ? player.getSpeed() : 0);
                    break;
                case "w": case "ArrowUp":
                    if (msg.getState().equals("DOWN") && player.getY() >= 0) player.setVy(-15);
                    break;
                case " ": case "ArrowDown":
                    if (msg.getState().equals("DOWN")) {
                        player.setAttacking(true);
                        new Thread(() -> {
                            try { Thread.sleep(100); } catch (InterruptedException e) {}
                            player.setAttacking(false);
                        }).start();
                    }
                    break;
            }
        }

        @MessageMapping("/match/{matchId}/end")
        @Transactional
        public void handleMatchEnd(@DestinationVariable Long matchId, Map<String, Long> payload) {
            MatchState state = matchRegistry.getOrCreateMatch(matchId);
            if (state == null || state.isEnded()) return;



            state.setEnded(true);
            Long winnerId = payload.get("winnerId");

            PlayedMatch pm = playedMatchRepository.findByMatchId(matchId);
            if (pm != null) {
                pm.setWinnerId(winnerId);
                Long loserId = pm.getPlayerOne().getId().equals(winnerId) ? pm.getPlayerTwo().getId() : pm.getPlayerOne().getId();
                pm.setLoserId(loserId);
                playedMatchRepository.save(pm);

                updateXP(pm.getPlayerOne(), pm.getFcIdPlayerOne(),
                        pm.getPlayerOne().getId().equals(winnerId) ? 500 : 100,
                        pm.getPlayerOne().getId().equals(winnerId), matchId);

                updateXP(pm.getPlayerTwo(), pm.getFcIdPlayerTwo(),
                        pm.getPlayerTwo().getId().equals(winnerId) ? 500 : 100,
                        pm.getPlayerTwo().getId().equals(winnerId), matchId);
                fightController.clearPlayerFromMatch(pm.getPlayerOne().getGoogleSub());
                fightController.clearPlayerFromMatch(pm.getPlayerTwo().getGoogleSub());
            }

            messagingTemplate.convertAndSend("/topic/match/" + matchId + "/redirect", "/home/welcomeUser");
            matchRegistry.removeMatch(matchId);
        }

        private void updateXP(Player player, Long fcId, int amount, boolean isWinner, Long matchId) {
            FightingClass fc = fightingClassRepository.findById(fcId).orElse(null);
            if (fc != null) {
                PlayerClassProgression prog = progressionRepository.findPlayerClassProgressionByFcAndP(fc, player);
                if (prog != null) {
                    prog.addClassXp(amount);
                    progressionRepository.save(prog);
                }
            }

            player.setAccountXP(player.getAccountXP() + amount);
            player.setMatchesPlayed(player.getMatchesPlayed() + 1);
            if (isWinner) player.setMatchesWon(player.getMatchesWon() + 1);
            else player.setMatchesLost(player.getMatchesLost() + 1);
            player.setAccountLevel(player.getAccountXP() / 500);
            playerRepository.save(player);
            achievementService.checkAndAssignAchievements(player);
        }
    }
