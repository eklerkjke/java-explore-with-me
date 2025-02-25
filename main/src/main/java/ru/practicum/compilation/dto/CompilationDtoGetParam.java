package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.util.Constants;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDtoGetParam {

    private Boolean pinned;
    @PositiveOrZero
    private Integer from = Integer.valueOf(Constants.DEFAULT_PAGE_FROM);
    @Positive
    private Integer size = Integer.valueOf(Constants.DEFAULT_PAGE_SIZE);
}