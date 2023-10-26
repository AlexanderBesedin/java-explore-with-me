package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

public class HitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto dto) {
        return EndpointHit.builder()
                .id(dto.getId())
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }
}
