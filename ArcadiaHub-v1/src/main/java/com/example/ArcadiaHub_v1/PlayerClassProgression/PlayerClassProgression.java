package com.example.ArcadiaHub_v1.PlayerClassProgression;


import com.example.ArcadiaHub_v1.FightingClass.FightingClass;
import com.example.ArcadiaHub_v1.Player.Player;
import jakarta.persistence.*;

@Entity
@IdClass(PlayerClassProgressionId.class)
public class PlayerClassProgression {

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player p;

    @Id
    @ManyToOne
    @JoinColumn(name = "class_id")
    private FightingClass fc;

    private int classLevel;
    private int classXP;
    private double damage;
    private double health;
    private double speed;

    public PlayerClassProgression(){
    }

    public PlayerClassProgression(Player p,FightingClass fc){
        this.p=p;
        this.fc=fc;
        this.classLevel=0;
        this.classXP=0;
        this.damage = fc.getDamage();
        this.health = fc.getHealth();
        this.speed = fc.getSpeed();
    }

    public void setFc(FightingClass fc) {
        this.fc = fc;
    }

    public int getClassLevel(int classXP) {
        if(classXP>=1000) {
            return classXP / 1000;
        }
        return 0;
    }

    public void addClassXp(int added){
        this.classXP += added;
        this.classLevel = this.classXP / 100;
    }

    public void setClassXP(int classXP) {
        this.classXP = classXP;
    }

    public void setP(Player p) {
        this.p = p;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void updateDamage(double damage){
        this.damage+=damage;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void updateSpeed(double speed){
        this.speed+=speed;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void updateHealth(double health){
        this.health+=health;
    }

    public Player getP() {
        return p;
    }

    public int getClassXP() {
        return classXP;
    }

    public FightingClass getFc() {
        return fc;
    }

    public double getDamage() {
        return damage;
    }

    public double getSpeed() {
        return speed;
    }

    public double getHealth() {
        return health;
    }
}
