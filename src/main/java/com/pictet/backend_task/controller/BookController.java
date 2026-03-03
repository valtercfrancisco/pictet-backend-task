package com.pictet.backend_task.controller;

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
        val books = bookService.searchBooks(null, null, null, null);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        val book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<Book>> searchByTitle(@RequestParam String title) {
        val books = bookService.searchBooks(title, null, null, null);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<Book>> searchByAuthor(@RequestParam String author) {
        val books = bookService.searchBooks(null, author, null, null);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search/difficulty")
    public ResponseEntity<List<Book>> searchByDifficulty(@RequestParam String difficulty) {
        val books = bookService.searchBooks(null, null, difficulty, null);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<Book>> searchByCategory(@RequestParam String category) {
        val books = bookService.searchBooks(null, null, null, category);
        return ResponseEntity.ok(books);
    }

    @PostMapping("/{id}/categories/{category}")
    public ResponseEntity<Book> addCategoryToBook(@PathVariable Long id, @PathVariable String category) {
        val book = bookService.addCategoryToBook(id, category);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}/categories/{category}")
    public ResponseEntity<Book> removeCategoryFromBook(@PathVariable Long id, @PathVariable String category) {
        val book = bookService.removeCategoryFromBook(id, category);
        return ResponseEntity.ok(book);
    }
}
