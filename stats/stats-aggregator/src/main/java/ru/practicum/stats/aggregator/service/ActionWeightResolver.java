package ru.practicum.stats.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.stats.aggregator.config.ActionWeightsProperties;

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
