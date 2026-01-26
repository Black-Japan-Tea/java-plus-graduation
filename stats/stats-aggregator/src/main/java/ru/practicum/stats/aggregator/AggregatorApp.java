package ru.practicum.stats.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableKafka
@EnableDiscoveryClient
public class AggregatorApp {
    public static void main(String[] args) {
        SpringApplication.run(AggregatorApp.class, args);
    }
}
