package ru.practicum.stats.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.stats.analyzer.model.EventSimilarity;
import ru.practicum.stats.analyzer.model.EventSimilarityId;

import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, EventSimilarityId> {
    List<EventSimilarity> findByEventA(long eventA);

    List<EventSimilarity> findByEventB(long eventB);
}
