package ru.practicum.event.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAll(Specification<Event> specification, Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.initiator.id IN :userIds")
    List<Event> findAllByUserIdIn(List<Long> userIds);
}
