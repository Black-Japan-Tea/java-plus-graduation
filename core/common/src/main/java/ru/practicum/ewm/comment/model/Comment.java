package ru.practicum.ewm.comment.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "event_id")
    private Long eventId;

    private String text;

    private LocalDateTime created;
}