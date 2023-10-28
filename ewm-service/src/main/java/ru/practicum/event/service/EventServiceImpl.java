package ru.practicum.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.controller.EventControllerPublic;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.location.Location;
import ru.practicum.event.location.LocationDto;
import ru.practicum.event.location.LocationMapper;
import ru.practicum.event.location.LocationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.participation.dto.ParticipationDto;
import ru.practicum.participation.dto.ParticipationMapper;
import ru.practicum.participation.enums.ParticipationState;
import ru.practicum.participation.model.Participation;
import ru.practicum.participation.repository.ParticipationRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.util.DateConstant;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRepository participationRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public EventFullDto create(long userId, EventNewDto eventNewDto) {
        if (LocalDateTime.now().plusHours(2).isAfter(eventNewDto.getEventDate())) {
            throw new ConflictException("The event date must be 2 hours from the current time or later.");
        }

        User user = findUserById(userId);
        Event event = EventMapper.fromDto(eventNewDto);

        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (eventNewDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (eventNewDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (eventNewDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto patchByAdmin(long eventId, EventUpdateAdminRequest updateEventAdmin) {
        Event event = findEventById(eventId);

        if (updateEventAdmin.getEventDate() != null
                && LocalDateTime.now().plusHours(1).isAfter(updateEventAdmin.getEventDate())) {
            throw new ConflictException("The event date must be 1 hours from the current time or later.");
        }

        if (updateEventAdmin.getStateAction() != null) {
            if (updateEventAdmin.getStateAction().equals(EventUpdateAdminRequest.StateAction.PUBLISH_EVENT) &&
                    !event.getState().equals(EventState.PENDING)) {
                throw new ConflictException(
                        "Cannot publish the event because it's not in the right state: " + event.getState());
            }

            if (updateEventAdmin.getStateAction().equals(EventUpdateAdminRequest.StateAction.REJECT_EVENT) &&
                    event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException(
                        "Cannot reject the event because it's not in the right state: " + event.getState());
            }
        }

        if (updateEventAdmin.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventAdmin.getCategory()));
        }

        if (updateEventAdmin.getLocation() != null) {
            event.setLocation(handleLocationDto(updateEventAdmin.getLocation()));
        }

        Optional.ofNullable(updateEventAdmin.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventAdmin.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventAdmin.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventAdmin.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventAdmin.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventAdmin.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventAdmin.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (updateEventAdmin.getStateAction() != null) {
            switch (updateEventAdmin.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto patchByInitiator(long userId, long eventId, EventUpdateUserRequest updateEventUser) {
        Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        if (updateEventUser.getEventDate() != null
                && LocalDateTime.now().plusHours(2).isAfter(updateEventUser.getEventDate())) {
            throw new ConflictException("The event date must be 2 hours from the current time or later.");
        }

        if (!(event.getState().equals(EventState.CANCELED) ||
                event.getState().equals(EventState.PENDING))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEventUser.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventUser.getCategory()));
        }

        if (updateEventUser.getLocation() != null) {
            event.setLocation(handleLocationDto(updateEventUser.getLocation()));
        }

        Optional.ofNullable(updateEventUser.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventUser.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventUser.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventUser.getEventDate()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventUser.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventUser.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventUser.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (updateEventUser.getStateAction() != null) {
            switch (updateEventUser.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        return EventMapper.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchParticipationRequestsByInitiator(long userId,
                                                                                long eventId,
                                                                                EventRequestStatusUpdateRequest eventRequestStatusUpdate) {
        findUserById(userId);
        Event event = findEventById(eventId);

        long confirmLimit = event.getParticipantLimit() - participationRepository.countByEventIdAndStatus(eventId, ParticipationState.CONFIRMED);

        if (confirmLimit <= 0) {
            throw new ConflictException("The participant limit has been reached");
        }

        List<Participation> requestList = participationRepository.findAllByIdIn(eventRequestStatusUpdate.getRequestIds());

        List<Long> notFoundIds = eventRequestStatusUpdate.getRequestIds().stream()
                .filter(requestId -> requestList.stream()
                        .noneMatch(request -> request.getId().equals(requestId)))
                .collect(Collectors.toList());

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Participation request with id=" + notFoundIds + " was not found");
        }

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        for (Participation req : requestList) {
            if (!req.getEvent().getId().equals(eventId)) {
                throw new NotFoundException("Participation request with id=" + req.getId() + " was not found");
            }

            if (confirmLimit <= 0) {
                req.setStatus(ParticipationState.REJECTED);
                result.getRejectedRequests().add(ParticipationMapper.toDto(req));
                continue;
            }

            switch (eventRequestStatusUpdate.getStatus()) {
                case CONFIRMED:
                    req.setStatus(ParticipationState.CONFIRMED);
                    result.getConfirmedRequests().add(ParticipationMapper.toDto(req));
                    confirmLimit--;
                    break;
                case REJECTED:
                    req.setStatus(ParticipationState.REJECTED);
                    result.getRejectedRequests().add(ParticipationMapper.toDto(req));
                    break;
            }
        }
        participationRepository.saveAll(requestList);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(List<Long> users,
                                            List<EventState> states,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from,
                                            int size) {

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = DateConstant.getMaxDateTime();
        }

        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd,
                PageRequest.of(from, size));

        List<String> eventUrls = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStatsDto> viewStats = objectMapper.convertValue(
                statsClient.getStats(rangeStart, rangeEnd, eventUrls, true).getBody(),
                new TypeReference<>() {
                });

        return events.stream()
                .map(EventMapper::toFullDto)
                .peek(dto -> {
                    Optional<ViewStatsDto> matchingStats = viewStats.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStatsDto::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(participationRepository.countByEventIdAndStatus(dto.getId(),
                        ParticipationState.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(long userId, int from, int size) {
        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size)).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdByInitiator(long userId, long eventId) {
        Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        return EventMapper.toFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationDto> getParticipationRequestsByInitiator(long userId, long eventId) {
        findUserById(userId);
        findEventById(eventId);

        return participationRepository.findAllByEventId(eventId).stream()
                .map(ParticipationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublic(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            boolean onlyAvailable,
                                            EventControllerPublic.SortMode sort,
                                            int from,
                                            int size,
                                            HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Start date must be before end date");
        }

        if (categories != null && categories.size() == 1 && categories.get(0).equals(0L)) {
            categories = null;
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = DateConstant.getMaxDateTime();
        }
        statsClient.addHit(EndpointHitDto.builder()
                .app("ewm")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        List<Event> eventList = eventRepository.getAllPublic(text, categories, paid, rangeStart, rangeEnd);

        if (onlyAvailable) {
            eventList = eventList.stream()
                    .filter(event -> event.getParticipantLimit().equals(0)
                            || event.getParticipantLimit() < participationRepository.countByEventIdAndStatus(event.getId(),
                            ParticipationState.CONFIRMED))
                    .collect(Collectors.toList());
        }

        List<String> eventUrls = eventList.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());
        List<ViewStatsDto> viewStats = objectMapper.convertValue(
                statsClient.getStats(rangeStart, rangeEnd, eventUrls, true).getBody(),
                new TypeReference<>() {
                });

        List<EventShortDto> eventShortDtoList = eventList.stream()
                .map(EventMapper::toShortDto)
                .peek(dto -> {
                    Optional<ViewStatsDto> matchingStats = viewStats.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStatsDto::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(participationRepository.countByEventIdAndStatus(dto.getId(),
                        ParticipationState.CONFIRMED)))
                .collect(Collectors.toList());

        switch (sort) {
            case EVENT_DATE:
                eventShortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
                break;
            case VIEWS:
                eventShortDtoList.sort(Comparator.comparing(EventShortDto::getViews).reversed());
                break;
        }

        if (from >= eventShortDtoList.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(from + size, eventShortDtoList.size());
        return eventShortDtoList.subList(from, toIndex);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdPublic(long eventId, HttpServletRequest request) {
        Event event = findEventById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
        List<String> eventUrls = Collections.singletonList("/events/" + event.getId());

        List<ViewStatsDto> viewStats = objectMapper.convertValue(
                statsClient.getStats(DateConstant.getMinDateTime(), DateConstant.getMaxDateTime().plusYears(1), eventUrls, true).getBody(),
                new TypeReference<>() {
                });

        statsClient.addHit(EndpointHitDto.builder()
                .app("ewm")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());


        EventFullDto dto = EventMapper.toFullDto(event);
        dto.setViews(viewStats.isEmpty() ? 0L : viewStats.get(0).getHits());
        dto.setConfirmedRequests(participationRepository.countByEventIdAndStatus(dto.getId(), ParticipationState.CONFIRMED));

        return dto;
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    private Category findCategoryById(long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }

    private void checkInitiator(long userId, long eventId, long initiatorId) {
        if (userId != initiatorId) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    private Location handleLocationDto(LocationDto locationDto) {
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        return location != null ? location : locationRepository.save(LocationMapper.fromDto(locationDto));
    }
}
