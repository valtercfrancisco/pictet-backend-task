package com.pictet.backend_task.utils;

import com.pictet.backend_task.error.InvalidBookException;
import com.pictet.backend_task.repository.entity.Book;
import com.pictet.backend_task.repository.entity.Section;
import com.pictet.backend_task.repository.model.Category;
import com.pictet.backend_task.repository.model.Difficulty;
import com.pictet.backend_task.repository.model.SectionType;
import lombok.val;

import java.util.stream.Collectors;

public class BookUtils {

    private BookUtils() {}

    public static Difficulty parseDifficulty(String difficulty) {
        try {
            return Difficulty.valueOf(difficulty.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Difficulty must be one of: EASY, MEDIUM, HARD");
        }
    }

    public static Category parseCategory(String category) {
        try {
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Category must be one of: FICTION, SCIENCE, HORROR, ADVENTURE, FANTASY, MYSTERY, THRILLER, ROMANCE, HISTORICAL, BIOGRAPHY");
        }
    }

    public static void validateBook(Book book) {
        validateBeginSection(book);
        validateEndSection(book);
    }

    private static void validateBeginSection(Book book) {
        val beginSections = book.getSections().stream()
                .filter(s -> s.getType() == SectionType.BEGIN)
                .toList();

        if (beginSections.isEmpty()) {
            throw new InvalidBookException("Invalid book: no BEGIN section found");
        }

        if (beginSections.size() > 1) {
            throw new InvalidBookException("Invalid book: multiple BEGIN sections found");
        }
    }

    private static void validateEndSection(Book book) {
        val hasEndSection = book.getSections().stream()
                .anyMatch(s -> s.getType() == SectionType.END);
        if (!hasEndSection) {
            throw new InvalidBookException("Invalid book: no END section found");
        }
    }


}

