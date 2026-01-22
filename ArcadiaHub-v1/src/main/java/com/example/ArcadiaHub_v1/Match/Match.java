package com.example.ArcadiaHub_v1.Match;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="Matches")
public class Match {

    @Id
    @SequenceGenerator(
            name="match_sequence",
            sequenceName = "match_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "match_sequence"
    )
    private Long id;

    public Match(){
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }
}
