package com.pictet.backend_task.service;

import com.pictet.backend_task.error.BookNotFoundException;
import com.pictet.backend_task.repository.model.Book;
import com.pictet.backend_task.repository.BookRepository;
import com.pictet.backend_task.utils.Utils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final EntityManager entityManager;

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id %s not found".formatted(id)));
    }

    public List<Book> searchBooks(String title, String author, String difficulty) {
        if (title != null) {
            return bookRepository.findByTitleContainingIgnoreCase(title);
        } else if (author != null) {
            return bookRepository.findByAuthorContainingIgnoreCase(author);
        } else if (difficulty != null) {
            val difficultyEnum = Utils.parseDifficulty(difficulty);
            return bookRepository.findByDifficulty(difficultyEnum);
        } else {
            return (List<Book>) bookRepository.findAll();
        }
    }

    @Transactional
    public Book addCategoryToBook(Long bookId, String categoryName) {
        verifyBookExists(bookId);
        val category = Utils.parseCategory(categoryName);
        bookRepository.addCategoryToBook(bookId, category.name());
        entityManager.flush();
        entityManager.clear();
        return getBookById(bookId);
    }

    @Transactional
    public Book removeCategoryFromBook(Long bookId, String categoryName) {
        verifyBookExists(bookId);
        val category = Utils.parseCategory(categoryName);
        val rowsAffected = bookRepository.removeCategoryFromBook(bookId, category.name());
        if (rowsAffected == 0) {
            throw new IllegalArgumentException(
                    "Category %s not found on book with id %s".formatted(categoryName, bookId)
            );
        }

        entityManager.flush();
        entityManager.clear();
        return getBookById(bookId);
    }

    private void verifyBookExists(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("Book with id %s not found".formatted(bookId));
        }
    }
}
