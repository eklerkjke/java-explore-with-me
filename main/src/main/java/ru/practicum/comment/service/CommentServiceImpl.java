package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentDto;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    public List<CommentDto> getCommentsEvent(Long eventId, Integer from, Integer size) {
        validId(eventId);
        Event event = findEvent(eventId);
        Pageable pageable = getPageable(from, size);
        List<Comment> commentList = commentRepository.findAllCommentariesByEventId(event.getId(), pageable);

        return commentList.stream().map(commentMapper::toCommentDto).toList();
    }

    @Override
    public List<CommentDto> getCommentsUser(Long userId, Integer from, Integer size) {
        validId(userId);
        User user = findUser(userId);
        Pageable pageable = getPageable(from, size);

        List<Comment> commentList = commentRepository.findAllCommentariesByUserId(user.getId(), pageable);

        return commentList.stream().map(commentMapper::toCommentDto).toList();
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        return commentMapper.toCommentDto(findComment(commentId));
    }

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = findUser(userId);
        Event event = findEvent(eventId);

        Comment insertComment = commentMapper.toComment(newCommentDto);

        insertComment.setUser(user);
        insertComment.setEvent(event);

        Comment comment = commentRepository.save(insertComment);
        return commentMapper.toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto commentDto) {
        User user = findUser(userId);
        Comment comment = findComment(commentId);
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new ValidationException("Комментарий может изменять только автор");
        }

        comment.setText(commentDto.getText());
        commentRepository.save(comment);

        return commentMapper.toCommentDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        validId(userId);
        User user = findUser(userId);
        if (!userId.equals(user.getId())) {
            throw new ValidationException("Комментарий может удалять только автор");
        }

        findComment(commentId);

        commentRepository.deleteById(commentId);
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Комментарий не найден " + commentId));
    }

    private void validId(Long id) {
        if (id < 0) {
            throw new ValidationException("Поле ID должно быть больше 0");
        }
    }

    private User findUser(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь " + userId + " не найден"));
    }

    private Event findEvent(Long eventId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие " + eventId + " не найдено"));

        if (event.getState() != State.PUBLISHED) {
            throw new ValidationException("Событие не опубликовано");
        }

        return event;
    }

    private Pageable getPageable(Integer from, Integer size) {
        if (from < 0) {
            throw new ValidationException("Параметр запроса from должен быть больше 0, теперь from=" + from);
        }
        if (size < 0) {
            throw new ValidationException("Параметр запроса 'size' должен быть больше 0, теперь size=" + size);
        }
        return PageRequest.of(from / size, size);
    }
}
