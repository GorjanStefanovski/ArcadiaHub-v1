package com.example.ArcadiaHub_v1.GameState;

public class PlayerState {
    private double x = 0;
    private double y = 0;
    private double vx = 0;
    private double vy = 0;
    private int health = 100;
    private boolean isAttacking = false;
    private double attackWidth = 160;
    private double attackHeight = 50;
    private double attackOffsetX = 100;
    private String currentSprite = "idle";
    private double damage;
    private double speed;
    private Long classId;
    private Long playerId;
    private boolean isHit=false;

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

    public void attack() {
        // implement attack logic here
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public double getAttackOffsetX() {
        return attackOffsetX;
    }

    public double getAttackHeight() {
        return attackHeight;
    }

    public double getAttackWidth() {
        return attackWidth;
    }

    public void setAttacking(boolean attacking) {
        isAttacking = attacking;
    }

    public void setAttackOffsetX(double attackOffsetX) {
        this.attackOffsetX = attackOffsetX;
    }

    public void setAttackHeight(double attackHeight) {
        this.attackHeight = attackHeight;
    }

    public void setAttackWidth(double attackWidth) {
        this.attackWidth = attackWidth;
    }

    public double getDamage() {
        return damage;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public Long getClassId() {
        return classId;
    }

    public double getSpeed() {
        return speed;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }
}
