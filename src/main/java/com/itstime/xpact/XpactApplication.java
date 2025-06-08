package com.itstime.xpact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class XpactApplication {

    public static void main(String[] args) {
        SpringApplication.run(XpactApplication.class, args);
    }

}
