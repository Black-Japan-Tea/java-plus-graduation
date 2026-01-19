package ru.practicum.ewm.stats;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatsDto;

import java.util.List;

@FeignClient(name = "stats-server")
public interface StatsClient {
    @PostMapping("/hit")
    EndpointHitDto send(@RequestBody EndpointHitDto endpointHit);

    @GetMapping("/stats")
    List<ViewStatsDto> receive(@RequestParam("start") String start,
                               @RequestParam("end") String end,
                               @RequestParam(value = "uris", required = false) String[] uris,
                               @RequestParam("unique") Boolean unique);
}
