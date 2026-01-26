package ru.practicum.stats.analyzer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class EventSimilarityId implements Serializable {
    private long eventA;
    private long eventB;
}
