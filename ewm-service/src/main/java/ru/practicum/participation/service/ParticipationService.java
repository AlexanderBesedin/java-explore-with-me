package ru.practicum.participation.service;

import ru.practicum.participation.dto.ParticipationDto;

import java.util.List;

public interface ParticipationService {
    ParticipationDto create(long userId, long eventId);

    ParticipationDto patch(long userId, long requestId);

    List<ParticipationDto> getAll(long userId);
}
