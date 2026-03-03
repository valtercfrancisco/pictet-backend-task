package com.pictet.backend_task.error;

public class BookSectionNotFoundException extends RuntimeException {
    public BookSectionNotFoundException(String message) {
        super(message);
    }
}

