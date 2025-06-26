package com.example.awsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AwsdemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AwsdemoApplication.class, args);
	}

}
