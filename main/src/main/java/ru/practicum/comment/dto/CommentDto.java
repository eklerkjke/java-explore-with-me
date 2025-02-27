package ru.practicum.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDto {

    Long id;

    EventShortDto event;

    String text;

    UserShortDto user;

    LocalDateTime timestamp;
}