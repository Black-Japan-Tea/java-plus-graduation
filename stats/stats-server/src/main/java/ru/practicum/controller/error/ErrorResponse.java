package ru.practicum.controller.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponse {
    private final int status;
    private final List<String> errors;


    public ErrorResponse(HttpStatus status, List<String> messages) {
        this.status = status.value();
        this.errors = messages;
    }

    public ErrorResponse(HttpStatus status, String message) {
        this.status = status.value();
        this.errors = List.of(message);
    }

}
