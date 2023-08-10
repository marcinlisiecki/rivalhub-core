package com.rivalhub;

import jakarta.persistence.EntityManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class RivalHubApplication {
	public static void main(String[] args) {
		SpringApplication.run(RivalHubApplication.class, args);
	}

}
