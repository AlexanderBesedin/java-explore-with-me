package ru.practicum.participationRequest.service;

import ru.practicum.participationRequest.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto create(long userId, long eventId);

    ParticipationRequestDto patch(long userId, long requestId);

    List<ParticipationRequestDto> getAll(long userId);
}
