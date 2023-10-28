package ru.practicum.participation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.participation.dto.ParticipationDto;
import ru.practicum.participation.service.ParticipationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
@Slf4j
public class ParticipationPrivateController {
    private final ParticipationService participationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationDto create(@PathVariable long userId,
                                   @RequestParam long eventId) {
        log.debug("Participation in an event ID={} was requested from the user ID={}", eventId, userId);
        return participationService.create(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationDto patch(@PathVariable long userId,
                                  @PathVariable long requestId) {
        log.debug("Requested cancellation of participation in an event ID={} from the user ID={}", requestId, userId);
        return participationService.patch(userId, requestId);
    }

    @GetMapping()
    public List<ParticipationDto> getAll(@PathVariable long userId) {
        log.debug("Received requests from user ID={} to participate in events", userId);
        return participationService.getAll(userId);
    }
}
