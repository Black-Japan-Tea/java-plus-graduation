package ru.practicum.stats.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.service.EventSimilarityService;

@Component
@RequiredArgsConstructor
public class EventSimilarityConsumer {
    private final AvroMessageMapper avroMessageMapper;
    private final EventSimilarityService similarityService;

    @KafkaListener(topics = "${kafka.topics.event-similarity}")
    public void onSimilarity(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return;
        }
        EventSimilarityAvro similarity = avroMessageMapper.fromBytes(payload, EventSimilarityAvro.class);
        similarityService.upsertSimilarity(similarity);
    }
}
