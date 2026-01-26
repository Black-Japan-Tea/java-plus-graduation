package ru.practicum.stats.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.EventSimilarityId;
import ru.practicum.stats.analyzer.repository.EventSimilarityRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class EventSimilarityService {
    private final EventSimilarityRepository repository;

    @Transactional
    public void upsertSimilarity(EventSimilarityAvro similarity) {
        EventSimilarityId id = new EventSimilarityId();
        id.setEventA(similarity.getEventA());
        id.setEventB(similarity.getEventB());

        EventSimilarity entity = repository.findById(id).orElseGet(() -> {
            EventSimilarity created = new EventSimilarity();
            created.setEventA(similarity.getEventA());
            created.setEventB(similarity.getEventB());
            return created;
        });

        entity.setScore(similarity.getScore());
        LocalDateTime actionTime = LocalDateTime.ofInstant(
                similarity.getTimestamp(),
                ZoneId.systemDefault());
        entity.setLastActionTime(actionTime);

        repository.save(entity);
    }
}
