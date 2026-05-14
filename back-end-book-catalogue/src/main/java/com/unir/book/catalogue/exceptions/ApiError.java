package com.unir.book.catalogue.exceptions;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiError {

    private String message;

    private int status;

    private LocalDateTime timestamp;
}
