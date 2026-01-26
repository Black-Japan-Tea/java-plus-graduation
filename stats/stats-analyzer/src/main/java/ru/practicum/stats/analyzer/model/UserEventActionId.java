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
public class UserEventActionId implements Serializable {
    private long userId;
    private long eventId;
}
