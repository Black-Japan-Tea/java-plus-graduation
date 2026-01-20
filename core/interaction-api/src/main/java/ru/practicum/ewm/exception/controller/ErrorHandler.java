package ru.practicum.ewm.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.exception.AccessException;
import ru.practicum.ewm.exception.DataViolationException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.exception.model.ErrorResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static final DateTimeFormatter PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException exception) {
        return new ErrorResponse(exception.getMessage(),
                "Запрашиваемые данные не были найдены",
                "NOT_FOUND", LocalDateTime.now().format(PATTERN));
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleAccessException(final AccessException exception) {
        return new ErrorResponse(exception.getMessage(),
                "Нет доступа к запрашиваемому ресурсу",
                "BAD_REQUEST", LocalDateTime.now().format(PATTERN));
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public ErrorResponse handleDataViolationException(final DataViolationException exception) {
        return new ErrorResponse(exception.getMessage(),
                "Нарушение целостности данных",
                "CONFLICT", LocalDateTime.now().format(PATTERN));
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException exception) {
        return new ErrorResponse(exception.getMessage(),
                "Ошибка валидации данных, данные указаны некорректно",
                "BAD_REQUEST", LocalDateTime.now().format(PATTERN));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.error("Validation error: ", exception);
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Ошибка валидации данных");
        return new ErrorResponse(message,
                "Ошибка валидации данных, данные указаны некорректно",
                "BAD_REQUEST", LocalDateTime.now().format(PATTERN));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        log.error("Type mismatch error: ", exception);
        return new ErrorResponse("Неверный тип параметра: " + exception.getName(),
                "Ошибка валидации данных, данные указаны некорректно",
                "BAD_REQUEST", LocalDateTime.now().format(PATTERN));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException exception) {
        log.error("Missing parameter error: ", exception);
        return new ErrorResponse("Отсутствует обязательный параметр: " + exception.getParameterName(),
                "Ошибка валидации данных, данные указаны некорректно",
                "BAD_REQUEST", LocalDateTime.now().format(PATTERN));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        log.error("Unreadable request body: ", exception);
        return new ErrorResponse("Required request body is missing",
                "Ошибка валидации данных, данные указаны некорректно",
                "BAD_REQUEST", LocalDateTime.now().format(PATTERN));
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception exception) {
        log.error("Unhandled exception: ", exception);
        String message = exception.getMessage();
        if (message == null || message.isEmpty()) {
            message = exception.getClass().getSimpleName() + ": " + 
                     (exception.getCause() != null ? exception.getCause().getMessage() : "Внутренняя ошибка сервера");
        }
        return new ErrorResponse(message,
                "Произошла непредвиденная ошибка",
                "INTERNAL_SERVER_ERROR", LocalDateTime.now().format(PATTERN));
    }
}
