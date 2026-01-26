package com.example.ArcadiaHub_v1.Fight;


import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.FightingClass.FightingClassRepository;
import com.example.ArcadiaHub_v1.GameState.MatchRegistry;
import com.example.ArcadiaHub_v1.GameState.MatchState;
import com.example.ArcadiaHub_v1.Match.Match;
import com.example.ArcadiaHub_v1.Match.MatchRepository;
import com.example.ArcadiaHub_v1.MatchFoundMessage.MatchFoundMessage;
import com.example.ArcadiaHub_v1.PlayedMatch.PlayedMatch;
import com.example.ArcadiaHub_v1.PlayedMatch.PlayedMatchRepository;
import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.Player.PlayerRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


@Controller
@RequestMapping("/fight")
public class FightController {

    private final FightingClassRepository fightingClassRepository;
    private final PlayerRepository playerRepository;
    private final Queue<QueuedPlayer> queuedPlayers = new ConcurrentLinkedQueue<>();
    private final MatchRepository matchRepository;
    private final PlayedMatchRepository playedMatchRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Long> playerToMatch = new ConcurrentHashMap<>();
    private final MatchRegistry matchRegistry;

    public FightController(FightingClassRepository fightingClassRepository,
                           PlayerRepository playerRepository,
                           MatchRepository matchRepository,
                           PlayedMatchRepository playedMatchRepository,
                           SimpMessagingTemplate messagingTemplate,
                           MatchRegistry matchRegistry) {
        this.fightingClassRepository = fightingClassRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.playedMatchRepository = playedMatchRepository;
        this.messagingTemplate = messagingTemplate;
        this.matchRegistry = matchRegistry;
    }

    @GetMapping("/lobby")
    public String goToLobby() {
        return "fight_lobby";
    }

    @PostMapping("/queue")
    public String queue(@RequestParam("chosenClass") int chosenClass, OAuth2AuthenticationToken auth, RedirectAttributes ra) {
        FightingClass light_or_heavy = fightingClassRepository.findByFcId((long) chosenClass);
        Player p = playerRepository.findByGoogleSub(auth.getPrincipal().getAttribute("sub"));
        playerToMatch.remove(auth.getPrincipal().getAttribute("sub"));
        QueuedPlayer player = new QueuedPlayer(p, light_or_heavy);
        queuedPlayers.add(player);

        if (queuedPlayers.size() >= 2) {
            QueuedPlayer one = queuedPlayers.poll();
            QueuedPlayer two = queuedPlayers.poll();

            Match match = new Match();
            match = matchRepository.save(match);

            PlayedMatch currentMatch = new PlayedMatch(match, one.player(), two.player(), one.fc(), two.fc());
            playedMatchRepository.save(currentMatch);

            playerToMatch.put(one.player().getGoogleSub(), match.getId());
            playerToMatch.put(two.player().getGoogleSub(), match.getId());

            messagingTemplate.convertAndSendToUser(one.player().getGoogleSub(), "/queue/match-found", new MatchFoundMessage(match.getId()));
            messagingTemplate.convertAndSendToUser(two.player().getGoogleSub(), "/queue/match-found", new MatchFoundMessage(match.getId()));
        }

        ra.addFlashAttribute("message", "Queued for " + chosenClass);
        return "redirect:/fight/waiting";
    }

    @GetMapping("/check-match")
    @ResponseBody
    public Map<String, Object> checkMatch(OAuth2AuthenticationToken auth) {
        Map<String, Object> response = new HashMap<>();
        if (auth == null || !auth.isAuthenticated()) {
            response.put("matched", false);
            return response;
        }

        String sub = auth.getPrincipal().getAttribute("sub");
        Long matchId = playerToMatch.get(sub);

        if (matchId != null) {
            response.put("matched", true);
            response.put("matchId", matchId);
        } else {
            response.put("matched", false);
            response.put("position", queuedPlayers.size());
        }
        return response;
    }

    @GetMapping("/waiting")
    public String waitingPage(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/fight/lobby";
        }
        String sub = ((OAuth2AuthenticationToken) auth).getPrincipal().getAttribute("sub");
        model.addAttribute("sub", sub);
        return "fight_waiting";
    }

    @GetMapping("/match/{matchId}")
    public String startFight(@PathVariable Long matchId, OAuth2AuthenticationToken auth, Model model) {
        String sub = auth.getPrincipal().getAttribute("sub");
        PlayedMatch playedMatch = playedMatchRepository.findByMatchId(matchId);

        if (playedMatch == null) return "redirect:/fight/lobby";

        FightingClass fc1 = fightingClassRepository.findById(playedMatch.getFcIdPlayerOne()).orElseThrow();
        FightingClass fc2 = fightingClassRepository.findById(playedMatch.getFcIdPlayerTwo()).orElseThrow();

        MatchState state = matchRegistry.getOrCreateMatch(matchId);

        matchRegistry.setupMatch(
                state,
                playedMatch.getPlayerOne(), fc1,
                playedMatch.getPlayerTwo(), fc2
        );

        int playerNumber = playedMatch.getPlayerOne().getGoogleSub().equals(sub) ? 1 : 2;

        model.addAttribute("matchId", matchId);
        model.addAttribute("playerNumber", playerNumber);

        return "fight";
    }

    public void clearPlayerFromMatch(String sub) {
        playerToMatch.remove(sub);
    }

    @PostMapping("/leave-match")
    @ResponseBody
    public String leaveMatch(OAuth2AuthenticationToken auth) {
        if (auth != null) {
            String sub = auth.getPrincipal().getAttribute("sub");
            playerToMatch.remove(sub);
        }
        return "OK";
    }
}
