package ru.practicum.participation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participation.dto.ParticipationDto;
import ru.practicum.participation.dto.ParticipationMapper;
import ru.practicum.participation.enums.ParticipationState;
import ru.practicum.participation.model.Participation;
import ru.practicum.participation.repository.ParticipationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService {
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationDto create(long userId, long eventId) {
        User requester = findUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event initiator cannot submit a participation request for own event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Cannot participate in an unpublished event");
        }

        if (event.getParticipantLimit() > 0) {
            if (event.getParticipantLimit() <= participationRepository.countByEventIdAndStatus(eventId, ParticipationState.CONFIRMED)) {
                throw new ConflictException("The number of participation requests has exceeded the limit for the event");
            }
        }

        Participation participation = new Participation();
        participation.setRequester(requester);
        participation.setEvent(event);
        participation.setCreated(LocalDateTime.now());
        participation.setStatus(event.getRequestModeration() && !event.getParticipantLimit().equals(0) ?
                ParticipationState.PENDING : ParticipationState.CONFIRMED);

        return ParticipationMapper.toDto(participationRepository.save(participation));
    }

    @Override
    @Transactional
    public ParticipationDto patch(long userId, long requestId) {
        findUserById(userId);
        Participation participation = participationRepository.findById(requestId)
                .orElseThrow(
                        () -> new NotFoundException("Participation request with id=" + requestId + " was not found"));

        if (!participation.getRequester().getId().equals(userId)) {
            throw new NotFoundException("No events available for editing were found");
        }
        participation.setStatus(ParticipationState.CANCELED);

        return ParticipationMapper.toDto(participationRepository.save(participation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationDto> getAll(long userId) {
        findUserById(userId);
        return participationRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationMapper::toDto)
                .collect(Collectors.toList());
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }
}
