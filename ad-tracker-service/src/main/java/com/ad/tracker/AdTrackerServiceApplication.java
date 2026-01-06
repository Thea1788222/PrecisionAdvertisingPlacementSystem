package com.ad.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.ad.tracker.model")
@EnableJpaRepositories(basePackages = "com.ad.tracker.repository")
@EnableScheduling
public class AdTrackerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdTrackerServiceApplication.class, args);
    }

}