package com.pictet.backend_task.repository;

import com.pictet.backend_task.model.Category;
import com.pictet.backend_task.repository.model.Book;
import com.pictet.backend_task.repository.model.Difficulty;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findByDifficulty(Difficulty difficulty);

    @Modifying
    @Query(value = "MERGE INTO book_categories (book_id, category) KEY(book_id, category) VALUES (:bookId, :category)", nativeQuery = true)
    int addCategoryToBook(@Param("bookId") Long bookId, @Param("category") String category);

    @Modifying
    @Query(value = "DELETE FROM book_categories WHERE book_id = :bookId AND category = :category", nativeQuery = true)
    int removeCategoryFromBook(@Param("bookId") Long bookId, @Param("category") String category);
}
