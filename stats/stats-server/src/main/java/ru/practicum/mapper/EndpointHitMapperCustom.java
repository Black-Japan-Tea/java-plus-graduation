package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

public class EndpointHitMapperCustom {
    public static EndpointHit toModel(EndpointHitDto dto) {
        return EndpointHit.builder()
                .ip(dto.getIp())
                .uri(dto.getUri())
                .app(dto.getApp())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public static EndpointHitDto toDto(EndpointHit entity) {
        return EndpointHitDto.builder()
                .ip(entity.getIp())
                .uri(entity.getUri())
                .app(entity.getApp())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
