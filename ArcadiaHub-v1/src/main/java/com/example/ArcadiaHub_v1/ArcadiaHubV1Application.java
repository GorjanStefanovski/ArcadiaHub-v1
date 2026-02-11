package com.example.ArcadiaHub_v1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ArcadiaHubV1Application {

	public static void main(String[] args) {
		SpringApplication.run(ArcadiaHubV1Application.class, args);
	}

}
