package ru.practicum.ewm.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final StatsClient client;

    public EndpointHitDto post(EndpointHitDto endpointHit) {
        return client.send(endpointHit);
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, String[] uris, String unique) {
        Boolean uniqueValue = unique != null && Boolean.parseBoolean(unique);
        return client.receive(start.format(FORMATTER), end.format(FORMATTER), uris, uniqueValue);
    }
}
