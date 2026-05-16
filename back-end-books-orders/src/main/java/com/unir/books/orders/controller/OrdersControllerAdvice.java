package com.unir.books.orders.controller;

import com.unir.books.orders.controller.model.ErrorResponse;
import com.unir.books.orders.exception.BadBookModificationException;
import com.unir.books.orders.exception.InternalErrorException;
import com.unir.books.orders.exception.BookNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class OrdersControllerAdvice {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSupplyNotFound(BookNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .details(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(BadBookModificationException.class)
    public ResponseEntity<ErrorResponse> handleSupplyModification(BadBookModificationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .details(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<ErrorResponse> handleGenericError(InternalErrorException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .details(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String detail = ex.getMessage();
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            String validValues = Arrays.stream(ex.getRequiredType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            detail = "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'."
                    + " Valid values: " + validValues;
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder().details(detail).build());
    }
}
