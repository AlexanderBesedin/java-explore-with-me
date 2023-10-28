package ru.practicum.participation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.participation.enums.ParticipationState;
import ru.practicum.util.DateConstant;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipationDto {
    private Long id;
    private Long requester;
    private Long event;
    private ParticipationState status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstant.DATE_FORMAT)
    private LocalDateTime created;
}
