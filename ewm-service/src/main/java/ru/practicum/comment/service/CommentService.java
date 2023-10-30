package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;
import ru.practicum.comment.dto.CommentUpdateRequest;

import java.util.List;

public interface CommentService {
    CommentDto create(long userId, long eventId, CommentNewDto dto);

    CommentDto patchByUser(long userId, long commentId, CommentUpdateRequest updateRequest);

    CommentDto patchByAdmin(long commentId, CommentUpdateRequest updateRequest);

    List<CommentDto> getAllByEventId(long eventId);

    void deleteByUser(long userId, long commentId);

    void deleteByAdmin(long commentId);
}
