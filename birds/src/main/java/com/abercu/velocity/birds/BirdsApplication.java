package com.abercu.velocity.birds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing // for using @CreationTimestamp and @UpdateTimestamp JPA auditing annotations
@SpringBootApplication
public class BirdsApplication {

	public static void main(String[] args) {
		SpringApplication.run(BirdsApplication.class, args);
	}

}
