package com.dock.digital.adapters.inbound.rest.dto.response;

import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;

public record ErrorResponse(
        OffsetDateTime timestamp,
        int status,
        String error,
        String message
) {
    public ErrorResponse(HttpStatus httpStatus, String message) {
        this(OffsetDateTime.now(), httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }
}