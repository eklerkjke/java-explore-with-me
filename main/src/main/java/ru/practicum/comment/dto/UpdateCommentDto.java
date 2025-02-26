package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCommentDto {

    @NotBlank(message = "Единственный параметр не может быть пустым или null")
    @Size(max = 5000, min = 20, message = "Текст комментария должен содержать от 20 до 5000 символов")
    private String text;
}
