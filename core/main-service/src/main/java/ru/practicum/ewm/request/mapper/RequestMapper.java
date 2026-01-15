package ru.practicum.ewm.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class RequestMapper {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public ParticipationRequestDto toParticipationRequestDto(ParticipationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        String created = null;
        if (request.getCreated() != null) {
            created = request.getCreated().format(formatter);
        }
        Long eventId = null;
        if (request.getEvent() != null) {
            eventId = request.getEvent().getId();
        }
        Long requesterId = null;
        if (request.getRequester() != null) {
            requesterId = request.getRequester().getId();
        }
        String status = null;
        if (request.getStatus() != null) {
            status = request.getStatus().toString();
        }
        return new ParticipationRequestDto(
                request.getId(),
                created,
                eventId,
                requesterId,
                status);
    }
}
