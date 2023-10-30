package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.location.LocationDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.util.DateConstants;

import java.time.LocalDateTime;

@Data
@Builder
public class EventFullDto {

    private Long id;

    private UserShortDto initiator;
    private CategoryDto category;
    private LocationDto location;

    private String title;
    private String annotation;
    private String description;
    private EventState state;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    private LocalDateTime eventDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    private LocalDateTime createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    private LocalDateTime publishedOn;

    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;

    private Long confirmedRequests;
    private Long views;
}
