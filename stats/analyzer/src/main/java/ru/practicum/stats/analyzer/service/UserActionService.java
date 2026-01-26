package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.stats.analyzer.model.UserEventAction;
import ru.practicum.stats.analyzer.model.UserEventActionId;
import ru.practicum.stats.analyzer.repository.UserEventActionRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class UserActionService {
    private final UserEventActionRepository repository;
    private final ActionWeightResolver weightResolver;

    @Transactional
    public void upsertUserAction(UserActionAvro action) {
        UserEventActionId id = new UserEventActionId();
        id.setUserId(action.getUserId());
        id.setEventId(action.getEventId());

        UserEventAction entity = repository.findById(id).orElseGet(() -> {
            UserEventAction created = new UserEventAction();
            created.setUserId(action.getUserId());
            created.setEventId(action.getEventId());
            created.setWeight(0.0);
            return created;
        });

        double newWeight = weightResolver.resolve(action.getActionType());
        if (newWeight > entity.getWeight()) {
            entity.setWeight(newWeight);
        }

        LocalDateTime actionTime = LocalDateTime.ofInstant(
                action.getTimestamp(),
                ZoneId.systemDefault());
        entity.setLastActionTime(actionTime);

        repository.save(entity);
    }
}
