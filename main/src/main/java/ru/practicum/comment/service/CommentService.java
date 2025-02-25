package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto commentDto);

    List<CommentDto> getCommentsUser(Long userId, Integer from, Integer size);

    List<CommentDto> getCommentsEvent(Long userId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);

    void deleteComment(Long userId, Long commentId);
}
