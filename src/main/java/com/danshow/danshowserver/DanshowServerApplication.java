package com.danshow.danshowserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DanshowServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DanshowServerApplication.class, args);
    }

}
