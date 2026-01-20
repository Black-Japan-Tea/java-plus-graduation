package ru.practicum.ewm.request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ru.practicum.ewm.request",
        "ru.practicum.ewm.exception.controller"
})
@EnableJpaRepositories(basePackages = {
        "ru.practicum.ewm.request.repository",
        "ru.practicum.ewm.event.repository",
        "ru.practicum.ewm.user.repository"
})
@EntityScan(basePackages = {
        "ru.practicum.ewm.request.model",
        "ru.practicum.ewm.event.model",
        "ru.practicum.ewm.user.model",
        "ru.practicum.ewm.category.model"
})
public class RequestsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(RequestsServiceApplication.class, args);
    }
}
