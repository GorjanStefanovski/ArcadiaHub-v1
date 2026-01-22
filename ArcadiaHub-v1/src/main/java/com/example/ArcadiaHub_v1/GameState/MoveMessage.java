package com.example.ArcadiaHub_v1.GameState;

public class MoveMessage {
    private int playerId; // 1 or 2
    private String key;   // "a", "d", "ArrowLeft", etc.
    private String state; // "DOWN" or "UP"

    // getters/setters
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}