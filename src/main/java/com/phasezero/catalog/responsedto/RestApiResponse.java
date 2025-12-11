package com.phasezero.catalog.responsedto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestApiResponse<T> {
    private String status;
    private String message;
    private T data;
    private String path;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}