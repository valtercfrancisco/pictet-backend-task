package com.pictet.backend_task.controller;

import com.pictet.backend_task.error.BookNotFoundException;
import com.pictet.backend_task.repository.model.Book;
import com.pictet.backend_task.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        val books = bookService.searchBooks(null, null, null);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new BookNotFoundException("Book with id " + id + " not found"));
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> searchByTitle(@RequestParam String title) {
        val books = bookService.searchBooks(title, null, null);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> searchByAuthor(@RequestParam String author) {
        val books = bookService.searchBooks(null, author, null);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search/difficulty")
    public ResponseEntity<List<Book>> searchByDifficulty(@RequestParam String difficulty) {
        val books = bookService.searchBooks(null, null, difficulty);
        return ResponseEntity.ok(books);
    }
}
