package com.example.ArcadiaHub_v1.Configuration;

import com.example.ArcadiaHub_v1.Achievment.Achievement;
import com.example.ArcadiaHub_v1.Achievment.AchievementRepository;
import com.example.ArcadiaHub_v1.FightingClass.FightingClassRepository;
import com.example.ArcadiaHub_v1.FightingClass.HeavyClass;
import com.example.ArcadiaHub_v1.FightingClass.LightClass;
import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.Player.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FakeDataInitializer {

    @Bean
    CommandLineRunner fakeUsers(PlayerRepository rep, FightingClassRepository fcRep, AchievementRepository achievementRepository){
        return args->{
            rep.save(new Player("defaultUsername567","mail1@gmail.com","1234ab",58,184,23,23000,7L));
            rep.save(new Player("defaultUsername789","mail2@gmail.com","1234abc",72,257,25,25000,15L));
            rep.save(new Player("defaultUsername91011","mail3@gmail.com","1234abcd",23,94,18,18000,13L));
            rep.save(new Player("defaultUsername111213","mail4@gmail.com","1234abef",150,358,30,30000,20L));
            rep.save(new Player("defaultUsername131415","mail5@gmail.com","1234abefg",190,234,27,27000,18L));
            rep.save(new Player("defaultUsername123","mail6@gmail.com","1234hi",45,60,15,15000,5L));
            rep.save(new Player("defaultUsername456","mail7@gmail.com","1234jk",13,50,20,20000,3L));
            rep.save(new Player("defaultUsername678","mail8@gmail.com","1234lmnop",9,13,15,15000,1L));
            rep.save(new Player("defaultUsername8910","mail9@gmail.com","1234qrs",300,700,150,150000,28L));
            rep.save(new Player("defaultUsername101112","mail10@gmail.com","1234ymz",64,102,34,34000,8L));
            fcRep.save(new LightClass(20,100,10));
            fcRep.save(new HeavyClass(30,100,5));
            achievementRepository.save(new Achievement("Beginner Fighter", "Reached Level 1", 1));
            achievementRepository.save(new Achievement("Intermediate Fighter", "Reached Level 5", 5));
            achievementRepository.save(new Achievement("Expert Fighter", "Reached Level 10", 10));
        };
    }
}
