package com.exercise.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class BasicMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicMongoApplication.class, args);
	}

}
