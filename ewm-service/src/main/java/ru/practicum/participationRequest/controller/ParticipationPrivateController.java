package ru.practicum.participationRequest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.participationRequest.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class ParticipationPrivateController {
    private final ParticipationRequestService participationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable long userId,
                                          @RequestParam long eventId) {
        log.debug("Participation in an event ID={} was requested from the user ID={}", eventId, userId);
        return participationService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto patch(@PathVariable long userId,
                                         @PathVariable long requestId) {
        log.debug("Requested cancellation of participation in an event ID={} from the user ID={}", requestId, userId);
        return participationService.patch(userId, requestId);
    }

    @GetMapping
    public List<ParticipationRequestDto> getAll(@PathVariable long userId) {
        log.debug("Received requests from user ID={} to participate in events", userId);
        return participationService.getAll(userId);
    }
}
