package com.pictet.backend_task.service;

import com.pictet.backend_task.repository.model.Book;
import com.pictet.backend_task.repository.BookRepository;
import com.pictet.backend_task.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
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
}
