package ru.practicum.ewm.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ru.practicum.ewm.user",
        "ru.practicum.ewm.exception.controller"
})
@EnableJpaRepositories(basePackages = "ru.practicum.ewm.user.repository")
@EntityScan(basePackages = "ru.practicum.ewm.user.model")
public class AdminUsersServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminUsersServiceApplication.class, args);
    }
}
