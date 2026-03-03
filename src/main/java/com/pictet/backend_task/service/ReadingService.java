package com.pictet.backend_task.service;

import com.pictet.backend_task.error.BookSectionNotFoundException;
import com.pictet.backend_task.error.InvalidBookException;
import com.pictet.backend_task.error.SessionNotFoundException;
import com.pictet.backend_task.repository.ReadingRepository;
import com.pictet.backend_task.repository.entity.Book;
import com.pictet.backend_task.repository.entity.Section;
import com.pictet.backend_task.repository.entity.Session;
import com.pictet.backend_task.repository.model.Consequence;
import com.pictet.backend_task.repository.model.ReadingSessionResponse;
import com.pictet.backend_task.repository.model.SectionType;
import com.pictet.backend_task.utils.BookUtils;
import com.pictet.backend_task.utils.SessionUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReadingService {

    private final ReadingRepository readingRepository;
    private final BookService bookService;

    public Optional<Session> getExistingSession(Long bookId) {
        return readingRepository.findByBookId(bookId);
    }

    private Session findSessionById(Long sessionId) {
        return readingRepository.findById(sessionId)
                .orElseThrow(() ->
                        new SessionNotFoundException("Session with id %s not found".formatted(sessionId)));
    }

    public ReadingSessionResponse startNewSession(Long bookId) {
        val book = bookService.getBookById(bookId);

        BookUtils.validateBook(book);

        // Delete previous session if it exists
        readingRepository.deleteByBookId(bookId);

        val beginSection = findSectionByType(book);
        val session = SessionUtils.newSession(bookId, beginSection.getId());
        readingRepository.save(session);

        return buildResponse(session, book, null);
    }

    public ReadingSessionResponse getCurrentState(Long sessionId) {
        val session = findSessionById(sessionId);
        val book = bookService.getBookById(session.getBookId());
        val currentSection = findSectionById(book, session.getCurrentSectionId());
        
        val endResponse = checkIfGameEnded(session, book, currentSection);
        if (endResponse != null) return endResponse;

        validateDeadEnd(session, currentSection);

        return buildResponse(session, book, null);
    }

    public ReadingSessionResponse makeChoice(Long sessionId, Integer optionIndex) {
        val session = findSessionById(sessionId);
        val book = bookService.getBookById(session.getBookId());
        val currentSection = findSectionById(book, session.getCurrentSectionId());
        
        validateDeadEnd(session, currentSection);

        if (optionIndex >= currentSection.getOptions().size()) {
            throw new IllegalArgumentException("Invalid option index");
        }

        val chosenOption = currentSection.getOptions().get(optionIndex);
        validateNextSectionExists(session, book, currentSection, chosenOption);
        val consequence = applyOptionConsequence(session, chosenOption);

        val deathResponse = checkIfPlayerDied(session, book, consequence);
        if (deathResponse != null) return deathResponse;

        // Move to next section
        session.setCurrentSectionId(chosenOption.gotoId());
        session.setUpdatedAt(LocalDateTime.now());
        readingRepository.save(session);

        return buildResponse(session, book, consequence);
    }

    private ReadingSessionResponse checkIfPlayerDied(Session session, Book book, Consequence consequence) {
        if (session.getHealth() <= 0) {
            readingRepository.delete(session);
            return buildResponse(session, book, consequence);
        }
        return null;
    }

    private Consequence applyOptionConsequence(Session session, Section.Option option) {
        if (option.consequence() == null)return null;
        val previousHealth = session.getHealth();
        applyConsequence(session, option.consequence());
        return new Consequence(
                option.consequence().type(),
                option.consequence().value(),
                option.consequence().text(),
                previousHealth,
                session.getHealth()
        );
    }

    private void applyConsequence(Session session, Section.Consequence consequence) {
        switch (consequence.type()) {
            case LOSE_HEALTH:
                val loseAmount = Integer.parseInt(consequence.value());
                session.setHealth(Math.max(0, session.getHealth() - loseAmount));
                break;
            case GAIN_HEALTH:
                val gainAmount = Integer.parseInt(consequence.value());
                session.setHealth(Math.min(10, session.getHealth() + gainAmount));
                break;
        }
    }

    private ReadingSessionResponse buildResponse(
            Session session,
            Book book,
            Consequence consequence
    ) {
        val currentSection = findSectionById(book, session.getCurrentSectionId());
        return SessionUtils.buildResponse(session, book, currentSection, consequence);
    }

    private Section findSectionById(Book book, Integer sectionId) {
        return book.getSections().stream()
                .filter(s -> s.getId().equals(sectionId))
                .findFirst()
                .orElseThrow(() ->
                        new BookSectionNotFoundException("Section with id %s not found".formatted(sectionId)));
    }

    private Section findSectionByType(Book book) {
        return book.getSections().stream()
                .filter(s -> s.getType() == SectionType.BEGIN)
                .findFirst()
                .orElse(null);
    }

    private void validateDeadEnd(Session session, Section section) {
        if (section.getType() != SectionType.END) {
            if (section.getOptions() == null || section.getOptions().isEmpty()) {
                readingRepository.delete(session);
                throw new InvalidBookException("Dead end - Invalid book: section %d (non-END) has no options".formatted(section.getId()));
            }
        }
    }

    private ReadingSessionResponse checkIfGameEnded(Session session, Book book, Section currentSection) {
        if (currentSection.getType() == SectionType.END) {
            readingRepository.delete(session);
            return buildResponse(session, book, null);
        }
        return null;
    }

    private void validateNextSectionExists(Session session, Book book, Section currentSection, Section.Option chosenOption) {
        val nextSectionExists = book.getSections().stream()
                .anyMatch(s -> s.getId().equals(chosenOption.gotoId()));
        if (!nextSectionExists) {
            readingRepository.delete(session);
            throw new InvalidBookException("Dead end - Invalid book: section %d has option pointing to non-existent section %d"
                    .formatted(currentSection.getId(), chosenOption.gotoId()));
        }
    }
}
