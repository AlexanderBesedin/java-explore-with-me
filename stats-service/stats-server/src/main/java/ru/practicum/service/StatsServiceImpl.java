package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.HitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void add(EndpointHitDto endpointHitDto) {
        EndpointHit hit = HitMapper.toEndpointHit(endpointHitDto);
        statsRepository.save(hit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris != null) {
            return (unique) ? statsRepository.findUniqueStats(start, end, uris) :
                              statsRepository.findStats(start, end, uris);
        } else {
            return (unique) ? statsRepository.findUniqueStats(start, end) :
                              statsRepository.findStats(start, end);
        }
    }
}
