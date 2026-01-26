package ru.practicum.stats.aggregator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.kafka.SimilarityProducer;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SimilarityAggregator {
    private final SimilarityProducer producer;
    private final ActionWeightResolver weightResolver;

    private final Map<Long, Map<Long, Double>> eventUserWeights = new HashMap<>();
    private final Map<Long, Double> eventWeightsSum = new HashMap<>();
    private final Map<Long, Map<Long, Double>> minWeightsSums = new HashMap<>();

    public synchronized void handleAction(UserActionAvro action) {
        long eventId = action.getEventId();
        long userId = action.getUserId();
        double newWeight = weightResolver.resolve(action.getActionType());

        Map<Long, Double> userWeights = eventUserWeights.computeIfAbsent(eventId, key -> new HashMap<>());
        double oldWeight = userWeights.getOrDefault(userId, 0.0);
        if (newWeight <= oldWeight) {
            return;
        }

        userWeights.put(userId, newWeight);
        eventWeightsSum.merge(eventId, newWeight - oldWeight, Double::sum);

        for (Map.Entry<Long, Map<Long, Double>> entry : eventUserWeights.entrySet()) {
            long otherEventId = entry.getKey();
            if (otherEventId == eventId) {
                continue;
            }
            Double otherWeight = entry.getValue().get(userId);
            if (otherWeight == null) {
                continue;
            }

            double oldMin = Math.min(oldWeight, otherWeight);
            double newMin = Math.min(newWeight, otherWeight);
            double delta = newMin - oldMin;
            if (delta == 0.0) {
                continue;
            }
            addMinWeightsSum(eventId, otherEventId, delta);

            double sMin = getMinWeightsSum(eventId, otherEventId);
            if (sMin <= 0.0) {
                continue;
            }
            double sA = eventWeightsSum.getOrDefault(eventId, 0.0);
            double sB = eventWeightsSum.getOrDefault(otherEventId, 0.0);
            if (sA <= 0.0 || sB <= 0.0) {
                continue;
            }
            double score = sMin / Math.sqrt(sA * sB);

            EventSimilarityAvro similarity = EventSimilarityAvro.newBuilder()
                    .setEventA(Math.min(eventId, otherEventId))
                    .setEventB(Math.max(eventId, otherEventId))
                    .setScore(score)
                    .setTimestamp(action.getTimestamp())
                    .build();
            producer.send(similarity);
        }
    }

    private void addMinWeightsSum(long eventA, long eventB, double delta) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        Map<Long, Double> values = minWeightsSums.computeIfAbsent(first, key -> new HashMap<>());
        values.put(second, values.getOrDefault(second, 0.0) + delta);
    }

    private double getMinWeightsSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);
        Map<Long, Double> values = minWeightsSums.get(first);
        if (values == null) {
            return 0.0;
        }
        return values.getOrDefault(second, 0.0);
    }
}
