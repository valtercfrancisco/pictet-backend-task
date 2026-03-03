package com.pictet.backend_task.utils;

import com.pictet.backend_task.repository.entity.Book;
import com.pictet.backend_task.repository.entity.Section;
import com.pictet.backend_task.repository.entity.Session;
import com.pictet.backend_task.repository.model.*;

import java.time.LocalDateTime;

public class SessionUtils {

    private SessionUtils() {}

    public static Session newSession(Long bookId, Integer currentSectionId) {
        Session session = new Session();
        session.setBookId(bookId);
        session.setCurrentSectionId(currentSectionId);
        session.setHealth(10);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        return session;
    }

    public static ReadingSessionResponse buildResponse(
            Session session,
            Book book,
            Section currentSection,
            Consequence consequence
    ) {
        CurrentSectionInfo currentSectionInfo = new CurrentSectionInfo(
                currentSection.getId(),
                currentSection.getText(),
                currentSection.getType(),
                currentSection.getOptions(),
                consequence
        );

        return new ReadingSessionResponse(
                session.getId(),
                book.getId(),
                book.getTitle(),
                session.getHealth(),
                currentSectionInfo
        );
    }
}

