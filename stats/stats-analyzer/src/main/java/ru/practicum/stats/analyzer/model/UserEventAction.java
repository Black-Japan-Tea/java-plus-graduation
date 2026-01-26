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
@Table(name = "user_event_actions")
@IdClass(UserEventActionId.class)
@Getter
@Setter
@NoArgsConstructor
public class UserEventAction {
    @Id
    @Column(name = "user_id")
    private long userId;

    @Id
    @Column(name = "event_id")
    private long eventId;

    @Column(name = "weight")
    private double weight;

    @Column(name = "last_action_time")
    private LocalDateTime lastActionTime;
}
