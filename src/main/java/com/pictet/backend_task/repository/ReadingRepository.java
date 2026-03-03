package com.pictet.backend_task.repository;

import com.pictet.backend_task.repository.entity.Session;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ReadingRepository extends CrudRepository<Session, Long> {
    Optional<Session> findByBookId(Long bookId);

    @Transactional
    @Modifying
    void deleteByBookId(Long bookId);
}
