package it.myhouse.switchbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SwitchBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwitchBotApplication.class, args);
    }

}
