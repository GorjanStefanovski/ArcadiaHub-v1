package com.example.ArcadiaHub_v1.Fight;


import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.FightingClass.FightingClassRepository;
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
import org.springframework.transaction.annotation.Transactional;
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
    private final Queue<QueuedPlayer> queuedPlayers= new ConcurrentLinkedQueue<>();
    private final MatchRepository matchRepository;
    private final PlayedMatchRepository playedMatchRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, Long> playerToMatch = new ConcurrentHashMap<>();

    public FightController(FightingClassRepository fightingClassRepository, PlayerRepository playerRepository, MatchRepository matchRepository, PlayedMatchRepository playedMatchRepository, SimpMessagingTemplate messagingTemplate) {
        this.fightingClassRepository = fightingClassRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.playedMatchRepository = playedMatchRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/lobby")
    public String goToLobby(){
        return "fight_lobby";
    }

    @PostMapping("/queue")
    public String queue(@RequestParam("chosenClass") int chosenClass, OAuth2AuthenticationToken auth, RedirectAttributes ra) {
        System.out.println("HIT");
        FightingClass light_or_heavy=fightingClassRepository.findByFcId((long) chosenClass);
        Player p=playerRepository.findByGoogleSub(auth.getPrincipal().getAttribute("sub"));
        QueuedPlayer player=new QueuedPlayer(p,light_or_heavy);
        queuedPlayers.add(player);

        if(queuedPlayers.size()>=2){
            QueuedPlayer one=queuedPlayers.poll();
            QueuedPlayer two=queuedPlayers.poll();
            Match match=new Match();
            match=matchRepository.save(match);
            System.out.println(one.player().getUsername());
            System.out.println(two.player().getUsername());

            PlayedMatch currentMatch=new PlayedMatch(match,one.player(),two.player(),one.fc(),two.fc());
            playedMatchRepository.save(currentMatch);

            playerToMatch.put(one.player().getGoogleSub(), match.getId());
            playerToMatch.put(two.player().getGoogleSub(), match.getId());

            messagingTemplate.convertAndSendToUser(one.player().getGoogleSub(), "/queue/match-found", new MatchFoundMessage(match.getId()));

            messagingTemplate.convertAndSendToUser(two.player().getGoogleSub(), "/queue/match-found", new MatchFoundMessage(match.getId()));

            //return "redirect:/fight/match/" + match.getId();
        }
        ra.addFlashAttribute("message", "Queued for " + chosenClass);
       // return "redirect:/fight/waiting";
        return "redirect:/fight/waiting";

    }

    /*
    @PostMapping("/queue")
    @Transactional
    public String queue(@RequestParam("chosenClass") Long chosenClass,
                        OAuth2AuthenticationToken auth,
                        RedirectAttributes ra) {

        // ... player & class lookup
        FightingClass light_or_heavy=fightingClassRepository.findByFcId((long) chosenClass);
        Player p=playerRepository.findByGoogleSub(auth.getPrincipal().getAttribute("sub"));

        queuedPlayers.add(new QueuedPlayer(p, light_or_heavy));

        if (queuedPlayers.size() >= 2) {
            QueuedPlayer q1 = queuedPlayers.poll();
            QueuedPlayer q2 = queuedPlayers.poll();

            Match match = new Match();
            match = matchRepository.save(match);

            PlayedMatch currentMatch = new PlayedMatch(match, q1.player(), q2.player(), q1.fc(), q2.fc());
            playedMatchRepository.save(currentMatch);

            String matchId = match.getId().toString();

            // Notify BOTH players via WebSocket
            MatchFoundMessage msg = new MatchFoundMessage(match.getId());
            messagingTemplate.convertAndSendToUser(q1.player().getGoogleSub(), "/queue/match-found", msg);
            messagingTemplate.convertAndSendToUser(q2.player().getGoogleSub(), "/queue/match-found", msg);

            // Redirect the current player (q2) immediately
            ra.addFlashAttribute("matchId", matchId);
            return "redirect:/fight/match/" + matchId;
        }

        ra.addFlashAttribute("message", "Queued with class ID " + chosenClass + " — waiting for opponent...");
        return "redirect:/fight/lobby";  // stay on lobby until WS pushes match found
    }
     */

    @GetMapping("/check-match")
    @ResponseBody
    public Map<String, Object> checkMatch(OAuth2AuthenticationToken auth) {
        Map<String, Object> response = new HashMap<>();

        if (auth == null || !auth.isAuthenticated()) {
            response.put("matched", false);
            response.put("position", -1);
            return response;
        }

        String sub = auth.getPrincipal().getAttribute("sub");

        Long matchId = playerToMatch.get(sub);

        if (matchId == null) {
            PlayedMatch match = playedMatchRepository.findByPlayerSub(sub);
            if (match != null) {
                matchId = match.getMatch().getId();
                playerToMatch.put(sub, matchId);
            }
        }

        if (matchId != null) {
            response.put("matched", true);
            response.put("matchId", matchId);
        } else {
            response.put("matched", false);
            response.put("position", queuedPlayers.size() + 1);
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
    public String startFight(@PathVariable Long matchId, OAuth2AuthenticationToken auth,Model model) {
        String sub = auth.getPrincipal().getAttribute("sub");
        PlayedMatch match = playedMatchRepository.findByMatchId(matchId);
        int playerNumber;
        if (match.getPlayerOne().getGoogleSub().equals(sub)) {
            playerNumber = 1;
        } else {
            playerNumber = 2;
        }
        model.addAttribute("matchId", matchId);
        model.addAttribute("playerNumber", playerNumber);
        return "fight";
    }
}
