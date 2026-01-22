package com.example.ArcadiaHub_v1.Player;


import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/home")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping("/welcomeUser")
    public String welcomeUser(Model model,OAuth2AuthenticationToken token){
        OAuth2User user=token.getPrincipal();
        String sub=user.getAttribute("sub");
        String username=this.service.findByGoogleSub(sub).getUsername();
        model.addAttribute("username",username);
        return "welcome_user";
    }

    @GetMapping("/createUsername")
    public String showCreateUsernameForm() {
        return "create_username";
    }

    @GetMapping("/fight")
    public String goToLobby(){
        return "fight_lobby";
    }

    @PostMapping("/fight/queue")
    public String queue(@RequestParam String chosenClass, OAuth2AuthenticationToken auth, RedirectAttributes ra) {
        // queue logic
        ra.addFlashAttribute("message", "Queued for " + chosenClass);
        return "redirect:/fight/queue-status";  // or WebSocket notify
    }


    @PostMapping("/createUsername")
    public String createUsername(@RequestParam String username, OAuth2AuthenticationToken token, RedirectAttributes redirectAttributes){
        OAuth2User user=token.getPrincipal();
        Player usernameCheck=this.service.findPlayerByUsername(username);
        if(usernameCheck!=null){
            redirectAttributes.addFlashAttribute("error", "Username already taken!");
            return "redirect:/home/createUsername";
        }
        if(username.isEmpty()){
            redirectAttributes.addFlashAttribute("error","Username is empty!");
            return "redirect:/createUsername";
        }

        String sub=user.getAttribute("sub");
        this.service.updateUsername(sub,username);
        this.service.addInitialProgressions(sub);
        return "redirect:/home/welcomeUser";
    }

    @GetMapping("/statistics")
    public String getStatistics(OAuth2AuthenticationToken token, Model model) {
        String sub = token.getPrincipal().getAttribute("sub");
        PlayerDto dto = this.service.getPlayerInformation(sub);
        model.addAttribute("player", dto);
        model.addAttribute("accountXP", dto.accountXP());
        model.addAttribute("accountLevel", dto.accountLevel());
        model.addAttribute("achievements", dto.achievements());
        return "statistics_user";
    }

    @GetMapping("/leaderboardFilter")
    public String sendToLeaderboardFilter(){
        return "leaderboard_filter_page";
    }

    @PostMapping("/leaderboardFilter")
    public String getLeaderboardFilter(@RequestParam String top_bottom,@RequestParam Integer number,@RequestParam String criterion){
        return "redirect:/home/leaderboard?top_bottom=" + top_bottom +
                "&number=" + number +
                "&criterion=" + criterion;
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard(
            @RequestParam(required = false, defaultValue = "top") String top_bottom,
            @RequestParam(required = false, defaultValue = "10") Integer number,
            @RequestParam(required = false, defaultValue = "wins") String criterion,
            Model model
    ){
        List<LeaderboardPlayerDto> players=this.service.leaderboardPlayerDtos(top_bottom,number,criterion);
        model.addAttribute("players",players);
        return "leaderboard_page";
    }
}
