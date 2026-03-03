package com.pictet.backend_task.error;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class BookExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex) {
        log.error("Book not found: {}", ex.getMessage());
        val error = new ErrorResponse(
                NOT_FOUND.value(),
                "Book Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidBookException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBook(InvalidBookException ex) {
        log.error("Invalid book data: {}", ex.getMessage());
        val error = new ErrorResponse(
                UNPROCESSABLE_CONTENT.value(),
                "Invalid Book Data",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(UNPROCESSABLE_CONTENT).body(error);
    }

    @ExceptionHandler(SessionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSessionNotFound(SessionNotFoundException ex) {
        log.error("Session not found: {}", ex.getMessage());
        val error = new ErrorResponse(
                NOT_FOUND.value(),
                "Session Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(NOT_FOUND).body(error);
    }

    @ExceptionHandler(BookSectionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookSectionNotFound(BookSectionNotFoundException ex) {
        log.error("Book section not found: {}", ex.getMessage());
        val error = new ErrorResponse(
                NOT_FOUND.value(),
                "Book Section Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(NOT_FOUND).body(error);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFound(CategoryNotFoundException ex) {
        log.error("Category not found: {}", ex.getMessage());
        val error = new ErrorResponse(
                NOT_FOUND.value(),
                "Category Not Found",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("Invalid argument: {}", ex.getMessage());
        val error = new ErrorResponse(
                BAD_REQUEST.value(),
                "Invalid Request",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        val error = new ErrorResponse(
                INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                LocalDateTime.now()
        );
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
    }
}