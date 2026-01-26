package ru.practicum.stats.analyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "recommendations")
public record RecommendationProperties(int neighborsCount) {
}
