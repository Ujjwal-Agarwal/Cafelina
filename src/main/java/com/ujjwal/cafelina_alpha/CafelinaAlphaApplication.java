package com.ujjwal.cafelina_alpha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CafelinaAlphaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CafelinaAlphaApplication.class, args);
    }

}
