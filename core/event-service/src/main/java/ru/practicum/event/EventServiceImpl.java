package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.CategoryRepository;
import ru.practicum.client.user.UserAdminClient;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdatedEventDto;
import ru.practicum.dto.event.enums.State;
import ru.practicum.dto.event.enums.StateAction;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConditionsNotMetException;
import ru.practicum.exception.ConflictPropertyConstraintException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final CategoryRepository categoryRepository;
    private final EventDtoMapper eventDtoMapper;
    private final UserAdminClient userAdminClient;

    public EventFullDto saveEvent(NewEventDto newEventDto, long userId, String ip) {
        log.debug("Попытка сохранить новое событие: {}", newEventDto);

        checkUserId(userId);
        checkCategoryId(newEventDto.getCategory());

        String uri = "/users/" + userId + "/events";

        log.info("Отправка статистики: ip={}, uri={}", ip, uri);
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build());
        Event event = eventDtoMapper.mapToModel(newEventDto, userId);
        event.setCreatedOn(LocalDateTime.now());

        if (newEventDto.getPaid() == null)
            event.setPaid(false);
        if (newEventDto.getRequestModeration() == null)
            event.setRequestModeration(true);
        log.debug("Событие после маппинга: {}", event);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        Event savedEvent = eventRepository.save(event);
        log.info("Событие сохранено с ID {}", savedEvent.getId());
        EventFullDto dto = eventDtoMapper.mapToFullDto(savedEvent);
        log.debug("Сформированный EventFullDto: {}", dto);
        return dto;
    }

    public EventFullDto updateEvent(UpdatedEventDto updatedEvent,
                                    long userId, long eventId, String ip) {
        log.info("Попытка обновить событие. userId={}, eventId={}, ip={}", userId, eventId, ip);

        checkUserId(userId);

       Event event = checkAndGetEventById(eventId);

        String uri = "/users/" + userId + "/events/" + eventId;
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build());

        if (!event.getState().equals(State.CANCELED) && !event.getState().equals(State.PENDING)) {
            log.warn("Событие нельзя изменить. Состояние: {}, Модерация: {}",
                    event.getState(), event.getRequestModeration());
            throw new ConflictPropertyConstraintException(
                    "Изменить можно только отменённое событие или находящееся на модерации");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Событие начинается слишком скоро: {}", event.getEventDate());
            throw new ConditionsNotMetException(
                    "Нельзя изменить событие, которое начинается в течение двух часов");
        }

        log.info("Применение обновлений к событию id={}", eventId);
        applyUpdate(event, updatedEvent);

        Event savedUpdatedEvent = eventRepository.save(event);
        log.info("Событие успешно обновлено. id={}", savedUpdatedEvent.getId());

        return eventDtoMapper.mapToFullDto(savedUpdatedEvent);
    }

    public EventFullDto updateAdminEvent(UpdatedEventDto updatedEvent,
                                         long eventId, String ip) {
        log.info("Админ запрашивает обновление события. eventId={}, ip={}", eventId, ip);

        Event event = checkAndGetEventById(eventId);

        String uri = "/admin/events/" + eventId;
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build());

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            log.warn("Событие начинается в течение часа: {}", event.getEventDate());
            throw new ConditionsNotMetException(
                    "Нельзя изменить событие, которое начинается в течение часа");
        }


        if(event.getState().equals(State.PUBLISHED))
            throw new ConflictPropertyConstraintException("Нельзя менять статус у уже опубликованного события");
        if (event.getState().equals(State.CANCELED))
            throw new ConflictPropertyConstraintException("Нельзя менять статус у уже отмененного события");

        if (updatedEvent.getStateAction() != null
                && updatedEvent.getStateAction().equals(StateAction.PUBLISH_EVENT)
                && !event.getState().equals(State.PENDING)) {
            log.warn("Попытка опубликовать событие без модерации. eventId={}", eventId);
            throw new ConflictPropertyConstraintException(
                    "Нельзя опубликовать событие, которое не находится в ожидании публикации");
        }

        log.info("Применение обновлений админом к событию id={}", eventId);
        applyUpdate(event, updatedEvent);
        Event savedUpdatedEvent = eventRepository.save(event);
        log.info("Событие успешно обновлено админом. id={}", savedUpdatedEvent.getId());

        return eventDtoMapper.mapToFullDto(savedUpdatedEvent);
    }


    public List<EventFullDto> getEvents(String text, List<Long> categories, Boolean paid,
                                        String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                        String sort, Integer from, Integer size, String ip, String user) {
        log.info("Получен запрос на получение событий. Пользователь: {}, IP: {}, параметры: [text: {}, categories: {}, paid: {}, rangeStart: {}, rangeEnd: {}, onlyAvailable: {}, sort: {}, from: {}, size: {}]",
                user, ip, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = LocalDateTime.now().format(formatter);
            log.info("rangeStart не был задан, использовано текущее время: {}", rangeStart);
        }

        LocalDateTime start = LocalDateTime.parse(rangeStart, formatter);
        LocalDateTime end = null;

        if (rangeEnd != null && !rangeEnd.isBlank()) {
            end = LocalDateTime.parse(rangeEnd, formatter);
            if (start.isAfter(end)) {
                throw new BadRequestException("Время начала события не может быть позже даты окончания");
            }
        }

        log.info("Финальный диапазон дат: start={}, end={}", start, end);

        boolean isAdmin = !"user".equalsIgnoreCase(user);
        String uri = isAdmin ? "/admin/events" : "/events";

        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build());


        Sort sortParam;
        if (sort != null && sort.equals("EVENT_DATE")) {
            sortParam = Sort.by("eventDate").ascending();
        } else {
            sortParam = Sort.unsorted();
        }

        int safeFrom = (from != null) ? from : 0;
        int safeSize = (size != null) ? size : 10;
        PageRequest page = PageRequest.of(safeFrom / safeSize, safeSize, sortParam);


        List<EventFullDto> events = eventRepository.getEvents(text, categories, paid,
                        start, end, onlyAvailable, isAdmin, page)
                .stream()
                .map(eventDtoMapper::mapToFullDto)
                .toList();

        log.info("Получено {} событий после фильтрации", events.size());

        List<EventFullDto> eventsWithViews = events.stream()
                .map(e -> {
                    String uriEvent = "/events/" + e.getId();
                    List<ViewStatsDto> statsList = statsClient.getStats(
                            LocalDateTime.of(1900, 1, 1, 0, 0), LocalDateTime.now(), List.of(uriEvent), false);
                    long views = statsList.isEmpty() ? 0L : statsList.getFirst().getHits();
                    e.setViews(views);
                    return e;
                }).toList();

        if (sort != null && sort.equals("VIEWS")) {
            log.info("Сортировка по количеству просмотров");
            return events.stream()
                    .sorted(Comparator.comparing(EventFullDto::getViews))
                    .toList();
        }

        return eventsWithViews;
    }

    public List<EventShortDto> getEventsByUserId(long userId, Integer from, Integer size, String ip) {
        log.debug("Получен запрос на получение событий пользователя с id={} (from={}, size={}, ip={})", userId, from, size, ip);

        log.debug("Проверка существования пользователя с id={}", userId);
        if (userAdminClient.getUserById(userId) == null) {
            log.warn("Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        log.debug("Пользователь с id {} найден", userId);

        String uri = "/users/" + userId + "/events";
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build());


        int safeFrom = (from != null) ? from : 0;
        int safeSize = (size != null) ? size : 10;
        PageRequest page = PageRequest.of(safeFrom / safeSize, safeSize);

        log.debug("Ищем события пользователя с id={} с пагинацией from={}, size={}", userId, safeFrom, safeSize);
        List<Event> userEvents = eventRepository.findAllByInitiatorId(userId, page);
        log.debug("Найдено {} событий", userEvents.size());

        return userEvents.stream()
                .map(e -> {
                    String uriEvent = "/events/" + e.getId();
                    List<ViewStatsDto> statsList = statsClient.getStats(
                            LocalDateTime.of(1900, 1, 1, 0, 0), LocalDateTime.now(), List.of(uriEvent), false);
                    long views = statsList.isEmpty() ? 0L : statsList.getFirst().getHits();
                    e.setViews(views);
                    return e;
                })
                .map(eventDtoMapper::mapToShortDto)
                .toList();
    }

    public EventFullDto getEventByUserIdAndEventId(long userId, long eventId, String ip) {
        log.debug("Получен запрос на получение события с id={} пользователя с id={} (ip={})",
                eventId, userId, ip);

        if (userAdminClient.getUserById(userId) == null) {
            log.warn("Пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        String uri = "/users/" + userId + "/events/" + eventId;
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build());

        Optional<Event> eventOpt = eventRepository.findByInitiatorIdAndId(userId, eventId);
        if (eventOpt.isEmpty()) {
            log.warn("Событие с id={} для пользователя с id={} не найдено", eventId, userId);
            throw new NotFoundException("Событие с id " + eventId + " не найдено для пользователя " + userId);
        }

        log.debug("Событие с id={} найдено для пользователя с id={}", eventId, userId);
        return eventDtoMapper.mapToFullDto(eventOpt.get());
    }


    public EventFullDto getEventById(long id, String ip) {
        log.debug("Получен запрос на получение события с id={} от ip={}", id, ip);

        String uri = "/events/" + id;
        statsClient.saveHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .ip(ip)
                .uri(uri)
                .timestamp(LocalDateTime.now())
                .build());

        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isEmpty()) {
            log.warn("Событие с id={} не найдено", id);
            throw new NotFoundException("Событие с id " + id + " не найдено");
        }

        Event event = eventOpt.get();

        if (!event.getState().equals(State.PUBLISHED))
            throw new NotFoundException("Событие с id " + id + " не опубликовано");


        log.debug("Событие с id={} найдено", id);
        EventFullDto dto = eventDtoMapper.mapToFullDto(event);
        List<ViewStatsDto> statsList = statsClient.getStats(
                LocalDateTime.of(1900, 1, 1, 0, 0), LocalDateTime.now(), List.of(uri), true);
        long views = statsList.isEmpty() ? 0L : statsList.get(0).getHits();
        dto.setViews(views);

        log.debug("Количество просмотров события с id={}: {}", id, views);
        return dto;
    }

    public Integer checkInitiatorEvent(Long eventId, Long initiatorId) {
        Optional<Event> found = eventRepository.findByInitiatorIdAndId(initiatorId, eventId);
        if (found.isEmpty()) {
            return Integer.valueOf(0);
        } else {
            return Integer.valueOf(1);
        }
    }

    public EventFullDto saveFullEvent(EventFullDto event) {
        log.info("Начат процесс полного сохранения события");
        Event foundEvent = checkAndGetEventById(event.getId());
        log.debug("Количество запросов до обновления: " + foundEvent.getConfirmedRequests());
        applyUpdateFromFull(foundEvent, event);
        Event saved = eventRepository.saveAndFlush(foundEvent);
        log.debug("Количество запросов после обновления: " + saved.getConfirmedRequests());
        return eventDtoMapper.mapToFullDto(saved);
    }

    private void applyUpdateFromFull(Event event, EventFullDto dto) {
        log.info("Начато полное обновление Event из EventFullDto");

        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }

        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }

        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }

        if (dto.getCategory() != null && dto.getCategory().getId() != null) {
            event.setCategory(dto.getCategory().getId());
        }

        if (dto.getConfirmedRequests() >= 0) {
            event.setConfirmedRequests(dto.getConfirmedRequests());
        }

        if (dto.getParticipantLimit() >= 0) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }

        if (dto.getCreatedOn() != null) {
            event.setCreatedOn(dto.getCreatedOn());
        }

        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }

        if (dto.getPublishedOn() != null) {
            event.setPublishedOn(dto.getPublishedOn());
        }

        if (dto.getInitiator() != null && dto.getInitiator().getId() != null) {
            event.setInitiatorId(dto.getInitiator().getId());
        }

        if (dto.getLocation() != null) {
            if (dto.getLocation().getLat() != 0.0) {
                event.setLocationLat(dto.getLocation().getLat());
            }
            if (dto.getLocation().getLon() != 0.0) {
                event.setLocationLon(dto.getLocation().getLon());
            }
        }

        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }

        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }

        if (dto.getState() != null) {
            event.setState(dto.getState());
        }

        if (dto.getViews() != null) {
            event.setViews(dto.getViews());
        }

        log.info("Обновление Event завершено успешно");
    }


    private void applyUpdate(Event event, UpdatedEventDto dto) {
        log.info("Начат процесс обновления события, просматриваются поля на изменения");
        if (dto.getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        if (dto.getDescription() != null) {
            event.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            event.setLocationLat(dto.getLocation().getLat());
            event.setLocationLon(dto.getLocation().getLon());
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() > 0) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            event.setTitle(dto.getTitle());
        }
        if (dto.getCategory() > 0) {
            categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория не найдена"));
            event.setCategory(dto.getCategory());
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                case PUBLISH_EVENT -> event.setState(State.PUBLISHED);
                case REJECT_EVENT -> event.setState(State.CANCELED);
                default -> throw new BadRequestException("Неизвестное действие: " + dto.getStateAction());
            }
        }
        log.info("Событие обновлено");
    }

    private void checkUserId(long userId) {
        if (userAdminClient.getUserById(userId) == null) {
            log.warn("Пользователь с id {} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
    }

    private void checkCategoryId(long catId) {
        if (categoryRepository.findById(catId).isEmpty()) {
            log.warn("Категория с id {} не найдена", catId);
            throw new NotFoundException("Категория с id " + catId + " не найдена");
        }
    }

    private Event checkAndGetEventById(long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            log.warn("Событие с id {} не найдено", eventId);
            throw new NotFoundException("Событие с id " + eventId + " не найдено");
        }
        return eventOpt.get();
    }
}
