package ru.practicum.stats.aggregator.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.aggregator.config.KafkaTopicsProperties;

@Service
@RequiredArgsConstructor
public class SimilarityProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final KafkaTopicsProperties topics;
    private final AvroMessageMapper avroMessageMapper;

    public void send(EventSimilarityAvro similarity) {
        byte[] payload = avroMessageMapper.toBytes(similarity);
        kafkaTemplate.send(topics.eventSimilarity(), payload);
    }
}
