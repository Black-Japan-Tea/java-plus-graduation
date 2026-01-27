package ru.practicum.stats.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.aggregator.service.SimilarityAggregator;

@Component
@RequiredArgsConstructor
public class UserActionListener {
    private final AvroMessageMapper avroMessageMapper;
    private final SimilarityAggregator similarityAggregator;

    @KafkaListener(topics = "${kafka.topics.user-actions}")
    public void onUserAction(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return;
        }
        UserActionAvro action = avroMessageMapper.fromBytes(payload, UserActionAvro.class);
        similarityAggregator.handleAction(action);
    }
}
