package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
public class StatsController {
    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private final StatsService statsService;

    @PostMapping("/hit")
    public void add(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Entry created {}", endpointHitDto.toString());
        statsService.add(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> get(
            @RequestParam(name = "start") @DateTimeFormat(pattern = DATE_TIME) LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = DATE_TIME) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Get stats");
        return statsService.get(start, end, uris, unique);
    }
}