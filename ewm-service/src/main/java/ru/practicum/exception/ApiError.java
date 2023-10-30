package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import ru.practicum.util.DateConstants;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiError {
    private HttpStatus status;
    private String reason;
    private String message;
    private StackTraceElement[] errors;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    private LocalDateTime timestamp;
}
