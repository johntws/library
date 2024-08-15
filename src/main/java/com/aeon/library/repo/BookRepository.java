package com.aeon.library.repo;

import com.aeon.library.entity.BookCopy;
import com.aeon.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
//    List<BookCopy> findByIdAndDeletedFalse(@Param("id") Long id);
}
