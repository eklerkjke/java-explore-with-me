package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictStateException;
import ru.practicum.exception.ConflictTimeException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final ViewService viewService;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final EventSpecification eventSpecification;

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event event = eventMapper.toEntity(newEventDto);
        event.setCategory(categoryService.getCategory(event.getCategory().getId()));
        event.setInitiator(userService.getUserById(userId));
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        return eventMapper.toEventShortDto(events);
    }

    @Override
    public EventFullDto getEventByIdForUser(Long userId, Long eventId) {
        Event event = eventRepository
                .findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format(
                                "Событие с id %d для пользователя с id %d не найдено.",
                                eventId, userId
                        )
                ));
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size
    ) {
        if (rangeStart != null && rangeEnd != null) {
            dateValid(rangeStart, rangeEnd);
        }

        List<Event> events = eventRepository.findAll(
                eventSpecification.buildForAdmin(
                        users,
                        states,
                        categories,
                        rangeStart,
                        rangeEnd
                ),
                PageRequest.of(from / size, size)
        );

        return eventMapper.toEventFullDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long id, UpdateEventAdminRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id %d не найдено.", id)));
        if ((request.getStateAction() == StateAction.PUBLISH_EVENT && event.getState() != State.PENDING) ||
                (request.getStateAction() == StateAction.REJECT_EVENT && event.getState() == State.PUBLISHED)) {
            throw new ConflictStateException(
                    (request.getStateAction() == StateAction.PUBLISH_EVENT) ?
                            "Невозможно опубликовать событие, так как текущий статус не PENDING"
                            : "Нельзя отменить публикацию, так как событие уже опубликовано");
        }
        event.setState(State.CANCELED);
        if (request.getLocation() != null) {
            Location sLk = locationService.getLocation(locationMapper.toLocation(request.getLocation()));
            event.setLocation(sLk);
        }
        eventMapper.updateFromAdmin(request, event);
        event.setState(request.getStateAction() == StateAction.PUBLISH_EVENT ? State.PUBLISHED : State.CANCELED);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest request) {
        if (request.getEventDate() != null && request.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ConflictTimeException("Время не может быть раньше, через два часа от текущего момента");
        }
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id %d для пользователя с id %d не найдено.", eventId, userId)));
        if (event.getState() == State.PUBLISHED) {
            throw new ConflictStateException("Изменить можно только неопубликованное событие");
        }
        if (request.getStateAction() != null) {
            switch (request.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                default -> throw new IllegalArgumentException("Неизвестный статус: " + request.getStateAction());
            }
        }
        if (request.getLocation() != null) {
            event.setLocation(locationService.getLocation(locationMapper.toLocation(request.getLocation())));
        }
        eventMapper.updateFromUser(request, event);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public List<EventShortDto> getPublicEvents(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            HttpServletRequest request
    ) {
        dateValid(rangeStart, rangeEnd);

        PageRequest pageRequest;
        switch (sort) {
            case "EVENT_DATE":
                pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate"));
                break;
            case "VIEWS":
                pageRequest = PageRequest.of(from / size, size, Sort.by("views").descending());
                break;
            default:
                throw new jakarta.validation.ValidationException("Unknown sort: " + sort);
        }
        List<Event> events = eventRepository.findAll(
                eventSpecification.buildForPublic(
                        text,
                        categories,
                        paid,
                        rangeStart,
                        rangeEnd,
                        onlyAvailable
                ),
                pageRequest
        );

        viewService.saveViews(events, request);

        return eventMapper.toEventShortDto(events);
    }

    @Override
    @Transactional
    public EventFullDto getPublicEventById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Event with id=" + id + " was not found"));

        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Event must be published.");
        }

        viewService.saveView(event, request);
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<Event> getAllEventByIds(List<Long> ids) {
        return eventRepository.findAllById(ids);
    }

    private void dateValid(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new ValidationException("Дата начала события позже даты окончания");
        }
    }
}

