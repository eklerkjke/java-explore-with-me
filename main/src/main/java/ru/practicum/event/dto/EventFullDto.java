package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.util.Constants;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    @JsonFormat(pattern = Constants.JSON_DATE_FORMAT)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = Constants.JSON_DATE_FORMAT)
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private LocationDto location;
    private boolean paid;
    @Builder.Default
    private Integer participantLimit = 0;
    @JsonFormat(pattern = Constants.JSON_DATE_FORMAT)
    private LocalDateTime publishedOn;
    @Builder.Default
    private boolean requestModeration = true;
    private State state;
    private String title;
    private Integer views;
}
