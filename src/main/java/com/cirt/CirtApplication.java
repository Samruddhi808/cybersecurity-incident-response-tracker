package com.cirt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Cybersecurity Incident Response Tracker (CIRT) application.
 *
 * This is a standard Spring Boot monolithic application.
 * Spring Boot auto-configures JPA, web MVC, and Thymeleaf from the classpath.
 */
@SpringBootApplication
public class CirtApplication {

    public static void main(String[] args) {
        SpringApplication.run(CirtApplication.class, args);
    }
}
