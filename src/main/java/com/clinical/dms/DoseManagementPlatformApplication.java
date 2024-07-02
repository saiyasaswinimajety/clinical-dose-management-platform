package com.clinical.dms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoseManagementPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoseManagementPlatformApplication.class, args);
    }
}
