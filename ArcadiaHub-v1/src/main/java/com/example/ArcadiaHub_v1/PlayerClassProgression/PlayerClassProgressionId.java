package com.example.ArcadiaHub_v1.PlayerClassProgression;


import java.io.Serializable;
import java.util.Objects;

public class PlayerClassProgressionId implements Serializable {

    private Long p;
    private Long fc;

    public PlayerClassProgressionId(){
    }

    public PlayerClassProgressionId(Long pID,Long fID){
        this.p =pID;
        this.fc =fID;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        PlayerClassProgressionId that = (PlayerClassProgressionId) o;
        return Objects.equals(p, that.p) &&
                Objects.equals(fc, that.fc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p, fc);
    }
}
