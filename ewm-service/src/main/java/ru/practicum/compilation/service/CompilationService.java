package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationNewDto;
import ru.practicum.compilation.dto.CompilationUpdateRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto create(CompilationNewDto compilationNewDto);

    CompilationDto patch(long compId, CompilationUpdateRequest compilationUpdateRequest);

    CompilationDto getById(long compId);

    List<CompilationDto> getAll(Boolean pinned, int from, int size);

    void delete(long compId);
}
