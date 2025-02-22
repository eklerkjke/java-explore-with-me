package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.AdminCompilationDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.NotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Override
    public CompilationDto get(Long id) {
        return CompilationMapper.INSTANCE.toDto(getById(id));
    }

    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations;

        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, pageable);
        } else {
            compilations = compilationRepository.findAll(pageable).getContent();
        }
        return compilations.stream()
                .map(CompilationMapper.INSTANCE::toDto)
                .toList();
    }

    @Override
    public CompilationDto create(AdminCompilationDto adminCompilationDto) {
        Compilation compilation = CompilationMapper.INSTANCE.toEntity(adminCompilationDto);

        if (!adminCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(eventService.getAllEventByIds(adminCompilationDto.getEvents()));
        }
        Compilation newCompilation = compilationRepository.save(compilation);

        return toDtoWithEventShortDto(newCompilation);
    }

    @Override
    public CompilationDto update(AdminCompilationDto adminCompilationDto, Long id) {
        Compilation compilation = getById(id);
        Compilation updateCompilation = CompilationMapper.INSTANCE
                .updateCompilationFromDto(adminCompilationDto, compilation);
        if (!adminCompilationDto.getEvents().isEmpty()) {
            updateCompilation.setEvents(eventService.getAllEventByIds(adminCompilationDto.getEvents()));
        }

        return toDtoWithEventShortDto(compilationRepository.save(updateCompilation));
    }

    @Override
    public void delete(Long id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException("Не удалось найти подборку событий с id: " + id);
        }
        compilationRepository.deleteById(id);
    }

    private Compilation getById(Long id) {
        return compilationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Не удалось найти подборку событий с id: " + id));
    }

    private CompilationDto toDtoWithEventShortDto(Compilation compilation) {
        CompilationDto cd = CompilationMapper.INSTANCE.toDto(compilation);
        if (compilation.getEvents() == null) {
            cd.setEvents(List.of());
            return cd;
        }
        List<EventShortDto> events = compilation.getEvents().stream()
                .map(eventMapper::toEventShortDto)
                .toList();
        cd.setEvents(events);
        return cd;
    }
}