package ru.practicum.ewm.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentShort;
import ru.practicum.ewm.comment.dto.CommentShortImpl;
import ru.practicum.ewm.comment.dto.FullCommentDto;
import ru.practicum.ewm.comment.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CommentMapper {

    public FullCommentDto toFullCommentDto(Comment comment) {
        FullCommentDto commentDto = new FullCommentDto();
        commentDto.setId(comment.getId());
        commentDto.setEventId(comment.getEventId());
        if (comment.getAuthor() != null) {
            commentDto.setAuthorName(comment.getAuthor().getName());
        }
        commentDto.setText(comment.getText());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getEventId(), comment.getText());
    }

    public CommentShort toCommentShort(Comment comment) {
        if (comment == null) {
            return null;
        }
        String authorName = comment.getAuthor() != null ? comment.getAuthor().getName() : null;
        return new CommentShortImpl(authorName, comment.getText());
    }

    public List<CommentShort> toCommentShortList(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return comments.stream()
                .map(this::toCommentShort)
                .filter(comment -> comment != null)
                .collect(Collectors.toList());
    }
}