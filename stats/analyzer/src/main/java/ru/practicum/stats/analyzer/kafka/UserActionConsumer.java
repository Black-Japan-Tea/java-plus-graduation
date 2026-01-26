package ru.practicum.stats.analyzer.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.service.UserActionService;

@Component
@RequiredArgsConstructor
public class UserActionConsumer {
    private final AvroMessageMapper avroMessageMapper;
    private final UserActionService userActionService;

    @KafkaListener(topics = "${kafka.topics.user-actions}")
    public void onUserAction(byte[] payload) {
        if (payload == null || payload.length == 0) {
            return;
        }
        UserActionAvro action = avroMessageMapper.fromBytes(payload, UserActionAvro.class);
        userActionService.upsertUserAction(action);
    }
}
