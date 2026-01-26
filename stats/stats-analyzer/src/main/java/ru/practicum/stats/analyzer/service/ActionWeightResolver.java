package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.stats.analyzer.config.ActionWeightsProperties;

@Component
@RequiredArgsConstructor
public class ActionWeightResolver {
    private final ActionWeightsProperties weights;

    public double resolve(ActionTypeAvro actionType) {
        return switch (actionType) {
            case REGISTER -> weights.register();
            case LIKE -> weights.like();
            case VIEW -> weights.view();
        };
    }
}
