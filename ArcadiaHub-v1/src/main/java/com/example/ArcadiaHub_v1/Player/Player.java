package com.example.ArcadiaHub_v1.Player;

import com.example.ArcadiaHub_v1.Achievment.Achievement;
import com.example.ArcadiaHub_v1.Friends.FriendRequest;
import com.example.ArcadiaHub_v1.MatchParticipant.MatchParticipant;
import com.example.ArcadiaHub_v1.PlayerClassProgression.PlayerClassProgression;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="Player")
public class Player {

    @Id
    @SequenceGenerator(
            name="sequence_generator",
            sequenceName="sequence_generator",
            initialValue = 1,
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator ="sequence_generator"
    )
    private Long id;
    @Column(unique = true)
    private String username;
    private String email;
    @Column(unique = true)
    private String googleSub;
    private Integer matchesPlayed;
    private Integer matchesLost;
    private Integer matchesWon;
    private Integer accountLevel;
    private Integer accountXP;
    private Long hoursPlayed;
    private double winRate=0.0;
    @ManyToMany
    @JoinTable(
            name = "player_friends",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id")
    )
    private List<Player> friends = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<FriendRequest> sentRequests = new ArrayList<>();

    // Барања кои овој играч ги примил
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<FriendRequest> receivedRequests = new ArrayList<>();

    @OneToMany(mappedBy = "p", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerClassProgression> progressions;

    @OneToMany(mappedBy = "playerId",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MatchParticipant> matchHistory;

    @ManyToMany
    @JoinTable(
            name = "player_achievements",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "achievement_id")
    )
    private List<Achievement> achievements = new ArrayList<>();

    public Player(){
    }

    public Player(String username, String email, String googleSub, Integer matchesLost, Integer matchesWon, Integer accountLevel, Integer accountXP, Long hoursPlayed) {
        this.username = username;
        this.email = email;
        this.googleSub = googleSub;
        this.matchesLost = matchesLost;
        this.matchesWon = matchesWon;
        this.accountLevel = accountLevel;
        this.accountXP = accountXP;
        this.hoursPlayed = hoursPlayed;
        this.matchesPlayed = matchesWon+matchesLost;
        this.winRate=getWinRate();
        this.friends=new ArrayList<>();
        this.progressions=new ArrayList<>();
        this.matchHistory=new ArrayList<>();
    }

    public void setProgressions(List<PlayerClassProgression> progressions) {
        this.progressions = progressions;
    }

    public void setFriends(List<Player> friends) {
        this.friends = friends;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMatchHistory(List<MatchParticipant> matchHistory) {
        this.matchHistory = matchHistory;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }

    public void setHoursPlayed(Long hoursPlayed) {
        this.hoursPlayed = hoursPlayed;
    }

    public void setAccountXP(Integer accountXP) {
        this.accountXP = accountXP;
    }

    public void setAccountLevel(Integer accountLevel) {
        this.accountLevel = accountLevel;
    }

    public void setMatchesWon(Integer matchesWon) {
        this.matchesWon = matchesWon;
    }

    public void setMatchesPlayed(Integer matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public void setGoogleSub(String googleSub) {
        this.googleSub = googleSub;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMatchesLost(Integer matchesLost) {
        this.matchesLost = matchesLost;
    }

    public Long getId() {
        return id;
    }

    public Long getHoursPlayed() {
        return hoursPlayed;
    }

    public Integer getAccountXP() {
        return accountXP;
    }

    public Integer getAccountLevel() {
        return accountLevel;
    }

    public Integer getMatchesWon() {
        return matchesWon;
    }

    public Integer getMatchesLost() {
        return matchesLost;
    }

    public Integer getMatchesPlayed() {
        return matchesPlayed;
    }

    public String getGoogleSub() {
        return googleSub;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public double getWinRate(){
        int totalMatches = matchesPlayed;
        return totalMatches > 0 ? (double) matchesWon / totalMatches * 100 : 0;
    }

    public List<Player> getFriends() {
        return friends;
    }

    public void addFriend(Player p){
        this.friends.add(p);
    }

    public void removeFriend(Player p){
        this.friends.remove(p);
    }

    public List<PlayerClassProgression> getProgressions() {
        return progressions;
    }

    public List<MatchParticipant> getMatchHistory() {
        return matchHistory;
    }

    public void setSentRequests(List<FriendRequest> sentRequests) {
        this.sentRequests = sentRequests;
    }

    public void setReceivedRequests(List<FriendRequest> receivedRequests) {
        this.receivedRequests = receivedRequests;
    }

    public List<FriendRequest> getSentRequests() {
        return sentRequests;
    }

    public List<FriendRequest> getReceivedRequests() {
        return receivedRequests;
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }
}
