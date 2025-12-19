package xyz.whysoarbh.bugetbaba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BugetbabaApplication {
    public static void main(String[] args) {
        SpringApplication.run(BugetbabaApplication.class, args);
    }
}
