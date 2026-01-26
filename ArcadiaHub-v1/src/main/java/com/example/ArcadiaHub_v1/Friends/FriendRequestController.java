package com.example.ArcadiaHub_v1.Friends;

import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.Player.PlayerRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/friends")
public class FriendRequestController {

    private final PlayerRepository playerRepository;
    private final FriendRequestService friendRequestService;

    public FriendRequestController(PlayerRepository playerRepository, FriendRequestService friendRequestService) {
        this.playerRepository = playerRepository;
        this.friendRequestService = friendRequestService;
    }

    @GetMapping("/viewFriends")
    public String viewFriends(Model model, OAuth2AuthenticationToken auth) {
        if (auth == null) return "redirect:/";

        Player currentUser = playerRepository.findByGoogleSub(auth.getPrincipal().getAttribute("sub"));

        model.addAttribute("friends", currentUser.getFriends());
        model.addAttribute("receivedRequests", currentUser.getReceivedRequests());
        model.addAttribute("sentRequests", currentUser.getSentRequests());

        return "friends_list";
    }

    @PostMapping("/sendRequest")
    public String sendRequest(@RequestParam String receiverUsername,
                              OAuth2AuthenticationToken auth,
                              RedirectAttributes redirectAttributes) {
        Player sender = playerRepository.findByGoogleSub(auth.getPrincipal().getAttribute("sub"));
        try {
            friendRequestService.sendRequest(sender, receiverUsername);
            redirectAttributes.addFlashAttribute("success", "Signal transmitted to " + receiverUsername + "!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/friends/viewFriends";
    }

    @PostMapping("/process")
    public String processRequest(@RequestParam Long requestId,
                                 @RequestParam String action,
                                 OAuth2AuthenticationToken auth) {
        Player currentUser = playerRepository.findByGoogleSub(auth.getPrincipal().getAttribute("sub"));
        friendRequestService.processRequest(requestId, action, currentUser);

        return "redirect:/friends/viewFriends";
    }
}