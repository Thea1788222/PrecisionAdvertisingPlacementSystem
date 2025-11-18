package com.ad.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.ad.tracker.model")
@EnableJpaRepositories(basePackages = "com.ad.tracker.repository")
public class AdTrackerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdTrackerServiceApplication.class, args);
    }

}