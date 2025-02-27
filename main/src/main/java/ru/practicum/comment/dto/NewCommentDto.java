package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCommentDto {
    @NotBlank(message = "Единственный параметр не может быть пустым или null")
    @Size(max = 5000, min = 20, message = "Текст комментария должен содержать от 20 до 5000 символов")
    String text;

    Long userId;

    Long eventId;

    LocalDateTime timestamp;
}
