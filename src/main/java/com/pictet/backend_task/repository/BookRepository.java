package com.pictet.backend_task.repository;

import com.pictet.backend_task.repository.model.Book;
import com.pictet.backend_task.repository.model.Difficulty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByDifficulty(Difficulty difficulty);
}
