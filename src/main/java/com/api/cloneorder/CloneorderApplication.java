package com.api.cloneorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.api")
@EntityScan("com.api.model")
@EnableJpaRepositories("com.api.repository")
public class CloneorderApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloneorderApplication.class, args);
	}

}
