package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentDto> getAllByEventId(@PathVariable long eventId) {
        log.info("All comments requested for the event ID={}", eventId);
        return commentService.getAllByEventId(eventId);
    }
}
