package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.util.DateConstants;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String userName;
    private String text;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    private LocalDateTime createdOn;
}
