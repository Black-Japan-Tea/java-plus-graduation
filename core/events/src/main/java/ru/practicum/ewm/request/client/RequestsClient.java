package ru.practicum.ewm.request.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.enums.State;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestStatusUpdateRequest;

import java.util.List;

@FeignClient(name = "requests-service")
public interface RequestsClient {
    @GetMapping("/internal/events/{eventId}/requests")
    List<ParticipationRequestDto> getByEvent(@PathVariable Long eventId);

    @PatchMapping("/internal/events/{eventId}/requests")
    List<ParticipationRequestDto> updateStatus(@PathVariable Long eventId,
                                               @RequestBody RequestStatusUpdateRequest request);

    @GetMapping("/internal/events/{eventId}/requests/count")
    Long countByEventAndStatus(@PathVariable Long eventId,
                               @RequestParam("status") State status);
}
