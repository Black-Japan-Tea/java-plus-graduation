package ru.practicum.ewm.comment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentShortImpl implements CommentShort {
    @JsonProperty("authorName")
    private String authorName;
    
    @JsonProperty("text")
    private String text;
}
