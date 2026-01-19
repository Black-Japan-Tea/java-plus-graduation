package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/internal/events/{eventId}/requests")
@RequiredArgsConstructor
public class InternalRequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getByEvent(@PathVariable Long eventId) {
        return requestService.getByEvent(eventId);
    }

    @PatchMapping
    public List<ParticipationRequestDto> updateStatus(@PathVariable Long eventId,
                                                      @RequestBody RequestStatusUpdateRequest request) {
        return requestService.updateStatus(eventId, request);
    }

    @GetMapping("/count")
    public long countByEventAndStatus(@PathVariable Long eventId,
                                      @RequestParam State status) {
        return requestService.countByEventAndStatus(eventId, status);
    }
}
