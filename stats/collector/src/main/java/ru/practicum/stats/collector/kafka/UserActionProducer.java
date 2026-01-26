package ru.practicum.stats.collector.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.collector.config.KafkaTopicsProperties;

@Service
@RequiredArgsConstructor
public class UserActionProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private final KafkaTopicsProperties topics;
    private final AvroMessageMapper avroMessageMapper;

    public void send(UserActionAvro action) {
        byte[] payload = avroMessageMapper.toBytes(action);
        kafkaTemplate.send(topics.userActions(), payload);
    }
}
