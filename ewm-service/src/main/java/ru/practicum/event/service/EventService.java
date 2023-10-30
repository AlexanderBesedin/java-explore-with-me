package ru.practicum.event.service;

import ru.practicum.event.controller.EventPublicController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventState;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.update_request.EventUpdateAdminRequest;
import ru.practicum.event.dto.update_request.EventUpdateUserRequest;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto create(long userId, EventNewDto eventNewDto);

    EventFullDto patchByAdmin(long eventId, EventUpdateAdminRequest eventUpdateAdminRequest);

    EventFullDto patchByInitiator(long userId, long eventId, EventUpdateUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult patchParticipationRequestsByInitiator(long userId,
                                                                         long eventId,
                                                                         EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getAllByAdmin(List<Long> users,
                                     List<EventState> states,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from,
                                     int size);

    List<EventShortDto> getAllByInitiator(long userId, int from, int size);

    EventFullDto getByIdByInitiator(long userId, long eventId);

    List<ParticipationRequestDto> getParticipationRequestsByInitiator(long userId, long eventId);


    List<EventShortDto> getAllPublic(String text,
                                     List<Long> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     boolean onlyAvailable,
                                     EventPublicController.SortMode sort,
                                     int from,
                                     int size,
                                     HttpServletRequest request);

    EventFullDto getByIdPublic(long eventId, HttpServletRequest request);
}
