package com.example.ArcadiaHub_v1.Friends;


import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.Player.PlayerDto;
import com.example.ArcadiaHub_v1.Player.PlayerRepository;
import com.example.ArcadiaHub_v1.Player.PlayerService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/friends")
public class FriendsController {

    private final FriendsService friendsService;
    private final PlayerRepository repository;
    private final PlayerService service;

    public FriendsController(FriendsService friendsService, PlayerRepository repository, PlayerService service) {
        this.friendsService = friendsService;
        this.repository = repository;
        this.service = service;
    }

    @GetMapping("/addFriend")
    public String sendToAddFriend(){
        return "add_friend_page";
    }

    @PostMapping("/addFriend")
    public String addFriend(@RequestParam String username, OAuth2AuthenticationToken token, Model model){
        System.out.println("ADD FRIEND HIT");
        String sub=token.getPrincipal().getAttribute("sub");
        Long senderID=this.repository.findByGoogleSub(sub).getId();
        Long reciverID=this.repository.findByUsername(username).getId();
        this.friendsService.addFriend(senderID,reciverID);
        List<PlayerDto> friends=this.service.getAFriends(token);
        model.addAttribute("friends",friends);
        model.addAttribute("numberOfFriends",friends.size());
        return "add_friend_page";
    }

    @PostMapping("/friends/remove")
    public String removeFriend(@RequestParam("friendId") Long friendId,
                               OAuth2AuthenticationToken token,
                               RedirectAttributes redirectAttributes) {

        String sub = token.getPrincipal().getAttribute("sub");
        Player current = repository.findByGoogleSub(sub);

        try {
            friendsService.removeFriend(current.getId(), friendId);
            redirectAttributes.addFlashAttribute("message", "Friend removed");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/friends";
    }
}
