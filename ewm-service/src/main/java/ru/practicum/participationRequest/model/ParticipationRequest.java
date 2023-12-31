package ru.practicum.participationRequest.model;

import lombok.Data;
import ru.practicum.event.model.Event;
import ru.practicum.participationRequest.dto.ParticipationRequestState;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "participation_requests")
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_request_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participation_user_id")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "participation_event_id")
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParticipationRequestState status;

    @Column(name = "created_date")
    private LocalDateTime created;
}
