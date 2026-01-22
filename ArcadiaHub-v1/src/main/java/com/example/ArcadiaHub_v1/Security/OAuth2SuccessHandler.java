package com.example.ArcadiaHub_v1.Security;


import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.Player.PlayerRepository;
import com.example.ArcadiaHub_v1.Player.PlayerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final PlayerService service;

    public OAuth2SuccessHandler(PlayerService service) {
        this.service = service;
        this.setDefaultTargetUrl("/home");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
        //kastiranjevo sekogas raboti bidejki rabotime samo so OAuth2 log in
        OAuth2AuthenticationToken token=(OAuth2AuthenticationToken) authentication;

        OAuth2User user= token.getPrincipal();

        String googleSub=user.getAttribute("sub");
        String email=user.getAttribute("email");
        String defaultUsername="Player"+(Math.random()*100);
        if (googleSub == null) {
            throw new RuntimeException("Google sub missing!");
        }
        Player player=this.service.findByGoogleSub(googleSub);
        if(player==null){
            this.service.savePlayer(new Player(defaultUsername,email,googleSub,0,0,0,0,0L));
            resp.sendRedirect("/home/createUsername");
            return;
        }
        super.onAuthenticationSuccess(req,resp,authentication);
    }
}
