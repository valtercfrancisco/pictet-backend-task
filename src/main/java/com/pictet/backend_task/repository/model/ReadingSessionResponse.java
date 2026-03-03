package com.pictet.backend_task.repository.model;

public record ReadingSessionResponse(
    Long sessionId,
    Long bookId,
    String bookTitle,
    Integer health,
    CurrentSectionInfo currentSection
) {}

