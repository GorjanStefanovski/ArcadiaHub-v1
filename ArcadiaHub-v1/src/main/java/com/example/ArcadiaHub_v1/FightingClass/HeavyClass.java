package com.example.ArcadiaHub_v1.FightingClass;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("HEAVY_CLASS")
public class HeavyClass extends FightingClass{


    public HeavyClass(){
    }

    public HeavyClass(double damage,double health,double speed){
       super(damage,health,speed);
    }
}
