package ru.practicum.event.service;

import ru.practicum.event.controller.EventControllerPublic;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventState;
import ru.practicum.participation.dto.ParticipationDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto create(long userId, EventNewDto eventNewDto);

    EventFullDto patchByAdmin(long eventId, EventUpdateAdminRequest eventUpdateAdminRequest);

    EventFullDto patchByInitiator(long userId, long eventId, EventUpdateUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult patchParticipationRequestsByInitiator(long userId,
                                                                         long eventId,
                                                                         EventRequestStatusUpdateRequest eventRequestStatusUpdate);

    List<EventFullDto> getAllByAdmin(List<Long> users,
                                     List<EventState> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from,
                                     int size);

    EventFullDto getByIdByInitiator(long userId, long eventId);

    List<EventShortDto> getAllByInitiator(long userId, int from, int size);

    List<ParticipationDto> getParticipationRequestsByInitiator(long userId, long eventId);

    EventFullDto getByIdPublic(long eventId, HttpServletRequest request);

    List<EventShortDto> getAllPublic(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     boolean onlyAvailable,
                                     EventControllerPublic.SortMode sort,
                                     int from,
                                     int size,
                                     HttpServletRequest request);
}
