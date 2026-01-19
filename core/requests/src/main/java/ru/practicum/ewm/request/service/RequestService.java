package ru.practicum.ewm.request.service;

import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> get(Long userId);

    ParticipationRequestDto cancel(Long userId, Long requestId);

    List<ParticipationRequestDto> getByEvent(Long eventId);

    List<ParticipationRequestDto> updateStatus(Long eventId, RequestStatusUpdateRequest request);

    long countByEventAndStatus(Long eventId, State status);
}
