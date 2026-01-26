package ru.practicum.stats.analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "recommendations.action-weights")
public record ActionWeightsProperties(double view, double register, double like) {
}
