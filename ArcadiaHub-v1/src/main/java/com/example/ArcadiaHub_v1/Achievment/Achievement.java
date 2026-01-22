package com.example.ArcadiaHub_v1.Achievment;

import com.example.ArcadiaHub_v1.Player.Player;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int requiredLevel;

    @ManyToMany(mappedBy = "achievements")
    private List<Player> players = new ArrayList<>();

    public Achievement(){
    }

    public Achievement(String description, String requierment, int requiredLevel) {
        this.name=description;
        this.description=requierment;
        this.requiredLevel=requiredLevel;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setRequiredLevel(int requiredLevel) {
        this.requiredLevel = requiredLevel;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }
}
