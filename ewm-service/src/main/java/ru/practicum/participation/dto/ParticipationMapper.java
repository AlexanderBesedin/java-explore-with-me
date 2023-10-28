package ru.practicum.participation.dto;

import ru.practicum.participation.model.Participation;

public class ParticipationMapper {
    public static ParticipationDto toDto(Participation participation) {
        return ParticipationDto.builder()
                .id(participation.getId())
                .requester(participation.getRequester().getId())
                .event(participation.getEvent().getId())
                .status(participation.getStatus())
                .created(participation.getCreated())
                .build();
    }
}
