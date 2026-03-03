package com.pictet.backend_task.controller;

import com.pictet.backend_task.repository.model.ReadingSessionResponse;
import com.pictet.backend_task.service.ReadingService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/reading")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService readingService;
    
    @GetMapping("/book/{bookId}/session")
    public ResponseEntity<Long> getExistingSession(@PathVariable Long bookId) {
        return readingService.getExistingSession(bookId)
                .map(session -> ResponseEntity.ok(session.getId()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/book/{bookId}/read")
    public ResponseEntity<ReadingSessionResponse> startReading(@PathVariable Long bookId) {
        val response = readingService.startNewSession(bookId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ReadingSessionResponse> continueReading(@PathVariable Long sessionId) {
        val response = readingService.getCurrentState(sessionId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/session/{sessionId}/choose/{optionIndex}")
    public ResponseEntity<ReadingSessionResponse> makeChoice(
            @PathVariable Long sessionId,
            @PathVariable Integer optionIndex
    ) {
        val response = readingService.makeChoice(sessionId, optionIndex);
        return ResponseEntity.ok(response);
    }
}
