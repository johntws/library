package com.aeon.library.repo;

import com.aeon.library.entity.BookCopy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long>, JpaSpecificationExecutor<BookCopy> {
//    Optional<Book> findByIsbn(@Param("isbn") String isbn);
    Optional<BookCopy> findByIdAndDeletedFalse(@Param("id") Long id);

}
