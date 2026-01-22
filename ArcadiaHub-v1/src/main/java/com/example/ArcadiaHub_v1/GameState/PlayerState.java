package com.example.ArcadiaHub_v1.GameState;

public class PlayerState {
    private double x = 0;
    private double y = 0;
    private double vx = 0;
    private double vy = 0;
    private int health = 100;

    // You can add more if needed, like current sprite or attack cooldown
    private String currentSprite = "idle";

    // getters/setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getVx() { return vx; }
    public void setVx(double vx) { this.vx = vx; }
    public double getVy() { return vy; }
    public void setVy(double vy) { this.vy = vy; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
    public String getCurrentSprite() { return currentSprite; }
    public void setCurrentSprite(String sprite) { this.currentSprite = sprite; }

    // Example attack method
    public void attack() {
        // implement attack logic here
    }
}
