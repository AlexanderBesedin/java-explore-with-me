package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;
import ru.practicum.comment.dto.CommentUpdateRequest;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable long userId,
                             @PathVariable long eventId,
                             @Valid @RequestBody CommentNewDto commentNewDto) {
        log.info("Request to create a comment {} received", commentNewDto.getText());
        return commentService.create(userId, eventId, commentNewDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto patch(@PathVariable long userId,
                            @PathVariable long commentId,
                            @Valid @RequestBody CommentUpdateRequest commentUpdate) {
        log.info("Request to update comment {} received", commentUpdate.getText());
        return commentService.patchByUser(userId, commentId, commentUpdate);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long userId, @PathVariable long commentId) {
        log.info("Request to delete comment ID={} received", commentId);
        commentService.deleteByUser(userId, commentId);
    }
}
