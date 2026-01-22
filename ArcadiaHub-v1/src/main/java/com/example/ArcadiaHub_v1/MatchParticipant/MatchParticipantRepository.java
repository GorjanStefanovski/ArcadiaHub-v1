package com.example.ArcadiaHub_v1.MatchParticipant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchParticipantRepository extends JpaRepository<MatchParticipant,MatchParticipantId> {
}
