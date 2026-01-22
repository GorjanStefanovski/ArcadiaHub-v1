package com.example.ArcadiaHub_v1.FightingClass;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("LIGHT_CLASS")
public class LightClass extends FightingClass {

    public LightClass(){
    }

    public LightClass(double damage,double health,double speed){
        super(damage,health,speed);
    }
}
