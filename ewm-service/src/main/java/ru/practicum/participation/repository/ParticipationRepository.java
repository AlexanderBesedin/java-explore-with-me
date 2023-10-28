package ru.practicum.participation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.participation.enums.ParticipationState;
import ru.practicum.participation.model.Participation;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByRequesterId(Long id);

    List<Participation> findAllByEventId(Long id);

    List<Participation> findAllByIdIn(List<Long> ids);

    @Query("SELECT COUNT (pr) FROM Participation pr WHERE pr.event.id = :eventId AND pr.status = :status")
    Long countByEventIdAndStatus(@Param("eventId") Long eventId,
                                 @Param("status") ParticipationState status);
}
