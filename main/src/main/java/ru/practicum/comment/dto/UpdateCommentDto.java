package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCommentDto {

    @NotBlank(message = "Единственный параметр не может быть пустым или null")
    @Size(max = 5000, min = 20, message = "Текст комментария должен содержать от 20 до 5000 символов")
    private String text;
}
