package com.example.ArcadiaHub_v1.GameState;

public class MatchState {
    private final Long id;
    private final PlayerState player1;
    private final PlayerState player2;

    private final int playerSpeed = 5;
    private final int jumpVelocity = 20;
    private boolean isEnded=false;

    public MatchState(Long id) {
        this.id = id;
        this.player1 = new PlayerState();
        this.player2 = new PlayerState();

        this.player1.setX(100);
        this.player1.setAttackOffsetX(100);
        this.player1.setAttackWidth(160);

        this.player2.setX(800);
        this.player2.setAttackOffsetX(-170);
        this.player2.setAttackWidth(170);
    }

    // Getters
    public Long getId() { return id; }
    public PlayerState getPlayer1() { return player1; }
    public PlayerState getPlayer2() { return player2; }
    public int getPlayerSpeed() { return playerSpeed; }
    public int getJumpVelocity() { return jumpVelocity; }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean b) {
        this.isEnded=b;
    }
}