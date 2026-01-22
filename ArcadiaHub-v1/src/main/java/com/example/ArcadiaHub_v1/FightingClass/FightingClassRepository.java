package com.example.ArcadiaHub_v1.FightingClass;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FightingClassRepository extends JpaRepository<FightingClass,Long> {
    FightingClass findByFcId(Long id);
}
