package com.example.HNR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication

public class HnrApplication {
	public static void main(String[] args) {
		SpringApplication.run(HnrApplication.class, args);
	}
}