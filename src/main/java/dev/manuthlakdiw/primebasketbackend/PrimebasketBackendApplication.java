package dev.manuthlakdiw.primebasketbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
@EnableScheduling
public class PrimebasketBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrimebasketBackendApplication.class, args);
    }

}
