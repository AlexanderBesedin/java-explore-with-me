package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentUpdateRequest;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto patch(@PathVariable long commentId,
                            @Valid @RequestBody CommentUpdateRequest commentUpdate) {
        log.info("Received a request from the admin to update a comment {}", commentUpdate.getText());
        return commentService.patchByAdmin(commentId, commentUpdate);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long commentId) {
        log.info("Received a request from the admin to delete a comment ID={}", commentId);
        commentService.deleteByAdmin(commentId);
    }
}
