package ru.practicum.event;

import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;

import java.util.List;

public interface EventService {

    EventFullDto saveEvent(NewEventDto newEventDto, long userId, String ip);

    List<EventFullDto> getEvents(String text, List<Long> categories, Boolean paid,
                                 String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                 String sort, Integer from, Integer size,
                                 String ip, String user);

    EventFullDto getEventById(long id, String ip);

    List<EventShortDto> getEventsByUserId(long userId, Integer from, Integer size, String ip);

    EventFullDto getEventByUserIdAndEventId(long userId, long eventId, String ip);

    EventFullDto updateEvent(UpdatedEventDto updatedEventDto,
                                       long userId, long eventId, String ip);

    EventFullDto updateAdminEvent(UpdatedEventDto updatedEventDto,
                                            long eventId, String ip);

    EventFullDto saveFullEvent(EventFullDto event);

    Integer checkInitiatorEvent(Long initiatorId, Long eventId);
}