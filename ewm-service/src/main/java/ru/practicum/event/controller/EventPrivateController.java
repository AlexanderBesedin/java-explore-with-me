package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.update_request.EventUpdateUserRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable long userId,
                               @Valid @RequestBody EventNewDto eventNewDto) {
        log.debug("Requested to create an event {} by the initiator ID {}", eventNewDto.toString(), userId);
        return eventService.create(userId, eventNewDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEventInfo(@PathVariable long userId,
                                       @PathVariable long eventId,
                                       @Valid @RequestBody EventUpdateUserRequest updateEventUserRequest) {
        log.debug("Update event ID={} requested by initiator ID={}", eventId, userId);
        return eventService.patchByInitiator(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchEventRequests(@PathVariable long userId,
                                                             @PathVariable long eventId,
                                                             @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.debug("Requested update of requests for participation in event ID={} by initiator ID={}", eventId, userId);
        return eventService.patchParticipationRequestsByInitiator(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getById(@PathVariable long userId,
                                @PathVariable long eventId) {
        log.debug("Requested to view an event ID={} added by user ID={}", eventId, userId);
        return eventService.getByIdByInitiator(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationByInitiator(@PathVariable long userId,
                                                                     @PathVariable long eventId) {
        log.debug("initiator ID={} views requests to participate in the event ID={}", userId, eventId);
        return eventService.getParticipationRequestsByInitiator(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getAll(@PathVariable long userId,
                                      @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Requested for all event views added by user ID={}", userId);
        return eventService.getAllByInitiator(userId, from, size);
    }
}
