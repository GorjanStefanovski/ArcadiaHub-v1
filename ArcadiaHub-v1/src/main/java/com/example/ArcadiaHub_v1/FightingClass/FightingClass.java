package com.example.ArcadiaHub_v1.FightingClass;


import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public abstract class FightingClass {

    @Id
    @SequenceGenerator(
            name="fighting_class_sequence",
            sequenceName = "fighting_class_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "fighting_class_sequence"
    )
    private Long fcId;
    private double damage;
    private double health;
    private double speed;

    public FightingClass(){
    }

    public FightingClass(double damage,double health,double speed){
        this.damage=damage;
        this.health=health;
        this.speed=speed;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
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

    public Long getFcId() {
        return fcId;
    }
}
