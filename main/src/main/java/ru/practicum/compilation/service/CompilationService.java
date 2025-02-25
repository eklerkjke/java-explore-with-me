package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.AdminCompilationDto;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto get(Long id);

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto create(AdminCompilationDto adminCompilationDto);

    CompilationDto update(AdminCompilationDto adminCompilationDto, Long id);

    void delete(Long id);
}
