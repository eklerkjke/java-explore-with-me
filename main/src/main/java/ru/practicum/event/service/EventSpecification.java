package ru.practicum.event.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class EventSpecification {

    public Specification<Event> buildForAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd
    ) {
        Specification<Event> specification = Specification.where(null);

        if (users != null && !users.isEmpty()) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users)
            );
        }
        if (states != null && !states.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (rangeStart != null && rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));

            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }

        return specification;
    }

    public Specification<Event> buildForPublic(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable
    ) {
        Specification<Event> specification = Specification.where(null);
        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (paid != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("paid"), paid));
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }

        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), State.PUBLISHED));

        return specification;
    }
}
