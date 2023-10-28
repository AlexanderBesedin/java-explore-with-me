package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventUpdateAdminRequest;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.service.EventService;
import ru.practicum.util.DateConstant;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping()
    public List<EventFullDto> getAllByFilter(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<EventState> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = DateConstant.DATE_FORMAT) LocalDateTime rangeStart,
                                             @RequestParam(required = false)
                                             @DateTimeFormat(pattern = DateConstant.DATE_FORMAT) LocalDateTime rangeEnd,
                                             @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                             @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.debug("Admin requested all event views by filters");
        return eventService.getAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patch(@PathVariable long eventId,
                              @Valid @RequestBody EventUpdateAdminRequest updateEventAdminRequest) {
        log.debug("Event ID={} updated by admin", eventId);
        return eventService.patchByAdmin(eventId, updateEventAdminRequest);
    }
}
