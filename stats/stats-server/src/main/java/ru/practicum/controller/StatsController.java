package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@RequestBody @Valid EndpointHitDto hitDto) {
        log.info("POST /hit - Received: {}", hitDto);
        service.saveHit(hitDto);
        log.info("Hit successfully saved.");
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@NotNull @RequestParam("start") LocalDateTime start,
                                       @NotNull @RequestParam("end") LocalDateTime end,
                                       @RequestParam(value = "uris", required = false) List<String> uris,
                                       @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("GET /stats - Params: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        List<ViewStatsDto> stats = service.getStats(start, end, uris, unique);
        log.info("Stats retrieved: {} records", stats.size());
        return stats;
    }
}
