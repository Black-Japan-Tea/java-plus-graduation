package ru.practicum.ewm.extra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
        "ru.practicum.ewm.category",
        "ru.practicum.ewm.compilation",
        "ru.practicum.ewm.comment",
        "ru.practicum.ewm.event.mapper",
        "ru.practicum.ewm.user.mapper",
        "ru.practicum.ewm.exception.controller"
})
@EnableJpaRepositories(basePackages = {
        "ru.practicum.ewm.category.repository",
        "ru.practicum.ewm.compilation.repository",
        "ru.practicum.ewm.comment.repository",
        "ru.practicum.ewm.event.repository",
        "ru.practicum.ewm.user.repository"
})
@EntityScan(basePackages = {
        "ru.practicum.ewm.category.model",
        "ru.practicum.ewm.compilation.model",
        "ru.practicum.ewm.comment.model",
        "ru.practicum.ewm.event.model",
        "ru.practicum.ewm.user.model"
})
public class ExtraServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExtraServiceApplication.class, args);
    }
}
