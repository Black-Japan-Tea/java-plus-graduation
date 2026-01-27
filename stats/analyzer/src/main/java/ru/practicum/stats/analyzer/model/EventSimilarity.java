package ru.practicum.stats.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_similarity")
@IdClass(EventSimilarityId.class)
@Getter
@Setter
@NoArgsConstructor
public class EventSimilarity {
    @Id
    @Column(name = "event_a")
    private long eventA;

    @Id
    @Column(name = "event_b")
    private long eventB;

    @Column(name = "score")
    private double score;

    @Column(name = "last_action_time")
    private LocalDateTime lastActionTime;
}
