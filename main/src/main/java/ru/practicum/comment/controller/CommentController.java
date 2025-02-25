package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsEvent(
            @PathVariable(name = "eventId") Long eventId,
            @RequestParam(defaultValue = "0", name = "from") Integer from,
            @RequestParam(defaultValue = "10", name = "size") Integer size
    ) {
        return commentService.getCommentsEvent(eventId, from, size);
    }

    @GetMapping("/user/{userId}")
    public List<CommentDto> getCommentsUser(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(defaultValue = "0", name = "from") Integer from,
            @RequestParam(defaultValue = "10", name = "size") Integer size
    ) {
        return commentService.getCommentsUser(userId, from, size);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable(name = "commentId") Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @PostMapping("/{userId}/{eventId}")
    public CommentDto createComment(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId,
            @RequestBody @Valid NewCommentDto commentDto
    ) {
        return commentService.createComment(userId, eventId, commentDto);
    }

    @PatchMapping("/{userId}/{commentId}")
    public CommentDto updateComment(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "commentId") Long commentId,
            @RequestBody @Valid UpdateCommentDto commentDto
    ) {
        return commentService.updateComment(userId, commentId, commentDto);
    }

    @DeleteMapping("/{userId}/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(userId, commentId);
    }
}