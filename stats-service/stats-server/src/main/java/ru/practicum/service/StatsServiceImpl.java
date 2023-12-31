package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void addHit(EndpointHitDto endpointHitDto) {
        statsRepository.save(EndpointHitMapper.fromDto(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null || start.isAfter(end)) {
            throw new ValidationException("Invalid date range OR 'start' or 'end' is null");
        }
        return (unique) ? statsRepository.findUniqueStats(start, end, uris) :
                statsRepository.findStats(start, end, uris);
    }
}
