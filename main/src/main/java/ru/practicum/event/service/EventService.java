package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsForUser(Long userId, Integer from, Integer size);

    EventFullDto getEventByIdForUser(Long userId, Long eventId);

    List<EventFullDto> getEventsForAdmin(List<Long> users, List<String> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long id, UpdateEventAdminRequest rq);

    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest rq);

    List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                        Integer size, HttpServletRequest request);

    EventFullDto getPublicEventById(Long id, HttpServletRequest rqt);

    List<Event> getAllEventByIds(List<Long> ids);
}
