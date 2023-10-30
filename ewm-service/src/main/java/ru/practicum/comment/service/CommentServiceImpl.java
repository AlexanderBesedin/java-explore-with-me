package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.CommentNewDto;
import ru.practicum.comment.dto.CommentUpdateRequest;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentDto create(long userId, long eventId, CommentNewDto dto) {
        User user = findUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        return CommentMapper.INSTANCE.toDto(commentRepository.save(
                Comment.builder()
                        .user(user)
                        .event(event)
                        .text(dto.getText())
                        .createdOn(LocalDateTime.now())
                        .build()));
    }

    @Override
    @Transactional
    public CommentDto patchByUser(long userId, long commentId, CommentUpdateRequest updateRequest) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("User id=" + userId + " not owner of Comment id=" + commentId);
        }

        Optional.ofNullable(updateRequest.getText()).ifPresent(comment::setText);

        return CommentMapper.INSTANCE.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto patchByAdmin(long commentId, CommentUpdateRequest updateRequest) {
        Comment comment = findCommentById(commentId);
        Optional.ofNullable(updateRequest.getText()).ifPresent(comment::setText);

        return CommentMapper.INSTANCE.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllByEventId(long eventId) {
        return commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteByUser(long userId, long commentId) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("User id=" + userId + " not owner of Comment id=" + commentId);
        }

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void deleteByAdmin(long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment findCommentById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + id + " was not found"));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }
}
