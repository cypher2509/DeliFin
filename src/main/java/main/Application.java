package main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * Entry point for the Spring Boot application.
 */

@SpringBootApplication
@EntityScan("entity")
@ComponentScan(basePackages = {"filter", "utility","config","main"})

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}