package com.clinical.coms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClinicalOrderManagementPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicalOrderManagementPlatformApplication.class, args);
    }
}
