package com.example.ArcadiaHub_v1.Player;


import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.FightingClass.FightingClassRepository;
import com.example.ArcadiaHub_v1.PlayerClassProgression.PlayerClassProgression;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepository repository;
    private final FightingClassRepository rep;

    public PlayerService(PlayerRepository repository, FightingClassRepository rep) {
        this.repository = repository;
        this.rep = rep;
    }

    public PlayerDto convertPlayer(Player p){
        return new PlayerDto(p.getUsername(),p.getMatchesPlayed(),p.getMatchesLost(),p.getMatchesWon(),p.getAccountLevel(),p.getHoursPlayed(),p.getAccountXP(),p.getWinRate());
    }

    public LeaderboardPlayerDto convertPlayerLeaderboard(Player p){
        return new LeaderboardPlayerDto(p.getUsername(),p.getMatchesPlayed(),p.getMatchesWon(),p.getMatchesLost(),p.getWinRate());
    }

    public void savePlayer(Player p){
        this.repository.save(p);
    }

    public Player findByGoogleSub(String sub){
        return this.repository.findByGoogleSub(sub);
    }

    public Player findPlayerByUsername(String username){
        return this.repository.findByUsername(username);
    }

    public void updateUsername(String sub,String username){
        Player p=this.repository.findByGoogleSub(sub);
        p.setUsername(username);
        this.repository.save(p);
    }

    public PlayerDto getPlayerInformation(String sub){
        Player p=this.repository.findByGoogleSub(sub);
        return this.convertPlayer(p);
    }

    public List<LeaderboardPlayerDto> leaderboardPlayerDtos(String top_bottom,Integer number,String criterion){
        Map<String, Supplier<List<Player>>> leaderboardMap = new HashMap<>();

        // Pobedi
        leaderboardMap.put("top-5-wins", repository::findTop5ByOrderByMatchesWonDesc);
        leaderboardMap.put("top-10-wins", repository::findTop10ByOrderByMatchesWonDesc);
        leaderboardMap.put("bottom-5-wins", repository::findTop5ByOrderByMatchesWonAsc);
        leaderboardMap.put("bottom-10-wins", repository::findTop10ByOrderByMatchesWonAsc);

        // Porazi
        leaderboardMap.put("top-5-losses", repository::findTop5ByOrderByMatchesLostDesc);
        leaderboardMap.put("top-10-losses", repository::findTop10ByOrderByMatchesLostDesc);
        leaderboardMap.put("bottom-5-losses", repository::findTop5ByOrderByMatchesLostAsc);
        leaderboardMap.put("bottom-10-losses", repository::findTop10ByOrderByMatchesLostAsc);

        // Build the key based on form parameters
        String key = top_bottom.toLowerCase() + "-" + number + "-" + criterion.toLowerCase();

        return leaderboardMap.getOrDefault(key,
                repository::findTop10ByOrderByMatchesWonDesc).get().stream().map(this::convertPlayerLeaderboard).collect(Collectors.toList());
    }

    public List<PlayerDto> getAFriends(OAuth2AuthenticationToken token){
        String sub=token.getPrincipal().getAttribute("sub");
        Player p=this.repository.findByGoogleSub(sub);
        return p.getFriends().stream().map(this::convertPlayer).toList();
    }

    public void addInitialProgressions(String sub){
        Player p=this.repository.findByGoogleSub(sub);
        FightingClass light=rep.findByFcId(1L);
        FightingClass heavy= rep.findByFcId(2L);
        p.getProgressions().add(new PlayerClassProgression(p,light));
        p.getProgressions().add(new PlayerClassProgression(p,heavy));
        this.repository.save(p);
    }
}
