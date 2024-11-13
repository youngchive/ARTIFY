package com.elice.artBoard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ArtBoardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArtBoardApplication.class, args);
	}

}
