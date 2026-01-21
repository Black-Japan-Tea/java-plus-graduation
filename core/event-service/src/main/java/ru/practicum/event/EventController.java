package ru.practicum.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto saveEvent(@Valid @RequestBody NewEventDto newEventDto,
                                  @PathVariable(name = "userId") Long userId,
                                  HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("Получен запрос на создание события от пользователя с ID {}, IP: {}", userId, ip);
        return eventService.saveEvent(newEventDto, userId, ip);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByIdAndUserId(@RequestBody @Valid UpdatedEventDto updatedEventDto,
                                                 @PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("PATCH /users/{}/events/{} from IP {}", userId, eventId, ip);
        return eventService.updateEvent(updatedEventDto, userId, eventId, ip);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateAdminEventById(@RequestBody @Valid UpdatedEventDto updatedEventDto,
                                                 @PathVariable Long eventId,
                                                 HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("PATCH /admin/events/{} from IP {}", eventId, ip);
        return eventService.updateAdminEvent(updatedEventDto, eventId, ip);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("GET /events/{} from IP {}", id, ip);
        return eventService.getEventById(id, ip);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        log.info("GET /events from IP {}, params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, sort={}, from={}, size={}",
                ip, text, categories, paid, rangeStart, rangeEnd, sort, from, size);
        return eventService.getEvents(text, categories, paid,
                                        rangeStart, rangeEnd,
                                        onlyAvailable, sort, from, size,
                                        ip, "user");
    }

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEventsByUserId(@PathVariable Long userId, HttpServletRequest request,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        String ip = request.getRemoteAddr();
        log.info("GET /users/{}/events from IP {}, from={}, size={}", userId, ip, from, size);
        return eventService.getEventsByUserId(userId, from, size, ip);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEventByUserIdAndEventId(@PathVariable("userId") Long userId,
                                                   @PathVariable("eventId") Long eventId,
                                                   HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        log.info("GET /users/{}/events/{} from IP {}, from={}, size={}", userId, eventId, ip);
        return eventService.getEventByUserIdAndEventId(userId, eventId, ip);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getAdminEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        String ip = request.getRemoteAddr();
        log.info("GET /admin/events from IP {}, params: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, sort={}, from={}, size={}",
                ip, text, categories, paid, rangeStart, rangeEnd, sort, from, size);
        return eventService.getEvents(text, categories, paid,
                rangeStart, rangeEnd,
                onlyAvailable, sort, from, size,
                ip, "admin");
    }

    @PostMapping("/admin/events/increment")
    public EventFullDto saveFullEvent(@RequestBody EventFullDto event) {
        return eventService.saveFullEvent(event);
    }
}