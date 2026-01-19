package ru.practicum.ewm.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "ru.practicum.ewm.stats",
        "ru.practicum.ewm.request.client"
})
@ComponentScan(basePackages = {
        "ru.practicum.ewm.event",
        "ru.practicum.ewm.category.mapper",
        "ru.practicum.ewm.comment.mapper",
        "ru.practicum.ewm.user.mapper",
        "ru.practicum.ewm.exception.controller",
        "ru.practicum.ewm.stats"
})
@EnableJpaRepositories(basePackages = {
        "ru.practicum.ewm.event.repository",
        "ru.practicum.ewm.category.repository",
        "ru.practicum.ewm.comment.repository",
        "ru.practicum.ewm.user.repository"
})
@EntityScan(basePackages = {
        "ru.practicum.ewm.event.model",
        "ru.practicum.ewm.category.model",
        "ru.practicum.ewm.comment.model",
        "ru.practicum.ewm.user.model"
})
public class EventsServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(EventsServiceApplication.class, args);
    }
}
