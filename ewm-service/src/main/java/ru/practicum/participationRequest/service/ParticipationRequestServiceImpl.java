package ru.practicum.participationRequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventState;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.participationRequest.dto.ParticipationRequestMapper;
import ru.practicum.participationRequest.dto.ParticipationRequestState;
import ru.practicum.participationRequest.model.ParticipationRequest;
import ru.practicum.participationRequest.repository.ParticipationRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(long userId, long eventId) {
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
            if (event.getParticipantLimit()
                    <= participationRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED)) {
                throw new ConflictException("The number of participation requests has exceeded the limit for the event");
            }
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(requester);
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(event.getRequestModeration() && !event.getParticipantLimit().equals(0) ?
                ParticipationRequestState.PENDING : ParticipationRequestState.CONFIRMED);

        return ParticipationRequestMapper.INSTANCE.toDto(participationRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto patch(long userId, long requestId) {
        findUserById(userId);
        ParticipationRequest participationRequest = participationRepository.findById(requestId)
                .orElseThrow(
                        () -> new NotFoundException("Participation request with id=" + requestId + " was not found"));

        if (!participationRequest.getRequester().getId().equals(userId)) {
            throw new NotFoundException("No events available for editing were found");
        }

        participationRequest.setStatus(ParticipationRequestState.CANCELED);

        return ParticipationRequestMapper.INSTANCE.toDto(participationRepository.save(participationRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getAll(long userId) {
        findUserById(userId);
        return participationRepository.findAllByRequesterId(userId).stream()
                .map(ParticipationRequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }
}
