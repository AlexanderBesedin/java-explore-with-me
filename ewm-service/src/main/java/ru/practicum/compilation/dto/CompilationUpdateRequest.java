package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationUpdateRequest {
    @NotBlank(message = "{Compilation title must be completed}")
    @Size(max = 50)
    private String title;
    private Boolean pinned;
    private List<Long> events;
}
