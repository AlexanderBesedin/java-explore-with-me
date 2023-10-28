package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(CompilationNewDto compilationNewDto) {
        Set<Event> events = compilationNewDto.getEvents() != null && !compilationNewDto.getEvents().isEmpty() ?
                eventRepository.findAllByIdIn(compilationNewDto.getEvents()) : Collections.emptySet();

        return CompilationMapper.toDto(compilationRepository.save(CompilationMapper.fromDto(compilationNewDto, events)));
    }

    @Override
    @Transactional
    public CompilationDto patch(long compId, CompilationUpdateRequest compUpdateRequest) {
        findCompilationById(compId);

        Set<Event> events = compUpdateRequest.getEvents() != null && !compUpdateRequest.getEvents().isEmpty() ?
                eventRepository.findAllByIdIn(compUpdateRequest.getEvents()) : Collections.emptySet();

        return CompilationMapper.toDto(
                compilationRepository.save(CompilationMapper.fromDto(compUpdateRequest, events)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, int from, int size) {
        return compilationRepository.findAllByPublic(pinned, PageRequest.of(from, size)).stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(long compId) {
        return CompilationMapper.toDto(findCompilationById(compId));
    }

    @Override
    @Transactional
    public void delete(long compId) {
        findCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    private Compilation findCompilationById(long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + id + " was not found"));
    }
}
